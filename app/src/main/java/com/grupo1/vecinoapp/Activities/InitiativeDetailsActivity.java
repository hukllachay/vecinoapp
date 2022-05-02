package com.grupo1.vecinoapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.grupo1.vecinoapp.Adapters.NeighborsAdapter;
import com.grupo1.vecinoapp.Models.InitiativesModel;
import com.grupo1.vecinoapp.Models.NeighborsModel;
import com.grupo1.vecinoapp.R;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class InitiativeDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String TAG = "TAG";
    private InitiativesModel initiativesModel;
    private TextView tvStatus, tvTitle, tvTipo, tvDesc;
    private RecyclerView rvNeighbors;
    private NeighborsAdapter neighborsAdapter;
    private ArrayList<NeighborsModel> arrayList = new ArrayList<>();
    private ProgressBar pbLoading;
    private LinearLayout llNoDataFound;
    private FloatingActionButton fbDelete, fbEdit, fbAddNeighbour;
    private ProgressDialog progressDialogDel;
    //show gragmen
    GoogleMap mMap;
    LatLng friendLatLng;
    String latitude,longitude;//,name,userid,prevdate,prevImage,myImage,myName,myLat,myLng,myDate;
    Marker marker;
    DatabaseReference reference;

    ArrayList<String> mKeys;
    MarkerOptions myOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiative_details);
        try {

            //set up progress dialogue
            setupProgressDialog();
            //receive selected initiative data from previous class
            initiativesModel = new Gson().fromJson(getIntent().getStringExtra("IDM"), InitiativesModel.class);
            //initialize view and bind with id's
            fbAddNeighbour = findViewById(R.id.fbAddNeighbour);
            fbDelete = findViewById(R.id.fbDelete);
            fbEdit = findViewById(R.id.fbEdit);
            llNoDataFound = findViewById(R.id.llNoDataFound);
            pbLoading = findViewById(R.id.pbLoading);
            rvNeighbors = findViewById(R.id.rvNeighbors);
            rvNeighbors.setHasFixedSize(true);
            rvNeighbors.setLayoutManager(new LinearLayoutManager(this));
            neighborsAdapter = new NeighborsAdapter(initiativesModel.getiId(), arrayList, InitiativeDetailsActivity.this);
            rvNeighbors.setAdapter(neighborsAdapter);
            arrayList.clear();
            tvTipo = findViewById(R.id.tvTipo);
            tvStatus = findViewById(R.id.tvStatus);
            tvTitle = findViewById(R.id.tvTitle);
            tvDesc = findViewById(R.id.tvDesc);
            //initialize fragment
            SupportMapFragment mapFragment =  (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            //end of fragment
            //set data on TextView's
            tvStatus.setText("Estado: " + initiativesModel.getiStatus());
            tvTitle.setText("Título: " + initiativesModel.getiTitle());
            tvTipo.setText("Tipo: " + initiativesModel.getiTipo());
            tvDesc.setText("Descripción: " + initiativesModel.getiDescription());
            //set up toolbar
            Toolbar toolbarToolbar = findViewById(R.id.toolbarToolbar);
            getSupportActionBar();
            setSupportActionBar(toolbarToolbar);
            Objects.requireNonNull(getSupportActionBar()).setTitle("Choose Your Provider");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbarToolbar.setNavigationOnClickListener(v -> finish());
            //End Of toolbar text
            getListOfNeighbors(initiativesModel);
            fbAddNeighbour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(InitiativeDetailsActivity.this, NeighborsSelectActivity.class);
                    intent.putExtra("IM", new Gson().toJson(initiativesModel));
                    intent.putExtra("tps", "single");
                    startActivity(intent);
                }
            });
            fbEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(InitiativeDetailsActivity.this, EditInitiativeActivity.class);
                    intent.putExtra("ZXC", new Gson().toJson(initiativesModel));
                    startActivity(intent);
                }
            });
            fbDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        new SweetAlertDialog(InitiativeDetailsActivity.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                                .setTitleText("Estás seguro")
                                .setContentText("Está seguro de eliminar la iniciativa?")
                                .setConfirmButton("Aceptar", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        progressDialogDel.show();
                                        deleteInitiative(initiativesModel);
                                    }
                                })
                                .setCancelButton("Rechazar", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                }).show();
                    } catch (Exception e) {
                        Log.e(TAG, "logout: " + e.toString());
                    }

                }
            });
            //load Realtime data of the Initiative
            loadInitiative(initiativesModel);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View row = getLayoutInflater().inflate(R.layout.custom_snippet,null);
                TextView nameTxt = row.findViewById(R.id.snippetName);
              //  TextView dateTxt = row.findViewById(R.id.snippetDate);
                nameTxt.setText(initiativesModel.getiTitle());
               // CircleImageView imageTxt = row.findViewById(R.id.snippetImage);
               /* if(myName == null && myDate == null)
                {
                    dateTxt.setText(dateTxt.getText().toString() + prevdate);
                    Picasso.get().load(prevImage).placeholder(R.drawable.defaultprofile).into(imageTxt);
                }
                else
                {
                    nameTxt.setText(myName);
                    dateTxt.setText(dateTxt.getText().toString() + myDate);
                    Picasso.get().load(myImage).placeholder(R.drawable.defaultprofile).into(imageTxt);
                }*/


                return row;
            }
        });

        friendLatLng = new LatLng(Double.parseDouble(initiativesModel.getiLat()),Double.parseDouble(initiativesModel.getiLng()));
        MarkerOptions optionsnew = new MarkerOptions();

        optionsnew.position(friendLatLng);
        optionsnew.title(initiativesModel.getiTitle());
        optionsnew.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));


        if(marker == null)
        {
            marker = mMap.addMarker(optionsnew);
        }
        else
        {
            marker.setPosition(friendLatLng);
        }


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(friendLatLng,15));



    }
    private void loadInitiative(InitiativesModel model) {
        try {
            DatabaseReference tableInitiatives = FirebaseDatabase.getInstance().getReference().child("Initiatives");
            tableInitiatives.child(model.getiId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    InitiativesModel newModel = snapshot.getValue(InitiativesModel.class);
                    initiativesModel = newModel;
                    try {
                        Log.e(TAG, "onDataChange: " );
                        tvStatus.setText("Estado: " + initiativesModel.getiStatus());
                        tvTitle.setText("Título: " + initiativesModel.getiTitle());
                        tvTipo.setText("Tipo: " + initiativesModel.getiTipo());
                        tvDesc.setText("Descripción: " + initiativesModel.getiDescription());
                    }catch (Exception e){
                        Log.e(TAG, "onDataChange: "+e.toString() );
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: " + error.toString());
                }

            });
        } catch (Exception e) {
            Log.e(TAG, "loadInitiative: " + e.toString());
        }
    }

    private void deleteInitiative(InitiativesModel initiativesModel) {
        try {
            DatabaseReference tableInitiatives = FirebaseDatabase.getInstance().getReference().child("Initiatives");
            tableInitiatives.child(initiativesModel.getiId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialogDel.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(InitiativeDetailsActivity.this, "Iniciativa eliminada", Toast.LENGTH_SHORT).show();
                        InitiativeDetailsActivity.this.finish();
                    } else {
                        Toast.makeText(InitiativeDetailsActivity.this, "Error:" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "deleteInitiative: " + e.toString());
        }
    }

    //set up progress dialogue
    private void setupProgressDialog() {
        progressDialogDel = new ProgressDialog(InitiativeDetailsActivity.this);
        progressDialogDel.setTitle("Eliminar iniciativa");
        progressDialogDel.setMessage("Espere mientras configuramos la información de su cuenta");
        progressDialogDel.setCancelable(false);
    }


    private void getListOfNeighbors(InitiativesModel projectModel) {
        pbLoading.setVisibility(View.VISIBLE);
        DatabaseReference tableInitiatives = FirebaseDatabase.getInstance().getReference().child("Initiatives");
        tableInitiatives.child(projectModel.getiId()).child("Neighbors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pbLoading.setVisibility(View.GONE);
                ArrayList<NeighborsModel> arrayList1 = new ArrayList<>();
                arrayList1.clear();
                float complete = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NeighborsModel neighborsModel = dataSnapshot.getValue(NeighborsModel.class);
                    arrayList1.add(neighborsModel);
                }
                arrayList.clear();
                arrayList.addAll(arrayList1);
                if (arrayList.size() < 1) {
                    llNoDataFound.setVisibility(View.VISIBLE);
                } else {
                    llNoDataFound.setVisibility(View.GONE);
                }
                neighborsAdapter.notifyDataSetChanged();
                arrayList1.clear();
                Log.e(TAG, "onDataChange: complet" + complete);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pbLoading.setVisibility(View.GONE);
                Log.e(TAG, "onCancelled: " + error);
            }
        });
    }

}