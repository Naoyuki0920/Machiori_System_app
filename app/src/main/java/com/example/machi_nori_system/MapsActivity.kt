package com.example.machi_nori_system

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Marker
import com.example.machi_nori_system.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var latlng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
        latlng = LatLng(36.532264, 136.62770)
        addMarker()

        // Add a marker in Sydney and move the camera
        val kanazawa = LatLng(36.56330, 136.65414)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(kanazawa))
    }

    private fun setIcon(latitude: Double, longitude: Double) {
        latlng = LatLng(latitude, longitude)
        val descriptor = BitmapDescriptorFactory.fromResource(R.drawable.machinori_port)

        // 貼り付設定
        val overlayOptions = GroundOverlayOptions()
        overlayOptions.image(descriptor)
        // 画像固定位置
        overlayOptions.anchor(0.5f, 0.9f)

        // 張り付け画像の大きさ メートル単位
        // public GroundOverlayOptions	position(LatLng location, float width, float height)
        overlayOptions.position(latlng, 80f, 160f)
        overlayOptions.zIndex(10f)

        // マップに貼り付け・アルファを設定
        val overlay = mMap.addGroundOverlay(overlayOptions)
        overlay.transparency = 0.0f

    }



    private fun addMarker() {
        try {
            val data = parseJson("Machinori.json")
            val jsonObj = data.getJSONArray("Machinori")
            for (i in 0 until jsonObj.length()) {
                val central = jsonObj.getJSONObject(i)
                //                Log.d("Check", String.valueOf(central));
                val port = Port(central)
                //                setMarker(central_inter.location ,central_inter.lat, central_inter.lng);
                setIcon(port.lat, port.lng)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    class Port(json: JSONObject) {
        var id: Int
        var location: String
        var lat: Double
        var lng: Double

        init {
            id = json.getInt("id")
            location = json.getString("location")
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