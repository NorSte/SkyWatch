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

    private fun getNearestFlight() {
        CoroutineScope(Dispatchers.IO).launch {
            _flightUiState.value = flightDatasource.fetchNearestFlight()
        }
    }

    private fun getFlight(icao24: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _flightUiState.value = flightDatasource.fetchFlightDetails(icao24)
        }
    }
}
