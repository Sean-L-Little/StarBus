package com.example.starraspberry;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private EditText editIP;
    private EditText editPort;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editIP= findViewById(R.id.editTextIP);
        editPort= findViewById(R.id.editTextPort);


        SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

        String savedIP= sharedPref.getString("savedIP", null);
        String savedPort= sharedPref.getString("savedPort", null);


        if(savedIP!=null) editIP.setText(savedIP);
        if(savedPort!=null) editPort.setText(savedPort);
    }

    public static boolean validateIPAddress(String ipAddress) {
        String[] tokens = ipAddress.split("\\.");
        int i;

        if (tokens.length != 4) { return false; }

        for (String str : tokens) {
            try {
                i = Integer.parseInt(str);
            }catch (NumberFormatException | NullPointerException nfe) {
                return false;
            }
            if ((i < 0) || (i > 255)) { return false; }
        }

        return true;
    }

    public static boolean validatePort(String port){
        int i;

        try {
            i = Integer.parseInt(port);
        }catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }

        if(i<0 || i>10000) return false;

        return true;

    }

    public void save(View view){


            sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

            editor = sharedPref.edit();

            if(validateIPAddress(editIP.getText().toString())) {
                editor.putString("savedIP", editIP.getText().toString());
                Toast.makeText(this, "IP Enregistré: " + editIP.getText(), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "IP non conforme", Toast.LENGTH_SHORT).show();

            }

            if(validatePort(editPort.getText().toString())) {
                editor.putString("savedPort", editPort.getText().toString());
                Toast.makeText(this, "Port Enregistré: " + editPort.getText(), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Port non conforme", Toast.LENGTH_SHORT).show();
            }
            editor.commit();


    }
}
