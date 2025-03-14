package com.flydata.data.flight

import android.location.Location
import android.util.Log
import com.flydata.ui.mainScreen.AirportIdentification
import com.flydata.ui.mainScreen.MainScreenViewmodel
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Datakilde for flyvninger.
 *
 * Kan gjøre uthentinger av flyvninger i Norge, uthentinger av detaljert inforrmasjon om en gitt
 * flyvning og uthenting av nærmeste fly.
 */
class FlightDatasource(private val mainScreenViewmodel: MainScreenViewmodel) {
    private val location: Location = Location("").apply {
        // Hvis vi ikke har posisjonen til enheten, så bruker vi posisjonen til IFI
        if (mainScreenViewmodel.deviceLocation == Location("")) {
            this.latitude = 59.943
            this.longitude = 10.717
        } else {
            this.latitude = mainScreenViewmodel.deviceLocation.latitude
            this.longitude = mainScreenViewmodel.deviceLocation.longitude
        }
    }

    // Koordinatene til et rektangel som innkapsler Norge
    private val blLat = 57
    private val blLng = 4
    private val trLat = 72
    private val trLng = 17

    // Grense for antall fly per uthenting
    private val limit = 300

    // API-parametere
    private val client = OkHttpClient()
    private val apiKey = "a17611f0a4mshae99c0a1ac03ecap12fdc0jsn0334234ad7ea"
    private val baseUrl = "https://flight-radar1.p.rapidapi.com"

    // Cache for flyvninger for å redusere antall uthentinger
    private var flightCache: MutableList<FlightDetails> = mutableListOf()

    /**
     * Henter det nærmeste flyet i luftlinje.
     *
     * @return [FlightDetails]-objekt med all informasjon om nærmeste fly.
     */
    fun fetchNearestFlight(): FlightDetails {
        // Gjør uthenting av fly-liste
        val flightList = fetchFlightList().aircraft
        if (flightList.isEmpty()) {
            return FlightDetails()
        }

        return fetchNearestFlight(location, flightList)
    }

    /**
     * Henter det nærmeste flyet i luftlinje.
     *
     * @param location lokasjonen flyene skal sorteres med avstand fra.
     * @param flightList liste over fly-vektorene som skal letes i.
     * @return [FlightDetails]-objekt med all informasjon om nærmeste fly.
     */
    fun fetchNearestFlight(location: Location, flightList: List<List<String>>): FlightDetails {
        // Går gjennom listen med fly og finner det nærmeste flyet
        var nearestFLightState: List<String> = emptyList()
        var nearestDistance: Float = Float.MAX_VALUE
        for (state: List<String> in flightList) {
            val flightLocation = Location("").apply {
                latitude = state[2].toDouble()
                longitude = state[3].toDouble()
            }
            val distance = location.distanceTo(flightLocation)
            if (distance < nearestDistance) {
                nearestFLightState = state
                nearestDistance = distance
            }
        }

        // Setter avstanden til det nærmeste flyet og returnerer det
        val flightDetails = fetchFlightDetails(nearestFLightState[0])
        flightDetails.distance = nearestDistance / 1000
        return flightDetails
    }

    /**
     * Henter en liste med fly i Norge.
     *
     * @return [FlightList]-objekt med liste over fly-vektorer.
     */
    fun fetchFlightList(): FlightList {
        // JSON-deserialiserer til FLightList-klassen
        val adapter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            .adapter(FlightList::class.java)

        // API forespørsel for å hente en liste med fly i en boks rundt Norge
        val request = Request.Builder()
            .url(
                "$baseUrl/flights/list-in-boundary" +
                    "?bl_lat=$blLat&bl_lng=$blLng&tr_lat=$trLat&tr_lng=$trLng&limit=$limit"
            )
            .get()
            .addHeader("X-RapidAPI-Key", apiKey)
            .build()

        Log.d("FLIGHTLIST", request.toString())

        // Henter API-svar og konverterer til FLightList-objekt ved hjelp av JSON-deserialisereren
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        if (responseBody != null) {
            return adapter.fromJson(responseBody)!!
        } else {
            throw Exception("Response body is null")
        }
    }

    /**
     * Henter detaljert informasjon om et gitt fly.
     *
     * @param icao24 koden til flyet som skal hentes.
     * @return [FlightDetails]-objekt som inneholder detaljert informasjon om flyet.
     */
    fun fetchFlightDetails(icao24: String): FlightDetails {
        // Sjekk om flightDetails ligger i cache
        val potentialFlight = flightCache.find { flightDetails ->
            flightDetails.identification?.id == icao24
        }
        if (potentialFlight != null) {
            return potentialFlight
        }

        // JSON-deserialiserer til FLightDetails-klassen
        val adapter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            .adapter(FlightDetails::class.java)

        // API-forespørsel for å hente detaljer om et gitt fly utifra icao24
        val request = Request.Builder()
            .url("$baseUrl/flights/detail?flight=$icao24")
            .get()
            .addHeader("X-RapidAPI-Key", apiKey)
            .build()

        Log.d("Test", request.toString())

        // Henter API-svar og konverterer til FlightDetails-objekt ved hjelp av JSON-deserialisereren
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        if (responseBody != null) {
            val flight = adapter.fromJson(responseBody)!!

            // Legger flyplassinformasjon som bare kan hentes fra dette API-et i flyplass repository
            val iataOrigin = flight.airport?.origin?.code?.iata ?: "OSL"
            val icaoOrigin = flight.airport?.origin?.code?.icao ?: "ENGM"
            val nameOrigin = flight.airport?.origin?.name ?: "Oslo Lufthavn"
            val iataDestination = flight.airport?.destination?.code?.iata ?: "OSL"
            val icaoDestination = flight.airport?.destination?.code?.icao ?: "ENGM"
            val nameDestination = flight.airport?.destination?.name ?: "Oslo Lufthavn"

            val originIdentification = AirportIdentification(iataOrigin, icaoOrigin, nameOrigin)
            val destinationIdentification =
                AirportIdentification(iataDestination, icaoDestination, nameDestination)

            mainScreenViewmodel.addIdentification(originIdentification)
            mainScreenViewmodel.addIdentification(destinationIdentification)

            // Legg til flight i cache og returner
            flightCache.add(flight)
            return flight
        } else {
            throw Exception("Response body is null")
        }
    }
}
