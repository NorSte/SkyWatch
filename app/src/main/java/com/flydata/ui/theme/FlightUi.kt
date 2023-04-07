package com.flydata.ui.theme

data class FlightUi(
    val flightId: String,
    val departureAirport: String?,
    val departureScheduleTime: String?,
    val departureActualTime: String?,
    val destinationAirport: String?,
    val destinationScheduleTime: String?,
    val destinationActualTime: String?,
    val distance: Float,
)
