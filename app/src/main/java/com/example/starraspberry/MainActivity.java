package com.example.starraspberry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private String request;

    private static final String SERVER_IP = "85.170.143.208"; //server IP address
    private static final int SERVER_PORT = 4444;

    private TextView textSlot1;
    private TextView textSlot2;
    private TextView textSlot3;

    private String request1;
    private String request2;
    private String request3;

    private static final int CODE_RESULT_1 = 1;
    private static final int CODE_RESULT_2 = 2;
    private static final int CODE_RESULT_3 = 3;


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

        if(savedSlot1!=null) textSlot1.setText(makeNiceString(savedSlot1));
        if(savedSlot2!=null) textSlot2.setText(makeNiceString(savedSlot2));
        if(savedSlot3!=null) textSlot3.setText(makeNiceString(savedSlot3));




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

    public String makeNiceString(String request){
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





    public void sendData1(View view){

        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(request1);

        SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("savedSlot1", request1);
        editor.commit();

    }

    public void sendData2(View view){

        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(request2);

        SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("savedSlot2", request2);
        editor.commit();

    }

    public void sendData3(View view){

        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute(request3);

        SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("savedSlot3", request3);
        editor.commit();

    }


    public void sup1(View view){
        textSlot1.setText("Votre première requête ici");
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute("sup/0");

    }

    public void sup2(View view){
        textSlot2.setText("Votre deuxième requête ici");
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute("sup/1");

    }

    public void sup3(View view){
        textSlot3.setText("Votre troisième requête ici");
        BackgroundTask backgroundTask = new BackgroundTask();
        backgroundTask.execute("sup/2");

    }


    class BackgroundTask extends AsyncTask<String,Void, Void> {


        Socket socket;
        PrintWriter writer;

        @Override
        protected Void doInBackground(String... voids) {

            try{
                String message = voids[0];
                socket= new Socket(SERVER_IP,SERVER_PORT);

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
