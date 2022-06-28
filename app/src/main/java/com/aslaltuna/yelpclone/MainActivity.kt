package com.aslaltuna.yelpclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    companion object {
        private const val BASE_URL = "https://api.yelp.com/v3/"
        private const val TAG = "MainActivity"
        private const val API_KEY = "1L98zM7hcor-ZHfs75XbyxUea4EQLVgV-dGtopovG0KJV55RRu8nI_lPC9HNlFsF1DmR9urJoWs0uMuenpzDPffkVWZTqEj1okmsryy4DhzpWH8_i_AeA-hNqGe5YnYx"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantsAdapter(this, restaurants)

        rvRestaurants.adapter = adapter
        rvRestaurants.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

        val yelpService = retrofit.create(YelpService::class.java)

        yelpService.searchRestaurants("Bearer $API_KEY","Avocado Toast", "New York").enqueue(object: Callback<YelpSearchResult> {
            override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                Log.i(TAG, "onResponse $response")
                val body = response.body()
                
                if (body == null) {
                    Log.w(TAG, "Invalid data!")
                    return
                } else {
                    restaurants.addAll(body.restaurants)
                    adapter.notifyItemInserted(restaurants.size - 1)
                }
            }

            override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }

        })


    }
}