package com.teamar.cmu.arapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class POIDescription extends ListActivity {
    private String poi_name;
    private int poi_id;
    private String poi_description;
    private TextView poi_name_textView;
    private TextView poi_description_textView;
    private Button navigate_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poidescription);

        Intent intent = getIntent();
        poi_id = intent.getIntExtra("poi_id", 0);
        poi_name = intent.getStringExtra("poi_name");
        poi_description = intent.getStringExtra("poi_description");
        poi_name_textView = (TextView) findViewById(R.id.poi_name);
        poi_description_textView = (TextView) findViewById(R.id.poi_description);
        navigate_button = (Button)findViewById(R.id.button_start_navigation);

        poi_name_textView.setText(poi_name);
        poi_description_textView.setText(poi_description);
        String[] pois = {"AR1", "AR2", "AR3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, pois);
        setListAdapter(adapter);

        navigate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(POIDescription.this, MainActivity.class);
                intent.putExtra("poi_id", poi_id);
                displayToast("ID: "+poi_id);
                intent.putExtra("poi_name", poi_name);
                intent.putExtra("poi_description", poi_description);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    }

    public void displayToast(String str)
    {
        Toast.makeText(POIDescription.this, str, Toast.LENGTH_SHORT).show();
    }
}
