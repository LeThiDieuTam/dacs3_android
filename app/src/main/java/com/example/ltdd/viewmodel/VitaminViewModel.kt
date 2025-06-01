package com.example.ltdd.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ltdd.models.Vitamin
import com.example.ltdd.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class VitaminUiState {
    object Loading : VitaminUiState()
    data class Success(val vitamins: List<Vitamin>) : VitaminUiState()
    data class Error(val message: String) : VitaminUiState()
    object Idle : VitaminUiState()
}

class VitaminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<VitaminUiState>(VitaminUiState.Idle)
    val uiState: StateFlow<VitaminUiState> = _uiState

    init {
        fetchVitamins()
    }

    fun fetchVitamins() {
        _uiState.value = VitaminUiState.Loading
        viewModelScope.launch {
            try {
                val vitamins = RetrofitClient.vitaminApiService.getVitamins()
                _uiState.value = VitaminUiState.Success(vitamins)
            } catch (e: Exception) {
                _uiState.value = VitaminUiState.Error("Lỗi tải dữ liệu: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}