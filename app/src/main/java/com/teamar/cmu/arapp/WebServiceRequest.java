package com.teamar.cmu.arapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by Yash on 03-Jun-16.
 */
public class WebServiceRequest implements Response.ErrorListener, Response.Listener<JSONObject> {

    public static final String REQUEST_TAG = "WebServiceRequest";

    public void makeRequest(String url, RequestQueue queue, CustomJSONObjectRequest jsonRequest, Context context)
    {

        queue = CustomVolleyRequestQueue.getInstance(context)
                .getRequestQueue();
        //url = "http://api.androidhive.info/volley/person_object.json";
        //String url = "http://jsonplaceholder.typicode.com/posts";
        jsonRequest = new CustomJSONObjectRequest(Request.Method
                .GET, url,
                new JSONObject(), this, this);
        jsonRequest.setTag(REQUEST_TAG);
        queue.add(jsonRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {

    }
}
