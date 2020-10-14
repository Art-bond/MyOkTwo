package ru.d3st.myoktwo.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.d3st.myoktwo.databinding.ListItemGroupBinding

import ru.d3st.myoktwo.network.MyGroup

class GroupListAdapter(val onClickListener: OnClickListener) : ListAdapter<MyGroup, GroupListAdapter.ViewHolder>(GroupListDiffCallback()) {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(item)
        }
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    class ViewHolder private constructor(private val binding:ListItemGroupBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item: MyGroup){
            binding.group = item
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup):ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemGroupBinding.inflate(layoutInflater,parent,false)
                return ViewHolder(binding)
            }
        }
    }
    class OnClickListener( val clickListener: (group:MyGroup) -> Unit){
        fun onClick(group: MyGroup) = clickListener(group)
    }
}
    class GroupListDiffCallback : DiffUtil.ItemCallback<MyGroup>() {
        override fun areItemsTheSame(oldItem: MyGroup, newItem: MyGroup): Boolean {
            return oldItem.groupId == newItem.groupId
        }

        override fun areContentsTheSame(oldItem: MyGroup, newItem: MyGroup): Boolean {
            return oldItem == newItem
        }
    }


