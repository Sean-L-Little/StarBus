package com.example.starraspberry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private final String request=null;

    private static String SERVER_IP = ""; //server IP address
    private static int SERVER_PORT = -1;

    private TextView textSlot1;
    private TextView textSlot2;
    private TextView textSlot3;

    private String request1;
    private String request2;
    private String request3;

    private static final int CODE_RESULT_1 = 1;
    private static final int CODE_RESULT_2 = 2;
    private static final int CODE_RESULT_3 = 3;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private static boolean succesEnvoi=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textSlot1= findViewById(R.id.text_slot1);
        textSlot2= findViewById(R.id.text_slot2);
        textSlot3= findViewById(R.id.text_slot3);

        SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

        String savedSlot1= sharedPref.getString("savedSlot1", null);
        String savedSlot2= sharedPref.getString("savedSlot2", null);
        String savedSlot3= sharedPref.getString("savedSlot3", null);

        String savedIP= sharedPref.getString("savedIP", null);
        String savedPort= sharedPref.getString("savedPort",null);


        if(savedSlot1!=null) textSlot1.setText(makeNiceString(savedSlot1));
        if(savedSlot2!=null) textSlot2.setText(makeNiceString(savedSlot2));
        if(savedSlot3!=null) textSlot3.setText(makeNiceString(savedSlot3));

        if(savedIP!=null&&SettingsActivity.validateIPAddress(savedIP)) SERVER_IP=savedIP;
        if(savedPort!=null&& SettingsActivity.validatePort(savedPort)) SERVER_PORT = Integer.parseInt(savedPort);


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        // Verifier que c'est la bonne requête
        if (requestCode == CODE_RESULT_1) {
            // Verifier que le résultat est bien formulé
            if (resultCode == RESULT_OK) {

                request1 = resultIntent.getStringExtra("CODE_RESULT_1");
                textSlot1.setText(makeNiceString(request1));


            }
        }else if (requestCode == CODE_RESULT_2) {

            if (resultCode == RESULT_OK) {

                request2 = resultIntent.getStringExtra("CODE_RESULT_2");
                textSlot2.setText(makeNiceString(request2));


            }
        }else if (requestCode == CODE_RESULT_3) {

            if (resultCode == RESULT_OK) {

                request3 = resultIntent.getStringExtra("CODE_RESULT_3");
                textSlot3.setText(makeNiceString(request3));


            }
        }
    }

    private String makeNiceString(String request){
        String[] strings = request.split("/");

        return "Ligne: "+strings[1]+"\nDirection: "+ strings[2]+"\nArret: "+strings[3];
    }

    public void modifierSlotUn(View view) {

        Intent intent = new Intent(this, LignesActivity.class);

        intent.putExtra("stringLignes",request);
        intent.putExtra("slot", 0);

        startActivityForResult(intent, CODE_RESULT_1);
    }

    public void modifierSlotDeux(View view) {

        Intent intent = new Intent(this, LignesActivity.class);

        intent.putExtra("stringLignes",request);
        intent.putExtra("slot", 1);

        startActivityForResult(intent, CODE_RESULT_2);
    }

    public void modifierSlotTrois(View view) {

        Intent intent = new Intent(this, LignesActivity.class);

        intent.putExtra("stringLignes",request);
        intent.putExtra("slot", 2);

        startActivityForResult(intent, CODE_RESULT_3);
    }


    public void parametres(View view) {

        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);
    }





    public void sendData1(View view){
        succesEnvoi=(SettingsActivity.validatePort(Integer.toString(SERVER_PORT))&&SettingsActivity.validateIPAddress(SERVER_IP));

        if(request1!=null&&succesEnvoi) {
            BackgroundTask backgroundTask = new BackgroundTask();
            backgroundTask.execute(request1);

            sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

            editor = sharedPref.edit();
            editor.putString("savedSlot1", request1);
            editor.apply();

            Toast.makeText(this, "Requête 1 envoyé avec succès", Toast.LENGTH_LONG).show();
        }else if(!succesEnvoi){
            Toast.makeText(this, "Echec envoi, Port ou IP mal configuré", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Echec envoi, Requête vide", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendData2(View view){
        succesEnvoi=(SettingsActivity.validatePort(Integer.toString(SERVER_PORT))&&SettingsActivity.validateIPAddress(SERVER_IP));

        if(request2!=null&&succesEnvoi) {
            BackgroundTask backgroundTask = new BackgroundTask();
            backgroundTask.execute(request2);

            sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

            editor = sharedPref.edit();
            editor.putString("savedSlot2", request2);
            editor.apply();

            Toast.makeText(this, "Requête 2 envoyé avec succès", Toast.LENGTH_LONG).show();
        }else if(!succesEnvoi){
            Toast.makeText(this, "Echec envoi, Port ou IP mal configuré", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Echec envoi, Requête vide", Toast.LENGTH_SHORT).show();
        }

    }

    public void sendData3(View view){
        succesEnvoi=(SettingsActivity.validatePort(Integer.toString(SERVER_PORT))&&SettingsActivity.validateIPAddress(SERVER_IP));

        if(request3!=null&&succesEnvoi) {
            BackgroundTask backgroundTask = new BackgroundTask();
            backgroundTask.execute(request3);

            sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

            editor = sharedPref.edit();
            editor.putString("savedSlot3", request3);
            editor.apply();

            Toast.makeText(this, "Requête 3 envoyé avec succès", Toast.LENGTH_LONG).show();
        }else if(!succesEnvoi){
            Toast.makeText(this, "Echec envoi, Port ou IP mal configuré", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Echec envoi, Requête vide", Toast.LENGTH_SHORT).show();
        }

    }


    public void sup1(View view){

        textSlot1.setText(R.string.text_slot1);
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute("sup/0");

        sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

        succesEnvoi=(SettingsActivity.validatePort(Integer.toString(SERVER_PORT))&&SettingsActivity.validateIPAddress(SERVER_IP));

        if(request1!=null&&succesEnvoi) {
            editor = sharedPref.edit();
            editor.remove("savedSlot1");
            editor.apply();

            Toast.makeText(this, "Requête 1 supprimé avec succès", Toast.LENGTH_LONG).show();
        }else if(!succesEnvoi){
            Toast.makeText(this, "Echec envoi, Port ou IP mal configuré", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Echec envoi, Requête vide", Toast.LENGTH_SHORT).show();
        }

        request1=null;

    }

    public void sup2(View view){
        textSlot2.setText(R.string.text_slot2);
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute("sup/1");

        sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

        succesEnvoi=(SettingsActivity.validatePort(Integer.toString(SERVER_PORT))&&SettingsActivity.validateIPAddress(SERVER_IP));

        if(request2!=null&&succesEnvoi) {
            editor = sharedPref.edit();
            editor.remove("savedSlot2");
            editor.apply();

            Toast.makeText(this, "Requête 2 supprimé avec succès", Toast.LENGTH_LONG).show();

        }else if(!succesEnvoi){
            Toast.makeText(this, "Echec envoi, Port ou IP mal configuré", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Echec envoi, Requête vide", Toast.LENGTH_SHORT).show();
        }

        request2=null;

    }

    public void sup3(View view){
        textSlot3.setText(R.string.text_slot3);
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute("sup/2");


        sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

        succesEnvoi=(SettingsActivity.validatePort(Integer.toString(SERVER_PORT))&&SettingsActivity.validateIPAddress(SERVER_IP));

        if(request3!=null&&succesEnvoi) {
            editor = sharedPref.edit();
            editor.remove("savedSlot3");
            editor.apply();

            Toast.makeText(this, "Requête 3 supprimé avec succès", Toast.LENGTH_LONG).show();
        }else if(!succesEnvoi){
            Toast.makeText(this, "Echec envoi, Port ou IP mal configuré", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Echec envoi, Requête vide", Toast.LENGTH_SHORT).show();
        }

        request3=null;
    }


    class BackgroundTask extends AsyncTask<String,Void, Void> {


        Socket socket;
        PrintWriter writer;

        @Override
        protected Void doInBackground(String... voids) {

            try{
                String message = voids[0];

                    socket = new Socket(SERVER_IP, SERVER_PORT);

                    writer = new PrintWriter(socket.getOutputStream());
                    writer.write(message);
                    writer.flush();
                    writer.close();

                }
            catch(IOException e){
                e.printStackTrace();
            }


                        return null;
        }
    }



}
