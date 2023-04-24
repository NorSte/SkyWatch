package com.flydata.ui.mainScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flydata.data.FlightDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenViewmodel : ViewModel() {
    val flightDatasource = FlightDatasource()

    private val _mainScreenUIState = MutableStateFlow(MainScreenUIState())
    val mainScreenUIState: StateFlow<MainScreenUIState> = _mainScreenUIState.asStateFlow()

    private var isFlightDisplayed by mutableStateOf(false)
    private var displayedFlightIcao24 by mutableStateOf("")

    private fun updateUIState() {
        viewModelScope.launch(Dispatchers.IO) {
            _mainScreenUIState.update { currentState ->
                currentState.copy(
                    isFlightDisplayed = isFlightDisplayed,
                    displayedFlightIcao24 = displayedFlightIcao24
                )
            }
        }
    }

    fun updateDisplayedFlight(icao24: String) {
        displayedFlightIcao24 = icao24
        updateUIState()
    }

    fun displayFlight() {
        isFlightDisplayed = true
        updateUIState()
    }

    fun dismissFlight() {
        isFlightDisplayed = false
        updateUIState()
    }
}
