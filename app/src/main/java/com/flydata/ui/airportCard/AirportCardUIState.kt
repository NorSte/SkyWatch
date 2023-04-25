package com.flydata.ui.airportCard

import com.flydata.data.airport.AirportFlight

data class AirportCardUIState(
    val airportFlights: List<AirportFlight> = emptyList(),
    val airportName: String = "",
    val airportCode: String = "",
)
