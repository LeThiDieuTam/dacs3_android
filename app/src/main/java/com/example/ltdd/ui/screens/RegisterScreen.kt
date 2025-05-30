package com.example.ltdd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.Response

interface RegisterApiService {
    @FormUrlEncoded
    @POST("register.php")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("name") name: String,
        @Field("email") email: String
    ): Response<RegisterResponse>
}

data class RegisterResponse(
    val success: Boolean,
    val message: String
)

val retrofitRegisterService: RegisterApiService by lazy {
    Retrofit.Builder()
        .baseUrl(" https://cd89-2001-ee0-4b6d-f0a0-bc15-4b82-50c3-ee65.ngrok-free.app/mevabe_api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RegisterApiService::class.java)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFCE4EC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tạo tài khoản mới",
                style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFF00796B))
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Đăng ký để theo dõi sức khỏe mẹ và bé",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.DarkGray),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Tên đăng nhập") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF00796B),
                    cursorColor = Color(0xFF00796B)
                )
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF00796B),
                    cursorColor = Color(0xFF00796B)
                )
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Xác nhận mật khẩu") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF00796B),
                    cursorColor = Color(0xFF00796B)
                )
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Họ và tên") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF00796B),
                    cursorColor = Color(0xFF00796B)
                )
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF00796B),
                    cursorColor = Color(0xFF00796B)
                )
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (username.isBlank() || password.isBlank() || confirmPassword.isBlank() || name.isBlank()) {
                        message = "Vui lòng điền đầy đủ thông tin"
                        return@Button
                    }

                    if (password != confirmPassword) {
                        message = "Mật khẩu không khớp"
                        return@Button
                    }

                    isLoading = true
                    message = ""

                    coroutineScope.launch {
                        try {
                            val response = retrofitRegisterService.register(username, password, name, email)
                            isLoading = false
                            if (response.isSuccessful) {
                                val body = response.body()
                                if (body != null) {
                                    message = body.message
                                    if (body.success) {
                                        onRegisterSuccess()
                                    }
                                } else {
                                    message = "Dữ liệu phản hồi rỗng"
                                }
                            } else {
                                message = "Đăng ký thất bại: ${response.code()}"
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            message = "Lỗi kết nối: ${e.localizedMessage}"
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00796B),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading)
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                else
                    Text("Đăng ký")
            }

            Spacer(Modifier.height(10.dp))

            if (message.isNotEmpty()) {
                Text(text = message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewRegisterScreenUI() {
    MaterialTheme {
        RegisterScreen(onRegisterSuccess = {})
    }
}
