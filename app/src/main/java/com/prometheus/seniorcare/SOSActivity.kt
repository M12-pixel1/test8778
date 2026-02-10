package com.prometheus.seniorcare

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.compose.runtime.DisposableEffect
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.prometheus.seniorcare.ui.theme.SeniorCareTheme

class SOSActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SeniorCareTheme {
                SOSScreen(
                    onCancel = { finish() },
                    onCallEmergency = { callEmergency() }
                )
            }
        }
    }

    private fun callEmergency() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:112")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startActivity(intent)
        }
    }
}

@Composable
fun SOSScreen(
    onCancel: () -> Unit,
    onCallEmergency: () -> Unit
) {
    var countdown by remember { mutableIntStateOf(5) }
    var isCancelled by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdown = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                if (!isCancelled) {
                    onCallEmergency()
                }
            }
        }.start()

        onDispose {
            timer.cancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SOS",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD32F2F)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Skambinama pagalbos numeriu po",
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "$countdown",
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD32F2F)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "sekundžių",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = {
                isCancelled = true
                onCancel()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Atšaukti",
                fontSize = 20.sp
            )
        }
    }
}
