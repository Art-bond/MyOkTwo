package ru.d3st.myoktwo.network


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentUser(
    @Json(name = "uid")
    val uid: String,
    @Json(name = "birthday")
    val birthday: String,
    @Json(name = "birthdaySet")
    val birthdaySet: Boolean,
    @Json(name = "age")
    val age: Int,
    @Json(name = "first_name")
    val firstName: String,
    @Json(name = "last_name")
    val lastName: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "locale")
    val locale: String,
    @Json(name = "gender")
    val gender: String,
    @Json(name = "has_email")
    val hasEmail: Boolean,
    @Json(name = "location")
    val location: Location,
    @Json(name = "online")
    val online: String?,
    @Json(name = "photo_id")
    val photoId: String,
    @Json(name = "pic_1")
    val pic1: String,
    @Json(name = "pic_2")
    val pic2: String,
    @Json(name = "pic_3")
    val pic3: String
) {
    @JsonClass(generateAdapter = true)
    data class Location(
        @Json(name = "city")
        val city: String,
        @Json(name = "country")
        val country: String,
        @Json(name = "countryCode")
        val countryCode: String,
        @Json(name = "countryName")
        val countryName: String
    )
}