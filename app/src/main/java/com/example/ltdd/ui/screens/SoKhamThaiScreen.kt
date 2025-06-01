package com.example.ltdd.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ltdd.models.Visit
import com.example.ltdd.remote.RetrofitClient
import kotlinx.coroutines.*
import java.io.IOException
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoKhamThaiScreen(navController: NavHostController, userId: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var date by rememberSaveable { mutableStateOf("") }
    var doctorName by rememberSaveable { mutableStateOf("") }
    var gestationalAge by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var bloodPressure by rememberSaveable { mutableStateOf("") }
    var fetalHeartRate by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }

    var isEditing by rememberSaveable { mutableStateOf(false) }
    var editId by rememberSaveable { mutableStateOf("") }
    var showForm by rememberSaveable { mutableStateOf(false) }
    var list by remember { mutableStateOf(listOf<Visit>()) } // Thay đổi kiểu dữ liệu từ JSONObject sang Visit

    val pastelPink = Color(0xFFFFC1CC)
    val pastelGreen = Color(0xFFB2F7EF)
    val pastelBackground = Color(0xFFFDF6F6)

    fun fetchList() {
        coroutineScope.launch(Dispatchers.IO) {
            try {

                val fetchedList = RetrofitClient.instance.getVisits(userId)
                withContext(Dispatchers.Main) {
                    list = fetchedList
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val message = when (e) {
                        is IOException -> "Lỗi mạng: Vui lòng kiểm tra kết nối internet."
                        is HttpException -> "Lỗi server: ${e.code()}. Vui lòng thử lại sau."
                        else -> "Đã xảy ra lỗi không xác định: ${e.localizedMessage}"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }

    fun clearForm() {
        date = ""; doctorName = ""; gestationalAge = ""; weight = ""
        bloodPressure = ""; fetalHeartRate = ""; note = ""
        isEditing = false; editId = ""; showForm = false
    }

    fun sendForm(id: String?) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val response = if (id == null) {
                    // Gọi API thêm mới
                    RetrofitClient.instance.addVisit(
                        userId, date, doctorName, gestationalAge, weight, bloodPressure, fetalHeartRate, note
                    )
                } else {
                    RetrofitClient.instance.updateVisit(
                        id, userId, date, doctorName, gestationalAge, weight, bloodPressure, fetalHeartRate, note
                    )
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                    if (response.success) {
                        clearForm()
                        fetchList()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val message = when (e) {
                        is IOException -> "Lỗi kết nối: Không thể gửi yêu cầu đến server."
                        is HttpException -> "Lỗi phản hồi từ server: ${e.code()}. ${e.response()?.errorBody()?.string()}" // Lấy thêm thông tin lỗi từ body nếu có
                        else -> "Đã xảy ra lỗi: ${e.localizedMessage}"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }
    }


    fun sendDelete(id: String) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                // Gọi API xóa
                val response = RetrofitClient.instance.deleteVisit(userId, id)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, response.message, Toast.LENGTH_SHORT).show()
                    if (response.success) {
                        fetchList()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val message = when (e) {
                        is IOException -> "Lỗi kết nối: Không thể xóa sổ khám thai."
                        is HttpException -> "Lỗi server khi xóa: ${e.code()}"
                        else -> "Đã xảy ra lỗi: ${e.localizedMessage}"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }

    LaunchedEffect(Unit) { fetchList() }

    Surface(modifier = Modifier.fillMaxSize(), color = pastelBackground) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color(0xFF4C4C6D)
                        )
                    }

                    Text(
                        "Sổ khám thai",
                        style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF4C4C6D)),
                        modifier = Modifier.weight(1f),
                    )

                    Button(
                        onClick = { clearForm(); showForm = true },
                        colors = ButtonDefaults.buttonColors(containerColor = pastelPink),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("➕ Thêm sổ khám thai", color = Color.White)
                    }
                }
            }


            items(list) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = pastelGreen),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("📅 Ngày khám: ${item.visitDate}")
                        Text("👩‍⚕️ Bác sĩ: ${item.doctorName}")
                        Text("🍼 Tuổi thai: ${item.gestationalAge} tuần")
                        Text("⚖️ Cân nặng: ${item.weight} kg")
                        Text("💓 Huyết áp: ${item.bloodPressure}")
                        Text("❤️ Tim thai: ${item.fetalHeartRate}")
                        Text("📝 Ghi chú: ${item.notes ?: "Không có"}")
                        Row(modifier = Modifier.padding(top = 8.dp)) {
                            IconButton(onClick = {
                                date = item.visitDate
                                doctorName = item.doctorName
                                gestationalAge = item.gestationalAge
                                weight = item.weight
                                bloodPressure = item.bloodPressure
                                fetalHeartRate = item.fetalHeartRate
                                note = item.notes ?: ""
                                editId = item.visitId
                                showForm = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = Color(0xFF4C4C6D))
                            }
                            IconButton(onClick = {
                                sendDelete(item.visitId)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Xoá", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            if (showForm) {
                item {
                    Divider(Modifier.padding(vertical = 12.dp))
                    Text(
                        if (isEditing) "🛠️ Cập nhật thông tin" else "📝 Thêm sổ khám thai",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF4C4C6D)
                    )
                    Spacer(Modifier.height(8.dp))
                    FormField("📅 Ngày khám (yyyy-mm-dd)", date) { date = it }
                    FormField("👩‍⚕️ Bác sĩ khám", doctorName) { doctorName = it }
                    FormField("🍼 Tuổi thai (tuần)", gestationalAge) { gestationalAge = it }
                    FormField("⚖️ Cân nặng (kg)", weight) { weight = it }
                    FormField("💓 Huyết áp", bloodPressure) { bloodPressure = it }
                    FormField("❤️ Tim thai", fetalHeartRate) { fetalHeartRate = it }
                    FormField("📝 Ghi chú", note) { note = it }
                    Spacer(Modifier.height(8.dp))
                    Row {
                        Button(
                            onClick = {
                                if (date.isBlank() || doctorName.isBlank() || gestationalAge.isBlank() || weight.isBlank() || bloodPressure.isBlank() || fetalHeartRate.isBlank()) {
                                    Toast.makeText(context, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                sendForm(if (isEditing) editId else null)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = pastelPink),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (isEditing) "✅ Cập nhật" else "➕ Thêm mới", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { clearForm() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4C4C6D))
                        ) {
                            Text("❌ Huỷ")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFFFC1CC),
            unfocusedBorderColor = Color(0xFFB2F7EF),
            focusedLabelColor = Color(0xFF4C4C6D),
            unfocusedLabelColor = Color(0xFF4C4C6D)
        ),
        shape = RoundedCornerShape(8.dp)
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SoKhamThaiScreenPreview() {
    SoKhamThaiScreen(navController = rememberNavController(), userId = "1")
}