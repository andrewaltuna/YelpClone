package com.aslaltuna.yelpclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.aslaltuna.yelpclone.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class DetailPagerAdapter(private val context: Context, private val imageUrlList: List<String>) : RecyclerView.Adapter<DetailPagerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_restaurant_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUrl = imageUrlList[position]
        var imgView: ImageView = holder.itemView.findViewById(R.id.ivDetailRestaurantImage)

        Glide.with(context).load(imageUrl)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(20))).into(imgView)
    }

    override fun getItemCount() = imageUrlList.size
}