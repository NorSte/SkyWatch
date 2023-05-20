package com.flydata

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.flydata.ui.mainScreen.MainScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class NosigmetTest {

    @get:Rule
    val rule = createComposeRule()


    @Test
    fun click_sigmet()
    {
        rule.setContent{ MainScreen() }

        //trykker på værmeldingen
        rule.onNodeWithText("Værtrusler").performClick()

        rule.onNodeWithText("Ingen trusler nå").assertExists()

    }
}