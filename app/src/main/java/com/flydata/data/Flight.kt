package com.flydata.data

data class Flight(
    val icao24: String,
    val callsign: String,
    val countryOfOrigin: String,
    val longitude: Double,
    val latitude: Double,
    val distance: Float
)
