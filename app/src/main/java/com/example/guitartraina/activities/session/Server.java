package com.example.guitartraina.activities.session;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private List<Socket> clients = new ArrayList<>();
    private ServerSocket serverSocket;
    private boolean running = false;

    public void start() {
        running = true;
        // Listen for incoming TCP connections
        try {
            serverSocket = new ServerSocket(5000);
            while (running) {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    public void stop() {
        running = false;

        // Close server socket and all client sockets
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            for (Socket client : clients) {
                if (client != null) {
                    client.close();
                }
            }
            clients.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(int data) {
        // Send data to all connected clients
        for (Socket client : clients) {
            try {
                OutputStream outputStream = client.getOutputStream();
                outputStream.write(data);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientHandler extends Thread {

        private Socket clientSocket;
        private boolean running = true;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                // Continuously read data from client socket
                while (running) {
                    // Do nothing - this example just listens for incoming connections
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Remove client socket when connection is closed
                clients.remove(clientSocket);
            }
        }
    }
}