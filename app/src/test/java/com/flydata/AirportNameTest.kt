package com.flydata

import com.flydata.data.airport.AirportDatasource
import org.junit.Assert
import org.junit.Test

class AirportNameTest {
    val airportdatasource = AirportDatasource()

    @Test
    fun Name_compressing_Osl() {

        val actual = airportdatasource.AirportNameShortner("Gardermoen Oslo Lufthavn")
        Assert.assertEquals("Gardermoen Osl..", actual)
    }

    @Test
    fun Name_compressing_charles() {
        //paris charles de gaulle airport
        val actual = airportdatasource.AirportNameShortner("paris charles de gaulle airport")
        Assert.assertEquals("paris charles ..", actual)
    }

    @Test
    fun Name_compressing_london() {
        //London Stansted Airport
        val actual = airportdatasource.AirportNameShortner("London Stansted Airport")
        Assert.assertEquals("London Stanste..", actual)
    }


}