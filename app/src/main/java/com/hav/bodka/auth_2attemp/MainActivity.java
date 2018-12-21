package com.hav.bodka.auth_2attemp;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser = null;

    //UI
    private EditText mEmail, mPassword;
    private Button btnSignIn, btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //DECLARE UI BUTTONS AND TEXTS
        mEmail = (EditText) findViewById(R.id.email_field);
        mPassword = (EditText) findViewById(R.id.password_field);
        btnSignIn = (Button) findViewById(R.id.sign_in_btn);
        btnRegister = (Button) findViewById(R.id.register_btn);

        mAuth = FirebaseAuth.getInstance();



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if(!email.equals("") && !password.equals(""))
                {
                    //mAuth.createUserWithEmailAndPassword(email,password);
                    Register(email,password);
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if(!email.equals("") && !password.equals(""))
                {
                    SignIn(email,password);
                }
            }
        });

    }

    public void updateUI(FirebaseUser currentUser)
    {

        if(currentUser != null)
        {
            Intent intent = new Intent(this, LogedInActivity.class);
            //EditText editText = (EditText)findViewById(R.id.emailText);
            //editText.setText(currentUser.getEmail().toString());
            //String message = editText.getText().toString();
            intent.putExtra("email",currentUser.getEmail().toString());
            startActivity(intent);
        }
        else
        {
            return;
        }
    }







    public void Register(String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(),"Success registration, try sign in :)))",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "-----------------------------------------createUserWithEmail:success");
                    currentUser = null;
                }else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getApplicationContext(),"Failed registration, try again :)",Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "-----------------------------------------createUserWithEmail:failure", task.getException());
                    currentUser = null;
                    //Toast.makeText(MainActivity.this, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void SignIn(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(),"Success SignIN:)",Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "----------------------------------------------------------signInWithEmail:success");
                    currentUser = mAuth.getCurrentUser();
                    updateUI(currentUser);
                }else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getApplicationContext(),"SignIN failed :(, try to register",Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "----------------------------------------------------------signInWithEmail:failure", task.getException());
                    currentUser = null;
                    //Toast.makeText(MainActivity.this, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
