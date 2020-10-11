package ru.d3st.myoktwo.network


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupInfoItem(
    val name: String, // Стройный подход
    val picAvatar: String, // https://i.mycdn.me/image?id=867965349840&t=32&plc=API&aid=1265589504&tkn=*_dcUqVn3ig10R04zsBFlybnacZM
    @Json(name = "members_count")
    val membersCount: Int, // 29519
    @Json(name = "pin_notifications_off")
    val pinNotificationsOff: Boolean // false
)