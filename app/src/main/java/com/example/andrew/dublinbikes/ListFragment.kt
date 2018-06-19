package com.example.andrew.dublinbikes

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import kotlinx.android.synthetic.main.fragment_list.view.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

private val BIKE_URL = "https://api.jcdecaux.com/vls/v1/stations?contract=Dublin&apiKey=bd691853cab2e508f00c0fea04bd3599d1ba42e5"

class ListFragment : Fragment() {

    private var mBackupList: MutableList<Station> = ArrayList()
    private var mAdapter: StationAdapter = StationAdapter()
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        mRecyclerView = view.bikeListView
        mRecyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(context)
        mRecyclerView.layoutManager = mLayoutManager
        val dividerItemDecoration = DividerItemDecoration(context,
                mLayoutManager.orientation)
        mRecyclerView.addItemDecoration(dividerItemDecoration)
        mRecyclerView.adapter = mAdapter

        setHasOptionsMenu(true)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        run()
    }

    private fun run() {
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
                    mAdapter.addStation(station)
                    mAdapter.sort()
                    mAdapter.notifyDataSetChanged()
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.search)
        val refreshItem = menu.findItem(R.id.refresh)

        refreshItem?.setOnMenuItemClickListener {
            //            mAdapter.notifyDataSetChanged()
            run()
            true
        }

        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && !newText.isEmpty()) {
                    mAdapter.stations = ArrayList(mBackupList)

                    val stations = ArrayList<Station>(mBackupList)
                    for (item in stations) {
                        if (!item.getName()!!.contains(newText.toUpperCase())) {
                            mAdapter.stations.remove(item)
                            mAdapter.sort()
                            mAdapter.notifyDataSetChanged()
                        }
                    }
                } else {
                    mAdapter.stations = ArrayList(mBackupList)
                    mAdapter.sort()
                    mAdapter.notifyDataSetChanged()
                }

                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

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
//            val myIntent = Intent(context, ListFragment::class.java)
//            return Intent(context, ListFragment::class.java)
//        }
//    }


}
