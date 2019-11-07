package com.peersafe.hdtsdk.api;

public class CommonTransInfo {
    private String mTxId;
    private int mType;

    public CommonTransInfo() {
    }

    public CommonTransInfo(int type, String txId) {
        this.mType = type;
        this.mTxId = txId;
    }

    public int getType() {
        return this.mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public String getTxId() {
        return this.mTxId;
    }

    public void setTxId(String txId) {
        this.mTxId = txId;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("type is:" + this.mType);
        stringBuilder.append(" ");
        stringBuilder.append("txId is:" + this.mTxId);
        return stringBuilder.toString();
    }
}
