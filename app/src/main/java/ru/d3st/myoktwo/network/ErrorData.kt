package ru.d3st.myoktwo.network


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorData(
    @Json(name = "error_code")
    val errorCode: Int, // 3
    @Json(name = "error_msg")
    val errorMsg: String, // METHOD : Method get not found
    @Json(name = "error_data")
    val errorData: Any? // null
)