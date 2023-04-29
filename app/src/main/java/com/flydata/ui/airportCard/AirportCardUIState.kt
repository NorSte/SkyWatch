package com.flydata.ui.airportCard

import com.flydata.data.airport.AirportFlight

enum class TypeOfListing {
    ARRIVAL, DEPARTURE
}

data class AirportCardUIState(
    val airportFlights: List<AirportFlight> = emptyList(),
    val airportName: String = "",
    val airportCode: String = "",
    val typeOfListing: TypeOfListing = TypeOfListing.DEPARTURE,
)
