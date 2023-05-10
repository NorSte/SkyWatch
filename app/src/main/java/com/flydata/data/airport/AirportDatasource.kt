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

    // cache
    private var airportCache: MutableList<Airport> = mutableListOf()

    suspend fun fetchAirportFlights(
        iata: String,
        typeOfListing: TypeOfListing
    ): MutableList<AirportFlight> {
        // sjekk om flightDetails ligger i cache
        val potentialAirport = airportCache.find { airport ->
            airport.iata == iata
        }
        if (potentialAirport != null) {
            var airportFlights = potentialAirport.airportFlights
            airportFlights = airportFlights.filter {
                if (typeOfListing == TypeOfListing.ARRIVAL) {
                    it.arrDep == "A"
                } else {
                    it.arrDep == "D"
                }
            } as MutableList<AirportFlight>

            println("Brukte cache")
            return airportFlights
        }

        // gjør uthenting og hopper over de første to taggene for at parsingen skal fungere
        var data: String = client.get(flightsPath + iata).body()
        data = data.substring(data.indexOf('>') + 1, data.length - 1)
        data = data.substring(data.indexOf('>') + 1, data.length - 1)

        // parser API-svar og filtrerer etter ankomst/avgang
        val inputStream: InputStream = data.byteInputStream()
        val airportFlights = AirportFlightsXmlParser().parse(inputStream)
        val filteredAirportFlights = airportFlights.filter {
            if (typeOfListing == TypeOfListing.ARRIVAL) {
                it.arrDep == "A"
            } else {
                it.arrDep == "D"
            }
        } as MutableList<AirportFlight>

        // legg til airport i cache og returner
        airportCache.add(Airport(iata, airportFlights))
        return filteredAirportFlights
    }

    fun AirportNameShortner(text : String) : String
    {
        val Maxlenght : Int = 14
        return "."
    }
}

data class Airport(
    val iata: String,
    val airportFlights: MutableList<AirportFlight>
)

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
