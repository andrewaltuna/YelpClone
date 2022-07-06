package com.aslaltuna.yelpclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aslaltuna.yelpclone.R
import com.aslaltuna.yelpclone.YelpRestaurantReview
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_review.view.*

class ReviewsAdapter(private val context: Context, private val reviews: List<YelpRestaurantReview>) : RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]
        val ivReviewUser = holder.itemView.ivReviewUser
        val tvReviewText = holder.itemView.tvReviewText
        val tvReviewName = holder.itemView.tvReviewName
        val tvReviewDateCreated = holder.itemView.tvReviewDateCreated
        val ratingBarReview = holder.itemView.ratingBarReview

        Glide.with(context).load(review.user.imageUrl)
            .apply(RequestOptions().transform(CenterCrop())).into(ivReviewUser)
        tvReviewText.text = review.text
        tvReviewName.text = review.user.name
        tvReviewDateCreated.text = review.dateCreated
        ratingBarReview.rating = review.rating.toFloat()
    }

    override fun getItemCount() = reviews.size
}
