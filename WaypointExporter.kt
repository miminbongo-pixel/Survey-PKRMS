package id.geoexplorer.app.core.vector

import id.geoexplorer.app.core.coordinates.IndonesiaUtmZones
import java.util.Locale

data class SurveyWaypoint(
    val id: String,
    val label: String,
    val latitude: Double,
    val longitude: Double
) {
    val epsg: Int?
        get() = IndonesiaUtmZones.epsgFor(latitude, longitude)

    val utmZoneLabel: String
        get() {
            val zone = IndonesiaUtmZones.zoneFor(longitude) ?: return "Di luar cakupan starter"
            return "Zona ${zone.number}${if (latitude >= 0.0) "N" else "S"}"
        }
}

/**
 * Utilitas ekspor dan impor daftar titik survei ke format CSV dan GeoJSON (JSON)
 * agar kompatibel dengan QGIS, spreadsheet, dan alat GIS eksternal.
 */
object WaypointExporter {
    fun toCsv(waypoints: List<SurveyWaypoint>): String {
        val header = "id,label,latitude,longitude,utm_zone,epsg"
        if (waypoints.isEmpty()) return header
        val rows = waypoints.map { wp ->
            val lat = String.format(Locale.US, "%.6f", wp.latitude)
            val lon = String.format(Locale.US, "%.6f", wp.longitude)
            val epsgText = wp.epsg?.toString() ?: ""
            listOf(
                escapeCsv(wp.id),
                escapeCsv(wp.label),
                lat,
                lon,
                escapeCsv(wp.utmZoneLabel),
                epsgText
            ).joinToString(",")
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    fun toGeoJson(waypoints: List<SurveyWaypoint>): String {
        val features = waypoints.joinToString(",\n") { wp ->
            val lat = String.format(Locale.US, "%.6f", wp.latitude)
            val lon = String.format(Locale.US, "%.6f", wp.longitude)
            val epsgJson = wp.epsg?.toString() ?: "null"
            """
            |    {
            |      "type": "Feature",
            |      "properties": {
            |        "id": "${escapeJson(wp.id)}",
            |        "label": "${escapeJson(wp.label)}",
            |        "latitude": $lat,
            |        "longitude": $lon,
            |        "utm_zone": "${escapeJson(wp.utmZoneLabel)}",
            |        "epsg": $epsgJson
            |      },
            |      "geometry": {
            |        "type": "Point",
            |        "coordinates": [$lon, $lat]
            |      }
            |    }
            """.trimMargin()
        }

        return """
            |{
            |  "type": "FeatureCollection",
            |  "name": "geoexplorer_waypoints",
            |  "features": [
            |$features
            |  ]
            |}
        """.trimMargin()
    }

    private fun escapeCsv(value: String): String {
        return if (value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    private fun escapeJson(value: String): String {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
    }
}
