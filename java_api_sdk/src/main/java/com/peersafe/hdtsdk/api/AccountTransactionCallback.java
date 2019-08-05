package com.peersafe.hdtsdk.api;

public interface AccountTransactionCallback {
    void accountTransactionResult(int i, String str, TransferInfo transferInfo);
}
