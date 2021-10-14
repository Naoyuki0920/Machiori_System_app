package com.example.machi_nori_system

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.example.machi_nori_system.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import android.graphics.Bitmap

import android.graphics.BitmapFactory
import android.util.Log

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        try {
            val data = parseJson("Machinori.json")
            val jsonObj = data.getJSONArray("Machinori")
            var cnt = 0;
            for (i in 0 until jsonObj.length()) {
                val central = jsonObj.getJSONObject(i)
                val port = Port(central)
                googleMap.addMarker(
                    MarkerOptions()
                        .title(port.location)
                        .zIndex(10f)
                        .position(LatLng(port.lat, port.lng))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.machinori))
                )
                cnt++
                Log.d("debug", cnt.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val kanazawa = LatLng(36.5757632, 136.6372995)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(kanazawa))
    }

    class Port(json: JSONObject) {
        var id: Int
        var location: String
        var lat: Double
        var lng: Double

        init {
            id = json.getInt("id")
            location = json.getString("name")
            lat = json.getDouble("lat")
            lng = json.getDouble("lng")
        }
    }

    @Throws(JSONException::class, IOException::class)
    private fun parseJson(file: String): JSONObject {
        val inputStream = this.assets.open(file)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var json: JSONObject? = null
        var data: String? = ""
        var str = bufferedReader.readLine()
        while (str != null) {
            data += str
            str = bufferedReader.readLine()
        }
        json = JSONObject(data)
        inputStream.close()
        bufferedReader.close()
        return json
    }
}