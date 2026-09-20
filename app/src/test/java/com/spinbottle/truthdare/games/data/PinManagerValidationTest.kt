package com.spinbottle.truthdare.games.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PinManagerValidationTest {

    @Test
    fun fourDigitsAreAccepted() {
        assertTrue(PinManager.isValidPin("0123"))
        assertTrue(PinManager.isValidPin("9876"))
    }

    @Test
    fun nonFourDigitOrNonNumericPinsAreRejected() {
        assertFalse(PinManager.isValidPin(""))
        assertFalse(PinManager.isValidPin("123"))
        assertFalse(PinManager.isValidPin("12345"))
        assertFalse(PinManager.isValidPin("12a4"))
        assertFalse(PinManager.isValidPin("１２３４"))
    }
}
