package garg.sushant.mymovielist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class SignInActivity extends GoogleSignInActivity implements View.OnClickListener {

    private static final String TAG = SignInActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 9001;
    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    public PrefManagerSignIn prefManager;
    private FirebaseAuth.AuthStateListener mAuthListener;
    boolean isFirstSignInTime = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstSignInTime = PrefManagerSignIn.isFirst(SignInActivity.this);
        if (!isFirstSignInTime) {
            launchHomeScreen();
            finish();
        }

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_signin);



        //btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
       // btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        findViewById(R.id.button_sign_in).setOnClickListener(this);

//        btnResetPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SignInActivity.this, ResetPasswordActivity.class));
//            }
//        });

//        btnSignIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(SignInActivity.this, LoginActivity.class));
//            }
//        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                              //  Toast.makeText(SignInActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignInActivity.this, "Sign In failed. User already exists!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(SignInActivity.this, NavigationDrawerActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = firebaseAuth.getCurrentUser();
                if (mFirebaseUser != null){
                    if (BuildConfig.DEBUG)
                        Log.d(TAG,"onAuthStateChanged:signed_in " + mFirebaseUser.getDisplayName());

                }
                else {
                    if (BuildConfig.DEBUG) Log.d(TAG,"onAuthStateChanged:signed_out");
                }
            }
        };
    }



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    private void launchHomeScreen() {
        isFirstSignInTime = false;
        startActivity(new Intent(SignInActivity.this, NavigationDrawerActivity.class));
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_sign_in:
                showProgressDialog();
                signIn();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (resultCode == Activity.RESULT_OK){
            if (requestCode == RC_SIGN_IN){
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if (result.isSuccess()){
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);

                } else {
                    hideProgressDialog();
                }
            } else {
                hideProgressDialog();
            }
        } else {
            hideProgressDialog();
        }
    }



    private void firebaseAuthWithGoogle(final GoogleSignInAccount account) {
        if (BuildConfig.DEBUG) Log.d(TAG, "firebaseAuthWithGooogle: " + account.getDisplayName());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (BuildConfig.DEBUG) Log.d(TAG, "signInWithCredential:onCOmplete: " + task.isSuccessful());

                        if (task.isSuccessful()){
                            String photoUrl = null;
                            if (account.getPhotoUrl() != null){
                                photoUrl = account.getPhotoUrl().toString();
                            }

                            Users user = new Users(
                                    account.getDisplayName(),
                                    account.getEmail(),
                                    photoUrl,
                                    FirebaseAuth.getInstance().getCurrentUser().getUid()
                            );

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference userRef = database.getReference(Constants.USER_KEY);

                            userRef.child(account.getEmail().replace(".",","))
                                    .setValue(user, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            startActivity(new Intent(SignInActivity.this, NavigationDrawerActivity.class));

                                        }
                                    });
                            if (BuildConfig.DEBUG) Log.v(TAG,"Authentication successful");

                        } else {
                            hideProgressDialog();
                            if (BuildConfig.DEBUG){
                                Log.v(TAG, "signInWithCredential", task.getException());
                                Log.v(TAG, "Authentication failed");
                                Toast.makeText(SignInActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                signOut();
                            }
                        }
                    }
                });

    }



}
