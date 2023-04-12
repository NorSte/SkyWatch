package com.flydata.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.flydata.data.AirportIdentification
import java.text.SimpleDateFormat
import java.util.*

data class TimeTable(val expected: Long, val actual: Long)
data class TimeTables(val origin: TimeTable, val destination: TimeTable)

@Composable
fun FlightCard() {
    val flightViewModel by remember { mutableStateOf(FlightCardViewmodel()) }
    val flightUIState by flightViewModel.flightCardUiState.collectAsState()

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

    if (flightUIState.identification.id != "N/A") {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("SkyWatch", fontSize = 32.sp)
            Text("Det nærmeste (${String.format("%.1f", flightUIState.distance)}km) flyet er: ")
        }
        Column(
            Modifier.padding(bottom = 100.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Row {
                Text(flightUIState.identification.callsign, fontWeight = FontWeight.Bold)
                Text(flightUIState.airline.short, color = Color.Gray)
            }
            Text("Type: ${flightUIState.aircraft.model.code}", color = Color.Gray)

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
                        .background(Color.White)
                )
                Divider()
                AirportInfo(
                    true,
                    flightUIState.airport.destination ?: AirportIdentification(),
                    timeTables.destination,
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White)
                )
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
                fontSize = 10.sp,
            )
            Text(airport.code.iata, fontWeight = FontWeight.Bold)

            val lastIndexOfSpace = airport.name.lastIndexOf(" ")
            Text(
                if (lastIndexOfSpace == -1) {
                    airport.name
                } else airport.name.substring(0, lastIndexOfSpace)
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
            .background(Color(0XFFEBEBEB))
            .padding(5.dp),
        Arrangement.SpaceBetween
    ) {
        Text(if (isPlanned) "Planlagt" else "Forventet")
        Text(time, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun Divider() {
    Box(Modifier.width(6.dp))
}
