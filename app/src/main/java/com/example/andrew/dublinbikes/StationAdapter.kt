package com.example.andrew.dublinbikes

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*


class StationAdapter : RecyclerView.Adapter<StationAdapter.ViewHolder>() {
    //List of stations
    var stations: MutableList<Station> = ArrayList()

    private val mAdapter: StationAdapter? = null

    internal fun addStation(station: Station) {
        stations.add(station)
    }

    internal fun addAll(list: MutableList<Station>) {
        stations = list
    }

    internal fun sort() {
        stations.sort()
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val station = stations[position]

        viewHolder.mTitleView.text = station.getName()
        //viewHolder.mSubTitleView.setText(station.getAddress());
        viewHolder.mNumberView.text = station.getAvailableBikes().toString() + ""
        viewHolder.mSubNumberView.text = station.getAvailableParking().toString() + ""

        viewHolder.mView.setOnClickListener { view ->
            //            val myIntent = Intent(view.context, MapsActivity::class.java)
//                myIntent.putExtra(STATION_ID, station.getNumber())
//                myIntent.putExtra(STATION_LNG, station.getLng())
//                myIntent.putExtra(STATION_LAT, station.getLat())
//            startActivity(view.context, myIntent, null)
        }

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

}