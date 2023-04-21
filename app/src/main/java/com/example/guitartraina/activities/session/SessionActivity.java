package com.example.guitartraina.activities.session;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private TextView txtIp;
    private Server server = null;
    private SocketListener socketListener = null;
    private AlertDialog dialog;

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
        txtIp = findViewById(R.id.txtIp);
        scanServersInNetwork();
        dialog = createDialogAlert();
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
            dialog.show();
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

    private void scanServersInNetwork() {
        NetworkScanner networkScanner = new NetworkScanner(5000);
        networkScanner.scanNetwork(new NetworkScanner.ScanListener() {
            @Override
            public void onDeviceFound(InetAddress deviceAddress) {
                runOnUiThread(() -> Toast.makeText(SessionActivity.this, "Ip:" + deviceAddress.toString(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onScanComplete() {
                runOnUiThread(() -> {
                    dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);
                    Toast.makeText(SessionActivity.this, "Scan Finalizado", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private AlertDialog createDialogAlert() {
        String[] items = {"192.168.1.103", "192.168.1.104", "192.168.1.105","3","4","5"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setNegativeButton(R.string.cancel, (dialog1, which) -> dialog1.cancel());
        builder.setNeutralButton("Refresh list", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setTitle("Servidores en la red local");
        //builder.setMessage("Select an item from the list:");
        builder.setItems(items, (dialog, which) -> connectToSv(items[which]));
        return builder.create();
    }

    private void connectToSv(String ip) {
        txtIp.setText("Connecting to server...");
        btnJoinSession.setEnabled(false);
        btnCreateSession.setEnabled(false);
        if (socketListener != null || server != null) {
            Toast.makeText(SessionActivity.this, "Can't connect to a new server while being on a session", Toast.LENGTH_SHORT).show();
            return;
        }
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