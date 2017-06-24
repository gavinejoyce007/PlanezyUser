package com.planezy.planezyuserapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private EditText username, emailid, phone, password,cpassword;
    private TextInputLayout inputlayoutname,inputlayoutemail, inputlayoutphone, inputlayoutpassword,inputlayoutcpassword;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        inputlayoutname = (TextInputLayout) findViewById(R.id.layout_username);
        inputlayoutemail = (TextInputLayout)findViewById(R.id.layout_email);
        inputlayoutphone = (TextInputLayout)findViewById(R.id.layout_phonenumber) ;
        inputlayoutpassword = (TextInputLayout)findViewById(R.id.layout_password);
        inputlayoutcpassword = (TextInputLayout)findViewById(R.id.layout_confirm_password);
        username = (EditText)findViewById(R.id.username);
        emailid = (EditText)findViewById(R.id.emailid);
        phone = (EditText)findViewById(R.id.phone) ;
        password = (EditText)findViewById(R.id.password);
        cpassword = (EditText)findViewById(R.id.confirm_password);
        username.addTextChangedListener(new MyTextWatcher(username));
        emailid.addTextChangedListener(new MyTextWatcher(emailid));
        phone.addTextChangedListener(new MyTextWatcher(phone));
        password.addTextChangedListener(new MyTextWatcher(password));
        cpassword.addTextChangedListener(new MyTextWatcher(cpassword));
    }

    private boolean validateUserName(){
        if(username.getText().toString().trim().isEmpty()){
            inputlayoutname.setError(getString(R.string.error_invalid_username));
            requestFocus(username);
            return false;
        }
        else
            inputlayoutname.setErrorEnabled(false);
        return true;
    }

    private boolean validateEmailAddress() {
        String email = emailid.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputlayoutemail.setError(getString(R.string.error_invalid_email));
            requestFocus(emailid);
            return false;
        } else
            inputlayoutemail.setErrorEnabled(false);
        return true;
    }

    private static boolean isValidEmail(String email){
            return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePhone(){
        if(phone.getText().toString().trim().length()!=10){
            inputlayoutphone.setError(getString(R.string.error_invalid_phonenumber));
            requestFocus(phone);
            return false;
        }
        else
            inputlayoutphone.setErrorEnabled(false);
        return true;
    }

    private boolean validatePassword(){
        String passwd = password.getText().toString();
        if(passwd.length()<6) {
            inputlayoutpassword.setError(getString(R.string.error_invalid_password));
            requestFocus(password);
            return false;
        }
        if(password.getText().toString().trim().isEmpty())
        {
            inputlayoutpassword.setError(getString(R.string.error_invalid_password));
            requestFocus(password);
            return false;
        }
        else
            inputlayoutpassword.setErrorEnabled(false);
            return true;
    }

    private boolean validateCPassword(){
        String cpasswd = cpassword.getText().toString();
        String passwd = password.getText().toString();
        if(cpasswd.length()<6) {
            inputlayoutcpassword.setError(getString(R.string.error_invalid_password));
            requestFocus(cpassword);
            return false;
        }
        if(!passwd.equals(cpasswd)){
            inputlayoutcpassword.setError(getString(R.string.error_password_mismatch));
            requestFocus(cpassword);
            return false;
        }
        if(cpassword.getText().toString().trim().isEmpty())
        {
            inputlayoutcpassword.setError(getString(R.string.error_invalid_password));
            requestFocus(cpassword);
            return false;
        }
        else
            inputlayoutcpassword.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view)
    {
        if(view.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher{
        private View view;
        private MyTextWatcher(View view){
            this.view=view;
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count){
            //passwd = password.getText().toString().length();
        }

        public void afterTextChanged(Editable s) {
            switch (view.getId()){
                case R.id.username:
                    validateUserName();
                    break;
                case R.id.emailid:
                    validateEmailAddress();
                    break;
                case R.id.phone:
                    validatePhone();
                    break;
                case R.id.password:
                    validatePassword();
                    break;
                case R.id.confirm_password:
                    validateCPassword();
                    break;
            }
        }
    }

    public void SignupSubmission(View view){
        if (validateEmailAddress() && validateUserName() && validatePhone() && validatePassword() && validateCPassword()) {
            String email= emailid.getText().toString();
            String passwd = password.getText().toString();
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);

            mAuth.createUserWithEmailAndPassword(email, passwd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("MainActivity", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("MainActivity", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignupActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // ...
                        }
                    });
            Toast.makeText(SignupActivity.this, "Account created :)", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }

    }
}
