package com.example.guitartraina.activities.group_session.share_metronome.sync_utilities;

import android.util.Log;

import com.example.guitartraina.activities.group_session.share_metronome.SharedMetronomeActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MetroSyncServer {
    class SyncSocket {

        Socket socket;

        DataOutputStream dos;
        DataInputStream dis;
        Lock lock;

        public SyncSocket(Socket s) {
            socket = s;
            lock = new ReentrantLock();
            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                Log.e("SYNC_SOCK", e.toString());
                close();
            }
        }

        public void play() {
            if (socket != null && dos != null) {
                lock.lock();
                try {
                    dos.writeInt(MetroSyncCommand.PLAY.ordinal());
                    dos.flush();
                } catch (IOException e) {
                    Log.e("SYNC_SOCK", e.toString());
                    socket = null;
                }
                lock.unlock();
            } else {
                close();
            }
        }

        public void pause() {
            if (socket != null && dos != null) {
                lock.lock();
                try {
                    dos.writeInt(MetroSyncCommand.PAUSE.ordinal());
                    dos.flush();
                } catch (IOException e) {
                    Log.e("SYNC_SOCK", e.toString());
                    socket = null;
                }
                lock.unlock();
            } else {
                close();
            }
        }

        public boolean isNull() {
            return (socket == null);
        }

        public void close() {
            try {
                connectedSockets.remove(this);
                socket.close();
            } catch (IOException e) {
                Log.e("SYNC_SOCK", e.toString());
            }
            socket = null;
        }
    }

    private final String TAG = "SYNC_SERV";

    private final ArrayList<SyncSocket> connectedSockets;

    private final SharedMetronomeActivity metronomeActivity;
    private ServerSocket serv;

    private final AtomicBoolean running;

    private final Executor exec;

    private boolean playState;

    public MetroSyncServer(SharedMetronomeActivity  activity) {
        metronomeActivity = activity;

        try {
            serv = new ServerSocket(1603);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        connectedSockets = new ArrayList<>();

        playState = false;
        exec = Executors.newSingleThreadExecutor();

        running = new AtomicBoolean(true);
        Thread listeningThread = new Thread(() -> {
            while (running.get()) {
                try {
                    Socket s = serv.accept();
                    SyncSocket syncSocket = new SyncSocket(s);
                    connectedSockets.add(syncSocket);
                } catch (IOException e) {
                    running.set(false);
                    Log.e(TAG, e.toString());
                }
            }
        });
        listeningThread.start();

    }
    public void syncPlayState(boolean playState) {
        if (playState) {
            exec.execute(() -> {
                for (SyncSocket s : connectedSockets) {
                    s.play();
                }
            });
        } else {
            exec.execute(() -> {
                for (SyncSocket s : connectedSockets) {
                    s.pause();
                }
            });
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    public void close() {
        for (SyncSocket s: connectedSockets) {
            s.close();
        }
        try {
            serv.close();
        } catch (IOException e) {
            Log.e("SYNC_SERV", e.toString());
        }
    }
}
