package com.flydata.ui.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flydata.R
import com.flydata.ui.airportCard.AirportCard
import com.flydata.ui.flightCard.FlightCard
import com.flydata.ui.flightMap.FlightMap

@Composable
fun MainScreen() {
    val mainScreenViewmodel by remember { mutableStateOf(MainScreenViewmodel()) }
    val mainScreenUIState by mainScreenViewmodel.mainScreenUIState.collectAsState()
    var isPilot by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .background(color = Color.LightGray)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Skytracker",
                    modifier = Modifier
                        .padding(start = 8.dp, end = 4.dp)
                )
                Image(
                    painter = painterResource(R.drawable.skytrackerlogo),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            // Se nærmeste knapp
            Button(
                onClick = {
                    mainScreenViewmodel.updateDisplayedFlight("")
                    mainScreenViewmodel.displayFlight()
                },
                modifier = Modifier
                    .weight(1f)

            ) {
                Text("SjekkFly",)
            }

            // Pilot switch
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(text = "Pilot?")

                Switch(
                    checked = isPilot,
                    onCheckedChange = { isPilot = it },
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp),
                )
            }
        }
        // Start av flightmap
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
