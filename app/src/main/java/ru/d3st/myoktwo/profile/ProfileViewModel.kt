package ru.d3st.myoktwo.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import ru.d3st.myoktwo.network.OkMyApi.ok
import ru.d3st.myoktwo.network.OkMyApi.userMoshiAdapter

import ru.ok.android.sdk.OkRequestMode


class ProfileViewModel(application: Application) : AndroidViewModel(application) {


    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()

    // The external immutable LiveData for the response String
    val response: LiveData<String>
        get() = _response

    private val _oneName = MutableLiveData<String>()
    val oneName: LiveData<String>
        get() = _oneName

    private val _ageUser = MutableLiveData<String>()
    val ageUser: LiveData<String>
        get() = _ageUser

    private val _cityUser = MutableLiveData<String>()
    val cityUser: LiveData<String>
        get() = _cityUser

    private val _avatarUser = MutableLiveData<String>()
    val avatarUser: LiveData<String>
        get() = _avatarUser


    init {
        execute()
    }

    private fun execute() = viewModelScope.launch {
        onPreExecute()
        val result = doInBackground() // runs in background thread without blocking the Main Thread
        onPostExecute(result)
    }

    private suspend fun doInBackground(): String =
        withContext(Dispatchers.IO) { // to run code in Background Thread
            // do async work
            Log.i("json", "")
            try {
                val result = ok.request("users.getCurrentUser", null, OkRequestMode.DEFAULT)
                return@withContext result.toString()
            } catch (exc: java.lang.Exception) {
                Log.e("jsonExp", "Failed to get current user info", exc)
            }
            //delay(1000) // simulate async work
            return@withContext "Failed to get current user info"
        }

    // Runs on the Main(UI) Thread
    private fun onPreExecute() {
        // show progress
    }

    // Runs on the Main(UI) Thread
    private fun onPostExecute(result: String) {
        // hide progress
        val moshiUser = userMoshiAdapter.fromJson(result)

        moshiUser?.let {
            _response.value = moshiUser.toString()
            _oneName.value = "${moshiUser.firstName} ${moshiUser.lastName}"
            _ageUser.value = "Age: " + moshiUser.age
            _cityUser.value = "City: " + moshiUser.location.city
            _avatarUser.value = moshiUser.pic2
        }
    }

}

