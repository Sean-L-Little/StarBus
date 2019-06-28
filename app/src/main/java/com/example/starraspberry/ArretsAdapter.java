package com.example.starraspberry;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ArretsAdapter extends RecyclerView.Adapter<ArretsAdapter.ViewHolder> {

    private final List<String> aData;
    private final LayoutInflater aInflater;
    private ItemClickListener aClickListener;

    // Les données sont envoyés au constructeur
    ArretsAdapter(Context context, List<String> data) {
        this.aInflater = LayoutInflater.from(context);
        this.aData = data;
    }

    //Gonfle les rangées de l'xml quand on en a besoin
    // Au début nous voulions mettre des Images pour chaque ligne mais ça s'est averé pas possible
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = aInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    //Relie les données avec le TextView de chaque rangée
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String str = aData.get(position);
        holder.myTextView.setText(str);
    }

    // Nombre total de rangées
    @Override
    public int getItemCount() {
        return aData.size();
    }

    //garde en mémoire et recycle les Views quand elles ne sont plus sur l'écran
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.elementsRecycle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (aClickListener != null) aClickListener.onArretsClick(getAdapterPosition());
        }
    }

    // Methode pour récuperer un élément
    String getItem(int id) {
        return aData.get(id);
    }

    // Permets les clickevent
    void setClickListener(ItemClickListener itemClickListener) {
        this.aClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onArretsClick(int position);
    }
}