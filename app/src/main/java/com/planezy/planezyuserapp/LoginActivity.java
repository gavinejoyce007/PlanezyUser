package com.planezy.planezyuserapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
  //  private int passwordNotVisible=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    private boolean validateEmailAddress(String emailAddress){
        String  expression="^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    public void LoginSubmission(View view) {
        final EditText useremailid = (EditText) findViewById(R.id.emailid);
        if (validateEmailAddress(useremailid.getText().toString().trim())) {

            Intent intent = new Intent(this, ImageListing.class);
            startActivity(intent);
        } else {
            useremailid.requestFocus();
            useremailid.setError(getString(R.string.error_invalid_email));
        }
        useremailid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                useremailid.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    /*    private void PasswordToggler()
    {
        ImageView showPassword = (ImageView) findViewById(R.id.show_password);
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText password = (EditText) findViewById(R.id.password);
                if (passwordNotVisible == 1) {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    passwordNotVisible = 0;
                } else {

                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    passwordNotVisible = 1;
                }


                password.setSelection(password.length());

            }
        });
    } */
}
