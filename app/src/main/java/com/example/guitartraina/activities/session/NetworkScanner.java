package com.example.guitartraina.activities.session;

import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetworkScanner {

    private static final String TAG = "NetworkScanner";
    private final int port; // Port to scan

    public NetworkScanner(int port) {
        this.port = port;
    }

    public interface ScanListener {
        void onDeviceFound(InetAddress deviceAddress);

        void onScanComplete();
    }

    public void scanNetwork(ScanListener listener) {
        new NetworkScannerTask(listener).start();
    }

    private class NetworkScannerTask extends Thread {

        private final ScanListener listener;
        private final List<InetAddress> deviceAddresses;

        public NetworkScannerTask(ScanListener listener) {
            this.listener = listener;
            deviceAddresses = new ArrayList<>();
        }

        @Override
        public void run() {
            super.run();
            String ip = "" + getLocalIpAddress();
            String pattern = "\\d+$";
            ip = ip.replaceAll(pattern, "");
            for (int i = 1; i < 255; i++) {
                String host = ip + i;
                try {
                    InetAddress inetAddress = InetAddress.getByName(host);
                    if (inetAddress.isReachable(100)) {
                        // Check if the specified port is open on the device
                        if (isPortOpen(inetAddress, port)) {
                            deviceAddresses.add(inetAddress);
                            if (listener != null) {
                                listener.onDeviceFound(inetAddress);
                            }
                        }
                    }
                } catch (UnknownHostException e) {
                    Log.e(TAG, "UnknownHostException: " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "IOException: " + e.getMessage());
                }
            }
            if (listener != null) {
                listener.onScanComplete();
            }
        }

        // Helper method to check if a specific port is open on a given InetAddress
        private boolean isPortOpen(InetAddress inetAddress, int port) {
            try {
                SocketAddress socketAddress = new InetSocketAddress(inetAddress, port);
                Socket socket = new Socket();
                socket.connect(socketAddress, 1000);
                socket.close();
                return true;
            } catch (IOException e) {
                return false;
            }
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
}
