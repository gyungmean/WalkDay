package com.ddw.andorid.walkday;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MyCursorAdapter extends CursorAdapter {

    LayoutInflater inflater;
    int layout;

    public MyCursorAdapter(Context context, int layout, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(layout, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvDate = view.findViewById(R.id.tvModifyDate);
        TextView tvDistance = view.findViewById(R.id.tvDistance);
        TextView tvTime = view.findViewById(R.id.tvTime);
        TextView tvKcal = view.findViewById(R.id.tvKcal);
        TextView tvName = view.findViewById(R.id.tvName);

        tvDate.setText(cursor.getString(cursor.getColumnIndex(WalkDBHelper.COL_DATE)));
        tvDistance.setText(cursor.getString(cursor.getColumnIndex(WalkDBHelper.COL_DIS)));
        tvTime.setText(cursor.getString(cursor.getColumnIndex(WalkDBHelper.COL_TIME)));
        tvKcal.setText(cursor.getString(cursor.getColumnIndex(WalkDBHelper.COL_KCAL)));
        tvName.setText(cursor.getString(cursor.getColumnIndex(WalkDBHelper.COL_NAME)));
    }
}
