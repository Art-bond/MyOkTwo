package ru.d3st.myoktwo.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.d3st.myoktwo.databinding.OverviewFragmentBinding

class Overview : Fragment() {

    private var _bind : OverviewFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val bind get() = _bind!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = OverviewFragmentBinding.inflate(inflater, container, false )

        val viewModel = ViewModelProvider(this).get(OverviewViewModel::class.java)
        bind.lifecycleOwner  = this
        bind.listGroupDataViewModel = viewModel

        val adapter = GroupListAdapter(GroupListAdapter.OnClickListener {
            viewModel.displaySelectedGroup(it)
        })
        bind.rvGroupList.adapter = adapter

/*        viewModel.groupList.observe(viewLifecycleOwner, {
            it?.let {
                adapter.data = it
            }
        })*/
        viewModel.groupOne.observe(viewLifecycleOwner,{
            it?.let {
                adapter.submitList(it)
            }
        })
        //наблюдаем за переходом во фрагмент деталей групп
        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, {
            //проверяем есть ли данные в группе
            if (it != null){
                //вызываем финдНавКонтроллер
                this.findNavController().navigate(
                    OverviewDirections.actionOverviewToDetail(it))
                    //приводим пеерменную отвечающую за переход в исходное состояние
                viewModel.displaySelectedGroupComplete()
            }
        })

        return bind.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _bind = null
    }
}