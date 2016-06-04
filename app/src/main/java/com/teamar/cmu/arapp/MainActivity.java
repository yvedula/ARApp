package com.teamar.cmu.arapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.StartupConfiguration;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private ArchitectView architectView;
    protected Location lastKnownLocaton;
    protected Location poiLocation;
    Button bLoc, bPOIs;


    protected double latitude = 40.458438;
    protected double longitude = -79.928995;

    String imageToRender = "4c9aLKBKi.jpeg";
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;



    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //displayLocation();
        poiLocation = new Location("POI");
        poiLocation.setLatitude(latitude);
        poiLocation.setLongitude(longitude);
        bLoc = (Button) findViewById(R.id.buttonLocation);
        bPOIs = (Button) findViewById(R.id.buttonPOIs);
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    1);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
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


//        this.architectView = (ArchitectView) this.findViewById(R.id.architectView);
//        final StartupConfiguration config = new StartupConfiguration("N78dKe/7vwHCJYIW2k0heOaNYyIE5EX2NiBrtYJhFMAaEBLdX0RrD7WoYMfKLlr0czbU5IL6Jl54+VmCFcp4/aKVIozWqGe2FvoFLHhEhkxopx3qZeDkLQ+fKoLBZQ79A+3UXn9FBxSg8aE3hRdcyww80nTy3pKV1lzDT02GKfdTYWx0ZWRfX02mWSHVrgDw49S/vdK7jaaJqdRKfADZwA81pc62IoswUzkEuPe8l6Nle8EsCOs9eU1j+hFSz0FT5gY/zunGpEihbdLtR6bTGnp+Qy18PbeIQZrP2NHAVPl/ksaBVx1BWMt6xmorTmrFICITgDYFFyUPCIBLpo7ZrdErZA25Oi3nmtskFnxNlyGLij4lOCEV6phC7xDbItlSmtNG3pYxurRqfbC18Z2/deIYR07aRRHYloD1JKPn4BT5dbJxwsUhsFJvtkrVPFXYqM0+MJveDgZD3LuovqhX/EFIWzhK3E9w55sbP+p+UgFUDaaH8D7VLtyN4D1P2QO5WnaKPIzchn1njnECeF6JuN0mRAQ1Y0HfpLaCInLAA4RH/6zHvx0jzC/AtKZMBHgtbrznLEmqS757FYvkf+jEuDPY1ISUzyPQ4JHQM8eaD1eGGT+UfgV9AkhKddTHIeXuv9iL0L7aE8OTXZD19H6BhNq/1JG1mMvdyo10z85fLO4=");
//        this.architectView.onCreate(config);


        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

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
                displayLocation();
                try {
                    architectView.setLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1f);
                    Toast.makeText(MainActivity.this, ""+mLastLocation.distanceTo(poiLocation), Toast.LENGTH_SHORT).show();
                    if(mLastLocation.distanceTo(poiLocation) < 15000)
                        architectView.load("file:///android_asset/2D_rendering/index.html?url='"+imageToRender+"'");
                    else
                        architectView.load("file:///android_asset/selecting/index.html?lat="+poiLocation.getLatitude()+"&lon="+poiLocation.getLongitude());
                    //Toast.makeText(MainActivity.this, "Asset Loaded", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        });
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

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        try {
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
     * Method to display the location on UI
     * */
    @TargetApi(Build.VERSION_CODES.M)
    private void displayLocation() {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
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
    public void onLocationChanged(Location location) {
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
}
