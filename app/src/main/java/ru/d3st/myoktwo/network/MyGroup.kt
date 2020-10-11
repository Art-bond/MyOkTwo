package ru.d3st.myoktwo.network

//класс для занесения данных
data class MyGroup(
    val groupId: String, // ID группы
    val name: String?, // Стройный подход
    val membersCount: Int?, // 29519
    val picAvatar: String?, // https://i.mycdn.me/image?id=867965349840&t=32&plc=API&aid=1265589504&tkn=*_dcUqVn3ig10R04zsBFlybnacZM
    val membersDiff: Int?, //количество последователей за сутки
    val topicOpens: Int? //количество постов за день
    )