package ru.d3st.myoktwo.overview

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import org.json.JSONObject
import ru.d3st.myoktwo.network.*
import ru.d3st.myoktwo.network.OkMyApi.getGroupInfo
import ru.d3st.myoktwo.network.OkMyApi.getGroupStatToday
import ru.d3st.myoktwo.network.OkMyApi.getStatTopics
import ru.ok.android.sdk.OkRequestMode
import java.lang.Exception

enum class JsonStatus { LOADING, ERROR, DONE }

class OverviewViewModel(application: Application) : AndroidViewModel(application) {

    //Сбор информации для заполнения полей листа групп пользоватея
    private val _groupOne = MutableLiveData<List<MyGroup>>()
    val groupOne: LiveData<List<MyGroup>>
        get() = _groupOne

    private val _navigateToSelectedProperty = MutableLiveData<MyGroup>()
    val navigateToSelectedProperty: LiveData<MyGroup>
        get() = _navigateToSelectedProperty

    //статус загрузки JSON листа
    private val _status = MutableLiveData<JsonStatus>()
    val status: LiveData<JsonStatus>
        get() = _status

    //загрузка данных в процентах
    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int>
        get() = _progress

    private val _maxProgress = MutableLiveData<Int>()
    val maxProgress: LiveData<Int>
        get() = _maxProgress


    init {
        execute()
    }

    private fun execute() = viewModelScope.launch {

        onPreExecute()
        val result = doInBackground() // runs in background thread without blocking the Main Thread
        val idGroups: List<GroupUser.Group> = getListGroup(result) // получаем список ID групп
        //Log.e("json2", idGroups.toString())
        val fillList: List<MyGroup> = getMyGroupList(idGroups) //заполняем класс MyGroup данными
        //Log.e("json3", fillList.toString())
        onPostExecute(fillList)// переносим данные в RecyclerView
    }

    private suspend fun getMyGroupList(idGroups: List<GroupUser.Group>): List<MyGroup> =
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
                Log.i("jsonProgress", _progress.value.toString())


                //Log.i("jsonstats", one.toString())

            }
            return@withContext resultList

        }


    private suspend fun getListGroup(json: String): List<GroupUser.Group> =
        withContext(Dispatchers.IO) {
            //полученный результат пропускаем через Моши Адаптер
            //TODO нужна проверка поступающего JSOn наличие данных или ошибки
            val list = OkMyApi.adapterGroupMoshi.fromJson(json)
            if (list != null) {
                return@withContext list.groups
            }
            return@withContext emptyList()
        }

    // Runs on the Main(UI) Thread
    private fun onPreExecute() {
        // show progress
        _status.value = JsonStatus.LOADING
    }

    private suspend fun doInBackground(): String =
        withContext(Dispatchers.IO) {    // to run code in Background Thread
            try {
                val result = OkMyApi.ok.request("group.getUserGroupsV2",
                    OkMyApi.mapCountGroup,
                    OkRequestMode.DEFAULT)!!
                val json = JSONObject(result)
                if (json.has("errorCode")) {
                    _status.value = JsonStatus.ERROR

                }
                return@withContext result
            } catch (e: Exception) {
                _status.value = JsonStatus.ERROR
                return@withContext "error group response $e"
            }

        }

    // Runs on the Main(UI) Thread
    private fun onPostExecute(result: List<MyGroup>) {
        // hide progress
        _groupOne.value = result
        _status.value = JsonStatus.DONE
    }

    fun displaySelectedGroup(group: MyGroup) {
        _navigateToSelectedProperty.value = group
    }

    fun displaySelectedGroupComplete() {
        _navigateToSelectedProperty.value = null
    }

    fun sortListByAllMember() {
        val sortedList = _groupOne.value?.sortedByDescending { it.membersCount }
        _groupOne.value = sortedList

    }

    fun sortListByDayGrowMember() {
        val sortedList = _groupOne.value?.sortedByDescending { it.membersDiff }
        _groupOne.value = sortedList

    }
}
