package ru.d3st.myoktwo.domain


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GroupPeopleStat(
    @Json(name = "demography_male")
    val demographyMale: List<DemographyMale>,
    @Json(name = "demography_female")
    val demographyFemale: List<DemographyFemale>,
    val references: List<Reference>,
    val countries: List<Country>,
    val cities: List<City>
) {
    @JsonClass(generateAdapter = true)
    data class DemographyMale(
        val name: String, // <12
        val value: Int, // 377
        val percentage: Double // 0.5
    )

    @JsonClass(generateAdapter = true)
    data class DemographyFemale(
        val name: String, // <12
        val value: Int, // 1028
        val percentage: Double // 1.1
    )

    @JsonClass(generateAdapter = true)
    data class Reference(
        val name: String, // FEED
        val value: Int, // 71
        val percentage: Double // 52.3
    )

    @JsonClass(generateAdapter = true)
    data class Country(
        val name: String, // Россия
        val value: Int, // 2347
        val percentage: Double // 87.8
    )

    @JsonClass(generateAdapter = true)
    data class City(
        val name: String, // Moscow
        val value: Int, // 313
        val percentage: Double // 38.0
    )
}