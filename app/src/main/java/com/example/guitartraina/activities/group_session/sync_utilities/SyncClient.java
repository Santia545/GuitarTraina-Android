package com.example.guitartraina.activities.group_session.sync_utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.group_session.PlayerActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncClient implements Runnable {

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private final AtomicBoolean running;

    private final PlayerActivity playerActivity;

    private final InetAddress addr;
    private SharedPreferences archivo;

    public SyncClient(PlayerActivity playerActivity, InetAddress addr) {
        this.addr = addr;
        getEncryptedSharedPreferences(playerActivity);
        running = new AtomicBoolean(true);
        this.playerActivity = playerActivity;
        Thread thr = new Thread(this);
        thr.start();
    }

    public void close() {
        try {
            socket.close();
        } catch (Exception e) {
            Log.e("SYNC_CLIENT", e.toString());
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket(addr, 1603);
        } catch (IOException e) {
            Log.e("SYNC_CLIENT", e.toString());
        }
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            // Send nick to the server
            dos.writeUTF(archivo.getString("email", playerActivity.getString(R.string.guest)));
        } catch (IOException e) {
            Log.e("SYNC_CLIENT", e.toString());
        }

        Semaphore sem = new Semaphore(1);
        while (running.get()) {
            try {
                int command = dis.readInt();
                long val = dis.readLong();

                if (command == SyncCommand.SYNC.ordinal()) {
                    playerActivity.runOnUiThread(() -> playerActivity.seekTo(val));
                } else if (command == SyncCommand.PLAY.ordinal()) {
                    playerActivity.runOnUiThread(() -> {
                        playerActivity.seekTo(val);
                        playerActivity.setPlay(true);
                    });
                } else if (command == SyncCommand.PAUSE.ordinal()) {
                    playerActivity.runOnUiThread(() -> {
                        playerActivity.seekTo(val);
                        playerActivity.setPlay(false);
                    });
                } else if (command == SyncCommand.ECHO_RTT.ordinal()) {
                    try {
                        dos.writeInt(command);
                        dos.writeLong(val);
                    } catch (IOException e) {
                        Log.e("SYNC_CLIENT", e.toString());
                    }
                } else if (command == SyncCommand.ECHO_SEEK.ordinal()) {
                    playerActivity.runOnUiThread(() -> {
                        try {
                            sem.acquire();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        playerActivity.seekTo(playerActivity.getPlaybackPosition());
                        sem.release();
                    });
                    try {
                        sem.acquire();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    dos.writeInt(command);
                    dos.writeLong(val);
                    sem.release();
                }
            } catch (IOException e) {
                running.set(false);
            }
        }
    }

    private void getEncryptedSharedPreferences(Context context) {
        String masterKeyAlias;
        archivo = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            archivo = EncryptedSharedPreferences.create("archivo", masterKeyAlias, context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }


    public boolean isRunning() {
        return running.get();
    }
}
