package com.flydata.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.io.InputStream

class AirportDatasource {
    private val client = HttpClient()
    private val path =
        "https://flydata.avinor.no/XmlFeed.asp?TimeFrom=1&TimeTo=2&airport=OSL"

    suspend fun fetchAirportFlights(): List<AirportFlight> {
        var data: String = client.get(path).body()
        client.close()

        // Må hoppe over de første to taggene for at parsingen skal fungere
        data = data.substring(data.indexOf('>') + 1, data.length - 1)
        data = data.substring(data.indexOf('>') + 1, data.length - 1)

        val inputStream: InputStream = data.byteInputStream()
        return AirportXmlParser().parse(inputStream)
    }
}
