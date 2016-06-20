package com.teamar.cmu.arapp;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

    //String[] arNames;

    ArrayList<String> arNames_al = new ArrayList<String>();
    ArrayList<Integer> arID_al = new ArrayList<Integer>();

    /**
     * The URL for the details of the selected POI.
     */
    public static final String POIS_URL = "http://ec2-54-209-186-152.compute-1.amazonaws.com:7001/v1/pois/";

    /**
     *
     * TODO This is hardcoded. Has to be fetched from the server.
     */
    String markerFile = "https://s3.amazonaws.com/testarbucket/resources/markers/magazine.wtc";
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
                intent.putExtra("poi_id", poiID);
                intent.putStringArrayListExtra("ar_names", arNames_al);
                intent.putIntegerArrayListExtra("ar_ids", arID_al);
                displayToast("ID: " + poiID);
                intent.putExtra("poi_name", poiName);
                intent.putExtra("poi_description", poiDescription);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, final long id) {


        populateDialogOnItemClick(position);
//        final Dialog dialog = new Dialog(POIDescription.this);
//        dialog.setContentView(R.layout.dialog_ar_details);
//        dialog.setTitle("Title...");
//
//        TextView text1 = (TextView) dialog.findViewById(R.id.textview_artwork);
//        TextView text2 = (TextView) dialog.findViewById(R.id.textview_description);
//        TextView text3 = (TextView) dialog.findViewById(R.id.textview_artist);
//        Button ok = (Button) dialog.findViewById(R.id.buttonOK);
//
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                dialog.dismiss();
//            }
//        });
//        String item = (String) getListAdapter().getItem(position);
//
//        text1.setText(arList.get(position).getARName());
//        text2.setText(arList.get(position).getDescription());
//        //text3.setText(arList.get(position).getArtistID());
//        text3.setText(arList.get(position).getArtistID());
//        dialog.show();
        //Toast.makeText(this, arList.get(position).getArtistID() + " selected", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();



        makeJsonObjectRequest(POIS_URL + poiID);

        displayToast(POIS_URL + poiID + "/ar");
        makeJsonArrayRequest(POIS_URL + poiID + "/ar");
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
                            arNames_al = new ArrayList<String>();
                            arID_al = new ArrayList<Integer>();
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject ar = (JSONObject) response
                                        .get(i);

                                String name = ar.getString("arName");
                                String description = ar.getString("description");
                                String artistID = ar.getString("userID");
                                int id = Integer.parseInt(ar.getString("arID"));
                                String imageURL = ar.getString("storageLocation");
                                ARContent newAR;
                                try {
                                    newAR = new ARContent(id);
                                    arNames[i] = name;
                                    arNames_al.add(name);
                                    arID_al.add(id);
                                    newAR.setARName(name);
                                    newAR.setDescription(description);
                                    newAR.setArtistID(artistID);
                                    newAR.setImageURL(imageURL);
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
     * Method to make json object request where json response starts with {
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


    /**
     * Method to make json object request where json response starts with {
     * */
    private void populateDialogOnItemClick(final int position) {

        showpDialog();
        String artistURL = "http://ec2-54-209-186-152.compute-1.amazonaws.com:7001/v1/user/"+arList.get(position).getArtistID();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                artistURL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object
                    String first_name = response.getString("firstname");
                    String last_name = response.getString("lastname");
                    //populateUI(name, description, address);
                    final Dialog dialog = new Dialog(POIDescription.this);
                    dialog.setContentView(R.layout.dialog_ar_details);
                    dialog.setTitle("Title...");

                    TextView text1 = (TextView) dialog.findViewById(R.id.textview_artwork);
                    TextView text2 = (TextView) dialog.findViewById(R.id.textview_description);
                    TextView text3 = (TextView) dialog.findViewById(R.id.textview_artist);
                    ImageView imgView = (ImageView)dialog.findViewById(R.id.image_preview);



                    Button ok = (Button) dialog.findViewById(R.id.buttonOK);



                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();
                        }
                    });
                    String item = (String) getListAdapter().getItem(position);

                    text1.setText(arList.get(position).getARName());
                    text2.setText(arList.get(position).getDescription());
                    //text3.setText(arList.get(position).getArtistID());
                    text3.setText(first_name + " " + last_name);
                    //displayToast(arList.get(position).getImageURL());
                    showpDialog();
                    new FetchImageFromURL(imgView).execute(arList.get(position).getImageURL());
                    hidepDialog();
                    dialog.show();

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

    public void checkStoragePermissions(){
        if (ContextCompat.checkSelfPermission(POIDescription.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(POIDescription.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    3);


        }

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


    private class FetchImageFromURL extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;

        public FetchImageFromURL(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
