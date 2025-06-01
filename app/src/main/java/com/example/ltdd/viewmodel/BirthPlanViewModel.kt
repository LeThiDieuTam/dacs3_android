package com.example.ltdd.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ltdd.models.BirthPlanOption
import com.example.ltdd.models.BirthPlanQuestion
import com.example.ltdd.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.ltdd.models.BirthPlanSelectionRequest // Import DTO mới

sealed class BirthPlanUiState {
    object Loading : BirthPlanUiState()
    data class Success(val questions: List<BirthPlanQuestion>) : BirthPlanUiState()
    data class Error(val message: String) : BirthPlanUiState()
    object Idle : BirthPlanUiState()
    object Saving : BirthPlanUiState() // Trạng thái mới khi đang lưu
    data class SaveSuccess(val message: String) : BirthPlanUiState() // Trạng thái lưu thành công
    data class SaveError(val message: String) : BirthPlanUiState() // Trạng thái lưu lỗi
}

class BirthPlanViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<BirthPlanUiState>(BirthPlanUiState.Idle)
    val uiState: StateFlow<BirthPlanUiState> = _uiState.asStateFlow()

    private val _questionsWithSelections = MutableStateFlow<List<BirthPlanQuestion>>(emptyList())
    val questionsWithSelections: StateFlow<List<BirthPlanQuestion>> = _questionsWithSelections.asStateFlow()

    // Lưu userId để sử dụng khi gửi dữ liệu lên server
    private var currentUserId: String = ""

    init {
        // fetchBirthPlanQuestions() // Không gọi ở đây nữa, sẽ gọi từ Composable
    }

    // Hàm khởi tạo userId (gọi từ Composable)
    fun setUserId(userId: String) {
        if (currentUserId != userId) {
            currentUserId = userId
            fetchBirthPlanQuestions() // Chỉ fetch khi userId thay đổi hoặc lần đầu tiên
        }
    }


    fun fetchBirthPlanQuestions() {
        _uiState.value = BirthPlanUiState.Loading
        viewModelScope.launch {
            try {
                val fetchedQuestions = RetrofitClient.birthPlanApiService.getBirthPlanQuestions()
                // Giữ lại các lựa chọn cũ nếu có, nếu không thì mặc định là false
                val updatedQuestions = fetchedQuestions.map { newQuestion ->
                    val existingQuestion = _questionsWithSelections.value.find { it.id == newQuestion.id }
                    newQuestion.copy(
                        options = newQuestion.options.map { newOption ->
                            val existingOption = existingQuestion?.options?.find { it.id == newOption.id }
                            newOption.copy(isSelected = existingOption?.isSelected ?: false)
                        }
                    )
                }
                _questionsWithSelections.value = updatedQuestions
                _uiState.value = BirthPlanUiState.Success(fetchedQuestions)
            } catch (e: Exception) {
                _uiState.value = BirthPlanUiState.Error("Lỗi tải kế hoạch sinh: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun updateOptionSelection(questionId: String, optionId: String, isSelected: Boolean) {
        _questionsWithSelections.value = _questionsWithSelections.value.map { question ->
            if (question.id == questionId) {
                question.copy(
                    options = question.options.map { option ->
                        if (option.id == optionId) {
                            // Gọi hàm lưu lên server ngay khi trạng thái thay đổi
                            saveOptionSelectionToServer(questionId, optionId, isSelected)
                            option.copy(isSelected = isSelected)
                        } else {
                            option
                        }
                    }
                )
            } else {
                question
            }
        }
    }

    private fun saveOptionSelectionToServer(questionId: String, optionId: String, isSelected: Boolean) {
        viewModelScope.launch {
            _uiState.value = BirthPlanUiState.Saving // Cập nhật trạng thái đang lưu
            try {
                val request = BirthPlanSelectionRequest(
                    userId = currentUserId, // Sử dụng userId đã được set
                    questionId = questionId,
                    optionId = optionId,
                    isSelected = isSelected
                )
                val response = RetrofitClient.birthPlanApiService.saveBirthPlanSelection(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.value = BirthPlanUiState.SaveSuccess(response.body()?.message ?: "Lưu thành công!")
                } else {
                    _uiState.value = BirthPlanUiState.SaveError(
                        "Lỗi khi lưu: ${response.message()} - ${response.errorBody()?.string()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = BirthPlanUiState.SaveError("Lỗi kết nối khi lưu: ${e.message}")
                e.printStackTrace()
            }
            // Sau khi lưu xong, quay lại trạng thái Success hoặc Error dựa trên kết quả fetch ban đầu
            // Hoặc có thể giữ nguyên trạng thái UI hiện tại
            // _uiState.value = BirthPlanUiState.Success(_questionsWithSelections.value) // Nếu muốn quay lại Success sau khi lưu
        }
    }

    fun getQuestionById(questionId: String): BirthPlanQuestion? {
        return _questionsWithSelections.value.find { it.id == questionId }
    }

    fun saveBirthPlan() {
        // Hàm này có thể dùng để gửi toàn bộ kế hoạch sinh nếu cần
        println("Saving Birth Plan: ${_questionsWithSelections.value}")
    }
}