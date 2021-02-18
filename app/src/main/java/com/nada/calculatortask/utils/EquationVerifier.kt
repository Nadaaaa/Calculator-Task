package com.nada.calculatortask.utils

import java.util.regex.Pattern

class EquationVerifier {

    companion object {
        fun isOneOperator(equation: String): Char {
            var operator: Char = ' '
            var result: Char = ' '

            for (i in equation.indices) {
                if (!equation[i].isDigit()) {
                    if (operator == ' ') {
                        operator = equation[i]
                        result = operator
                    } else {
                        if (equation[i] != operator) {
                            result = '0'
                        } else
                            result = operator
                    }
                }
            }
            return result
        }


        fun isValidContext(equation: String): Boolean {
            val numbers: Pattern = Pattern.compile("[0-9]")
            val operators: Pattern = Pattern.compile("[*+-/]")
            val chars: Pattern = Pattern.compile("[a-zA-z]")
            val specialChars: Pattern = Pattern.compile("[!@#\$%&()_=|<>?{}\\[\\]~]")

            var doubledOperation = true
            for (i in 0 until equation.length - 2) {
                if (!equation[i].isDigit() && !equation[i + 1].isDigit()) {
                    doubledOperation = false
                }

            }

            return numbers.matcher(equation).find() && operators.matcher(equation).find()
                    && !chars.matcher(equation).find() && !specialChars.matcher(equation).find()
                    && equation[0].isDigit() && equation[equation.length - 1].isDigit() && doubledOperation
        }
    }
}