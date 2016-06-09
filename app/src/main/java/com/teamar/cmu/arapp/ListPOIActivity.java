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

public class ListPOIActivity extends ListActivity {

   // private RequestQueue mQueue;


    // Progress dialog
    private ProgressDialog pDialog;

    ArrayList<POI> poiList = new ArrayList<POI>();

    public static final String REQUEST_TAG = "ListPOIActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_poi);

        POI fence = new POI("Fence");
        POI gates = new POI("Gates");
        POI nsh = new POI("NSH");

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);






       // makeRequest("http://jsonplaceholder.typicode.com/posts", mQueue, jsonRequest);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Intent intent = new Intent(this, POIDescription.class);
        intent.putExtra("poi_name", item);
        startActivity(intent);
    }



    @Override
    protected void onStart() {
        super.onStart();

        String url = "https://example.wikitude.com/GetSamplePois/";
        makeJsonArrayRequest(url);

               // mQueue.add(jsonRequest);

    }

    @Override
    protected void onStop() {
        super.onStop();

    }



    /**
     * Method to make json array request where response starts with [
     * */
    private void makeJsonArrayRequest(String urlJsonArry) {

        showpDialog();

        JsonArrayRequest req = new JsonArrayRequest(urlJsonArry,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("TAG", response.toString());

                        try {
                            // Parsing json array response
                            // loop through each json object
                            String jsonResponse = "";
                            String[] poiNames = new String[response.length()];
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject person = (JSONObject) response
                                        .get(i);

                                String name = person.getString("name");

                                String description = person.getString("description");
                                String id = person.getString("id");

                                poiList.add(new POI(name));
                                poiNames[i] = name;
                                jsonResponse += "Name: " + name + "\n\n";
                                jsonResponse += "Description: " + description + "\n\n";
                                jsonResponse += "ID: " + id + "\n\n\n";

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>
                                        (ListPOIActivity.this, android.R.layout.simple_list_item_1, poiNames);
                                setListAdapter(adapter);

                            }

                            //displayToast(jsonResponse);

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
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("TAG", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    public void displayToast(String str)
    {
        Toast.makeText(ListPOIActivity.this, str, Toast.LENGTH_LONG).show();
    }

}
