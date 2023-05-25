package com.flydata

import com.flydata.data.weather.MetarDatasource
import org.junit.Assert
import org.junit.Test

class WeatherTest {
    private val tafmetardatasource = MetarDatasource()

    @Test
    fun general_convert() {
        val values = mapOf(
            "4,6 m/s" to
                tafmetardatasource.metarDecoder("ESGG 061150Z 11009KT 080V150 CAVOK 11/M05 Q1026="),
            "5,1 m/s" to
                tafmetardatasource.metarDecoder("ENHF 060120Z 24010KT 27019KT="),
            "1,0 m/s" to
                tafmetardatasource.metarDecoder("ENGM 052250Z 36002KT CAVOK 02/M07 Q1028=")
        )
        values.forEach { entry ->
            Assert.assertEquals(entry.key, entry.value.wind)
        }
    }

    @Test
    fun convertion_returns_empty() {
        val actual = tafmetardatasource.metarDecoder("")
        Assert.assertEquals(".", actual.wind)
    }

    @Test
    fun convertion_returns_edge() {

        val actual = tafmetardatasource.metarDecoder("ENHF 060120Z 24022KT 2753019KT 2537019KT")
        Assert.assertEquals("11,2 m/s", actual.wind)

        val actual2 = tafmetardatasource.metarDecoder("ENHF 060120Z 24022K 2537019")
        Assert.assertEquals(".", actual2.wind)
    }
}
