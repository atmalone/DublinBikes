package com.example.andrew.dublinbikes

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

private val BIKE_URL = "https://api.jcdecaux.com/vls/v1/stations?contract=Dublin&apiKey=bd691853cab2e508f00c0fea04bd3599d1ba42e5"

class MapsActivity : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    lateinit var marker: Marker
    private var mBackupList: MutableList<Station> = ArrayList()
    private val stationPosition: Int = 0
    private val stationNumber: Int = 0
    private val stationByIdLat: Double = 0.toDouble()
    private val stationByIdLng: Double = 0.toDouble()
    private val mapFragment = null

    var stations = ArrayList<Station>()
    @SuppressLint("UseSparseArrays")
    private val mStationMap = HashMap<Int, Marker>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //inflate the layout for this fragment
//        val googleMap = activity!!.supportFragmentManager.findFragmentById(R.id.map) as MapView
//        googleMap.getMapAsync(this)
//        val mapFragment: SupportMapFragment? = fragmentManager!!.findFragmentById(R.id.map) as SupportMapFragment?
//        mapFragment?.getMapAsync(this)
        return inflater.inflate(R.layout.frament_maps, container, false)!!
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
    }

    fun sortStations() {
        stations.sortWith(Comparator { name, name2 ->
            val s1 = name.getName()
            val s2 = name2.getName()
            s1!!.compareTo(s2!!, ignoreCase = true)
        })
    }

    fun parseJsonResponse(result: String) {
        activity?.runOnUiThread {

            var stationJson: JSONObject

            try {
                val jsonArray = JSONArray(result) //Json array object made from string that was passed into it.
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

                    //This is where we add a particular station into the dataset that backups the array adapter
                    mBackupList.add(station)
                    val stationPosition = LatLng(station.getLat(), station.getLng())
                    val markerOption = MarkerOptions().position(stationPosition)
                            .title(station.getName())
                            .snippet("Available Bikes: " + station.getAvailableBikes()
                                    + " | " + "Parking Spaces: " +
                                    station.getAvailableParking())
                    marker = mMap!!.addMarker(markerOption)
                    mStationMap.put(station.getNumber(), marker)
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

//    fun parseJsonResponse(result: String) {
//
//        var stationJson: JSONObject
//
//        try {
//            val jsonArray = JSONArray(result) //Json array object made from string that
//            // was passed into it.
//            // Process each result in json array, decode and convert to business object
//            for (i in 0 until jsonArray.length()) {
//                try {
//                    stationJson = jsonArray.getJSONObject(i)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    continue
//                }
//
//                val station = Station()
//                station.fromJson(stationJson)
//                if (station != null) {
//                    //This is where we add a particular station into the dataset that backups the
//                    // array adapter
//
//                    stations.add(station)
//                    val stationPosition = LatLng(station.getLat(), station.getLng())
//
//                    val markerOption = MarkerOptions().position(stationPosition)
//                            .title(station.getName())
//                            .snippet("Available Bikes: " + station.getAvailableBikes()
//                                    + " | " + "Parking Spaces: " +
//                                    station.getAvailableParking())
//                    //                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bike_24px));
//
//
//                    marker = mMap!!.addMarker(markerOption)
//                    mStationMap.put(station.getNumber(), marker)
//                }
//            }
//            sortStations()
//
//        } catch (e: JSONException) {
//            e.printStackTrace()
//        }
//    }

    fun run() {
        val client = OkHttpClient()

        val request = Request.Builder().url(BIKE_URL)
                .get()
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("MAPS ACTIVITY", "Error Present")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val result = Objects.requireNonNull<ResponseBody>(response.body()).string() //Json string
                try {
                    parseJsonResponse(result) // Pass Json string into parseJsonResponse methods
//                    writeToFile(result)
//                    updateAdapter()

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
        })
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
        run()

        var point = LatLng(53.3498, -6.2603)

        if (stationNumber === -1 || stations.size <= 0) {
            point = LatLng(53.3498, -6.2603)
        } else {
            point = LatLng(stationByIdLat, stationByIdLng)

            //            markers.get(stationNumber).showInfoWindow();
            mStationMap[stationNumber]!!.showInfoWindow()

        }

        // Add a marker in Sydney and move the camera
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 14.25f))

    }
}
