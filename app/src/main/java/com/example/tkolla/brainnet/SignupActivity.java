package com.example.tkolla.brainnet;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class SignupActivity extends AppCompatActivity implements View.OnClickListener{




    private EditText username;

    public Button brainwave_password;

    public Button register;

    public TextView whatanedf;

    public File file;
    public String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    final StringBuilder returnMsg = new StringBuilder();

    public void makeToast(String msg){
        if(returnMsg.toString() == ""){
            Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
        }
        else{
            if(returnMsg.toString().equals("OK"))
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "successfully Registered!", Toast.LENGTH_SHORT).show());
            else{
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "error occured", Toast.LENGTH_SHORT).show());
            }
        }
    }

    public long cloudStartTime;
    public long fogStartTime;
    public long endTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        brainwave_password = (Button) findViewById(R.id.button_brainwave_password);

        brainwave_password.setOnClickListener(this);

        register = (Button) findViewById(R.id.button_registered);

        username = (EditText) findViewById(R.id.select_user_name);

        whatanedf = (TextView) findViewById(R.id.whatanedf);

        register.setOnClickListener(new View.OnClickListener(){
            @TargetApi(Build.VERSION_CODES.O)
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view){
                String urlString = "10.152.75.201/register";
                if(username.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(whatanedf.getText()==""){
                    Toast.makeText(getApplicationContext(), "Please select a brainwave to upload", Toast.LENGTH_SHORT).show();
                    return;
                }

                File brainwave = new File(file.getAbsolutePath(), (String) whatanedf.getText());

                try{


                    Map<String, String> map = new HashMap<String, String>();
                    map.put("username", username.getText().toString());
                    map.put("edf", (String) whatanedf.getText());

                    List<JSONObject> jsonObj = new ArrayList<JSONObject>();

                    JSONObject postData = new JSONObject();

                    postData.put("username", username.getText());
                    postData.put("edf", (String) whatanedf.getText());

                    // have to register in both the places
                    cloudStartTime = System.currentTimeMillis();
                    sendPostRequest("http://18.217.22.211:3400/register", postData);

                    fogStartTime = System.currentTimeMillis();
                    sendPostRequest("http://10.152.75.201:5000/register", postData);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        brainwave_password.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent docfile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                docfile.addCategory(Intent.CATEGORY_OPENABLE);
                docfile.setType("*/*");
                startActivityForResult(Intent.createChooser(docfile, "edf_thoughtID"), 1);
                Uri uri = docfile.getData();
            }
        });



    }

    public void sendPostRequest(String requestUrl, final JSONObject p) {
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

    @Override
    public void onClick(View view) {
        switch(view.getId()){



            case R.id.button_brainwave_password:
                Intent docfile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                docfile.addCategory(Intent.CATEGORY_OPENABLE);
                docfile.setType("*/*");
                startActivityForResult(Intent.createChooser(docfile, "Open"), 1);
                break;

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
                    file = new File(uri.toString());
                    String path = file.getAbsolutePath();
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
                        displayName = file.getName();
                    }

                    whatanedf.setText(displayName);




                    super.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }
}

