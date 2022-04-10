package com.zahid_iqbal699.vecinoapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.zahid_iqbal699.vecinoapp.FirebaseHelper.FirebaseDatabaseHelper;
import com.zahid_iqbal699.vecinoapp.FirebaseHelper.PreferencesManager;
import com.zahid_iqbal699.vecinoapp.Models.UserModel;
import com.zahid_iqbal699.vecinoapp.R;

public class LogInActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    private EditText etemail, etpassword;
    private DatabaseReference tableUser;
    private ProgressBar pbLogin;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private TextView tvsignUp, tvLogin;
    private Button btnLogIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        try {
            tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
            btnLogIn = findViewById(R.id.btnLogIn);
            tvLogin = findViewById(R.id.btnLogIn);
            tvsignUp = findViewById(R.id.tvSignUp);
            pbLogin = findViewById(R.id.pbLogin);
            etemail = findViewById(R.id.etEmail);
            etpassword = findViewById(R.id.etPassword);
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

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
                    if(email.isEmpty()){
                        etemail.setError("Debe llenar el campo");
                        etemail.requestFocus();
                        return;
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        etemail.setError("Debe ingresar un correo electrónico válido");
                        etemail.requestFocus();
                        return ;
                    }
                    if(password.isEmpty()){
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
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
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
                        Toast.makeText(LogInActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onComplete: " + task.getException());
                    }
                }
            });
        } catch (Exception e) {
            btnLogIn.setVisibility(View.VISIBLE);
            pbLogin.setVisibility(View.GONE);
            Log.e(TAG, "attemptLogin: " + e.toString());
            Toast.makeText(this, "" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


}
