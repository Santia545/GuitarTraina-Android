package com.example.guitartraina.ui.views.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitartraina.R;
import com.example.guitartraina.services.Notification;

import java.util.List;

public class NotificationsRVAdapter extends RecyclerView.Adapter<NotificationsRVAdapter.NotifViewHolder> {

    private View.OnClickListener onDeleteTuningClickListener;

    public void setOnDeleteTuningClickListener(View.OnClickListener onClickListener) {
        this.onDeleteTuningClickListener=onClickListener;
    }

    public static class NotifViewHolder extends RecyclerView.ViewHolder{
        final CardView cardView;
        final TextView tvTitle;
        final TextView tvBody;
        NotifViewHolder(View itemView){
            super(itemView);
            cardView = itemView.findViewById(R.id.item);
            tvTitle=itemView.findViewById(R.id.tuning_title);
            tvBody =itemView.findViewById(R.id.tuning_notes);
        }
    }
    public int getItem() {
        return item;
    }

    private final List<Notification> notificationList;
    private int item;

    public NotificationsRVAdapter(List<Notification> notificationList){
        this.notificationList = notificationList;
    }
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        NotifViewHolder notifViewHolder = new NotifViewHolder(view);
        ImageButton btnDelete=notifViewHolder.itemView.findViewById(R.id.tuning_delete);
        btnDelete.setOnClickListener(view12 -> {
            if(onDeleteTuningClickListener==null){
                return;
            }
            int adapterPos = notifViewHolder.getAdapterPosition();
            if (adapterPos != RecyclerView.NO_POSITION) {
                item=adapterPos;
                onDeleteTuningClickListener.onClick(view);
            }
        });
        return notifViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.tvTitle.setText(notification.getTitle());
        holder.tvBody.setText(String.format("%s %s", notification.getBody(), notification.getDate().toString()));
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}