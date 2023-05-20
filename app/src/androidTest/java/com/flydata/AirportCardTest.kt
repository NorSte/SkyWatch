package com.flydata

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flydata.ui.airportCard.AirportCard
import com.flydata.ui.airportCard.AirportCardViewmodel
import com.flydata.ui.mainScreen.MainScreen
import com.flydata.ui.mainScreen.MainScreenViewmodel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AirportCardTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun ShowAirportWeather()
    {
        rule.setContent{ AirportCard(mainScreenViewmodel = MainScreenViewmodel() ) }

        rule.onNodeWithContentDescription("arrow").assertExists()

    }

}