package com.zahid_iqbal699.vecinoapp.Activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zahid_iqbal699.vecinoapp.Adapters.InitiativesAdapter;
import com.zahid_iqbal699.vecinoapp.BuildConfig;
import com.zahid_iqbal699.vecinoapp.FirebaseHelper.PreferencesManager;
import com.zahid_iqbal699.vecinoapp.Models.InitiativesModel;
import com.zahid_iqbal699.vecinoapp.Models.UserModel;
import com.zahid_iqbal699.vecinoapp.R;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private EditText et_workshop_search;
    public static final String TAG = "TAG";
    //floating action button
    FloatingActionButton floatingActionButton;
    private PreferencesManager preferencesManager;
    private UserModel currentUser;
    ArrayList<InitiativesModel> arrayList = new ArrayList<>();
    private LinearLayout llNoDataFound;
    private RecyclerView rvPlayListNames;
    private InitiativesAdapter initiativesAdapter;
    private ProgressBar pbMain;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            //
            preferencesManager = new PreferencesManager(this);
            currentUser = preferencesManager.getCurrentUser();
            floatingActionButton = findViewById(R.id.floatingActionButton);
            et_workshop_search = findViewById(R.id.et_workshop_search);
            //Recycler View
            llNoDataFound = findViewById(R.id.llNoDataFound);
            pbMain = findViewById(R.id.pbMain);

            rvPlayListNames = findViewById(R.id.rvPlayListNames);
            rvPlayListNames.setHasFixedSize(true);
            rvPlayListNames.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            initiativesAdapter = new InitiativesAdapter(arrayList, MainActivity.this);
            rvPlayListNames.setAdapter(initiativesAdapter);
            arrayList.clear();
            initiativesAdapter.notifyDataSetChanged();
            getAllPlayList();
            initiativesAdapter.notifyDataSetChanged();

            //Toolbar
            Toolbar toolbar = findViewById(R.id.toolbarMain);
            setSupportActionBar(toolbar);
            //set up Drawer
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer,
                    toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(this);
            //Search Function
            try {
                et_workshop_search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        filter(s.toString());
                        if(s.toString().isEmpty()){
                            et_workshop_search.setCursorVisible(false);
                        }

                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "onCreate: " + e.toString());
            }
            //set up profile
            setUserProfile();
            floatingActionButton.setOnClickListener(view -> {
                startActivity(new Intent(MainActivity.this, AddInitiativeActivity.class));
            });

        } catch (Exception e) {
            Log.e(TAG, "onCreate: MainActivity EXC :" + e.toString());
        }
    }


    private void filter(String toString) {
        ArrayList<InitiativesModel> arrayList1 = new ArrayList<>();
        for (InitiativesModel item : arrayList) {
            if (item.getiTitle().toLowerCase().contains(toString.toLowerCase())) {
                arrayList1.add(item);
            }
        }
        initiativesAdapter.filterList(arrayList1);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        switch (id) {
            case R.id.share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out latest punjabi songs at: https://play.google.com/store/apps/details?id="
                                + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.rateus:
                try {
                    MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + MainActivity.this.getPackageName())));
                    return false;
                } catch (ActivityNotFoundException unused2) {
                    Toast.makeText(this, " unable to find market app", Toast.LENGTH_SHORT).show();
                    return false;
                }
            case R.id.logout:
                logout();
        }

        return false;
    }

    private void logout() {
        try {
            new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                    .setTitleText("¿Estás seguro")
                    .setContentText("de cerrar sesión?")
                    .setConfirmButton("Confirmar", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                            try {
                                FirebaseAuth.getInstance().signOut();
                                Log.e("TAG", "onClick: logout");
                            } catch (Exception e) {
                                Log.e("TAG", "onClick: error while login");
                                e.printStackTrace();
                            }
                            startActivity(new Intent(MainActivity.this, LogInActivity.class));
                            finish();
                        }
                    })
                    .setCancelButton("Cancelar", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    }).show();
        } catch (Exception e) {
            Log.e(TAG, "logout: " + e.toString());
        }
    }

    private void setUserProfile() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        ((TextView) headerLayout.findViewById(R.id.tvNavName)).setText(currentUser.getuName());
        ((TextView) headerLayout.findViewById(R.id.tvNavEmail)).setText(currentUser.getuEmail());
        //CircularImageView userImage = headerLayout.findViewById(R.id.ivNavUser);
        //  Glide.with(headerLayout).load(currentUser.getImage()).into(userImage);
        //headerLayout.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, Student_ProfileActivity.class)));
    }

    private void getAllPlayList() {
        pbMain.setVisibility(View.VISIBLE);
        DatabaseReference tablePlayList = FirebaseDatabase.getInstance().getReference().child("Initiatives");
        Query query = tablePlayList.orderByChild("iDate").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //Log.e(TAG, "getAllExpenses: PID:"+projectModel.getPid() );
        tablePlayList.orderByChild("iDate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e(TAG, "onDataChange: " + snapshot);
                ArrayList<InitiativesModel> arrayList1 = new ArrayList<>();
                arrayList1.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    InitiativesModel commentModel = dataSnapshot.getValue(InitiativesModel.class);
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
                initiativesAdapter.notifyDataSetChanged();
                arrayList1.clear();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pbMain.setVisibility(View.GONE);
                Toast.makeText(context, "Error" + error.toString(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onCancelled: " + error);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllPlayList();
    }
}
