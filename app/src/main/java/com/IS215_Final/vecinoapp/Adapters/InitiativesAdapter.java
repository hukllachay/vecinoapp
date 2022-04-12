package com.IS215_Final.vecinoapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.IS215_Final.vecinoapp.Activities.InitiativeDetailsActivity;
import com.IS215_Final.vecinoapp.Models.InitiativesModel;
import com.IS215_Final.vecinoapp.Models.NeighborsModel;
import com.IS215_Final.vecinoapp.R;

import java.util.ArrayList;

public class InitiativesAdapter extends RecyclerView.Adapter<InitiativesAdapter.ViewHolder> {

    private static final String TAG = "TAG";
    private ArrayList<InitiativesModel> arrayList;
    private Context context;

    public InitiativesAdapter(ArrayList<InitiativesModel> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_item_initiative, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InitiativesModel projectModel = arrayList.get(position);
        holder.tvProjectName.setText(projectModel.getiTitle());
        holder.tvDate.setText(projectModel.getiDateFrom() + "-" + projectModel.getiDateTo());
        holder.tvProjectStatus.setText("Tipo:" + projectModel.getiTipo());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InitiativeDetailsActivity.class);
                intent.putExtra("IDM", new Gson().toJson(projectModel));
                context.startActivity(intent);
            }
        });
        loadNoOfNeighbors(projectModel, holder);
    }

    private void loadNoOfNeighbors(InitiativesModel projectModel, ViewHolder holder) {
        DatabaseReference tableInitiatives = FirebaseDatabase.getInstance().getReference().child("Initiatives");
        tableInitiatives.child(projectModel.getiId()).child("Neighbors").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<NeighborsModel> arrayList1 = new ArrayList<>();
                arrayList1.clear();
                float complete = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NeighborsModel neighborsModel = dataSnapshot.getValue(NeighborsModel.class);
                    arrayList1.add(neighborsModel);
                    assert neighborsModel != null;
                   // Toast.makeText(context, ""+neighborsModel.getnParStatus(), Toast.LENGTH_SHORT).show();
                    if (neighborsModel.getnParStatus().equals("Completado")) {
                        complete=complete+1;
                    }
                }
                Log.e(TAG, "onDataChange: complet"+complete );
                if (complete > 0) {
                    float size=arrayList1.size();
                    float percentage = complete / size;
                    Log.e(TAG, "onDataChange: size"+size+" per" +percentage);
                    percentage=percentage*100;

                    holder.tvPercent.setText(String.format("%.2f",percentage)+"%");
                    if (percentage < 50) {
                        holder.TvProgressStatus.setText("Pendiente");
                    } else if (percentage > 50) {
                        holder.TvProgressStatus.setText("En Progreso");
                    }
                    if (arrayList1.size() == complete) {
                        holder.TvProgressStatus.setText("Completado");
                    }
                } else {
                    holder.tvPercent.setText("0");
                    holder.TvProgressStatus.setText("Pendiente");

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void filterList(ArrayList<InitiativesModel> arrayList1) {
        this.arrayList = arrayList1;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProjectName, tvProjectStatus, tvDate, TvProgressStatus, tvPercent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPercent = itemView.findViewById(R.id.tvPercent);
            TvProgressStatus = itemView.findViewById(R.id.TvProgressStatus);
            tvProjectStatus = itemView.findViewById(R.id.tvProjectStatus);
            tvProjectName = itemView.findViewById(R.id.tvProjectName);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}

