package com.example.andrew.dublinbikes

import org.json.JSONException
import java.lang.reflect.Array.getDouble
import org.json.JSONObject



class Station {
    private var number: Int = 0
    private var name: String? = null
    private var address: String? = null
    private var lat: Double = 0.toDouble()
    private var lng: Double = 0.toDouble()
    private var availableBikes: Int = 0
    private var availableParking: Int = 0

    fun getNumber(): Int {
        return this.number
    }

    fun getName(): String? {
        return this.name
    }

    fun getAddress(): String? {
        return this.address
    }

    fun getAvailableBikes(): Int {
        return this.availableBikes
    }

    fun getAvailableParking(): Int {
        return this.availableParking
    }

    fun getLat(): Double {
        return lat
    }

    fun getLng(): Double {
        return lng
    }

    fun fromJson(jsonObject: JSONObject) {

        // Deserialize json into object fields
        try {

            this.number = jsonObject.getInt("number")
            this.name = jsonObject.getString("name")
            this.address = jsonObject.getString("address")
            this.availableBikes = jsonObject.getInt("available_bikes")
            this.availableParking = jsonObject.getInt("available_bike_stands")
            this.lat = jsonObject.getJSONObject("position").getDouble("lat")
            this.lng = jsonObject.getJSONObject("position").getDouble("lng")

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }
}