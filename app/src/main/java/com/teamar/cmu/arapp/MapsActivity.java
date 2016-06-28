package com.teamar.cmu.arapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private ArrayList<POI> poisList = new ArrayList<POI>();

    /**
     * Progress dialog to show that information is being fetched.
     */
    private ProgressDialog pDialog;

    public static final String POIS_URL = "http://placmakarapi.cf:7001/v1/pois/";

    HashMap<String, POI> markersAndPOIs = new HashMap<String, POI>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //retrievePOIs();

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

    }

    public void loadMap(GoogleMap googleMap) {
        mMap = googleMap;

        // Set a listener for info window events.
        mMap.setOnInfoWindowClickListener(this);
        int numOfPOIs = poisList.size();

        for (int i = 0; i < numOfPOIs; i++) {
            POI currentPOI = poisList.get(i);
            final LatLng mapMarker = new LatLng(currentPOI.getLatitude(), currentPOI.getLongitude());

            Marker m = mMap.addMarker(new MarkerOptions().position(mapMarker).title(currentPOI.getPoiName()).snippet(currentPOI.getDescription()));
            markersAndPOIs.put(m.getId(), currentPOI);
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
                    tvLng.setText("Description:" + marker.getSnippet());


                    // Returning the view containing InfoWindow contents
                    return v;

                }
            });

        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //loadMap(googleMap);
        makeJsonArrayRequest(POIS_URL, googleMap);
    }

    @Override
    protected void onStart() {
        super.onStart();


        //retrievePOIs();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // Getting the position from the marker
        final LatLng latLng = marker.getPosition();

        //Toast.makeText(MapsActivity.this, markersAndPOIs.get(marker.getId()).getPoiID()+"", Toast.LENGTH_SHORT).show();
        int poiID = markersAndPOIs.get(marker.getId()).getPoiID();
        Intent intent = new Intent(this, POIDescription.class);
        intent.putExtra("poi_id", poiID);
        startActivity(intent);
    }


    /**
     * Method to make JSON array request where response starts with '[' .
     * @param urlJsonArry : The string containing the JSON array returned by the web request
     */
    public void makeJsonArrayRequest(final String urlJsonArry, final GoogleMap googleMap) {

        showpDialog();


        JsonArrayRequest req = new JsonArrayRequest(urlJsonArry,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(final JSONArray response) {
                        Log.d("TAG", response.toString());

                        try {
                            /**
                             * Parsing json array response
                             * loop through each json object
                             **/
                            String jsonResponse = "";
                            String[] poiNames = new String[response.length()];

                            for (int i = 0; i < response.length(); i++) {

                                JSONObject person = (JSONObject) response
                                        .get(i);

                                String name = person.getString("poiName");

                                String description = person.getString("description");
                                String id = person.getString("poiID");
                                String latitude = person.getString("latitude");
                                String longitude = person.getString("longitude");
                                Log.d("LAT", latitude);
                                POI newPOI;
                                try {
                                    newPOI = new POI(Integer.parseInt(id));
                                    poiNames[i] = name;
                                    newPOI.setPoiName(name);
                                    newPOI.setDescription(description);
                                    newPOI.setLongitude(Double.parseDouble(longitude));
                                    newPOI.setLatitude(Double.parseDouble(latitude));
                                    poisList.add(newPOI);

                                } catch (NumberFormatException e) {
                                    displayToast("Invalid POI data encountered");
                                }
                                loadMap(googleMap);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);


    }

    /**
     * Function to show the progress dialog.
     */
    private void showpDialog() {
        if (!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    /**
     * Function to hide the progress dialog.
     */
    private void hidepDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }


    public void displayToast(String str)
    {
        Toast.makeText(MapsActivity.this, str, Toast.LENGTH_SHORT).show();
    }
}
