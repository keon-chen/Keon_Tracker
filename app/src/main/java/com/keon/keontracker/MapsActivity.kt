package com.keon.keontracker

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import android.widget.TextView
import android.Manifest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.keon.keontracker.databinding.ActivityMapsBinding


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //KC261018+s
    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 1001
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationText: TextView
    //KC261018+e

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //KC261018+s
        locationText = findViewById(R.id.locationText)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //KC261018+e

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

        /* KC261018-s
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
         KC261018-e */
        //KC261018+s
        // 檢查並啟用定位
        enableMyLocation()
        //KC261018+e
    }

    //KC261018+s
    private fun enableMyLocation() {
        // 1. 檢查是否已經取得定位權限
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) &&
            (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            // 2. 如果沒有權限，向使用者發起請求

            ActivityCompat.requestPermissions(
                this,
                arrayOf<String?>(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }

        // 3. 已經有權限，啟用 Google Maps 內建的藍色定位點與右上角定位按鈕
        mMap.setMyLocationEnabled(true)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    moveToLocation(location)
                } else {
                    locationText.text = getString(R.string.location_unavailable)
                }
            }
            .addOnFailureListener {
                locationText.text = getString(R.string.location_unavailable)
            }

    }

    private fun moveToLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, 17f)
        )

        locationText.text = getString(
            R.string.location_format,
            location.latitude,
            location.longitude,
            location.accuracy
        )
    }

    // 4. 處理使用者點擊「允許」或「拒絕」權限後的結果
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 使用者點選允許，啟用定位
                enableMyLocation()
            }
        }
    }
    //KC261018+e
}