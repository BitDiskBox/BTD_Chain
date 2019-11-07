package com.peersafe.hdtsdk.api;

public class TransferInfo {
    private String mAmount;
    private String mFromAddr;
    private String mToAddr;
    private String mTxId;
    private int mType;

    public TransferInfo() {
    }

    public TransferInfo(int type, String fromAddr, String toAddr, String amount, String txId) {
        this.mType = type;
        this.mFromAddr = fromAddr;
        this.mToAddr = toAddr;
        this.mAmount = amount;
        this.mTxId = txId;
    }

    public int getType() {
        return this.mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public String getFromAddr() {
        return this.mFromAddr;
    }

    public void setFromAddr(String fromAddr) {
        this.mFromAddr = fromAddr;
    }

    public String getToAddr() {
        return this.mToAddr;
    }

    public void setToAddr(String toAddr) {
        this.mToAddr = toAddr;
    }

    public String getAmount() {
        return this.mAmount;
    }

    public void setAmount(String amount) {
        this.mAmount = amount;
    }

    public String getTxId() {
        return this.mTxId;
    }

    public void setTxId(String txId) {
        this.mTxId = txId;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("type is:" + this.mType).append(" ");
        stringBuilder.append("fromAddr is:" + this.mFromAddr).append(" ");
        stringBuilder.append("toAddr is:" + this.mToAddr).append(" ");
        stringBuilder.append("amount is:" + this.mAmount).append(" ");
        stringBuilder.append("txId is:" + this.mTxId);
        return stringBuilder.toString();
    }
}
