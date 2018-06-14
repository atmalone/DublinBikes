package com.example.andrew.dublinbikes

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

import com.example.andrew.dublinbikes.Utilities.readFromFile


class MapsActivity : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var mMapView: MapView? = null
    private var mView: View? = null
    lateinit var marker: Marker

    var stations = ArrayList<Station>()
    @SuppressLint("UseSparseArrays")
    private val mStationMap = HashMap<Int, Marker>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.frament_maps)
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//                .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //inflate the layout for this fragment
        return inflater.inflate(R.layout.frament_maps,container,false)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//
//        if (mMapView !=  null)
//            mMapView.onCreate(null)
//            mMapView.onResume()
//            mMapView.getMapAsync(this)
    }

    fun sortStations() {
        stations.sortWith(Comparator { name, name2 ->
            val s1 = name.getName()
            val s2 = name2.getName()
            s1!!.compareTo(s2!!, ignoreCase = true)
        })
    }

    fun parseJsonResponse(result: String) {

        var stationJson: JSONObject

        try {
            val jsonArray = JSONArray(result) //Json array object made from string that
            // was passed into it.
            // Process each result in json array, decode and convert to business object
            for (i in 0 until jsonArray.length()) {
                try {
                    stationJson = jsonArray.getJSONObject(i)
                } catch (e: Exception) {
                    e.printStackTrace()
                    continue
                }

                val station = Station()
                station.fromJson(stationJson)
                if (station != null) {
                    //This is where we add a particular station into the dataset that backups the
                    // array adapter

                    stations.add(station)
                    val stationPosition = LatLng(station.getLat(), station.getLng())

                    val markerOption = MarkerOptions().position(stationPosition)
                            .title(station.getName())
                            .snippet("Available Bikes: " + station.getAvailableBikes()
                                    + " | " + "Parking Spaces: " +
                                    station.getAvailableParking())
                    //                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bike_24px));


                    marker = mMap!!.addMarker(markerOption)
                    mStationMap.put(station.getNumber(), marker)
                }
            }
            sortStations()

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun run() {
//        val response = readFromFile(getApplicationContext())
//        parseJsonResponse(response) // Pass Json string into parseJsonResponse methods
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

        val point = LatLng(53.3498, -6.2603)

        // Add a marker in Sydney and move the camera
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(point))

    }
}
