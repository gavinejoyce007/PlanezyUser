package com.planezy.planezyuserapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class ImageListing extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    GoogleApiClient mGoogleApiClient;
    String log;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_listing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());



        Bundle b1 = getIntent().getExtras();
        String name="",email="";
        if(b1!=null)
        {
            name = b1.getString("UserName");
            email = b1.getString("UserEmail");
            log = b1.getString("Provider");
        }



        Intent intent = getIntent();
        String image = intent.getStringExtra("UserPhoto");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        ImageView img = (ImageView)header.findViewById(R.id.imageView);
        TextView user = (TextView)header.findViewById(R.id.userName);
        TextView userID = (TextView)header.findViewById(R.id.userMail);
        user.setText(name);
        userID.setText(email);

        if(image!=null) {
            Uri uphoto = Uri.parse(image);
            Context context = img.getContext();
            Picasso.with(context).load(uphoto).into(img);
        }else{
            img.setImageDrawable(getResources().getDrawable(R.drawable.sample));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_listing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            if(log.equals("Google")) {
                MyGoogleApiClient_Singleton gac = new MyGoogleApiClient_Singleton();
                gac.getInstance(null);
                mGoogleApiClient = gac.get_GoogleApiClient();
                mGoogleApiClient.connect();
                mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                Toast.makeText(ImageListing.this, "G+ Logged out", Toast.LENGTH_SHORT).show();
                                Intent in = new Intent(ImageListing.this, MainActivity.class);
                                log = "Google";
                                Bundle b1 = new Bundle();
                                b1.putString("log",log);
                                in.putExtras(b1);
                                startActivity(in);
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                });
            }else if(log.equals("Facebook")) {
                Toast.makeText(ImageListing.this, "FB Logged out", Toast.LENGTH_SHORT).show();
                log = "Facebook";
                Intent in = new Intent(ImageListing.this, MainActivity.class);
                Bundle b1 = new Bundle();
                b1.putString("log", log);
                in.putExtras(b1);
                startActivity(in);
                LoginManager.getInstance().logOut();
                startActivity(in);
            }
            else if(log.equals("Firebase")){
                mAuth.signOut();
                Toast.makeText(ImageListing.this, "Logged out", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(ImageListing.this, MainActivity.class);
                startActivity(in);

            }
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
