package com.planezy.planezyuserapp;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Gavine Joyce on 24/06/2017.
 */

public class MyGoogleApiClient_Singleton {
    private static final String TAG = "MyGoogleApiClient_Singleton";
    private static MyGoogleApiClient_Singleton instance = null;

    private static GoogleApiClient mGoogleApiClient = null;
    public MyGoogleApiClient_Singleton(){

    }
    public static MyGoogleApiClient_Singleton getInstance(GoogleApiClient aGoogleApiClient) {
        if(instance == null) {
            instance = new MyGoogleApiClient_Singleton();
            if (mGoogleApiClient == null)
                mGoogleApiClient = aGoogleApiClient;
        }
        return instance;
    }

    public GoogleApiClient get_GoogleApiClient(){
        return mGoogleApiClient;
    }
}
