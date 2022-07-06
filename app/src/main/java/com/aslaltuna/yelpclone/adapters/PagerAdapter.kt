package com.aslaltuna.yelpclone.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aslaltuna.yelpclone.YelpRestaurantDetail
import com.aslaltuna.yelpclone.YelpRestaurantReviewList
import com.aslaltuna.yelpclone.fragments.DetailsFragment
import com.aslaltuna.yelpclone.fragments.ReviewsFragment

class PagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, private val detail: YelpRestaurantDetail, private val review: YelpRestaurantReviewList): FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DetailsFragment.newInstance(detail)
            else -> ReviewsFragment.newInstance(review)
        }
    }
}