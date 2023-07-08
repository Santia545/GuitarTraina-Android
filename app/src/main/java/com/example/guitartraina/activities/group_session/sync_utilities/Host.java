package com.example.guitartraina.activities.group_session.sync_utilities;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;

public class Host implements Parcelable {
    public String hostName;
    public InetAddress hostAddress;
    public int hostPort;

    public Host(String hostName, InetAddress hostAddress, int hostPort) {
        this.hostAddress = hostAddress;
        this.hostName = hostName;
        this.hostPort = hostPort;
    }

    protected Host(Parcel in) {
        hostName = in.readString();
        hostPort = in.readInt();
        hostAddress = (InetAddress) in.readSerializable();
    }

    public static final Creator<Host> CREATOR = new Creator<Host>() {
        @Override
        public Host createFromParcel(Parcel in) {
            return new Host(in);
        }

        @Override
        public Host[] newArray(int size) {
            return new Host[size];
        }
    };

    public String getHostName() {
        return hostName;
    }

    public InetAddress getHostAddress() {
        return hostAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(hostName);
        parcel.writeInt(hostPort);
        parcel.writeSerializable(hostAddress);
    }
}
