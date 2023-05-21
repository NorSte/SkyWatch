package com.flydata

import com.flydata.data.airport.AirportDatasource
import com.flydata.data.airport.MetarDatasource
import com.flydata.data.airport.Weather
import com.flydata.ui.airportCard.AirportCardViewmodel
import com.flydata.ui.airportCard.TypeOfListing
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @Before
    fun setUp() {
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun typelisting_test() = runTest {
        given(airportdatasource.fetchAirportFlights("", TypeOfListing.DEPARTURE))
            .willReturn(arrayListOf())

        given(tafmetardatasource.getTafmetar("")).willReturn(Weather(".", "."))

        advanceUntilIdle()

        val airportviewmodel = AirportCardViewmodel(
            airportdatasource,
            tafmetardatasource = tafmetardatasource,
            icao = ""
        )

        val airportUIState = airportviewmodel.airportCardUIState.value

        advanceUntilIdle()
        Assert.assertEquals(TypeOfListing.DEPARTURE, airportUIState.typeOfListing)
    }
}
