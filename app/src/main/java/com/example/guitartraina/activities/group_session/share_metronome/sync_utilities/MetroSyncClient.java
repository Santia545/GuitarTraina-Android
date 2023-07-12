package com.example.guitartraina.activities.group_session.share_metronome.sync_utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.group_session.share_metronome.SharedMetronomeActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MetroSyncClient implements Runnable {

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private final AtomicBoolean running;

    private final SharedMetronomeActivity metronomeActivity;

    private final InetAddress addr;
    private SharedPreferences archivo;

    public MetroSyncClient(SharedMetronomeActivity playerActivity, InetAddress addr) {
        this.addr = addr;
        getEncryptedSharedPreferences(playerActivity);
        running = new AtomicBoolean(true);
        this.metronomeActivity = playerActivity;
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
            dos.writeUTF(archivo.getString("email", metronomeActivity.getString(R.string.guest)));
        } catch (IOException e) {
            Log.e("SYNC_CLIENT", e.toString());
        }

        while (running.get()) {
            try {
                int command = dis.readInt();

                if (command == MetroSyncCommand.PLAY.ordinal()) {
                    metronomeActivity.runOnUiThread(() -> {
                        metronomeActivity.getMetronome().run();
                        metronomeActivity.binding.metronomePlayBtn.setText(R.string.pause);
                    });
                } else if (command == MetroSyncCommand.PAUSE.ordinal()) {
                    metronomeActivity.runOnUiThread(() -> {
                        metronomeActivity.getMetronome().pause();
                        metronomeActivity.binding.metronomePlayBtn.setText(R.string.play);
                    });
                } else if (command == MetroSyncCommand.ACCENT_ON.ordinal()) {
                    metronomeActivity.runOnUiThread(() -> {
                        metronomeActivity.getMetronome().setNoteAccent(0);
                    });
                } else if (command == MetroSyncCommand.ACCENT_OFF.ordinal()) {
                    metronomeActivity.runOnUiThread(() -> {
                        metronomeActivity.getMetronome().setNoteAccent(-1);
                    });
                } else if (command == MetroSyncCommand.TEMPO.ordinal()) {
                    int noteNumber = dis.readInt();
                    int noteType = dis.readInt();
                    metronomeActivity.runOnUiThread(() -> {
                        metronomeActivity.binding.metronomeView.setNotesNumber(noteNumber);
                        metronomeActivity.getMetronome().setNotesNumber(noteNumber);
                        metronomeActivity.getMetronome().setNoteType(noteType);
                    });
                } else if (command == MetroSyncCommand.BPM.ordinal()) {
                    int bpm = dis.readInt();
                    metronomeActivity.runOnUiThread(() -> metronomeActivity.getMetronome().setBpm(bpm));
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