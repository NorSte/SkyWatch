package com.flydata.ui.flightMap

import com.flydata.data.FlightDatasource
import com.flydata.data.FlightList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlightMapViewmodel {
    private val flightDatasource = FlightDatasource()
    private val _flightMapUIState = MutableStateFlow(FlightList())
    val flightMapUiState: StateFlow<FlightList> = _flightMapUIState.asStateFlow()

    init {
        getFlightList()
    }

    private fun getFlightList() {
        CoroutineScope(Dispatchers.IO).launch {
            _flightMapUIState.value = flightDatasource.fetchFlightList()
        }
    }
}
