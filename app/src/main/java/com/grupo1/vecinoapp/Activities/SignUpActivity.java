package com.grupo1.vecinoapp.Activities;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.grupo1.vecinoapp.FirebaseHelper.FirebaseDatabaseHelpers;
import com.grupo1.vecinoapp.Models.UserModel;
import com.grupo1.vecinoapp.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUpActivity extends AppCompatActivity implements FirebaseDatabaseHelpers.OnLoginSignupAttemptCompleteListener {
    private ProgressDialog progressDialog;
    public static final String TAG = "TAG";
    private EditText etFullName, etPassword, etEmail, etDNI, etAddress;
    private TextView tvSignUp;
    private Button btnLogIn;
    private ImageView ivMain;
    private ImageButton ibChooseImage;
    private FirebaseDatabaseHelpers firebaseDatabaseHelper;
    private Uri mainImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        try {
            firebaseDatabaseHelper = new FirebaseDatabaseHelpers(this);
            //set up progress dialogue
            setupProgressDialog();
            //initialize view and bind with id's
            ivMain = findViewById(R.id.ivMain);
            ibChooseImage = findViewById(R.id.ibChooseImage);
            etFullName = findViewById(R.id.etFullName);
            etPassword = findViewById(R.id.etPassword);
            etEmail = findViewById(R.id.etEmail);
            etDNI = findViewById(R.id.etDNI);
            etAddress = findViewById(R.id.etAddress);
            tvSignUp = findViewById(R.id.tvSignUp);
            btnLogIn = findViewById(R.id.btnLogIn);
            //show dialogue
            showDialogue();
            ibChooseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(SignUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != getPackageManager().PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            //  Toast.makeText(SignUpActivity.this, "Grant Permissions.", Toast.LENGTH_SHORT).show();
                        } else {
                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setAspectRatio(1, 1)
                                    .start(SignUpActivity.this);
                            // Toast.makeText(SignUpActivity.this, "Permissions Added", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(SignUpActivity.this);

                    }

                }
            });

            //on regsiter click
            btnLogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String date = new SimpleDateFormat("dd MMMM yyyy ", Locale.getDefault()).format(new Date());

                    UserModel model = new UserModel();
                    model.setuEmail(etEmail.getText().toString());
                    model.setuName(etFullName.getText().toString());
                    model.setuPassword(etPassword.getText().toString());
                    model.setuAddress(etAddress.getText().toString());
                    model.setuDNI(etDNI.getText().toString());
                    model.setuDate(date);
                    if (invalidate(model)) {
                        progressDialog.show();
                        firebaseDatabaseHelper.attemptSignUp(mainImageUri,model, SignUpActivity.this);
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

    private void showDialogue() {
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

    private boolean invalidate(UserModel model) {
        if (mainImageUri == null) {
            Toast.makeText(SignUpActivity.this, "Elija la imagen primero", Toast.LENGTH_SHORT).show();
            return false;
        }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                Glide.with(this).load(mainImageUri).into(ivMain);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(TAG, "onActivityResult: " + error.toString());
            }
        }
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


      //  Toast.makeText(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //when permission granted
                //call method
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(SignUpActivity.this);

            }
        }

    }
}