package com.aslaltuna.yelpclone.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.aslaltuna.yelpclone.*
import com.aslaltuna.yelpclone.adapters.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_restaurant_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestaurantDetailActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "RestaurantDetail"
    }

    private lateinit var restaurantId: String
    private var restaurantDetail: YelpRestaurantDetail? = null
    private var restaurantReview: YelpRestaurantReviewList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        restaurantId = intent.getSerializableExtra(RESTAURANT_DETAIL) as String

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(
            GsonConverterFactory.create()).build()

        val yelpService = retrofit.create(YelpService::class.java)

        yelpService.restaurantDetail("Bearer $API_KEY", restaurantId)
            .enqueue(object : Callback<YelpRestaurantDetail> {
                override fun onResponse(
                    call: Call<YelpRestaurantDetail>,
                    response: Response<YelpRestaurantDetail>
                ) {
                    Log.i(TAG, "onResponse $response")
                    val body = response.body()

                    if (body == null) {
                        Log.w(TAG, "Invalid data!")
                        return
                    }

                    restaurantDetail = body

                    // Set Data in Details Fragment
                    supportActionBar?.title = body.name

                    // Banner image
                    Glide.with(this@RestaurantDetailActivity).load(body.imageUrl)
                        .apply(RequestOptions().transform(CenterCrop())).into(imageViewDetail)

                    setAdapter()
                }

                override fun onFailure(call: Call<YelpRestaurantDetail>, t: Throwable) {
                    Log.i(TAG, "onFailure $t")
                }
            })

        yelpService.restaurantReview("Bearer $API_KEY", restaurantId)
            .enqueue(object : Callback<YelpRestaurantReviewList> {
                override fun onResponse(
                    call: Call<YelpRestaurantReviewList>,
                    response: Response<YelpRestaurantReviewList>
                ) {
                    Log.i(TAG, "onResponse $response")
                    val body = response.body()

                    if (body == null) {
                        Log.w(TAG, "Invalid data!")
                        return
                    }

                    restaurantReview = body
                    setAdapter()
                }

                override fun onFailure(call: Call<YelpRestaurantReviewList>, t: Throwable) {
                    Log.i(TAG, "onFailure $t")
                }
            })
    }

    private fun setAdapter() {
        if (restaurantDetail != null && restaurantReview != null) {
            val detail = restaurantDetail as YelpRestaurantDetail
            val review = restaurantReview as YelpRestaurantReviewList

            val labelList = listOf("Details", "Reviews")
            val pagerAdapter = PagerAdapter(supportFragmentManager, lifecycle, detail, review)

            viewPager.adapter = pagerAdapter

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = labelList[position]
            }.attach()

            viewPager.isUserInputEnabled = false
        }
    }
}