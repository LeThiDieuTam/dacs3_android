@file:Suppress("UnusedImport")

package com.example.ltdd.ui.screens

import android.app.*
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.work.*
import com.example.ltdd.ui.components.BottomNavigationBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

// Import các class từ các package mới
import com.example.ltdd.models.ActivityModel
import com.example.ltdd.remote.RetrofitClient
import com.example.ltdd.models.ActivityResponse // Cần để chuyển đổi từ response API
import com.example.ltdd.models.CompletionResponse
import com.example.ltdd.models.ActivityDayIndexResponse
import com.example.ltdd.models.SaveActivityResponse


// Worker giữ nguyên
class DailyReminderWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val chId = "reminders"
        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                NotificationChannel(chId, "Thai giáo", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        val n: Notification = NotificationCompat.Builder(applicationContext, chId)
            .setSmallIcon(android.R.drawable.ic_notification_clear_all)
            .setContentTitle("Nhắc nhở thai kỳ")
            .setContentText("Đừng quên hoạt động thai giáo hôm nay!")
            .build()
        nm.notify(1, n)
        return Result.success()
    }
}

// Chuyển masterList vào đây để dễ quản lý, không cần OkHttpClient nữa
private val masterList = listOf(
    "📖 Kể chuyện",
    "🎵 Thai giáo âm thanh",
    "🎨 Nghệ thuật",
    "🎬 Xem phim",
    "💄 Làm đẹp"
)

// === ViewModel cho ShopScreen ===
class ShopViewModel(private val userId: String) : ViewModel() {

    private val _activities = MutableStateFlow<List<ActivityModel>>(emptyList())
    val activities: StateFlow<List<ActivityModel>> = _activities

    private val _completionPercent = MutableStateFlow(0)
    val completionPercent: StateFlow<Int> = _completionPercent

    private val _activityDayIndex = MutableStateFlow(1)
    val activityDayIndex: StateFlow<Int> = _activityDayIndex

    val doneToday: StateFlow<Int> = MutableStateFlow(0).apply {
        viewModelScope.launch {
            _activities.collect { acts -> value = acts.count { it.isDone } }
        }
    }

    // Hàm loadActivities ban đầu
    fun loadData(context: Context) = viewModelScope.launch {
        try {
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // 1. Fetch Activities
            val serverActivities = RetrofitClient.activityInstance.getDailyActivities(userId, currentDate)
            _activities.value = if (serverActivities.isEmpty()) {
                defaultActivities().also { saveLocally(context, it) }
            } else {
                // Chuyển đổi ActivityResponse sang ActivityModel
                val map = serverActivities.associate {
                    it.activity_title to ActivityModel(
                        it.activity_title,
                        it.is_done == 1,
                        if (it.is_done == 1) "Đã hoàn thành" else it.description
                    )
                }
                // Đảm bảo tất cả các hoạt động trong masterList đều có mặt
                masterList.map { map[it] ?: ActivityModel(it, false, "Chưa hoàn thành") }
            }

            // 2. Fetch Completion Percent
            val completionResponse = RetrofitClient.activityInstance.getCompletion(userId, currentDate)
            _completionPercent.value = completionResponse.completion_percentage

            // 3. Fetch Activity Day Index
            val dayIndexResponse = RetrofitClient.activityInstance.getActivityDayIndex(userId, currentDate)
            _activityDayIndex.value = maxOf(1, dayIndexResponse.day_index)

        } catch (e: Exception) {
            Log.e("ShopViewModel", "Lỗi tải dữ liệu: ${e.message}", e)
            Toast.makeText(context, "Lỗi tải dữ liệu: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            // Nếu có lỗi, có thể đặt lại về defaultActivities hoặc hiển thị trạng thái lỗi
            _activities.value = defaultActivities()
            _completionPercent.value = 0
            _activityDayIndex.value = 1
        }
    }

    // Hàm updateAt mới để tương tác với ViewModel
    fun updateActivityStatus(context: Context, index: Int, updatedActivity: ActivityModel) = viewModelScope.launch {
        try {
            // Cập nhật trạng thái cục bộ ngay lập tức để UI phản hồi nhanh
            val currentActivities = _activities.value.toMutableList()
            currentActivities[index] = updatedActivity
            _activities.value = currentActivities

            // Gửi cập nhật lên server
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val saveResponse = RetrofitClient.activityInstance.saveActivity(
                action = "save_activity",
                userId = userId,
                activityTitle = updatedActivity.title,
                description = updatedActivity.desc,
                isDone = if (updatedActivity.isDone) 1 else 0,
                activityDate = currentDate
            )
            _completionPercent.value = saveResponse.completion_percentage // Cập nhật phần trăm từ server

            Toast.makeText(context, "Cập nhật hoạt động thành công!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("ShopViewModel", "Lỗi lưu hoạt động: ${e.message}", e)
            Toast.makeText(context, "Lỗi lưu hoạt động: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            // Hoàn lại trạng thái nếu lưu thất bại (tùy chọn)
            val revertedActivities = _activities.value.toMutableList()
            revertedActivities[index] = updatedActivity.copy(isDone = !updatedActivity.isDone, desc = "Chưa hoàn thành") // Hoàn lại trạng thái cũ
            _activities.value = revertedActivities
        }
    }

    private fun defaultActivities() = masterList.map { ActivityModel(it, false, "Chưa hoàn thành") }

    private fun saveLocally(ctx: Context, list: List<ActivityModel>) {
        ctx.getSharedPreferences("app_pref", 0).edit().putString(
            "daily_activities",
            org.json.JSONArray().apply { // Sử dụng org.json.JSONArray
                list.forEach {
                    put(org.json.JSONObject().apply { // Sử dụng org.json.JSONObject
                        put("title", it.title); put("isDone", it.isDone); put("desc", it.desc)
                    })
                }
            }.toString()
        ).apply()
    }
}

// === ViewModelFactory cho ShopViewModel ===
class ShopViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(model: Class<T>): T {
        require(model.isAssignableFrom(ShopViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return ShopViewModel(userId) as T
    }
}

// === Composable ShopScreen ===
@Composable
fun ShopScreen(navController: NavController, userId: String) {

    val context = LocalContext.current
    val viewModel: ShopViewModel = viewModel(factory = ShopViewModelFactory(userId))
    val lifecycleOwner = LocalLifecycleOwner.current

    val activities by viewModel.activities.collectAsState()
    val completionPercent by viewModel.completionPercent.collectAsState()
    val activityDayIndex by viewModel.activityDayIndex.collectAsState()
    val doneToday by viewModel.doneToday.collectAsState()

    LaunchedEffect(userId) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
            viewModel.loadData(context)
        }
    }

    LaunchedEffect(Unit) {
        scheduleReminder(context)
    }

    Scaffold(bottomBar = { BottomNavigationBar(navController, userId) }) { pad ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ProgressSection(
                    completionPercent,
                    activityDayIndex,
                    doneToday,
                    activities.size
                )
                Spacer(Modifier.height(16.dp))
            }

            itemsIndexed(activities) { idx, act ->
                ActivityItem(
                    title = act.title,
                    desc = act.desc,
                    isDone = act.isDone
                ) {
                    viewModel.updateActivityStatus(
                        context,
                        idx,
                        act.copy(isDone = true, desc = "Đã hoàn thành")
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ProgressSection(percent: Int, dayIdx: Int, done: Int, total: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(120.dp).clip(CircleShape).background(Color(0xFFFBEAEA)),
            contentAlignment = Alignment.Center
        ) { Text("$percent%", color = Color(0xFFE57373), fontSize = 28.sp) }

        Spacer(Modifier.height(8.dp))
        Text("Thai giáo ngày thứ $dayIdx", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Mục tiêu hàng ngày của mẹ đã gần hoàn thành!", color = Color.Gray, fontSize = 14.sp)
        Text("%02d/%02d hoạt động trong ngày".format(done, total), color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
private fun ActivityItem(title: String, desc: String, isDone: Boolean, onDone: () -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                if (isDone) Text("Đã hoàn thành", color = Color(0xFF4CAF50), fontSize = 12.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text(desc, fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            if (!isDone) {
                Button(
                    onClick = onDone,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6)),
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Hoàn thành", color = Color.White, fontSize = 14.sp) }
            }
        }
    }
}

// Hàm này vẫn dùng để lưu cục bộ, không liên quan đến Retrofit
private fun saveLocally(ctx: Context, list: List<ActivityModel>) {
    ctx.getSharedPreferences("app_pref", 0).edit().putString(
        "daily_activities",
        org.json.JSONArray().apply {
            list.forEach {
                put(org.json.JSONObject().apply {
                    put("title", it.title); put("isDone", it.isDone); put("desc", it.desc)
                })
            }
        }.toString()
    ).apply()
}

// Hàm này vẫn dùng WorkManager, không liên quan đến Retrofit
private fun scheduleReminder(ctx: Context) {
    val req = OneTimeWorkRequestBuilder<DailyReminderWorker>()
        .setInitialDelay(1, TimeUnit.DAYS)
        .build()
    WorkManager.getInstance(ctx).enqueueUniqueWork("daily_reminder", ExistingWorkPolicy.KEEP, req)
}

// Các hàm loadActivities, saveActivityRemote, fetchPercent, fetchActivityDay, request
// đã được thay thế bằng Retrofit trong ViewModel, nên có thể xóa chúng ở đây.

@Preview(showBackground = true)
@Composable
fun PreviewShopScreen() = ShopScreen(rememberNavController(), "1")