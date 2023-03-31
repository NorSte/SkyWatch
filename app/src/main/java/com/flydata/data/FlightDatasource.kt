package com.flydata.data

import android.location.Location
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*
import java.math.RoundingMode

class FlightDatasource() {

    // posisjonen til IFI
    private val currentLat = 59.94325070410129
    private val currentLong = 10.717822698554867

    private val client = HttpClient()
    private val path =
        "https://opensky-network.org/api/states/all?lamin=57&lomin=4&lamax=72&lomax=15"

    suspend fun fetchFlights(): MutableList<Flight> {
        val flightDataString: String = client.get(path).body()
        client.close()
        val rawData = flightDataString.substring(
            flightDataString.indexOf('[') + 2,
            flightDataString.length - 3
        )
        val listOfFlightStrings: List<String> = rawData.split("],[")
        var listOfFlightObjects = mutableListOf<Flight>()
        for (flight in listOfFlightStrings) {
            val properties = flight.split(",")
            var distance = FloatArray(1)
            Location.distanceBetween(
                currentLong, currentLat, properties[5].toDouble(),
                properties[6].toDouble(), distance
            )
            var flightObject = Flight(
                properties[0],
                properties[1],
                properties[2],
                properties[5].toDouble(),
                properties[6].toDouble(),
                (distance[0] / 1000).toBigDecimal().setScale(
                    1,
                    RoundingMode.HALF_UP
                ).toFloat()
            )
            listOfFlightObjects.add(flightObject)
        }
        listOfFlightObjects.sortBy { it.distance }
        return listOfFlightObjects
    }
}
