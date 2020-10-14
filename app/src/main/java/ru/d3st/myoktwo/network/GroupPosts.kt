package ru.d3st.myoktwo.network


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupPosts(
    @Json(name = "has_more")
    val hasMore: Boolean, // false
    val topics: List<Topic>
) {
    @JsonClass(generateAdapter = true)
    data class Topic(
        val id: String, // 152104418108797
        val engagement: Int, // 86
        @Json(name = "feedback_total")
        val feedbackTotal: Int // 50
    )
}