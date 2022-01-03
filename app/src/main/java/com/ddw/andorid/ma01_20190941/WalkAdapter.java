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

public class WalkAdapter  extends RecyclerView.Adapter<WalkAdapter.ViewHolder>{

    final static String TAG = "WalkAdapter";

    private ArrayList<WalkDTO> mData = null;
    static private Context context;

    WalkAdapter(Context mContext, ArrayList<WalkDTO> list) {
        context = mContext;
        mData = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWalkDate;
        TextView tvWalkPeople;
        TextView tvWalkTime;
        TextView tvWalkDistance;

        ViewHolder(View itemView) {
            super(itemView);

            tvWalkDate = itemView.findViewById(R.id.tvWalkDate);
            tvWalkPeople = itemView.findViewById(R.id.tvWalkPeople);
            tvWalkTime = itemView.findViewById(R.id.tvWalkTime);
            tvWalkDistance = itemView.findViewById(R.id.tvWalkDistance);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    WalkDTO walk = mData.get(pos);
                    Log.d(TAG, "pos: " + Integer.toString(pos));
                    if(pos != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, DetailWalkActivity.class);
                        Log.d(TAG, "walk id: " + Long.toString(walk.getId()));
                        intent.putExtra("id", Long.toString(walk.getId()));

                        notifyItemChanged(pos);

                        if (intent != null) context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                }
            });
        }
    }
    @NonNull
    @Override
    public WalkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.listview_walk_layout, parent, false);
        WalkAdapter.ViewHolder vh = new WalkAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(WalkAdapter.ViewHolder holder, int position) {
        WalkDTO walk = mData.get(position);

        holder.tvWalkDate.setText(walk.getDate());
        holder.tvWalkPeople.setText(walk.getPeople());
        holder.tvWalkTime.setText(walk.getTime());
        holder.tvWalkDistance.setText(walk.getDistance());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
