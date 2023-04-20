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
        TextView txtIp = findViewById(R.id.txtIp);
        btnCreateSession.setOnClickListener(view -> {
            if (server != null) {
                Toast.makeText(SessionActivity.this, "Server Already Started", Toast.LENGTH_SHORT).show();
                return;
            }
            if (socketListener != null) {
                Toast.makeText(SessionActivity.this,"Can't start a new server while being on a session",Toast.LENGTH_SHORT).show();
                return;
            }
            server = new Server();
            server.start();
            txtIp.setText(String.format("%s%s", getString(R.string.server_listening_on_ip), getLocalIpAddress()));
        });

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
}