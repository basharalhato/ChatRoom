package dev.bhato.chatroom.models

import com.google.firebase.firestore.GeoPoint
import dev.bhato.chatroom.models.User

data class UserLocation(
    var geo_point: GeoPoint? = null,
    val timestamp: Long = 0L,
    val user: User? = null
)