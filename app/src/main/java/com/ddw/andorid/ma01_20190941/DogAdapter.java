package com.ddw.andorid.ma01_20190941;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DogAdapter extends RecyclerView.Adapter<DogAdapter.ViewHolder> {

    final static String TAG = "DogAdapter";

    private ArrayList<DogDTO> mData = null;
    static private Context context;

    DogAdapter(Context mContext, ArrayList<DogDTO> list) {
        context = mContext;
        mData = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDogName;
        TextView tvDogAge;
        TextView tvDogType;
        TextView tvDogWeight;

        ViewHolder(View itemView) {
            super(itemView);

            tvDogName = itemView.findViewById(R.id.tvDogName);
            tvDogAge = itemView.findViewById(R.id.tvDogAge);
            tvDogType = itemView.findViewById(R.id.tvDogType);
            tvDogWeight = itemView.findViewById(R.id.tvDogWeight);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    DogDTO dog = mData.get(pos);
                    Log.d(TAG, "pos: " + Integer.toString(pos));
                    if(pos != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, DogDetailActivity.class);
                        Log.d(TAG, "dog id: " + Integer.toString(dog.getId()));
                        intent.putExtra("id", Integer.toString(dog.getId()));

                        notifyItemChanged(pos);

                        if (intent != null) context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                }
            });
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.listview_dog_info_layout, parent, false);
        DogAdapter.ViewHolder vh = new DogAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(DogAdapter.ViewHolder holder, int position) {
        DogDTO dog = mData.get(position);
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        int nowYear = Integer.parseInt(format.format(date));
        int age = nowYear - Integer.parseInt(dog.getBirthY());

        holder.tvDogName.setText(dog.getName());
        holder.tvDogAge.setText(Integer.toString(age));
        holder.tvDogType.setText(dog.getType());
        holder.tvDogWeight.setText(Float.toString(dog.getWeight()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
