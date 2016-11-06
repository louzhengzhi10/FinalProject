package com.example.samuel.finalproject;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by mengxiongliu on 05/11/2016.
 */

public class CustomList extends ArrayAdapter<String>{

    private final Activity context;
    private final String[] text;
    private final int layout_id;
    public CustomList(Activity context, int layout_id, String[] text) {
        super(context, layout_id, text);
        this.context = context;
        this.layout_id = layout_id;
        this.text = text;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(layout_id, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        txtTitle.setText(text[position]);

        return rowView;
    }
}