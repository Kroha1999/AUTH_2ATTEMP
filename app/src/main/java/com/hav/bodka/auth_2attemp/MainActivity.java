package com.hav.bodka.auth_2attemp;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser = null;

    //UI
    private EditText mEmail, mPassword;
    private Button btnSignIn, btnRegister;
    private SignInButton btnGoogle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //DECLARE UI BUTTONS AND TEXTS
        mEmail = (EditText) findViewById(R.id.email_field);
        mPassword = (EditText) findViewById(R.id.password_field);
        btnSignIn = (Button) findViewById(R.id.sign_in_btn);
        btnRegister = (Button) findViewById(R.id.register_btn);
        btnGoogle = (SignInButton) findViewById(R.id.google_btn);

        //Firebase
        mAuth = FirebaseAuth.getInstance();

        //GOOGLE SIGN IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        //Email and Password
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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
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






    // Configure Google Sign In
    private final static int RC_SIGN_IN = 2;
    GoogleSignInClient mGoogleSignInClient;



    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override//google signin activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    //connecting to firebase
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "----------------------------------------signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "-----------------------------------------signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(),"GOOGLE Authinication Failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


    //CONFIGURE Email SIGNIN and Register
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
