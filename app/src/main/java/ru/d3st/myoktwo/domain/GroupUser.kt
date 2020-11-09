package ru.d3st.myoktwo.domain


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupUser(
    @Json(name = "groups")
    val groups: List<Group>,
    @Json(name = "anchor")
    val anchor: String
)
{
    @JsonClass(generateAdapter = true)
    data class Group(
        @Json(name = "groupId")
        val groupId: String,
        @Json(name = "userId")
        val userId: String,
        @Json(name = "status")
        val status: String,
        @Json(name = "role")
        val role: String?
    )
}