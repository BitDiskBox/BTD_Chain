package com.peersafe.hdtsdk.api;

public class WalletInfo {
    private String mPrivateKey;
    private String mPublicKey;
    private String mWalletAddr;

    public WalletInfo() {
    }

    public WalletInfo(String privateKey, String publicKey, String walletAddr) {
        this.mPrivateKey = privateKey;
        this.mPublicKey = publicKey;
        this.mWalletAddr = walletAddr;
    }

    public String getPrivateKey() {
        return this.mPrivateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.mPrivateKey = privateKey;
    }

    public String getPublicKey() {
        return this.mPublicKey;
    }

    public void setPublicKey(String publicKey) {
        this.mPublicKey = publicKey;
    }

    public String getWalletAddr() {
        return this.mWalletAddr;
    }

    public void setWalletAddr(String walletAddr) {
        this.mWalletAddr = walletAddr;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("privateKey is:" + this.mPrivateKey).append(" ");
        stringBuilder.append("publicKey is:" + this.mPublicKey).append(" ");
        stringBuilder.append("walletAddr is:" + this.mWalletAddr);
        return stringBuilder.toString();
    }
}
