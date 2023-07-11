package com.example.guitartraina.activities.group_session;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.guitartraina.R;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class NsdHost {
    private final String TAG = "NsdHostTAG";

    private final String SERVICE_NAME;

    private final NsdManager manager;
    private NsdManager.RegistrationListener registrationListener;
    private SharedPreferences archivo;
    int port = 7830;

    public NsdHost(Context context) {
        getEncryptedSharedPreferences(context);
        SERVICE_NAME = archivo.getString("email", context.getString(R.string.guest));
        manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        makeRegistrationListener();
    }
    private void getEncryptedSharedPreferences(Context context) {
        String masterKeyAlias;
        archivo = null;
        try {
            masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            archivo = EncryptedSharedPreferences.create("archivo", masterKeyAlias, context, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }


    public void registerService() {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(SERVICE_NAME);
        String SERVICE_TYPE = "_http._tcp.";
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);

        manager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
    }

    public void unRegisterService() {
        if(manager != null) {
            manager.unregisterService(registrationListener);
        }
    }

    public void makeRegistrationListener() {
        registrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(TAG, "Service registration failed");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(TAG, "Service unregistration failed");
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service registered successfully");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service unregistered successfully");
            }
        };
    }


}

