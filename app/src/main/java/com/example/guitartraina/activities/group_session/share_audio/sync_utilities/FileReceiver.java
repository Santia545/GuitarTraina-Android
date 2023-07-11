package com.example.guitartraina.activities.group_session.share_audio.sync_utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.guitartraina.R;
import com.example.guitartraina.activities.group_session.share_audio.PreAudioSessionActivity;

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

public class FileReceiver implements Runnable {
    Thread t;
    Socket sock;
    DataInputStream dis;
    BufferedInputStream bis;
    DataOutputStream dos;
    FileOutputStream fos;

    InetAddress IP;
    int port;
    PreAudioSessionActivity downloadActivity;
    private SharedPreferences archivo;

    public FileReceiver(InetAddress IP, int port, PreAudioSessionActivity downloadActivity) {
        getEncryptedSharedPreferences(downloadActivity);
        this.downloadActivity = downloadActivity;
        this.IP = IP;
        this.port = port;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        String fle = "xxx";
        String filepath = "";
        File file;
        long filelen = 0;
        try {
            sock = new Socket(IP, port);
            dis = new DataInputStream(sock.getInputStream());
            dos = new DataOutputStream(sock.getOutputStream());

            String filename = dis.readUTF();
            filelen = dis.readLong();
            fle = filename.substring(filename.lastIndexOf("/") + 1);
            filepath = downloadActivity.getApplicationContext().getExternalFilesDir(null) + File.separator + fle;
            file = new File(filepath);
            file.createNewFile();
            fos = new FileOutputStream(file);
            bis = new BufferedInputStream(sock.getInputStream());

        } catch (Exception e) {
            Log.e("FILE_RECV", e.toString());
        }

        Log.d("FILE_RECV", "Recieve_START");
        int read;
        int flen = 2048;
        long total_read = 0;
        byte[] arr = new byte[flen];
        String terminationSignal = "FILE_TRANSFER_COMPLETE";
        try {
            while ((read = bis.read(arr, 0, flen)) != -1) {
                total_read += read;
                fos.write(arr, 0, read);
                downloadActivity.setProgress((int) (total_read * 100 / filelen), ((total_read / 1024) / 1024), ((filelen / 1024) / 1024));
                if (new String(arr, 0, read).contains(terminationSignal)) {
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("FILE_RECV", e.toString());
        }
        try {
            fos.close();
        } catch (IOException e) {
            Log.e("FILE_RECV", e.toString());
        }

        Log.d("FILE_RECV", "Recieved " + fle);

        if (!fle.equals("xxx") && total_read >= filelen) {
            try {
                dos.writeUTF(archivo.getString("email", downloadActivity.getString(R.string.guest)));
                dos.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            downloadActivity.onFinishDownload();
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
                downloadActivity.goToPlayer(Uri.fromFile(new File(filepath)));
            }
        } else {
            downloadActivity.onFailure("Host Disconnected.");
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

