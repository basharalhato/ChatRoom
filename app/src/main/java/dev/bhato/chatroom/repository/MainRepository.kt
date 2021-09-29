package dev.bhato.chatroom.repository

import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.StorageReference
import dev.bhato.chatroom.models.ChatMessage
import dev.bhato.chatroom.models.Chatroom
import dev.bhato.chatroom.models.User
import dev.bhato.chatroom.models.UserLocation
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MainRepository
@Inject
constructor(
    private val auth: FirebaseAuth,
    private val storageRef: StorageReference,
    private val firestoreRef: FirebaseFirestore
) {

    fun getUid() = auth.uid

    suspend fun getCurrentlyLoggedInUser(): User {
        val uid = getUid()
        val currentUserRef = uid?.let { firestoreRef.collection("Users").document(it) }
        val currentUserSnapshot = currentUserRef?.get()?.await()
        val currentUser = currentUserSnapshot?.toObject<User>()
        return currentUser!!
    }

    suspend fun registerUser(email: String, password: String): AuthResult =
        auth.createUserWithEmailAndPassword(email, password).await()

    suspend fun loginUser(email: String, password: String): AuthResult =
        auth.signInWithEmailAndPassword(email, password).await()

    fun logout() {
        auth.signOut()
    }

    suspend fun uploadProfileImage(photoUri: Uri): Uri {
        val filename = getUid()!!
        val ref = storageRef.child("ProfileImage/$filename")
        ref.putFile(photoUri).await()

        return ref.downloadUrl.await()
    }

    suspend fun saveUserToFirestore(user: User) {
        val newUserRef = firestoreRef.collection("Users").document(getUid()!!)
        newUserRef.set(user).await()
    }

    suspend fun createChatroom(name: String, uri: Uri?): Chatroom {
        val newChatroomRef = firestoreRef.collection("Chatrooms").document()
        val chatroom = createChatroomObject(newChatroomRef.id, name, uri)
        newChatroomRef.set(chatroom).await()
        return chatroom
    }

    private suspend fun createChatroomObject(
        documentId: String,
        name: String,
        uri: Uri?
    ): Chatroom {
        val uploadedUri = uri?.let {
            uploadChatroomImage(it, documentId)
        }
        return Chatroom(documentId, name, uploadedUri.toString())
    }

    private suspend fun uploadChatroomImage(uri: Uri, documentId: String): Uri {
        val ref = storageRef.child("ChatroomImage/$documentId")
        ref.putFile(uri).await()
        return ref.downloadUrl.await()
    }

    suspend fun getCurrentUserProfileImage(): String {
        return storageRef.child("ProfileImage/${getUid()}").downloadUrl.await().toString()
    }

    fun getChatrooms(): CollectionReference {
        return firestoreRef.collection("Chatrooms")
    }

    suspend fun insertChatMessage(chatroomId: String, message: String) {
        val newMessageDocumentRef = firestoreRef.collection("Chatrooms")
            .document(chatroomId).collection("Chat Messages").document()

        val user = getCurrentlyLoggedInUser()
        val chatMessage =
            ChatMessage(user, message, newMessageDocumentRef.id, System.currentTimeMillis())

        newMessageDocumentRef.set(chatMessage).await()
    }

    fun getChatMessages(chatroomId: String): CollectionReference {
        return firestoreRef.collection("Chatrooms")
            .document(chatroomId)
            .collection("Chat Messages")
    }

    suspend fun joinChatroom(chatroomId: String) {
        val joinChatroomRef = firestoreRef.collection("Chatrooms")
            .document(chatroomId).collection("User List").document(getUid()!!)

        val user = getCurrentlyLoggedInUser()
        joinChatroomRef.set(user)
    }

    suspend fun leaveChatroom(chatroomId: String) {
        val joinChatroomRef = firestoreRef.collection("Chatrooms")
            .document(chatroomId).collection("User List").document(getUid()!!)

        joinChatroomRef.delete().await()
    }

    fun getChatroomUsers(chatroomId: String): CollectionReference {
        return firestoreRef.collection("Chatrooms")
            .document(chatroomId).collection("User List")
    }

    suspend fun saveUserLocation(geoPoint: GeoPoint) {
        val user = getCurrentlyLoggedInUser()

        val locationRef = firestoreRef.collection("User Locations")
            .document(getUid()!!)
        val userLocation = UserLocation(geoPoint, System.currentTimeMillis(), user)

        locationRef.set(userLocation).await()
    }

    suspend fun getUserLocation(userId: String): UserLocation {
        val locationRef = firestoreRef.collection("User Locations")
            .document(userId)
        val userLocationSnapshot = locationRef.get().await()
        val userLocation = userLocationSnapshot.toObject<UserLocation>()

        return userLocation!!
    }
}















