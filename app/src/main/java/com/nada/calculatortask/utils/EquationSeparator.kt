package com.nada.calculatortask.utils

import com.nada.calculatortask.data.Operator
import com.nada.calculatortask.data.Operator.*

class EquationSeparator {

    companion object {
        // TODO
        fun getNumbers(equation: String): List<Int> {
            val numbersList: MutableList<Int> = mutableListOf()
            var number = ""
            for (i in equation.indices) {
                if (equation[i].isDigit()) {
                    number += equation[i]
                }
                if (!equation[i].isDigit() || i == equation.length - 1) {
                    numbersList.add(number.toInt())
                    number = ""
                }
            }
            return numbersList
        }

        fun getOperator(operatorSign: Char): Operator {
            var result: Operator = UNKNOWN
            if (operatorSign == '+') {
                result = ADD
            } else if (operatorSign == '-') {
                result = SUB
            } else if (operatorSign == '*') {
                result = MUL
            } else if (operatorSign == '/') {
                result = DIV
            }
            return result
        }
    }
}