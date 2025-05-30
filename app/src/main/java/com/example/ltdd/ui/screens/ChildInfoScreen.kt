package com.example.ltdd.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.Response

interface PregnancyApi {
    @FormUrlEncoded
    @POST("pregnancy_add.php")
    suspend fun addPregnancyInfo(
        @Field("user_id") userId: String,
        @Field("week") week: String,
        @Field("weight") weight: String,
        @Field("length") length: String,
        @Field("due_date") dueDate: String
    ): Response<Unit>
}

object PregnancyRetrofitInstance {
    private const val BASE_URL = "   https://cd89-2001-ee0-4b6d-f0a0-bc15-4b82-50c3-ee65.ngrok-free.app/mevabe_api/"

    val api: PregnancyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PregnancyApi::class.java)
    }
}

@Composable
fun ChildInfoScreen(
    navController: NavController,
    userId: String?,
    onSuccess: () -> Unit = { navController.popBackStack() }
) {
    val context = LocalContext.current
    val uiScope = rememberCoroutineScope()

    if (userId == null) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Không tìm thấy thông tin người dùng", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    var week by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var length by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại")
            }
            Text(
                "Thông tin thai kỳ",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        OutlinedTextField(
            value = week,
            onValueChange = { if (it.length <= 2 && it.all(Char::isDigit)) week = it },
            label = { Text("Tuần thai (1-40)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Cân nặng (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = length,
            onValueChange = { length = it },
            label = { Text("Chiều dài (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        OutlinedTextField(
            value = dueDate,
            onValueChange = { dueDate = it },
            label = { Text("Ngày dự sinh (YYYY-MM-DD)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                Icon(Icons.Filled.CalendarToday, contentDescription = "Chọn ngày")
            }
        )

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Button(
            enabled = !isLoading,
            onClick = {
                when {
                    week.isBlank() || weight.isBlank() || length.isBlank() || dueDate.isBlank() -> {
                        errorMessage = "Vui lòng điền đầy đủ thông tin"
                    }
                    week.toIntOrNull() !in 1..40 -> {
                        errorMessage = "Tuần thai phải từ 1 đến 40"
                    }
                    else -> {
                        errorMessage = null
                        isLoading = true

                        uiScope.launch {
                            try {
                                val response = PregnancyRetrofitInstance.api.addPregnancyInfo(
                                    userId, week, weight, length, dueDate
                                )
                                isLoading = false

                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Thêm dữ liệu thành công!", Toast.LENGTH_SHORT).show()
                                    onSuccess()
                                    week = ""
                                    weight = ""
                                    length = ""
                                    dueDate = ""
                                } else {
                                    errorMessage = "Lỗi máy chủ: ${response.code()}"
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = "Lỗi kết nối: ${e.localizedMessage}"
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    "Lưu thông tin",
                    style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChildInfoScreenPreview() {
    val nav = rememberNavController()
    ChildInfoScreen(navController = nav, userId = "1")
}
