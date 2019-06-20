package com.example.starraspberry;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import org.json.*;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.*;


public class LignesActivity extends AppCompatActivity implements LignesAdapter.ItemClickListener, DirectionAdapter.ItemClickListener, ArretsAdapter.ItemClickListener {

    private final ArrayList<String> listLignes = new ArrayList<>();
    private final ArrayList<String> listDirections = new ArrayList<>();
    private final ArrayList<String> listArrets = new ArrayList<>();

    private LignesAdapter adapterLignes;
    private DirectionAdapter adapterDirections;
    private ArretsAdapter adapterArrets;

    static final int CODE_RESULT_1 = 1;
    static final int CODE_RESULT_2 = 2;
    static final int CODE_RESULT_3 = 3;

    private static int slot;

    private static final String accessKey = "31838441ceda02ce01a7a8b9de8268ecb9837fa062e68b4f65b3ae2a";
    private static final String host = "https://data.explore.star.fr";

    private static String[] listBus = new String[200];
    private static String[] listArr = new String[50];

    private static String[][] directionArray;

    private static String ligneStr;
    private static String directionStr;
    private static String arretStr;

    private static String arretArriveAller;
    private static String arretArriveRetour;

    private static String codeAller;
    private static String codeRetour;



    private String request;
    private String result="";

    private RequestQueue queue;

    private static final String path = "/api/records/1.0/search/?dataset=tco-bus-topologie-lignes-td&rows=120&sort=-idbillettique&facet=nomfamillecommerciale&exclude.nomfamillecommerciale=Complémentaire&exclude.nomfamillecommerciale=Evènementiel&exclude.nomfamillecommerciale=Spéciale&exclude.nomfamillecommerciale=Transport+scolaire";
    //   static String directionPath = "/api/records/1.0/search/?dataset=tco-bus-topologie-parcours-td&rows=100&facet=idligne&facet=nomcourtligne&facet=senscommercial&facet=type&facet=nomarretdepart&facet=nomarretarrivee&facet=estaccessiblepmr&refine.nomcourtligne=";
    private static final String directionPath = "/api/records/1.0/search/?dataset=tco-bus-topologie-parcours-td&facet=idligne&facet=nomcourtligne&facet=senscommercial&facet=type&facet=nomarretdepart&facet=nomarretarrivee&facet=estaccessiblepmr&refine.type=Principal&refine.nomcourtligne=";

    private static final String arretPath = "/api/records/1.0/search/?dataset=tco-bus-topologie-dessertes-td&rows=100&facet=nomcourtligne&facet=nomarret&facet=idparcours&facet=nomarret&refine.nomcourtligne=";


    /** Pour enlever les elements null d'un tableau
     *
     * @param a
     * @return a sans elements null
     */
    private static String[] removeNull(String[] a) {
        ArrayList<String> removedNull = new ArrayList<String>();
        for (String str : a)
            if (str != null)
                removedNull.add(str);
        return removedNull.toArray(new String[0]);
    }


    /** Pour enlever les elements null d'un tableau à 2 dimensions
     *
     * @param a
     * @return a sans elements null
     */
    public static String[][] removeNull(String[][] a){

        int newLength=0;

        for(String[] elt:a){
            if(elt[0]!=null) newLength++;
        }

        String[][]newArray = new String[newLength][a[0].length];

        int i=0,j=0,k=0;

        while(i<a.length&&j<newLength){
            if(a[i][0]!=null){
                while(k<a[0].length){
                    newArray[j][k]=a[i][k];
                    k++;
                }
                k=0; j++;
            }
            i++;
        }
        return newArray;
    }


    private String removeSpaces(String str){
        if(!str.contains(" ")){
            return str;
        }else{
            char[] strChar=str.toCharArray();

            for(int i = 0;i < strChar.length; i++){
                if(strChar[i]==' '){
                    strChar[i]= '+';
                }
            }

            return String.copyValueOf(strChar);
        }

    }

    private String constructRequest(){

        return  slot+"/"+ligneStr+"/"+directionStr+"/"+arretStr;

    }


    public String searchArretArrive(String dir){
        for(String[]a: directionArray){
            if(a[0].equals(dir)) return a[2];
        }

        return null;
    }

    /** Methode pour faire une requete pour récuperer les lignes depuis le web API de Star
     *
     */
    private void RequestLignes() {

        String url =host+path;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            request=response;
                            listBus=parserLignes(response);

                            listBus = removeNull(listBus);

                            for(int i=0;i<listBus.length;i++) {
                                listLignes.add(listBus[i]);
                            }

                            adapterLignes.notifyDataSetChanged();

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
                Map<String, String> params = new HashMap<String, String>();
                params.put("Ocp-Apim-Subscription-Key", accessKey);
                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    private void RequestDirections(String ligne) {

        String url =host+directionPath+ligne;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            request=response;
                            parserDirection(response);

                            listDirections.clear();

                            listDirections.add(arretArriveAller);

                            if(arretArriveRetour != null) listDirections.add(arretArriveRetour);


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
                Map<String, String> params = new HashMap<String, String>();
                params.put("Ocp-Apim-Subscription-Key", accessKey);
                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);


    }



    private void RequestArrets(String ligne, String code) {

        String url =host+arretPath+ligne+"&refine.idparcours="+code+"&sort=-ordre";

        //      URL url = new URL(host+searchPath+ligne[0]+"&refine.idparcours="+ligne[1]+"&sort=-ordre");


// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            request=response;
                            listArr=parserArrets(response);

                            listArr = removeNull(listArr);

                            listArrets.clear();

                            for(int i=0;i<listArr.length;i++) {
                                listArrets.add(listArr[i]);
                            }

                            adapterArrets.notifyDataSetChanged();

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
                Map<String, String> params = new HashMap<String, String>();
                params.put("Ocp-Apim-Subscription-Key", accessKey);
                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    private static String[] parserLignes(String reponseJson) {

        String[] ligneArray = new String[100];

        try{

            JSONObject jsonObject = new JSONObject(reponseJson);
            long nhits = Long.parseLong(jsonObject.getString("nhits"));

            JSONArray records = jsonObject.getJSONArray("records");

            for(int i=0;i<nhits;i++){
                JSONObject temp= records.getJSONObject(i);
                JSONObject fields= temp.getJSONObject("fields");
                ligneArray[i] = fields.getString("nomcourt");
            }


            return ligneArray;


        }
        catch(JSONException e) {e.printStackTrace();
            return null;}

    }


    private static void parserDirection(String reponseJson) {

        /*	POUR directionArray:
         *
         *  index i 	-> libellé long
         *  index i+1	-> code
         *  index i+2	-> arret arrivé
         *
         */

        try {

            JSONObject jsonObject = new JSONObject(reponseJson);
            long nhits = Long.parseLong(jsonObject.getString("nhits"));


            JSONArray records = jsonObject.getJSONArray("records");

            if (nhits != 0) {


                if (nhits == 2) {
                    JSONObject tempAller = records.getJSONObject(0);
                    JSONObject tempRetour = records.getJSONObject(1);
                    JSONObject fieldsAller = tempAller.getJSONObject("fields");
                    JSONObject fieldsRetour = tempRetour.getJSONObject("fields");

                    arretArriveAller = fieldsAller.getString("nomarretarrivee");
                    codeAller = fieldsAller.getString("code");
                    arretArriveRetour = fieldsRetour.getString("nomarretarrivee");
                    codeRetour = fieldsRetour.getString("code");

                } else if (nhits == 1) {

                    JSONObject temp = records.getJSONObject(0);
                    JSONObject fields = temp.getJSONObject("fields");

                    arretArriveAller = fields.getString("nomarretarrivee");
                    arretArriveRetour = null;

                }
            } else {
                arretArriveRetour= ("Plantage pendant le parsage des Directions");
            }


        } catch (JSONException e) {
            e.printStackTrace();


        }
    }


    private static String[] parserArrets(String reponseJson) {

        String[] arretsArray = new String[100];

        try{

            JSONObject jsonObject = new JSONObject(reponseJson);
            long nhits = Long.parseLong(jsonObject.getString("nhits"));

            JSONArray records = jsonObject.getJSONArray("records");

            for(int i=0;i<nhits;i++){

                JSONObject temp= records.getJSONObject(i);
                JSONObject fields= temp.getJSONObject("fields");
                String arret = (String) fields.get("nomarret");

                if(!Arrays.asList(arretsArray).contains(arret)){
                    arretsArray[i] = (String) fields.get("nomarret");
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

        RequestLignes();

        // Notre Recycler view pour les Lignes
        RecyclerView recyclerViewLignes = findViewById(R.id.List_Lignes);
        recyclerViewLignes.setLayoutManager(new LinearLayoutManager(this));
        adapterLignes = new LignesAdapter(this, listLignes);
        adapterLignes.setClickListener(this);
        recyclerViewLignes.setAdapter(adapterLignes);


        // Notre RecyclerView pour les Directions
        RecyclerView recyclerViewDirection = findViewById(R.id.List_Directions);
        recyclerViewDirection.setLayoutManager(new LinearLayoutManager(this));
        adapterDirections = new DirectionAdapter(this, listDirections);
        adapterDirections.setClickListener(this);
        recyclerViewDirection.setAdapter(adapterDirections);

        // Notre RecyclerView pour les Arrets
        RecyclerView recyclerViewArrets = findViewById(R.id.List_Arrets);
        recyclerViewArrets.setLayoutManager(new LinearLayoutManager(this));
        adapterArrets = new ArretsAdapter(this, listArrets);
        adapterArrets.setClickListener(this);
        recyclerViewArrets.setAdapter(adapterArrets);


    }



    public void onLignesClick(View view, int position) {

        Toast.makeText(this, "Vous avez cliqué: " + adapterLignes.getItem(position), Toast.LENGTH_SHORT).show();

        ligneStr=adapterLignes.getItem(position);
        RequestDirections(ligneStr);

        listArrets.clear(); // Vider liste des Arrets
        adapterArrets.notifyDataSetChanged(); //Dire a l'adaptateur que les données ont changé

        // Notre RecyclerView pour les Directions
        RecyclerView recyclerViewDirection = findViewById(R.id.List_Directions);
        recyclerViewDirection.setLayoutManager(new LinearLayoutManager(this));
        adapterDirections = new DirectionAdapter(this, listDirections);
        adapterDirections.setClickListener(this);
        recyclerViewDirection.setAdapter(adapterDirections);


    }



    public void onDirectionClick(View view, int position) {

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

        // Notre RecyclerView pour les Arrets
        RecyclerView recyclerViewArrets = findViewById(R.id.List_Arrets);
        recyclerViewArrets.setLayoutManager(new LinearLayoutManager(this));
        adapterArrets = new ArretsAdapter(this, listArrets);
        adapterArrets.setClickListener(this);
        recyclerViewArrets.setAdapter(adapterArrets);

    }

    public void onArretsClick(View view, int position) {
        Toast.makeText(this, "Vous avez cliqué: " + adapterArrets.getItem(position), Toast.LENGTH_SHORT).show();
        arretStr=adapterArrets.getItem(position);

        result= constructRequest();

        Toast.makeText(this, "Requête: " + result, Toast.LENGTH_SHORT).show();

    }

    /**@TODO
     * Méthode pour renvoyer la requete à l'écran d'accueil pour pouvoir être transmise
     *
     * @param view
     */
    public void envoyer(View view){

        if(result.equals("")){
            Toast.makeText(this, "Requête mal formulé", Toast.LENGTH_SHORT).show();

        }else{
            Intent resultIntent = new Intent();
            if(slot==0) resultIntent.putExtra("CODE_RESULT_1", result);
            else if(slot==1) resultIntent.putExtra("CODE_RESULT_2", result);
            else if(slot==2) resultIntent.putExtra("CODE_RESULT_3", result);

            setResult(LignesActivity.RESULT_OK, resultIntent);
            finish();
        }

    }

    /** Pour rafraîchir la liste des Lignes
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
