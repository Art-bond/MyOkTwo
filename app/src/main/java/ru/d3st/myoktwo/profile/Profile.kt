package ru.d3st.myoktwo.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import ru.d3st.myoktwo.databinding.ProfileFragmentBinding

class Profile : Fragment() {

    private lateinit var viewModel: ProfileViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bind = ProfileFragmentBinding.inflate(inflater, container, false)
        arguments?.let {
            bind.jsonProfile.text = ProfileArgs.fromBundle(it).jsonProfile
        }
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        bind.lifecycleOwner = this
        bind.profileViewModelData = viewModel

        bind.btnToGroup.setOnClickListener {
            navigateToGroupList()
        }

        return bind.root
    }


    private fun navigateToGroupList(){
        //действие
        val action =
            ProfileDirections.
            actionProfileToOverview()

        view?.findNavController()?.navigate(action)
    }
}