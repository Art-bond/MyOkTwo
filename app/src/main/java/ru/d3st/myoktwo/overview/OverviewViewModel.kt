package ru.d3st.myoktwo.overview

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.*
import ru.d3st.myoktwo.databse.getDatabase
import ru.d3st.myoktwo.domain.MyGroup
import ru.d3st.myoktwo.repository.GroupsRepository
import ru.d3st.myoktwo.repository.Sort
import timber.log.Timber
import java.lang.Exception

enum class JsonStatus { LOADING, ERROR, DONE }

class OverviewViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * The data source this ViewModel will fetch results from.
     */
    private val dataBase = getDatabase(application)
    private val groupsRepository = GroupsRepository(dataBase)
    private val groups = groupsRepository.groups




    //Сбор информации для заполнения полей листа групп пользоватея
    private var _groupOne = MutableLiveData<List<MyGroup>>()
    val groupOne: LiveData<List<MyGroup>>
        get() = _groupOne


    private val _navigateToSelectedProperty = MutableLiveData<MyGroup>()
    val navigateToSelectedProperty: LiveData<MyGroup>
        get() = _navigateToSelectedProperty

    //статус загрузки JSON листа
    private val _status = MutableLiveData<JsonStatus>()
    val status: LiveData<JsonStatus>
        get() = _status


    init {
        execute()
    }

    private fun execute() = viewModelScope.launch {

        onPreExecute() //операции до фонового получения данных
        try {
            groupsRepository.refreshGroups()
            Timber.i("get list from repository is done and list")
            onPostExecute()


        } catch (e: Exception) {
            Timber.d("exception getRepository is $e")
        }
    }

    private suspend fun transferDataToMyGroupOne(groups: LiveData<List<MyGroup>>):List<MyGroup> =
            withContext(Dispatchers.IO){
                try {
                    return@withContext groups.value!!
                }catch (e:Exception){
                    Timber.e("happening erorr $e")
                    return@withContext emptyList()
                }
            }


    // Runs on the Main(UI) Thread
    private fun onPreExecute() {
        // show progress
        _groupOne = groups as MutableLiveData<List<MyGroup>>
        _status.value = JsonStatus.LOADING
        Timber.d("onPreExecute is done and ${status.value}")
    }


    // Runs on the Main(UI) Thread
    private fun onPostExecute() {
        // hide progress
        _status.value = JsonStatus.DONE
        Timber.d("Post execute is done and containts ${_groupOne.value?.size} elements")
    }

    fun displaySelectedGroup(group: MyGroup) {
        _navigateToSelectedProperty.value = group
    }

    fun displaySelectedGroupComplete() {
        _navigateToSelectedProperty.value = null
    }

    //сортировка групп по численности подписчиков
    fun sortListByAllMembers() {
        viewModelScope.launch {
            val sortedList = _groupOne.value?.sortedByDescending { it.membersCount }
            _groupOne.value = sortedList

            //select * from mygroupdb order by membersCount desc
            //groupsRepository.sortBy(Sort.dayGrow)
        }

    }

    // сортировка групп по росту за день
    fun sortListByDayGrowMember() {
        viewModelScope.launch {
            val sortedList = _groupOne.value?.sortedByDescending { it.membersDiff }
            _groupOne.value = sortedList

            //groupsRepository.sortBy(Sort.member)
        }
    }

    fun refreshDataFromNetwork() {

    }
}
