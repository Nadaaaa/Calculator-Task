package com.nada.calculatortask.utils

class BasicOperations {
    companion object{
        fun sumArray(numbersArray: IntArray): Int {
            for (i in 1 until numbersArray.size) {
                numbersArray[0] += numbersArray[i]
            }
            return numbersArray[0]
        }

        fun subtractArray(numbersArray: IntArray): Int {
            for (i in 1 until numbersArray.size) {
                numbersArray[0] -= numbersArray[i]
            }
            return numbersArray[0]
        }

        fun multiplyArray(numbersArray: IntArray): Int {
            for (i in 1 until numbersArray.size) {
                numbersArray[0] *= numbersArray[i]
            }
            return numbersArray[0]
        }

        fun divideArray(numbersArray: IntArray): Int {
            for (i in 1 until numbersArray.size) {
                numbersArray[0] /= numbersArray[i]
            }
            return numbersArray[0]
        }
    }
}