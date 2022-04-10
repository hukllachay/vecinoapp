package com.zahid_iqbal699.vecinoapp.Adapters;

import static com.zahid_iqbal699.vecinoapp.Activities.SplashScreen.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zahid_iqbal699.vecinoapp.Models.NeighborsModel;
import com.zahid_iqbal699.vecinoapp.R;

import java.util.ArrayList;

public class NeighborsAdapter extends RecyclerView.Adapter<NeighborsAdapter.ViewHolder> {

    private ArrayList<NeighborsModel> arrayList;
    private final Context context;
    private String iId;

    public NeighborsAdapter(String s, ArrayList<NeighborsModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
        this.iId = s;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.signle_item_neighbour, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NeighborsModel projectModel = arrayList.get(position);
        holder.tvName.setText("Nombre: " + projectModel.getnName());
        holder.tvStatus.setText("Estado: " + projectModel.getnParStatus());
        holder.TvNotes.setText("notas: " + projectModel.getnNotes());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogueToChangeStatus(projectModel, iId);
            }
        });
    }

    private void showDialogueToChangeStatus(NeighborsModel projectModel, String id) {
        try {
            DatabaseReference tableInitiatives = FirebaseDatabase.getInstance().getReference().child("Initiatives");
            final String[] selectedStatus = {"Pendiente"};
            final BottomSheetDialog dialog = new BottomSheetDialog(context);
            dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.signle_layout_edit);

            EditText etNotes = (EditText) dialog.findViewById(R.id.etNotes);
            ProgressBar pbAdd = (ProgressBar) dialog.findViewById(R.id.pbAdd);
            Spinner spStatus = (Spinner) dialog.findViewById(R.id.spStatus);
            //set status data on spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_spinner_item, context.getResources().getStringArray(R.array.status_array));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spStatus.setAdapter(adapter);
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
            Button dialogOk = (Button) dialog.findViewById(R.id.btnOk);
            dialogOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NeighborsModel neighborsModel = projectModel;
                    if (etNotes.getText().toString().isEmpty()) {
                        neighborsModel.setnNotes("No hay notas disponibles");
                    } else {
                        neighborsModel.setnNotes(etNotes.getText().toString());
                    }
                    neighborsModel.setnParStatus(selectedStatus[0]);
                    pbAdd.setVisibility(View.VISIBLE);
                    Log.e(TAG, "onClick: id" + id);
                    tableInitiatives.child(id).child("Neighbors").child(projectModel.getnId()).setValue(neighborsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                            } else {
                                Log.e(TAG, "onComplete: " + task.getException());
                            }
                        }
                    });

                }
            });

            Button dialogButton = (Button) dialog.findViewById(R.id.btnCancel);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "openPrivacyPolicyUrl: " + e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView TvNotes, tvStatus, tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            TvNotes = itemView.findViewById(R.id.TvNotes);
        }
    }
}



