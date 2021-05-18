package com.example.aplikacjadlabiegacza;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShowMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference rootRef, childReference;
    LatLng testLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Bundle extras = getIntent().getExtras(); //pobieramy nazwę treningu
        //jeśli nazwa!=null to pobieramy z bazy punkty i dodajemy do mapy jako

        if (extras != null) {
            String value = extras.getString("name");
            Log.i("dane", value);
            rootRef = FirebaseDatabase.getInstance().getReference();
            childReference = rootRef.child(value);

            childReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        double longitude = Double.parseDouble(String.valueOf(ds.child("longitude").getValue()));
                        double latitude = Double.parseDouble(String.valueOf(ds.child("latitude").getValue()));
                        String steps = String.valueOf(ds.child("steps").getValue());
                        String date = String.valueOf(ds.child("date").getValue());

                        testLocation = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(testLocation).title(date + ", " + steps + " steps"));
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(testLocation, 15));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }
}