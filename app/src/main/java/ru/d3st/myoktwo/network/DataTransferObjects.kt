package ru.d3st.myoktwo.network

import com.squareup.moshi.JsonClass
import ru.d3st.myoktwo.databse.MyGroupDB
import ru.d3st.myoktwo.domain.MyGroup

@JsonClass(generateAdapter = true)
data class NetworkMyGroupContainer(val groups: List<MyGroupNetwork>)

@JsonClass(generateAdapter = true)
data class MyGroupNetwork(
    val groupId: String, // ID группы
    val name: String?, // Стройный подход
    val membersCount: Int, // 29519
    val picAvatar: String?, // https://i.mycdn.me/image?id=867965349840&t=32&plc=API&aid=1265589504&tkn=*_dcUqVn3ig10R04zsBFlybnacZM
    val membersDiff: Int?, //количество последователей за сутки
    val topicOpens: Int?, //количество постов за день
)

/**
 * Convert Network results to database objects
 */
fun NetworkMyGroupContainer.asDomainModel(): List<MyGroup> {
    return groups.map {
        MyGroup(
            groupId = it.groupId,
            name = it.name,
            membersCount = it.membersCount,
            membersDiff = it.membersDiff,
            picAvatar = it.picAvatar,
            topicOpens = it.topicOpens
        )
    }

}

/**
 * Convert Network results to database objects
 */
fun NetworkMyGroupContainer.asDataBaseModel(): List<MyGroupDB> {
    return groups.map {
        MyGroupDB(
            groupId = it.groupId,
            name = it.name,
            membersCount = it.membersCount,
            membersDiff = it.membersDiff,
            picAvatar = it.picAvatar,
            topicOpens = it.topicOpens
        )
    }
}