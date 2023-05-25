package com.flydata.ui.airportCard

import androidx.lifecycle.ViewModel
import com.flydata.data.airport.AirportDatasource
import com.flydata.data.weather.MetarDatasource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Viewmodel for [AirportCard].
 *
 * @param iata IATA-koden for nåværende vist flyplass.
 * @property airportDatasource datakilde for flyplasser.
 * @property metarDatasource datakilde for flyplass-værmeldinger.
 * @property icao ICAO24-koden for nåværende vist flyplass. automatisk hentet, derav ikke parameter.
 * @constructor sørger for at valgt flyplass har en standardverdi ved kodefeil.
 */
class AirportCardViewmodel(
    private val airportDatasource: AirportDatasource,
    iata: String = "",
    private val metarDatasource: MetarDatasource,
    private val icao: String
) : ViewModel() {

    private val _airportCardUiState = MutableStateFlow(AirportCardUIState())
    val airportCardUIState: StateFlow<AirportCardUIState> = _airportCardUiState.asStateFlow()

    init {
        if (iata == "") {
            getAirportFlights("OSL")
        } else {
            getAirportFlights(iata)
        }
    }

    /**
     * Henter flyvninger ved en gitt flyplass og oppdaterer UI-tilstanden direkte.
     *
     * @param iata IATA-koden til valgt flyplass.
     */
    private fun getAirportFlights(iata: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _airportCardUiState.value = AirportCardUIState(
                airportDatasource.fetchAirportFlights(
                    iata,
                    airportCardUIState.value.typeOfListing
                ),
                airportCardUIState.value.airportName,
                iata,
                airportWeather = metarDatasource.getTafmetar(icao)
            )
        }
    }

    /**
     * Endrer hvilken type flyvninger som er vist (avgang/ankomst).
     *
     * @param typeOfListing [TypeOfListing]-verdi.
     */
    fun changeTypeOfListing(typeOfListing: TypeOfListing) {
        CoroutineScope(Dispatchers.IO).launch {
            _airportCardUiState.value = AirportCardUIState(
                airportDatasource.fetchAirportFlights(
                    airportCardUIState.value.airportCode,
                    typeOfListing
                ),
                airportCardUIState.value.airportName,
                airportCardUIState.value.airportCode,
                airportWeather = metarDatasource.getTafmetar(icao)
            )
        }
    }
}
