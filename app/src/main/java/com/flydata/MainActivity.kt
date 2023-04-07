package com.flydata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.flydata.ui.theme.BackgroundColor
import com.flydata.ui.theme.FlightCardViewmodel
import com.flydata.ui.theme.FlydataTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            FlydataTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundColor
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text("SkyWatch", fontSize = 32.sp)
                            Text("Det nærmeste flyet er: ")
                        }
                        FlightCard()
                    }
                }
            }
        }
    }
}

data class TimeTable(val expected: String, val actual: String)

data class TimeTables(val origin: TimeTable, val destination: TimeTable)

// TODO: FlightCard tar f.eks. inn icao24, gjør API-uthentinger, bytt ut hardkodede verdier
@Composable
fun FlightCard(flightViewModel: FlightCardViewmodel = viewModel()) {
    val flightUIState by flightViewModel.flightCardUiState.collectAsState()

    val timeTables = TimeTables(
        TimeTable(flightUIState.departureScheduleTime, flightUIState.departureActualTime),
        TimeTable(flightUIState.destinationScheduleTime, flightUIState.destinationActualTime)
    )

    Column(
        Modifier.padding(bottom = 100.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("${flightUIState.distance} km", fontWeight = FontWeight.Bold)
        Text("${flightUIState.flightId}", color = Color.Gray)
        Text("787-9 Dreamliner")

        // TODO: Finn dynamisk måte å hente bilde. Alternativt fjern bildet
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            model = "https://flysmart24.no/wp-content/uploads/2021/12/dymaxern-777x437.jpg",
            contentDescription = "Image of a Norwegian plane"
        )

        Row {
            AirportInfo(
                false,
                flightUIState.departureAirport,
                "(flyplassnavn)",
                timeTables.origin,
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White)
            )
            Divider()
            AirportInfo(
                true,
                flightUIState.destinationAirport,
                "(flyplassnavn)",
                timeTables.destination,
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White)
            )
        }
    }
}

// TODO: AirportInfo tar inn identifikator, gjør API-uthentinger, fyller informasjon deretter
@Composable
fun AirportInfo(
    isDestinationAirport: Boolean,
    code: String,
    name: String,
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
            Text(code, fontWeight = FontWeight.Bold)
            Text(name)
        }
        Time(true, timeTable.expected)
        Time(false, timeTable.actual)
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
