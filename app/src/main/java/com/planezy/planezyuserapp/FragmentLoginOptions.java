package com.planezy.planezyuserapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FragmentLoginOptions extends Fragment{

    private EditText emailid, password;
    private TextInputLayout inputlayoutemail, inputlayoutpassword;
    private FirebaseAuth mAuth;
    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_options_fragment,container,false);
        mAuth = FirebaseAuth.getInstance();
        emailid = (EditText)view.findViewById(R.id.emailid);
        password = (EditText)view.findViewById(R.id.password);
        inputlayoutemail = (TextInputLayout)view.findViewById(R.id.layout_email);
        inputlayoutpassword= (TextInputLayout)view.findViewById(R.id.layout_password);
        emailid.addTextChangedListener(new FragmentLoginOptions.MyTextWatcher(emailid));
        password.addTextChangedListener(new FragmentLoginOptions.MyTextWatcher(password));
        Button login = (Button)view.findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginSubmission(view);
            }
        });
        return view;
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

    private boolean validatePassword(){
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

    private void requestFocus(View view)
    {
        if(view.requestFocus()){
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {
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
    }

    public void LoginSubmission(View view) {

        if (validateEmailAddress() && validatePassword() ) {
            String email = emailid.getText().toString();
            String passwd = password.getText().toString();
            mAuth.signInWithEmailAndPassword(email, passwd)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("FragmentLogin", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                String name = user.getDisplayName();
                                String mailid = user.getEmail();
                                Bundle b2 = new Bundle();
                                b2.putString("UserName",name);
                                b2.putString("UserEmail",mailid);
                                Intent intent = new Intent(getActivity(), ImageListing.class);
                                intent.putExtras(b2);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("FragmentLogin", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getActivity(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });

        }
    }

}
