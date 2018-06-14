package com.example.andrew.dublinbikes

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_list.view.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

class ListActivity : Fragment() {

    private var mAdapter: StationAdapter? = null
    private var backupStations: List<Station> = ArrayList<Station>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mRecyclerView = view.bikeListView
        mRecyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = mLayoutManager

        mAdapter = StationAdapter()
        //ListView is empty while awaiting stationArrayList info from run()

        mRecyclerView.setAdapter(mAdapter)
    }

    override fun onStart() {
        super.onStart()
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

    private fun run() {
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
//                    writeToFile(result)
//                    updateAdapter()

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
        })
    }

//    fun onCreateOptionsMenu(menu: Menu) {
//
//        val inflater = menuInflater
//        inflater.inflate(R.menu.menu_main, menu)
//        val searchItem = menu.findItem(R.id.search)
//        val refreshItem = menu.findItem(R.id.refresh)
//
//
//        refreshItem.setOnMenuItemClickListener {
//            mAdapter!!.notifyDataSetChanged()
//            true
//        }
//
//        val searchView = searchItem.actionView as SearchView
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                if (newText != null && !newText.isEmpty()) {
//                    mAdapter!!.stations = ArrayList(backupStations)
//
//                    val stations = ArrayList<Station>(backupStations)
//                    for (item in stations) {
//                        if (!item.getName()!!.contains(newText.toUpperCase())) {
//                            mAdapter!!.stations.remove(item)
//                            mAdapter!!.notifyDataSetChanged()
//                        }
//                    }
//                } else {
//                    mAdapter!!.stations = ArrayList<Station>(backupStations)
//                    mAdapter!!.notifyDataSetChanged()
//                }
//
//                return true
//            }
//        })
//
//        return super.onCreateOptionsMenu(menu)
//    }

//    internal fun updateAdapter() {
//        this@ListActivityrunOnUiThread(Runnable { mAdapter!!.notifyDataSetChanged() })
//    }


    //Writing the json data to file
//    fun writeToFile(data: String) {
//        val filename = "stationArrayList.json"
//        val outputStream: FileOutputStream
//        try {
//            outputStream = openFileOutput(filename, Context.MODE_PRIVATE)
//            outputStream.write(data.toByteArray())
//            outputStream.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//    companion object {
//
//        fun createIntent(context: Context): Intent {
//            val myIntent = Intent(context, ListActivity::class.java)
//            return Intent(context, ListActivity::class.java)
//        }
//    }


}
