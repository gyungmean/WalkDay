package com.ddw.andorid.walkday;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WalkDogAdapter extends RecyclerView.Adapter<WalkDogAdapter.ViewHolder> {

    final static String TAG = "WalkDogAdapter";

    private ArrayList<Item> mData = new ArrayList<>();
    static private Context context;

    public WalkDogAdapter(Context mContext, ArrayList<DogDTO> list){
        context = mContext;
        for(DogDTO d : list){
            Item i = new Item();
            i.setSelected(false);
            i.setName(d.getName());
            i.setId(d.getId());

            mData.add(i);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvWalkDogName;
        public CheckBox cbWalkDog;

        public ViewHolder(View itemView) {
            super(itemView);
            tvWalkDogName = (TextView) itemView.findViewById(R.id.tvWalkDogName);
            cbWalkDog = (CheckBox) itemView.findViewById(R.id.cbWalkDog);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.listview_dog_check, parent, false);
        WalkDogAdapter.ViewHolder vh = new WalkDogAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final Item dog = mData.get(position);
        holder.tvWalkDogName.setText(dog.getName());

        holder.cbWalkDog.setOnCheckedChangeListener(null);
        holder.cbWalkDog.setChecked(dog.isSelected);

        holder.cbWalkDog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                Log.d(TAG, "onCheckedChanged!");

                if(isChecked){
                    holder.cbWalkDog.setChecked(true);
                    Log.d(TAG, "dog name " + dog.getName() + " check");
                    dog.setSelected(true);
                } else{
                    holder.cbWalkDog.setChecked(false);
                    Log.d(TAG, "dog name " + dog.getName() + " uncheck");
                    dog.setSelected(false);
                }
            }
        });

    }

    public int getItemCount() {
        return mData.size();
    }

    public ArrayList<Integer> checkList() {
        ArrayList<Integer> checked = new ArrayList<>();

        for(Item i : mData){
            if (i.getSelected()){
                checked.add(i.getId());
                Log.d(TAG, "add check list id : " + Integer.toString(i.getId()));
            }
        }

        return checked;
    }

    public class Item
    {
        boolean isSelected;
        int id;
        String name;

        public boolean getSelected(){ return isSelected; }
        public void setSelected(boolean selected) { this.isSelected = selected; }

        public String getName(){ return name; }
        public void setName(String name) { this.name = name; }

        public int getId(){ return id; }
        public void setId(int id) { this.id = id; }
    }
}

