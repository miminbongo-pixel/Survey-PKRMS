package id.geoexplorer.app.core.map

import android.content.Context
import android.os.Bundle
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

/**
 * Pengendali siklus hidup dan kamera MapLibre yang terpisah dari Activity UI
 * agar modul peta tetap modular dan mudah diperluas untuk layer manager & digitasi.
 */
class GeoMapController(private val context: Context) {
    private var mapView: MapView? = null
    private var mapLibreMap: MapLibreMap? = null
    private var fallbackLoaded = false
    private var isStarted = false
    private var isResumed = false

    fun setBasemap(
        source: BasemapCatalog.Source,
        onApplied: ((String) -> Unit)? = null
    ) {
        val map = mapLibreMap ?: return
        fallbackLoaded = false
        val styleBuilder = if (source.styleUri != null) {
            Style.Builder().fromUri(source.styleUri)
        } else {
            Style.Builder().fromJson(source.toRasterStyleJson())
        }
        map.setStyle(styleBuilder) {
            onApplied?.invoke("${source.displayName} aktif")
        }
    }

    fun createMapView(
        savedInstanceState: Bundle?,
        initialBasemap: BasemapCatalog.Source = BasemapCatalog.Source.ESRI_WORLD_IMAGERY,
        onCoordinateChanged: (latitude: Double, longitude: Double, zoom: Double, styleSource: String) -> Unit,
        onError: (String) -> Unit
    ): MapView? {
        return try {
            MapLibre.getInstance(context.applicationContext)
            fallbackLoaded = false
            isStarted = false
            isResumed = false

            lateinit var createdView: MapView
            createdView = MapView(context).apply {
                onCreate(savedInstanceState)
                addOnDidFailLoadingMapListener { errorMessage ->
                    if (!fallbackLoaded) {
                        fallbackLoaded = true
                        createdView.post {
                            mapLibreMap?.setStyle(
                                Style.Builder().fromJson(MapLibreConfig.OFFLINE_FALLBACK_STYLE_JSON)
                            ) {
                                val target = mapLibreMap?.cameraPosition?.target
                                val zoom = mapLibreMap?.cameraPosition?.zoom ?: MapLibreConfig.DEFAULT_ZOOM
                                onCoordinateChanged(
                                    target?.latitude ?: MapLibreConfig.DEFAULT_LATITUDE,
                                    target?.longitude ?: MapLibreConfig.DEFAULT_LONGITUDE,
                                    zoom,
                                    "Kanvas offline (gagal memuat style daring: $errorMessage)"
                                )
                            }
                        }
                    }
                }
            }

            createdView.getMapAsync { map ->
                mapLibreMap = map
                map.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(MapLibreConfig.DEFAULT_LATITUDE, MapLibreConfig.DEFAULT_LONGITUDE))
                    .zoom(MapLibreConfig.DEFAULT_ZOOM)
                    .build()

                onCoordinateChanged(
                    MapLibreConfig.DEFAULT_LATITUDE,
                    MapLibreConfig.DEFAULT_LONGITUDE,
                    MapLibreConfig.DEFAULT_ZOOM,
                    "Memuat ${initialBasemap.displayName}..."
                )

                val initialStyle = if (initialBasemap.styleUri != null) {
                    Style.Builder().fromUri(initialBasemap.styleUri)
                } else {
                    Style.Builder().fromJson(initialBasemap.toRasterStyleJson())
                }

                map.setStyle(initialStyle) {
                    val target = map.cameraPosition.target
                    onCoordinateChanged(
                        target?.latitude ?: MapLibreConfig.DEFAULT_LATITUDE,
                        target?.longitude ?: MapLibreConfig.DEFAULT_LONGITUDE,
                        map.cameraPosition.zoom,
                        "${initialBasemap.displayName} aktif"
                    )
                }

                map.addOnCameraIdleListener {
                    val pos = map.cameraPosition
                    val target = pos.target ?: return@addOnCameraIdleListener
                    val sourceLabel = if (fallbackLoaded) "Kanvas offline MapLibre" else "Basemap MapLibre aktif"
                    onCoordinateChanged(target.latitude, target.longitude, pos.zoom, sourceLabel)
                }

                map.addOnMapClickListener { point ->
                    val sourceLabel = if (fallbackLoaded) "Titik klik (kanvas offline)" else "Titik klik pada peta"
                    onCoordinateChanged(point.latitude, point.longitude, map.cameraPosition.zoom, sourceLabel)
                    true
                }
            }

            mapView = createdView
            createdView
        } catch (t: Throwable) {
            onError(t.message ?: t::class.java.simpleName)
            null
        }
    }

    fun onStart() {
        val view = mapView ?: return
        if (!isStarted) {
            view.onStart()
            isStarted = true
        }
    }

    fun onResume() {
        val view = mapView ?: return
        if (!isStarted) {
            view.onStart()
            isStarted = true
        }
        if (!isResumed) {
            view.onResume()
            isResumed = true
        }
    }

    fun onPause() {
        val view = mapView ?: return
        if (isResumed) {
            view.onPause()
            isResumed = false
        }
    }

    fun onStop() {
        val view = mapView ?: return
        if (isResumed) {
            view.onPause()
            isResumed = false
        }
        if (isStarted) {
            view.onStop()
            isStarted = false
        }
    }

    fun onLowMemory() {
        mapView?.onLowMemory()
    }

    fun onSaveInstanceState(outState: Bundle) {
        mapView?.onSaveInstanceState(outState)
    }

    fun destroyMapView() {
        val view = mapView ?: return
        if (isResumed) {
            view.onPause()
            isResumed = false
        }
        if (isStarted) {
            view.onStop()
            isStarted = false
        }
        mapLibreMap = null
        view.onDestroy()
        mapView = null
    }
}
