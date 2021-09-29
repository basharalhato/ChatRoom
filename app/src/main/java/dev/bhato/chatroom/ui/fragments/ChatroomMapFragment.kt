package dev.bhato.chatroom.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint
import dev.bhato.chatroom.R
import dev.bhato.chatroom.databinding.FragmentChatroomMapBinding
import dev.bhato.chatroom.models.Chatroom
import dev.bhato.chatroom.models.ClusterMarker
import dev.bhato.chatroom.models.UserLocation
import dev.bhato.chatroom.utils.ClusterManagerRenderer
import dev.bhato.chatroom.utils.Resource
import dev.bhato.chatroom.viewmodels.ChatroomViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ChatroomMapFragment : Fragment(R.layout.fragment_chatroom_map) {

    @Inject
    lateinit var auth: FirebaseAuth

    private val args: ChatroomMapFragmentArgs by navArgs()
    private val viewModel: ChatroomViewModel by activityViewModels()

    private var map: GoogleMap? = null

    private var userLocations = mutableListOf<UserLocation>()
    private var currentUserLocation: UserLocation? = null

    private lateinit var chatroom: Chatroom

    lateinit var binding: FragmentChatroomMapBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatroomMapBinding.bind(view)
        binding.mapView.onCreate(savedInstanceState)

        chatroom = args.chatroom
        setupListeners()

        getChatroomUsers()

        binding.mapView.getMapAsync {
            map = it
            setupObservers()
        }
    }

    private fun addMapMarkers(map: GoogleMap?) {
        val clusterManager = ClusterManager<ClusterMarker>(requireContext(), map)
        clusterManager.renderer = ClusterManagerRenderer(requireContext(), map, clusterManager)

        for (userLocation in userLocations) {
            val snippet = if (userLocation.user?.user_id == auth.uid) {
                "This is you"
            } else {
                "Determine route to ${userLocation.user?.username} ?"
            }

            val clusterMarker = ClusterMarker(
                LatLng(userLocation.geo_point?.latitude!!, userLocation.geo_point?.longitude!!),
                userLocation.user?.username!!,
                snippet,
                userLocation.user
            )
            clusterManager.addItem(clusterMarker)
        }
        clusterManager.cluster()
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getChatroomUsers() {
        viewModel.getChatroomUsers(chatroom.chatroom_id)
    }

    private fun setupObservers() {
        viewModel.chatroomUsersState.observe(viewLifecycleOwner, {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { userList ->
                        viewModel.getUserLocation(userList)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.userLocationsState.observe(viewLifecycleOwner, {
            userLocations = it
            getCurrentUserLocation()
            moveCameraToUser()
            addMapMarkers(map)
        })
    }

    private fun getCurrentUserLocation() {
        for (userLocation in userLocations) {
            if (userLocation.user?.user_id == auth.uid) {
                currentUserLocation = userLocation
                break
            }
        }
    }

    private fun moveCameraToUser() {
        val bottomBoundary = currentUserLocation?.geo_point?.latitude?.minus(.1)
        val leftBoundary = currentUserLocation?.geo_point?.longitude?.minus(.1)
        val topBoundary = currentUserLocation?.geo_point?.latitude?.plus(.1)
        val rightBoundary = currentUserLocation?.geo_point?.longitude?.plus(.1)
        val bounds = LatLngBounds(
            LatLng(bottomBoundary!!, leftBoundary!!),
            LatLng(topBoundary!!, rightBoundary!!)
        )
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, 0)
        )
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}