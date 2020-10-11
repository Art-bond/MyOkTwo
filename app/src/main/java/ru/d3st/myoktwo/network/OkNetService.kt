package ru.d3st.myoktwo.network

import android.app.Application

import android.widget.Toast
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.ok.android.sdk.*

import java.lang.reflect.Type


// -------------- YOUR APP DATA GOES HERE ------------
private const val APP_ID = "1265589504"
private const val APP_KEY = "CBAOFQGMEBABABABA"
private const val REDIRECT_URL = "okauth://ok1265589504"
// -------------- YOUR APP DATA ENDS -----------------

private const val BASE_URL =
    "https://api.ok.ru/fb.do"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

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

    val mapCountGroup = mapOf("count" to "100")




    //подключаем Moshi
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        //.add(MoshiArrayListJsonAdapter.FACTORY)
        //.add(GroupArrayListMoshiAdapter())
        .build()
    //адаптеры моши на все случаи жизни
    //адаптер для пользователя
    val userMoshiAdapter = moshi.adapter(CurrentUser::class.java)
    //адаптер для одной группы
    val adapterGroupMoshi = moshi.adapter(GroupUser::class.java)
    val adapterGroupStatsMoshi = moshi.adapter(GroupStats::class.java)

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




}



