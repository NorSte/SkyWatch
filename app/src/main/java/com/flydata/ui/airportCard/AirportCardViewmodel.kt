package com.flydata.ui.airportCard

import androidx.lifecycle.ViewModel
import com.flydata.data.airport.AirportDatasource
import com.flydata.data.airport.MetarDatasource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AirportCardViewmodel(
    private val airportDatasource: AirportDatasource,
    iata: String = "",
    private val tafmetardatasource: MetarDatasource,
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

    private fun getAirportFlights(iata: String) {
        CoroutineScope(Dispatchers.IO).launch {
            _airportCardUiState.value = AirportCardUIState(
                airportDatasource.fetchAirportFlights(
                    iata,
                    airportCardUIState.value.typeOfListing
                ),
                airportCardUIState.value.airportName,
                iata,
                airportWeather = tafmetardatasource.getTafmetar(icao)
            )
        }
    }

    fun changeTypeOfListing(typeOfListing: TypeOfListing) {
        CoroutineScope(Dispatchers.IO).launch {
            _airportCardUiState.value = AirportCardUIState(
                airportDatasource.fetchAirportFlights(
                    airportCardUIState.value.airportCode,
                    typeOfListing
                ),
                airportCardUIState.value.airportName,
                airportCardUIState.value.airportCode
            )
        }
    }
}
