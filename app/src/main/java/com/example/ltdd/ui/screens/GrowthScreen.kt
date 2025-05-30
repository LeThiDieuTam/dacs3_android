package com.example.ltdd.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import com.example.ltdd.models.GrowthModel // Thay vì PregnancyData
import com.example.ltdd.remote.RetrofitClient // Giữ nguyên

private val PinkPastel = Color(0xFFFFF1F8)
private val MintPastel = Color(0xFFE6F8F7)
private val pastelGradient = Brush.verticalGradient(
    colors = listOf(PinkPastel, MintPastel)
)

class GrowthViewModel(private val userId: String) : ViewModel() {
    private val _growthData = MutableStateFlow<List<GrowthModel>>(emptyList())
    val growthData: StateFlow<List<GrowthModel>> = _growthData // Đổi tên biến và kiểu
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg

    fun reload(ctx: Context) = viewModelScope.launch {
        _isLoading.value = true
        _errorMsg.value = null
        try {
            val data = withContext(Dispatchers.IO) { RetrofitClient.growthService.getGrowthInfo(userId) }
            _growthData.value = data
        } catch (e: Exception) {
            _errorMsg.value = "Không tải được dữ liệu: ${e.message}"
            Toast.makeText(ctx, _errorMsg.value, Toast.LENGTH_SHORT).show()
        } finally { _isLoading.value = false }
    }
}

class GrowthViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(model: Class<T>): T {
        require(model.isAssignableFrom(GrowthViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return GrowthViewModel(userId) as T
    }
}

@Composable
fun GrowthScreen(
    navController: NavHostController,
    userId: String,
    viewModelFactory: GrowthViewModelFactory = remember { GrowthViewModelFactory(userId) }
) {

    val vm: GrowthViewModel = viewModel(factory = viewModelFactory)
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val data by vm.growthData.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val errorMsg by vm.errorMsg.collectAsState()
    var selectedWeek by rememberSaveable { mutableStateOf(1) }

    LaunchedEffect(userId) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            vm.reload(ctx)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(pastelGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            Text("Tháng tăng trưởng", style = MaterialTheme.typography.headlineMedium)

            val listState = rememberLazyListState()
            LaunchedEffect(selectedWeek) { listState.animateScrollToItem(selectedWeek - 1) }

            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items((1..40).toList()) { week ->
                    WeekCircle(
                        week = week,
                        selected = week == selectedWeek,
                        onClick = { selectedWeek = week }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.padding(top = 40.dp))
                !errorMsg.isNullOrEmpty() -> Text(
                    errorMsg ?: "",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 40.dp)
                )
                else -> {
                    // Kiểm tra dữ liệu GrowthModel
                    val info = data.firstOrNull { it.week?.toIntOrNull() == selectedWeek }
                    if (info == null) {
                        Text(
                            "Chưa có dữ liệu cho tuần $selectedWeek",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 40.dp)
                        )
                    } else {
                        DetailCard(info)
                    }
                }
            }
        }
    }
}


@Composable
private fun WeekCircle(week: Int, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) MaterialTheme.colorScheme.primary
    else PinkPastel.copy(alpha = 0.65f)
    val fg = if (selected) Color.White
    else MaterialTheme.colorScheme.onBackground

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable { onClick() },
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = iconForWeek(week), fontSize = 24.sp)
        Text(
            "Tuần $week",
            style = TextStyle(fontSize = 11.sp, color = fg),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun DetailCard(d: GrowthModel) {
     val baseUrl = " https://cd89-2001-ee0-4b6d-f0a0-bc15-4b82-50c3-ee65.ngrok-free.app/mevabe_api/"
    val fullImageUrl = d.image?.let { if (it.startsWith("http")) it else baseUrl + it } ?: ""
    Log.d("ImageLoadDebug", "Final Image URL to load: $fullImageUrl") // Đảm bảo dòng này có

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (fullImageUrl.isNotBlank()) {
                item {
                    AsyncImage(
                        model = fullImageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            item { InfoRow("Chiều cao", d.length ?: "--", "📏") }
            item { InfoRow("Cân nặng", d.weight ?: "--", "⚖️") }
            item { InfoRow("Mô tả",     d.description ?: "--", "📝") }

        }
    }
}

@Composable private fun TitleText(text: String) =
    Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold)

@Composable
private fun InfoRow(label: String, value: String, icon: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.width(6.dp))
        Text("$label: ", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Text(value, fontSize = 15.sp)
    }
}


private fun iconForWeek(week: Int): String = when (week) {
    1 -> "🟢"; 2 -> "🍒"; 3 -> "🍊"; 4 -> "🍋"; 5 -> "🍓"
    6 -> "🥑"; 7 -> "🌽"; 8 -> "🥕"; 9 -> "🍆"
    else -> "🍍"
}

@Preview(showBackground = true)
@Composable
fun GrowthScreenPreview() {
    val nav = androidx.navigation.compose.rememberNavController()
    GrowthScreen(navController = nav, userId = "1")
}