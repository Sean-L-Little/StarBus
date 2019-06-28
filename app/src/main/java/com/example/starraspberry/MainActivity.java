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

    private final String request = null;

    // Variables pour l'envoi des données --------------------------------------------------------------------
    private static String SERVER_IP = ""; //server IP address
    private static int SERVER_PORT = -1;

    //TextViews pour les differents slots --------------------------------------------------------------------
    private TextView textSlot1;
    private TextView textSlot2;
    private TextView textSlot3;

    //Strings pour les differents requetes -------------------------------------------------------------------
    private String request1;
    private String request2;
    private String request3;

    //Codes des résultats du retour de LignesActivity --------------------------------------------------------
    private static final int CODE_RESULT_1 = 1;
    private static final int CODE_RESULT_2 = 2;
    private static final int CODE_RESULT_3 = 3;

    //Preferences pour la sauvgarde des données ---------------------------------------------------------------
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    //Boolean pour vérifier si on peut envoyer une requete ----------------------------------------------------
    private static boolean succesEnvoi = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialisation des textsViews
        textSlot1 = findViewById(R.id.text_slot1);
        textSlot2 = findViewById(R.id.text_slot2);
        textSlot3 = findViewById(R.id.text_slot3);


        SharedPreferences sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE); //Récuperation des données sauvgardés

        String savedSlot1 = sharedPref.getString("savedSlot1", null);
        String savedSlot2 = sharedPref.getString("savedSlot2", null);
        String savedSlot3 = sharedPref.getString("savedSlot3", null);

        String savedIP = sharedPref.getString("savedIP", null);
        String savedPort = sharedPref.getString("savedPort", null);


        if (savedSlot1 != null) {
            textSlot1.setText(makeNiceString(savedSlot1)); //Si les données sont valides, on les places dans les TextViews
            request1=savedSlot1;
        }
        if (savedSlot2 != null){
            textSlot2.setText(makeNiceString(savedSlot2));
            request2=savedSlot2;
        }
        if (savedSlot3 != null){
            textSlot3.setText(makeNiceString(savedSlot3));
            request3=savedSlot3;
        }

        if (savedIP != null && SettingsActivity.validateIPAddress(savedIP)) SERVER_IP = savedIP;
        if (savedPort != null && SettingsActivity.validatePort(savedPort))
            SERVER_PORT = Integer.parseInt(savedPort);


    }

    /**
     * Quand on recoit les données des Lignes Activity
     *
     * @param requestCode
     * @param resultCode
     * @param resultIntent
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        // Verifier que c'est la bonne requête
        if (requestCode == CODE_RESULT_1) {
            // Verifier que le résultat est bien formulé
            if (resultCode == RESULT_OK) {

                request1 = resultIntent.getStringExtra("CODE_RESULT_1");
                textSlot1.setText(makeNiceString(request1));


            }
        } else if (requestCode == CODE_RESULT_2) {

            if (resultCode == RESULT_OK) {

                request2 = resultIntent.getStringExtra("CODE_RESULT_2");
                textSlot2.setText(makeNiceString(request2));


            }
        } else if (requestCode == CODE_RESULT_3) {

            if (resultCode == RESULT_OK) {

                request3 = resultIntent.getStringExtra("CODE_RESULT_3");
                textSlot3.setText(makeNiceString(request3));


            }
        }
    }

    /**
     * Découpe une requete pour qu'elle soit plus lisible
     *
     * @param request
     * @return Requete sous forme lisible
     */
    private String makeNiceString(String request) {
        String[] strings = request.split("/");

        return "Ligne: " + strings[1] + "\nDirection: " + strings[2] + "\nArret: " + strings[3];
    }

    /**
     * Méthode pour modifier les données dans le slot 1
     * Lance LignesActivity avec intention de renvoyer la requete
     *
     * @param view
     */
    public void modifierSlotUn(View view) {

        Intent intent = new Intent(this, LignesActivity.class);

        intent.putExtra("stringLignes", request);
        intent.putExtra("slot", 0);

        startActivityForResult(intent, CODE_RESULT_1);
    }

    /**
     * Méthode pour modifier les données dans le slot 2
     * Lance LignesActivity avec intention de renvoyer la requete
     *
     * @param view
     */
    public void modifierSlotDeux(View view) {

        Intent intent = new Intent(this, LignesActivity.class);

        intent.putExtra("stringLignes", request);
        intent.putExtra("slot", 1);

        startActivityForResult(intent, CODE_RESULT_2);
    }

    /**
     * Méthode pour modifier les données dans le slot 3
     * Lance LignesActivity avec intention de renvoyer la requete
     *
     * @param view
     */
    public void modifierSlotTrois(View view) {

        Intent intent = new Intent(this, LignesActivity.class);

        intent.putExtra("stringLignes", request);
        intent.putExtra("slot", 2);

        startActivityForResult(intent, CODE_RESULT_3);
    }

    /**
     * Lance l'activité paramètres associé au bouton paramètres
     * Qui change l'IP et le Port
     *
     * @param view
     */
    public void parametres(View view) {

        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);
    }


    /**
     * Envoi les données du slot 1 au serveur à l'IP donnée et sur le Port donnée au serveur
     *
     * @param view
     */
    public void sendData1(View view) {

        succesEnvoi = (SettingsActivity.validatePort(Integer.toString(SERVER_PORT)) && SettingsActivity.validateIPAddress(SERVER_IP));

        // On vérifie que l'IP et le port sont valide et que la requete n'est pas null

        if (request1 != null && succesEnvoi) {

            BackgroundTask backgroundTask = new BackgroundTask(); //On lance la tache à faire en fond
            backgroundTask.execute(request1);                     //Avec la requete 1 en param

            sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);  //On récupere les données enregistrés

            editor = sharedPref.edit(); //On sauvegarde les données du slot 1 comme ça quand on ferme et on rouvre ça enregistre
            editor.putString("savedSlot1", request1);
            editor.apply();

            Toast.makeText(this, "Requête 1 envoyé avec succès", Toast.LENGTH_LONG).show();
        } else if (!succesEnvoi) {
            Toast.makeText(this, "Echec envoi, Port ou IP mal configuré", Toast.LENGTH_SHORT).show(); //Messages d'erreur non fatale au cas où ça marche pas
        } else {
            Toast.makeText(this, "Echec envoi, Requête vide", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Envoi les données du slot 2 au serveur à l'IP donnée et sur le Port donnée au serveur
     *
     * @param view
     */
    public void sendData2(View view) {
        succesEnvoi = (SettingsActivity.validatePort(Integer.toString(SERVER_PORT)) && SettingsActivity.validateIPAddress(SERVER_IP));

        // On vérifie que l'IP et le port sont valide et que la requete n'est pas null

        if (request2 != null && succesEnvoi) {

            BackgroundTask backgroundTask = new BackgroundTask(); //On lance la tache à faire en fond
            backgroundTask.execute(request2);                     //Avec la requete 2 en param

            sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);  //On récupere les données enregistrés

            editor = sharedPref.edit(); //On sauvegarde les données du slot 2 comme ça quand on ferme et on rouvre ça enregistre
            editor.putString("savedSlot2", request2);
            editor.apply();

            Toast.makeText(this, "Requête 2 envoyé avec succès", Toast.LENGTH_LONG).show();
        } else if (!succesEnvoi) {
            Toast.makeText(this, "Echec envoi, Port ou IP mal configuré", Toast.LENGTH_SHORT).show(); //Messages d'erreur non fatale au cas où ça marche pas
        } else {
            Toast.makeText(this, "Echec envoi, Requête vide", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Envoi les données du slot 3 au serveur à l'IP donnée et sur le Port donnée au serveur
     *
     * @param view
     */
    public void sendData3(View view) {
        succesEnvoi = (SettingsActivity.validatePort(Integer.toString(SERVER_PORT)) && SettingsActivity.validateIPAddress(SERVER_IP));

        // On vérifie que l'IP et le port sont valide et que la requete n'est pas null

        if (request3 != null && succesEnvoi) {

            BackgroundTask backgroundTask = new BackgroundTask(); //On lance la tache à faire en fond
            backgroundTask.execute(request3);                     //Avec la requete 3 en param

            sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);  //On récupere les données enregistrés

            editor = sharedPref.edit(); //On sauvegarde les données du slot 3 comme ça quand on ferme et on rouvre ça enregistre
            editor.putString("savedSlot3", request3);
            editor.apply();

            Toast.makeText(this, "Requête 3 envoyé avec succès", Toast.LENGTH_LONG).show();
        } else if (!succesEnvoi) {
            Toast.makeText(this, "Echec envoi, Port ou IP mal configuré", Toast.LENGTH_SHORT).show(); //Messages d'erreur non fatale au cas où ça marche pas
        } else {
            Toast.makeText(this, "Echec envoi, Requête vide", Toast.LENGTH_SHORT).show();
        }
    }

    /**Supprime la requete du slot 1 et envoie un message de suppression au server
     *
     * @param view
     */
    public void sup1(View view) {

        succesEnvoi = (SettingsActivity.validatePort(Integer.toString(SERVER_PORT)) && SettingsActivity.validateIPAddress(SERVER_IP));

        if (request1 != null && succesEnvoi) {

            textSlot1.setText(R.string.text_slot1); //On réutilise la ressource de base pour le TextView
            BackgroundTask backgroundTask = new BackgroundTask();
            backgroundTask.execute("sup/0");        //Message qui dis de supprimer le slot 1 de l'affficheur

            sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

            // On vérifie que l'IP et le port sont valide et que la requete n'est pas déjà null

            editor = sharedPref.edit();
            editor.remove("savedSlot1");    //On supprime les données des données enregistrés
            editor.apply();

            request1 = null; //On remets la requete à zéro

            Toast.makeText(this, "Requête 1 supprimé avec succès", Toast.LENGTH_LONG).show();
        } else if (!succesEnvoi) {
            Toast.makeText(this, "Echec suppression, Port ou IP mal configuré", Toast.LENGTH_SHORT).show(); //Messages d'erreur non fatale au cas où ça marche pas
        } else {
            Toast.makeText(this, "Echec suppression, Requête vide", Toast.LENGTH_SHORT).show();
        }


    }
    /**Supprime la requete du slot 2 et envoie un message de suppression au server
     *
     * @param view
     */
    public void sup2(View view) {

        succesEnvoi = (SettingsActivity.validatePort(Integer.toString(SERVER_PORT)) && SettingsActivity.validateIPAddress(SERVER_IP));

        if (request2 != null && succesEnvoi) {

            textSlot2.setText(R.string.text_slot2); //On réutilise la ressource de base pour le TextView
            BackgroundTask backgroundTask = new BackgroundTask();
            backgroundTask.execute("sup/1");        //Message qui dis de supprimer le slot 2 de l'affficheur

            sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

            // On vérifie que l'IP et le port sont valide et que la requete n'est pas déjà null

            editor = sharedPref.edit();
            editor.remove("savedSlot2");    //On supprime les données des données enregistrés
            editor.apply();

            request2 = null; //On remets la requete à zéro

            Toast.makeText(this, "Requête 2 supprimé avec succès", Toast.LENGTH_LONG).show();
        } else if (!succesEnvoi) {
            Toast.makeText(this, "Echec suppression, Port ou IP mal configuré", Toast.LENGTH_SHORT).show(); //Messages d'erreur non fatale au cas où ça marche pas
        } else {
            Toast.makeText(this, "Echec suppression, Requête vide", Toast.LENGTH_SHORT).show();
        }




    }
    /**Supprime la requete du slot 2 et envoie un message de suppression au server
     *
     * @param view
     */
    public void sup3(View view) {

        succesEnvoi = (SettingsActivity.validatePort(Integer.toString(SERVER_PORT)) && SettingsActivity.validateIPAddress(SERVER_IP));

        if (request3 != null && succesEnvoi) {

            textSlot3.setText(R.string.text_slot3); //On réutilise la ressource de base pour le TextView
            BackgroundTask backgroundTask = new BackgroundTask();
            backgroundTask.execute("sup/2");        //Message qui dis de supprimer le slot 3 de l'affficheur

            sharedPref = getSharedPreferences("mySettings", MODE_PRIVATE);

            // On vérifie que l'IP et le port sont valide et que la requete n'est pas déjà null

            editor = sharedPref.edit();
            editor.remove("savedSlot3");    //On supprime les données des données enregistrés
            editor.apply();

            request3 = null; //On remets la requete à zéro

            Toast.makeText(this, "Requête 3 supprimé avec succès", Toast.LENGTH_LONG).show();
        } else if (!succesEnvoi) {
            Toast.makeText(this, "Echec suppression, Port ou IP mal configuré", Toast.LENGTH_SHORT).show(); //Messages d'erreur non fatale au cas où ça marche pas
        } else {
            Toast.makeText(this, "Echec suppression, Requête vide", Toast.LENGTH_SHORT).show();
        }



    }


    /**Class qui permets de faire d'envoyer les données du portable vers le serveur grâce au
     * AsyncTask une simple liason UDP pour envoyer dans un sens on n'as jamais de retour du serveur
     *
     */
    static class BackgroundTask extends AsyncTask<String, Void, Void> {

        Socket socket;
        PrintWriter writer;

        @Override
        protected Void doInBackground(String... voids) {

            try {
                String message = voids[0];


                socket = new Socket(SERVER_IP, SERVER_PORT); //On crée la socket

                writer = new PrintWriter(socket.getOutputStream());
                writer.write(message);              //On écris notre message et on l'envoi
                writer.flush();
                writer.close();                     //On ferme le flux

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }


}
