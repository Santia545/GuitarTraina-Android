package com.example.guitartraina.activities.group_session.share_metronome.sync_utilities;

import android.util.Log;

import com.example.guitartraina.activities.group_session.share_metronome.MetronomeHostActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class PreMetroHost implements Runnable{
    private Thread t;
    private ServerSocket sSock;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private int port;
    AtomicBoolean running;
    private MetronomeHostActivity downloadActivity;
    public PreMetroHost(int port, MetronomeHostActivity downloadActivity) {
        running = new AtomicBoolean(true);
        this.downloadActivity = downloadActivity;
        this.port = port;
        t = new Thread(this);
        t.start();
    }
    @Override
    public void run() {
        try {
            sSock = new ServerSocket(port);
            socket = sSock.accept();
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            while(running.get()){
                String client = dis.readUTF();
                downloadActivity.adapter.addClient(client);
                downloadActivity.sendStartMetronomeSignal(dos, dis,socket);
            }
        } catch (Exception e) {
            Log.e("FILE_RECV", e.toString());
        }

    }
}