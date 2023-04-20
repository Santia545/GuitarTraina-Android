package com.example.guitartraina.activities.session;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

public class SocketListener extends Thread implements Serializable {
        IData callback;
        String ip;
        private Socket socket;
        private DataInputStream dataInputStream;
        private boolean running = true;
        public SocketListener(IData callback, String ip){
            this.callback=callback;
            this.ip=ip;
        }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
        public void run() {
            try {
                // Create socket and data input stream
                int SERVER_PORT=5000;
                socket = new Socket(ip, SERVER_PORT);
                dataInputStream = new DataInputStream(socket.getInputStream());
                while (running) {
                    String data = dataInputStream.readUTF();
                    callback.notifyData(data);
                }

            } catch (IOException e) {
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