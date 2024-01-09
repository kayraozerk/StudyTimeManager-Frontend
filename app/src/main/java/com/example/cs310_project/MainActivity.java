package com.example.cs310_project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Set<String> namesSet;
    private StopwatchAdapter adapter;
    private ArrayList<String> namesList;
    private SharedPreferences sharedPreferences;
    private ListView namesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        namesListView = findViewById(R.id.namesListView);
        Button createStopwatchButton = findViewById(R.id.createStopwatchButton);

        sharedPreferences = getSharedPreferences("StopwatchNames", Context.MODE_PRIVATE);
        namesSet = new HashSet<>(sharedPreferences.getStringSet("names", new HashSet<>())); // Load saved names
        namesList = new ArrayList<>(namesSet);
        adapter = new StopwatchAdapter(this, namesList);
        namesListView.setAdapter(adapter);

        createStopwatchButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter Course Name");
            final EditText input = new EditText(this);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String courseName = input.getText().toString();
                if (!namesSet.contains(courseName)) {
                    saveName(courseName);
                    adapter.add(courseName);
                    adapter.notifyDataSetChanged();
                } else {
                    // Simulate click if name exists
                    int position = namesList.indexOf(courseName);
                    namesListView.performItemClick(
                            namesListView.getAdapter().getView(position, null, null),
                            position,
                            namesListView.getAdapter().getItemId(position)
                    );
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        namesListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, StopwatchActivity.class);
            String courseName = adapter.getItem(position);
            intent.putExtra("COURSE_NAME", courseName);
            startActivity(intent);
        });
    }

    private void saveName(String name) {
        namesSet.add(name);
        updateSharedPreferences();
    }

    private void deleteName(String name) {
        namesSet.remove(name);
        updateSharedPreferences();

        // Clear specific stopwatch data
        SharedPreferences stopwatchPrefs = getSharedPreferences("StopwatchPrefs_" + name, MODE_PRIVATE);
        SharedPreferences.Editor stopwatchEditor = stopwatchPrefs.edit();
        stopwatchEditor.clear();
        stopwatchEditor.apply();
    }

    private void updateSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("names", new HashSet<>(namesSet)); // Save the updated set
        editor.apply();
    }

    private class StopwatchAdapter extends ArrayAdapter<String> {

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
            buttonDelete.setFocusable(false);

            textViewName.setText(name);
            buttonDelete.setOnClickListener(v -> {
                remove(name);
                notifyDataSetChanged();
                deleteName(name); // Remove the name from SharedPreferences
            });

            return convertView;
        }
    }
}
