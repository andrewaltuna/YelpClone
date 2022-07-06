package com.aslaltuna.yelpclone.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aslaltuna.yelpclone.*
import com.aslaltuna.yelpclone.adapters.ReviewsAdapter
import kotlinx.android.synthetic.main.fragment_reviews.*

class ReviewsFragment : Fragment() {

    companion object {
        private const val TAG = "ReviewsFragment"

        fun newInstance(review: YelpRestaurantReviewList): ReviewsFragment {
            val fragment = ReviewsFragment()
            val args = Bundle()
            args.putSerializable("REVIEW", review)
            fragment.arguments = args
            Log.i(TAG, "Sent!")
            return fragment
        }
    }

    private lateinit var restaurantReviewList: YelpRestaurantReviewList
    private lateinit var restaurantReview: MutableList<YelpRestaurantReview>
    private lateinit var reviewsAdapter: ReviewsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        restaurantReviewList = requireArguments().getSerializable("REVIEW") as YelpRestaurantReviewList
        restaurantReview = mutableListOf()
        restaurantReview.addAll(restaurantReviewList.reviews)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reviews, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity

        if (activity != null) {
            reviewsAdapter = ReviewsAdapter(activity.applicationContext, restaurantReview)
            rvReviews.adapter = reviewsAdapter
            rvReviews.layoutManager = LinearLayoutManager(activity.applicationContext)
            rvReviews.addItemDecoration(DividerItemDecoration(rvReviews.context, 1))
        }

    }

}