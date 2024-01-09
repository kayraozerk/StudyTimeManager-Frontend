package com.example.cs310_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class StopwatchAdapter extends ArrayAdapter<String> {

    public StopwatchAdapter(Context context, ArrayList<String> names) {
        super(context, 0, names);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView textViewName = convertView.findViewById(R.id.textViewName);
        Button buttonDelete = convertView.findViewById(R.id.buttonDelete);

        textViewName.setText(name);
        buttonDelete.setOnClickListener(v -> {
            remove(name);
            notifyDataSetChanged();
            // Remove the name from SharedPreferences here
        });

        return convertView;
    }
}
