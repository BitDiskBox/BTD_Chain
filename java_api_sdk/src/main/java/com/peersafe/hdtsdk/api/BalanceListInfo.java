package com.peersafe.hdtsdk.api;

public class BalanceListInfo {
    private String balanceBtd;
    private String balanceHdt;
    private boolean isFreezePeerBtd;
    private boolean isFreezePeerHdt;

    public boolean isFreezePeerBtd() {
        return this.isFreezePeerBtd;
    }

    public void setFreezePeerBtd(boolean freezePeerBtd) {
        this.isFreezePeerBtd = freezePeerBtd;
    }

    public boolean isFreezePeerHdt() {
        return this.isFreezePeerHdt;
    }

    public void setFreezePeerHdt(boolean freezePeerHdt) {
        this.isFreezePeerHdt = freezePeerHdt;
    }

    public String getBalanceBtd() {
        return this.balanceBtd;
    }

    public void setBalanceBtd(String balanceBtd2) {
        this.balanceBtd = balanceBtd2;
    }

    public String getBalanceHdt() {
        return this.balanceHdt;
    }

    public void setBalanceHdt(String balanceHdt2) {
        this.balanceHdt = balanceHdt2;
    }

    @Override
    public String toString() {
        return "BalanceListInfo{" +
                "balanceBtd='" + balanceBtd + '\'' +
                ", balanceHdt='" + balanceHdt + '\'' +
                ", isFreezePeerBtd=" + isFreezePeerBtd +
                ", isFreezePeerHdt=" + isFreezePeerHdt +
                '}';
    }
}
