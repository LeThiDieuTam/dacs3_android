
package com.example.ltdd.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ltdd.models.LoginResponse
import com.example.ltdd.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (String, Int) -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = Color(0xFFF3E5F5)
    val primaryColor = Color(0xFF64B5F6)
    val accentColor = Color(0xFFF48FB1)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Chào Mẹ Bầu!",
                style = MaterialTheme.typography.headlineMedium.copy(color = primaryColor)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Hãy đăng nhập để theo dõi thai kỳ của bạn",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Tên đăng nhập") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = primaryColor,
                    cursorColor = primaryColor
                )
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = primaryColor,
                    cursorColor = primaryColor
                )
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank()) {
                        message = "Vui lòng nhập đầy đủ thông tin"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    message = ""

                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            val response = RetrofitClient.authInstance.login(username, password)
                            withContext(Dispatchers.Main) {
                                isLoading = false
                                if (response.success && response.userId != null) {
                                    onLoginSuccess(response.name ?: "Mẹ Bầu", response.userId)
                                } else {
                                    message = response.message
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: HttpException) {
                            withContext(Dispatchers.Main) {
                                isLoading = false
                                val errorMessage = e.response()?.errorBody()?.string() ?: "Lỗi từ server: ${e.code()}"
                                message = "Đăng nhập thất bại: $errorMessage"
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                Log.e("LoginError", "HTTP Exception: ${e.code()} - $errorMessage", e)
                            }
                        } catch (e: IOException) {
                            withContext(Dispatchers.Main) {
                                isLoading = false
                                message = "Không thể kết nối tới server. Vui lòng kiểm tra kết nối mạng."
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                Log.e("LoginError", "IO Exception: ${e.localizedMessage}", e)
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                isLoading = false
                                message = "Đã xảy ra lỗi không xác định: ${e.localizedMessage}"
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                Log.e("LoginError", "General Exception: ${e.localizedMessage}", e)
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading)
                    CircularProgressIndicator(
                        Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                else
                    Text("Đăng nhập")
            }

            Spacer(Modifier.height(12.dp))

            if (message.isNotEmpty() && !isLoading) {
                Text(text = message, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Chưa có tài khoản? Đăng ký ngay",
                color = accentColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewLoginScreen() {
    MaterialTheme {
        LoginScreen(
            onLoginSuccess = { _, _ -> },
            onNavigateToRegister = {}
        )
    }
}