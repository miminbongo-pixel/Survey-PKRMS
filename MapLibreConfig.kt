package id.geoexplorer.app.core.map

/**
 * Konfigurasi dasar mesin peta MapLibre untuk GeoExplorer.
 * Nilai default memusatkan kamera pada wilayah Indonesia.
 */
object MapLibreConfig {
    const val DEFAULT_LATITUDE = -2.5
    const val DEFAULT_LONGITUDE = 118.0
    const val DEFAULT_ZOOM = 4.2

    const val DEFAULT_STYLE_URL = "https://demotiles.maplibre.org/style.json"

    /**
     * Style JSON minimal sebagai fallback offline ketika perangkat/emulator tidak
     * memiliki koneksi internet atau gagal memuat style eksternal.
     */
    val OFFLINE_FALLBACK_STYLE_JSON = """
        {
          "version": 8,
          "name": "GeoExplorer Offline Canvas",
          "sources": {},
          "layers": [
            {
              "id": "geoexplorer-background",
              "type": "background",
              "paint": {
                "background-color": "#DCE8F5"
              }
            }
          ]
        }
    """.trimIndent()
}
