package com.flydata.data.airport

import com.flydata.ui.airportCard.TypeOfListing
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.io.InputStream

class AirportDatasource {
    private val client = HttpClient()

    // URL til Avinor-API
    private val flightsPath = "https://flydata.avinor.no/XmlFeed.asp?TimeFrom=1&TimeTo=2&airport="

    suspend fun fetchAirportFlights(
        iata: String,
        typeOfListing: TypeOfListing
    ): MutableList<AirportFlight> {
        var data: String = client.get(flightsPath + iata).body()

        // Må hoppe over de første to taggene for at parsingen skal fungere
        data = data.substring(data.indexOf('>') + 1, data.length - 1)
        data = data.substring(data.indexOf('>') + 1, data.length - 1)

        val inputStream: InputStream = data.byteInputStream()

        var airportFlights = AirportFlightsXmlParser().parse(inputStream)
        airportFlights = airportFlights.filter {
            if (typeOfListing == TypeOfListing.ARRIVAL) {
                it.arrDep == "A"
            } else {
                it.arrDep == "D"
            }
        } as MutableList<AirportFlight>

        return airportFlights
    }
}

data class AirportFlight(
    val airline: String,
    val flightId: String,
    val scheduleTime: String,
    val arrDep: String,
    var airport: String,
    var statusText: String,
    var statusTime: String,
)

data class FlightStatus(
    var status: String?,
    var time: String?
)
