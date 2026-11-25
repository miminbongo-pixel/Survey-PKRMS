package id.geoexplorer.app.core.map

/**
 * Katalog sumber peta dasar (basemap) untuk MapLibre Native Android SDK.
 * Mendukung citra satelit Esri, Google XYZ, OpenStreetMap, dan MapLibre Demo.
 */
object BasemapCatalog {
    enum class Source(
        val id: String,
        val displayName: String,
        val tileUrlTemplate: String?,
        val styleUri: String?,
        val attribution: String,
        val maxZoom: Int = 19
    ) {
        ESRI_WORLD_IMAGERY(
            id = "esri-satellite",
            displayName = "Satelit Esri",
            tileUrlTemplate = "https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}",
            styleUri = null,
            attribution = "Esri, Maxar, Earthstar Geographics",
            maxZoom = 19
        ),
        GOOGLE_SATELLITE(
            id = "google-satellite",
            displayName = "Satelit Google Earth",
            tileUrlTemplate = "https://mt1.google.com/vt/lyrs=s&x={x}&y={y}&z={z}",
            styleUri = null,
            attribution = "Google Satellite XYZ",
            maxZoom = 20
        ),
        GOOGLE_HYBRID(
            id = "google-hybrid",
            displayName = "Satelit Google Hybrid",
            tileUrlTemplate = "https://mt1.google.com/vt/lyrs=y&x={x}&y={y}&z={z}",
            styleUri = null,
            attribution = "Google Hybrid XYZ",
            maxZoom = 20
        ),
        GOOGLE_ROADMAP(
            id = "google-roadmap",
            displayName = "Peta Google Maps",
            tileUrlTemplate = "https://mt1.google.com/vt/lyrs=m&x={x}&y={y}&z={z}",
            styleUri = null,
            attribution = "Google Maps XYZ",
            maxZoom = 20
        ),
        OPENSTREETMAP(
            id = "osm-map",
            displayName = "Peta OpenStreetMap",
            tileUrlTemplate = "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
            styleUri = null,
            attribution = "OpenStreetMap contributors",
            maxZoom = 19
        ),
        MAPLIBRE_DEMO(
            id = "maplibre-vector",
            displayName = "Peta Vektor MapLibre",
            tileUrlTemplate = null,
            styleUri = MapLibreConfig.DEFAULT_STYLE_URL,
            attribution = "MapLibre Demo Tiles",
            maxZoom = 18
        );

        fun toRasterStyleJson(): String {
            val url = tileUrlTemplate ?: return MapLibreConfig.OFFLINE_FALLBACK_STYLE_JSON
            return """
                {
                  "version": 8,
                  "name": "${displayName.replace("\"", "'")}",
                  "sources": {
                    "basemap": {
                      "type": "raster",
                      "tiles": ["$url"],
                      "tileSize": 256,
                      "maxzoom": $maxZoom
                    }
                  },
                  "layers": [
                    {
                      "id": "basemap-raster",
                      "type": "raster",
                      "source": "basemap",
                      "minzoom": 0,
                      "maxzoom": 22
                    }
                  ]
                }
            """.trimIndent()
        }
    }
}
