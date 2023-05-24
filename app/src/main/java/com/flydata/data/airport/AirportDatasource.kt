package com.flydata.data.airport

import com.flydata.ui.airportCard.TypeOfListing
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import java.io.InputStream

/**
 * Datakilde for flyplasser.
 *
 * Kan gjøre uthentinger av flyvninger ved en gitt flyplass.
 */
class AirportDatasource {
    // Variabler til API-uthenting
    private val client = HttpClient()
    private val flightsPath = "https://flydata.avinor.no/XmlFeed.asp?TimeFrom=1&TimeTo=2&airport="

    // Flyplass-cache for å reduserer antall uthentinger
    private var airportCache: MutableList<Airport> = mutableListOf()

    /**
     * API-uthenting av flyvninger ved en flyplass.
     *
     * Henter flyvninger fra cache eller Avinor API.
     *
     * @param iata IATA-koden til flyplassen.
     * @param typeOfListing typen flyvninger som skal vises blant typene fra [TypeOfListing].
     *
     * @return liste med [AirportFlight].
     */
    suspend fun fetchAirportFlights(
        iata: String,
        typeOfListing: TypeOfListing
    ): MutableList<AirportFlight> {
        // Sjekk om flightDetails ligger i cache
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

        // Gjør uthenting og hopper over de første to taggene for at parsingen skal fungere
        var data: String = client.get(flightsPath + iata).body()
        data = data.substring(data.indexOf('>') + 1, data.length - 1)
        data = data.substring(data.indexOf('>') + 1, data.length - 1)

        // Parser API-svar og filtrerer etter ankomst/avgang
        val inputStream: InputStream = data.byteInputStream()
        val airportFlights = AirportFlightsXmlParser().parse(inputStream)
        val filteredAirportFlights = airportFlights.filter {
            if (typeOfListing == TypeOfListing.ARRIVAL) {
                it.arrDep == "A"
            } else {
                it.arrDep == "D"
            }
        } as MutableList<AirportFlight>

        // Legg til airport i cache og returner
        airportCache.add(Airport(iata, airportFlights))
        return filteredAirportFlights
    }

    /**
     * Sørger for at flyplassnavn ikke blir for lange.
     *
     * @param name navn på flyplass som skal forkortes.
     *
     * @return forkortet navn.
     */
    fun airportNameShortner(name: String): String {
        val maxLength = 14
        return if (name.length < maxLength) {
            name
        } else {
            name.substring(0, maxLength) + ".."
        }
    }
}

/**
 * Dataklasse som inneholder all informasjon som trenger å bli cachet fra en flyplass.
 *
 * @property iata koden til flyplassen.
 * @property airportFlights liste over flyvningene ved flyplassen.
 */
data class Airport(
    val iata: String,
    val airportFlights: MutableList<AirportFlight>
)

/**
 * Dataklasse som inneholder all informasjonen om et gitt fly fra API-uthentingen.
 *
 * @property airline navn på flyselskap.
 * @property flightId id-en (callsign) til flyvningen.
 * @property scheduleTime planlagt tid for ankomst/avgang.
 * @property arrDep type flyvning, ankomst/avgang. Kan være "A" (Arrival) eller "D" (Departure).
 * @property airport navn på flyplass.
 * @property statusText statusmelding.
 * @property statusTime tidspunktet statusmeldingen ble gitt.
 */
data class AirportFlight(
    val airline: String,
    val flightId: String,
    val scheduleTime: String,
    val arrDep: String,
    var airport: String,
    var statusText: String,
    var statusTime: String,
)

/**
 * Dataklasse som inneholder informasjon om statusmeldinger som enkelte fly kan ha.
 *
 * @property status statusmelding.
 * @property time tidspunktet statusmeldingen ble gitt.
 */
data class FlightStatus(
    var status: String?,
    var time: String?
)
