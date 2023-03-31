package com.flydata.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import com.flydata.FlightCardUiState
import com.flydata.data.FlightDatasource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlightCardViewmodel : ViewModel() {
    private val data = FlightDatasource()
    private val _flightCardUiState = MutableStateFlow(FlightCardUiState())

    val flightCardUiState: StateFlow<FlightCardUiState> = _flightCardUiState.asStateFlow()

    init {
        getFlights()
    }

    fun getFlights() {
        CoroutineScope(Dispatchers.IO).launch {
            var data = FlightDatasource()
            var flights = data.fetchFlights()
            for (flight in flights) {
                Log.d("DEBUG", flight.toString())
            }
            _flightCardUiState.value = FlightCardUiState(
                flights[0].distance,
                flights[0].callsign
            )
        }
    }
}
