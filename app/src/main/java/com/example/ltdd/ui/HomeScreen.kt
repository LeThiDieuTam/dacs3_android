package com.example.ltdd.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ltdd.R
import com.example.ltdd.ui.components.BottomNavigationBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

@Composable
fun HomeScreen(
    navController: NavHostController,
    username: String,
    userId: String
) {
    var showForm by remember { mutableStateOf(false) }

    var week by remember { mutableStateOf(20) }
    var length by remember { mutableStateOf(25.0) }
    var weight by remember { mutableStateOf(300.0) }
    var dueDate by remember { mutableStateOf("2025-08-01") }

    LaunchedEffect(userId) {
        loadPregnancyInfo(userId) { w, l, wt, dd ->
            week = w; length = l; weight = wt; dueDate = dd
        }
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController, userId) },
        containerColor = Color(0xFFF4F4F9)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { GreetingSection(username) }

            item {
                PregnancyProgressModern(
                    week = week,
                    length = length,
                    weight = weight,
                    dueDate = dueDate,
                    onClick = { showForm = true }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FeatureButton(
                        title = "Sổ khám thai",
                        icon = Icons.Default.Receipt,
                        backgroundColor = Color(0xFFB2EBF2),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("soKhamThai/$userId") }
                    )
                    FeatureButton(
                        title = "Sổ tay dinh dưỡng",
                        icon = Icons.Default.Restaurant,
                        backgroundColor = Color(0xFFE1BEE7),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("soTayDinhDuong/$userId") }
                    )
                    FeatureButton(
                        title = "Bài đăng",
                        icon = Icons.Default.Star,
                        backgroundColor = Color(0xFFFFF9C4),
                        modifier = Modifier.weight(1f),
                        onClick = { navController.navigate("dauAnThaiKy/$userId") }
                    )
                }
            }

            item {
                Image(
                    painter = painterResource(R.drawable.banner_thaigiao),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            item {
                Text(
                    "Cẩm nang",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF6200EE),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            items(
                listOf(
                    "Dấu hiệu nhận biết trẻ có hệ tiêu hoá khoẻ mạnh",
                    "Lời khuyên hữu ích khi chăm sóc trẻ sơ sinh"
                )
            ) { tip -> TipItem(tip) }
        }

        if (showForm) {
            PregnancyUpdateForm(
                userId = userId,
                onDismiss = { showForm = false },
                onUpdateSuccess = { w, l, wt, dd ->
                    week = w; length = l; weight = wt; dueDate = dd
                    showForm = false
                }
            )
        }
    }
}

@Composable
fun GreetingSection(userName: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(R.drawable.pink_welcome_icon),
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            "Chào mừng $userName!",
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = Color(0xFF6200EE)
        )
    }
}

@Composable
fun PregnancyProgressModern(
    week: Int,
    totalWeeks: Int = 40,
    length: Double,
    weight: Double,
    dueDate: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val progress = (week / totalWeeks.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE4EC)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Tiến trình thai kỳ",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF880E4F)
                )
                Text(
                    "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFFE91E63),
                trackColor = Color(0xFFEEEEEE)
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painterResource(R.drawable.ic_calendar), contentDescription = null, tint = Color(0xFFE91E63))
                    Spacer(Modifier.height(4.dp))
                    Text("$week tuần", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painterResource(R.drawable.ic_ruler), contentDescription = null, tint = Color(0xFFF06292))
                    Spacer(Modifier.height(4.dp))
                    Text("${length} cm", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painterResource(R.drawable.ic_weight), contentDescription = null, tint = Color(0xFFAB47BC))
                    Spacer(Modifier.height(4.dp))
                    Text("${weight} g", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painterResource(R.drawable.ic_due_date), contentDescription = null, tint = Color(0xFF7B1FA2))
                    Spacer(Modifier.height(4.dp))
                    Text(dueDate, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }
            }
        }
    }
}
@Composable
fun FeatureButton(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(8.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TipItem(tip: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF6200EE))
            Spacer(Modifier.width(8.dp))
            Text(tip)
        }
    }
}


@Composable
fun PregnancyUpdateForm(
    userId: String,
    onDismiss: () -> Unit,
    onUpdateSuccess: (week: Int, length: Double, weight: Double, dueDate: String) -> Unit
) {
    var weekInput by remember { mutableStateOf("") }
    var lengthInput by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }
    var dueDateInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.5f)
    ) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Cập nhật thông tin thai kỳ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF6200EE)
                    )

                    OutlinedTextField(
                        value = weekInput,
                        onValueChange = { weekInput = it },
                        label = { Text("Tuần thai") },
                        singleLine = true,
                        isError = errorMessage.contains("tuần", true)
                    )
                    OutlinedTextField(
                        value = lengthInput,
                        onValueChange = { lengthInput = it },
                        label = { Text("Chiều dài (cm)") },
                        singleLine = true,
                        isError = errorMessage.contains("chiều dài", true)
                    )
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Cân nặng (g)") },
                        singleLine = true,
                        isError = errorMessage.contains("cân nặng", true)
                    )
                    OutlinedTextField(
                        value = dueDateInput,
                        onValueChange = { dueDateInput = it },
                        label = { Text("Dự sinh (YYYY-MM-DD)") },
                        singleLine = true,
                        isError = errorMessage.contains("dự sinh", true)
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { onDismiss() }) {
                            Text("Huỷ")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(onClick = {
                            // Validate input
                            val w = weekInput.toIntOrNull()
                            val l = lengthInput.toDoubleOrNull()
                            val we = weightInput.toDoubleOrNull()
                            val dd = dueDateInput.trim()

                            errorMessage = when {
                                w == null || w !in 1..42 -> "Tuần thai phải từ 1 đến 42"
                                l == null || l <= 0 -> "Chiều dài phải là số dương"
                                we == null || we <= 0 -> "Cân nặng phải là số dương"
                                !Regex("""\d{4}-\d{2}-\d{2}""").matches(dd) -> "Dự sinh phải có định dạng YYYY-MM-DD"
                                else -> ""
                            }

                            if (errorMessage.isEmpty()) {
                                scope.launch(Dispatchers.IO) {
                                    val success = updatePregnancyInfo(
                                        userId,
                                        w!!,
                                        l!!,
                                        we!!,
                                        dd
                                    )
                                    if (success) {
                                        onUpdateSuccess(w, l, we, dd)
                                    } else {
                                        errorMessage = "Cập nhật thất bại, vui lòng thử lại"
                                    }
                                }
                            }
                        }) {
                            Text("Cập nhật")
                        }
                    }
                }
            }
        }
    }
}


private val client = OkHttpClient()

fun loadPregnancyInfo(
    userId: String,
    onResult: (week: Int, length: Double, weight: Double, dueDate: String) -> Unit
) {
    val url = "  https://2629-2401-d800-f531-9420-9015-e99e-277c-272a.ngrok-free.app/mevabe_api/get_pregnancy.php?user_id=$userId"
    val request = Request.Builder().url(url).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("HomeScreen", "Load pregnancy info failed", e)
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { jsonStr ->
                try {
                    val json = JSONObject(jsonStr)
                    val week = json.optInt("week", 20)
                    val length = json.optDouble("length", 25.0)
                    val weight = json.optDouble("weight", 300.0)
                    val dueDate = json.optString("due_date", "2025-08-01")
                    onResult(week, length, weight, dueDate)
                } catch (e: Exception) {
                    Log.e("HomeScreen", "Parse pregnancy info error", e)
                }
            }
        }
    })
}

fun updatePregnancyInfo(
    userId: String,
    week: Int,
    length: Double,
    weight: Double,
    dueDate: String
): Boolean {
    val url = "  https://2629-2401-d800-f531-9420-9015-e99e-277c-272a.ngrok-free.app/mevabe_api/save_pregnancy.php\n"

    val formBody = FormBody.Builder()
        .add("user_id", userId)
        .add("week", week.toString())
        .add("length", length.toString())
        .add("weight", weight.toString())
        .add("due_date", dueDate)
        .build()

    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val body = response.body?.string()
            body?.contains("success", ignoreCase = true) == true
        } else {
            false
        }
    } catch (e: Exception) {
        Log.e("HomeScreen", "Update pregnancy info failed", e)
        false
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {

    HomeScreen(
        navController = rememberNavController(),
        username = "Lan Anh",
        userId = "1"
    )
}
