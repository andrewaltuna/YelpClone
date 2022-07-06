package com.aslaltuna.yelpclone.models

import java.io.Serializable

data class Place(val title: String, val category: String, val latitude: Double, val longitude: Double, val rating: Double, val distance: String, val id: String) : Serializable
