package com.peersafe.hdtsdk.api;

/**
 * Description:    <br>
 * Author: cxh <br>
 * Date: 2019/8/5
 */
public interface GenerateResultCallback {
    void result(int code,String message, WalletInfo walletInfo);
}
