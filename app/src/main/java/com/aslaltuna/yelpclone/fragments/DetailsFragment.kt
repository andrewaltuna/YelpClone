package com.aslaltuna.yelpclone.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aslaltuna.yelpclone.*
import com.aslaltuna.yelpclone.adapters.DetailPagerAdapter
import kotlinx.android.synthetic.main.fragment_details.*


class DetailsFragment : Fragment() {

    companion object {
        private const val TAG = "DetailsFragment"

        fun newInstance(detail: YelpRestaurantDetail): DetailsFragment {
            val fragment = DetailsFragment()
            val args = Bundle()
            args.putSerializable("RESTAURANT", detail)
            fragment.arguments = args
            Log.i(TAG, "Sent!")
            return fragment
        }
    }

    private lateinit var restaurantDetail: YelpRestaurantDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        restaurantDetail = requireArguments().getSerializable("RESTAURANT") as YelpRestaurantDetail

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "Created!")
        setViewData()
    }

    private fun setViewData() {
        val categories = restaurantDetail.categories.take(2).joinToString(", ") { it.title }
        val transactions = restaurantDetail.transactions.joinToString(", ") { it.replaceFirstChar { Char -> Char.titlecase() } }

        tvDetailName.text = restaurantDetail.name
        tvDetailRatingBar.rating = restaurantDetail.rating.toFloat()
        tvDetailNumReviews.text = "${restaurantDetail.numReviews} Reviews"
        tvDetailPriceCategory.text = "${restaurantDetail.price} • $categories"
        tvDetailTransactions.text = transactions
        tvDetailLocation.text = restaurantDetail.location.address
        tvDetailContact.text = restaurantDetail.phone

        val activity = activity
        val urlList = restaurantDetail.photos.toMutableList()

        if (activity != null) {
            viewPagerDetail.adapter = DetailPagerAdapter(activity.applicationContext, urlList.drop(1))
        }
    }
}