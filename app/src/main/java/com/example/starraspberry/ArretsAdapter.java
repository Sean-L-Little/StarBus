package com.example.starraspberry;

import android.content.Context;
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

    // data is passed into the constructor
    ArretsAdapter(Context context, List<String> data) {
        this.aInflater = LayoutInflater.from(context);
        this.aData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = aInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String str = aData.get(position);
        holder.myTextView.setText(str);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return aData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (aClickListener != null) aClickListener.onArretsClick(getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return aData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.aClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onArretsClick(int position);
    }
}