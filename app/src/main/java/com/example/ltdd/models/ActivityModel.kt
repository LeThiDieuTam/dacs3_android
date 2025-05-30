
package com.example.ltdd.models


data class ActivityModel(
    val title: String,
    var isDone: Boolean,
    var desc: String
)


data class ActivityResponse(
    val activity_title: String,
    val description: String,
    val is_done: Int

)

data class CompletionResponse(
    val completion_percentage: Int
)

data class ActivityDayIndexResponse(
    val day_index: Int
)


data class SaveActivityResponse(
    val completion_percentage: Int
)