package com.aslaltuna.yelpclone

import android.location.Location.distanceBetween
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.aslaltuna.yelpclone.databinding.ActivityRestaurantsMapBinding
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.SphericalUtil
import java.io.Serializable

data class ListPlaces(val places: List<Place>) : Serializable
data class Place(val title: String, val category: String, val latitude: Double, val longitude: Double, val rating: Double, val distance: String) : Serializable

class RestaurantsMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityRestaurantsMapBinding
    private lateinit var listPlaces: ListPlaces

//    val currentLocation = intent.getParcelableExtra<LatLng>(CURRENT_LOCATION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRestaurantsMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listPlaces = intent.getSerializableExtra(EXTRA_PLACES_NEARBY) as ListPlaces
        val currentLocation = intent.getParcelableExtra<LatLng>(CURRENT_LOCATION)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.setTitle("Restaurants Near Me")
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

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        val currentLocation = intent.getParcelableExtra<LatLng>(CURRENT_LOCATION)

        val boundsBuilder = LatLngBounds.Builder()
        for (place in listPlaces.places) {
            val latLng = LatLng(place.latitude, place.longitude)

            boundsBuilder.include(latLng)
            mMap.addMarker(MarkerOptions().position(latLng).title(place.title).snippet("${place.category}, ${place.distance} | ${place.rating}⭐"))
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 1000, 1000, 0))



    }


}