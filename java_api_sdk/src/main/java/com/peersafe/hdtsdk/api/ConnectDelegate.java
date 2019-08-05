package com.peersafe.hdtsdk.api;

public interface ConnectDelegate {
    public static final int CONNECT_CONNECTING = 1;
    public static final int CONNECT_FAIL = -1;
    public static final int CONNECT_SUCCESS = 0;

    void connectState(int i);
}
