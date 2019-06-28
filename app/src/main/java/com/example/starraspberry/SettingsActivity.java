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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editIP= findViewById(R.id.editTextIP);
        editPort= findViewById(R.id.editTextPort);


        SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE); //On récupère les données sauvgardés

        String savedIP= sharedPref.getString("savedIP", null);
        String savedPort= sharedPref.getString("savedPort", null);


        if(savedIP!=null) editIP.setText(savedIP);          //On les associe que si elles ne sont pas null
        if(savedPort!=null) editPort.setText(savedPort);
    }

    /**Methode pour verifier si un string donnée est une adress IP valide
     *
     * @param ipAddress Adresse IP à verifier
     * @return  C'est bien un IP ?
     */
    public static boolean validateIPAddress(String ipAddress) {

        String[] tokens = ipAddress.split("\\."); //On sépare le string aved avec des points
        int i;

        if (tokens.length != 4)  return false;  //Il faut qu'on aie 4 sous strings avec 3 points

        for (String str : tokens) {
            try {
                i = Integer.parseInt(str);   //on verifie que c'est réelement un int
            }catch (NumberFormatException | NullPointerException nfe) {
                return false;
            }
            if ((i < 0) || (i > 255))  return false; //Verification qu'elle est comprise entre 0 et 255
        }

        return true;
    }

    /**Methode pour déterminer si le string est un Port
     *
     * @param port Le numéro de port en String
     * @return C'est bien un Port ?
     */
    public static boolean validatePort(String port){
        int i;

        try {
            i = Integer.parseInt(port);                                 //On vérifie que c'est bien un int
        }catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }

        return (i>=0 && i<65535);  //Verification qu'elle est comprises dans les ports possible

    }

    /**Pour enregistrer en cliquant sur "Sauvgarder"
     *
     * @param view Le view
     */
    public void save(View view){


        SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE); //On charge les paramètres

        SharedPreferences.Editor editor = sharedPref.edit();

            if(validateIPAddress(editIP.getText().toString())) {
                editor.putString("savedIP", editIP.getText().toString()); //Si l'IP est correct on enregistre
                Toast.makeText(this, "IP Enregistré: " + editIP.getText(), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "IP non conforme", Toast.LENGTH_SHORT).show(); //Sinon on affiche un court message à l'utilisateur

            }

            if(validatePort(editPort.getText().toString())) {
                editor.putString("savedPort", editPort.getText().toString());   //Si le port est correct, on enregistre
                Toast.makeText(this, "Port Enregistré: " + editPort.getText(), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Port non conforme", Toast.LENGTH_SHORT).show();   //Sinon on affiche un court message à l'utilisateur
            }
            editor.apply(); //On applique les changements


    }
}
