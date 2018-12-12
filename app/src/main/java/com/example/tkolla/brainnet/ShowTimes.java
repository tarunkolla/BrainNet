package com.example.tkolla.brainnet;

import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ShowTimes extends AppCompatActivity {

    public TextView cloudTime;
    public TextView fogTime;
    public TextView loginStatus;
    public TextView batterylevel;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged__in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        cloudTime = (TextView) findViewById(R.id.cloudTime);
        fogTime = (TextView) findViewById(R.id.fogTime);
        loginStatus = (TextView) findViewById(R.id.loginstatus);
        batterylevel = (TextView) findViewById(R.id.batterylevel);
        BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        if(bundle.getString("config").toString().equals("automatic")) {
            long cloud = bundle.getLong("cloudEnd") - bundle.getLong("cloudStart");

            System.out.println(cloud);
            cloudTime.setText("Cloud response:   " + cloud + "ms");
            if(cloud < 0){
                cloudTime.setText("Cloud response:   " + "Not recorded");
            }

            long fog = bundle.getLong("fogEnd") - bundle.getLong("fogStart");
            System.out.println(fog);
            fogTime.setText("Fog response: " + fog + "ms");
            if(fog < 0){
                fogTime.setText("Fog response: " + "Not recorded" );
            }

            loginStatus.setText(bundle.getString("login status"));
            batterylevel.setText("battery % " + batLevel);
        }
        if(bundle.getString("config").toString().equals("cloud")) {
            long cloud = bundle.getLong("cloudEnd") - bundle.getLong("cloudStart");
            System.out.println(cloud);
            cloudTime.setText("Cloud response: " + cloud );
            fogTime.setText("Fog Response: -");
            loginStatus.setText(bundle.getString("login status"));
            batterylevel.setText("battery % " + batLevel);


        }
        if(bundle.getString("config").toString().equals("fog")) {
            long fog = bundle.getLong("fogEnd") - bundle.getLong("fogStart");
            System.out.println(fog);
            cloudTime.setText("Cloud response: -" );
            fogTime.setText("Fog response: " + fog);
            loginStatus.setText(bundle.getString("login status"));

            batterylevel.setText("battery % " + batLevel);
        }


    }

}
