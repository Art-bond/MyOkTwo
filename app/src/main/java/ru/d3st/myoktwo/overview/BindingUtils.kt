package ru.d3st.myoktwo.overview

import android.widget.TextView
import androidx.databinding.BindingAdapter
import ru.d3st.myoktwo.R
import ru.d3st.myoktwo.domain.MyGroup

@BindingAdapter("setName")
fun TextView.setGroupName(item: MyGroup) {
    text = item.name
}

@BindingAdapter("setMembers")
fun TextView.setGroupMembers(item: MyGroup) {
    text = item.membersCount.toString()
}

@BindingAdapter("setPosts")
fun TextView.setGroupPostToday(item: MyGroup) {
    val postString = resources.getString(R.string.posts) + item.topicOpens.toString()
    text = postString
}

@BindingAdapter("setDiffMember")
fun TextView.setGroupDiffMemberToday(item: MyGroup) {
    val diffMemberString = resources.getString(R.string.followers) + item.membersDiff.toString()
    text = diffMemberString
}

