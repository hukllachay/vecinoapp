package com.grupo1.vecinoapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.grupo1.vecinoapp.FirebaseHelper.FirebaseDatabaseHelpers;
import com.grupo1.vecinoapp.FirebaseHelper.PreferencesManager;
import com.grupo1.vecinoapp.Models.UserModel;
import com.grupo1.vecinoapp.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.logging.LoggingMXBean;

public class LogInActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    private EditText etemail, etpassword;
    private DatabaseReference tableUser;
    private ProgressBar pbLogin;
    private FirebaseDatabaseHelpers firebaseDatabaseHelper;
    private TextView tvsignUp, tvLogin;
    private Button btnLogIn;
    private CardView cvGoogle, cvFb;
    //google sign in
    private GoogleApiClient googleApiClient;
    private static final int GOOGLE_SIGN_IN = 234;
    ProgressDialog dialog;
    private FirebaseAuth firebaseAuth;
    private CallbackManager callbackManager;
    AuthCredential authCredential;
    boolean booleanGoogle;
    String string_uid;
    String string_email;
    String string_password = "null"; //null (IMPORTANT - DO NOT EDIT THIS FIELD)
    String string_name;
    String string_photo;
    String string_thumb;
    String string_image;
    String string_cover;
    private GoogleSignInClient mGoogleSignInClient;
    //
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    //fb
    LoginButton login_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        try {
            callbackManager = CallbackManager.Factory.create();
            //facebook
            FacebookSdk.sdkInitialize(getApplicationContext());
            //Firebase Auth
            firebaseAuth = FirebaseAuth.getInstance();
            //facebook signin
            login_button=findViewById(R.id.login_button);
             String EMAIL = "email";

            login_button = (LoginButton) findViewById(R.id.login_button);
            login_button.setReadPermissions(Arrays.asList(EMAIL));
            // Callback registration
            login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    Log.e(TAG, "onSuccess"+loginResult );
                   // Toast.makeText(LogInActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel() {
                    // App code
                    Toast.makeText(LogInActivity.this, "Cancel", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                    Toast.makeText(LogInActivity.this, "Error:"+exception.toString(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onError: "+ exception.toString() );
                }
            });
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code
                            dialog.dismiss();
                           // Toast.makeText(LogInActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onSuccess: login success" + loginResult);
                        }

                        @Override
                        public void onCancel() {
                            // App code
                            dialog.dismiss();
                           // Toast.makeText(LogInActivity.this, "Cacelled", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onCancel: login cancelled");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            dialog.dismiss();
                           // Toast.makeText(LogInActivity.this, "Error " + exception, Toast.LENGTH_SHORT).show();
                            // App code
                            Log.e(TAG, "onError: " + exception);
                        }
                    });
            //set up progress dialogue
            dialog = new ProgressDialog(LogInActivity.this);
            dialog.setMessage("cargando");
            dialog.setCancelable(false);
            //google sign in section
   /*         gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                   // .requestIdToken("416301751423-jrtb7qqmi062n7qmhpo7kklhi46hmgpo.apps.googleusercontent.com")
                    //.requestIdToken("416301751423-npradmg05emimcdksup7hbba82u2ub8t.apps.googleusercontent.com")
                    .requestEmail().build();
            gsc = GoogleSignIn.getClient(this, gso);*/

            //createRequest();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    ///.requestIdToken(getString(R.string.default_web_client_id))
                    .requestIdToken("416301751423-jrtb7qqmi062n7qmhpo7kklhi46hmgpo.apps.googleusercontent.com")
                    .requestEmail()
                    .build();

            googleApiClient = new GoogleApiClient.Builder(LogInActivity.this)
                    .enableAutoManage(LogInActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            //end of google sign in
            tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
            cvFb = findViewById(R.id.cvFb);
            cvGoogle = findViewById(R.id.cvGoogle);
            btnLogIn = findViewById(R.id.btnLogIn);
            tvLogin = findViewById(R.id.btnLogIn);
            tvsignUp = findViewById(R.id.tvSignUp);
            pbLogin = findViewById(R.id.pbLogin);
            etemail = findViewById(R.id.etEmail);
            etpassword = findViewById(R.id.etPassword);
            firebaseDatabaseHelper = new FirebaseDatabaseHelpers(this);
            callbackManager = CallbackManager.Factory.create();

            cvFb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //FacebookFirebaseClick();
                    LoginManager.getInstance().logInWithReadPermissions(LogInActivity.this, Arrays.asList("public_profile"));
                    dialog.show();
                }
            });
            cvGoogle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
//                    Intent signInIntent = gsc.getSignInIntent();
//                    startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
//                    //old
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
                    /*Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, GOOGLE_SIGN_IN);*/

                }
            });
            tvsignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }
            });
            tvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = etemail.getText().toString();
                    String password = etpassword.getText().toString();
                    if (email.isEmpty()) {
                        etemail.setError("Debe llenar el campo");
                        etemail.requestFocus();
                        return;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        etemail.setError("Debe ingresar un correo electrónico válido");
                        etemail.requestFocus();
                        return;
                    }
                    if (password.isEmpty()) {
                        etemail.setError("Debe llenar el campo");
                        etemail.requestFocus();
                        return;
                    }
                    //if ((!TextUtils.isEmpty(email)) && (!TextUtils.isEmpty(password))) {
                    pbLogin.setVisibility(View.VISIBLE);
                    //pbLogin.setVisibility(View.GONE);
                    btnLogIn.setVisibility(View.GONE);

                    attemptLogin(email, password);
                    // } else {
                    //  Toast.makeText(LogInActivity.this, "Please fill username and password fields", Toast.LENGTH_SHORT).show();
                    //}
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(LogInActivity.this, MainActivity.class));
            finish();
        }
    }

    //Doing well here
    public void attemptLogin(String email, String password) {
        try {
            Log.e(TAG, "attemptLogin: Try to login");
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        tableUser.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    UserModel user = dataSnapshot.getValue(UserModel.class);
                                    new PreferencesManager(LogInActivity.this).saveCurrentUser(user);
                                    pbLogin.setVisibility(View.GONE);
                                    btnLogIn.setVisibility(View.VISIBLE);
                                    Log.e(TAG, "onLoadUserInfoComplete:Redirecting ");
                                    startActivity(new Intent(LogInActivity.this, MainActivity.class));
                                    LogInActivity.this.finish();
                                    tableUser.removeEventListener(this);
                                } else {
                                    pbLogin.setVisibility(View.GONE);
                                    btnLogIn.setVisibility(View.VISIBLE);
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(LogInActivity.this, "No hay datos para la cuenta", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                pbLogin.setVisibility(View.GONE);
                                btnLogIn.setVisibility(View.VISIBLE);
                            }
                        });

                    } else {
                        btnLogIn.setVisibility(View.VISIBLE);
                        pbLogin.setVisibility(View.GONE);
                        Toast.makeText(LogInActivity.this, "El usuario y contraseña no son correctos", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onComplete: " + task.getException());
                    }
                }
            });
        } catch (Exception e) {
            btnLogIn.setVisibility(View.VISIBLE);
            pbLogin.setVisibility(View.GONE);
            Log.e(TAG, "attemptLogin: " + e.toString());
            //Toast.makeText(this, "" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void FacebookFirebaseClick() {
        try {
            LoginManager.getInstance().logOut();

            LoginManager.getInstance()
                    .logInWithReadPermissions(LogInActivity.this,
                            Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.e(TAG, "onSuccess: " + loginResult);
                            authCredential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                            FirebaseSignin();
                            Toast.makeText(LogInActivity.this,
                                    getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(LogInActivity.this,
                                    getResources().getString(R.string.login_cancelled), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            Log.e(TAG, "onCancel: ");
                        }
                        @Override
                        public void onError(FacebookException error) {
                            Toast.makeText(LogInActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            Log.e(TAG, "onError: " + error.toString());

                        }
                    }
            );
        } catch (Exception e) {
            Log.e(TAG, "FacebookFirebaseClick: " + e.toString());
        }
    }

    private void FirebaseSignin() {
        try {
            firebaseAuth.signInWithCredential(authCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.e(TAG, "onComplete: task is successful");
                                string_uid = task.getResult().getUser().getUid();
                                string_name = task.getResult().getUser().getDisplayName();
                                string_email = task.getResult().getUser().getEmail();
                                string_photo = task.getResult().getUser().getPhotoUrl().toString();

                                if (booleanGoogle) {

                                    string_thumb = string_photo.replace("s96-c", "s100-c");
                                    string_image = string_photo.replace("s96-c", "s300-c");
                                    string_cover = string_photo.replace("s96-c", "s400-c");

                                } else {
                                    string_thumb = string_photo + "?type=large&redirect=true&width=100&height=100";
                                    string_image = string_photo + "?type=large&redirect=true&width=300&height=300";
                                    string_cover = string_photo + "?type=large&redirect=true&width=400&height=400";
                                }
                                tableUser.child(string_uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            Log.e(TAG, "onDataChange: User Exists");
                                            new PreferencesManager(LogInActivity.this).saveCurrentUser(snapshot.getValue(UserModel.class));
                                            startActivity(new Intent(LogInActivity.this, MainActivity.class));
                                            finish();
                                        } else {

                                            String date = new SimpleDateFormat("dd MMMM yyyy ", Locale.getDefault()).format(new Date());
                                            UserModel userModel = new UserModel();
                                            userModel.setUid(string_uid);
                                            userModel.setuName(string_name);
                                            userModel.setuEmail(string_email);
                                            userModel.setuImage(string_image);
                                            userModel.setuAddress("null");
                                            userModel.setuDNI("12345678");
                                            userModel.setuDate(date);
                                            userModel.setuPassword("null");
                                            tableUser.child(string_uid).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        new PreferencesManager(LogInActivity.this).saveCurrentUser(userModel);
                                                        startActivity(new Intent(LogInActivity.this, MainActivity.class));
                                                        finish();
                                                    } else {
                                                        Log.e(TAG, "onComplete: " + task.getException());
                                                    }

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e(TAG, "onCancelled: " + error);
                                    }
                                });
                            } else {
                                Log.e(TAG, "onComplete: " + task.getException());
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "FirebaseSignin: " + e.toString());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
/*
        if (requestCode == GOOGLE_SIGN_IN) {
            Log.e(TAG, "onActivityResult: " + requestCode);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            dialog.dismiss();
            try {
                task.getResult(ApiException.class);
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                if (account != null) {

                    Toast.makeText(LogInActivity.this, "Hurrah", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onActivityResult: User data is fopund");
                } else {
                    Toast.makeText(LogInActivity.this, "Else case", Toast.LENGTH_SHORT).show();

                    Log.e(TAG, "onActivityResult: else no user found");
                }
                Log.e(TAG, "onActivityResult: Signed In");
            } catch (ApiException e) {
                e.printStackTrace();
                Log.e(TAG, "onActivityResult: " + e.toString());
            }
            */
/* GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();

                authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseSignin();

                booleanGoogle = true;

                Toast.makeText(LogInActivity.this,
                        "Success", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(this,
                        "Failed", Toast.LENGTH_SHORT)
                        .show();
                dialog.dismiss();
                Log.e(TAG, "onActivityResult: failed"+result.getStatus().getStatusMessage() );
            }*/

        if (requestCode == GOOGLE_SIGN_IN) {
            //Log.e(TAG, "onActivityResult: " + requestCode);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();

                authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseSignin();

                booleanGoogle = true;

                Toast.makeText(LogInActivity.this,
                        "Success", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this,
                        "Failed", Toast.LENGTH_SHORT)
                        .show();
                dialog.dismiss();
                Log.e(TAG, "onActivityResult: failed" + result.getStatus().getStatusMessage());
            }
        }
    }
/*
    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
*/

}
