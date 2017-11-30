package com.lilo.lilo;

import android.app.Application;

import com.android.volley.RequestQueue;

/**
 * Created by ashis on 11/23/2017.
 */

public class MainApplication extends Application {
    public final String SERVER_URL = "http://devostrum.no-ip.info/lilo-backend";
    public RequestQueue queue;
}
