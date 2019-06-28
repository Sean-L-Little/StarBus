package com.example.starraspberry;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.json.*;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.*;


public class LignesActivity extends AppCompatActivity implements LignesAdapter.ItemClickListener, DirectionAdapter.ItemClickListener, ArretsAdapter.ItemClickListener {

    // Les differents arrays dans lesquel on place les données dans les adapters pour la GUI -------------------------------------------
    private final ArrayList<String> listLignes = new ArrayList<>();
    private final ArrayList<String> listDirections = new ArrayList<>();
    private final ArrayList<String> listArrets = new ArrayList<>();

    // Differents Adaptateurs pour les different RecyclerView ---------------------------------------------------------------------------
    private LignesAdapter adapterLignes;        //
    private DirectionAdapter adapterDirections;
    private ArretsAdapter adapterArrets;

    //Valeur de la slot selectionner en Main Activity------------------------------------------------------------------------------------
    private static int slot;

    //Clé d'accès à la BDD de la star et URL de l'hote de l'API -------------------------------------------------------------------------
    private static final String accessKey = "31838441ceda02ce01a7a8b9de8268ecb9837fa062e68b4f65b3ae2a";
    private static final String host = "https://data.explore.star.fr";

    //Liste des Arrays résultant du parsage des requêtes Volley -------------------------------------------------------------------------
    private static String[] listBus = new String[200];
    private static String[] listArr = new String[50];

    //Strings à ajouter dans les requêtes Volley API ------------------------------------------------------------------------------------
    private static String ligneStr;
    private static String directionStr;
    private static String arretStr;

    //Nom Français des arrets Terminus d'une même ligne, du fait que toutes les lignes ne sont pas pareil dans les deux sens ------------
    private static String arretArriveAller;
    private static String arretArriveRetour;

    //Codes pour les deux directions "Principales" des lignes de bus --------------------------------------------------------------------
    private static String codeAller;
    private static String codeRetour;

    //String pour le retour de la requête -----------------------------------------------------------------------------------------------
    private String result="";

    //RequestQueue qui sers pour empiler les requêtes Volley ----------------------------------------------------------------------------
    private RequestQueue queue;


    //Differents morceaux d'URL pour les differentes requêtes ----------------------------------------------------------------------------
    private static final String path = "/api/records/1.0/search/?dataset=tco-bus-topologie-lignes-td&rows=120&sort=-idbillettique&facet=nomfamillecommerciale&exclude.nomfamillecommerciale=Complémentaire&exclude.nomfamillecommerciale=Evènementiel&exclude.nomfamillecommerciale=Spéciale&exclude.nomfamillecommerciale=Transport+scolaire";
    private static final String directionPath = "/api/records/1.0/search/?dataset=tco-bus-topologie-parcours-td&facet=idligne&facet=nomcourtligne&facet=senscommercial&facet=type&facet=nomarretdepart&facet=nomarretarrivee&facet=estaccessiblepmr&refine.type=Principal&refine.nomcourtligne=";
    private static final String arretPath = "/api/records/1.0/search/?dataset=tco-bus-topologie-dessertes-td&rows=100&facet=nomcourtligne&facet=nomarret&facet=idparcours&facet=nomarret&refine.nomcourtligne=";


    /** Pour enlever les elements null d'un tableau de Strings
     *
     * @param a un tableau de Strings
     * @return a sans elements null
     */
    private static String[] removeNull(@NonNull String[] a) {

        //On crée un nouveau ArrayList dans laquel on n'ajoute que des elements non null
        ArrayList<String> removedNull = new ArrayList<>();
        for (String str : a){
            if (str != null) removedNull.add(str);
        }
        //Ensuite on renvoi le nouvel array sous forme de String[]
        return removedNull.toArray(new String[0]);
    }

    /** Construit la requete sous forme: SLOT/LIGNE/DIRECTION/ARRET
     *  Ces requêtes sont envoyés ensuite à la Raspberry Pi qui les interprête en découpant
     *
     * @return  String Requête bien formulé pour la Raspberry Pi
     */
    private String constructRequest(){

        return  slot+"/"+ligneStr+"/"+directionStr+"/"+arretStr;

    }


    /** Methode pour faire une requete pour récuperer les lignes depuis le web API de Star grâce à la bibliothéque Volley.
     *  Ne nécéssite ni paramètres ni retour car il modifie que des elements déjà présent.
     *  Il est toujours effectué en début d'activité
     *
     */
    private void RequestLignes() {
        String url =host+path; //Construction de l'URL

        // On envoi un StringRequest puisqu'on demande un String en retour
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Lorsqu'on a notre réponse...

                        try {
                            //On parse les lignes
                            listBus=parserLignes(response);

                            assert listBus != null;
                            listBus = removeNull(listBus);  //On enlève les nulls pour ne pas faire beuger l'adapter

                            listLignes.addAll(Arrays.asList(listBus)); //On ajoute les éléments du tableau dans le RecyclerView

                            adapterLignes.notifyDataSetChanged();      //On mets à jour l'adapter

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            /**Ici on rajoute les éléments dans l'entete
             * Notamment la clé d'accès API
             *
             * @return
             */
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Ocp-Apim-Subscription-Key", accessKey);
                return params;
            }
        };

    // On ajoute la requête à la queue
        queue.add(stringRequest);
    }

    /**Methode pour faire une requete pour récuperer les directions depuis le web API de Star grâce à la bibliothéque Volley,
     * similaire a RequestLignes plus haut en forme, mais on ne nécéssite pas d'Array de String
     * Vu qu'on n'as que 2 directions
     *
     *
     * @param ligne String pour désigner la ligne sélectionné
     */
    private void RequestDirections(String ligne) {

        String url =host+directionPath+ligne;

        // On envoi un StringRequest puisqu'on demande un String en retour
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            parserDirection(response);
                            listDirections.clear();

                            listDirections.add(arretArriveAller);   //Ajout de l'aller

                            if(arretArriveRetour != null) listDirections.add(arretArriveRetour);
                            //Dans le cas où c'est un bus à sens unique (eg arretArriveRetour == null) on n'ajoute pas de retour

                            adapterDirections.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){

            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Ocp-Apim-Subscription-Key", accessKey);
                return params;
            }
        };

// On ajoute la requête à la queue
        queue.add(stringRequest);


    }


    /**Methode pour faire une requête API pour les Arrets grâce à la bibliothéque Volley
     * Similaire aux deux autres méthodes RequestLignes et RequestDirections
     *
     * @param ligne Nom court de la ligne
     * @param code Code de la direction emprunté
     */
    private void RequestArrets(String ligne, String code) {

        String url =host+arretPath+ligne+"&refine.idparcours="+code+"&sort=-ordre";


        // On envoi un StringRequest puisqu'on demande un String en retour
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            listArr=parserArrets(response);

                            assert listArr != null;
                            listArr = removeNull(listArr); //On enlève les nulls pour ne pas faire beuger l'adapter

                            listArrets.clear();

                            listArrets.addAll(Arrays.asList(listArr)); //On ajoute les éléments du tableau dans le RecyclerView

                            adapterArrets.notifyDataSetChanged(); //On mets a jour l'adapter

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            /**Ici on rajoute les éléments dans l'entete
             * Notamment la clé d'accès API
             *
             * @return
             */
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Ocp-Apim-Subscription-Key", accessKey);
                return params;
            }
        };

//On ajoute la requête à la queue
        queue.add(stringRequest);
    }

    /**Méthode de parsage JSON pour les lignes
     *
     * @param reponseJson de la requête des Lignes
     * @return Array de Strings des lignes découpé
     */
    private static String[] parserLignes(String reponseJson) {
        //Le nombre total des lignes de la star est environ 164 mais sans les transports scolaire etc on se retrouve avec un peu moins de 100
        // Avec notre méthode removeNonNull on peut enlever facilement les elements null
        String[] ligneArray = new String[100];

        try{

            JSONObject jsonObject = new JSONObject(reponseJson); //On recupère notre object JSON en le parsant du string

            long nhits = Long.parseLong(jsonObject.getString("nhits")); //nhits represent le nombre totale d'elements dans le JSON Array

            JSONArray records = jsonObject.getJSONArray("records"); //On recupère le JSONArray contenant les réponses

            for(int i=0;i<nhits;i++){
                JSONObject temp= records.getJSONObject(i);          //On itère sur chaque élément de la JSONArray et on récupère seulement le 'nomcourt' de la ligne
                JSONObject fields= temp.getJSONObject("fields");
                ligneArray[i] = fields.getString("nomcourt");
            }


            return ligneArray;


        }
        catch(JSONException e) {e.printStackTrace();
            return null;}

    }

    /**Méthode de parsage JSON pour les Directions
     *
     * @param reponseJson de la requête des Directions
     */
    private static void parserDirection(String reponseJson) {

        try {

            JSONObject jsonObject = new JSONObject(reponseJson); //On recupère notre object JSON en le parsant du string

            long nhits = Long.parseLong(jsonObject.getString("nhits")); //nhits represent le nombre totale d'elements dans le JSON Array

            JSONArray records = jsonObject.getJSONArray("records"); //On recupère le JSONArray contenant les réponses

            //Ici on s'attends à une réponse de maximum 2

            if (nhits != 0) {

                if (nhits == 2) {
                    JSONObject tempAller = records.getJSONObject(0);    //On ne prends que les trajets 'principal' ce qui veut dire uniquement deux directions
                    JSONObject tempRetour = records.getJSONObject(1);   //Inutile d'utiliser un Array pour le montrer alors juste quelques variables suffisent
                    JSONObject fieldsAller = tempAller.getJSONObject("fields");
                    JSONObject fieldsRetour = tempRetour.getJSONObject("fields");


                    arretArriveAller = fieldsAller.getString("nomarretarrivee");    //Ici on a besoin de récuperer deux informations car le code est assez
                    codeAller = fieldsAller.getString("code");                      //illisible alors on récupère le nom de l'arrêt d'arrivé
                    arretArriveRetour = fieldsRetour.getString("nomarretarrivee");
                    codeRetour = fieldsRetour.getString("code");

                } else if (nhits == 1) { //Même cas de figure mais avec une seule direction qu'on considère comme l'aller

                    JSONObject temp = records.getJSONObject(0);
                    JSONObject fields = temp.getJSONObject("fields");

                    arretArriveAller = fields.getString("nomarretarrivee");
                    arretArriveRetour = null;

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();


        }
    }

    /**Méthode pour récuperer les arrêts à partir du string réponse
     *
     * @param reponseJson Réponse de la requête API
     * @return Array des Arrets de la ligne et direction choisis
     */
    private static String[] parserArrets(String reponseJson) {

        String[] arretsArray = new String[100];

        try{

            JSONObject jsonObject = new JSONObject(reponseJson); //On recupère notre object JSON en le parsant du string

            long nhits = Long.parseLong(jsonObject.getString("nhits")); //nhits represent le nombre totale d'elements dans le JSON Array

            JSONArray records = jsonObject.getJSONArray("records"); //On recupère le JSONArray contenant les réponses

            for(int i=0;i<nhits;i++){

                JSONObject temp= records.getJSONObject(i);          //On rentre dans la liste des arrets
                JSONObject fields= temp.getJSONObject("fields");
                String arret = (String) fields.get("nomarret");     //On recupère le nom de l'arrêt

                if(!Arrays.asList(arretsArray).contains(arret)){
                    arretsArray[i] = (String) fields.get("nomarret");   //On a trouvé que quelques fois que dans l'API il y avait
                                                                        //deux fois le même alors on enlève les doublons
                }
            }


            return arretsArray;


        }
        catch(JSONException e) {e.printStackTrace();
            return null;}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lignes);

        slot = getIntent().getIntExtra("slot",0 );

        queue = Volley.newRequestQueue(this);

        RequestLignes();    //On lance direct la requête pour les lignes puisqu'on n'as pas besoin de User Input

        // Initialisation de notre Recycler view pour les Lignes-------------------------------------------------
        RecyclerView recyclerViewLignes = findViewById(R.id.List_Lignes);
        recyclerViewLignes.setLayoutManager(new LinearLayoutManager(this));
        adapterLignes = new LignesAdapter(this, listLignes);
        adapterLignes.setClickListener(this);
        recyclerViewLignes.setAdapter(adapterLignes);


        // Initialisation de notre RecyclerView pour les Directions ----------------------------------------------
        RecyclerView recyclerViewDirection = findViewById(R.id.List_Directions);
        recyclerViewDirection.setLayoutManager(new LinearLayoutManager(this));
        adapterDirections = new DirectionAdapter(this, listDirections);
        adapterDirections.setClickListener(this);
        recyclerViewDirection.setAdapter(adapterDirections);

        // Initialisation de notre RecyclerView pour les Arrets --------------------------------------------------
        RecyclerView recyclerViewArrets = findViewById(R.id.List_Arrets);
        recyclerViewArrets.setLayoutManager(new LinearLayoutManager(this));
        adapterArrets = new ArretsAdapter(this, listArrets);
        adapterArrets.setClickListener(this);
        recyclerViewArrets.setAdapter(adapterArrets);


    }

    /**Methode quand on appui sur la LigneAdapter
     *
     * @param position de l'élément sélectionné
     */
    public void onLignesClick(int position) {
        // On affiche sur quoi on a cliqué grâce à Toast
        Toast.makeText(this, "Vous avez cliqué: " + adapterLignes.getItem(position), Toast.LENGTH_SHORT).show();

        ligneStr=adapterLignes.getItem(position);
        RequestDirections(ligneStr);

        listArrets.clear(); // Vider liste des Arrets
        adapterArrets.notifyDataSetChanged(); //Dire a l'adaptateur que les données ont changé

        // On reinitialise le view des Directions
        RecyclerView recyclerViewDirection = findViewById(R.id.List_Directions);
        recyclerViewDirection.setLayoutManager(new LinearLayoutManager(this));
        adapterDirections = new DirectionAdapter(this, listDirections);
        adapterDirections.setClickListener(this);
        recyclerViewDirection.setAdapter(adapterDirections);


    }



    public void onDirectionClick(int position) {

        Toast.makeText(this, "Vous avez cliqué: " + adapterDirections.getItem(position), Toast.LENGTH_SHORT).show();

        directionStr=adapterDirections.getItem(position);
        String code;

        if(position==0){
            code=codeAller;
        }else{
            code=codeRetour;
        }

        if(code!=null) {
            RequestArrets(ligneStr, code);
        }else{
            Toast.makeText(this, "Erreur récupérant les arrêts", Toast.LENGTH_SHORT).show();
        }

        // ON reinitialise notre RecyclerView pour les arrets
        RecyclerView recyclerViewArrets = findViewById(R.id.List_Arrets);
        recyclerViewArrets.setLayoutManager(new LinearLayoutManager(this));
        adapterArrets = new ArretsAdapter(this, listArrets);
        adapterArrets.setClickListener(this);
        recyclerViewArrets.setAdapter(adapterArrets);

    }

    public void onArretsClick(int position) {
        Toast.makeText(this, "Vous avez cliqué: " + adapterArrets.getItem(position), Toast.LENGTH_SHORT).show();
        arretStr=adapterArrets.getItem(position);

        result= constructRequest();

    }

    /**
     * Méthode pour renvoyer la requete à l'écran d'accueil pour pouvoir être transmise
     *
     * @param view
     */
    public void envoyer(View view){

        if(result.equals("")){
            Toast.makeText(this, "Requête mal formulé", Toast.LENGTH_SHORT).show();

        }else{
            Intent resultIntent = new Intent();
            if(slot==0) resultIntent.putExtra("CODE_RESULT_1", result);         //En fonction du slot sur laquel on a cliqué, on envoi un code different
            else if(slot==1) resultIntent.putExtra("CODE_RESULT_2", result);
            else if(slot==2) resultIntent.putExtra("CODE_RESULT_3", result);

            setResult(LignesActivity.RESULT_OK, resultIntent);
            finish();
        }

    }

    /** Pour rafraîchir la liste des Lignes
     *  Supprime les choix déjà faites et remets la requête à zéro
     *
     * @param view
     */
    public void refresh(View view) {

        result="";
        ligneStr="";
        directionStr="";
        arretStr="";

        RecyclerView recyclerViewLignes = findViewById(R.id.List_Lignes);
        recyclerViewLignes.setLayoutManager(new LinearLayoutManager(this));
        adapterLignes = new LignesAdapter(this, listLignes);
        adapterLignes.setClickListener(this);
        recyclerViewLignes.setAdapter(adapterLignes);

        listDirections.clear(); // Vider liste des Directions
        listArrets.clear(); // Vider liste des Arrets

        adapterDirections.notifyDataSetChanged();
        adapterArrets.notifyDataSetChanged();


    }

}
