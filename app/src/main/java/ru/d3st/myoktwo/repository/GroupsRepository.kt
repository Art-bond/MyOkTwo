package ru.d3st.myoktwo.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.d3st.myoktwo.databse.GroupsDataBase
import ru.d3st.myoktwo.databse.asDomainModel
import ru.d3st.myoktwo.domain.MyGroup
import ru.d3st.myoktwo.network.NetworkMyGroupContainer
import ru.d3st.myoktwo.network.OkMyApi
import ru.d3st.myoktwo.network.asDataBaseModel
import timber.log.Timber

enum class Sort { member, dayGrow }
class GroupsRepository(private val database: GroupsDataBase) {

    private var _groups = MutableLiveData<List<MyGroup>>()

    val groups: LiveData<List<MyGroup>>
        get() = Transformations.map(database.dao.getGroupData()) {
            it.asDomainModel()
        }


    //обновляет данные кеша
    suspend fun refreshGroups() {
        withContext(Dispatchers.IO) {
            Timber.i("refresh groups is called")
            val groupList = OkMyApi.getMyGroup()
            database.dao.insertAll(groupList.asDataBaseModel())
            Timber.i("group list get ${groupList.groups}")
        }
        Timber.e("Group List get empty")

    }

    suspend fun sortBy(sortingMethod: Sort) {
        withContext(Dispatchers.IO) {
            when (sortingMethod) {
                Sort.member -> _groups.postValue(database.dao.sortByMember().asDomainModel())
                Sort.dayGrow -> _groups.postValue(database.dao.sortByDayGrow().asDomainModel())
            }
        }
    }


}