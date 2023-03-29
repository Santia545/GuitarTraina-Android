package com.example.guitartraina.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.MainActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PracticeNotificationService extends Service {
    private static final String CHANNEL_ID = "practice_notification_channel";
    private static final int NOTIFICATION_ID = 1;
    private static final long INTERVAL = 5*1000;//5 * 60 * 1000; // 5 minutes in milliseconds

    private final Handler handler= new Handler();
    private Runnable runnable;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(2, createNotification());
        createNotificationChannel();
        runnable = ()->{
            Date date = new Date();
            Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
            calendar.setTime(date);   // assigns calendar to given date
            int currentHour=calendar.get(Calendar.HOUR_OF_DAY);
            if(currentHour>11&&currentHour<22) { // gets hour in 24h format)
                int reminderTime=getPracticeReminderTime();
                int secondsPracticed = 0;
                if (secondsPracticed < reminderTime) {
                    sendNotification();
                }
            }
            handler.postDelayed(runnable, INTERVAL);
        };
        handler.postDelayed(runnable, INTERVAL);
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private int getPracticeReminderTime() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String practiceTime = sharedPreferences.getString("practice_notifications_time", null);
        return stringToSeconds(practiceTime);
    }

    private int stringToSeconds(String practiceTime) {
        int seconds = 0;
        String[] date = practiceTime.split(":");
        int[] intDate = stringArrayToIntArray(date);
        seconds += intDate[2];
        seconds += intDate[1] * 60;
        seconds += intDate[0] * 3600;
        return seconds;
    }
    private int[] stringArrayToIntArray(String[] splittedDate) {
        int[] numbers = new int[splittedDate.length];
        for (int i = 0; i < splittedDate.length; i++) {
            numbers[i] = Integer.parseInt(splittedDate[i]);
        }
        return numbers;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(getString(R.string.practice_notification_service_title))
                .setContentText(getString(R.string.practice_notification_service_description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Practice Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        return builder.build();
    }

    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle(getString(R.string.practice_reminder))
                .setContentText(getString(R.string.notification_practice_reminder_desc))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}