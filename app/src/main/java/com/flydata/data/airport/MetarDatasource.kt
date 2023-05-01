package com.flydata.data.airport

/* 0kHttp bibliotek: ...
import java.io.InputStream
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
*/

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.io.InputStream

class MetarDataSource {

    private val client = HttpClient() {}

    // private val client1 = OkHttpClient()
    private val baseUrl = "https://api.met.no/weatherapi/tafmetar/1.0/?" +
        "content_type=text/xml&date=2023-03-31&offset=+02:00&content=metar" +
        "&icao="

    suspend fun getTafmetar(icao: String): Weather {

        val url = baseUrl + icao

        val response: String = client.get(url).body()
        val inputStream: InputStream = response.byteInputStream() // til byte
        val listOfMetar: List<Metar> = MetarXmlParser().parse(inputStream)

        // Til bruk av 0khttp biblioteket...
        /*val request = Request.Builder()
            .url(url)
            .get()
            .build()
        val response1: Response = client1.newCall(request).execute()
        val responseBody = response1.body?.string()

        val inputStream1: InputStream? = responseBody?.byteInputStream() // til byte
        val listOfMetar1: List<Metar> = MetarXmlParser().parse(inputStream1)*/

        // return MetarDecoder(listOfMetar1.last().metarText)

        return MetarDecoder(listOfMetar.last().metarText)
    }

    private fun MetarDecoder(text: String?): Weather {
        // i form ENGM 310350Z 03006KT CAVOK M02/M07 Q1004 NOSIG=
        // det er mulighet for videre dekoding, til og med temperatur

        if (text == null) { return Weather(".", ".") }
        val words = text.split("\\s+".toRegex())
        var airport = words[0]
        var direction = "."
        var wind = "."

        for (word in words) {
            // var aa = word.take(6)
            var bb = word.takeLast(2)

            if (bb == "KT") {
                // 05007G17KT
                var aa = word.take(5)
                direction = aa.take(3)
                wind = aa.takeLast(2)
            }
        }

        val floatWind = wind.toFloat() * 0.51
        // 03 --> 3.000 ,  3.000*0.51= 1.53 m/s

        val printWind: String = String.format("%.1f", floatWind)

        return Weather("$printWind m/s", direction)
    }
}

data class Weather(val wind: String, val direction: String)
