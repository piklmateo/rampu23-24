package hr.foi.rampu.eventmanager.fragments

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hr.foi.rampu.eventmanager.R
import hr.foi.rampu.eventmanager.database.eventsDAO

import java.io.IOException

class LocationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val eventsDAO = eventsDAO()
    private val markers: MutableList<MarkerOptions> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment

        // must be fixed, slows down app at startup
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        fetchAndDisplayLocations()

    }

   private fun fetchAndDisplayLocations() {
        eventsDAO.getAllLocations(
            onSuccess = { locationNames ->
                for (locationName in locationNames) {
                    addLocationToMap(locationName)
                }
                showAllMarkers()
            },
            onFailure = { exception ->
                Log.e("LocationFragment", "Error getting events", exception)
            }
        )
    }

    private fun addLocationToMap(locationName: String) {
        val geocoder = Geocoder(requireContext())

        try {
            val addressList = geocoder.getFromLocationName(locationName, 1)

            if (addressList?.isNotEmpty() == true) {
                val latitude = addressList[0].latitude
                val longitude = addressList[0].longitude
                val location = LatLng(latitude, longitude)

                val markerOptions = MarkerOptions().position(location).title(locationName)
                markers.add(markerOptions)
            } else {
                Log.e("LocationFragment", "Could not find coordinates for location: $locationName")
            }
        } catch (e: IOException) {
            Log.e("LocationFragment", "Error while getting coordinates for location: $locationName", e)
        }
    }

    private fun showAllMarkers() {
        if (markers.isNotEmpty()) {
            val firstMarkerIndex = 5

            if (firstMarkerIndex < markers.size) {
                val selectedMarker = markers[firstMarkerIndex]
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(selectedMarker.position, 7.0f)
                googleMap.animateCamera(cameraUpdate)
            } else {
                handleIndexOutOfBoundsError()
                return
            }
            for (markerOptions in markers) {
                googleMap.addMarker(markerOptions)
            }
        } else {
            handleEmptyListError()
        }
    }

    private fun handleIndexOutOfBoundsError() {
        Log.e("LocationFragment", "Index is out of bounds")
    }

    private fun handleEmptyListError() {
        Log.e("LocationFragment", "List is empty")
    }

}






