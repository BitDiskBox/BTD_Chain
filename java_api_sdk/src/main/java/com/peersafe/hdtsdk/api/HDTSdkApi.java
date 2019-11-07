package com.peersafe.hdtsdk.api;

import com.peersafe.hdtsdk.inner.StringUtils;
import com.peersafe.hdtsdk.inner.ZXWalletManager;
import com.peersafe.hdtsdk.log.ZXLogger;
import java.util.List;

public class HDTSdkApi {
    public static final int CODE_ACCOUNT_HDT_FREEZED = 3;
    public static final int CODE_ACCOUNT_NOT_ACTIVATE = 1;
    public static final int CODE_ACCOUNT_NOT_TRUST = 2;
    public static final int CODE_FAIL = -1;
    public static final int CODE_SUCCESS = 0;
    private static final int INIT_CODE = 101010;
    public static final int OFFER_TYPE_BOOK = 3;
    public static final int OFFER_TYPE_CANCEL = 2;
    public static final int OFFER_TYPE_CREATE = 1;
    public static final int OFFER_TYPE_TRANSFER_IN = 4;
    public static final int RECEIVE_TYPE = 1;
    public static final int TRANS_TYPE_TRANSFER_IN = 2;
    public static final int TRANS_TYPE_TRANSFER_OUT = 3;
    public static final int TRANS_TYPE_TRUST = 1;
    /* access modifiers changed from: private */
    public int lastCode;

    public enum CoinType {
        HDT,
        BTD
    }

    public void sdkInit(String chainSqlNodeAddr, String issueAddr, ConnectDelegate connectDelegate) {
        if (StringUtils.isEmpty(chainSqlNodeAddr) || StringUtils.isEmpty(issueAddr) || connectDelegate == null) {
            ZXLogger.e(HDTSdkApi.class.getSimpleName(), "sdkInit failed,param is invalid!");
        } else {
            ZXWalletManager.getInstance().sdkInit(chainSqlNodeAddr, issueAddr, connectDelegate);
        }
    }

    public void sdkClose() {
        ZXWalletManager.getInstance().sdkClose();
    }

    public void generateWallet(final GenerateResultCallback callback) {
        if(callback == null) return;
        final WalletInfo info = ZXWalletManager.getInstance().generateWallet();
        if(info == null){
            callback.result(HDTSdkApi.CODE_FAIL,"create wallet fail",null);
            return;
        }
        ZXWalletManager.getInstance().activityWallet(info.getWalletAddr(), new GenerateResultCallback() {
            @Override
            public void result(int code, String message, WalletInfo walletInfo) {
                if(code == HDTSdkApi.CODE_SUCCESS){
                    trustIssueCurrency(info.getPrivateKey(), new CommonTransactionCallback() {
                        @Override
                        public void transactionResult(int i, String str, CommonTransInfo commonTransInfo) {
                            if(i == HDTSdkApi.CODE_SUCCESS){
                                callback.result(i,str,info);
                            }else {
                                callback.result(i,str,null);
                            }
                        }
                    });
                }else {
                    callback.result(code,message,null);
                }
            }
        });
    }

    public void subscribeAccountTransaction(String walletAddr, SubscribeResultCallback subscribeResultCallback, AccountTransactionCallback accountTransactionCallback) {
        ZXWalletManager.getInstance().subscribeAccountTransaction(walletAddr, subscribeResultCallback, accountTransactionCallback);
    }

    public void trustIssueCurrency(String privateKey, final CommonTransactionCallback commonTransactionCallback) {
        this.lastCode = INIT_CODE;
        ZXWalletManager.getInstance().trustIssueCurrency(ZXWalletManager.CURRENCY_TYPE_HDT, privateKey, new CommonTransactionCallback() {
            public void transactionResult(int i, String s, CommonTransInfo commonTransInfo) {
                if (HDTSdkApi.this.lastCode == HDTSdkApi.INIT_CODE) {
                    if (i != 0) {
                        commonTransactionCallback.transactionResult(i, s, commonTransInfo);
                    }
                } else if (HDTSdkApi.this.lastCode == 0) {
                    commonTransactionCallback.transactionResult(i, s, commonTransInfo);
                }
                HDTSdkApi.this.lastCode = i;
            }
        });
        ZXWalletManager.getInstance().trustIssueCurrency(ZXWalletManager.CURRENCY_TYPE_BTD, privateKey, new CommonTransactionCallback() {
            public void transactionResult(int i, String s, CommonTransInfo commonTransInfo) {
                if (HDTSdkApi.this.lastCode == HDTSdkApi.INIT_CODE) {
                    if (i != 0) {
                        commonTransactionCallback.transactionResult(i, s, commonTransInfo);
                    }
                } else if (HDTSdkApi.this.lastCode == 0) {
                    commonTransactionCallback.transactionResult(i, s, commonTransInfo);
                }
                HDTSdkApi.this.lastCode = i;
            }
        });
    }

    public void transferCurrency(CoinType coinType, String privateKey, String toWalletAddr, String amount, String remark, String type, AccountTransactionCallback accountTransactionCallback) {
        ZXWalletManager.getInstance().transferCurrency(getCurrencyType(coinType), privateKey, toWalletAddr, amount, remark, type, accountTransactionCallback);
    }

    public void offerCreate(CoinType coinType, String privateKey, String payAmount, String getyAmount, String remark, String type, OfferCallback offerCallback) {
        ZXWalletManager.getInstance().offerCreate(getCurrencyType(coinType), privateKey, payAmount, getyAmount, remark, type, offerCallback);
    }

    public void offerCancel(CoinType coinType, String privateKey, String remark, String type, long sequence, OfferCallback offerCallback) {
        ZXWalletManager.getInstance().offerCancel(getCurrencyType(coinType), privateKey, remark, type, sequence, offerCallback);
    }

    public void accountOffers(String walletAddr, int limit, String marker, AccountOfferCallback accountOfferCallback) {
        ZXWalletManager.getInstance().accountOffers(walletAddr, limit, marker, accountOfferCallback);
    }

    public void subscribe(CoinType coinType, List<String> walletAddrs, boolean isLisenOffer, SubscribeResultCallback subscribeResultCallback, ReceiveListCallback receiveListCallback) {
        subscribe(coinType, walletAddrs, isLisenOffer, false, subscribeResultCallback, receiveListCallback);
    }

    public void subscribe(CoinType coinType, List<String> walletAddrs, boolean isLisenOffer, boolean both, SubscribeResultCallback subscribeResultCallback, ReceiveListCallback receiveListCallback) {
        ZXWalletManager.getInstance().subscribe(getCurrencyType(coinType), walletAddrs, isLisenOffer, both, subscribeResultCallback, receiveListCallback);
    }

    public void getSysCoinBalance(String walletAddr, BalanceInfoCallback balanceInfoCallback) {
        ZXWalletManager.getInstance().getSysCoinBalance(walletAddr, balanceInfoCallback);
    }

    public void getIssueCurrencyBalance(CoinType type, String walletAddr, BalanceInfoCallback balanceInfoCallback) {
        ZXWalletManager.getInstance().getIssueCurrencyBalance(getCurrencyType(type), walletAddr, balanceInfoCallback);
    }

    public void getIssueCurrencyBalanceList(String walletAddr, BalanceListInfoCallback balanceInfoCallback) {
        ZXWalletManager.getInstance().getIssueCurrencyBalanceList(walletAddr, balanceInfoCallback);
    }

    public void getIssueCurrencyTxDetail(String walletAddr, int limit, String marker, CurrencyTxsInfoCallback currencyTxsInfoCallback) {
        ZXWalletManager.getInstance().getIssueCurrencyTxDetail(walletAddr, limit, marker, currencyTxsInfoCallback);
    }

    public void getTransferFee(TransferFeeCallback transferFeeCallback) {
        ZXWalletManager.getInstance().getTransferFee(transferFeeCallback);
    }

    public boolean isLegalAddress(String address) {
        return ZXWalletManager.getInstance().isLegalAddress(address);
    }

    public int getConnectState() {
        return ZXWalletManager.getInstance().getConnectState();
    }

    public String getCurrencyType(CoinType type) {
        return type == CoinType.HDT ? ZXWalletManager.CURRENCY_TYPE_HDT : ZXWalletManager.CURRENCY_TYPE_BTD;
    }
}
