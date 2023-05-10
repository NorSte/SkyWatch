package com.flydata

import com.flydata.data.airport.MetarDatasource
import org.junit.Assert
import org.junit.Test

class WeatherTest {
    val tafmetardatasource = MetarDatasource()

    @Test
    fun convertion_returns1_360() {
        val actual = tafmetardatasource.Metardecoder(
            "" +
                "ENGM 052250Z 36002KT CAVOK 02/M07 Q1028="
        )
        Assert.assertEquals("1,0 m/s", actual.wind)
        Assert.assertEquals("360", actual.direction)
    }

    @Test
    fun convertion_returns51_240() {
        val actual = tafmetardatasource.Metardecoder(
            "ENHF 060120Z 24010KT 220V290" +
                " 9999 FEW012/// SCT016/// BKN022/// 02/00 Q1025 RMK WIND 1254FT 27019KT="
        )
        Assert.assertEquals("5,1 m/s", actual.wind)
        Assert.assertEquals("240", actual.direction)
    }

    @Test
    fun convertion_returns46_110() {

        val actual = tafmetardatasource.Metardecoder(
            "ESGG 061150Z 11009KT 080V150 CAVOK 11/M05 Q1026="
        )
        Assert.assertEquals("4,6 m/s", actual.wind)
        Assert.assertEquals("110", actual.direction)
    }
}
