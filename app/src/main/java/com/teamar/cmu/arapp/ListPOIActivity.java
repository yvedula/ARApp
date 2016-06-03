package com.teamar.cmu.arapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;


public class ListPOIActivity extends ActionBarActivity implements Response.Listener,
        Response.ErrorListener {
    public static final String REQUEST_TAG = "ListPOIActivity";
    private TextView mTextView;
    private Button mButton;
    private RequestQueue mQueue;
    CustomJSONObjectRequest jsonRequest = null;
    WebServiceRequest webServiceRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_poi);

        mTextView = (TextView) findViewById(R.id.textView);
        mButton = (Button) findViewById(R.id.button);

        webServiceRequest = new WebServiceRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
//                .getRequestQueue();
//        String url = "http://api.androidhive.info/volley/person_object.json";
//        //String url = "http://jsonplaceholder.typicode.com/posts";
//        final CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method
//                .GET, url,
//                new JSONObject(), this, this);
//        jsonRequest.setTag(REQUEST_TAG);

//        makeRequest("http://api.androidhive.info/volley/person_object.json");
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeRequest("http://api.androidhive.info/volley/person_object.json", mQueue, jsonRequest);

            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(REQUEST_TAG);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mTextView.setText(error.getMessage());
    }

    @Override
    public void onResponse(Object response) {
       // mTextView.setText("Response is: " + response);
        try {
            mTextView.setText(parseJSON("name", response));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // The following functions are for making web requests


    public void makeRequest(String url, RequestQueue queue, CustomJSONObjectRequest jsonRequest)
    {

        queue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                .getRequestQueue();
        //url = "http://api.androidhive.info/volley/person_object.json";
        //String url = "http://jsonplaceholder.typicode.com/posts";
        jsonRequest = new CustomJSONObjectRequest(Request.Method
                .GET, url,
                new JSONObject(), this, this);
        jsonRequest.setTag(REQUEST_TAG);
        queue.add(jsonRequest);
    }

    public String parseJSON(String tag, Object response) throws JSONException {
        return ((JSONObject) response).getString("name");
    }

}