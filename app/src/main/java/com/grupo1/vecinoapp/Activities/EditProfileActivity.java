package com.grupo1.vecinoapp.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.grupo1.vecinoapp.FirebaseHelper.FirebaseDatabaseHelpers;
import com.grupo1.vecinoapp.Models.UserModel;
import com.grupo1.vecinoapp.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditProfileActivity extends AppCompatActivity implements FirebaseDatabaseHelpers.OnLoginSignupAttemptCompleteListener {
    private ProgressDialog progressDialog;
    public static final String TAG = "TAG";
    private EditText etFullName, etDNI, etAddress;
    private Button btnLogIn;
    private ImageView ivMain;
    private ImageButton ibChooseImage;
    private FirebaseDatabaseHelpers firebaseDatabaseHelper;
    private Uri mainImageUri = null;
    private UserModel currentUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        try {
            //
            currentUserModel = new Gson().fromJson(getIntent().getStringExtra("PSFD"), UserModel.class);
            firebaseDatabaseHelper = new FirebaseDatabaseHelpers(this);
            //set up progress dialogue
            setupProgressDialog();
            //initialize view and bind with id's
            ivMain = findViewById(R.id.ivMain);
            ibChooseImage = findViewById(R.id.ibChooseImage);
            etFullName = findViewById(R.id.etFullName);
            etDNI = findViewById(R.id.etDNI);
            etAddress = findViewById(R.id.etAddress);
            btnLogIn = findViewById(R.id.btnLogIn);
            ibChooseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != getPackageManager().PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            //  Toast.makeText(SignUpActivity.this, "Grant Permissions.", Toast.LENGTH_SHORT).show();
                        } else {
                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setAspectRatio(1, 1)
                                    .start(EditProfileActivity.this);
                            // Toast.makeText(SignUpActivity.this, "Permissions Added", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(EditProfileActivity.this);

                    }

                }
            });
            //set existing data on fields
            Glide.with(this).load(currentUserModel.getuImage()).into(ivMain);
            etFullName.setText(currentUserModel.getuName());
            etDNI.setText(currentUserModel.getuDNI());
            etAddress.setText(currentUserModel.getuAddress());
            //on regsiter click
            btnLogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String date = new SimpleDateFormat("dd MMMM yyyy ", Locale.getDefault()).format(new Date());

                    UserModel model = new UserModel();
                    model.setuEmail(currentUserModel.getuEmail());
                    model.setuName(etFullName.getText().toString());
                    model.setuPassword(currentUserModel.getuPassword());
                    model.setuAddress(etAddress.getText().toString());
                    model.setuDNI(etDNI.getText().toString());
                    model.setuDate(currentUserModel.getuDate());
                    model.setUid(currentUserModel.getUid());

                    if (invalidate(model)) {
                        progressDialog.show();
                        firebaseDatabaseHelper.updateProfile(mainImageUri, model, EditProfileActivity.this);
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }

    }

    private boolean invalidate(UserModel model) {
        if (mainImageUri == null) {
            model.setuImage(currentUserModel.getuImage());
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
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setTitle("Creando tu cuenta");
        progressDialog.setMessage("Espere mientras configuramos la información de su cuenta");
        progressDialog.setCancelable(false);
    }

    public void sendToLogin(View view) {
        startActivity(new Intent(EditProfileActivity.this, LogInActivity.class));
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

   /*   @Override
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

    }*/

    @Override
    public void onLoginSignupSuccess(UserModel user) {
        progressDialog.dismiss();
//        FirebaseAuth.getInstance().signOut();
        finish();

        //      Toast.makeText(EditProfileActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
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
                        .start(EditProfileActivity.this);

            }
        }

    }
}