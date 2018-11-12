package com.example.tkolla.brainnet;




import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import butterknife.BindView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private int STORAGE_PERMISSION_CODE = 1;
    public Button brainwave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        brainwave = (Button) findViewById(R.id.button_brainwave_Signal);

        //spinner for server selection
        String[] arraySpinner = new String[]{
                "Automatic", "Cloud Server", "Fog Server"
        };
        final Spinner server = (Spinner) findViewById(R.id.spinner_server);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        server.setAdapter(adapter);


        //button to navigate to sign up activity
        final Button register = (Button) findViewById(R.id.button_register);
        register.setOnClickListener(this);


        server.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String selected_server = server.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(), "Server set to " + selected_server, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }

    public void onStart() {


        super.onStart();
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getApplicationContext(), "External storage permission granted", Toast.LENGTH_SHORT).show();

        }
        else{
            brainwave.setEnabled(false);
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == STORAGE_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            brainwave.setEnabled(true);
            Toast.makeText(getApplicationContext(), " remove this toast later ", Toast.LENGTH_SHORT).show();
        }
        else {
            //The app can do this all day till you allow access.
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onClick(View view) {
        switch(view.getId()){

            case R.id.button_brainwave_Signal:
                Readfiles read = new Readfiles();

            case R.id.button_register:
                Intent sign_up;
                sign_up = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(sign_up);
            default:
                break;
        }
    }




}

