package com.example.tkolla.brainnet;




import android.content.Intent;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Toast.makeText(getApplicationContext(), selected_server, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }


    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_register:
                Intent sign_up;
                sign_up = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(sign_up);
            default:
                break;
        }
    }



}

