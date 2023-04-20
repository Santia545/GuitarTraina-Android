package com.example.guitartraina.activities.session;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketListener extends Thread implements Serializable {
    IData callback;
    String ip;
    private Socket socket;
    private DataInputStream dataInputStream;
    private boolean running = true;

    public SocketListener(IData callback, String ip) {
        this.callback = callback;
        this.ip = ip;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        try {
            // Create socket and data input stream
            int SERVER_PORT = 5000;
            SocketAddress address = new InetSocketAddress(ip, SERVER_PORT);
            socket = new Socket();
            socket.connect(address,10000);
            dataInputStream = new DataInputStream(socket.getInputStream());
            while (running) {
                // Read the data byte by byte
                byte[] buffer = new byte[1024];
                int bytesRead = dataInputStream.read(buffer);
                if(bytesRead==-1){
                    break;
                }
                // Convert the buffer to a string
                String data = new String(buffer, 2, bytesRead-2);
                callback.notifyData(data);
            }

        } catch (IOException e) {
            callback.notifyError(e.getMessage());
            e.printStackTrace();
        } finally {
            // Close socket and data input stream
            try {
                if (socket != null) {
                    socket.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}