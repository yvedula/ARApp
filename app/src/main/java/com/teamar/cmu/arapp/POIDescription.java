package com.teamar.cmu.arapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class POIDescription extends ListActivity {
    private String poi_name;
    private TextView poi_name_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poidescription);

        Intent intent = getIntent();
        poi_name = intent.getStringExtra("poi_name");
        poi_name_textView = (TextView) findViewById(R.id.poi_name);
        poi_name_textView.setText(poi_name);

        String[] pois = {"AR1", "AR2", "AR3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, pois);
        setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    }
}
