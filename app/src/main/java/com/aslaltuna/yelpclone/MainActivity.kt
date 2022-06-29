package com.aslaltuna.yelpclone

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val EXTRA_PLACES_NEARBY = "EXTRA_PLACES_NEARBY"
const val CURRENT_LOCATION = "CURRENT_LOCATION"
const val CURRENT_LATITUDE = "CURRENT_LATITUDE"
const val CURRENT_LONGITUDE = "CURRENT_LONGITUDE"

class MainActivity : AppCompatActivity() {
    companion object {
        private const val BASE_URL = "https://api.yelp.com/v3/"
        private const val TAG = "MainActivity"
        private const val API_KEY = "1L98zM7hcor-ZHfs75XbyxUea4EQLVgV-dGtopovG0KJV55RRu8nI_lPC9HNlFsF1DmR9urJoWs0uMuenpzDPffkVWZTqEj1okmsryy4DhzpWH8_i_AeA-hNqGe5YnYx"
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private var currentLat: Double = 0.0
    private var currentLong: Double = 0.0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
    private lateinit var listPlaces: ListPlaces

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantsAdapter(this, restaurants)

        rvRestaurants.adapter = adapter
        rvRestaurants.layoutManager = LinearLayoutManager(this)

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

        val yelpService = retrofit.create(YelpService::class.java)

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(searchTerm: String?): Boolean {
                if (searchTerm != null) {
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
                }
                return true
            }

            override fun onQueryTextChange(searchTerm: String?): Boolean {
                return true
            }

        })

        getNearbyRestaurants()
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
            // 3. PutExtra (List of places) to RestaurantsMapActivity
            intent = Intent(this@MainActivity, RestaurantsMapActivity::class.java)
            intent.putExtra(EXTRA_PLACES_NEARBY, listPlaces)
            intent.putExtra(CURRENT_LOCATION, LatLng(currentLat, currentLong))
//          intent.putExtra(CURRENT_LONGITUDE, currentLong)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getNearbyRestaurants() {
        // 1. Get current location and query API (lat, long, radius: 2km )
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()

        val yelpService = retrofit.create(YelpServiceNearbyRestaurants::class.java)

        yelpService.searchRestaurants("Bearer $API_KEY", currentLat, currentLong, 1000)
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
                            Place(it.name, it.categories[0].title, it.coordinates.latitude, it.coordinates.longitude, it.rating, it.displayDistance())
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
//                Toast.makeText(applicationContext, "Location Services Enabled", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(applicationContext, "Denied Location Services Access", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        Log.i(TAG, "getCurrentLocation()")
        if (checkPermission()) {
            if (isLocationEnabled()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) {
                    val location = it.result
                    if (location == null) {
                        Toast.makeText(this, "Null received", Toast.LENGTH_SHORT).show()
                    } else {
//                        Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                        currentLat = location.latitude
                        currentLong = location.longitude

                        Log.i(TAG, "$currentLat, $currentLong")
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
            PERMISSION_REQUEST_ACCESS_LOCATION)
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


}