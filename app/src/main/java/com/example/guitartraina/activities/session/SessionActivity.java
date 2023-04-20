package com.example.guitartraina.activities.session;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guitartraina.R;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class SessionActivity extends AppCompatActivity {
    private Button btnCreateSession, btnJoinSession, btnShareTuning, btnShareAudio, btnShareMetronome, btnExitSession;
    Server server = null;
    SocketListener socketListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        btnCreateSession = findViewById(R.id.btnCreateSession);
        btnJoinSession = findViewById(R.id.btnJoinSession);
        btnShareTuning = findViewById(R.id.btnShareTuning);
        btnShareAudio = findViewById(R.id.btnShareAudio);
        btnShareMetronome = findViewById(R.id.btnShareMetronome);
        btnExitSession = findViewById(R.id.btnExitSession);
        btnShareTuning.setEnabled(false);
        btnShareAudio.setEnabled(false);
        btnShareMetronome.setEnabled(false);
        btnExitSession.setEnabled(false);
        TextView txtIp = findViewById(R.id.txtIp);
        btnCreateSession.setOnClickListener(view -> {
            if (server != null) {
                Toast.makeText(SessionActivity.this, "Server Already Started", Toast.LENGTH_SHORT).show();
                return;
            }
            if (socketListener != null) {
                Toast.makeText(SessionActivity.this, "Can't start a new server while being on a session", Toast.LENGTH_SHORT).show();
                return;
            }
            server = new Server();
            server.start();
            txtIp.setText(String.format("%s%s", getString(R.string.server_listening_on_ip), getLocalIpAddress()));
            toggleSessionButtons(true);
        });
        btnJoinSession.setOnClickListener(view -> {
            txtIp.setText("Connecting to server...");
            view.setEnabled(false);
            btnCreateSession.setEnabled(false);
            if (socketListener != null || server != null) {
                Toast.makeText(SessionActivity.this, "Can't connect to a new server while being on a session", Toast.LENGTH_SHORT).show();
                return;
            }
            String ip = "192.168.1.103";
            socketListener = new SocketListener(new IData() {
                @Override
                public void notifyData(Object data) {
                    if (data.toString().equals("connected")) {
                        runOnUiThread(() -> {
                            toggleSessionButtons(true);
                            txtIp.setText(String.format("Connected to: %s", ip));
                            Toast.makeText(SessionActivity.this, "Conectado", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void notifyError(String error) {
                    runOnUiThread(() -> {
                        btnCreateSession.setEnabled(true);
                        btnJoinSession.setEnabled(true);
                        Toast.makeText(SessionActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                        txtIp.setText("");
                    });
                    socketListener = null;
                }
            }, ip);
            socketListener.start();
        });
        btnExitSession.setOnClickListener(view -> {
            if (socketListener != null) {
                socketListener.setRunning(true);
                socketListener = null;
                Toast.makeText(SessionActivity.this, "Disconnected from server", Toast.LENGTH_SHORT).show();
                txtIp.setText("");
                toggleSessionButtons(false);
                return;
            }
            if (server != null) {
                server.stopServer();
                server = null;
                Toast.makeText(SessionActivity.this, "Server Closed, session ended", Toast.LENGTH_SHORT).show();
                txtIp.setText("");
                toggleSessionButtons(false);
                return;
            }
            Toast.makeText(SessionActivity.this, "Can't exit a session while not being on one", Toast.LENGTH_SHORT).show();

        });
    }

    private void toggleSessionButtons(boolean isConnected) {
        btnCreateSession.setEnabled(!isConnected);
        btnJoinSession.setEnabled(!isConnected);
        btnShareTuning.setEnabled(isConnected);
        btnShareAudio.setEnabled(isConnected);
        btnShareMetronome.setEnabled(isConnected);
        btnExitSession.setEnabled(isConnected);
    }


    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (server != null) {
            server.stopServer();
            server = null;
        }
    }
}