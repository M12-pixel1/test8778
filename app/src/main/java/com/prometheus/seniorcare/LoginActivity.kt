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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.prometheus.seniorcare.data.SeniorDataStore
import com.prometheus.seniorcare.data.ApiClient
import com.prometheus.seniorcare.data.LoginRequest
import com.prometheus.seniorcare.ui.theme.SeniorCareTheme
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if already logged in
        val dataStore = SeniorDataStore(this)
        if (dataStore.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContent {
            SeniorCareTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Prometheus SeniorCare",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Telefono numeris") },
            placeholder = { Text("+37061234567") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Slaptažodis") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (phoneNumber.isBlank() || password.isBlank()) {
                    errorMessage = "Užpildykite visus laukus"
                    return@Button
                }

                isLoading = true
                scope.launch {
                    try {
                        val apiService = ApiClient.createService()
                        val response = apiService.login(
                            LoginRequest(
                                phone_number = phoneNumber,
                                password = password
                            )
                        )

                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            val dataStore = SeniorDataStore(context)

                            dataStore.saveAuthToken(loginResponse?.access_token ?: "")
                            dataStore.saveUserId(loginResponse?.user?.id ?: "")
                            dataStore.saveUserName(loginResponse?.user?.full_name ?: "")

                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                            (context as? LoginActivity)?.finish()
                        } else {
                            errorMessage = "Neteisingi duomenys"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Klaida: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Prisijungti")
            }
        }
    }
}
