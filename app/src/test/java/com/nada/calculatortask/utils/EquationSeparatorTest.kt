package com.nada.calculatortask.utils

import com.nada.calculatortask.data.Operator
import org.junit.Assert.*
import org.junit.Test


class EquationSeparatorTest {

    @Test
    fun validateSumGetNumbers() {
        val result = EquationSeparator.getNumbers("1+234+567")
        assertArrayEquals(result.toIntArray(), intArrayOf(1, 234, 567))
    }

    @Test
    fun validateSubGetNumbers() {
        val result = EquationSeparator.getNumbers("1-234-3-5-2")
        assertArrayEquals(result.toIntArray(), intArrayOf(1, 234, 3, 5, 2))
    }

    @Test
    fun validateMulGetNumbers() {
        val result = EquationSeparator.getNumbers("20*20")
        assertArrayEquals(result.toIntArray(), intArrayOf(20, 20))
    }

    @Test
    fun validateDivgetNumbers() {
        val result = EquationSeparator.getNumbers("9/3/1")
        assertArrayEquals(result.toIntArray(), intArrayOf(9, 3, 1))
    }

    @Test
    fun validateAddOperator() {
        val result = EquationSeparator.getOperator('+')
        assertSame(result, Operator.ADD)
    }

    @Test
    fun validateSubOperator() {
        val result = EquationSeparator.getOperator('-')
        assertSame(result, Operator.SUB)
    }

    @Test
    fun validateMulOperator() {
        val result = EquationSeparator.getOperator('*')
        assertSame(result, Operator.MUL)
    }

    @Test
    fun validateDIVOperator() {
        val result = EquationSeparator.getOperator('/')
        assertSame(result, Operator.DIV)
    }

    @Test
    fun validateUnknownOperator() {
        val result = EquationSeparator.getOperator('%')
        assertSame(result, Operator.UNKNOWN)
    }
}