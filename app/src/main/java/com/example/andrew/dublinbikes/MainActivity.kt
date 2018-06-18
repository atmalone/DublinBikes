package com.example.andrew.dublinbikes

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        run()

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun run(){

        val apiUrl = "https://api.jcdecaux.com/vls/v1/stations?contract=Dublin&apiKey=bd691853cab2e508f00c0fea04bd3599d1ba42e5"
        val client = OkHttpClient()
        val request: Request = Request.Builder().url(apiUrl)
                .get()
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val result = Objects.requireNonNull(response?.body()?.toString())

            }
            override fun onFailure(call: Call?, e: IOException?) {
                Log.d("MAINACTIVITY", "Error Present: ok http call failed")
            }
        })
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment? {
            return when(position) {
                0 -> {
                    MapsActivity()
                }
                1 -> {
                    ListFragment()
                }
                2 -> { FavouritesActivity() }
                else -> null
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when(position){
                0 -> return "MAP"
                1 -> return "LIST"
                2 -> return "FAVOURITES"
            }
            return null
        }
    }
}
