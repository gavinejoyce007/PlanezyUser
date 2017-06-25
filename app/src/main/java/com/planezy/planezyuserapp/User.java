package com.planezy.planezyuserapp;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Gavine Joyce on 23/06/2017.
 */

public class User {
    public GoogleApiClient getmGoogle() {
        return mGoogle;
    }

    public void setmGoogle(GoogleApiClient mGoogle) {
        this.mGoogle = mGoogle;
    }

    private GoogleApiClient mGoogle;

    public User(GoogleApiClient mGoogleApiClient){

        this.mGoogle = mGoogleApiClient;
    }
}
