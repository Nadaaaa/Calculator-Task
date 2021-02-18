package com.nada.calculatortask.backgroundservice

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.workDataOf
import com.nada.calculatortask.data.Operator
import com.nada.calculatortask.utils.Constants
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CalculatorWorkerTest {
    @get:Rule
    var wmRule = WorkManagerTestRule()

    @Test
    fun testWorkManager() {
        // Define input data
        val inputData = workDataOf(
            Constants.KEY_EQUATION to "1+2+3",
            Constants.KEY_OPERATOR to Operator.ADD.toString(),
            Constants.KEY_NUMBER_LIST to intArrayOf(1, 2, 3)
        )
        // Create request
        val request = OneTimeWorkRequest.Builder(CalculatorWorker::class.java)
            .setInputData(inputData)
            .build()

        // Enqueue and wait for result. This also runs the Worker synchronously
        // because we are using a SynchronousExecutor.
        wmRule.workManager.enqueue(request).result.get()
        // Get WorkInfo
        val workInfo = wmRule.workManager.getWorkInfoById(request.id).get()
        val output = workInfo.outputData.getInt(Constants.KEY_RESULT,0)

        // Assert
        assertEquals(output, 6)
        assertEquals(workInfo.state, (WorkInfo.State.SUCCEEDED))
    }
}