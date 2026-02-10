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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prometheus.seniorcare.data.ApiClient
import com.prometheus.seniorcare.data.LoginRequest
import com.prometheus.seniorcare.data.SeniorDataStore
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStore = SeniorDataStore(this)

        // Skip login if already logged in
        if (dataStore.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContent {
            MaterialTheme {
                LoginScreen(
                    dataStore = dataStore,
                    onLoginSuccess = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun LoginScreen(dataStore: SeniorDataStore, onLoginSuccess: () -> Unit) {
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Prometheus AgeTech",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Senior Protection Platform",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = ""
                    try {
                        val service = ApiClient.createService()
                        val response = service.login(LoginRequest(phoneNumber, password))
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                dataStore.saveAuthToken(body.access_token)
                                dataStore.saveUserId(body.user.id)
                                dataStore.saveUserName(body.user.full_name)
                                dataStore.saveUserPhone(body.user.phone_number)
                                onLoginSuccess()
                            } else {
                                errorMessage = "Invalid response from server."
                            }
                        } else {
                            errorMessage = "Invalid credentials. Please try again."
                        }
                    } catch (e: Exception) {
                        errorMessage = "Connection error. Please check your network."
                    }
                    isLoading = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isLoading && phoneNumber.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text(text = "Login", fontSize = 18.sp)
            }
        }
    }
}
