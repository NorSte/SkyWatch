package com.flydata.ui.mainScreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flydata.ui.airportCard.AirportCard
import com.flydata.ui.flightCard.FlightCard
import com.flydata.ui.flightMap.FlightMap

@Composable
fun MainScreen() {
    val mainScreenViewmodel by remember { mutableStateOf(MainScreenViewmodel()) }
    val mainScreenUIState by mainScreenViewmodel.mainScreenUIState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        Box(Modifier.fillMaxSize()) {
            FlightMap(mainScreenViewmodel)
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
            ) {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    if (mainScreenUIState.currentlyDisplayed == CurrentlyDisplayed.FLIGHT) {
                        FlightCard(mainScreenViewmodel)
                    } else if (mainScreenUIState.currentlyDisplayed == CurrentlyDisplayed.AIRPORT) {
                        AirportCard(mainScreenViewmodel)
                    }
                }
            }
        }
    }
}
