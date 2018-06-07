package com.example.andrew.dublinbikes

import org.json.JSONException
import org.json.JSONArray
import org.json.JSONObject
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*


class StationAdapter : RecyclerView.Adapter<StationAdapter.ViewHolder>() {
    //List of stations
    var stations: ArrayList<Station> = ArrayList()

    private var backupStations: List<Station> = ArrayList()

    private val mAdapter: StationAdapter? = null

    internal fun addStation(station: Station) {
        stations.add(station)
    }

    internal fun removeStation(position: Int) {
        stations.removeAt(position)
    }

    internal fun getStation(position: Int): Station {
        return stations[position]
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        //        Here we setup an empty station_item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.station_item, parent, false)

        // set the view's size, margins, paddings and layout parameters
        //        ...
        //        here we create a VieWHolder, and pass it the above view
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val station = stations[position]

        viewHolder.mTitleView.text = station.getName()
        //        viewHolder.mSubTitleView.setText(station.getAddress());
        viewHolder.mNumberView.text = station.getAvailableBikes().toString() + ""
        viewHolder.mSubNumberView.text = station.getAvailableParking().toString() + ""

        viewHolder.mView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val myIntent = Intent(view.getContext(), MapsActivity::class.java)
//                myIntent.putExtra(STATION_ID, station.getNumber())
//                myIntent.putExtra(STATION_LNG, station.getLng())
//                myIntent.putExtra(STATION_LAT, station.getLat())
                view.getContext().startActivity(myIntent)

            }
        })

    }


    override fun getItemCount(): Int {
        return stations.size
    }

    class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        // each data item is just a string in this case
        var mTitleView: TextView
        var mNumberView: TextView
        var mSubNumberView: TextView


        init {
            //            mTextView = v;
            mTitleView = mView.findViewById(R.id.station_item_title)
            mNumberView = mView.findViewById(R.id.station_item_bikes)
            mSubNumberView = mView.findViewById(R.id.station_item_parking)

        }
    }

    fun parseJsonResponse(result: String) {

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
                if (station != null) {
                    //This is where we add a particular station into the dataset that backups the array adapter
                    try {
                        mAdapter!!.addStation(station)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        return
                    }

                }
            }
//            Collections.sort(backupStations = mAdapter!!.stations, object : Comparator<Station>() {
//                fun compare(name: Station, name2: Station): Int {
//                    val s1 = name.getName()
//                    val s2 = name2.getName()
//                    return s1!!.compareTo(s2!!, ignoreCase = true)
//                }
//            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


}