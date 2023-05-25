package com.flydata.ui.mainScreen

/**
 * Dataklasse som inneholder UI-tilstanden til [MainScreen].
 *
 * @param currentlyDisplayed bestemmer hvilket element som vises (fly/flyplass/ingenting).
 * @param displayedFlightIcao24 ICAO24-koden til eventuelt valgt fly.
 * @param displayedAirportIata IATA-koden til evnetuell valgt flyplass.
 * @param sigmetMessage nåværende SIGMET-melding.
 * @param displayedAirportIcao ICAO24-koden til eventuell valgt flyplass.
 */
data class MainScreenUIState(
    var currentlyDisplayed: CurrentlyDisplayed = CurrentlyDisplayed.FLIGHT,
    var displayedFlightIcao24: String = "",
    var displayedAirportIata: String = "",
    var sigmetMessage: String = "",
    var displayedAirportIcao: String = "",
)

/**
 * Enum for hvilke element som skal vises i [MainScreen].
 */
enum class CurrentlyDisplayed {
    FLIGHT, AIRPORT, NONE
}
