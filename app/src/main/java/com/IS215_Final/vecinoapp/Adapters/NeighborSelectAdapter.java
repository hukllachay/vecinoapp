package com.IS215_Final.vecinoapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.IS215_Final.vecinoapp.Models.UserModel;
import com.IS215_Final.vecinoapp.R;

import java.util.ArrayList;

public class NeighborSelectAdapter extends RecyclerView.Adapter<NeighborSelectAdapter.viewholder> {
    //  private OnItemCheckListener onItemClick;
    ArrayList<UserModel> arrayList;
    Context context;

    public NeighborSelectAdapter(Context context, ArrayList<UserModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_item_user_list, parent, false);
        return new viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder instanceof viewholder) {
            final UserModel model = arrayList.get(position);
            holder.Email.setText(String.valueOf(model.getuEmail()));
            holder.Name.setText(model.getuName());
            //Glide.with(context).load(model.getImage()).into(holder.image);
            holder.cbStudentList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isSelected = ((CheckBox) v).isChecked();
                    arrayList.get(position).setSelected(isSelected);
                    // Toast.makeText(context, ""+arrayList.get(position).getEmail(), Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class viewholder extends RecyclerView.ViewHolder {
        TextView Name, Email;
        CircularImageView image;
        CheckBox cbStudentList;
        View itemView;

        public viewholder(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.ivStudentlist);
            Name = v.findViewById(R.id.tvStudentNamelist);
            Email = v.findViewById(R.id.tvEmailStudentlist);
            cbStudentList = v.findViewById(R.id.cbStudentList);

        }

    }

    public ArrayList<UserModel> getSelectActorList() {
        ArrayList<UserModel> list = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).isSelected())
                list.add(arrayList.get(i));
        }
        return list;
    }
}


