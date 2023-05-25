package com.flydata.ui.airportCard

import com.flydata.data.airport.AirportFlight
import com.flydata.data.weather.Weather

/**
 * Dataklasse som inneholder UI-tilstanden til [AirportCard].
 *
 * @property airportFlights listen over flyvninger.
 * @property airportName flyplassens navn.
 * @property airportCode flyplassens IATA-kode.
 * @property typeOfListing hvilken type flyvninger som skal vises (avgang/ankomst).
 * @property airportWeather METAR-værmeldingen for denne flyplassen.
 * @property airportIcao flyplassens ICAO24-kode.
 */
data class AirportCardUIState(
    val airportFlights: List<AirportFlight> = emptyList(),
    val airportName: String = "",
    val airportCode: String = "",
    val typeOfListing: TypeOfListing = TypeOfListing.DEPARTURE,
    val airportWeather: Weather = Weather("", ""),
    val airportIcao: String = ""
)

/**
 * Enum for typer flyvninger som kan vises i et flyplass-kort.
 */
enum class TypeOfListing {
    ARRIVAL, DEPARTURE
}
