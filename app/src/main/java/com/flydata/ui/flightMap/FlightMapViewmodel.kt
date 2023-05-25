package com.flydata.ui.flightMap

import com.flydata.data.flight.FlightDatasource
import com.flydata.data.flight.FlightList
import com.flydata.ui.mainScreen.MainScreenViewmodel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Viewmodel for flykart.
 *
 * @constructor oppdaterer kartet med flyvninger i Norge.
 */
class FlightMapViewmodel {
    private val flightDatasource = FlightDatasource(MainScreenViewmodel())
    private val _flightMapUIState = MutableStateFlow(FlightList())
    val flightMapUiState: StateFlow<FlightList> = _flightMapUIState.asStateFlow()

    init {
        getFlightList()
    }

    /**
     * Henter flyvninger i Norge og oppdaterer UI-tilstanden.
     */
    private fun getFlightList() {
        CoroutineScope(Dispatchers.IO).launch {
            _flightMapUIState.value = flightDatasource.fetchFlightList()
        }
    }
}
