package com.flydata.ui.theme

import androidx.lifecycle.ViewModel
import com.flydata.FlightCardUiState
import com.flydata.data.FlightsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlightCardViewmodel : ViewModel() {
    private var flightsRepository = FlightsRepository()
    private val _flightCardUiState = MutableStateFlow(FlightCardUiState())

    val flightCardUiState: StateFlow<FlightCardUiState> = _flightCardUiState.asStateFlow()

    init {
        getFlights()
    }

    fun getFlights() {
        CoroutineScope(Dispatchers.IO).launch {
            var nearestFlight = flightsRepository.fetchNearestFlight()

            _flightCardUiState.value = FlightCardUiState(
                nearestFlight.flightId,
                nearestFlight.departureAirport ?: "Ukjent",
                nearestFlight.departureScheduleTime ?: "Ukjent",
                nearestFlight.departureActualTime ?: "Ukjent",
                nearestFlight.destinationAirport ?: "Ukjent",
                nearestFlight.destinationScheduleTime ?: "Ukjent",
                nearestFlight.destinationActualTime ?: "Ukjent",
                nearestFlight.distance
            )
        }
    }
}
