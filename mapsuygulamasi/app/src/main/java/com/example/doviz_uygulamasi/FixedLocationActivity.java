package com.example.doviz_uygulamasi;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.example.doviz_uygulamasi.databinding.ActivityFixedLocationBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class FixedLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityFixedLocationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFixedLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng fixedLocation = new LatLng(40.973797877849016, 29.244186985406174);
        mMap.addMarker(new MarkerOptions().position(fixedLocation).title("Seçili Konum"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fixedLocation, 15f));
    }
}
