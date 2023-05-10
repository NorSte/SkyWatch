package com.flydata

import com.flydata.data.airport.AirportDatasource
import org.junit.Assert
import org.junit.Test

class AirportNameTest {
    private val airportdatasource = AirportDatasource()

    @Test
    fun name_compressing() {
        val values = mapOf(
            "Gardermoen Osl.." to
                airportdatasource.airportNameShortner("Gardermoen Oslo Lufthavn"),
            "Paris charles .." to
                airportdatasource.airportNameShortner("Paris charles de gaulle airport"),
            "London Stanste.." to
                airportdatasource.airportNameShortner("London Stansted Airport")
        )
        values.forEach { entry ->
            Assert.assertEquals(entry.key, entry.value)
        }
    }

    @Test
    fun name_compressing_edge() {

        val actual = airportdatasource.airportNameShortner("Paris")
        Assert.assertEquals("Paris", actual)
    }

    @Test
    fun name_compressing_empty() {
        // London Stansted Airport
        val actual = airportdatasource.airportNameShortner("")
        Assert.assertEquals("", actual)
    }
}
