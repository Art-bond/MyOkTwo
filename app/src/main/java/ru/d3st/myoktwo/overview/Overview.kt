package ru.d3st.myoktwo.overview

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import ru.d3st.myoktwo.R
import ru.d3st.myoktwo.databinding.OverviewFragmentBinding


class Overview : Fragment() {

    private val viewModel: OverviewViewModel by lazy {
        ViewModelProvider(this).get(OverviewViewModel::class.java)
    }

    private var _bind: OverviewFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val bind get() = _bind!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _bind = OverviewFragmentBinding.inflate(inflater, container, false)

        bind.lifecycleOwner = this
        bind.listGroupDataViewModel = viewModel

        val adapter = GroupListAdapter(GroupListAdapter.OnClickListener {
            viewModel.displaySelectedGroup(it)
        })
        bind.recyclerViewGroup.adapter = adapter

        viewModel.groupOne.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
            }
        })
        //наблюдаем за переходом во фрагмент деталей групп
        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, {
            //проверяем есть ли данные в группе
            if (it != null) {
                //вызываем финдНавКонтроллер
                this.findNavController().navigate(
                    OverviewDirections.actionOverviewToDetail(it))
                //приводим пеерменную отвечающую за переход в исходное состояние
                viewModel.displaySelectedGroupComplete()
            }
        })
        setHasOptionsMenu(true)
        return bind.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_by_member -> {
                viewModel.sortListByAllMember()
            }
            R.id.sort_by_day_grow -> {
                viewModel.sortListByDayGrowMember()
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _bind = null
    }
}


