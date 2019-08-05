package com.peersafe.hdtsdk.api;

import java.util.Date;

public class CurrencyTxDetail {
    private String coinType;
    private String mAmount;
    private Date mDate;
    private String mFromAddr;
    private String mToAddr;
    private String mTransferFee;
    private String mTxId;
    private int mTxType;

    public String getCoinType() {
        return this.coinType;
    }

    public void setCoinType(String coinType2) {
        this.coinType = coinType2;
    }

    public String getTxId() {
        return this.mTxId;
    }

    public void setTxId(String txId) {
        this.mTxId = txId;
    }

    public int getTxType() {
        return this.mTxType;
    }

    public void setTxType(int txType) {
        this.mTxType = txType;
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

    public Date getDate() {
        return this.mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public String getTransferFee() {
        return this.mTransferFee;
    }

    public void setTransferFee(String transferFee) {
        this.mTransferFee = transferFee;
    }

    public String toString() {
        return new StringBuilder("txId:" + this.mTxId).append(" txType:" + this.mTxType).append(" fromAddr:" + this.mFromAddr).append(" toAddr:" + this.mToAddr).append(" amount:" + this.mAmount).append(" date:" + this.mDate).append(" transferFee:" + this.mTransferFee).toString();
    }
}
