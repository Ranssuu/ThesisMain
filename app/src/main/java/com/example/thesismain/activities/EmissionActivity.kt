package com.example.thesismain.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.thesismain.R
import com.example.thesismain.api.ApiClient
import com.example.thesismain.api.ApiService
import java.util.TimeZone
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class EmissionActivity : AppCompatActivity() {

    private val tag = "EmissionActivity"
    private val deviceName = "ESP32SmokeDetector"
    private val myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null

    private lateinit var progressText: TextView
    private lateinit var statusTextView: TextView
    private lateinit var resultTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var startTestButton: Button

    private val REQUEST_BLUETOOTH_PERMISSIONS = 1
    private var isConnected: Boolean = false
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emission)

        // Initialize UI components
        progressText = findViewById(R.id.progress_text)
        statusTextView = findViewById(R.id.status)
        resultTextView = findViewById(R.id.result_text)
        progressBar = findViewById(R.id.progress_bar)
        startTestButton = findViewById(R.id.starttest)

        startTestButton.setOnClickListener {
            if (isConnected) {
                sendStartCommand()
                startTestButton.text = getString(R.string.testing)
                startTestButton.setBackgroundColor(ContextCompat.getColor(this, R.color.failedColor))
                handler?.postDelayed({
                    // Use listenForData() instead of receiveData()
                    listenForData()
                    sendDataToBackend()
                    startTestButton.text = getString(R.string.start_test)
                    startTestButton.setBackgroundColor(ContextCompat.getColor(this, R.color.Red))
                }, 20000) // 20 seconds delay
            } else {
                Toast.makeText(this, "Device not connected", Toast.LENGTH_SHORT).show()
            }
        }

        handler = Handler(Looper.getMainLooper())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions()) {
                requestPermissions()
            } else {
                setupBluetooth()
            }
        } else {
            setupBluetooth()
        }
    }

    private fun hasPermissions(): Boolean {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN
            )
        }

        ActivityCompat.requestPermissions(this, permissions, REQUEST_BLUETOOTH_PERMISSIONS)
    }

    private fun setupBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.e(tag, "Bluetooth not supported")
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show()
            return
        }

        if (bluetoothAdapter?.isEnabled == false) {
            // Request the user to enable Bluetooth
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_PERMISSIONS)
        } else {
            val device = findDeviceByName(deviceName)
            if (device != null) {
                connectToDevice(device)
            } else {
                Log.e(tag, "Device not found")
                Toast.makeText(this, "Device not found", Toast.LENGTH_SHORT).show()
                updateConnectionStatus("Disconnected")
            }
        }
    }

    private fun findDeviceByName(name: String): BluetoothDevice? {
        return bluetoothAdapter?.bondedDevices?.firstOrNull { it.name == name }
    }

    private fun connectToDevice(device: BluetoothDevice) {
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID)
            bluetoothSocket?.connect()
            inputStream = bluetoothSocket?.inputStream
            isConnected = true
            updateConnectionStatus("Connected")
            // Listen for data in a separate thread
            listenForData()
        } catch (e: Exception) {
            Log.e(tag, "Connection failed", e)
            isConnected = false
            updateConnectionStatus("Disconnected")
            Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun listenForData() {
        Thread {
            val buffer = ByteArray(1024)
            var bytes: Int

            while (isConnected) {
                try {
                    bytes = inputStream?.read(buffer) ?: break
                    val received = String(buffer, 0, bytes).trim()
                    val opacity = received.toFloatOrNull()
                    if (opacity != null) {
                        runOnUiThread {
                            updateUIWithOpacity(opacity)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(tag, "Error reading data", e)
                    break
                }
            }
        }.start()
    }

    private fun sendStartCommand() {
        try {
            bluetoothSocket?.outputStream?.write("START\n".toByteArray())
            Toast.makeText(this, "Start command sent", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(tag, "Failed to send start command", e)
            Toast.makeText(this, "Failed to send start command", Toast.LENGTH_SHORT).show()
        }
    }

    private var currentOpacity: Float = 0.0f // Class-level variable to store opacity

    private fun updateUIWithOpacity(opacity: Float) {
        // Update the class-level opacity variable
        currentOpacity = opacity

        // Set the progress bar progress
        val progress = ((opacity / 3.0) * 100).toInt()
        progressBar.progress = progress

        // Set the progress text as percentage
        progressText.text = "$progress%"

        // Set the opacity text with the actual opacity value
        findViewById<TextView>(R.id.opacity_text).text = String.format("%.2f", opacity)

        // Update the result based on the opacity value
        val resultTextView = findViewById<TextView>(R.id.result_text)
        if (opacity < 2.4) { // Assuming 0.20 is the pass threshold
            progressBar.progressDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.passedColor),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            resultTextView.text = "Passed"
            resultTextView.setTextColor(ContextCompat.getColor(this, R.color.passedColor))
        } else {
            progressBar.progressDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.failedColor),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
            resultTextView.text = "Failed"
            resultTextView.setTextColor(ContextCompat.getColor(this, R.color.failedColor))
        }
    }

    private fun updateConnectionStatus(status: String) {
        statusTextView.text = status
    }

    private fun sendDataToBackend() {
        val sharedPref = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val token = sharedPref.getString("auth_token", null)

        Log.d("EmissionActivity", "Token: $token")

        if (token == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Convert opacity value to decimal by dividing by 100
        val decimalOpacityValue = currentOpacity

        // Format the current time to a readable string with timezone information
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val pstTimeZone = TimeZone.getTimeZone("Asia/Manila")
        sdf.timeZone = pstTimeZone
        val formattedDate = sdf.format(Date())

        val apiService = ApiClient.create(token)
        val data = mapOf(
            "smokeTestId" to UUID.randomUUID().toString(),
            "opacity" to decimalOpacityValue.toString(),
            "smoke_result" to resultTextView.text.toString(),
            "createdAt" to formattedDate
        )

        Log.d("EmissionActivity", "Data to send: $data")

        val call = apiService.sendOpacityData("Bearer $token", data)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EmissionActivity, "Data sent successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@EmissionActivity, "Failed to send data", Toast.LENGTH_SHORT).show()
                    Log.e("API Error", "Error: ${response.code()} - ${response.message()}")
                    Log.e("API Error Body", "Error Body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("EmissionActivity", "Error: ${t.message}")
                Toast.makeText(this@EmissionActivity, "Error sending data", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                setupBluetooth()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            inputStream?.close()
            bluetoothSocket?.close()
        } catch (e: Exception) {
            Log.e(tag, "Error closing connection", e)
        }
    }
}