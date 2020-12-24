package ru.d3st.myoktwo.network

import android.app.Application

import android.widget.Toast
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

import ru.d3st.myoktwo.domain.*
import ru.ok.android.sdk.*
import timber.log.Timber
import java.lang.Exception

import java.lang.reflect.Type


// -------------- YOUR APP DATA GOES HERE ------------
private const val APP_ID = "1265589504"
private const val APP_KEY = "CBAOFQGMEBABABABA"
private const val REDIRECT_URL = "okauth://ok1265589504"
// -------------- YOUR APP DATA ENDS -----------------

private const val BASE_URL =
    "https://api.ok.ru/fb.do"

/*private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()*/

val applicationKey = "application_key=$APP_KEY"
val format = "format=json"
val method = "method=users.getCurrentUser"

object OkMyApi {
    lateinit var ok: Odnoklassniki

    fun getOk(application: Application): Odnoklassniki {
        return Odnoklassniki(
            application.applicationContext,
            APP_ID,
            APP_KEY
        )
    }



    //подключаем Moshi
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        //.add(MoshiArrayListJsonAdapter.FACTORY)
        //.add(GroupArrayListMoshiAdapter())
        .build()

    //адаптеры моши на все случаи жизни
    //адаптер для пользователя
    val adapterMoshiUser: JsonAdapter<CurrentUser> = moshi.adapter(CurrentUser::class.java)

    //адаптер для одной группы
    val adapterGroupMoshi = moshi.adapter(GroupUser::class.java)
    val adapterGroupStatsMoshi = moshi.adapter(GroupStats::class.java)

    //моши адаптер для класса GroupPosts
    val adapterGroupPostsMoshi = moshi.adapter(GroupPosts::class.java)

    //моши адаптер для GroupPeopleStat
    val adapterGroupPeopleStat = moshi.adapter(GroupPeopleStat::class.java)

    //адаптер под распарсивание информации групп
    var type: Type = Types.newParameterizedType(
        List::class.java,
        GroupInfoItem::class.java
    )
    val adapterGroupInfo: JsonAdapter<List<GroupInfoItem>> = moshi.adapter(type)


    //  val mapGroupInfo = mapOf("fields" to "NAME, PIC_AVATAR, MEMBERS_COUNT")


    fun toast(application: Application, text: String) = Toast.makeText(
        application,
        text,
        Toast.LENGTH_LONG
    ).show()

    //функция для вызова постов группы
    suspend fun getStatTopics(groupId: String): GroupPosts =
        withContext(Dispatchers.IO) {
            val dayLongTime = 24 * 60 * 60 * 1000
            //вчера
            val start_time: Long = System.currentTimeMillis() - dayLongTime
            //сейчас
            val end_time: Long = System.currentTimeMillis()
            val gid = groupId
            val field = "id, feedback_total, engagement"
            val method = "group.getStatTopics"
            val mapGroupPosts = mapOf(
                "gid" to gid,
                "start_time" to start_time.toString(),
                "end_time" to end_time.toString(),
                "fields" to field
            )
            try {
                val json = ok.request(method, mapGroupPosts, OkRequestMode.DEFAULT)

                return@withContext adapterGroupPostsMoshi.fromJson(json.toString())!!
            } catch (e: Exception) {
                Timber.d(e.toString())
                return@withContext emptyList<GroupPosts>().first()
            }
        }

    suspend fun getGroupInfo(oneList: GroupUser.Group): GroupInfoItem =
        withContext(Dispatchers.IO) {
            //вынимаем данные id групп из общего листа с данными групп

            //переводим массив в строку через запятую
            val mapGroupInfo = mapOf(
                "uids" to oneList.groupId,
                "fields" to "NAME, PIC_AVATAR, MEMBERS_COUNT"
            )
            val methodGroupGetInfo = "group.getInfo"

            try {
                val json = ok.request(methodGroupGetInfo, mapGroupInfo, OkRequestMode.DEFAULT)
                Timber.d(json.toString())


                return@withContext adapterGroupInfo.fromJson(json.toString())!!.first()
            } catch (e: Exception) {
                Timber.d(e.toString())
                return@withContext emptyList<GroupInfoItem>().first()
            }
        }

    //функция получения данных КОНКРЕТНОЙ группы по ID
    suspend fun getGroupStatToday(groupId: String): GroupStats =
        withContext(Dispatchers.IO) {
            //cутки
            val dayLongTime = 86400 * 1000
            //вчера
            val start_time: Long = System.currentTimeMillis() - dayLongTime
            //сейчас
            val end_time: Long = System.currentTimeMillis()

            val mapGroupStat = mapOf(
                "gid" to groupId,
                "start_time" to start_time.toString(),
                "end_time" to end_time.toString(),
                "fields" to "members_count, members_diff, topic_opens"
            )
            try {
                val json = ok.request("group.getStatTrends", mapGroupStat, OkRequestMode.DEFAULT)
                Timber.d("для группы $groupId - $json")
                val resultMoshi = adapterGroupStatsMoshi.fromJson(json.toString())


                return@withContext resultMoshi!!
            } catch (e: Exception) {
                Timber.i("для группы $groupId $e")
                return@withContext emptyList<GroupStats>().first()

            }
        }

    suspend fun getStatPeople(groupId: String, listener: OkListener) =
        withContext(Dispatchers.IO) {
            val mapStatPeople = mapOf(
                "gid" to groupId,
                "fields" to "CITIES, COUNTRIES, DEMOGRAPHY_FEMALE, DEMOGRAPHY_MALE, REFERENCES"
            )
            ok.request("group.getStatPeople",
                mapStatPeople,
                OkRequestMode.DEFAULT,
                listener
            )
        }

    suspend fun getMyGroup(): NetworkMyGroupContainer {
        //TODO переписать логику для получения списка сюда из OVerView
        Timber.d("get my group is Ready")
        val result = getUserGroupsData() // user group data class
        val idGroups: List<GroupUser.Group> = getListGroupsId(result) // получаем список ID групп
        val fillList: List<MyGroup> = getDataForMyGroups(idGroups) //заполняем класс MyGroup данными
        val list: List<MyGroupNetwork> = fillList.map {
            MyGroupNetwork(
                groupId = it.groupId,
                name = it.name,
                membersCount = it.membersCount,
                membersDiff = it.membersDiff,
                picAvatar = it.picAvatar,
                topicOpens = it.topicOpens
            )
        }
        Timber.d("networkGroup ${list.size}}")
        return NetworkMyGroupContainer(list)
    }

    // to run code in Background Thread
    private suspend fun getUserGroupsData(): String =
        withContext(Dispatchers.IO) {
            val mapCountGroup = mapOf("count" to "100")
            //TODO уместен ли тут Try Catch
            try {
                val result = ok.request("group.getUserGroupsV2",
                    mapCountGroup,
                    OkRequestMode.DEFAULT)!!
                val json = JSONObject(result)
                if (json.has("errorCode")) {
                    //TODO вставить обработчик ошибок
                }
                return@withContext result
            } catch (e: Exception) {
                return@withContext "error group response $e"
            }

        }
    suspend fun getListGroupsId(json: String): List<GroupUser.Group> =
        withContext(Dispatchers.IO) {
            //полученный результат пропускаем через Моши Адаптер
            //TODO нужна проверка поступающего JSOn наличие данных или ошибки
            val list = adapterGroupMoshi.fromJson(json)
            if (list != null) {
                return@withContext list.groups
            }
            return@withContext emptyList()
        }
    suspend fun getDataForMyGroups(idGroups: List<GroupUser.Group>): List<MyGroup> =
        withContext(Dispatchers.IO) {
            var resultList: List<MyGroup> = emptyList()
            idGroups.forEach {
                val id = it.groupId
                val stats: GroupStats = getGroupStatToday(id)
                val info: GroupInfoItem = getGroupInfo(it)
                val posts: GroupPosts = getStatTopics(id)
                val postCountToday = posts.topics.size

                val name = info.name
                val membersCount = info.membersCount
                val picAvatar = info.picAvatar
                val listMemberDiff = stats.membersDiff
                var membersDiffToday: Int? = 0
                if (listMemberDiff.isNotEmpty()) {
                    membersDiffToday = stats.membersDiff.first()?.value
                }
                // val postToday =stats.topicOpens.first().value

                val one =
                    MyGroup(id, name, membersCount, picAvatar, membersDiffToday, postCountToday)
                resultList = resultList + one


                //Log.i("jsonstats", one.toString())

            }
            return@withContext resultList

        }

}



