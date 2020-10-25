package com.nicoqueijo.android.currencyconverter.kotlin.util

import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.elementAfter
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.elementBefore
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.hasOnlyOneElement
import com.nicoqueijo.android.currencyconverter.kotlin.util.Utils.isNotLastElement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class UtilsTest {

    @Test
    fun listHasOnlyOneElement_emptyListShouldReturnFalse() {
        val list = listOf<Any>()
        val expected = false
        val actual = list.hasOnlyOneElement()
        assertEquals(expected, actual)
    }

    @Test
    fun listHasOnlyOneElement_listOfSizeOneShouldReturnTrue() {
        val list = listOf(Any())
        val expected = true
        val actual = list.hasOnlyOneElement()
        assertEquals(expected, actual)
    }

    @Test
    fun listHasOnlyOneElement_listOfSizeTwoShouldReturnFalse() {
        val list = listOf(Any(), Any())
        val expected = false
        val actual = list.hasOnlyOneElement()
        assertEquals(expected, actual)
    }

    @Test
    fun listIsNotLastElement_lastPositionShouldReturnFalse() {
        val list = listOf(Any(), Any(), Any())
        val expected = false
        val actual = list.isNotLastElement(2)
        assertEquals(expected, actual)
    }

    @Test
    fun listIsNotLastElement_nonLastPositionShouldReturnTrue() {
        val list = listOf(Any(), Any(), Any())
        val expected = true
        var actual = list.isNotLastElement(0)
        assertEquals(expected, actual)
        actual = list.isNotLastElement(1)
        assertEquals(expected, actual)
    }

    @Test
    fun listIsNotLastElement_positionForEmptyListShouldThrowException() {
        val list = listOf<Any>()
        assertThrows(IllegalArgumentException::class.java) {
            list.isNotLastElement(3)
        }
        assertThrows(IllegalArgumentException::class.java) {
            list.isNotLastElement(-3)
        }
    }

    @Test
    fun listIsNotLastElement_positionOutOfBoundShouldThrowException() {
        val list = listOf(Any(), Any(), Any())
        assertThrows(IllegalArgumentException::class.java) {
            list.isNotLastElement(3)
        }
        assertThrows(IllegalArgumentException::class.java) {
            list.isNotLastElement(-1)
        }
    }

    @Test
    fun listElementBefore_validPositionShouldReturnCorrectElement() {
        val list = listOf(1, 2, 3, 4)
        val expected = 3
        val actual = list.elementBefore(3)
        assertEquals(expected, actual)
    }

    @Test
    fun listElementBefore_invalidPositionShouldThrowException() {
        val list = listOf(1, 2, 3, 4)
        assertThrows(IllegalArgumentException::class.java) {
            list.elementBefore(0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            list.elementBefore(4)
        }
    }

    @Test
    fun listElementAfter_validPositionShouldReturnCorrectElement() {
        val list = listOf(1, 2, 3, 4)
        val expected = 3
        val actual = list.elementAfter(1)
        assertEquals(expected, actual)
    }

    @Test
    fun listElementAfter_invalidPositionShouldThrowException() {
        val list = listOf(1, 2, 3, 4)
        assertThrows(IllegalArgumentException::class.java) {
            list.elementAfter(3)
        }
        assertThrows(IllegalArgumentException::class.java) {
            list.elementAfter(-1)
        }
    }
}