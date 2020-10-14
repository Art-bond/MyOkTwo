package ru.d3st.myoktwo.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.d3st.myoktwo.network.MyGroup
import java.lang.IllegalArgumentException

class DetailViewModelFactory(
    private val selectedMyGroup: MyGroup,
    private val application: Application):ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)){
            return DetailViewModel(selectedMyGroup,application) as T
        }
        throw  IllegalArgumentException("Unknown ViewModel Class")
    }

}
