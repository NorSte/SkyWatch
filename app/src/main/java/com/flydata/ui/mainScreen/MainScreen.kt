package com.flydata.ui.mainScreen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.flydata.R
import com.flydata.ui.airportCard.AirportCard
import com.flydata.ui.flightCard.FlightCard
import com.flydata.ui.flightMap.FlightMap
import com.flydata.ui.theme.md_theme_light_onPrimary
import com.flydata.ui.theme.md_theme_light_onPrimaryContainer
import com.flydata.ui.theme.md_theme_light_primary
import com.flydata.ui.theme.md_theme_light_primaryContainer
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@Composable
fun MainScreen(compActivity: ComponentActivity) {
    ComposableLifecycle { _, event ->
        val tag = "MAINSCREEN"
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                Log.d(tag, "MainScreen.ON_CREATE")
            }
            Lifecycle.Event.ON_START -> {
                Log.d(tag, "MainScreen.ON_START")
            }
            Lifecycle.Event.ON_RESUME -> {
                Log.d(tag, "MainScreen.ON_RESUME")
            }
            Lifecycle.Event.ON_PAUSE -> {
                Log.d(tag, "MainScreen.ON_PAUSE")
            }
            Lifecycle.Event.ON_STOP -> {
                Log.d(tag, "MainScreen.ON_STOP")
            }
            Lifecycle.Event.ON_DESTROY -> {
                Log.d(tag, "MainScreen.ON_DESTROY")
            }
            else -> {}
        }
    }

    val mainScreenViewmodel by remember { mutableStateOf(MainScreenViewmodel()) }
    val mainScreenUIState by mainScreenViewmodel.mainScreenUIState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    mainScreenViewmodel.updateLocation(getLocation(compActivity))

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
                    text = "SkyWatch",
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
                    containerColor = md_theme_light_primaryContainer
                )
            ) {
                Text(
                    "Nærmeste?",
                    fontSize = 12.sp,
                    color = md_theme_light_onPrimaryContainer
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

fun getLocation(componentActivity: ComponentActivity): Location {
    locationServicePrompt(componentActivity)

    // Lager en lokasjonsklient
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(componentActivity)

    val deviceLocation = Location("")

    // forespørsel på lokasjonen går hvert intervall
    val locationRequest = LocationRequest.Builder(20000).build()

    // Lager et objekt som kaller på lokasjonen
    val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                // Sende lokasjon til uiState,
                deviceLocation.latitude = location.latitude
                deviceLocation.longitude = location.longitude
            }
        }
    }

    val locationNumber = 100
    // sjekker om det er tilgang til lokasjonen, hvis ikke spør den om tilgang
    if (ContextCompat.checkSelfPermission(
            componentActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Spør om tilgang
        ActivityCompat.requestPermissions(
            componentActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationNumber
        )
    }

    // klienten som kobler alt sammen og kaller på posisjon
    fusedLocationClient.requestLocationUpdates(
        locationRequest, locationCallback, Looper.getMainLooper()
    )

    return deviceLocation
}

fun locationServicePrompt(componentActivity: ComponentActivity) {
    // Hvis bruker har skrudd av lokasjonstjenester så ber den bruker å skru det på igjen
    val locationManager = componentActivity
        .getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        // Ikke tilgang på lokasjon, lager en melding
        android.app.AlertDialog.Builder(componentActivity)
            .setTitle("Lokasjonstjenester er ikke aktive")
            .setMessage("Vennligst skru på lokasjonstjenester og GPS")
            .setPositiveButton("OK") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                componentActivity.startActivity(intent)
            }
            .setNegativeButton("Avbryt") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}

@Composable
fun ComposableLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
