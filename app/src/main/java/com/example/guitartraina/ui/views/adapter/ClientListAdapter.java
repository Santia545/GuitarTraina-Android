package com.example.guitartraina.ui.views.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitartraina.R;

import java.util.ArrayList;

public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.ClientViewHolder> {

    ArrayList<String> clientNicks;
    Activity hostActivity;

    public ClientListAdapter(Activity hostActivity) {
        clientNicks = new ArrayList<>();
        this.hostActivity = hostActivity;
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.viewholder_host, viewGroup, false);

        Log.d("CLIENT_VIEW_CREATE", "CREATE: " + i);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder hostViewHolder, int i) {
        TextView textView = hostViewHolder.textView;
        textView.setText(clientNicks.get(i));
        textView.setOnClickListener(hostViewHolder);
        Log.d("CLIENT_VIEW_CREATE", "BIND: " + i);
    }

    @Override
    public int getItemCount() {
        return clientNicks.size();
    }

    public void addClient(String clientNick) {
        clientNicks.add(clientNick);
        hostActivity.runOnUiThread(() -> notifyItemInserted(getItemCount()));
    }
    public class ClientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView;

        @Override
        public void onClick(View view) {
            Log.i("CLIENT_VIEW_CREATE", "BUTTON_PRESS");
            Toast.makeText(view.getContext(), clientNicks.get(getAdapterPosition()), Toast.LENGTH_LONG).show();
        }

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_item);
        }
    }
}
