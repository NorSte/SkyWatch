package com.flydata.data.airport

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.io.InputStream

class MetarDatasource {

    private val client = HttpClient {}

    private val baseUrl = "https://api.met.no/weatherapi/tafmetar/1.0/?" +
        "content_type=text/xml&date=2023-03-31&offset=+02:00&content=metar" +
        "&icao="

    suspend fun getTafmetar(icao: String): Weather {

        val url = baseUrl + icao

        val response: String = client.get(url).body()
        val inputStream: InputStream = response.byteInputStream() // til byte
        val listOfMetar: List<Metar> = MetarXmlParser().parse(inputStream)

        return metarDecoder(listOfMetar.last().metarText)
    }

    private fun metarDecoder(text: String?): Weather {
        // i form ENGM 310350Z 03006KT CAVOK M02/M07 Q1004 NOSIG=
        // det er mulighet for videre dekoding, til og med temperatur

        if (text == null) { return Weather(".", ".") }
        val words = text.split("\\s+".toRegex())
        var direction = "."
        var wind = "."

        for (word in words) {
            val knots = word.takeLast(2)

            if (knots == "KT") {
                // 05007G17KT
                val knotNumber = word.take(5)
                direction = knotNumber.take(3)
                wind = knotNumber.takeLast(2)
            }
        }

        val floatWind = wind.toFloat() * 0.51
        // 03 --> 3.000 ,  3.000*0.51= 1.53 m/s

        val printWind: String = String.format("%.1f", floatWind)

        return Weather("$printWind m/s", direction)
    }

    public fun Metardecoder(text: String?): Weather {
        return metarDecoder(text)
    }
}

data class Weather(val wind: String, val direction: String)
