package com.flydata.ui.airportCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flydata.data.airport.AirportFlight
import com.flydata.ui.mainScreen.MainScreenViewmodel

@Composable
fun AirportCard(
    mainScreenViewmodel: MainScreenViewmodel
) {
    val airportViewmodel by remember {
        mutableStateOf(
            AirportCardViewmodel(
                mainScreenViewmodel.airportDatasource,
                mainScreenViewmodel.mainScreenUIState.value.displayedAirportIata
            )
        )
    }
    val airportUIState by airportViewmodel.airportScreenUiState.collectAsState()

    Card(
        Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        if (airportUIState.airportCode != "") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = { mainScreenViewmodel.displayFlight() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
                IconButton(onClick = { mainScreenViewmodel.dismissCard() }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Black
                    )
                }
            }
            Row {
                Text(airportUIState.airportCode, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                Text(
                    ", " + airportUIState.airportName,
                    fontSize = 32.sp,
                    color = Color.Gray
                )
            }
            FlightTable(tablelist = airportUIState.airportFlights)
        } else {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                Arrangement.Center
            ) {
                Text("Laster inn flyplass-data ...")
            }
        }
    }
}

@Composable
fun FlightTable(tablelist: List<AirportFlight>) {
    val mediumColumnWeight = 3f
    val minColumnWeight = 2f
    Column(
        Modifier
            .fillMaxSize()
    ) {
        if (tablelist.isEmpty()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Ingen flydata tilgjengelig for denne flyplassen")
            }
        } else {
            Row(
                Modifier
                    .background(MaterialTheme.colorScheme.inversePrimary)
                    .padding(6.dp)
            ) {
                Text("Til", Modifier.weight(minColumnWeight), fontWeight = FontWeight.Bold)
                Text("Tid", Modifier.weight(mediumColumnWeight), fontWeight = FontWeight.Bold)
                Text("Flight", Modifier.weight(mediumColumnWeight), fontWeight = FontWeight.Bold)
                Text("Status", Modifier.weight(mediumColumnWeight), fontWeight = FontWeight.Bold)
            }

            LazyColumn(Modifier.padding(6.dp)) {
                items(tablelist) { flight ->
                    Row(Modifier.fillMaxWidth()) {
                        Text(flight.airport, Modifier.weight(minColumnWeight))
                        Column(Modifier.weight(mediumColumnWeight)) {
                            Text(flight.scheduleTime, fontWeight = FontWeight.Bold)
                            Text(flight.statusTime)
                        }
                        Text(
                            flight.flightId,
                            Modifier.weight(mediumColumnWeight),
                            fontWeight = FontWeight.Bold
                        )
                        Text(flight.statusText, Modifier.weight(mediumColumnWeight))
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outline)
                    )
                }
            }
        }
    }
}
