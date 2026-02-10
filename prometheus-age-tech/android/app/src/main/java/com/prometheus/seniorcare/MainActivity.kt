package com.prometheus.seniorcare

import android.content.Intent
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prometheus.seniorcare.data.SeniorDataStore
import com.prometheus.seniorcare.services.DailyCheckService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStore = SeniorDataStore(this)

        // Start daily check service
        val serviceIntent = Intent(this, DailyCheckService::class.java)
        startService(serviceIntent)

        setContent {
            MaterialTheme {
                MainScreen(
                    userName = dataStore.getUserName() ?: "User",
                    onSOSClick = {
                        startActivity(Intent(this, SOSActivity::class.java))
                    },
                    onCheckInClick = { /* Daily check-in logic */ },
                    onLogoutClick = {
                        lifecycleScope.launch {
                            dataStore.clearAuth()
                        }
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    userName: String,
    onSOSClick: () -> Unit,
    onCheckInClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Prometheus AgeTech",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 32.dp)
        )

        Text(
            text = "Welcome, $userName! How are you today?",
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )

        // SOS Button - Large and prominent
        Button(
            onClick = onSOSClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        ) {
            Text(
                text = "SOS",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Daily Check-In Button
        Button(
            onClick = onCheckInClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 8.dp)
                .height(64.dp)
        ) {
            Text(text = "Daily Check-In", fontSize = 20.sp)
        }

        // Logout Button
        TextButton(
            onClick = onLogoutClick,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "Logout", fontSize = 16.sp)
        }
    }
}
