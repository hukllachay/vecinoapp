package com.grupo1.vecinoapp.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.grupo1.vecinoapp.Adapters.NeighborSelectAdapter;
import com.grupo1.vecinoapp.FirebaseHelper.FirebaseDatabaseHelpers;
import com.grupo1.vecinoapp.FirebaseHelper.PreferencesManager;
import com.grupo1.vecinoapp.Models.InitiativesModel;
import com.grupo1.vecinoapp.Models.NeighborsModel;
import com.grupo1.vecinoapp.Models.UserModel;
import com.grupo1.vecinoapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NeighborsSelectActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private InitiativesModel initiativesModel;
    private UserModel projectModel;
    private RecyclerView rvStudentList;
    private LinearLayout llNoDataFound;
    private List<UserModel> arrayList = new ArrayList<>();
    private TextView tvselectStudent;
    private PreferencesManager prefs;
    private FirebaseDatabaseHelpers firebaseDatabaseHelper;
    private NeighborSelectAdapter studentListAdapter;
    private ProgressBar pbMain;
    private ImageView ivChooseDate;
    private UserModel currentUser;
    private ProgressDialog progressDialog;
    private String type,iId;
    private DatabaseReference tableInitiatives;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neighbors_select);
        try {
            tableInitiatives = FirebaseDatabase.getInstance().getReference().child("Initiatives");

            initiativesModel = new Gson().fromJson(getIntent().getStringExtra("IM"), InitiativesModel.class);
            type = getIntent().getStringExtra("tps");

            iId=initiativesModel.getiId();
            Log.e(TAG, "onCreate: IID"+initiativesModel.getiId() );
            Log.e(TAG, "onCreate: IID"+initiativesModel.getiTitle() );
            String date = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
            //Support Toolbar
            Toolbar toolbarToolbar = findViewById(R.id.toolbarToolbar);
            getSupportActionBar();
            setSupportActionBar(toolbarToolbar);
            Objects.requireNonNull(getSupportActionBar()).setTitle("Choose Your Provider");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbarToolbar.setNavigationOnClickListener(v -> finish());
            //End Of toolbar text
            prefs = new PreferencesManager(NeighborsSelectActivity.this);
            currentUser = prefs.getCurrentUser();
            firebaseDatabaseHelper = new FirebaseDatabaseHelpers(NeighborsSelectActivity.this);
            llNoDataFound = findViewById(R.id.llNoDataFound);
            rvStudentList = findViewById(R.id.rvStudentList);
            pbMain = findViewById(R.id.pbStudentList);
            tvselectStudent = findViewById(R.id.tvselectStudent);
            rvStudentList.setHasFixedSize(true);
            rvStudentList.setLayoutManager(new LinearLayoutManager(NeighborsSelectActivity.this));
            studentListAdapter = new NeighborSelectAdapter(NeighborsSelectActivity.this, (ArrayList<UserModel>) arrayList);
            rvStudentList.setAdapter(studentListAdapter);
            setupProgressDialog();
            tvselectStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<UserModel> actorList = ((NeighborSelectAdapter) rvStudentList.getAdapter()).getSelectActorList();
                    if (actorList.size() < 1) {
                        Toast.makeText(NeighborsSelectActivity.this, "No User Selected", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressDialog.show();
                        if (type.equals("single")) {
                            Log.e(TAG, "onClick: single");
                            for (int i = 0; i < actorList.size(); i++) {
                                UserModel newModel = actorList.get(i);
                                //  DatabaseReference tablePresence = FirebaseDatabase.getInstance().getReference().child("Initiatives");
                                NeighborsModel neighborsModel = new NeighborsModel();
                                neighborsModel.setnId(newModel.getUid());
                                neighborsModel.setnName(newModel.getuName());
                                neighborsModel.setnNotes("No Description Available");
                                neighborsModel.setnParStatus("Pendiente");
                                int finalI = i;
                                Query query = tableInitiatives.child(iId).child("Neighbors").child(neighborsModel.getnId());
                                Log.e(TAG, "onComplete: loop");
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Log.e(TAG, "onDataChange: snapshott" + snapshot);
                                        if (!snapshot.exists()) {
                                            tableInitiatives.child(iId).child("Neighbors").child(neighborsModel.getnId()).setValue(neighborsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                    } else {
                                                        Log.e(TAG, "onComplete: Error " + task.getException());
                                                        Toast.makeText(NeighborsSelectActivity.this, "Error:" + task.getException(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        } else {
                                            Log.e(TAG, "onDataChange: already available");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e(TAG, "onCancelled: " + error.toString());
                                    }
                                });

                                if (finalI == (actorList.size() - 1)) {
                                    progressDialog.dismiss();
                                    Toast.makeText(NeighborsSelectActivity.this, "Vecinos Agregados", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(NeighborsSelectActivity.this, MainActivity.class));
                                    finish();
                                }
                            }

                        }
                        else
                            {
                            tableInitiatives.child(iId).setValue(initiativesModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        for (int i = 0; i < actorList.size(); i++) {
                                            UserModel newModel = actorList.get(i);
                                            //  DatabaseReference tablePresence = FirebaseDatabase.getInstance().getReference().child("Initiatives");
                                            NeighborsModel neighborsModel = new NeighborsModel();
                                            neighborsModel.setnId(newModel.getUid());
                                            neighborsModel.setnName(newModel.getuName());
                                            neighborsModel.setnNotes("No Description Available");
                                            neighborsModel.setnParStatus("Pendiente");
                                            int finalI = i;

                                          //  Query query = tableInitiatives.child(initiativesModel.getiId()).child("Neighbors").child(neighborsModel.getnId());
                                            Log.e(TAG, "onComplete: loop"+neighborsModel.getnId());
                                            tableInitiatives.child(iId).child("Neighbors").child(neighborsModel.getnId()).setValue(neighborsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                    } else {
                                                        Log.e(TAG, "onComplete: Error " + task.getException());
                                                        Toast.makeText(NeighborsSelectActivity.this, "Error:" + task.getException(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

/*
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Log.e(TAG, "onDataChange: snapshott" + snapshot);
                                                    if (!snapshot.exists()) {

                                                    } else {
                                                        Log.e(TAG, "onDataChange: already available");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.e(TAG, "onCancelled: " + error.toString());
                                                }
                                            });
*/

                                            if (finalI == (actorList.size() - 1)) {
                                                progressDialog.dismiss();
                                                Toast.makeText(NeighborsSelectActivity.this, "Neighbours Added", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(NeighborsSelectActivity.this, "Error:" + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }
                }
            });
            ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    Toast.makeText(NeighborsSelectActivity.this, "on Move", Toast.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                    //Remove swiped item from list and notify the RecyclerView
                    if (swipeDir == ItemTouchHelper.RIGHT) {
                        //whatever code you want the swipe to perform
                        studentListAdapter.notifyDataSetChanged();
                        Toast.makeText(NeighborsSelectActivity.this, "on Swiped right ", Toast.LENGTH_SHORT).show();

                    }
                    if (swipeDir == ItemTouchHelper.LEFT) {
                        //whatever code you want the swipe to perform
                        studentListAdapter.notifyDataSetChanged();
                        Toast.makeText(NeighborsSelectActivity.this, "on Swiped left", Toast.LENGTH_SHORT).show();
                    }

                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(rvStudentList);
            //firebaseDatabaseHelper.queryStudentData(currentUser.getCampus(), currentUser.getDepartment(), MarkPresentActivity.this);
            getLaboursList();
        } catch (
                Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }

    }

    private void getLaboursList() {
        try {
            pbMain.setVisibility(View.VISIBLE);
            DatabaseReference tablePlayList = FirebaseDatabase.getInstance().getReference().child("Users");
            //Query query = tablePlayList.orderByChild("pid").equalTo(projectModel.getPid());
            //Log.e(TAG, "getAllExpenses: PID:"+projectModel.getPid() );
            tablePlayList.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.e(TAG, "onDataChange: " + snapshot);
                    ArrayList<UserModel> arrayList1 = new ArrayList<>();
                    arrayList1.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        UserModel commentModel = dataSnapshot.getValue(UserModel.class);
                        arrayList1.add(commentModel);
                    }
                    arrayList.clear();
                    pbMain.setVisibility(View.GONE);
                    arrayList.addAll(arrayList1);
                    if (arrayList.size() < 1) {
                        llNoDataFound.setVisibility(View.VISIBLE);
                    } else {
                        llNoDataFound.setVisibility(View.GONE);
                    }
                    studentListAdapter.notifyDataSetChanged();
                    arrayList1.clear();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    pbMain.setVisibility(View.GONE);
                    Toast.makeText(NeighborsSelectActivity.this, "Error" + error.toString(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onCancelled: " + error);
                }

            });

        } catch (Exception e) {
            Log.e(TAG, "getlabourList: " + e.toString());
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(NeighborsSelectActivity.this);
        progressDialog.setTitle("Agregar iniciativa");
        progressDialog.setMessage("Espere mientras configuramos su información");
        progressDialog.setCancelable(false);
    }
}