package com.flydata.ui.airportCard

import com.flydata.data.airport.AirportFlight
import com.flydata.data.airport.Weather

enum class TypeOfListing {
    ARRIVAL, DEPARTURE
}

data class AirportCardUIState(
    val airportFlights: List<AirportFlight> = emptyList(),
    val airportName: String = "",
    val airportCode: String = "",
    val typeOfListing: TypeOfListing = TypeOfListing.DEPARTURE,
    val airportWeather: Weather = Weather("", ""),
    val airportIcao: String = ""
)
