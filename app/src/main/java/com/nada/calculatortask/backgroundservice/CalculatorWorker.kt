package com.nada.calculatortask.backgroundservice

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.nada.calculatortask.data.Operator
import com.nada.calculatortask.utils.BasicOperations
import com.nada.calculatortask.utils.Constants.Companion.KEY_EQUATION
import com.nada.calculatortask.utils.Constants.Companion.KEY_NUMBER_LIST
import com.nada.calculatortask.utils.Constants.Companion.KEY_OPERATOR
import com.nada.calculatortask.utils.Constants.Companion.KEY_RESULT
import java.lang.Exception

class CalculatorWorker(context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {

    override fun doWork(): Result {
        try {
            // Receive data sent by math equation request from main activity
            val equation: String = inputData.getString(KEY_EQUATION).toString()

            val operator: String = inputData.getString(KEY_OPERATOR).toString()

            val numbersArray: IntArray? = inputData.getIntArray(KEY_NUMBER_LIST)

            // Send the data to the function to get the answers
            var result = 0
            if (numbersArray != null) {
                result = getResult(numbersArray, operator)
            }

            // Data Builder with the Math Question answer
            val outPutData = Data.Builder()
                .putString(KEY_EQUATION, equation)
                .putInt(KEY_RESULT, result)
                .build()

            // This is where you send the answer to be presented
            return Result.success(outPutData)
        } catch (e: Exception) {
            return Result.failure()
        }
    }

    private fun getResult(numbersArray: IntArray, operator: String): Int {
        var result = 0
        if (operator == Operator.ADD.toString()) {
            result = BasicOperations.sumArray(numbersArray)
        } else if (operator == Operator.SUB.toString()) {
            result = BasicOperations.subtractArray(numbersArray)
        } else if (operator == Operator.MUL.toString()) {
            result = BasicOperations.multiplyArray(numbersArray)
        } else if (operator == Operator.DIV.toString()) {
            result = BasicOperations.divideArray(numbersArray)
        }
        return result
    }
}