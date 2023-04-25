package com.flydata.ui.airportCard

import androidx.lifecycle.ViewModel
import com.flydata.data.airport.AirportDatasource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AirportCardViewmodel(private val airportDatasource: AirportDatasource, iata: String = "") :
    ViewModel() {
    private val _airportScreenUiState = MutableStateFlow(AirportCardUIState())
    val airportScreenUiState: StateFlow<AirportCardUIState> = _airportScreenUiState.asStateFlow()

    init {
        if (iata == "") {
            getAirportFlights("OSL")
        } else {
            getAirportFlights(iata)
        }
    }

    private fun getAirportFlights(iata: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _airportScreenUiState.value = AirportCardUIState(
                airportDatasource.fetchAirportFlights(
                    iata
                ),
                "Navn",
                iata
            )
        }
    }
}
