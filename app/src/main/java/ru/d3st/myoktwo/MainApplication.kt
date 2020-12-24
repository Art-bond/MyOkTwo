package ru.d3st.myoktwo

import android.app.Application
import android.os.Build
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.d3st.myoktwo.work.RefreshDataWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainApplication: Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    /**
     * onCreate is called before the first screen is shown to the user.
     *
     * Use it to setup any background tasks, running expensive setup operations in a background
     * thread to avoid delaying app start.
     */
    override fun onCreate() {
        super.onCreate()
        delayedInit()
    }
    //for init WorkManager and Timber in background process
    private fun delayedInit() {
        applicationScope.launch {
            Timber.plant(Timber.DebugTree())
            setupRecurringWork()
        }
    }

    //метод создает и инициализирует переодический запрос
    private fun setupRecurringWork() {
        //ограничения
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED) // только когда в сети без измерений
            .setRequiresCharging(true)//только когда заряжается
            .setRequiresBatteryNotLow(true) //ограничение на низкий заряд батареи
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            } //только когда девайс в режиме ожидания
            .build()

/*        val repeatingRequest =
            PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.DAYS).build()*/

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            60, TimeUnit.MINUTES)
            .setConstraints(constraints) //устанавливаем ограничения
            .build()

        Timber.d("WorkManager: Periodic Work request for sync is scheduled")
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)
    }

}