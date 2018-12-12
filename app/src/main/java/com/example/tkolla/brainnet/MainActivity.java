package com.example.tkolla.brainnet;




import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tkolla.brainnet.R;


import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public Intent show_times;
    private int PERMISSION_CODE = 1;
    public int flag =0;
    String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private EditText username;

    public Button brainwave;

    public TextView brain_wave_name;

    public String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    public StringBuilder returnMsg = new StringBuilder();

    public long cloudStartTime;
    public long fogStartTime;

    public long cloudEndTime;
    public long fogEndTime;

    public Spinner server;
    public long endTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (EditText) findViewById(R.id.editText_userName);
        brain_wave_name = (TextView) findViewById(R.id.brain_wave_name);
        //spinner for server selection
        String[] arraySpinner = new String[]{
                "Automatic", "Cloud Server", "Fog Server"
        };
        server = (Spinner) findViewById(R.id.spinner_server);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        server.setAdapter(adapter);


        //button to navigate to sign up activity
        final Button register = (Button) findViewById(R.id.button_register);
        final Button authenticate = (Button) findViewById(R.id.button_authenticate);
        register.setOnClickListener(this);
        authenticate.setOnClickListener(this);
        brainwave = (Button) findViewById(R.id.button_brainwave);

        brainwave.setOnClickListener(this);


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

        if((ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)  && (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) ) {

            Toast.makeText(getApplicationContext(), "Required permissions granted", Toast.LENGTH_SHORT).show();

        }


        else{
            brainwave.setEnabled(false);
            for (String permission : PERMISSIONS)
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            PERMISSIONS,
                            PERMISSION_CODE);
                }


        }
    }

    public void onStart() {


        super.onStart();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED){
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



            case R.id.button_brainwave:
                Intent docfile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                docfile.addCategory(Intent.CATEGORY_OPENABLE);
                docfile.setType("*/*");
                startActivityForResult(Intent.createChooser(docfile, "Open EDF"), 1);

                break;


            case R.id.button_register:
                Intent sign_up;
                sign_up = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(sign_up);
                break;

            case R.id.button_authenticate:
                if (username.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please provide Username ", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (brain_wave_name.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please provide brainwave file ", Toast.LENGTH_SHORT).show();
                    break;
                }



                try{


                    Map<String, String> map = new HashMap<String, String>();
                    map.put("username", username.getText().toString());
                    map.put("edf", brain_wave_name.getText().toString());

                    List<JSONObject> jsonObj = new ArrayList<JSONObject>();
                    JSONObject postData = new JSONObject();

                    postData.put("username", username.getText());
                    postData.put("edf", brain_wave_name.getText().toString());

                    // have to register in both the places
                    //String respCloudServer = sendPostRequest("http://18.217.22.211:3400/register", postData);
                    if(server.getSelectedItem().toString().equals("Automatic")) {
                        fogStartTime = System.currentTimeMillis();
                        sendPostRequest1("http://10.152.75.201:5000/login", postData);

                        cloudStartTime = System.currentTimeMillis();
                        sendPostRequest2("http://18.217.22.211:3400/login", postData);
                    }
                    if(server.getSelectedItem().toString().equals("Cloud Server")) {

                        cloudStartTime = System.currentTimeMillis();
                        sendPostRequest2("http://18.217.22.211:3400/login", postData);
                    }
                    if(server.getSelectedItem().toString().equals("Fog Server")) {
                        fogStartTime = System.currentTimeMillis();
                        sendPostRequest1("http://10.152.75.201:5000/login", postData);

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1: {

                if (resultCode == RESULT_OK) {


                        Uri uri = data.getData();
                        File myFile = new File(uri.toString());
                        String path = myFile.getAbsolutePath();
                        String displayName = null;
                        String uriString = uri.toString();

                        if (uriString.startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = this.getContentResolver().query(uri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = myFile.getName();
                        }

                        brain_wave_name.setText(displayName);

                        System.out.print(displayName);


                    super.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }
    public void sendPostRequest1(String requestUrl, final JSONObject p) {
        StringBuffer b = new StringBuffer();

        final JSONObject payload = p;

        try {
            final URL url = new URL(requestUrl);



            Thread thread = new Thread(new Runnable(){
                @Override
                public void run(){
                    String s;
                    try{
                        StringBuffer jsonString = new StringBuffer();
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Accept", "application/json");
                        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                        writer.write(payload.toString());
                        writer.close();
                        try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));


                            String line;
                            while ((line = br.readLine()) != null) {
                                jsonString.append(line);
                            }
                            br.close();
                            fogEndTime = System.currentTimeMillis();
                        }
                        catch(Exception e) {
                            returnMsg.append("Error Occured!");
                            makeToast(returnMsg.toString());
                        }
                        returnMsg.append("OK");
                        makeToast(returnMsg.toString());
                        returnMsg.setLength(0);
                        connection.disconnect();


                    }
                    catch(Exception e ){
                        e.printStackTrace();
                    }

                }

            });
            thread.start();


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }


    }
    public void sendPostRequest2(String requestUrl, final JSONObject p) {
        StringBuffer b = new StringBuffer();

        final JSONObject payload = p;

        try {
            final URL url = new URL(requestUrl);



            Thread thread = new Thread(new Runnable(){
                @Override
                public void run(){
                    String s;
                    try{
                        StringBuffer jsonString = new StringBuffer();
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setRequestMethod("POST");
                        connection.setRequestProperty("Accept", "application/json");
                        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                        writer.write(payload.toString());
                        writer.close();
                        try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));


                            String line;
                            while ((line = br.readLine()) != null) {
                                jsonString.append(line);
                            }
                            br.close();
                            cloudEndTime = System.currentTimeMillis();
                        }
                        catch(Exception e) {
                            returnMsg.append("Error Occured!");
                            makeToast(returnMsg.toString());
                        }
                        returnMsg.append("OK");
                        makeToast(returnMsg.toString());
                        returnMsg.setLength(0);
                        connection.disconnect();


                    }
                    catch(Exception e ){
                        e.printStackTrace();
                    }

                }

            });
            thread.start();


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }


    }
    public void makeToast(String msg){

        show_times = new Intent(MainActivity.this, ShowTimes.class);
        if(server.getSelectedItem().toString().equals("Automatic")) {
            show_times.putExtra("cloudStart", cloudStartTime);
            show_times.putExtra("cloudEnd", cloudEndTime);
            show_times.putExtra("fogStart", fogStartTime);
            show_times.putExtra("fogStart", fogEndTime);
            show_times.putExtra("config","automatic");
        }
        if(server.getSelectedItem().toString().equals("Cloud Server")){
            show_times.putExtra("cloudStart", cloudStartTime);
            show_times.putExtra("cloudEnd", cloudEndTime);
            show_times.putExtra("config","cloud");
        }
        if(server.getSelectedItem().toString().equals("Fog Server")){
            show_times.putExtra("fogStart", fogStartTime);
            show_times.putExtra("fogEnd", fogEndTime);
            show_times.putExtra("config","fog");
        }
        if(returnMsg.toString() == ""){
            Toast.makeText(getApplicationContext(), "Logged In!", Toast.LENGTH_SHORT).show();
        }
        else{
            if(returnMsg.toString().equals("OK")) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Logged In!", Toast.LENGTH_SHORT).show());

            }else{
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "login failed!", Toast.LENGTH_SHORT).show());
            }
        }
        try {
            Thread.sleep(5000);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        if(returnMsg.toString().equals("OK") || returnMsg.toString().equals(""))
            show_times.putExtra("login status", "Login successful");
        else
            show_times.putExtra("login status", "Login unsuccessful");
        if(flag == 0){
        startActivity(show_times);
        flag = 1;
        }
    }





}

