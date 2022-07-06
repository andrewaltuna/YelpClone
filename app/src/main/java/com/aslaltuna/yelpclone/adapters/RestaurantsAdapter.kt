package com.aslaltuna.yelpclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aslaltuna.yelpclone.R
import com.aslaltuna.yelpclone.YelpRestaurant
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_restaurant.view.*

class RestaurantsAdapter(val context: Context, private val restaurants: List<YelpRestaurant>, private val recyclerViewInterface: RecyclerViewInterface): RecyclerView.Adapter<RestaurantsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(restaurant: YelpRestaurant) {
            itemView.tvName.text = restaurant.name
            itemView.ratingBar.rating = restaurant.rating.toFloat()
            itemView.tvReviews.text = "${restaurant.numReviews} Reviews"
            itemView.tvLocation.text = restaurant.location.address
            itemView.tvCategory.text = restaurant.categories[0].title
            itemView.tvDistance.text = restaurant.displayDistance()
            itemView.tvPrice.text = restaurant.price
            Glide.with(context).load(restaurant.imageUrl).apply(RequestOptions().transform(CenterCrop(), RoundedCorners(20))).into(itemView.imageView)
        }
    }

    interface RecyclerViewInterface {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_restaurant, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val restaurant = restaurants[position]
        holder.bind(restaurant)
        holder.itemView.setOnClickListener {
            recyclerViewInterface.onItemClick(position)
        }
    }

    override fun getItemCount() = restaurants.size
}
