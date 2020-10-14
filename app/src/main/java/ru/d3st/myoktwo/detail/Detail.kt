package ru.d3st.myoktwo.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import ru.d3st.myoktwo.databinding.DetailFragmentBinding
import ru.d3st.myoktwo.network.MyGroup

class Detail : Fragment() {

    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val bind = DetailFragmentBinding.inflate(inflater,container,false)
        //через фрагмент получаем доступ к приложению
        val application = requireNotNull(activity).application
        //получаем данные из предыдущего фрагмента
        val selectedMyGroup = DetailArgs.fromBundle(arguments!!).selectedGroupId
        //создаем экземпляр ViewModelFactory, для того чтобы поместить данные из предыдущего фрагмента в ВьюМодел этого фрагмента
        val viewModelFactory = DetailViewModelFactory(selectedMyGroup, application)
        //биндим ВМ
        bind.detailDataViewModel = ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)
        //для обновления экрана
        bind.lifecycleOwner  = this

        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)


        return bind.root
    }


}