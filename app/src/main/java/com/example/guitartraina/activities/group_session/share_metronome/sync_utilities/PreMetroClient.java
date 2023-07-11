package com.example.guitartraina.activities.group_session.share_metronome.sync_utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.group_session.share_audio.PreAudioSessionActivity;
import com.example.guitartraina.activities.group_session.share_metronome.MetronomeHostActivity;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;

public class PreMetroClient implements Runnable{
    private Thread t;
    private Socket sock;
    private DataInputStream dis;
    private DataOutputStream dos;

    private InetAddress IP;
    private int port;
    private MetronomeHostActivity downloadActivity;
    private SharedPreferences archivo;
    public PreMetroClient(InetAddress IP, int port, MetronomeHostActivity downloadActivity) {
        this.downloadActivity = downloadActivity;
        getEncryptedSharedPreferences(downloadActivity);
        this.IP = IP;
        this.port = port;
        t = new Thread(this);
        t.start();
    }
    @Override
    public void run() {
        try {
            sock = new Socket(IP, port);
            dis = new DataInputStream(sock.getInputStream());
            dos = new DataOutputStream(sock.getOutputStream());

            String name = archivo.getString("email", downloadActivity.getString(R.string.guest));
            dos.writeUTF(name);
            dos.flush();
            downloadActivity.adapter.addClient(name);

            String receivedSignal = null;
            String playerSignal = "PLAYER_START";

            // Keep reading until the signal is received or the stream ends
            while (receivedSignal == null || !receivedSignal.equals(playerSignal)) {
                try {
                    receivedSignal = dis.readUTF();
                } catch (EOFException e) {
                    // The stream has ended, handle it accordingly
                    Log.d("wawawaw", "run: no puedeser");
                    downloadActivity.onFailure("Host Disconnected.");
                    break;
                } catch (IOException e) {
                    Log.e("FILE_RECV", e.toString());
                    downloadActivity.onFailure("Error occurred during signal reading.");
                    break;
                }
            }

            if (receivedSignal != null && receivedSignal.equals(playerSignal)) {
                downloadActivity.goToMetronome();
            }
        } catch (Exception e) {
            Log.e("FILE_RECV", e.toString());
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
}
