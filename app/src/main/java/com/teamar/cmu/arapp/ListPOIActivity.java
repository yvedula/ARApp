package com.teamar.cmu.arapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * Activity to display a list of all POIs in a ListView.
 * This is the activity that the user first sees when
 * the app is launched.
 */
public class ListPOIActivity extends ListActivity {


    /**
     * Progress dialog to show that information is being fetched.
     */
    private ProgressDialog pDialog;

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
    public static final String POIS_URL = "https://example.wikitude.com/GetSamplePois/";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_poi);

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
        intent.putExtra("poi_name", poiName);
        intent.putExtra("poi_description", poiDescription);
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


    /**
     * Method to make JSON array request where response starts with '[' .
     * @param urlJsonArry : The string containing the JSON array returned by the web request
     */
    private void makeJsonArrayRequest(final String urlJsonArry) {

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

                                String name = person.getString("name");

                                String description = person.getString("description");
                                String id = person.getString("id");
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
