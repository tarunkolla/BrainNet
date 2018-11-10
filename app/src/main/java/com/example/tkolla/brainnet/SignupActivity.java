package com.example.tkolla.brainnet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {



    private EditText username;

    DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebase = FirebaseDatabase.getInstance().getReference("UserAccount");

        Button register = (Button) findViewById(R.id.button_registered),
                select_password = (Button) findViewById(R.id.button_brainwave_password);

        username = (EditText) findViewById(R.id.select_user_name);


        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                addUser();

            }
        });

    }

    public void addUser(){

        String UserName = username.getText().toString();

        if(!TextUtils.isEmpty(UserName)) {
            String NewId = firebase.push().getKey();

            Database UserNameData = new Database(NewId, UserName);

            firebase.child(NewId).setValue(UserNameData);
            username.setText("");
        }
        else {
            Toast.makeText(SignupActivity.this, "Invalid", Toast.LENGTH_SHORT);
        }


    }
}
