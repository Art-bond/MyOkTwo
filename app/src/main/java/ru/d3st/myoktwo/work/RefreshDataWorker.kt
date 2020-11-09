package ru.d3st.myoktwo.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.HttpException
import ru.d3st.myoktwo.databse.getDatabase
import ru.d3st.myoktwo.repository.GroupsRepository
import timber.log.Timber

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object{
        const val WORK_NAME = "ru.d3st.myoktwo.work.work.RefreshDataWorker"
    }

    override val coroutineContext: CoroutineDispatcher
        get() = super.coroutineContext

    override suspend fun doWork(): Result {
        val dataBase = getDatabase(applicationContext)
        val repository = GroupsRepository(dataBase)
        try {
            repository.refreshGroups()
            Timber.d("Work request for sync is run")
        } catch (e: HttpException) {
            Timber.e("Error WorkManager $e")
            return Result.retry()
        }

        return Result.success()
    }
}