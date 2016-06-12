package com.teamar.cmu.arapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Activity to display details of the selected POI.
 * Also displays a list of AR available at the POI.
 */
public class POIDescription extends ListActivity {

    /**
     * Progress dialog to show that information is being fetched.
     */
    private ProgressDialog pDialog;

    /**
     * List of all POIs on the server.
     */
    private ArrayList<ARContent> arList = new ArrayList<ARContent>();
    /**
     * Variable to store the number of POIs returned.
     */
    int numElements;
    /**
     * Variable to store the name of the POI.
     */
    private String poiName;
    /**
     * Variable to store the ID of the POI.
     */
    private int poiID;
    /**
     * Variable to store the description of the POI.
     */
    private String poiDescription;
    /**
     * Identifier for the text view displaying the name of the POI.
     */
    private TextView poiNameTextView;
    /**
     * Identifier for the text view displaying the description of the POI.
     */
    private TextView poiDescriptionTextView;
    /**
     * Identifier for the text view displaying the location of the POI.
     */
    private TextView poiLocationTextView;
    private Button navigateButton;

    /**
     * The URL for the details of the selected POI.
     */
    public static final String POIS_URL = "http://ec2-54-209-186-152.compute-1.amazonaws.com:7001/v1/pois/";

    String markerFile = "https://s3.amazonaws.com/testarbucket/resources/markers/surfer.wtc";
    public static final String TAG = "POIDescription";
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poidescription);

        Intent intent = getIntent();
        poiID = intent.getIntExtra("poi_id", 0);
        poiNameTextView = (TextView) findViewById(R.id.poi_name);
        poiDescriptionTextView = (TextView) findViewById(R.id.poi_description);
        poiLocationTextView = (TextView) findViewById(R.id.poi_location);
        navigateButton = (Button) findViewById(R.id.button_start_navigation);

        /**
         * Setting up the progress dialog.
         */
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);


        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(POIDescription.this, MainActivity.class);
                intent.putExtra("poiID", poiID);
                displayToast("ID: " + poiID);
                intent.putExtra("poiName", poiName);
                intent.putExtra("poiDescription", poiDescription);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        makeJsonObjectRequest(POIS_URL + poiID);
        makeJsonArrayRequest(POIS_URL + poiID+"/ar");
        new DownloadFileFromURL().execute(markerFile);

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
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
                            String[] arNames = new String[response.length()];
                            numElements = response.length();
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject ar = (JSONObject) response
                                        .get(i);

                                String name = ar.getString("name");
                                String description = ar.getString("description");
                                String artistName = ar.getString("artist_name");
                                //String id = ar.getString("id");
                                ARContent newAR;
                                try {
                                    newAR = new ARContent(name);
                                    arNames[i] = name;
                                    newAR.setARName(name);
                                    newAR.setDescription(description);
                                    newAR.setDescription(artistName);
                                    arList.add(newAR);
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(POIDescription.this, android.R.layout.simple_list_item_1, arNames);
                                    setListAdapter(adapter);
                                } catch (NumberFormatException e) {
                                    displayToast("Invalid AR data encountered");
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
     * Method to make json object request where json response starts wtih {
     * */
    private void makeJsonObjectRequest(String urlJsonObj) {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object
                    String name = response.getString("poiName");
                    String description = response.getString("description");
                    String address = response.getString("address");
                    String markerLocation = response.getString("markerLocation");

//                    String jsonResponse = "";
//                    jsonResponse += "Name: " + name + "\n\n";
//                    jsonResponse += "Email: " + description + "\n\n";
//                    jsonResponse += "Address: " + address + "\n\n";
//                    jsonResponse += "Mobile: " + markerLocation + "\n\n";

                    populateUI(name, description, address);


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
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }

    public void populateUI(String poiName, String poiDescription, String poiLocation)
    {
        poiNameTextView.setText(poiName);
        poiDescriptionTextView.setText(poiDescription);
        poiLocationTextView.setText(poiLocation);
        //String[] pois = {"AR1", "AR2", "AR3"};

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
     * Function to display a toast for LENGTH_SHORT duration.
     * @param str : String to displayed by the toast.
     */
    public void displayToast(final String str) {
        Toast.makeText(POIDescription.this, str, Toast.LENGTH_SHORT).show();
    }


    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lengthOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/ar_images");
                myDir.mkdirs();
                Log.d(TAG, "WTC Location: "+myDir+"/tracker.wtc");
                OutputStream output = new FileOutputStream(myDir+"/tracker.wtc");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }


        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {


            // Displaying downloaded image into image view
            // Reading image path from sdcard
            //String imagePath = Environment.getExternalStorageDirectory().toString() + "/tracker.wtc";

            //displayToast("Tracker downloaded to");
            // setting downloaded into image view
            //my_image.setImageDrawable(Drawable.createFromPath(imagePath));
        }

    }
}
