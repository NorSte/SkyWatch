package com.flydata.ui.mainScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flydata.data.airport.AirportDatasource
import com.flydata.data.airport.MetarDatasource
import com.flydata.data.airport.SigmetDatasource
import com.flydata.data.flight.FlightDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AirportIdentification(val iata: String, val icao: String, val name: String)

class MainScreenViewmodel : ViewModel() {
    val flightDatasource = FlightDatasource(this)
    val airportDatasource = AirportDatasource()
    val tafmetardatasource = MetarDatasource()
    private val sigmetdatasource = SigmetDatasource()

    private val _mainScreenUIState = MutableStateFlow(MainScreenUIState())
    val mainScreenUIState: StateFlow<MainScreenUIState> = _mainScreenUIState.asStateFlow()

    private var currentlyDisplayed by mutableStateOf(CurrentlyDisplayed.FLIGHT)
    private var displayedFlightIcao24 by mutableStateOf("")
    private var displayedAirportIata by mutableStateOf("")

    private var airportIdentificationRepository: MutableList<AirportIdentification> =
        mutableListOf()

    fun addIdentification(airportIdentification: AirportIdentification) {
        val potentialIdentification =
            airportIdentificationRepository.find { it.iata == airportIdentification.iata }
        if (potentialIdentification == null) {
            airportIdentificationRepository.add(airportIdentification)
        }
    }

    private fun getIcaoFrom(iata: String): String {
        val potentialIdentification = airportIdentificationRepository.find { it.iata == iata }
        return potentialIdentification?.icao ?: "ENGM"
    }

    private fun updateUIState() {
        viewModelScope.launch(Dispatchers.IO) {
            _mainScreenUIState.update { currentState ->
                currentState.copy(
                    currentlyDisplayed = currentlyDisplayed,
                    displayedFlightIcao24 = displayedFlightIcao24,
                    displayedAirportIata = displayedAirportIata,
                    sigmetMessage = sigmetdatasource.getSigmet(),
                    displayedAirportIcao = getIcaoFrom(displayedAirportIata)
                )
            }
        }
    }

    fun updateDisplayedFlight(icao24: String) {
        displayedFlightIcao24 = icao24
        updateUIState()
    }

    fun displayFlight() {
        currentlyDisplayed = CurrentlyDisplayed.FLIGHT
        updateUIState()
    }

    fun updateDisplayedAirport(iata: String) {
        displayedAirportIata = iata
        updateUIState()
    }

    fun displayAirport() {
        currentlyDisplayed = CurrentlyDisplayed.AIRPORT
        updateUIState()
    }

    fun dismissCard() {
        currentlyDisplayed = CurrentlyDisplayed.NONE
        updateUIState()
    }
}
