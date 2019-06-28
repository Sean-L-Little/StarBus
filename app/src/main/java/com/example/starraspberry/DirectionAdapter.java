package com.example.starraspberry;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class DirectionAdapter extends RecyclerView.Adapter<DirectionAdapter.ViewHolder> {

    private final List<String> dData;
    private final LayoutInflater dInflater;
    private ItemClickListener dClickListener;

    // Les données sont envoyés au constructeur
    DirectionAdapter(Context context, List<String> data) {
        this.dInflater = LayoutInflater.from(context);
        this.dData = data;
    }
    //Gonfle les rangées de l'xml quand on en a besoin
    // Au début nous voulions mettre des Images pour chaque ligne mais ça s'est averé pas possible
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = dInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    //Relie les données avec le TextView de chaque rangée
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String str = dData.get(position);
        holder.myTextView.setText(str);
    }

    // Nombre total de rangées
    @Override
    public int getItemCount() {
        return dData.size();
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
            if (dClickListener != null) dClickListener.onDirectionClick(getAdapterPosition());
        }
    }

    // Methode pour récuperer un élément
    String getItem(int id) {
        return dData.get(id);
    }

    // Permets les clickevent
    void setClickListener(ItemClickListener itemClickListener) {
        this.dClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onDirectionClick(int position);
    }
}