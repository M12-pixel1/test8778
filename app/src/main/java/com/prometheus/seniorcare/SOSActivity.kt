package com.prometheus.seniorcare

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.prometheus.seniorcare.data.ApiClient
import com.prometheus.seniorcare.data.SOSRequest
import com.prometheus.seniorcare.data.SeniorDataStore
import com.prometheus.seniorcare.ui.theme.SeniorCareTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SOSActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            SeniorCareTheme {
                SOSScreen()
            }
        }
    }
}

@Composable
fun SOSScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var countdown by remember { mutableStateOf(10) }
    var locationSent by remember { mutableStateOf(false) }

    // Countdown timer
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown--
        }

        // When countdown reaches 0, send SOS
        if (countdown == 0) {
            sendSOSAlert(context, scope)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SKUBI PAGALBA",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Skambinama per: $countdown s",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (locationSent) {
            Text(
                text = "✅ Jūsų vieta nusiųsta artimiesiems",
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Cancel SOS
                (context as? SOSActivity)?.finish()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.width(200.dp)
        ) {
            Text("Atšaukti", fontSize = 18.sp)
        }
    }
}

private fun sendSOSAlert(context: Context, scope: CoroutineScope) {
    scope.launch {
        try {
            // Get current location
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        // Send to API
                        val dataStore = SeniorDataStore(context)
                        val userId = dataStore.getUserId()
                        val token = dataStore.getAuthToken()

                        val apiService = ApiClient.createService(token)
                        val sosRequest = SOSRequest(
                            senior_id = userId,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            timestamp = System.currentTimeMillis()
                        )

                        // Call API in a coroutine
                        scope.launch {
                            try {
                                apiService.sendSOSAlert(sosRequest)
                            } catch (e: Exception) {
                                // Log error but don't block emergency call
                            }
                        }
                    }
                }
            }

            // Call emergency number
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:112")
                }
                context.startActivity(intent)
            }

        } catch (e: Exception) {
            Toast.makeText(context, "SOS klaida: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
