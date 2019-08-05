package com.peersafe.hdtsdk.api;

import com.peersafe.hdtsdk.inner.AccountOfferModel;

public interface AccountOfferCallback {
    void accountOfferCallback(int i, String str, AccountOfferModel accountOfferModel);
}
