package com.flydata

import com.flydata.data.airport.AirportFlightsXmlParser
import org.junit.Assert
import org.junit.Test

class ConvertTimeTest {

    private val airportFlightsXmlParser = AirportFlightsXmlParser()

    // tester sommertid - skal bli UTC + 2 timer
    @Test
    fun daylightSavingTime() {
        val values = mapOf(
            "10:23" to
                airportFlightsXmlParser.convertTime("2023-05-10T08:23:00Z"),
            "17:08" to
                airportFlightsXmlParser.convertTime("2022-07-10T15:08:00Z"),
            "01:54" to
                airportFlightsXmlParser.convertTime("2023-06-10T23:54:00Z"),
        )
        values.forEach { entry ->
            Assert.assertEquals(entry.key, entry.value)
        }
    }

    // tester normaltid ("vintertid") - skal bli UTC + 1 time
    @Test
    fun normalTime() {
        val values = mapOf(
            "10:23" to
                airportFlightsXmlParser.convertTime("2023-01-10T09:23:00Z"),
            "16:08" to
                airportFlightsXmlParser.convertTime("2022-12-10T15:08:00Z"),
            "00:54" to
                airportFlightsXmlParser.convertTime("2023-02-10T23:54:00Z"),
        )
        values.forEach { entry ->
            Assert.assertEquals(entry.key, entry.value)
        }
    }

    @Test
    fun edgeCases() {
        // sommertid startet 26. mars 2023 - tester før og etter på denne datoen
        val values = mapOf(
            "01:30" to
                airportFlightsXmlParser.convertTime("2023-03-26T00:30:00Z"),
            "06:30" to
                airportFlightsXmlParser.convertTime("2023-03-26T04:30:00Z"),
        )
        values.forEach { entry ->
            Assert.assertEquals(entry.key, entry.value)
        }
    }
}
