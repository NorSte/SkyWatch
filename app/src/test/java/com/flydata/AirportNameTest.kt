package com.flydata

import com.flydata.data.airport.AirportDatasource
import org.junit.Assert
import org.junit.Test

class AirportNameTest {
    val airportdatasource = AirportDatasource()

    @Test
    fun Name_compressing() {
        val values = mapOf(
            "Gardermoen Osl.." to
                airportdatasource.AirportNameShortner("Gardermoen Oslo Lufthavn"),
            "Paris charles .." to
                airportdatasource.AirportNameShortner("Paris charles de gaulle airport"),
            "London Stanste.." to
                airportdatasource.AirportNameShortner("London Stansted Airport")
        )
        values.forEach { entry ->
            Assert.assertEquals(entry.key, entry.value)
        }
    }

    @Test
    fun Name_compressing_edge() {

        val actual = airportdatasource.AirportNameShortner("Paris")
        Assert.assertEquals("Paris", actual)
    }

    @Test
    fun Name_compressing_empty() {
        // London Stansted Airport
        val actual = airportdatasource.AirportNameShortner("")
        Assert.assertEquals("", actual)
    }
}
