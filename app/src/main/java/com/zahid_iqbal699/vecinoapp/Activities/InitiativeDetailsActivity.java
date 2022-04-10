package com.zahid_iqbal699.vecinoapp.Activities;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.zahid_iqbal699.vecinoapp.Adapters.NeighborsAdapter;
import com.zahid_iqbal699.vecinoapp.Models.InitiativesModel;
import com.zahid_iqbal699.vecinoapp.Models.NeighborsModel;
import com.zahid_iqbal699.vecinoapp.R;

import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class InitiativeDetailsActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private InitiativesModel initiativesModel;
    private TextView tvStatus, tvTitle, tvTipo, tvDesc;
    private RecyclerView rvNeighbors;
    private NeighborsAdapter neighborsAdapter;
    private ArrayList<NeighborsModel> arrayList = new ArrayList<>();
    private ProgressBar pbLoading;
    private LinearLayout llNoDataFound;
    private FloatingActionButton fbDelete, fbEdit,fbAddNeighbour;
    private ProgressDialog progressDialogDel;

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
            neighborsAdapter = new NeighborsAdapter(initiativesModel.getiId(),arrayList, InitiativeDetailsActivity.this);
            rvNeighbors.setAdapter(neighborsAdapter);
            arrayList.clear();
            tvTipo = findViewById(R.id.tvTipo);
            tvStatus = findViewById(R.id.tvStatus);
            tvTitle = findViewById(R.id.tvTitle);
            tvDesc = findViewById(R.id.tvDesc);
            //set data on TextView's
            tvStatus.setText("Estado: " + initiativesModel.getiStatus());
            tvTitle.setText("Título: " + initiativesModel.getiTitle());
            tvTipo.setText("Tipo: " + initiativesModel.getiTipo());
            tvDesc.setText("Descripción :" + initiativesModel.getiTipo());
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
                    finish();
                }
            });
            fbEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(InitiativeDetailsActivity.this,EditInitiativeActivity.class);
                    intent.putExtra("ZXC",new Gson().toJson(initiativesModel));
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
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
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
                       // Toast.makeText(InitiativeDetailsActivity.this, "Iniciativa eliminada", Toast.LENGTH_SHORT).show();
                        finish();
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