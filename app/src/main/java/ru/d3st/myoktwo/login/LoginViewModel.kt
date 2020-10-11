package ru.d3st.myoktwo.login

import android.content.Intent
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.d3st.myoktwo.MainActivity
import ru.d3st.myoktwo.R
import ru.ok.android.sdk.*
import ru.ok.android.sdk.util.OkAuthType
import ru.ok.android.sdk.util.OkScope



class LoginViewModel : ViewModel() {



    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()

    // The external immutable LiveData for the response String
    val response: LiveData<String>
        get() = _response



}
