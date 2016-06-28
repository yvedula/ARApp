package com.teamar.cmu.arapp;

import android.Manifest;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity to display a list of all POIs in a ListView.
 * This is the activity that the user first sees when
 * the app is launched.
 */
public class ListPOIActivity extends ListActivity {


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    /**
     * Progress dialog to show that information is being fetched.
     */
    private ProgressDialog pDialog;

    /**
     * Variable to store the number of POIs returned.
     */
    int numElements;
    /**
     * List of all POIs on the server.
     */
    private ArrayList<POI> poiList = new ArrayList<POI>();

    /**
     * Tag for displaying logcat messages.
     */
    public static final String REQUEST_TAG = "ListPOIActivity";

    /**
     * The URL for the web service with the list of all POIs.
     */
    //public static final String POIS_URL = "https://example.wikitude.com/GetSamplePois/";
    //public static final String POIS_URL = "http://ec2-54-209-186-152.compute-1.amazonaws.com:7001/v1/pois";
    public static final String POIS_URL = "http://placmakarapi.cf:7001/v1/pois/";
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_poi);

//        checkCameraPermissions();
//        checkLocationPermissions();
//        checkStoragePermissions();

        checkAndRequestPermissions();

        /**
         * Setting up the progress dialog.
         */
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);



    }

    /**
     * Function called when an item from the list is clicked on.
     * @param l : The list view being displayed
     * @param v : The view containing the list view
     * @param position : The index of the item being clicked
     * @param id : A unique ID
     */
    protected void onListItemClick(final ListView l, final View v, final int position, final long id) {

        /**
         * Stores the information of the POI selected from the list view.
         */
        POI selectedPOI = poiList.get(position);

        /**
         * Name of the selected POI.
         */
        String poiName = selectedPOI.getPoiName();
        /**
         * Description of the selected POI.
         */
        String poiDescription = selectedPOI.getDescription();
        /**
         * Stores the unique ID of the POI selected.
         */
        int poiID = selectedPOI.getPoiID();

        /**
         * Creating an intent to start a new activity with details on the POI.
         */
        Intent intent = new Intent(this, POIDescription.class);
        intent.putExtra("poi_id", poiID);
        startActivity(intent);
    }



    @Override
    protected void onStart() {
        super.onStart();

        makeJsonArrayRequest(POIS_URL);


    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    private  boolean checkAndRequestPermissions() {
        int permissionUseCamera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int storagePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionUseCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

//    public void checkCameraPermissions() {
//
//        // Here, thisActivity is the current activity
//        if (ContextCompat.checkSelfPermission(ListPOIActivity.this,
//                android.Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//
//
//            ActivityCompat.requestPermissions(ListPOIActivity.this,
//                    new String[]{android.Manifest.permission.CAMERA},
//                    1);
//
//
//        }
//    }
//
//    public void checkLocationPermissions() {
//        // Here, thisActivity is the current activity
//        if (ContextCompat.checkSelfPermission(ListPOIActivity.this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//
//            // No explanation needed, we can request the permission.
//
//            ActivityCompat.requestPermissions(ListPOIActivity.this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    2);
//
//        }
//    }
//
//    public void checkStoragePermissions(){
//        if (ContextCompat.checkSelfPermission(ListPOIActivity.this,
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//
//
//            // No explanation needed, we can request the permission.
//
//            ActivityCompat.requestPermissions(ListPOIActivity.this,
//                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    3);
//
//
//        }
//
//    }

    /**
     * Method to make JSON array request where response starts with '[' .
     * @param urlJsonArry : The string containing the JSON array returned by the web request
     */
    public int makeJsonArrayRequest(final String urlJsonArry) {

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
                            numElements = response.length();
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject person = (JSONObject) response
                                        .get(i);

                                String name = person.getString("poiName");

                                String description = person.getString("description");
                                String id = person.getString("poiID");
                                POI newPOI;
                                try {
                                    newPOI = new POI(Integer.parseInt(id));
                                    poiNames[i] = name;
                                    newPOI.setPoiName(name);
                                    newPOI.setDescription(description);
                                    poiList.add(newPOI);
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListPOIActivity.this, android.R.layout.simple_list_item_1, poiNames);
                                    setListAdapter(adapter);
                                } catch (NumberFormatException e) {
                                    displayToast("Invalid POI data encountered");
                                }
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
        return numElements;

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

    /**
     * Function to display a toast.
     * @param str : String to be displayed.
     */
    public void displayToast(final String str) {
        Toast.makeText(ListPOIActivity.this, str, Toast.LENGTH_SHORT).show();
    }
}
