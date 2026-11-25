package id.geoexplorer.app.core.coordinates

/**
 * Cakupan zona UTM standar yang melintasi wilayah Indonesia.
 * EPSG 326xx = WGS 84 / UTM northern hemisphere.
 * EPSG 327xx = WGS 84 / UTM southern hemisphere.
 *
 * Ini pemilih zona awal, bukan pengganti mesin transformasi CRS PROJ.
 */
object IndonesiaUtmZones {
    data class Zone(val number: Int, val westLongitude: Double, val eastLongitude: Double) {
        fun epsg(isNorthernHemisphere: Boolean): Int =
            (if (isNorthernHemisphere) 32600 else 32700) + number
    }

    val zones = listOf(
        Zone(46, 90.0, 96.0),
        Zone(47, 96.0, 102.0),
        Zone(48, 102.0, 108.0),
        Zone(49, 108.0, 114.0),
        Zone(50, 114.0, 120.0),
        Zone(51, 120.0, 126.0),
        Zone(52, 126.0, 132.0),
        Zone(53, 132.0, 138.0),
        Zone(54, 138.0, 144.0)
    )

    fun zoneFor(longitude: Double): Zone? {
        if (!longitude.isFinite() || longitude < 90.0 || longitude > 144.0) return null
        return zones.firstOrNull {
            longitude >= it.westLongitude &&
                (longitude < it.eastLongitude || (it.number == 54 && longitude <= it.eastLongitude))
        }
    }

    /**
     * Returns the WGS 84 / UTM EPSG code for a coordinate inside the standard
     * UTM latitude band. UTM is defined from 80°S through 84°N; coordinates
     * outside that range require another projection (for example UPS).
     *
     * This selects a CRS only. It does not transform coordinates.
     */
    fun epsgFor(latitude: Double, longitude: Double): Int? {
        if (!latitude.isFinite() || latitude !in -80.0..84.0) return null
        val zone = zoneFor(longitude) ?: return null
        return zone.epsg(isNorthernHemisphere = latitude >= 0.0)
    }
}
