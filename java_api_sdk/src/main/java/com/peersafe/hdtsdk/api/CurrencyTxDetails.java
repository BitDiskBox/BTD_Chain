package com.peersafe.hdtsdk.api;

import java.util.ArrayList;

public class CurrencyTxDetails {
    private ArrayList<CurrencyTxDetail> mCurrencyTxDetailList;
    private String mMarker;

    public String getMarker() {
        return this.mMarker;
    }

    public void setMarker(String marker) {
        this.mMarker = marker;
    }

    public ArrayList<CurrencyTxDetail> getCurrencyTxDetailList() {
        return this.mCurrencyTxDetailList;
    }

    public void setCurrencyTxDetailList(ArrayList<CurrencyTxDetail> currencyTxDetailList) {
        this.mCurrencyTxDetailList = currencyTxDetailList;
    }
}
