package com.example.guitartraina.ui.views.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitartraina.R;

import java.util.List;

public class ServersRVAdapter extends RecyclerView.Adapter<ServersRVAdapter.TuningViewHolder> {

    private View.OnClickListener onClickListener;

    public static class TuningViewHolder extends RecyclerView.ViewHolder{
        final CardView cardView;
        final TextView tvAddress;
        TuningViewHolder(View itemView){
            super(itemView);
            cardView = itemView.findViewById(R.id.item_server);
            tvAddress =itemView.findViewById(R.id.server_address);
        }
    }

    public List<String> getServerList() {
        return serverList;
    }

    public int getItem() {
        return item;
    }

    private final List<String> serverList;
    private int item;

    public ServersRVAdapter(List<String> serverList){
        this.serverList =serverList;
    }
    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener=onClickListener;
    }
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public TuningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tuning,parent,false);
        TuningViewHolder tuningViewHolder = new TuningViewHolder(view);
        tuningViewHolder.itemView.setOnClickListener(view1 -> {
            if(onClickListener==null){
                return;
            }
            int adapterPos = tuningViewHolder.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION) {
                item=adapterPos;
                this.onClickListener.onClick(view1);
            }
        });
        return tuningViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TuningViewHolder holder, int position) {
        String server = serverList.get(position);
        holder.tvAddress.setText(server);
    }

    @Override
    public int getItemCount() {
        return serverList.size();
    }
}
