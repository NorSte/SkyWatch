package com.flydata.data.flight

import android.location.Location
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request

class FlightDatasource {
    // posisjonen til IFI
    private val location: Location = Location("").apply {
        this.latitude = 59.943
        this.longitude = 10.717
    }

    // koordinatene til et rektangel som innkapsler Norge
    private val blLat = 57
    private val blLng = 4
    private val trLat = 72
    private val trLng = 17

    // grense for antall fly per uthenting
    private val limit = 300

    // API-parametere
    private val client = OkHttpClient()
    private val apiKey = "ea5e46b3c6msh7f32eeb76c5d39fp11ac4cjsn4b6bae119b37"
    private val baseUrl = "https://flight-radar1.p.rapidapi.com"

    // cache
    private var flightCache: MutableList<FlightDetails> = mutableListOf()

    fun fetchNearestFlight(): FlightDetails {
        // gjør uthenting av fly-liste
        val flightList = fetchFlightList().aircraft

        // går gjennom listen med fly og finner det nærmeste flyet
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

        // setter avstanden til det nærmeste flyet og returnerer det
        val flightDetails = fetchFlightDetails(nearestFLightState[0])
        flightDetails.distance = nearestDistance / 1000
        return flightDetails
    }

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

        // henter API-svar og konverterer til FLightList-objekt ved hjelp av JSON-deserialisereren
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        if (responseBody != null) {
            return adapter.fromJson(responseBody)!!
        } else {
            throw Exception("Response body is null")
        }
    }

    fun fetchFlightDetails(icao24: String): FlightDetails {
        // sjekk om flightDetails ligger i cache
        val potentialFlight = flightCache.find { flightDetails ->
            flightDetails.identification.id == icao24
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

        // henter API-svar og konverterer til FlightDetails-objekt ved hjelp av JSON-deserialisereren
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
        if (responseBody != null) {
            val flight = adapter.fromJson(responseBody)!!

            // legg til flight i cache og returner
            flightCache.add(flight)
            return flight
        } else {
            throw Exception("Response body is null")
        }
    }
}
