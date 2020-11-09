package ru.d3st.myoktwo.databse

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import timber.log.Timber

@Dao
interface MyGroupDao {

    @Query("select * from mygroupdb")
    fun getGroupData(): LiveData<List<MyGroupDB>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(groups: List<MyGroupDB>)

    @Query("select * from mygroupdb order by membersCount desc")
    fun sortByMember(): List<MyGroupDB>

    @Query("select * from mygroupdb order by membersDiff desc")
    fun sortByDayGrow(): List<MyGroupDB>
}

@Database(entities = [MyGroupDB::class], version = 1, exportSchema = false)
abstract class GroupsDataBase : RoomDatabase() {
    abstract val dao: MyGroupDao
}

private lateinit var INSTANCE: GroupsDataBase

fun getDatabase(context: Context): GroupsDataBase {
    synchronized(GroupsDataBase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                GroupsDataBase::class.java,
                "groups"
            ).build()
        }
    }
    Timber.i("database ${INSTANCE.dao}")
    return INSTANCE
}