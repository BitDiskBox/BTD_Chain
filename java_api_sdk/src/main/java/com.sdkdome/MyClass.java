package com.sdkdome;

import com.peersafe.hdtsdk.api.AccountTransactionCallback;
import com.peersafe.hdtsdk.api.BTDManager;
import com.peersafe.hdtsdk.api.BalanceListInfo;
import com.peersafe.hdtsdk.api.BalanceListInfoCallback;
import com.peersafe.hdtsdk.api.ConnectObserver;
import com.peersafe.hdtsdk.api.GenerateResultCallback;
import com.peersafe.hdtsdk.api.HDTSdkApi;
import com.peersafe.hdtsdk.api.TransferInfo;
import com.peersafe.hdtsdk.api.WalletInfo;

public class MyClass {
    static BTDManager mBTDManager = BTDManager.getInstance();

    public static void main(String[] args){
        if(mBTDManager.isConnected()){
            generateWallet();
        }else {
            mBTDManager.attach(new ConnectObserver() {
                @Override
                public void change(boolean z) {
                    if(z){
                        generateWallet();
                    }
                }
            });
        }
    }

    private static void generateWallet(){
        mBTDManager.generateWallet(new GenerateResultCallback() {
            @Override
            public void result(int code, String message, WalletInfo walletInfo) {
                System.out.println("code: "+code+",message: "+message);
                if(walletInfo != null){
                    System.out.println("创建成功，私钥: "+walletInfo.getPrivateKey()+", 地址："+walletInfo.getWalletAddr());
                    getBalance(walletInfo.getWalletAddr());
                    transfer(walletInfo.getWalletAddr());
                }
            }
        });
    }

    /**
     * 获取余额
     */
    private static void getBalance(String address){
//        mBTDManager.getIssueCurrencyBalanceBTD();
        mBTDManager.getIssueCurrencyBalanceList(address, new BalanceListInfoCallback() {
            @Override
            public void balanceInfo(int i, String str, BalanceListInfo balanceListInfo) {
                System.out.println("查询余额："+balanceListInfo.toString());
            }
        });
    }


    /**
     * 测试转账
     */
    private static void transfer(String address){
        String privateKey = "这里输入私钥";
//        String addressKey = "zELQuxfgeXbMTqo5sWpdvgjXX9k5JJPZsL";
        mBTDManager.transferCurrency(HDTSdkApi.CoinType.BTD, privateKey, address
                , "0.001", "remark", "type", new AccountTransactionCallback() {
                    @Override
                    public void accountTransactionResult(int i, String str, TransferInfo transferInfo) {
                        System.out.println("转账："+"code: "+i+", str: "+str);
                    }
                });
    }
}
