package com.example.guitartraina.activities.session;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guitartraina.R;
import com.example.guitartraina.ui.views.adapter.ServersRVAdapter;
import com.example.guitartraina.ui.views.adapter.TuningsRVAdapter;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SessionActivity extends AppCompatActivity {
    private Button btnCreateSession, btnJoinSession, btnShareTuning, btnShareAudio, btnShareMetronome, btnExitSession;
    private TextView txtIp;
    private Server server = null;
    private SocketListener socketListener = null;
    private AlertDialog dialog;

    private Button btnScanServers;
    private TextView scannerStatus;
    private ServersRVAdapter serversRVAdapter;
    private List<String> servers = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        serversRVAdapter = new ServersRVAdapter(servers);
        dialog = createDialogAlert();
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
        btnScanServers.setOnClickListener(view -> {
            view.setEnabled(false);
            serversRVAdapter.notifyItemRangeRemoved(0, servers.size());
            servers.removeAll(servers);
            scanServersInNetwork();
        });

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
            if (!scannerStatus.getText().equals(getString(R.string.scanning_servers))) {
                scanServersInNetwork();
            }
            dialog.show();
            //connectToSv("192.168.1.79");
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
        scannerStatus.setText(getString(R.string.scanning_servers));
        btnScanServers.setEnabled(false);
        networkScanner.scanNetwork(new NetworkScanner.ScanListener() {
            @Override
            public void onDeviceFound(InetAddress deviceAddress) {
                runOnUiThread(() -> {
                    Toast.makeText(SessionActivity.this, "Ip:" + deviceAddress.toString(), Toast.LENGTH_SHORT).show();
                    servers.add(deviceAddress.toString());
                    serversRVAdapter.notifyItemInserted(servers.size() - 1);
                });
            }

            @Override
            public void onScanComplete() {
                runOnUiThread(() -> {
                    Toast.makeText(SessionActivity.this, "Scan Finalizado", Toast.LENGTH_SHORT).show();
                    scannerStatus.setText(getString(R.string.found_servers));
                    btnScanServers.setEnabled(true);
                });
            }
        });
    }

    private AlertDialog createDialogAlert() {
        LayoutInflater factory = LayoutInflater.from(this);
        View deleteDialogView = factory.inflate(R.layout.dialog_local_servers, null);
        RecyclerView recyclerView = deleteDialogView.findViewById(R.id.recyclerViewServers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(serversRVAdapter);
        btnScanServers = deleteDialogView.findViewById(R.id.server_scan_btn);
        scannerStatus = deleteDialogView.findViewById(R.id.scanner_status);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setNegativeButton(R.string.cancel, (dialog1, which) -> dialog1.cancel());
        builder.setTitle(R.string.local_servers);
        builder.setView(deleteDialogView);
        return builder.create();
    }

    private void connectToSv(String ip) {
        txtIp.setText(R.string.connecting_to_server);
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
        if (socketListener != null) {
            if (socketListener.isAlive()) {
                socketListener.setRunning(false);
            }
        }
    }
}