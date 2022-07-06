package com.aslaltuna.yelpclone.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aslaltuna.yelpclone.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.aslaltuna.yelpclone.databinding.ActivityRestaurantsMapBinding
import com.aslaltuna.yelpclone.models.ListPlaces
import com.google.android.gms.maps.model.LatLngBounds

class RestaurantsMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityRestaurantsMapBinding
    private lateinit var listPlaces: ListPlaces

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRestaurantsMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listPlaces = intent.getSerializableExtra(EXTRA_PLACES_NEARBY) as ListPlaces

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.title = "Restaurants Near Me"
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener {
            val restaurantId = it.tag.toString()

            val intent = Intent(this@RestaurantsMapActivity, RestaurantDetailActivity::class.java)
            intent.putExtra(RESTAURANT_DETAIL, restaurantId)
            startActivity(intent)
        }

        val boundsBuilder = LatLngBounds.Builder()
        for (place in listPlaces.places) {
            val latLng = LatLng(place.latitude, place.longitude)

            boundsBuilder.include(latLng)
            mMap.addMarker(MarkerOptions().position(latLng).title(place.title).snippet("${place.category}, ${place.distance} | ${place.rating}⭐"))?.tag = place.id
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 1000, 1000, 0))
    }


}