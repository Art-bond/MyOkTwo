package ru.d3st.myoktwo.databse

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.d3st.myoktwo.domain.MyGroup

@Entity
data class MyGroupDB constructor(
    @PrimaryKey
    val groupId: String, // ID группы
    val name: String?, // Стройный подход
    val membersCount: Int, // 29519
    val picAvatar: String?, // https://i.mycdn.me/image?id=867965349840&t=32&plc=API&aid=1265589504&tkn=*_dcUqVn3ig10R04zsBFlybnacZM
    val membersDiff: Int?, //количество последователей за сутки
    val topicOpens: Int? //количество постов за день
)

/**
 * Map MyGroup to domain entities
 */
fun List<MyGroupDB>.asDomainModel(): List<MyGroup> {
    return map {
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