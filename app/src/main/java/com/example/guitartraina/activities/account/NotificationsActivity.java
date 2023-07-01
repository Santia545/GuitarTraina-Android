package com.example.guitartraina.activities.account;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.example.guitartraina.databinding.ActivityNotificationsBinding;
import com.example.guitartraina.services.Notification;
import com.example.guitartraina.ui.views.adapter.NotificationsRVAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    private ActivityNotificationsBinding binding;
    private List<Notification> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupRV();
    }

    private void setupRV() {
        notifications = getNotifications();
        binding.notificationsRv.setLayoutManager(new LinearLayoutManager(this));
        binding.notificationsRv.setHasFixedSize(false);
        NotificationsRVAdapter notificationsRVAdapter = new NotificationsRVAdapter(notifications);
        notificationsRVAdapter.setOnDeleteTuningClickListener(view -> {
            int position = notificationsRVAdapter.getItem();
            notifications.remove(position);
            saveNotification(notifications);
            notificationsRVAdapter.notifyItemRemoved(position);
        });
        binding.notificationsRv.setAdapter(notificationsRVAdapter);
    }

    private List<Notification> getNotifications() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String serializedList = sharedPreferences.getString("notifications", null);
        if(serializedList != null){
            Gson gson = new Gson();
            return gson.fromJson(serializedList, new TypeToken<List<Notification>>(){}.getType());
        }
        else{
            return new ArrayList<>();
        }
    }

    private void saveNotification(List<com.example.guitartraina.services.Notification> notifications){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String serializedList = gson.toJson(notifications);
        editor.putString("notifications", serializedList);
        editor.apply();
        Log.d(TAG, "saveNotification: " + notifications.toString());
    }
}