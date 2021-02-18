package com.nada.calculatortask.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.android.gms.location.*
import com.nada.calculatortask.R
import com.nada.calculatortask.adapters.MathQuestionAdapter
import com.nada.calculatortask.backgroundservice.CalculatorWorker
import com.nada.calculatortask.data.Operator
import com.nada.calculatortask.utils.Constants.Companion.KEY_EQUATION
import com.nada.calculatortask.utils.Constants.Companion.KEY_NUMBER_LIST
import com.nada.calculatortask.utils.Constants.Companion.KEY_OPERATOR
import com.nada.calculatortask.utils.Constants.Companion.KEY_RESULT
import com.nada.calculatortask.utils.Constants.Companion.PERMISSION_ID
import com.nada.calculatortask.utils.EquationSeparator
import com.nada.calculatortask.utils.EquationVerifier
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_basic_alert.view.*
import kotlinx.android.synthetic.main.layout_duration.*
import kotlinx.android.synthetic.main.layout_location_info.*
import kotlinx.android.synthetic.main.layout_panels.*
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private var operatorSign: Char = ' '
    lateinit var equation: String
    private lateinit var mathQuestionAdapter: MathQuestionAdapter
    private var enqueuedEquationsList: MutableList<String>
    private var succeededEquations: MutableList<String>
    private var numbersList: List<Int>

    // Location Variables
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    var lng: Double = 0.0
    var lat: Double = 0.0
    var city: String = " "
    var country: String = " "

    init {
        numbersList = listOf()
        enqueuedEquationsList = mutableListOf()
        succeededEquations = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpRecyclerViewsAndAdapters()
        button_calculate.setOnClickListener { onClickButtonCalculate() }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        switch_location.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                getCurrentLocation()
            } else {
                layout_location_info.visibility = View.GONE
            }
        }
    }

    private fun onClickButtonCalculate() {
        // get duration from edit text and if is empty the default will be Zero
        var duration: Long = 0
        if (edit_text_seconds.text.toString().isNotEmpty()) {
            duration = edit_text_seconds.text.toString().toLong()
        }

        // remove all white spaces
        equation = edit_text_equation.text.toString().filter { !it.isWhitespace() }

        // if the equation is valid - separate the equation
        // 1- get operator
        // 2- get numbers list
        // 3- call the background service
        // 4- add the enqueued equations to the list
        if (isValidEquation(equation)) {
            val operator = EquationSeparator.getOperator(operatorSign)
            if (operator != Operator.UNKNOWN) {
                numbersList = EquationSeparator.getNumbers(equation)
                setOneTimeRequest(numbersList, operator, duration)
                enqueuedEquationsList.add(equation)
                mathQuestionAdapter.notifyDataSetChanged()
            }
        }
    }

    /* This function is to send a request to the back ground service using WorkManager 
    * 1- prepare data that will be sent
    * 2- build the request with its parameters (delay - data - tag) 
    * 3- send the request and observe the result 
    * 4- once the task is done get the data from the background service and add it to the list 
    * */
    private fun setOneTimeRequest(numbersList: List<Int>, operator: Operator, duration: Long) {
        val workManager = WorkManager.getInstance(applicationContext)

        val data: Data = Data.Builder()
            .putString(KEY_EQUATION, equation)
            .putString(KEY_OPERATOR, operator.toString())
            .putIntArray(KEY_NUMBER_LIST, numbersList.toIntArray())
            .build()

        val mathQuestionRequest = OneTimeWorkRequest.Builder(CalculatorWorker::class.java)
            .setInitialDelay(duration, TimeUnit.SECONDS)
            .setInputData(data)
            .addTag(equation)
            .build()

        workManager.enqueue(mathQuestionRequest)
        workManager.getWorkInfoByIdLiveData(mathQuestionRequest.id)
            .observe(this, Observer {
                // SUCCEEDED RUNNING ENQUEUED STATE
                if (it.state.isFinished) {
                    val equation: String = it.outputData.getString(KEY_EQUATION).toString()
                    val result: Int = it.outputData.getInt(KEY_RESULT, 0)
                    val mathQuestion = "$equation =  $result"
                    succeededEquations.add(mathQuestion)
                    mathQuestionAdapter.notifyDataSetChanged()
                }
            })
    }

    // function to check validation and show alert dialogs
    public fun isValidEquation(equation: String): Boolean {
        var result = false
        if (equation.isNotEmpty()) {
            if (EquationVerifier.isValidContext(equation)) {
                operatorSign = EquationVerifier.isOneOperator(equation)
                if (operatorSign != '0') {
                    result = true
                } else {
                    showDialog(resources.getString(R.string.use_one_operator))
                }
            } else {
                showDialog(resources.getString(R.string.equation_not_valid))
            }
        } else {
            showDialog(resources.getString(R.string.enter_an_equation))
        }
        return result
    }

    private fun setUpRecyclerViewsAndAdapters() {
        // List of Enqueued Equations
        val enqueuedLayoutManager = LinearLayoutManager(applicationContext)
        mathQuestionAdapter = MathQuestionAdapter(enqueuedEquationsList)
        rv_enqueued_requests.layoutManager = enqueuedLayoutManager
        rv_enqueued_requests.itemAnimator = DefaultItemAnimator()
        rv_enqueued_requests.adapter = mathQuestionAdapter

        // List of Succeeded Equations
        val succeededLayoutManager = LinearLayoutManager(applicationContext)
        mathQuestionAdapter = MathQuestionAdapter(succeededEquations)
        rv_succeeded_requests.layoutManager = succeededLayoutManager
        rv_succeeded_requests.itemAnimator = DefaultItemAnimator()
        rv_succeeded_requests.adapter = mathQuestionAdapter
    }

    private fun showDialog(message: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_basic_alert, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
        val alertDialog = builder.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogView.dialog_button_ok.setOnClickListener {
            alertDialog.dismiss()
        }
        dialogView.dialog_text_alert.text = message
    }

    // Location Section

    // Permission Functions
    private fun checkPermissions(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            return true
        }
        return false
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("Debug", "onRequestPermissionsResult: Permission Granted")
            }

        }

    }

    // check if location service is enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        getNewLocation()
                    } else {
                        lng = location.longitude
                        lat = location.latitude
                        setLocationInfo(lat, lng)
                    }
                }
            } else {
                showDialog(resources.getString(R.string.enable_location_service))
                switch_location.isChecked = false
            }
        } else {
            requestPermission()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            lng = lastLocation.longitude
            lat = lastLocation.latitude
            setLocationInfo(lat, lng)
        }
    }

    private fun setLocationInfo(lat: Double, lng: Double) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, lng, 3)
        city = address.get(0).locality
        country = address.get(0).countryName

        location_info_text_lng.text = lng.toString()
        location_info_text_lat.text = lat.toString()
        location_info_text_country.text = country
        location_info_text_city.text = city

        layout_location_info.visibility = View.VISIBLE
    }
}
