package com.flydata.ui.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flydata.R
import com.flydata.ui.airportCard.AirportCard
import com.flydata.ui.flightCard.FlightCard
import com.flydata.ui.flightMap.FlightMap
import com.flydata.ui.theme.md_theme_light_onPrimary
import com.flydata.ui.theme.md_theme_light_primary
import com.flydata.ui.theme.md_theme_light_secondary
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val mainScreenViewmodel by remember { mutableStateOf(MainScreenViewmodel()) }
    val mainScreenUIState by mainScreenViewmodel.mainScreenUIState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    /*val findClosest = findViewById<Button>(R.id.my_button)
    val sigmet = findViewById<Button>(R.id.my_button)*/

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth()
                .background(color = md_theme_light_primary)
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
                        .padding(start = 8.dp, end = 4.dp),
                    color = md_theme_light_onPrimary
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
                    .padding(start = 4.dp, end = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = md_theme_light_secondary
                )
            ) {
                Text(
                    "Nærmeste?",
                    fontSize = 12.sp,
                    color = md_theme_light_onPrimary
                )
            }

            // Sigmet meldingsknapp
            Button(
                onClick = {
                    // show snackbar as a suspend function
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (mainScreenUIState.sigmetMessage == "") {
                                "Ingen trusler nå"
                            } else {
                                mainScreenUIState.sigmetMessage
                            }
                        )
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = md_theme_light_secondary
                )
            ) {
                Text(
                    "Naturtrusler",
                    fontSize = 12.sp,
                    color = md_theme_light_onPrimary
                )
            }
        }
        // Start av flightmap med snackbar i tillegg
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            content = {
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
                            } else if (mainScreenUIState.currentlyDisplayed
                                == CurrentlyDisplayed.AIRPORT
                            ) {
                                AirportCard(mainScreenViewmodel)
                            }
                        }
                    }
                }
            }
        )
    }
}
