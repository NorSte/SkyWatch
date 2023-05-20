package com.flydata

import androidx.compose.runtime.getValue
import com.flydata.data.airport.AirportDatasource
import com.flydata.data.airport.MetarDatasource
import com.flydata.data.airport.Weather
import com.flydata.ui.airportCard.AirportCardViewmodel
import com.flydata.ui.airportCard.TypeOfListing
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ChangeTypeListingTest {

    @Mock
    private lateinit var airportdatasource: AirportDatasource
    @Mock
    private lateinit var tafmetardatasource: MetarDatasource

    @Mock
    lateinit var airportviewmodel: AirportCardViewmodel

    // må omskrive disse metodene, de ligger i init metoden til viewmodel
    /*
    airportDatasource.fetchAirportFlights(iata,airportCardUIState.value.typeOfListing),
    airportWeather = tafmetardatasource.getTafmetar(icao)
     */

    @Before
    fun setUp() {
    }

    @Test
    fun typelisting_test() = runTest {
        given(airportdatasource.fetchAirportFlights("", TypeOfListing.DEPARTURE))
            .willReturn(arrayListOf())

        given(tafmetardatasource.getTafmetar("")).willReturn(Weather(".", "."))

        advanceUntilIdle()

        var airportviewmodel = AirportCardViewmodel(
            airportdatasource,
            tafmetardatasource = tafmetardatasource,
            icao = ""
        )

        val airportUIState = airportviewmodel.airportCardUIState.value

        advanceUntilIdle()
        Assert.assertEquals(TypeOfListing.DEPARTURE, airportUIState.typeOfListing)
    }
}
