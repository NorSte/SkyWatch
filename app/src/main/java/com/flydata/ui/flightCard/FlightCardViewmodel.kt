package com.flydata.ui.flightCard

import androidx.lifecycle.ViewModel
import com.flydata.data.flight.FlightDatasource
import com.flydata.data.flight.FlightDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Viewmodel for flykort.
 *
 * @param icao24 ICAO24-koden til flyet.
 * @property flightDatasource datakilden for flyvninger.
 * @constructor sørger for at valgt flyvning har en standardverdi ved kodefeil (nærmeste fly).
 */
class FlightCardViewmodel(private val flightDatasource: FlightDatasource, icao24: String = "") :
    ViewModel() {
    private val _flightUiState = MutableStateFlow(FlightDetails())
    val flightCardUIState: StateFlow<FlightDetails> = _flightUiState.asStateFlow()

    init {
        if (icao24 == "") {
            getNearestFlight()
        } else {
            getFlight(icao24)
        }
    }

    /**
     * Oppdaterer UI-tilstand til å gjenspeile nærmeste fly.
     */
    private fun getNearestFlight() {
        CoroutineScope(Dispatchers.IO).launch {
            _flightUiState.value = flightDatasource.fetchNearestFlight()
        }
    }

    /**
     * Oppdaterer UI-tilstand til å gjenspeile et gitt fly.
     *
     * @param icao24 ICAO24-koden til flyet.
     */
    private fun getFlight(icao24: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _flightUiState.value = flightDatasource.fetchFlightDetails(icao24)
        }
    }
}
