package com.aslaltuna.yelpclone.models

import com.aslaltuna.yelpclone.models.Place
import java.io.Serializable

data class ListPlaces(val places: List<Place>) : Serializable
