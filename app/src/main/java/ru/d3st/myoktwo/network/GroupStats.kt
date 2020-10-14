package ru.d3st.myoktwo.network


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupStats(
    @Json(name = "members_count")
    val membersCount: List<MembersCount>,
    @Json(name = "members_diff")
    val membersDiff: List<MembersDiff?>,
    @Json(name = "topic_opens")
    val topicOpens: List<TopicOpen>
) {
    @JsonClass(generateAdapter = true)
    data class MembersCount(
        val time: Long, // 1601845200000
        val value: Int // 89619
    )

    @JsonClass(generateAdapter = true)
    data class MembersDiff(
        val time: Long?, // 1601845200000
        val value: Int? // 1
    )

    @JsonClass(generateAdapter = true)
    data class TopicOpen(
        val time: Long, // 1601845200000
        val value: Int // 28
    )
}