package com.grupo1.vecinoapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.grupo1.vecinoapp.FirebaseHelper.PreferencesManager;
import com.grupo1.vecinoapp.Models.UserModel;
import com.grupo1.vecinoapp.R;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ProfileActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private PreferencesManager pref;
    private UserModel nurseModel;
    private TextView accStatus, tvDateJoin, profile_top_name_tv, profile_top_email_tv, profile_reg_id_tv, profile_emp_gender, tvLocation;
    private Button btnEdit;
    private CircularImageView profile_user_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        try {
            pref = new PreferencesManager(this);
            nurseModel = pref.getCurrentUser();
            //initialize view and bind with id's
            btnEdit = findViewById(R.id.btnEdit);
            //image
            profile_user_icon = findViewById(R.id.profile_user_icon);
            //name
            profile_top_name_tv = findViewById(R.id.profile_top_name_tv);
            //set email
            profile_top_email_tv = findViewById(R.id.profile_top_email_tv);
            //set phone number
            profile_reg_id_tv = findViewById(R.id.profile_reg_id_tv);
            //set Location
            tvLocation = findViewById(R.id.tvLocation);
            //set date of join
            tvDateJoin = findViewById(R.id.tvDateJoin);
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                    intent.putExtra("PSFD", new Gson().toJson(nurseModel));
                    startActivity(intent);
                }
            });
            updateUI();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }

    }

    private void updateUI() {
        try {
            Glide.with(this).load(nurseModel.getuImage()).into(profile_user_icon);
            profile_top_name_tv.setText(nurseModel.getuName());
            profile_top_email_tv.setText(nurseModel.getuEmail());
            profile_reg_id_tv.setText(nurseModel.getuDNI());
            tvDateJoin.setText(nurseModel.getuDate());
            tvLocation.setText(nurseModel.getuAddress());

        } catch (Exception e) {
            Log.e(TAG, "updateUI: " + e.toString());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        nurseModel = pref.getCurrentUser();
        updateUI();
    }

    public void goBack(View view) {
        onBackPressed();
    }
}