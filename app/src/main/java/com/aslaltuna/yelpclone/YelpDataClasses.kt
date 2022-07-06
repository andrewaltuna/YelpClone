package com.aslaltuna.yelpclone

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class YelpSearchResult(
    @SerializedName("total")  val total: Int,
    @SerializedName("businesses")  val restaurants: List<YelpRestaurant>,
)

data class YelpRestaurant(
    val id: String,
    val name: String,
    val rating: Double,
    val price: String,
    @SerializedName("review_count") val numReviews: Int,
    @SerializedName("distance") val distanceInMeters: Double,
    @SerializedName("image_url") val imageUrl: String,
    val categories: List<YelpCategory>,
    val location: YelpLocation,
    val coordinates: YelpCoordinates,
    val phone: String,
    val transactions: List<String>
) : Serializable {
    fun displayDistance(): String {
        val milesPerMeter = 0.000621371
        val distanceInMiles = "%.2f".format(distanceInMeters * milesPerMeter)
        return "$distanceInMiles mi"
    }
}

data class YelpRestaurantDetail(
    val name: String,
    val rating: Double,
    @SerializedName("review_count") val numReviews: Int,
    @SerializedName("display_phone") val phone: String,
    @SerializedName("image_url") val imageUrl: String,
    val location: YelpLocation,
    val categories: List<YelpCategory>,
    val price: String,
    val transactions: List<String>,
    @SerializedName("is_open") val isOpen: Boolean,
    val photos: List<String>
) : Serializable

data class YelpRestaurantReviewList(
    val reviews: List<YelpRestaurantReview>
) : Serializable

data class YelpRestaurantReview(
    val user: YelpUser,
    val rating: Double,
    val text: String,
    @SerializedName("time_created") val dateCreated: String,
) : Serializable

data class YelpCategory(
    val title: String
) : Serializable

data class YelpLocation(
    @SerializedName("address1") val address: String
) : Serializable

data class YelpCoordinates(
    val latitude: Double,
    val longitude: Double
) : Serializable

data class YelpUser(
    val name: String,
    @SerializedName("image_url") val imageUrl: String
)