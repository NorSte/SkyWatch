package com.flydata

import android.location.Location
import com.flydata.data.flight.FlightDatasource
import com.flydata.data.flight.FlightList
import com.flydata.ui.mainScreen.MainScreenViewmodel
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class nearestFlightTest {

    @Mock
    lateinit var screenviewmodel: MainScreenViewmodel
    lateinit var flightdatasource: FlightDatasource

    @Mock
    lateinit var IFIlokasjon: Location

    @Before
    fun setUp() {
        // IFIlokasjon.set(flightLocation)
        IFIlokasjon.setMock(true)
        // IFIlokasjon.setLatitude(59.943)
        // IFIlokasjon.setLongitude(10.717)

        given(IFIlokasjon.getLatitude()).willReturn(59.943)
        given(IFIlokasjon.getLongitude()).willReturn(10.717)
        flightdatasource = FlightDatasource(screenviewmodel)
    }

    @Test
    fun normalflightlist() {

        val list1 = listOf(
            "304572fd", "461F56", "34.32", "20.38", "242",
            "43000", "489", "", "F-EKRN3", "A359", "OH-LWO",
            "1683985964", "HEL", "LHR", "AY1337", "0", "0", "FIN7WG", "0"
        )

        val list2 = listOf(
            "30456fb8", "346319", "54.22", "12.56", "44",
            "41025", "448", "", "F-EDDB2", "B752", "EC-NIV",
            "1683985966", "CGN", "HEL", "WT560", "0", "0", "SWT560P", "0"
        )

        val list3 = listOf(
            "30457198", "493284", "57.48", "14.95", "225",
            "40000", "434", "", "F-ESMX4", "C68A", "CS-LTD",
            "1683985966", "TMP", "CPH", "", "0", "0", "NJE755L", "0"
        )

        val list4 = listOf(
            "3044fa25", "47A7A8", "57.20", "16.18", "338",
            "40000", "459", "", "F-ESGP4", "B738", "LN-NGK",
            "1683985964", "LCA", "TRD", "DY8841", "0", "0", "NOZ8841", "0"
        )

        // dette er den nærmeste
        val list5 = listOf(
            "30451986", "4A91F8", "59.72", "10.86", "1",
            "39000", "432", "", "F-EKRD1", "A20N", "SE-DOX",
            "1683985962", "ALC", "BGO", "SK4728", "0", "0", "SAS4728", "0"
        )

        val list6 = listOf(
            "30456df0", "484EE4", "55.61", "8.17", "38",
            "39000", "434", "", "F-EKSN2", "B738", "PH-HSD",
            "1683985965", "AMS", "ARN", "KL1115", "0", "0", "KLM1115", "0"
        )

        val list7 = listOf(
            "304565ce", "45AC34", "67.00", "15.09", "22",
            "39000", "450", "", "F-ENBO3", "A320", "OY-KAT",
            "1683985966", "OSL", "TOS", "SK4418", "0", "64", "SAS54T", "0"
        )

        val list8 = listOf(
            "30455cb0", "4ACA06", "55.27", "10.22", "46",
            "39000", "434", "", "F-EKYT5", "B738", "SE-RPF",
            "1683985964", "LGW", "ARN", "D84456", "0", "0", "NSZ6LA", "0"
        )

        val dummyaircrafts: List<List<String>> =
            listOf(list1, list2, list3, list4, list5, list6, list7, list8)

        val expected = "30451986"
        val actual = flightdatasource.fetchNearestFlight(
            IFIlokasjon,
            FlightList(aircraft = dummyaircrafts)
        )
        Assert.assertEquals(expected, actual)
    }
}
