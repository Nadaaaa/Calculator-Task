package com.nada.calculatortask.utils

import org.junit.Assert.*
import org.junit.Test

class EquationVerifierTest {

    @Test
    fun validateIsOneOperator_1() {
        val result = EquationVerifier.isOneOperator("1+23")
        assertEquals(result, '+')
    }

    @Test
    fun validateIsOneOperator_2() {
        val result = EquationVerifier.isOneOperator("1-45-7-8")
        assertEquals(result, '-')
    }

    @Test
    fun validateIsOneOperator_3() {
        val result = EquationVerifier.isOneOperator("1*23*45")
        assertEquals(result, '*')
    }

    @Test
    fun validateIsOneOperator_4() {
        val result = EquationVerifier.isOneOperator("1/2")
        assertEquals(result, '/')
    }

    @Test
    fun validateIsOneOperator_5() {
        val result = EquationVerifier.isOneOperator("1*23+45")
        assertEquals(result, '0')
    }

    @Test
    fun validateIsOneOperator_6() {
        val result = EquationVerifier.isOneOperator("1-23/45+7*8")
        assertEquals(result, '0')
    }

    @Test
    fun validateIsOneOperator_7() {
        val result = EquationVerifier.isOneOperator("14-7*72")
        assertEquals(result, '0')
    }


    @Test
    fun validateEquationContext_1() {
        val result = EquationVerifier.isValidContext("1+2+3")
        assertEquals(result, true)
    }

    @Test
    fun validateEquationContext_2() {
        val result = EquationVerifier.isValidContext("12-3")
        assertEquals(result, true)
    }

    @Test
    fun validateEquationContext_3() {
        val result = EquationVerifier.isValidContext("7*12*1234")
        assertEquals(result, true)
    }

    @Test
    fun validateEquationContext_4() {
        val result = EquationVerifier.isValidContext("1/3/2")
        assertEquals(result, true)
    }

    @Test
    fun validateEquationContext_5() {
        val result = EquationVerifier.isValidContext("1+ao0")
        assertEquals(result, false)
    }

    @Test
    fun validateEquationContext_6() {
        val result = EquationVerifier.isValidContext("+2+3")
        assertEquals(result, false)
    }

    @Test
    fun validateEquationContext_7() {
        val result = EquationVerifier.isValidContext("1+2+")
        assertEquals(result, false)
    }

    @Test
    fun validateEquationContext_8() {
        val result = EquationVerifier.isValidContext("1++2")
        assertEquals(result, false)
    }

    @Test
    fun validateEquationContext_9() {
        val result = EquationVerifier.isValidContext("1+2#@#$@$")
        assertEquals(result, false)
    }

    @Test
    fun validateEquationContext_10() {
        val result = EquationVerifier.isValidContext("SomeText")
        assertEquals(result, false)
    }

    @Test
    fun validateEquationContext_11() {
        val result = EquationVerifier.isValidContext("1+2=3")
        assertEquals(result, false)
    }
}