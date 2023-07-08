package com.example.guitartraina.activities.group_session.sync_utilities;

import static java.lang.System.currentTimeMillis;

import android.util.Log;

import com.example.guitartraina.activities.group_session.PlayerActivity;

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

public class SyncServer {
    class SyncSocket {

        Socket socket;

        DataOutputStream dos;
        DataInputStream dis;
        Lock lock;

        long echoDelay;
        long totalDelay;

        public SyncSocket(Socket s) {
            socket = s;
            lock = new ReentrantLock();
            try {
                dos = new DataOutputStream(socket.getOutputStream());
                dis = new DataInputStream(socket.getInputStream());


                calcDelay();
                calcSeekDelay();
            } catch (IOException e) {
                Log.e("SYNC_SOCK", e.toString());
                close();
            }
        }

        public void calcDelay() {
            try {
                dos.writeInt(SyncCommand.ECHO_RTT.ordinal());
                dos.writeLong(currentTimeMillis());
                if (dis.readInt() == SyncCommand.ECHO_RTT.ordinal()) {
                    echoDelay = (currentTimeMillis() - dis.readLong()) / 2;
                } else {
                    Log.e("SYNC_SOCK", "protocol_not_followed");
                    dis.readLong();
                }
            } catch (IOException e) {
                Log.e("SYNC_SERV", e.toString());
            }
        }

        public void calcSeekDelay() {
            try {
                dos.writeInt(SyncCommand.ECHO_SEEK.ordinal());
                dos.writeLong(currentTimeMillis());
                if (dis.readInt() == SyncCommand.ECHO_SEEK.ordinal()) {
                    totalDelay = (currentTimeMillis() - dis.readLong()) - echoDelay;
                } else {
                    Log.e("SYNC_SOCK", "protocol_not_followed");
                    dis.readLong();
                }
            } catch (IOException e) {
                Log.e("SYNC_SERV", e.toString());
            }
        }

        public void play(long l) {
            if (socket != null && dos != null) {
                lock.lock();
                try {
                    dos.writeInt(SyncCommand.PLAY.ordinal());
                    dos.writeLong(l + totalDelay);
                } catch (IOException e) {
                    Log.e("SYNC_SOCK", e.toString());
                    socket = null;
                }
                lock.unlock();
            } else {
                close();
            }
        }

        public void pause(long l) {
            if (socket != null && dos != null) {
                lock.lock();
                try {
                    dos.writeInt(SyncCommand.PAUSE.ordinal());
                    dos.writeLong(l + totalDelay);
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

    private final PlayerActivity playerActivity;
    private ServerSocket serv;

    private final AtomicBoolean running;

    private final Executor exec;

    private boolean playState;

    public SyncServer(PlayerActivity activity) {
        playerActivity = activity;

        try {
            serv = new ServerSocket(1603);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        connectedSockets = new ArrayList<>();

        playState = true;
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

    public void togglePlayState() {
        playState = !playState;
        sync();
    }

    public void sync() {
        long pos = playerActivity.getPlaybackPosition();
        if (playState) {
            exec.execute(() -> {
                for (SyncSocket s : connectedSockets) {
                    s.play(pos);
                }
            });
        } else {
            exec.execute(() -> {
                for (SyncSocket s : connectedSockets) {
                    s.pause(pos);
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