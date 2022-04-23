package com.zahid_iqbal699.vecinoapp.Activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.zahid_iqbal699.vecinoapp.FirebaseHelper.FirebaseDatabaseHelper;
import com.zahid_iqbal699.vecinoapp.Models.UserModel;
import com.zahid_iqbal699.vecinoapp.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUpActivity extends AppCompatActivity implements FirebaseDatabaseHelper.OnLoginSignupAttemptCompleteListener {
    private ProgressDialog progressDialog;
    public static final String TAG = "TAG";
    private EditText etFullName, etPassword, etEmail, etDNI, etAddress;
    private TextView tvSignUp;
    private Button btnLogIn;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        try {
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            //set up progress dialogue
            setupProgressDialog();
            //initialize view and bind with id's
            etFullName = findViewById(R.id.etFullName);
            etPassword = findViewById(R.id.etPassword);
            etEmail = findViewById(R.id.etEmail);
            etDNI = findViewById(R.id.etDNI);
            etAddress = findViewById(R.id.etAddress);
            tvSignUp = findViewById(R.id.tvSignUp);
            btnLogIn = findViewById(R.id.btnLogIn);
            //on regsiter click
            btnLogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UserModel model = new UserModel();
                    model.setuEmail(etEmail.getText().toString());
                    model.setuName(etFullName.getText().toString());
                    model.setuPassword(etPassword.getText().toString());
                    model.setuAddress(etAddress.getText().toString());
                    model.setuDNI(etDNI.getText().toString());
                    if (invalidate(model)) {
                        progressDialog.show();
                        firebaseDatabaseHelper.attemptSignUp(model, SignUpActivity.this);
                    }
                }
            });
            //send to login if already have an account
            tvSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }

    }

    private boolean invalidate(UserModel model) {

        if (model.getuEmail().isEmpty()) {
            etEmail.setError("Debe llenar el campo");
            etEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(model.getuEmail()).matches()) {
            etEmail.setError("Debe ingresar un correo electrónico válido");
            etEmail.requestFocus();
            return false;
        }
        if (model.getuPassword().isEmpty()) {
            etPassword.setError("Debe llenar el campo");
            etPassword.requestFocus();
            return false;
        }
        if (model.getuName().isEmpty()) {
            etFullName.setError("Debe llenar el campo");
            etFullName.requestFocus();
            return false;
        }
        if (model.getuDNI().isEmpty()) {
            etDNI.setError("Debe llenar el campo");
            etDNI.requestFocus();
            return false;
        }
        if (model.getuDNI().length() < 8) {
            etDNI.setError("La longitud mínima del DNI debe ser 8");
            etDNI.requestFocus();
            return false;
        }
        if (model.getuAddress().isEmpty()) {
            etAddress.setError("Debe llenar el campo");
            etAddress.requestFocus();
            return false;
        }
        return true;
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creando tu cuenta");
        progressDialog.setMessage("Espere mientras configuramos la información de su cuenta");
        progressDialog.setCancelable(false);
    }

    public void sendToLogin(View view) {
        startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Recordatorio")
                .setContentText("VecinoApp es un app directamenate para personas que vivan alrededor tuyo. Necesitamos tus datos correctos para corroborara tu seguridad")
                .setConfirmButton("Aceptar", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton("Rechazar", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        finish();
                    }
                }).show();

    }

    @Override
    public void onLoginSignupSuccess(UserModel user) {
        progressDialog.dismiss();
        FirebaseAuth.getInstance().signOut();
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Cuenta creada con éxito")
                .setContentText("Inicie sesión ahora para usar la aplicación")
                .setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        finish();
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        finish();
                    }
                }).show();


        Toast.makeText(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginSignupFailure(String failureMessage) {
        progressDialog.dismiss();
        Log.e(TAG, "onLoginSignupFailure: " + failureMessage);
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("No se puede crear su cuenta!")
                .setContentText("Error:" + failureMessage)
                .setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                }).show();

    }
}