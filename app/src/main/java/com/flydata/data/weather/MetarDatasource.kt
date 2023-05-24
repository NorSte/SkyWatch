package com.flydata.data.weather

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.io.InputStream

/**
 * Datakilde for METAR-værmeldinger.
 *
 * Kan gjøre uthentinger av TAFMETAR-meldinger, og dekode METAR-meldinger.
 */
class MetarDatasource {
    // API-parametere
    private val client = HttpClient {}
    private val baseUrl = "https://gw-uio.intark.uh-it.no/in2000/weatherapi/tafmetar/1.0/?" +
        "content_type=text/xml&offset=+02:00&content=metar" +
        "&icao="

    /**
     * Henter nåværende TAFMETAR-melding ved gitt flyplass.
     *
     * @param icao koden til flyplass hvor værmelding skal hentes.
     * @return [Weather]-objekt som inneholder vindstyrke og vindretning.
     */
    suspend fun getTafmetar(icao: String): Weather {

        val url = baseUrl + icao

        val response: String = client.get(url) {
            headers {
                append("X-Gravitee-API-Key", "63c8656a-886c-4423-a70c-2937fd41fb5e")
            }
        }.body()
        val inputStream: InputStream = response.byteInputStream() // til byte
        val listOfMetar: List<Metar> = MetarXmlParser().parse(inputStream)

        return metarDecoder(listOfMetar.last().metarText)
    }

    /**
     * Dekoder METAR-meldinger.
     *
     * Gjør om fra METAR-form (f.eks.: ENGM 310350Z 03006KT CAVOK M02/M07 Q1004 NOSIG=) til
     * [Weather]-objekt.
     *
     * @param metarText METAR-meldingen som skal dekodes.
     * @return [Weather]-objekt som inneholder vindstyrke og vindretning.
     */
    fun metarDecoder(metarText: String?): Weather {
        var weatherfound = false
        if (metarText == null) {
            return Weather(".", ".")
        }
        var direction = "."
        var wind = "."

        if (metarText == "") {
            return Weather(wind, direction)
        }

        val words = metarText.split("\\s+".toRegex())

        for (word in words) {
            val knots = word.takeLast(2)

            if (knots == "KT") {
                // 05007G17KT
                val knotNumber = word.take(5)
                direction = knotNumber.take(3)
                wind = knotNumber.takeLast(2)
                if (wind.toFloatOrNull() != null) {
                    // er null når ICAO slutter på KT, f.eks EPKT
                    weatherfound = true
                    break
                }
            }
        }
        if (!weatherfound) {
            return Weather(wind, direction)
        }
        val floatWind = wind.toFloat() * 0.51
        // 03 --> 3.000 ,  3.000*0.51= 1.53 m/s

        val printWind: String = String.format("%.1f", floatWind)

        return Weather("$printWind m/s", direction)
    }
}

/**
 * Dataklasse som inneholder vindstyrke og vindretning.
 *
 * @property wind vindstyrke.
 * @property direction vindretning.
 */
data class Weather(val wind: String, val direction: String)
