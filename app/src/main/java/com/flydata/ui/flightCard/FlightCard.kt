package com.flydata.ui.flightCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.flydata.data.AirportIdentification
import com.flydata.ui.mainScreen.MainScreenViewmodel
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

    val timeTables = TimeTables(
        TimeTable(
            flightUIState.time.scheduled.departure ?: 0,
            flightUIState.time.estimated.departure ?: 0
        ),
        TimeTable(
            flightUIState.time.scheduled.arrival ?: 0,
            flightUIState.time.estimated.arrival ?: 0
        )
    )

    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    ) {
        if (flightUIState.identification.id != "N/A") {
            Column(Modifier.padding(vertical = 12.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(Modifier.padding(horizontal = 6.dp)) {
                        Row {
                            Text(
                                flightUIState.identification.callsign,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight =
                                FontWeight.Bold
                            )
                            Text(
                                ", " + flightUIState.airline.short,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            "Type: ${flightUIState.aircraft.model.code}",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { mainScreenViewmodel.dismissFlight() }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Black
                        )
                    }
                }

                if (flightUIState.aircraft.images.medium.isNotEmpty()) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        model = flightUIState.aircraft.images.medium[0].src,
                        contentDescription = "Image of plane ${flightUIState.identification.id}"
                    )
                }

                Row {
                    AirportInfo(
                        false,
                        flightUIState.airport.origin ?: AirportIdentification(),
                        timeTables.origin,
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                    Divider()
                    AirportInfo(
                        true,
                        flightUIState.airport.destination ?: AirportIdentification(),
                        timeTables.destination,
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
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

@Composable
fun AirportInfo(
    isDestinationAirport: Boolean,
    airport: AirportIdentification,
    timeTable: TimeTable,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Column(
            Modifier
                .padding(5.dp)
        ) {
            Text(
                if (isDestinationAirport) "TIL" else "FRA",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
            )
            Text(
                airport.code.iata,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )

            val lastIndexOfSpace = airport.name.lastIndexOf(" ")
            Text(
                if (lastIndexOfSpace == -1) {
                    airport.name
                } else airport.name.substring(0, lastIndexOfSpace),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Time(
            true,
            SimpleDateFormat("HH:mm", Locale("no", "NO")).format(Date(timeTable.expected * 1000))
        )
        Time(
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
fun Time(isPlanned: Boolean, time: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp)
            .background(MaterialTheme.colorScheme.outline)
            .padding(5.dp),
        Arrangement.SpaceBetween
    ) {
        Text(
            if (isPlanned) "Planlagt" else "Forventet",
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(time, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun Divider() {
    Box(Modifier.width(6.dp))
}
