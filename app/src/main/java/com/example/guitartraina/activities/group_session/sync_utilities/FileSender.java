package com.example.guitartraina.activities.group_session.sync_utilities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.example.guitartraina.activities.group_session.PreSessionActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileSender implements Runnable {

    static class FileSenderHandler implements Runnable {
        Socket sock;
        DataInputStream dis;
        DataOutputStream dos;
        String filename;

        PreSessionActivity downloadActivity;
        final int flen = 2048;

        FileSenderHandler(Socket s, String filen, PreSessionActivity downloadActivity) {
            sock = s;
            this.downloadActivity = downloadActivity;
            try {
                dis = new DataInputStream(sock.getInputStream());
                dos = new DataOutputStream(sock.getOutputStream());
                filename = filen;
            } catch (IOException e) {
                Log.e("FILE_SENDER_HANDLER", e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }

        @Override
        public void run() {
            InputStream inputStream = null;
            try {
                Uri uri = Uri.parse(filename);
                String formatedFName = checkFileExtension();
                dos.writeUTF(formatedFName);
                inputStream = downloadActivity.getContentResolver().openInputStream(uri);
                long fileLength =  getFileLength(uri);
                dos.writeLong(fileLength);
                dos.flush();

                int read;
                byte[] arr = new byte[flen];
                int i = 0;
                while ((read = inputStream.read(arr, 0, flen)) != -1) {
                    i++;
                    Log.d("FILE_SENDER_PROG", Integer.toString(i));
                    dos.write(arr, 0, read);
                    dos.flush();
                    Thread.sleep(0, 1);
                }
                String terminationSignal = "FILE_TRANSFER_COMPLETE";
                dos.writeUTF(terminationSignal);
                dos.flush();
                String clientNick = dis.readUTF();
                clientFinishedDownload(clientNick);
            } catch (Exception e) {
                Log.e("FILE_SENDER_HANDLER", e.toString());
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    Log.e("FILE_SENDER_HANDLER", e.toString());
                }

            }
        }

        private void clientFinishedDownload(String clientNick) {
            downloadActivity.adapter.addClient(clientNick);
            downloadActivity.sendPlayerSignal(dos, dis, sock);
        }

        private String checkFileExtension() {
            String extension = getExtension(filename);
            if(!Objects.equals(extension, "mp3")){
                return filename.concat(".mp3");
            }else{
                return filename;
            }
        }

        private String getExtension(String str){
            int begin = str.lastIndexOf(".");
            if(begin == -1)
                return null;
            return str.substring(begin + 1);
        }

        private long getFileLength(Uri uri) {
            if (Objects.equals(uri.getScheme(), ContentResolver.SCHEME_FILE)) {
                String filePath = uri.getPath();
                if (filePath != null) {
                    File file = new File(filePath);
                    return file.length();
                }
            } else if (Objects.equals(uri.getScheme(), ContentResolver.SCHEME_CONTENT)) {
                try (Cursor cursor = downloadActivity.getContentResolver().query(uri, null, null, null, null)) {
                    if (cursor != null && cursor.moveToFirst()) {
                        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                        if (sizeIndex != -1) {
                            return cursor.getLong(sizeIndex);
                        }
                    }
                } catch (Exception e) {
                    Log.e("FILE_SENDER_HANDLER", e.toString());
                }
            }
            return 0;
        }
    }

    ServerSocket sock;
    ExecutorService tpe;
    Thread t;
    final String filen;

    AtomicBoolean running;

    PreSessionActivity downloadActivity;

    public FileSender(String filename, int port, PreSessionActivity downloadActivity) {
        running = new AtomicBoolean(true);
        filen = filename;
        this.downloadActivity = downloadActivity;
        tpe = Executors.newCachedThreadPool();
        try {
            sock = new ServerSocket(port);
            t = new Thread(this);
            t.start();
        } catch (IOException e) {
            Log.e("FILE_SENDER", e.toString());
        }
    }

    public synchronized void harakiri() {
        tpe.shutdownNow();
        running.set(false);
        try {
            sock.close();
        } catch (Exception e) {
            Log.e("FILE_SENDER", "HARAKIRI");
        }
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                Log.d("FILE_SENDER", "Listening");
                tpe.execute(new FileSenderHandler(sock.accept(), filen, downloadActivity));
            } catch (IOException e) {
                Log.e("FILE_SENDER", e + "1");
            }
        }

        // if not running close the socket
        try {
            sock.close();
        } catch (IOException e) {
            Log.e("FILE_SENDER", e + "2");
        }
    }
}
