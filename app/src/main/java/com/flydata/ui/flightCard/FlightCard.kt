package com.flydata.ui.flightCard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.flydata.data.flight.AirportIdentification
import com.flydata.ui.mainScreen.MainScreenViewmodel
import com.flydata.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FlightCard(mainScreenViewmodel: MainScreenViewmodel) {
    val flightViewmodel by remember {
        mutableStateOf(
            FlightCardViewmodel(
                mainScreenViewmodel.flightDatasource,
                mainScreenViewmodel.mainScreenUIState.value.displayedFlightIcao24
            )
        )
    }
    val flightUIState by flightViewmodel.flightCardUIState.collectAsState()

    // For Scrollable
    val scrollState = rememberScrollState()

    val timeTables = TimeTables(
        TimeTable(
            flightUIState.time?.scheduled?.departure ?: 0,
            flightUIState.time?.estimated?.departure ?: 0
        ),
        TimeTable(
            flightUIState.time?.scheduled?.arrival ?: 0,
            flightUIState.time?.estimated?.arrival ?: 0
        )
    )

    Card(
        Modifier
            .fillMaxWidth()
            .scrollable(
                orientation = Orientation.Vertical,
                state = scrollState
            )
    ) {
        // Øverste column er all content som skal bli gjort scrollable
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
        ) {
            if (flightUIState.identification?.id != "N/A") {
                Column(
                    Modifier
                        .background(color = md_theme_light_primary)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.padding(horizontal = 6.dp)) {
                            Row {
                                Text(
                                    flightUIState.identification?.callsign ?: "Ingen callsign",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    ", " + flightUIState.airline?.short,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                            Text(
                                "Type: ${flightUIState.aircraft?.model?.code}",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "Distanse: ${flightUIState.distance?.toInt()}km",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        IconButton(onClick = { mainScreenViewmodel.dismissCard() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }
                Column {

                    val imageUrl = flightUIState.aircraft?.images?.medium?.get(0)?.src
                    if (imageUrl != null) {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxSize(),
                            model = imageUrl,
                            contentDescription =
                            "Image of plane ${flightUIState.identification?.id ?: "N/A"}",
                            contentScale = ContentScale.FillWidth
                        )
                    }

                    Row(Modifier.fillMaxWidth()) {
                        AirportInfo(
                            mainScreenViewmodel,
                            false,
                            flightUIState.airport?.origin ?: AirportIdentification(),
                            timeTables.origin,
                            Modifier
                                .weight(1f)
                                .padding(5.dp)
                        )
                        AirportInfo(
                            mainScreenViewmodel,
                            true,
                            flightUIState.airport?.destination ?: AirportIdentification(),
                            timeTables.destination,
                            Modifier
                                .weight(1f)
                                .padding(5.dp)
                        )
                    }
                }
            } else {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    Arrangement.Center
                ) {
                    Text("Laster inn flydata ...")
                }
            }
        }
    }
}

@Composable
fun AirportInfo(
    mainScreenViewmodel: MainScreenViewmodel,
    isDestinationAirport: Boolean,
    airport: AirportIdentification,
    timeTable: TimeTable,
    modifier: Modifier
) {
    Column(modifier) {
        Column(
            // Kan klikke hvor som helst fra avg/ank ned til navnet
            modifier = Modifier.fillMaxWidth()
                .clickable {
                    mainScreenViewmodel.updateDisplayedAirport(airport.code?.iata ?: "OSL")
                    mainScreenViewmodel.displayAirport()
                }
        ) {
            Text(
                if (isDestinationAirport) "Ankomst" else "Avgang",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
            )

            Text(
                text = airport.code?.iata ?: "N/A",
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable {
                    if (airport.code != null) {
                        mainScreenViewmodel.updateDisplayedAirport(airport.code.iata)
                        mainScreenViewmodel.displayAirport()
                    }
                }
            )
        }
        val lastIndexOfSpace = airport.name?.lastIndexOf(" ") ?: 0
        Text(
            text = if (airport.name != null) {
                if (lastIndexOfSpace == -1) {
                    checkMaxAirportLength(airport.name)
                } else checkMaxAirportLength(airport.name.substring(0, lastIndexOfSpace))
            } else { "N/A" },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textDecoration = TextDecoration.Underline,
            fontSize = 13.sp
        )
        Time(
            isDestinationAirport, true,
            SimpleDateFormat(
                "HH:mm",
                Locale("no", "NO")
            ).format(Date(timeTable.expected * 1000))
        )
        Time(
            isDestinationAirport,
            false,
            SimpleDateFormat("HH:mm", Locale("no", "NO")).format(
                Date(
                    if (timeTable.actual != 0L) {
                        timeTable.actual * 1000
                    } else timeTable.expected * 1000
                )
            )
        )
    }
}

@Composable
fun Time(isDestinationAirport: Boolean, isPlanned: Boolean, time: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)
            .background(md_theme_light_secondary)
            .padding(5.dp),
        Arrangement.SpaceBetween
    ) {
        val timeString: String = if (isPlanned) "Planlagt" else {
            if (isDestinationAirport) "Forventet" else "Avreist"
        }
        Text(timeString, color = MaterialTheme.colorScheme.onPrimary)
        Text(time, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
    }
}

fun checkMaxAirportLength(string: String): String {
    val maxLength = 16

    return if (string.length < maxLength) {
        string
    } else {
        // returnerer maks 16 tegn + ...
        string.substring(0, maxLength) + "..."
    }
}
