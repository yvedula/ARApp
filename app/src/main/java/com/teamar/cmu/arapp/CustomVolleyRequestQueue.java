/**
 * TeamAR
 */
package com.teamar.cmu.arapp;

/**
 * Created by Yash on 03-Jun-16.
 */

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

/**
 * Custom implementation of Volley Request Queue.
 */
public final class CustomVolleyRequestQueue {


    /**
     * Defining the size of the request queue cache in MB.
     */
    static final int CACHE_IN_MB = 10;

    /**
     * Since the parameter of the DiskBasedCache constructor
     * requires cache size in bytes, we use this constant to convert.
     */
    static final int BYTE_UPGRADE_CONSTANT = 1024;

    /**
     * Declaring an instance of the CustomVolleyRequestQueue class.
     */
    private static CustomVolleyRequestQueue mInstance;

    /**
     * Declaring a global variable for storing the context of the activity.
     */
    private static Context mCtx;
    /**
     * Declaring an instance of the RequestQueue class.
     */
    private RequestQueue mRequestQueue;

    /**
     * Constructor for the current class.
     * @param context : Accepts the context of the activity
     *               invoking methods from this class.
     */
    private CustomVolleyRequestQueue(final Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    /**
     *
     * @param context : Accepts the context of the
     *               activity invoking the getInstance method.
     * @return mInstance
     */
    public static synchronized CustomVolleyRequestQueue getInstance(final Context context) {
        if (mInstance == null) {
            mInstance = new CustomVolleyRequestQueue(context);
        }
        return mInstance;
    }

    /**
     * Function to retrieve the request queue.
     * @return mRequestQueue
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), CACHE_IN_MB * BYTE_UPGRADE_CONSTANT * BYTE_UPGRADE_CONSTANT);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mRequestQueue.start();
        }
        return mRequestQueue;
    }
}
