package com.teamar.cmu.arapp;

/**
 * Created by Yash on 09-Jun-16.
 */
import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Class to streamline the control for the application's asynchronous requests.
 */
public class AppController extends Application {

    /**
     * Variable to store the tagname for logcat messages originating from the application.
     */
    public static final String TAG = AppController.class.getSimpleName();

    /**
     * Request queue for the HTTP request.
     */
    private RequestQueue mRequestQueue;

    /**
     * Instance of the AppController class.
     */
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    /**
     * Function to return the instance of the app controller.
     * @return : AppController instance
     */
    public static synchronized AppController getInstance() {
        return mInstance;
    }

    /**
     * Function to retrieve the request queue.
     * @return Volley request queue
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    /**
     * Function to add a new request to the queue.
     * @param req : Request to be added to the queue
     * @param tag : Tag associated with the request
     * @param <T> : Request type
     */
    public <T> void addToRequestQueue(final Request<T> req, final String tag) {
        //req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        if (TextUtils.isEmpty(tag)) {
            req.setTag(TAG);
        } else {
            req.setTag(tag);
        }
        getRequestQueue().add(req);
    }

    /**
     * Function to add a new request to the queue.
     * @param req : Request to be added to the queue
     * @param <T> : Request type
     */
    public <T> void addToRequestQueue(final Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    /**
     * Function to cancel pending requests.
     * @param tag : Tag for request to be cancelled
     */
    public void cancelPendingRequests(final Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
