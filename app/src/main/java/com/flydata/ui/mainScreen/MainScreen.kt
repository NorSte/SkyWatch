package com.flydata.ui.mainScreen

import androidx.activity.ComponentActivity
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
import com.flydata.ui.theme.md_theme_light_onPrimaryContainer
import com.flydata.ui.theme.md_theme_light_primary
import com.flydata.ui.theme.md_theme_light_primaryContainer
import kotlinx.coroutines.launch

/**
 * Composable funksjon som viser hovedskjermen og håndterer hvilke elementer som skal vises.
 *
 * @param componentActivity componentActivity for å bruke posisjonsdata.
 */
@Composable
fun MainScreen(componentActivity: ComponentActivity) {
    val mainScreenViewmodel by remember { mutableStateOf(MainScreenViewmodel()) }
    val mainScreenUIState by mainScreenViewmodel.mainScreenUIState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    mainScreenViewmodel.updateLocation(componentActivity)

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Toppbar
        TopBar(mainScreenViewmodel, mainScreenUIState, snackbarHostState)

        // Scaffold med flykart og eventuell fly- eller flyplasskort
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

/**
 * Composable funksjon som viser applikasjons toppbar.
 *
 * @param mainScreenViewmodel viewmodel til `mainScreen` for å åpne visning av nærmeste fly.
 * @param mainScreenUIState UI-tilstand til `mainScreen` for å hente nåværende SIGMET-varsel.
 * @param snackbarHostState SnackbarHost-tilstand for å vise SIGMET-snackbar.
 */
@Composable
fun TopBar(
    mainScreenViewmodel: MainScreenViewmodel,
    mainScreenUIState: MainScreenUIState,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()

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
                text = "SkyWatch",
                modifier = Modifier
                    .padding(start = 8.dp, end = 4.dp),
                color = md_theme_light_onPrimary
            )
            Image(
                painter = painterResource(R.drawable.skywatchlogo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
            )
        }

        // "Se nærmeste fly"-knapp
        Button(
            onClick = {
                mainScreenViewmodel.updateDisplayedFlight("")
                mainScreenViewmodel.displayFlight()
            },
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp, end = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = md_theme_light_primaryContainer
            )
        ) {
            Text(
                "Nærmeste?",
                fontSize = 12.sp,
                color = md_theme_light_onPrimaryContainer
            )
        }

        // SIGMET-knapp
        Button(
            // Vis en snackbar med eventuell SIGMET
            onClick = {
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
                containerColor = md_theme_light_primaryContainer
            )
        ) {
            Text(
                "Naturtrusler",
                fontSize = 12.sp,
                color = md_theme_light_onPrimaryContainer
            )
        }
    }
}
