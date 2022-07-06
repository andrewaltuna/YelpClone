package com.aslaltuna.yelpclone

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface YelpService {

    @GET("businesses/search")
    fun searchRestaurants(
        @Header("Authorization") authHeader: String,
        @Query("term") searchTerm: String,
        @Query("location") location: String
    ): Call<YelpSearchResult>

    @GET("businesses/search")
    fun searchNearbyRestaurants(
        @Header("Authorization") authHeader: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Int
    ): Call<YelpSearchResult>

    @GET("businesses/{id}")
    fun restaurantDetail(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
    ): Call<YelpRestaurantDetail>

    @GET("businesses/{id}/reviews")
    fun restaurantReview(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
    ): Call<YelpRestaurantReviewList>



}