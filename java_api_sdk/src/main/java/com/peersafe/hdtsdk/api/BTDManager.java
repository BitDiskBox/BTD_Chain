package com.peersafe.hdtsdk.api;

import com.peersafe.hdtsdk.api.HDTSdkApi.CoinType;

import java.util.ArrayList;
import java.util.List;

public class BTDManager implements ConnectDelegate {
    public static final String CHAIN_SQL_NODE_ADDR = "xxx";
    public static final String ISSUE_ADDR = "xxx";
    public static final String TAG = BTDManager.class.getSimpleName();
    private boolean isSdkConnected;
    private HDTSdkApi mHdtSdkApi;
    private List<ConnectObserver> mObservers;

    private static class Single {
        /* access modifiers changed from: private */
        public static final BTDManager sInstance = new BTDManager();

        private Single() {
        }
    }

    private BTDManager() {
        this.mHdtSdkApi = new HDTSdkApi();
        this.isSdkConnected = false;
        this.mObservers = new ArrayList();
        setSdkConnected();
    }

    public static BTDManager getInstance() {
        return Single.sInstance;
    }

    public void setSdkConnected() {
        if (!isConnected()) {
            this.mHdtSdkApi.sdkInit(CHAIN_SQL_NODE_ADDR, ISSUE_ADDR, this);
        }
    }

    public void generateWallet(GenerateResultCallback callback) {
        this.mHdtSdkApi.generateWallet(callback);
    }

    public void connectState(int i) {
        if (i == 0) {
            this.isSdkConnected = true;
        } else {
            this.isSdkConnected = false;
        }
        System.out.println("connet?"+isSdkConnected);
        notify(this.isSdkConnected);
    }

    public boolean isConnected() {
        if (this.mHdtSdkApi.getConnectState() == 0) {
            this.isSdkConnected = true;
        } else {
            this.isSdkConnected = false;
        }
        return this.isSdkConnected;
    }

    public void sdkClose() {
        if (this.mHdtSdkApi.getConnectState() != -1) {
            this.mHdtSdkApi.sdkClose();
        }
    }

    public void getTransferFee(TransferFeeCallback callback) {
        this.mHdtSdkApi.getTransferFee(callback);
    }

    public void getIssueCurrencyBalanceBTD(String walletAddr, BalanceInfoCallback callback) {
        this.mHdtSdkApi.getIssueCurrencyBalance(CoinType.BTD, walletAddr, callback);
    }

    public void getIssueCurrencyBalanceList(String walletAddr, BalanceListInfoCallback callback) {
        this.mHdtSdkApi.getIssueCurrencyBalanceList(walletAddr, callback);
    }

    public void transferCurrency(CoinType coinType, String privateKey, String toAddr, String amount, String remark, String type, AccountTransactionCallback callback) {
        this.mHdtSdkApi.transferCurrency(coinType, privateKey, toAddr, amount, remark, type, callback);
    }

    public void subscribe(List<String> walletAddrs, ReceiveListCallback callback) {
        this.mHdtSdkApi.subscribe(CoinType.BTD, walletAddrs, false, true, new SubscribeResultCallback() {
            public void subscribeResult(int code, String message, int result) {
            }
        }, callback);
    }

    public void attach(ConnectObserver observer) {
        this.mObservers.add(observer);
    }

    public void detach(ConnectObserver observer) {
        this.mObservers.remove(observer);
    }

    public void notify(boolean connect) {
        for (ConnectObserver observer : this.mObservers) {
            observer.change(connect);
        }
    }
}
