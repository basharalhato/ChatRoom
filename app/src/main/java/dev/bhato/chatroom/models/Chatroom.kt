package dev.bhato.chatroom.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chatroom(
    val chatroom_id: String = "",
    val title: String = "",
    val image: String = ""
) : Parcelable