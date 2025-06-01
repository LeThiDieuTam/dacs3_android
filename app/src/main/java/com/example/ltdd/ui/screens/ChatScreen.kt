package com.example.ltdd.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ltdd.R
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.Response

data class Message(
    val content: String,
    val sender: String,
    val time: String
)

data class MessageResponse(
    val success: Boolean,
    val message: String,
    val messages: List<MessageData>?
)

data class MessageData(
    val message: String,
    val sender: String,
    val time: String
)

data class SendMessageResponse(
    val success: Boolean,
    val message: String
)

interface ChatApiService {
    @GET("get_messages.php")
    suspend fun getMessages(
        @Query("user_id") userId: String,
        @Query("doctor_id") doctorId: Int
    ): Response<MessageResponse>

    @FormUrlEncoded
    @POST("send_message.php")
    suspend fun sendMessage(
        @Field("user_id") userId: String,
        @Field("doctor_id") doctorId: Int,
        @Field("sender") sender: String,
        @Field("message") message: String
    ): Response<SendMessageResponse>
}

object ChatRetrofitInstance {
    val api: ChatApiService by lazy {
        Retrofit.Builder()
            .baseUrl("   https://2629-2401-d800-f531-9420-9015-e99e-277c-272a.ngrok-free.app/mevabe_api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApiService::class.java)
    }
}

class ChatViewModel : ViewModel() {
    private val doctorId = 1
    var messages by mutableStateOf<List<Message>>(emptyList())
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadMessages(userId: String) {
        viewModelScope.launch {
            try {
                errorMessage = null
                val response = ChatRetrofitInstance.api.getMessages(userId, doctorId)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true && body.messages != null) {
                        messages = body.messages.map {
                            Message(it.message, it.sender, it.time)
                        }
                    } else {
                        errorMessage = body?.message ?: "Không thể tải tin nhắn"
                    }
                } else {
                    errorMessage = "Lỗi server: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Không thể kết nối đến máy chủ."
            }
        }
    }

    fun sendMessage(
        userId: String,
        content: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = ChatRetrofitInstance.api.sendMessage(userId, doctorId, "u", content)
                if (response.isSuccessful && response.body()?.success == true) {
                    onSuccess()
                } else {
                    onError()
                }
            } catch (e: Exception) {
                onError()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    userId: String,
    vm: ChatViewModel = viewModel()
) {
    val context = LocalContext.current
    var input by remember { mutableStateOf("") }

    val messages = vm.messages
    val error = vm.errorMessage

    LaunchedEffect(userId) {
        vm.loadMessages(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE4E1))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
            }
            Text(
                text = "Tin nhắn với bác sĩ",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        error?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            items(messages) { message ->
                val isUser = message.sender.equals("u", ignoreCase = true)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        if (!isUser) DoctorAvatar()
                        if (!isUser) Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .background(
                                    if (isUser) Color(0xFF4CAF50) else Color(0xFF2196F3),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                                .widthIn(max = 250.dp)
                        ) {
                            Text(text = message.content, color = Color.White)
                        }

                        if (isUser) Spacer(modifier = Modifier.width(8.dp))
                        if (isUser) UserAvatar()
                    }
                    Text(
                        text = message.time,
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(
                            start = if (isUser) 0.dp else 8.dp,
                            end = if (isUser) 8.dp else 0.dp,
                            top = 2.dp
                        )
                    )
                }
            }
        }

        Divider()

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = input,
                onValueChange = { input = it },
                placeholder = { Text("Nhập tin nhắn...") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Button(
                onClick = {
                    if (input.isNotBlank()) {
                        val msg = input.trim()
                        vm.sendMessage(
                            userId = userId,
                            content = msg,
                            onSuccess = {
                                input = ""
                                vm.loadMessages(userId)
                            },
                            onError = {
                                Toast.makeText(context, "Gửi tin nhắn thất bại!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Gửi")
            }
        }
    }
}

@Composable
private fun UserAvatar() {
    Image(
        painter = painterResource(R.drawable.user),
        contentDescription = "User Avatar",
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color(0xFF4CAF50))
            .padding(4.dp)
    )
}

@Composable
private fun DoctorAvatar() {
    Image(
        painter = painterResource(R.drawable.doctor_avatar3),
        contentDescription = "Doctor Avatar",
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color(0xFF1976D2))
            .padding(4.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    Text("Preview không khả dụng do thiếu NavController.")
}
