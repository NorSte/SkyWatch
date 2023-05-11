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

@Composable
fun FlightMap(mainScreenViewmodel: MainScreenViewmodel) {
    val flightMapViewModel by remember { mutableStateOf(FlightMapViewmodel()) }
    val flightMapUIState by flightMapViewModel.flightMapUiState.collectAsState()

    fun handleClickMarker(): (Marker) -> Boolean {
        return { marker ->
            val icao24 = marker.title ?: ""
            mainScreenViewmodel.displayFlight()
            mainScreenViewmodel.updateDisplayedFlight(icao24)
            true
        }
    }

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
