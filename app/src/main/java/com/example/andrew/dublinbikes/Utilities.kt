package com.example.andrew.dublinbikes

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by Andy on 18/02/2017.
 */

object Utilities {
    fun readFromFile(context: Context): String {

        var ret = ""

        try {
            val inputStream = context.openFileInput("stationArrayList.json")

            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = null;
                val stringBuilder = StringBuilder()

                while ({ receiveString = bufferedReader.readLine(); receiveString }() != null) {
                    stringBuilder.append(receiveString)
                }

                inputStream.close()
                ret = stringBuilder.toString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: " + e.toString())
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: " + e.toString())
        }

        return ret
    }
}