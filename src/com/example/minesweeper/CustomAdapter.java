package com.example.minesweeper;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by neeraj on 9/5/17.
 */

public class CustomAdapter extends ArrayAdapter<ListItem> {

    private ArrayList<ListItem> listItems;

    CustomAdapter(Context context, ArrayList<ListItem> listItems) {
        super(context, 0, listItems);
        this.listItems = listItems;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public ListItem getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        Log.d("adapter","adapter called for position "+position);

        ListItem listItem = getItem(position);
        if(row == null){
            row = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView nameView =(TextView) row.findViewById(R.id.nameView);
        TextView scoreView =(TextView) row.findViewById(R.id.scoreView);
        TextView timeView =(TextView) row.findViewById(R.id.timeView);
        nameView.setText(listItem.getName());
        scoreView.setText(listItem.getScore());
        timeView.setText(listItem.getTime());

        return row;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
