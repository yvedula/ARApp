package com.teamar.cmu.arapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.StartupConfiguration;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * MainActivity : The activity responsible for launching a particular AR task.
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public String imgToBeDownloaded;
    public int lengthOfURL = 55;
    /**
     * The view for the Wikitude camera feed.
     */
    private ArchitectView architectView;
    /**
     * Variable to store the last known location of the device.
     */
    protected Location lastKnownLocation;
    /**
     * Variable to store the location of the POI to be visited.
     */
    protected Location poiLocation;
    /**
     * Button to display the device's current location.
     */
    private Button bLoc;
    /**
     * Button to switch activities to ListPOIActivity.
     */
    private Button bPOIs;

    /**
     * Button to view AR content.
     */
    private Button bViewAR;


    /**
     * Variable to store the URL of the image to be retrieved.
     */
    String imageToRender;// = "https://upload.wikimedia.org/wikipedia/en/thumb/e/ea/Superman_shield.svg/1280px-Superman_shield.svg.png";//"http://www.clipartbest.com/cliparts/4c9/aLK/4c9aLKBKi.jpeg";
    //String imageToRender1 = "4c9aLKBKi.jpeg";

    /**
     * Logcat tag.
     */
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * Variable to store the the number of services being resolved.
     */
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    /**
     * Variable to store the last known location of the device.
     */

    private Location mLastLocation;

    /**
     * Google client to interact with Google API.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Boolean flag to toggle periodic location updates
     */

    private boolean mRequestingLocationUpdates = true;

    /**
     * LocationRequest object.
     */
    private LocationRequest mLocationRequest;


    /**
     * Location updates intervals in seconds
     */

    ProgressDialog mProgressDialog;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    int poi_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = getIntent();
        poi_id = intent.getIntExtra("poi_id", 0);

        //displayLocation();
        poiLocation = new Location("POI");
        //poiLocation.setLatitude(latitude);
        //poiLocation.setLongitude(longitude);
        bLoc = (Button) findViewById(R.id.buttonLocation);
        bPOIs = (Button) findViewById(R.id.buttonPOIs);
        bViewAR = (Button) findViewById(R.id.bViewAR);

        checkLocationPermissions();

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
                final StartupConfiguration config = new StartupConfiguration("N78dKe/7vwHCJYIW2k0heOaNYyIE5EX2NiBrtYJhFMAaEBLdX0RrD7WoYMfKLlr0czbU5IL6Jl54+VmCFcp4/aKVIozWqGe2FvoFLHhEhkxopx3qZeDkLQ+fKoLBZQ79A+3UXn9FBxSg8aE3hRdcyww80nTy3pKV1lzDT02GKfdTYWx0ZWRfX02mWSHVrgDw49S/vdK7jaaJqdRKfADZwA81pc62IoswUzkEuPe8l6Nle8EsCOs9eU1j+hFSz0FT5gY/zunGpEihbdLtR6bTGnp+Qy18PbeIQZrP2NHAVPl/ksaBVx1BWMt6xmorTmrFICITgDYFFyUPCIBLpo7ZrdErZA25Oi3nmtskFnxNlyGLij4lOCEV6phC7xDbItlSmtNG3pYxurRqfbC18Z2/deIYR07aRRHYloD1JKPn4BT5dbJxwsUhsFJvtkrVPFXYqM0+MJveDgZD3LuovqhX/EFIWzhK3E9w55sbP+p+UgFUDaaH8D7VLtyN4D1P2QO5WnaKPIzchn1njnECeF6JuN0mRAQ1Y0HfpLaCInLAA4RH/6zHvx0jzC/AtKZMBHgtbrznLEmqS757FYvkf+jEuDPY1ISUzyPQ4JHQM8eaD1eGGT+UfgV9AkhKddTHIeXuv9iL0L7aE8OTXZD19H6BhNq/1JG1mMvdyo10z85fLO4=");
                this.architectView.onCreate(config);
            }
            catch (Exception e)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Error opening up camera");
                alertDialogBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }




        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        final Dialog dialog = new Dialog(MainActivity.this);
        //dialog.setContentView(R.layout.dialog_choose_ar);
        dialog.setTitle("Title...");

        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.dialog_choose_ar, null, false);

        dialog.setContentView(v);
        dialog.setCancelable(true);

        ListView list1 = (ListView) dialog.findViewById(R.id.listview_ar);
        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                int selected_ar = intent.getIntegerArrayListExtra("ar_ids").get(position);
                String url_1 = "http://ec2-54-209-186-152.compute-1.amazonaws.com:7001/v1/pois/" + poi_id + "/ar/" + selected_ar;
                Log.d("URL_1", url_1);
                getImageURL(url_1);

                Log.d("IMG", url_1);
//                displayToast(url);
                dialog.dismiss();

                viewAR(intent.getIntegerArrayListExtra("ar_ids").get(position));
            }
        });

        ArrayList<String> arNames_al = new ArrayList<String>();

        arNames_al = intent.getStringArrayListExtra("ar_names");
        String[] val = new String[arNames_al.size()];
        for(int i = 0; i < arNames_al.size(); i++) {
            val[i] = arNames_al.get(i);
        }
        list1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, val));
        //now that the dialog is set up, it's time to show it


        bViewAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        bPOIs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ListPOIActivity.class);
                startActivity(i);
            }
        });
        bLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locatePOI();
            }
        });



    }

    public void checkLocationPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    2);

        }
    }

    public void locatePOI(){
        try {
            architectView.setLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1f);
            architectView.load("file:///android_asset/radar/index.html?poi=" + poi_id);
            //Toast.makeText(MainActivity.this, "Asset Loaded", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void viewAR(int id){
        try {

            new DownloadImage().execute(imageToRender);

            architectView.load("file:///android_asset/2D_rendering/index.html?id="+id+"&file="+imgToBeDownloaded.substring(lengthOfURL));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
                    final StartupConfiguration config = new StartupConfiguration("N78dKe/7vwHCJYIW2k0heOaNYyIE5EX2NiBrtYJhFMAaEBLdX0RrD7WoYMfKLlr0czbU5IL6Jl54+VmCFcp4/aKVIozWqGe2FvoFLHhEhkxopx3qZeDkLQ+fKoLBZQ79A+3UXn9FBxSg8aE3hRdcyww80nTy3pKV1lzDT02GKfdTYWx0ZWRfX02mWSHVrgDw49S/vdK7jaaJqdRKfADZwA81pc62IoswUzkEuPe8l6Nle8EsCOs9eU1j+hFSz0FT5gY/zunGpEihbdLtR6bTGnp+Qy18PbeIQZrP2NHAVPl/ksaBVx1BWMt6xmorTmrFICITgDYFFyUPCIBLpo7ZrdErZA25Oi3nmtskFnxNlyGLij4lOCEV6phC7xDbItlSmtNG3pYxurRqfbC18Z2/deIYR07aRRHYloD1JKPn4BT5dbJxwsUhsFJvtkrVPFXYqM0+MJveDgZD3LuovqhX/EFIWzhK3E9w55sbP+p+UgFUDaaH8D7VLtyN4D1P2QO5WnaKPIzchn1njnECeF6JuN0mRAQ1Y0HfpLaCInLAA4RH/6zHvx0jzC/AtKZMBHgtbrznLEmqS757FYvkf+jEuDPY1ISUzyPQ4JHQM8eaD1eGGT+UfgV9AkhKddTHIeXuv9iL0L7aE8OTXZD19H6BhNq/1JG1mMvdyo10z85fLO4=");
                    this.architectView.onCreate(config);

                } else {
                        Log.d("ARApp", "Camera permissions denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case 2: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
                    final StartupConfiguration config = new StartupConfiguration("N78dKe/7vwHCJYIW2k0heOaNYyIE5EX2NiBrtYJhFMAaEBLdX0RrD7WoYMfKLlr0czbU5IL6Jl54+VmCFcp4/aKVIozWqGe2FvoFLHhEhkxopx3qZeDkLQ+fKoLBZQ79A+3UXn9FBxSg8aE3hRdcyww80nTy3pKV1lzDT02GKfdTYWx0ZWRfX02mWSHVrgDw49S/vdK7jaaJqdRKfADZwA81pc62IoswUzkEuPe8l6Nle8EsCOs9eU1j+hFSz0FT5gY/zunGpEihbdLtR6bTGnp+Qy18PbeIQZrP2NHAVPl/ksaBVx1BWMt6xmorTmrFICITgDYFFyUPCIBLpo7ZrdErZA25Oi3nmtskFnxNlyGLij4lOCEV6phC7xDbItlSmtNG3pYxurRqfbC18Z2/deIYR07aRRHYloD1JKPn4BT5dbJxwsUhsFJvtkrVPFXYqM0+MJveDgZD3LuovqhX/EFIWzhK3E9w55sbP+p+UgFUDaaH8D7VLtyN4D1P2QO5WnaKPIzchn1njnECeF6JuN0mRAQ1Y0HfpLaCInLAA4RH/6zHvx0jzC/AtKZMBHgtbrznLEmqS757FYvkf+jEuDPY1ISUzyPQ4JHQM8eaD1eGGT+UfgV9AkhKddTHIeXuv9iL0L7aE8OTXZD19H6BhNq/1JG1mMvdyo10z85fLO4=");
                    this.architectView.onCreate(config);

                } else {
                    Log.d("ARApp", "GPS permissions denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 3: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
                    final StartupConfiguration config = new StartupConfiguration("N78dKe/7vwHCJYIW2k0heOaNYyIE5EX2NiBrtYJhFMAaEBLdX0RrD7WoYMfKLlr0czbU5IL6Jl54+VmCFcp4/aKVIozWqGe2FvoFLHhEhkxopx3qZeDkLQ+fKoLBZQ79A+3UXn9FBxSg8aE3hRdcyww80nTy3pKV1lzDT02GKfdTYWx0ZWRfX02mWSHVrgDw49S/vdK7jaaJqdRKfADZwA81pc62IoswUzkEuPe8l6Nle8EsCOs9eU1j+hFSz0FT5gY/zunGpEihbdLtR6bTGnp+Qy18PbeIQZrP2NHAVPl/ksaBVx1BWMt6xmorTmrFICITgDYFFyUPCIBLpo7ZrdErZA25Oi3nmtskFnxNlyGLij4lOCEV6phC7xDbItlSmtNG3pYxurRqfbC18Z2/deIYR07aRRHYloD1JKPn4BT5dbJxwsUhsFJvtkrVPFXYqM0+MJveDgZD3LuovqhX/EFIWzhK3E9w55sbP+p+UgFUDaaH8D7VLtyN4D1P2QO5WnaKPIzchn1njnECeF6JuN0mRAQ1Y0HfpLaCInLAA4RH/6zHvx0jzC/AtKZMBHgtbrznLEmqS757FYvkf+jEuDPY1ISUzyPQ4JHQM8eaD1eGGT+UfgV9AkhKddTHIeXuv9iL0L7aE8OTXZD19H6BhNq/1JG1mMvdyo10z85fLO4=");
                    this.architectView.onCreate(config);

                } else {
                    Log.d("ARApp", "External Storage permissions denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        try {
            //architectView.setLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1f);
            architectView.onPostCreate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        architectView.onResume();
        checkPlayServices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        architectView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        architectView.onPause();
    }


    /**
     * Google api callback methods.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Method to make json object request where json response starts wtih {
     * */
    private void getImageURL(String urlJsonObj) {

        //showpDialog();



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    // Parsing json object response
                    // response will be a json object
//                    String name = response.getString("poiName");
//                    String description = response.getString("description");
//                    String address = response.getString("address");
                    //imageToRender = response.getString("storageLocation");
                    imageToRender = response.getString("storageLocation");
                    Log.d("RET1", " " + imageToRender);
                    new DownloadImage().execute(imageToRender);


                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
               // hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                // hide the progress dialog
               // hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

        Log.d("RET2", " " + imageToRender);
        //return imageToRender;
    }

    /**
     * Method to display the location on UI
     * */
    @TargetApi(Build.VERSION_CODES.M)
    private void displayLocation() {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            displayToast(latitude + ", " + longitude);

        } else {

            displayToast("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    public void displayToast(String str)
    {
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    // DownloadImage AsyncTask
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog title
            mProgressDialog.setTitle("Please wait while the image loads");
            // Set progressdialog message
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            // Show progressdialog
            mProgressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];
            imgToBeDownloaded = imageURL;
            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            //image.setImageBitmap(result);
            String filename = imgToBeDownloaded.substring(lengthOfURL);
            Log.d("IMGURL", filename);
            saveImageToFile(filename, result);
            displayToast("Image Downloaded!");
            // Close progressdialog
            mProgressDialog.dismiss();
        }
    }

    private void saveImageToFile(String fileName, Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/ar_images");
        myDir.mkdirs();
        Random generator = new Random();
//        int n = 10000;
//        n = generator.nextInt(n);
        String fname = fileName;
        File file = new File (myDir, fname);

        Log.d("IMG", file.getAbsolutePath());
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
