package com.planezy.planezyuserapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    int RC_SIGN_IN = 4526;
    CallbackManager callbackManager;
    String user;
    private FirebaseAuth mAuth;
    EditText emaili,pass;
    TextInputLayout inputlayoutemail,inputlayoutpassword;
    //LayoutInflater inflator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        LoginButton fbsignIn = (LoginButton) findViewById(R.id.fb_login);
        SignInButton signInButton = (SignInButton) findViewById(R.id.google_sign_in);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        Button planlog = (Button)findViewById(R.id.login_button1);
        planlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frag();
            }
        });

        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.planezy.planezyuserapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/

        setGooglePlusButtonText(signInButton,"Continue with Google");
        callbackManager = CallbackManager.Factory.create();
        fbsignIn.setBackgroundDrawable(getResources().getDrawable(R.drawable.fblogin));

        fbsignIn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken atoken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(atoken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Bundle bFacebookData = getFacebookData(object);
                        if (bFacebookData != null) {
                            String fname = bFacebookData.getString("first_name");
                            String lname = bFacebookData.getString("last_name");
                            user = fname +" "+ lname;
                            String userEmail = bFacebookData.getString("email");
                            String userPhoto = bFacebookData.getString("profile_pic");
                            Intent in =new Intent(MainActivity.this,ImageListing.class);
                            Bundle b1 = new Bundle();
                            b1.putString("UserName",user);
                            b1.putString("UserEmail",userEmail);
                            b1.putString("UserPhoto",userPhoto);
                            b1.putString("Provider","Facebook");
                            in.putExtras(b1);
                            startActivity(in);
                        }
                        Toast.makeText(getApplicationContext(), "Welcome " + user, Toast.LENGTH_LONG).show();
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Facebook authentication cancelled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
                Toast.makeText(MainActivity.this, "Facebook authentication failed. "+error, Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.google_sign_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.google_sign_in:
                        signIn();
                        break;
                    // ...
                }
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, this/* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        MyGoogleApiClient_Singleton gc = new MyGoogleApiClient_Singleton();
        gc.getInstance(mGoogleApiClient);
    }

public void frag(){
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    LayoutInflater inflator = this.getLayoutInflater();
    final View dialogView = inflator.inflate(R.layout.login_options_fragment,null);
    builder.setView(dialogView);
    Button login = (Button)dialogView.findViewById(R.id.login_button);
    emaili = (EditText)dialogView.findViewById(R.id.emailid);
    pass = (EditText)dialogView.findViewById(R.id.password);
    inputlayoutemail = (TextInputLayout)dialogView.findViewById(R.id.layout_email);
    inputlayoutpassword= (TextInputLayout)dialogView.findViewById(R.id.layout_password);
    //emaili.addTextChangedListener(new MainActivity.MyTextWatcher(emaili));
    //pass.addTextChangedListener(new MainActivity.MyTextWatcher(pass));
    builder.setTitle("Planezy");
    //builder.setMessage("Login");
    login.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

                String email = emaili.getText().toString();
                String passwd = pass.getText().toString();

                mAuth.signInWithEmailAndPassword(email, passwd)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("FragmentLogin", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String name = user.getDisplayName();
                                    String mailid = user.getEmail();
                                    Bundle b2 = new Bundle();
                                    b2.putString("UserName", name);
                                    b2.putString("UserEmail", mailid);
                                    b2.putString("Provider","Firebase");
                                    Intent intent = new Intent(getApplicationContext(), ImageListing.class);
                                    intent.putExtras(b2);
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("FragmentLogin", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
        }
    });
    AlertDialog d = builder.create();
    d.show();
}
    /*private boolean validateEmailAddress() {
        String email = emaili.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputlayoutemail.setError(getString(R.string.error_invalid_email));
            requestFocus(emaili);
            return false;
        } else
            inputlayoutemail.setErrorEnabled(false);
        return true;
    }

    private static boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword(){
        if(pass.getText().toString().trim().isEmpty())
        {
            inputlayoutpassword.setError(getString(R.string.error_invalid_password));
            requestFocus(pass);
            return false;
        }
        else
            inputlayoutpassword.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view)
    {
        if(view.requestFocus()){
            getParent().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();

        if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Bundle bFacebookData = getFacebookData(object);
                    if (bFacebookData != null) {
                        String fname = bFacebookData.getString("first_name");
                        String lname = bFacebookData.getString("last_name");
                        user = fname + " " + lname;
                        String userEmail = bFacebookData.getString("email");
                        String userPhoto = bFacebookData.getString("profile_pic");
                        Intent in = new Intent(MainActivity.this, ImageListing.class);
                        Bundle b1 = new Bundle();
                        b1.putString("UserName", user);
                        b1.putString("UserEmail", userEmail);
                        b1.putString("UserPhoto", userPhoto);
                        b1.putString("Provider", "Facebook");
                        in.putExtras(b1);
                        startActivity(in);
                    }
                    Toast.makeText(getApplicationContext(), "+" + user, Toast.LENGTH_LONG).show();
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location");
            request.setParameters(parameters);
            request.executeAsync();
        }

            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {

                Log.d("G+ Login" +
                        "", "Got cached sign-in");
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (JSONException e) {
            Log.d("Facebook", "Error parsing JSON");
        }
        return null;
    }

    public void SignupFunc(View view){
        Intent intent = new Intent(this,SignupActivity.class);
        startActivity(intent);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("Connection", "onConnectionFailed:" + connectionResult);
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("MainAct", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            updateUI(true,result);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false,result);
        }
    }
    private void updateUI(boolean sigIn,GoogleSignInResult result)
    {
        if(sigIn)
        {
            //GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto=null;
            Intent in = new Intent(MainActivity.this, ImageListing.class);
            try {
                personPhoto = acct.getPhotoUrl();
                in.putExtra("UserPhoto", personPhoto.toString());
            }catch(NullPointerException e)
            {
                e.printStackTrace();
            }
                Toast.makeText(getApplicationContext(), "Welcome " + personName, Toast.LENGTH_LONG).show();

                Bundle b1 = new Bundle();
                b1.putString("UserName", personName);
                b1.putString("UserEmail", personEmail);
                b1.putString("Provider","Google");
                in.putExtras(b1);
                startActivity(in);

        }
        else
        {
         //Toast.makeText(getApplicationContext(),"SignIn Failure",Toast.LENGTH_LONG).show();
        }
    }
    protected void setGooglePlusButtonText(SignInButton signInbtn, String text)
    {
        for(int i=0;i<signInbtn.getChildCount();i++)
        {
            View v = signInbtn.getChildAt(i);
            if(v instanceof TextView)
            {
                TextView mtextView = (TextView)v;
                mtextView.setText(text);
                return;
            }
        }
    }
    /*private class MyTextWatcher implements TextWatcher {
        private View view;
        private MyTextWatcher(View view){
            this.view=view;
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count){
        }

        public void afterTextChanged(Editable s) {
            switch (view.getId()){
                case R.id.emailid:
                    validateEmailAddress();
                    break;
                case R.id.password:
                    validatePassword();
                    break;
            }
        }
    }*/

}


