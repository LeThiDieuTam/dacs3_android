package com.example.ltdd.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Import các class từ các package mới
import com.example.ltdd.models.VaccinationData
import com.example.ltdd.remote.RetrofitClient

// === ViewModel cho MedicalRecordScreen ===
class MedicalRecordViewModel(private val userId: String) : ViewModel() {

    private val _vaccinationList = MutableStateFlow<List<VaccinationData>>(emptyList())
    val vaccinationList: StateFlow<List<VaccinationData>> = _vaccinationList

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg: StateFlow<String?> = _errorMsg

    fun loadVaccinations(context: Context) = viewModelScope.launch {
        _isLoading.value = true
        _errorMsg.value = null

        if (userId.isBlank()) {
            _errorMsg.value = "Không tìm thấy userId, vui lòng đăng nhập lại."
            _isLoading.value = false
            return@launch
        }

        try {
            val data = RetrofitClient.vaccinationInstance.getVaccinations(userId)
            _vaccinationList.value = data
        } catch (e: Exception) {
            Log.e("MedicalRecordVM", "Lỗi lấy dữ liệu: ${e.message}")
            _errorMsg.value = "Lỗi tải dữ liệu: ${e.localizedMessage}"
            Toast.makeText(context, _errorMsg.value, Toast.LENGTH_SHORT).show()
        } finally {
            _isLoading.value = false
        }
    }

    fun updateVaccinationStatus(context: Context, updatedShot: VaccinationData) = viewModelScope.launch {
        try {
            // Cập nhật trạng thái trong danh sách hiện tại trước khi gọi API
            _vaccinationList.value = _vaccinationList.value.map {
                if (it.id == updatedShot.id) updatedShot else it
            }

            RetrofitClient.vaccinationUpdateInstance.updateVaccinationInjected(
                userId = userId,
                shotId = updatedShot.id,
                isInjected = if (updatedShot.isInjected) 1 else 0
            )
            Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e("MedicalRecordVM", "Lỗi cập nhật: ${e.message}")
            // Hoàn lại trạng thái nếu cập nhật API thất bại (tùy chọn)
            _vaccinationList.value = _vaccinationList.value.map {
                if (it.id == updatedShot.id) updatedShot.copy(isInjected = !updatedShot.isInjected) else it
            }
            Toast.makeText(context, "Lỗi cập nhật: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}

// === ViewModelFactory cho MedicalRecordViewModel ===
class MedicalRecordViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(model: Class<T>): T {
        require(model.isAssignableFrom(MedicalRecordViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return MedicalRecordViewModel(userId) as T
    }
}

// === Composable MedicalRecordScreen ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalRecordScreen(navController: NavHostController, userId: String) {

    val viewModel: MedicalRecordViewModel = viewModel(factory = MedicalRecordViewModelFactory(userId))
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Collect các StateFlow từ ViewModel
    val vaccinationList by viewModel.vaccinationList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()

    LaunchedEffect(userId) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
            viewModel.loadVaccinations(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sổ tiêm vaccin") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Sử dụng AutoMirrored cho RTL
                            contentDescription = "Quay lại"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            VaccinationContent(
                isLoading = isLoading,
                errorMsg = errorMsg,
                vaccinations = vaccinationList
            ) { updatedShot ->
                // Gọi hàm update trong ViewModel
                viewModel.updateVaccinationStatus(context, updatedShot)
            }
        }
    }
}


@Composable
fun VaccinationContent(
    isLoading: Boolean,
    errorMsg: String?,
    vaccinations: List<VaccinationData>,
    onCheckedChange: (VaccinationData) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            errorMsg != null -> Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )

            else -> VaccinationList(vaccinations, onCheckedChange)
        }
    }
}

@Composable
fun VaccinationList(
    vaccinations: List<VaccinationData>,
    onCheckedChange: (VaccinationData) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(vaccinations) { shot ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFFE3F2FD) // Màu nền xanh pastel nhẹ
                ),
                elevation = CardDefaults.elevatedCardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = shot.vaccineName,
                        style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFD81B60)) // Hồng đậm
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\uD83D\uDCC5Ngày tiêm: ${shot.vaccinationDate}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "\uD83C\uDFE0 Địa điểm: ${shot.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF3F51B5)
                    )

                    if (shot.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "\uD83D\uDCDDGhi chú: ${shot.notes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = shot.isInjected,
                            onCheckedChange = {
                                onCheckedChange(shot.copy(isInjected = it))
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFFF48FB1), // hồng pastel
                                uncheckedColor = Color(0xFF90CAF9), // xanh nhạt
                                checkmarkColor = Color.White
                            )
                        )
                        Text(
                            text = "Đã tiêm",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (shot.isInjected) Color(0xFF4CAF50) else Color.Gray
                        )
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun MedicalRecordScreenPreview() {

}