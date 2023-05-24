package com.flydata.ui.flightMap

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.flydata.R
import com.flydata.ui.mainScreen.MainScreenViewmodel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

/**
 * Composable funksjon som viser et kart over flyvninger.
 *
 * @param mainScreenViewmodel bruker viewmodel fra `mainScreen` for å navigere til flyvisning.
 */
@Composable
fun FlightMap(mainScreenViewmodel: MainScreenViewmodel) {
    val flightMapViewModel by remember { mutableStateOf(FlightMapViewmodel()) }
    val flightMapUIState by flightMapViewModel.flightMapUiState.collectAsState()

    /**
     * Håndterer klikk på fly på kartet.
     *
     * @return returnerer en lambda-funksjon som åpner flyvisningen.
     */
    fun handleClickMarker(): (Marker) -> Boolean {
        return { marker ->
            val icao24 = marker.title ?: ""
            mainScreenViewmodel.displayFlight()
            mainScreenViewmodel.updateDisplayedFlight(icao24)
            true
        }
    }

    // Kartet
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        onMapClick = { mainScreenViewmodel.dismissCard() },
        cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(59.943, 10.717),
                6f
            )
        }
    ) {
        // Viser et fly-ikon for hvert fly i Norge på kartet
        flightMapUIState.aircraft.forEach {
            val icao24 = it[0]
            val lat = it[2]
            val lng = it[3]
            val rotation = it[4]

            Marker(
                state = MarkerState(position = LatLng(lat.toDouble(), lng.toDouble())),
                title = icao24,
                snippet = "Fly $icao24",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.plane_icon),
                rotation = rotation.toFloat(),
                onClick = handleClickMarker()
            )
        }
    }
}
