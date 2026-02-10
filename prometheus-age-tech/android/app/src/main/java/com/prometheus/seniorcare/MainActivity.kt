package com.prometheus.seniorcare

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prometheus.seniorcare.data.SeniorDataStore
import com.prometheus.seniorcare.services.DailyCheckService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var dataStore: SeniorDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStore = SeniorDataStore(this)

        // Start daily check service
        val serviceIntent = Intent(this, DailyCheckService::class.java)
        startService(serviceIntent)

        setContent {
            SeniorCareTheme {
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

        // Request permissions
        requestPermissions()

        // Start daily check service
        startService(Intent(this, DailyCheckService::class.java))
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.POST_NOTIFICATIONS
        )

        val permissionsToRequest = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest, 100)
        }
    }

    private fun startSOS() {
        val intent = Intent(this, SOSActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        dataStore.clearAuth()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userName: String,
    onSOSClick: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

        Text(
            text = "Welcome, $userName! How are you today?",
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp)
        )

    LaunchedEffect(Unit) {
        val dataStore = SeniorDataStore(context)
        seniorName = dataStore.getUserName() ?: "Senjoras"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sveiki, $seniorName!") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Atsijungti"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            item {
                // SOS Button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(120.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFD32F2F)
                    ),
                    onClick = onSOSClick
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "SOS",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Skubus iškvietimas",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }

            item {
                // Daily Check
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Dienos patikrinimas",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (!dailyCheckDone) {
                            Button(
                                onClick = {
                                    dailyCheckDone = true
                                    Toast.makeText(context, "Dienos patikrinimas atliktas!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Aš esu gerai")
                            }
                        } else {
                            Text(
                                text = "✅ Dienos patikrinimas atliktas",
                                color = Color.Green
                            )
                        }
                    }
                }
            }

            item {
                // Emergency Contacts
                Text(
                    text = "Skubios pagalbos kontaktai",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp, 8.dp, 16.dp, 0.dp)
                )
            }

            items(emergencyContacts.size) { index ->
                val contact = emergencyContacts[index]
                ContactCard(contact = contact)
            }
        }
    }
}

@Composable
fun ContactCard(contact: Contact) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = contact.relationship,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = { /* Call contact */ }) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "Skambinti",
                    tint = Color(0xFF2196F3)
                )
            }
        }
    }
}
