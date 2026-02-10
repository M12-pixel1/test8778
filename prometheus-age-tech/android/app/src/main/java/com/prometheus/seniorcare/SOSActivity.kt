package com.prometheus.seniorcare

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.prometheus.seniorcare.data.ApiClient
import com.prometheus.seniorcare.data.SOSRequest
import com.prometheus.seniorcare.data.SeniorDataStore
import kotlinx.coroutines.launch

class SOSActivity : ComponentActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStore = SeniorDataStore(this)

        // Request location permission for GPS sharing
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }

        setContent {
            MaterialTheme {
                SOSScreen(dataStore = dataStore, onBackClick = { finish() })
            }
        }
    }
}

@Composable
fun SOSScreen(dataStore: SeniorDataStore, onBackClick: () -> Unit) {
    var alertSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (alertSent) {
            Text(
                text = "✓ SOS Alert Sent!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Help is on the way. Your emergency contacts have been notified.",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(56.dp)
            ) {
                Text(text = "Back to Home", fontSize = 18.sp)
            }
        } else {
            Text(
                text = "Emergency SOS",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )

            Text(
                text = "Press the button below to send an emergency alert to all your contacts",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        try {
                            val token = dataStore.getAuthToken()
                            val userId = dataStore.getUserId()
                            if (token != null && userId != null) {
                                val service = ApiClient.createService(token)
                                val request = SOSRequest(
                                    senior_id = userId,
                                    latitude = 0.0,
                                    longitude = 0.0,
                                    timestamp = System.currentTimeMillis()
                                )
                                val response = service.sendSOSAlert(request)
                                if (response.isSuccessful) {
                                    alertSent = true
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        isLoading = false
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .size(250.dp)
                    .padding(16.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(
                        text = "SEND\nSOS",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            TextButton(
                onClick = onBackClick,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Text(text = "Cancel", fontSize = 18.sp)
            }
        }
    }
}
