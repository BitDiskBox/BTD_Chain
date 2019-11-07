package com.peersafe.hdtsdk.api;

public class OfferInfo {
    private String mAccount;
    private long mSequence;
    private String mTakerGets;
    private String mTakerPays;
    private String mTxId;
    private int mType;

    public long getSequence() {
        return this.mSequence;
    }

    public void setSequence(long sequence) {
        this.mSequence = sequence;
    }

    public String getTxId() {
        return this.mTxId;
    }

    public void setTxId(String txId) {
        this.mTxId = txId;
    }

    public String getTakerPays() {
        return this.mTakerPays;
    }

    public void setTakerPays(String takerPays) {
        this.mTakerPays = takerPays;
    }

    public String getAccount() {
        return this.mAccount;
    }

    public void setAccount(String account) {
        this.mAccount = account;
    }

    public String getTakerGets() {
        return this.mTakerGets;
    }

    public void setTakerGets(String takerGets) {
        this.mTakerGets = takerGets;
    }

    public int getType() {
        return this.mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public String toString() {
        return "OfferInfo{, mTxId='" + this.mTxId + '\'' + ", mType=" + this.mType + ", mSequence=" + this.mSequence + '}';
    }
}
