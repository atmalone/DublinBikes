package com.andrewmalone.assignmentapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.example.andrew.dublinbikes.R
import com.example.andrew.dublinbikes.R.id.bikeListView
import com.example.andrew.dublinbikes.Station
import com.example.andrew.dublinbikes.StationAdapter
import okhttp3.*

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.Objects

class ListActivity : AppCompatActivity() {

    private var mAdapter: StationAdapter? = null
    private var backupStations: List<Station> = ArrayList<Station>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_list)

        val toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Objects.requireNonNull<ActionBar>(supportActionBar).setTitle("City Bikes")

        val mRecyclerView = findViewById(bikeListView)
        mRecyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(this)
        mRecyclerView.setLayoutManager(mLayoutManager)

        mAdapter = StationAdapter()
        //ListView is empty while awaiting stationArrayList info from run()

        mRecyclerView.setAdapter(mAdapter)
        run()
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
                    mAdapter!!.addStation(station)
                }
            }
//            Collections.sort<Station>(backupStations = mAdapter!!.stations, Comparator<Any> { name, name2 ->
//                val s1 = name.getName()
//                val s2 = name2.getName()
//                s1.compareTo(s2, ignoreCase = true)
//            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    fun run() {
        val client = OkHttpClient()

        val request = Request.Builder().url("https://api.jcdecaux.com/vls/v1/stations?contract=Dublin&apiKey=bd691853cab2e508f00c0fea04bd3599d1ba42e5")
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
                    writeToFile(result, applicationContext)
                    updateAdapter()

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.search)
        val refreshItem = menu.findItem(R.id.refresh)


        refreshItem.setOnMenuItemClickListener {
            mAdapter!!.notifyDataSetChanged()
            true
        }

        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && !newText.isEmpty()) {
                    mAdapter!!.stations = ArrayList<Station>(backupStations)

                    val stations = ArrayList<Station>(backupStations)
                    for (item in stations) {
                        if (!item.getName()!!.contains(newText.toUpperCase())) {
                            mAdapter!!.stations.remove(item)
                            mAdapter!!.notifyDataSetChanged()
                        }
                    }
                } else {
                    mAdapter!!.stations = ArrayList<Station>(backupStations)
                    mAdapter!!.notifyDataSetChanged()
                }

                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    internal fun updateAdapter() {
        runOnUiThread { mAdapter!!.notifyDataSetChanged() }
    }


    //Writing the json data to file
    fun writeToFile(data: String, context: Context) {
        val filename = "stationArrayList.json"
        val outputStream: FileOutputStream
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE)
            outputStream.write(data.toByteArray())
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        fun createIntent(context: Context): Intent {
            val myIntent = Intent(context, ListActivity::class.java)
            return Intent(context, ListActivity::class.java)
        }
    }


}
