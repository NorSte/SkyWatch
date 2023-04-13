package com.flydata.ui

import androidx.lifecycle.ViewModel
import com.flydata.data.FlightDetails
import com.flydata.data.FlightsDatasource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlightCardViewmodel : ViewModel() {
    private var flightsDatasource = FlightsDatasource()
    private val _flightUiState = MutableStateFlow(FlightDetails())

    val flightCardUiState: StateFlow<FlightDetails> = _flightUiState.asStateFlow()

    init {
        getNearestFlight()
    }

    private fun getNearestFlight() {
        CoroutineScope(Dispatchers.IO).launch {
            _flightUiState.value = flightsDatasource.fetchNearestFlight()
        }
    }
}
