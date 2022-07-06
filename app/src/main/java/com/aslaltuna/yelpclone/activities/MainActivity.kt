package com.aslaltuna.yelpclone.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aslaltuna.yelpclone.*
import com.aslaltuna.yelpclone.adapters.RestaurantsAdapter
import com.aslaltuna.yelpclone.models.ListPlaces
import com.aslaltuna.yelpclone.models.Place
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val EXTRA_PLACES_NEARBY = "EXTRA_PLACES_NEARBY"
const val CURRENT_LOCATION = "CURRENT_LOCATION"
const val RESTAURANT_DETAIL = "RESTAURANT_DETAIL"
const val BASE_URL = "https://api.yelp.com/v3/"
const val API_KEY = "1L98zM7hcor-ZHfs75XbyxUea4EQLVgV-dGtopovG0KJV55RRu8nI_lPC9HNlFsF1DmR9urJoWs0uMuenpzDPffkVWZTqEj1okmsryy4DhzpWH8_i_AeA-hNqGe5YnYx"

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var listPlaces: ListPlaces

    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    private val yelpService = retrofit.create(YelpService::class.java)

    private var currentLat: Double = 0.00
    private var currentLong: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listPlaces = ListPlaces(mutableListOf())

        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantsAdapter(this, restaurants, object: RestaurantsAdapter.RecyclerViewInterface {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@MainActivity, RestaurantDetailActivity::class.java)
                intent.putExtra(RESTAURANT_DETAIL, restaurants[position].id)
                startActivity(intent)
            }
        })

        rvRestaurants.adapter = adapter
        rvRestaurants.layoutManager = LinearLayoutManager(this)

        fun searchRestaurants(searchTerm: String) {
            if (searchTerm.length >= 3) {
                yelpService.searchRestaurants("Bearer $API_KEY", searchTerm, "New York")
                    .enqueue(object : Callback<YelpSearchResult> {
                        override fun onResponse(
                            call: Call<YelpSearchResult>,
                            response: Response<YelpSearchResult>
                        ) {
                            Log.i(TAG, "onResponse $response")
                            val body = response.body()

                            if (body == null) {
                                Log.w(TAG, "Invalid data!")
                                return
                            } else {
                                restaurants.clear()
                                restaurants.addAll(body.restaurants)
                                adapter.notifyItemInserted(restaurants.size - 1)
                                rvRestaurants.adapter = adapter
                                checkDataset()
                            }
                        }

                        override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                            Log.i(TAG, "onFailure $t")
                        }

                    })
            } else {
                restaurants.clear()
                rvRestaurants.adapter = adapter
                checkDataset()
            }
        }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            var timer: CountDownTimer? = null

            override fun onQueryTextSubmit(text: String?): Boolean {
                searchRestaurants(text.toString())
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
                timer?.cancel()
                    timer = object : CountDownTimer(1000, 1500) {
                        override fun onTick(millisUntilFinished: Long) {}
                        override fun onFinish() {
                            searchRestaurants(text.toString())
                        }
                    }.start()
                    return true
            }
        })

        getCurrentLocation()

    }

    private fun checkDataset() {
        if (rvRestaurants.adapter?.itemCount == 0) {
            tvEmpty.visibility = View.VISIBLE
        } else {
            tvEmpty.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_restaurants_map, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miMap) {

            if (listPlaces.places.isNotEmpty()) {
                intent = Intent(this@MainActivity, RestaurantsMapActivity::class.java)
                intent.putExtra(EXTRA_PLACES_NEARBY, listPlaces)
                intent.putExtra(CURRENT_LOCATION, LatLng(currentLat, currentLong))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Accessing current location. Try again in a few seconds.", Toast.LENGTH_LONG).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getNearbyRestaurants() {
        yelpService.searchNearbyRestaurants("Bearer $API_KEY", currentLat, currentLong, 1000)
            .enqueue(object : Callback<YelpSearchResult> {
                override fun onResponse(
                    call: Call<YelpSearchResult>,
                    response: Response<YelpSearchResult>
                ) {
                    Log.i(TAG, "onResponse $response")
                    val body = response.body()

                    if (body == null) {
                        Log.w(TAG, "Invalid data!")
                        return
                    } else {
                        // 2. Map nearby places to Place dataclass and assign to nearbyPlaces
                        val placesNearby = body.restaurants.map {
                            Place(
                                it.name,
                                it.categories[0].title,
                                it.coordinates.latitude,
                                it.coordinates.longitude,
                                it.rating,
                                it.displayDistance(),
                                it.id
                            )
                        }.toMutableList()

                        listPlaces = ListPlaces(placesNearby)
                    }
                }

                override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                    Log.i(TAG, "onFailure $t")
                }

            })
    }

    // GET CURRENT LOCATION

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location is enabled
                getCurrentLocation()
            } else {
                Toast.makeText(applicationContext, "Denied Location Services Access", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        Log.i(TAG, "getCurrentLocation()")
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) {
                    val location = it.result
                    if (location != null) {
                        currentLat = location.latitude
                        currentLong = location.longitude

                        getNearbyRestaurants()
                        Log.i(TAG, "Set Location: $currentLat, $currentLong")
                    }
                }
            } else {
                // Open settings app
                Toast.makeText(this, "Turn on Location Services", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            // Request for permissions
            requestPermission()
            Log.i(TAG, "requestPermission()")
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }

        return false
    }

//    @SuppressLint("MissingPermission", "SetTextI18n")
//    private fun getCurrentLocation() {
//
//
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
//                    val location: Location? = task.result
//                    if (location != null) {
//                        currentLat = location.latitude
//                        currentLong = location.longitude
//                        getNearbyRestaurants()
//                        Log.i(TAG, "$currentLat, $currentLong")
//                    }
//                }
//            } else {
//                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
//                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                startActivity(intent)
//            }
//        } else {
//            requestPermissions()
//        }
//    }
//
//    private fun isLocationEnabled(): Boolean {
//        val locationManager: LocationManager =
//            getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER
//        )
//    }
//    private fun checkPermissions(): Boolean {
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            return true
//        }
//        return false
//    }
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ),
//            permissionId
//        )
//    }
//    @SuppressLint("MissingSuperCall")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == permissionId) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                getCurrentLocation()
//            }
//        }
//    }


}