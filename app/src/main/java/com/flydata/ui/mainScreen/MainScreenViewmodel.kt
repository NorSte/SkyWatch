package com.flydata.ui.mainScreen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flydata.data.airport.AirportDatasource
import com.flydata.data.flight.FlightDatasource
import com.flydata.data.weather.MetarDatasource
import com.flydata.data.weather.SigmetDatasource
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Viewmodel for [MainScreen].
 *
 * @constructor sørger for at standardverdi for lokasjon er Forskningsparken i Oslo.
 */
class MainScreenViewmodel : ViewModel() {
    var deviceLocation = Location("").apply {
        latitude = 59.943
        longitude = 10.717
    }

    val flightDatasource = FlightDatasource(this)
    val airportDatasource = AirportDatasource()
    val metarDatasource = MetarDatasource()
    private val sigmetDatasource = SigmetDatasource()

    private val _mainScreenUIState = MutableStateFlow(MainScreenUIState())
    val mainScreenUIState: StateFlow<MainScreenUIState> = _mainScreenUIState.asStateFlow()

    private var currentlyDisplayed by mutableStateOf(CurrentlyDisplayed.FLIGHT)
    private var displayedFlightIcao24 by mutableStateOf("")
    private var displayedAirportIata by mutableStateOf("")
    private var airportIdentificationRepository: MutableList<AirportIdentification> =
        mutableListOf()

    /**
     * Legger til flyplassidentifikasjon (kode og navn) slik at det senere kan brukes i
     * flyplass-kort.
     *
     * @param airportIdentification flyplassidentifikasjon av typen [AirportIdentification].
     */
    fun addIdentification(airportIdentification: AirportIdentification) {
        val potentialIdentification =
            airportIdentificationRepository.find { it.iata == airportIdentification.iata }
        if (potentialIdentification == null) {
            airportIdentificationRepository.add(airportIdentification)
        }
    }

    /**
     * Konverterer IATA-koder til ICAO24-koder
     *
     *  @param iata IATA-koden som skal konverteres.
     *  @return den konverterte ICAO24-koden.
     */
    private fun getIcaoFrom(iata: String): String {
        val potentialIdentification = airportIdentificationRepository.find { it.iata == iata }
        return potentialIdentification?.icao ?: "ENGM"
    }

    /**
     * Oppdaterer UI-tilstand basert på endringer gjort fra grensesnittet.
     */
    private fun updateUIState() {
        viewModelScope.launch(Dispatchers.Main) {
            _mainScreenUIState.update { currentState ->
                currentState.copy(
                    currentlyDisplayed = currentlyDisplayed,
                    displayedFlightIcao24 = displayedFlightIcao24,
                    displayedAirportIata = displayedAirportIata,
                    sigmetMessage = sigmetDatasource.getSigmet(),
                    displayedAirportIcao = getIcaoFrom(displayedAirportIata),
                )
            }
        }
    }

    /**
     * Oppdaterer hvilket fly som vises.
     *
     * @param icao24 koden til flyet som skal vises.
     */
    fun updateDisplayedFlight(icao24: String) {
        displayedFlightIcao24 = icao24
        updateUIState()
    }

    /**
     * Endrer visning til fly-visning.
     */
    fun displayFlight() {
        currentlyDisplayed = CurrentlyDisplayed.FLIGHT
        updateUIState()
    }

    /**
     * Oppdaterer hvilken flyplass som vises.
     *
     * @param iata koden til flyplassen som skal vises.
     */
    fun updateDisplayedAirport(iata: String) {
        displayedAirportIata = iata
        updateUIState()
    }

    /**
     * Endrer visning til flyplass-visning.
     */
    fun displayAirport() {
        currentlyDisplayed = CurrentlyDisplayed.AIRPORT
        updateUIState()
    }

    /**
     * Endrer visning til ingen (kun flykart).
     */
    fun dismissCard() {
        currentlyDisplayed = CurrentlyDisplayed.NONE
        updateUIState()
    }

    /**
     * Oppdaterer lokasjonen til brukeren.
     *
     * @param componentActivity componantActivity for å hente lokasjonen.
     * @return lokasjonen til brukeren.
     */
    fun updateLocation(componentActivity: ComponentActivity): Location {
        toggleLocationServicePrompt(componentActivity)

        // Lager en lokasjonsklient
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(componentActivity)
        val deviceLocation = Location("")
        val locationRequest = LocationRequest.Builder(20000).build()

        // Lager et objekt som kaller på lokasjonen
        val locationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    // Oppdaterer lokasjonen i UI-tilstanden
                    deviceLocation.latitude = location.latitude
                    deviceLocation.longitude = location.longitude
                }
            }
        }

        val locationNumber = 100

        // Sjekk om det er tilgang til lokasjonsdata, hvis ikke - etterspør tilgang
        if (ContextCompat.checkSelfPermission(
                componentActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Spør om tilgang til lokasjonsdata
            ActivityCompat.requestPermissions(
                componentActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationNumber
            )
        }

        // Klienten som kobler lokasjonsvariablene sammen og kaller på posisjonen
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )

        return deviceLocation
    }

    /**
     * Lager en varsling som etterspør lokasjon hvis lokasjon ikke er tilgjengelig.
     *
     * @param componentActivity componentActivity for å sjekke lokasjonstilgang og opprette
     * eventuell varsling.
     */
    private fun toggleLocationServicePrompt(componentActivity: ComponentActivity) {
        val locationManager = componentActivity
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Hvis bruker har skrudd av lokasjonstjenester, be bruker om å skru det på
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
}

/**
 * Dataklasse som inneholder flyplassidentifikasjon.
 *
 * @property iata IATA-koden til flyplassen.
 * @property icao ICAO24-koden til flyplassen.
 * @property name navnet til flyplassen.
 */
data class AirportIdentification(val iata: String, val icao: String, val name: String)
