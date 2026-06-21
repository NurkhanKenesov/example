package com.example.myapplication

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyFields_loginButtonIsDisabled() {
        composeTestRule.setContent {
            LoginScreen()
        }

        // Email and password start empty, so login button should be disabled
        composeTestRule.onNodeWithTag("login_button").assertIsNotEnabled()
    }

    @Test
    fun invalidEmail_showsValidationError() {
        composeTestRule.setContent {
            LoginScreen()
        }

        // Input an invalid email without '@'
        composeTestRule.onNodeWithTag("email_field").performTextInput("invalid-email")
        
        // Assert that the email error message is displayed
        composeTestRule.onNodeWithTag("email_error_text").assertIsDisplayed()
        
        // Login button should still be disabled
        composeTestRule.onNodeWithTag("login_button").assertIsNotEnabled()
    }

    @Test
    fun validMockData_showsLoadingThenSuccess() {
        var isSuccessCalled = false

        composeTestRule.setContent {
            LoginScreen(
                onLoginSuccess = { _, _ -> isSuccessCalled = true }
            )
        }

        // Input correct credentials
        composeTestRule.onNodeWithTag("email_field").performTextInput("test@test.com")
        composeTestRule.onNodeWithTag("password_field").performTextInput("123456")

        // Button should be enabled now
        composeTestRule.onNodeWithTag("login_button").assertIsEnabled()

        // Perform click
        composeTestRule.onNodeWithTag("login_button").performClick()

        // Assert that the loading indicator is displayed (since we simulate loading state)
        composeTestRule.onNodeWithTag("loading_indicator").assertIsDisplayed()

        // Advance time or wait for the callback to trigger (since delay is 800ms)
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            isSuccessCalled
        }

        assertTrue(isSuccessCalled)
    }

    @Test
    fun incorrectMockData_showsError() {
        composeTestRule.setContent {
            LoginScreen()
        }

        // Input incorrect credentials
        composeTestRule.onNodeWithTag("email_field").performTextInput("wrong@test.com")
        composeTestRule.onNodeWithTag("password_field").performTextInput("wrongpass")

        // Button should be enabled
        composeTestRule.onNodeWithTag("login_button").performClick()

        // Wait for error text to be displayed
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            try {
                composeTestRule.onNodeWithTag("general_error_text").assertIsDisplayed()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        composeTestRule.onNodeWithTag("general_error_text").assertIsDisplayed()
    }
}
