package com.teamar.cmu.arapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseViewActivity extends AppCompatActivity {

    Button chooseMap;
    Button chooseList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_view);

        chooseList = (Button)findViewById(R.id.bChooseList);
        chooseMap = (Button)findViewById(R.id.bChooseMap);

        chooseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startListPOIActivity();
            }
        });

        chooseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMapPOIActivity();
            }
        });
    }

    public void startListPOIActivity()
    {
        Intent intent = new Intent(ChooseViewActivity.this, ListPOIActivity.class);
        startActivity(intent);
    }

    public void startMapPOIActivity()
    {
        Intent intent = new Intent(ChooseViewActivity.this, MapsActivity.class);
        startActivity(intent);
    }
}
