package com.example.guitartraina.activities.group_session.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.group_session.ClientActivity;
import com.example.guitartraina.activities.group_session.PreSessionActivity;
import com.example.guitartraina.activities.group_session.sync_utilities.Host;

import java.net.InetAddress;
import java.util.ArrayList;

public class HostListAdapter extends RecyclerView.Adapter<HostListAdapter.HostViewHolder> {

    ArrayList<Host> hostList;

    ClientActivity clientActivity;

    public HostListAdapter(ClientActivity clientActivity) {
        this.clientActivity = clientActivity;
        hostList = new ArrayList<>();
    }

    @NonNull
    @Override
    public HostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View view = inflater.inflate(R.layout.viewholder_host, viewGroup, false);

        HostViewHolder viewHolder = new HostViewHolder(view);
        Log.d("HOST_VIEW_CREATE", "CREATE: " + i);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HostViewHolder hostViewHolder, int i) {
        TextView textView = hostViewHolder.textView;
        textView.setText(hostList.get(i).getHostName());
        textView.setOnClickListener(hostViewHolder);
        Log.d("HOST_VIEW_CREATE", "BIND: " + i);
    }

    @Override
    public int getItemCount() {
        return hostList.size();
    }

    public class HostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ConstraintLayout row;
        public TextView textView;

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            Log.i("HOST_VIEW_CREATE", "BUTTON_PRESS");
            Toast.makeText(view.getContext(), hostList.get(pos).getHostAddress().toString(), Toast.LENGTH_LONG).show();

            Intent intent = new Intent(clientActivity, PreSessionActivity.class);
            intent.putExtra("user",1);
            intent.putExtra("host", hostList.get(pos));
            clientActivity.startActivity(intent);
        }

        public HostViewHolder(@NonNull View itemView) {
            super(itemView);
            row = itemView.findViewById(R.id.host_list_row);
            textView = itemView.findViewById(R.id.tv_item);
        }
    }

    public void addHost(String hostName, InetAddress hostAddress, int hostPort) {
        hostList.add(new Host(hostName, hostAddress, hostPort));
        updateRecyclerView();
    }

    public void removeHost(String hostName) {
        int i;
        for(i = 0; i<hostList.size(); i++) {
            if(hostName.equals(hostList.get(i).getHostName())) {
                break;
            }
        }

        hostList.remove(i);
        updateRecyclerView();
    }

    public void clear() {
        hostList = new ArrayList<>();
        updateRecyclerView();
    }

    public void updateRecyclerView() {
        clientActivity.runOnUiThread(this::notifyDataSetChanged);
    }
}
