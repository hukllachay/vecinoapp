package com.grupo1.vecinoapp.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.grupo1.vecinoapp.FirebaseHelper.PreferencesManager;
import com.grupo1.vecinoapp.Location.LocationActivity;
import com.grupo1.vecinoapp.Models.InitiativesModel;
import com.grupo1.vecinoapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EditInitiativeActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private EditText etTitle, etDetails;
    private Spinner spStatus, spType;
    private Button btnSave, btnCancel, btnEndDate, btnStartDate, btnSelectLocation;

    private DatabaseReference tableInitiatives;
    ProgressDialog progressDialog;
    private InitiativesModel previousInitiativesModel;
    final String[] selectedStatus = {"Pendiente"};
    final String[] selectedType = {"Colecta"};
    private TextView tvLocation;
    private PreferencesManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_initiative);
        try {
            pref=new PreferencesManager(this);
            //receive data from previous Activity
            previousInitiativesModel = new Gson().fromJson(getIntent().getStringExtra("ZXC"), InitiativesModel.class);
            //set up progress dialogue
            setupProgressDialog();
            tableInitiatives = FirebaseDatabase.getInstance().getReference().child("Initiatives");
            selectedStatus[0] = previousInitiativesModel.getiStatus();
            selectedType[0] = previousInitiativesModel.getiTipo();
            //initialize view and bind with id's
            tvLocation = findViewById(R.id.tvLocation);
            btnSelectLocation = findViewById(R.id.btnSelectLocation);
            btnStartDate = findViewById(R.id.btnStartDate);
            btnEndDate = findViewById(R.id.btnEndDate);
            btnCancel = findViewById(R.id.btnCancel);
            btnSave = findViewById(R.id.btnSave);
            etTitle = findViewById(R.id.etTitle);
            spStatus = findViewById(R.id.spStatus);
            spType = findViewById(R.id.spType);
            etDetails = findViewById(R.id.etDetails);
            //set data on edit text
            etTitle.setText(previousInitiativesModel.getiTitle());
            etDetails.setText(previousInitiativesModel.getiDescription());
            btnStartDate.setText(previousInitiativesModel.getiDateFrom());
            btnEndDate.setText(previousInitiativesModel.getiDateTo());
            tvLocation.setText(previousInitiativesModel.getiAddress());
            //Support Toolbar
            Toolbar toolbarToolbar = findViewById(R.id.tbAddNewProject);
            getSupportActionBar();
            setSupportActionBar(toolbarToolbar);
            Objects.requireNonNull(getSupportActionBar()).setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbarToolbar.setNavigationOnClickListener(v -> finish());
            //End Of toolbar text
            setupSpinners();
            //on select location
            btnSelectLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(EditInitiativeActivity.this, LocationActivity.class));
                }
            });
            //get selected on spinner
            spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                    // TODO Auto-generated method stub
                    //Toast.makeText(getBaseContext(), "" + spGender.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                    selectedStatus[0] = "" + spStatus.getItemAtPosition(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
            spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                    // TODO Auto-generated method stub
                    //Toast.makeText(getBaseContext(), "" + spGender.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
                    selectedType[0] = "" + spType.getItemAtPosition(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
            //on cancel button click
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            //on save button click
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String date = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss", Locale.getDefault()).format(new Date());
                    InitiativesModel model = new InitiativesModel();
                    Map<String, Object> updates = new HashMap<String, Object>();

                    updates.put("iId", previousInitiativesModel.getiId());
                    updates.put("iTitle", etTitle.getText().toString());
                    updates.put("iDateFrom", btnStartDate.getText().toString());
                    updates.put("iDateTo", btnEndDate.getText().toString());
                    updates.put("iTipo", selectedType[0]);
                    updates.put("iStatus", selectedStatus[0]);
                    updates.put("iDescription", etDetails.getText().toString());
                    updates.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    updates.put("iDate", date);
                    updates.put("iAddress", pref.getLocationCity());
                    updates.put("iLng", pref.getLangPref());
                    updates.put("iLat", pref.getLatPref());
//                    model.setiAddress(pref.getLocationCity());
//                    model.setiLat(pref.getLatPref());
//                    model.setiLng(pref.getLangPref());
                    model.setiId(previousInitiativesModel.getiId());
                    model.setiTitle(etTitle.getText().toString());
                    model.setiDateFrom(btnStartDate.getText().toString());
                    model.setiDateTo(btnEndDate.getText().toString());
                    model.setiDescription(etDetails.getText().toString());
                    model.setiStatus(selectedStatus[0]);
                    model.setiTipo(selectedType[0]);
                    model.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    model.setiDate(date);
                    if (invalidate(model)) {
                        progressDialog.show();
                        updateInitiative(model, updates);
                    }
                }
            });
            btnStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //show date dialogue
                    final Calendar myCalendar = Calendar.getInstance();
                    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // TODO Auto-generated method stub
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, monthOfYear);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            String myFormat = "dd MMMM yyyy"; // your format
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                            btnStartDate.setText(sdf.format(myCalendar.getTime()));
                        }

                    };
                    new DatePickerDialog(EditInitiativeActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            btnEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //show date dialogue
                    final Calendar myCalendar = Calendar.getInstance();
                    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // TODO Auto-generated method stub
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, monthOfYear);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            String myFormat = "dd MMMM yyyy"; // your format
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                            btnEndDate.setText(sdf.format(myCalendar.getTime()));
                        }

                    };
                    new DatePickerDialog(EditInitiativeActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    private void updateInitiative(InitiativesModel model, Map<String, Object> updates) {
        try {
            tableInitiatives.child(model.getiId()).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(EditInitiativeActivity.this, "iniciativa actualizada con ??xito", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditInitiativeActivity.this, "Algo sali?? mal. ??Int??ntalo de nuevo!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onComplete: " + task.getException());
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "updateInitiative: " + e.toString());
        }
    }

    private boolean invalidate(InitiativesModel model) {
        if (model.getiTitle().isEmpty()) {
            etTitle.setError("Debe llenar el campo");
            etTitle.requestFocus();
            return false;
        }
        if (model.getiDescription().isEmpty()) {
            model.setiDescription("null");
        }
        if (model.getiDateFrom().equals("Fecha inicio")) {
            btnStartDate.setError("Debe llenar el campo");
            btnStartDate.requestFocus();
            return false;
        }
        if (model.getiDateTo().equals("Fecha fin")) {
            model.setiDateTo("null");
        }
        if (pref.getLocationCity().equals("")) {
            Toast.makeText(this, "Seleccionar Ubicaci??n", Toast.LENGTH_SHORT).show();
            btnSelectLocation.setError("Seleccionar Ubicaci??n");
            btnSelectLocation.requestFocus();
            return false;
        }
        //  String hash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(Double.parseDouble(preferencesManager.getLatPref()), Double.parseDouble(preferencesManager.getLangPref())));
        model.setiAddress(pref.getLocationCity());
        model.setiLat(pref.getLatPref());
        model.setiLng(pref.getLangPref());

        return true;
    }

    private void setupSpinners() {
        //set status data on spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.status_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(adapter);
        //set Type data on spinner
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.tipo_array));
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter1);
        spStatus.setSelection(adapter.getPosition(previousInitiativesModel.getiStatus()));
        spType.setSelection(adapter1.getPosition(previousInitiativesModel.getiTipo()));


    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(EditInitiativeActivity.this);
        progressDialog.setTitle("Iniciativa de actualizaci??n");
        progressDialog.setMessage("Espere mientras configuramos su informaci??n");
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvLocation.setText("" + pref.getLocationCity());
    }
}