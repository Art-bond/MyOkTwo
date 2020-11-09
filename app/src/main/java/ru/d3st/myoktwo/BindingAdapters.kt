package ru.d3st.myoktwo

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.d3st.myoktwo.overview.JsonStatus

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri =
            imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image))
            .into(imgView)
    }
}

//Адаптер для загрузки данных в лист
/*@BindingAdapter("listData")
fun bindRecyclerView(
    recyclerView: GroupListAdapter,
    data: List<MyGroup>,
) {

}*/

@BindingAdapter("listLoadingStatus")
fun bindStatus(statusImageView: ImageView, status: JsonStatus?) {
    when (status) {
        JsonStatus.LOADING -> {

            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        JsonStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        JsonStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("loading")
fun bindLoad(bar: ProgressBar, status: JsonStatus?) {
    when (status) {
        JsonStatus.LOADING -> {
            bar.visibility = View.VISIBLE

        }
        JsonStatus.ERROR -> {
            bar.visibility = View.VISIBLE
        }
        JsonStatus.DONE -> {
            bar.visibility = View.GONE
        }
    }
}


