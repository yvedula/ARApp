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

/**
 * Activity to display details of the selected POI.
 * Also displays a list of AR available at the POI.
 */
public class POIDescription extends ListActivity {

    /**
     * Variable to store the name of the POI.
     */
    private String poiName;
    /**
     * Variable to store the ID of the POI.
     */
    private int poiID;
    /**
     * Variable to store the description of the POI.
     */
    private String poiDescription;
    /**
     * Identifier for the text view displaying the name of the POI.
     */
    private TextView poiNameTextView;
    /**
     * Identifier for the text view displaying the name of the POI.
     */
    private TextView poiDescriptionTextView;
    /**
     * Identifier for the text view displaying the description of the POI.
     */
    private Button navigateButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poidescription);

        Intent intent = getIntent();
        poiID = intent.getIntExtra("poi_id", 0);
        poiName = intent.getStringExtra("poi_name");
        poiDescription = intent.getStringExtra("poi_description");
        poiNameTextView = (TextView) findViewById(R.id.poi_name);
        poiDescriptionTextView = (TextView) findViewById(R.id.poi_description);
        navigateButton = (Button) findViewById(R.id.button_start_navigation);

        poiNameTextView.setText(poiName);
        poiDescriptionTextView.setText(poiDescription);
        String[] pois = {"AR1", "AR2", "AR3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pois);
        setListAdapter(adapter);

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(POIDescription.this, MainActivity.class);
                intent.putExtra("poiID", poiID);
                displayToast("ID: " + poiID);
                intent.putExtra("poiName", poiName);
                intent.putExtra("poiDescription", poiDescription);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    }

    /**
     * Function to display a toast for LENGTH_SHORT duration.
     * @param str : String to displayed by the toast.
     */
    public void displayToast(final String str) {
        Toast.makeText(POIDescription.this, str, Toast.LENGTH_SHORT).show();
    }
}
