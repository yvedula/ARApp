package com.teamar.cmu.arapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private ArrayList<POI> poisList = new ArrayList<POI>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrievePOIs();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void retrievePOIs(){

        //Defining parameters for POI-1
        int id1 = 1;
        String name1 = "Fence";
        String description1 = "Painted thing";
        double lat1 = 40.442254;
        double lon1 = -79.943441;
        //Loading the parameters into POI-1
        POI newPOI1 = new POI(id1);
        newPOI1.setPoiName(name1);
        newPOI1.setDescription(description1);
        newPOI1.setLatitude(lat1);
        newPOI1.setLongitude(lon1);
        poisList.add(newPOI1);

        //Defining parameters for POI-2
        int id2 = 2;
        String name2 = "Hunt Library";
        String description2 = "Largest Library";
        double lat2 = 40.441132;
        double lon2 = -79.943707;
        //Loading the parameters into POI-1
        POI newPOI2 = new POI(id2);
        newPOI2.setPoiName(name2);
        newPOI2.setDescription(description2);
        newPOI2.setLatitude(lat2);
        newPOI2.setLongitude(lon2);
        poisList.add(newPOI2);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set a listener for info window events.
        mMap.setOnInfoWindowClickListener(this);
        int numOfPOIs = poisList.size();

        for (int i = 0; i < numOfPOIs; i++) {
            POI currentPOI = poisList.get(i);
            final LatLng mapMarker = new LatLng(currentPOI.getLatitude(), currentPOI.getLongitude());
            mMap.addMarker(new MarkerOptions().position(mapMarker).title(currentPOI.getPoiName()).snippet(currentPOI.getDescription()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mapMarker));
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    //Default info window frame
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    //Show information on the selected POI
                    // Getting view from the layout file info_window_layout
                    View v = getLayoutInflater().inflate(R.layout.infowindow_markerclick, null);

                    // Getting the position from the marker
                    final LatLng latLng = marker.getPosition();


                    // Getting reference to the TextView to set title
                    TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

                    // Getting reference to the TextView to set description
                    TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);


                    // Setting the latitude
                    tvLat.setText("Title:" + marker.getTitle());

                    // Setting the longitude
                    tvLng.setText("Description:"+ marker.getSnippet());


                    // Returning the view containing InfoWindow contents
                    return v;

                }
            });

        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // Getting the position from the marker
        final LatLng latLng = marker.getPosition();

        Toast.makeText(MapsActivity.this, latLng.latitude+"", Toast.LENGTH_SHORT).show();
    }
}
