package com.flydata.ui.mainScreen

enum class CurrentlyDisplayed {
    FLIGHT, AIRPORT, NONE
}

data class MainScreenUIState(
    var currentlyDisplayed: CurrentlyDisplayed = CurrentlyDisplayed.NONE,
    var displayedFlightIcao24: String = "",
    var displayedAirportIata: String = ""
)
