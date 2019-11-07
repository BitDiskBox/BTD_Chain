package com.peersafe.hdtsdk.inner;

import com.google.gson.Gson;
import com.peersafe.base.client.Account;
import com.peersafe.base.client.Client;
import com.peersafe.base.client.Client.OnConnected;
import com.peersafe.base.client.Client.OnDisconnected;
import com.peersafe.base.client.Client.OnMessage;
import com.peersafe.base.client.enums.Command;
import com.peersafe.base.client.pubsub.Publisher;
import com.peersafe.base.client.pubsub.Publisher.Callback;
import com.peersafe.base.client.requests.Request;
import com.peersafe.base.client.requests.Request.Builder;
import com.peersafe.base.client.requests.Request.Manager;
import com.peersafe.base.client.responses.Response;
import com.peersafe.base.client.transactions.ManagedTxn;
import com.peersafe.base.client.transactions.ManagedTxn.OnSubmitSuccess;
import com.peersafe.base.client.transactions.TransactionManager;
import com.peersafe.base.core.coretypes.AccountID;
import com.peersafe.base.core.coretypes.Amount;
import com.peersafe.base.core.coretypes.Currency;
import com.peersafe.base.core.coretypes.RippleDate;
import com.peersafe.base.core.coretypes.STArray;
import com.peersafe.base.core.coretypes.uint.UInt32;
import com.peersafe.base.core.fields.Field;
import com.peersafe.base.core.serialized.enums.EngineResult;
import com.peersafe.base.core.serialized.enums.TransactionType;
import com.peersafe.base.core.types.known.tx.Transaction;
import com.peersafe.base.core.types.known.tx.result.AffectedNode;
import com.peersafe.base.core.types.known.tx.result.TransactionResult;
import com.peersafe.base.core.types.known.tx.result.TransactionResult.Source;
import com.peersafe.base.core.types.known.tx.txns.OfferCancel;
import com.peersafe.base.core.types.known.tx.txns.OfferCreate;
import com.peersafe.base.core.types.known.tx.txns.Payment;
import com.peersafe.base.core.types.known.tx.txns.TrustSet;
import com.peersafe.chainsql.core.Chainsql;
import com.peersafe.chainsql.net.Connection;
import com.peersafe.chainsql.util.Util;
import com.peersafe.hdtsdk.BuildConfig;
import com.peersafe.hdtsdk.api.AccountOfferCallback;
import com.peersafe.hdtsdk.api.AccountTransactionCallback;
import com.peersafe.hdtsdk.api.BalanceInfoCallback;
import com.peersafe.hdtsdk.api.BalanceListInfo;
import com.peersafe.hdtsdk.api.BalanceListInfoCallback;
import com.peersafe.hdtsdk.api.CommonTransInfo;
import com.peersafe.hdtsdk.api.CommonTransactionCallback;
import com.peersafe.hdtsdk.api.ConnectDelegate;
import com.peersafe.hdtsdk.api.CurrencyTxDetail;
import com.peersafe.hdtsdk.api.CurrencyTxDetails;
import com.peersafe.hdtsdk.api.CurrencyTxsInfoCallback;
import com.peersafe.hdtsdk.api.GenerateResultCallback;
import com.peersafe.hdtsdk.api.HDTSdkApi;
import com.peersafe.hdtsdk.api.OfferCallback;
import com.peersafe.hdtsdk.api.OfferInfo;
import com.peersafe.hdtsdk.api.ReceiveInfo;
import com.peersafe.hdtsdk.api.ReceiveListCallback;
import com.peersafe.hdtsdk.api.SubscribeResultCallback;
import com.peersafe.hdtsdk.api.TransferFeeCallback;
import com.peersafe.hdtsdk.api.TransferInfo;
import com.peersafe.hdtsdk.api.WalletInfo;
import com.peersafe.hdtsdk.inner.AccountLineModel.LinesBean;
import com.peersafe.hdtsdk.log.ZXLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ZXWalletManager {
    private static final String ACCOUNT_ID = "account_id";
    private static final String ACCOUNT_NOT_FOUND = "actNotFound";
    public static final String CURRENCY_TYPE_BTD = "BTD";
    public static final String CURRENCY_TYPE_HDT = "HDT";
    private static final String PUBLIC_KEY = "public_key";
    private static final String SECRET = "secret";
    private static final String TAG = "HDTSDK_ZXWalletManager";
    private static volatile ZXWalletManager instance = null;
    /* access modifiers changed from: private */
    public static boolean mIsSubscribeAccount = false;
    /* access modifiers changed from: private */
    public boolean isBoth;
    /* access modifiers changed from: private */
    public boolean isReceiveListLisenOffer = false;
    /* access modifiers changed from: private */
    public AccountTransactionCallback mAccountTransactionCallback = null;
    /* access modifiers changed from: private */
    public String mChainSqlNodeAddr;
    /* access modifiers changed from: private */
    public ConnectDelegate mConnectDelegate;
    /* access modifiers changed from: private */
    public String mCurReceiveListCurrencyType;
    /* access modifiers changed from: private */
    public boolean mIsWebSocketConnecting = false;
    /* access modifiers changed from: private */
    public String mIssueAddr;
    /* access modifiers changed from: private */
    public String mLastTxId = BuildConfig.FLAVOR;
    /* access modifiers changed from: private */
    public ReceiveListCallback mReceiveListCallback = null;
    /* access modifiers changed from: private */
    public List<String> mReceiveListWalletAddrs;
    /* access modifiers changed from: private */
    public String mTransferFee = BuildConfig.FLAVOR;
    private static final String TOTAL_BALANCE = "xxx";

    private ZXWalletManager() {
    }

    public static ZXWalletManager getInstance() {
        if (instance == null) {
            synchronized (ZXWalletManager.class) {
                try {
                    if (instance == null) {
                        instance = new ZXWalletManager();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return instance;
    }

    public void sdkInit(String chainSqlNodeAddr, String issueAddr, ConnectDelegate connectDelegate) {
        if (StringUtils.isEmpty(chainSqlNodeAddr) || StringUtils.isEmpty(issueAddr) || connectDelegate == null) {
            ZXLogger.e(TAG, "!!!sdkInit fail,invalid params!");
            return;
        }
        sdkClose();
        this.mChainSqlNodeAddr = chainSqlNodeAddr;
        this.mIssueAddr = issueAddr;
        this.mConnectDelegate = connectDelegate;
        this.mConnectDelegate.connectState(1);
        this.mIsWebSocketConnecting = true;
        new Thread(new Runnable() {
            public void run() {
                ZXLogger.d(ZXWalletManager.TAG, "sdkInit,begin connect websocket!");
                Chainsql.c.connect(ZXWalletManager.this.mChainSqlNodeAddr);
                ZXWalletManager.this.setupChainsqlProcess();
            }
        }).start();
    }

    public void sdkClose() {
        if (mIsSubscribeAccount && this.mReceiveListWalletAddrs != null && this.mReceiveListWalletAddrs.size() > 0) {
            getInstance().unSubscribeAccount(this.mReceiveListWalletAddrs);
        }
        mIsSubscribeAccount = false;
        this.mReceiveListWalletAddrs = null;
        this.mCurReceiveListCurrencyType = BuildConfig.FLAVOR;
        this.isBoth = false;
        this.mChainSqlNodeAddr = BuildConfig.FLAVOR;
        this.mIssueAddr = BuildConfig.FLAVOR;
        this.mLastTxId = BuildConfig.FLAVOR;
        this.mConnectDelegate = null;
        this.mAccountTransactionCallback = null;
        this.mReceiveListCallback = null;
        this.mIsWebSocketConnecting = false;
        this.isReceiveListLisenOffer = false;
        if (isConnect()) {
            ZXLogger.d(TAG, "sdkClose,disconnect websocket!");
            Chainsql.c.getConnection().disconnect();
        }
    }

    public WalletInfo generateWallet() {
        try {
            JSONObject jsonObject = Chainsql.c.generateAddress();
            String secret = jsonObject.getString(SECRET);
            String accountID = jsonObject.getString(ACCOUNT_ID);
            String publicKey = jsonObject.getString(PUBLIC_KEY);
            if (!StringUtils.isEmpty(secret)) {
                return new WalletInfo(secret, publicKey, accountID);
            }
            ZXLogger.w(TAG, "generateWallet failed,secret is empty!");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            ZXLogger.w(TAG, "generateWallet failed,exception:" + e.toString());
            return null;
        }
    }

    boolean hasActivityWalletBack = false;
    public void activityWallet(String accountID, final GenerateResultCallback callback){
        try {
            Account account = Chainsql.c.getConnection().getClient().accountFromSeed(TOTAL_BALANCE);
            TransactionManager tm = account.transactionManager();
            Payment payment = new Payment();
            BigDecimal value = new BigDecimal(7);
            Amount amountToSend = new Amount(value, Currency.fromString("BTD"),
                    AccountID.fromAddress(mIssueAddr),true,true);
            hasActivityWalletBack = false;
            payment.as(AccountID.Account, account.id());
            payment.as(AccountID.Destination, accountID);
            payment.as(Amount.Amount, amountToSend);
            tm.queue(tm.manage(payment)
                    .onSubmitSuccess(new ManagedTxn.OnSubmitSuccess() {
                        @Override
                        public void called(Response response) {
                            ZXLogger.i(TAG, "called:transferCurrency,submit success!");
                        }
                    })
                    .onError(new Publisher.Callback<Response>() {
                        @Override
                        public void called(Response response) {
                            ZXLogger.e(TAG, "error:transferCurrency");
                            if(hasActivityWalletBack) return;
                            callback.result(HDTSdkApi.CODE_FAIL,"onError:transfer system coin fail",null);
                            hasActivityWalletBack = true;
                        }
                    })
                    .onValidated(new Publisher.Callback<ManagedTxn>() {
                        @Override
                        public void called(ManagedTxn managedTxn) {
                            if(hasActivityWalletBack) return;
                            TransactionResult result = managedTxn.result;
                            EngineResult engineResult = result.engineResult;
                            if (engineResult == EngineResult.tesSUCCESS) {
                                callback.result(HDTSdkApi.CODE_SUCCESS,"success",null);
                            } else {
                                callback.result(HDTSdkApi.CODE_FAIL,"onValidated:transfer system coin fail",null);
                            }
                            hasActivityWalletBack = true;
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            ZXLogger.e(TAG, "exception:transferCurrency " + e.toString());
            if(hasActivityWalletBack) return;
            callback.result(HDTSdkApi.CODE_FAIL,"exception:transferCurrency",null);
            hasActivityWalletBack = true;
        }
    }

    public void generateWallet(final GenerateResultCallback callback) {
        try {
            if(callback == null) return;
            JSONObject jsonObject = Chainsql.c.generateAddress();
            String secret = jsonObject.getString(SECRET);
            String accountID = jsonObject.getString(ACCOUNT_ID);
            String publicKey = jsonObject.getString(PUBLIC_KEY);
            if(StringUtils.isEmpty(secret)){
                ZXLogger.e(TAG, "generateWallet failed,secret is empty!");
                callback.result(HDTSdkApi.CODE_FAIL,"fail",null);
                return;
            }

            if (!isConnect() || StringUtils.isEmpty(mIssueAddr)) {
                ZXLogger.e(TAG, "transferCurrency failed,invalid param,is sdk inited?! or private key null");
                TransferInfo transferInfo = new TransferInfo();
                transferInfo.setType(HDTSdkApi.TRANS_TYPE_TRANSFER_OUT);
                callback.result(HDTSdkApi.CODE_FAIL,
                        "transferCurrency failed,invalid param,is sdk inited?! or private key null", null);
                return;
            }

            String privateKey = "znEwBEkpenKWhL9wFXeyBvpiVu4uK4UuCr";

            Account account = Chainsql.c.getConnection().getClient().accountFromSeed(privateKey);
            TransactionManager tm = account.transactionManager();
            Payment payment = new Payment();
            BigDecimal value = new BigDecimal(40);
            Amount amountToSend = new Amount(value, Currency.fromString("BTD"),
                    AccountID.fromAddress(mIssueAddr));

            payment.as(AccountID.Account, account.id());
            payment.as(AccountID.Destination, accountID);
            payment.as(Amount.Amount, amountToSend);
            tm.queue(tm.manage(payment)
                    .onSubmitSuccess(new ManagedTxn.OnSubmitSuccess() {
                        @Override
                        public void called(Response response) {
                            ZXLogger.i(TAG, "transferCurrency,submit success!");
                        }
                    })
                    .onError(new Publisher.Callback<Response>() {
                        @Override
                        public void called(Response response) {
                            ZXLogger.e(TAG, "transferCurrency,error:" + response.result.toString());
                            callback.result(HDTSdkApi.CODE_FAIL,"transfer system coin fail",null);
                        }
                    })
                    .onValidated(new Publisher.Callback<ManagedTxn>() {
                        @Override
                        public void called(ManagedTxn managedTxn) {
                            TransactionResult result = managedTxn.result;
                            EngineResult engineResult = result.engineResult;
                            if (engineResult == EngineResult.tesSUCCESS) {
                               //信任
                            } else {
                                callback.result(HDTSdkApi.CODE_FAIL,"transfer system coin fail",null);
                            }
                        }
                    }));
        } catch (Exception e) {
            e.printStackTrace();
            ZXLogger.e(TAG, "generateWallet failed,exception:" + e.toString());
            callback.result(HDTSdkApi.CODE_FAIL,"fail",null);
        }
    }

    public void subscribeAccountTransaction(final String walletAddr, final SubscribeResultCallback subscribeResultCallback, final AccountTransactionCallback accountTransactionCallback) {
        if (!isConnect() || StringUtils.isEmpty(walletAddr) || subscribeResultCallback == null || accountTransactionCallback == null) {
            ZXLogger.e(TAG, "subscribeAccountTransaction failed,not connect or wallet or callback is null!");
            if (subscribeResultCallback != null) {
                subscribeResultCallback.subscribeResult(-1, BuildConfig.FLAVOR, -1);
                return;
            }
            return;
        }
        if (mIsSubscribeAccount && this.mReceiveListWalletAddrs != null && this.mReceiveListWalletAddrs.size() > 0) {
            getInstance().unSubscribeAccount(this.mReceiveListWalletAddrs);
        }
        Chainsql.c.getConnection().getClient().makeManagedRequest(Command.subscribe, new Manager<JSONObject>() {
            public boolean retryOnUnsuccessful(Response r) {
                return false;
            }

            public void cb(Response response, JSONObject jsonObject) throws JSONException {
                ZXLogger.d(ZXWalletManager.TAG, "subscribeAccountTransaction response jsonObject:" + jsonObject.toString());
                if (jsonObject == null || jsonObject.isNull("status")) {
                    subscribeResultCallback.subscribeResult(-1, response.engineResult().human, -1);
                } else if (jsonObject.getString("status").equals("success")) {
                    ZXWalletManager.mIsSubscribeAccount = true;
                    ZXWalletManager.this.mAccountTransactionCallback = accountTransactionCallback;
                    subscribeResultCallback.subscribeResult(0, BuildConfig.FLAVOR, 0);
                } else {
                    subscribeResultCallback.subscribeResult(-1, response.engineResult().human, -1);
                }
            }
        }, new Builder<JSONObject>() {
            public void beforeRequest(Request request) {
                JSONArray accounts_arr = new JSONArray();
                accounts_arr.put(AccountID.fromAddress(walletAddr));
                request.json("accounts", accounts_arr);
            }

            public JSONObject buildTypedResponse(Response response) {
                return response.message;
            }
        });
    }

    public void getSysCoinBalance(final String walletAddr, final BalanceInfoCallback balanceInfoCallback) {
        if (balanceInfoCallback == null) {
            ZXLogger.e(TAG, "getSysCoinBalance failed,balanceInfoCallback is null!");
        } else if (!isConnect() || StringUtils.isEmpty(walletAddr)) {
            ZXLogger.e(TAG, "getSysCoinBalance failed,not connect or wallet is null!");
            balanceInfoCallback.balanceInfo(-1, "getSysCoinBalance failed,not connect or wallet is null!", "0");
        } else {
            new Thread(new Runnable() {
                public void run() {
                    Gson gson = new Gson();
                    try {
                        Request request = Chainsql.c.getConnection().getClient().accountInfo(AccountID.fromAddress(walletAddr));
                        if (request.response != null && request.response.result != null && !StringUtils.isEmpty(request.response.result.toString())) {
                            ZXLogger.i(ZXWalletManager.TAG, "fetchAccountInfo request response result : " + request.response.result);
                            balanceInfoCallback.balanceInfo(0, BuildConfig.FLAVOR, new BigDecimal(((AccountInfoModel) gson.fromJson(request.response.result.toString(), AccountInfoModel.class)).getAccount_data().getBalance()).divide(new BigDecimal(1000000)).stripTrailingZeros().toPlainString());
                        } else if (request.response == null || !ZXWalletManager.ACCOUNT_NOT_FOUND.equals(request.response.error)) {
                            balanceInfoCallback.balanceInfo(-1, request.response.engineResult().human, "0");
                        } else {
                            ZXLogger.i(ZXWalletManager.TAG, "fetchAccountInfo ACCOUNT_NOT_FOUND!");
                            balanceInfoCallback.balanceInfo(1, BuildConfig.FLAVOR, "0");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ZXLogger.e(ZXWalletManager.TAG, "getSysCoinBalance,error:" + e.toString());
                        balanceInfoCallback.balanceInfo(-1, e.toString(), "0");
                    }
                }
            }).start();
        }
    }

    public void getIssueCurrencyBalance(final String currencyType, final String walletAddr, final BalanceInfoCallback balanceInfoCallback) {
        if (balanceInfoCallback == null) {
            ZXLogger.e(TAG, "getIssueCurrencyBalance failed,balanceInfoCallback is null!");
        } else if (!isConnect() || StringUtils.isEmpty(walletAddr)) {
            ZXLogger.e(TAG, "getIssueCurrencyBalance failed,not connect or wallet is null!");
            balanceInfoCallback.balanceInfo(-1, "getIssueCurrencyBalance failed,not connect or wallet is null!", "0");
        } else {
            new Thread(new Runnable() {
                public void run() {
                    Gson gson = new Gson();
                    try {
                        Request request = Chainsql.c.getConnection().getClient().requestAccountLines(AccountID.fromAddress(walletAddr));
                        if (request.response != null && request.response.result != null && !StringUtils.isEmpty(request.response.result.toString())) {
                            ZXLogger.i(ZXWalletManager.TAG, "getIssueCurrencyBalance request response result : " + request.response.result);
                            String balance = null;
                            boolean isFreezePeer = false;
                            Iterator<LinesBean> it = ((AccountLineModel) gson.fromJson(request.response.result.toString(), AccountLineModel.class)).getLines().iterator();
                            while (true) {
                                if (!it.hasNext()) {
                                    break;
                                }
                                LinesBean item = it.next();
                                if (item.getCurrency().equals(currencyType)) {
                                    balance = item.getBalance();
                                    isFreezePeer = item.isFreezePeer();
                                    break;
                                }
                            }
                            if (balance == null) {
                                balanceInfoCallback.balanceInfo(2, BuildConfig.FLAVOR, "0");
                            } else if (isFreezePeer) {
                                balanceInfoCallback.balanceInfo(3, BuildConfig.FLAVOR, balance);
                            } else {
                                balanceInfoCallback.balanceInfo(0, BuildConfig.FLAVOR, balance);
                            }
                        } else if (request.response == null || !ZXWalletManager.ACCOUNT_NOT_FOUND.equals(request.response.error)) {
                            balanceInfoCallback.balanceInfo(-1, request.response != null ? request.response.error_message : BuildConfig.FLAVOR, "0");
                        } else {
                            balanceInfoCallback.balanceInfo(1, BuildConfig.FLAVOR, "0");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ZXLogger.e(ZXWalletManager.TAG, "getIssueCurrencyBalance,error:" + e.toString());
                        balanceInfoCallback.balanceInfo(-1, e.toString(), "0");
                    }
                }
            }).start();
        }
    }

    public void getIssueCurrencyBalanceList(final String walletAddr, final BalanceListInfoCallback balanceInfoCallback) {
        if (balanceInfoCallback == null) {
            ZXLogger.e(TAG, "getIssueCurrencyBalance failed,balanceInfoCallback is null!");
        } else if (!isConnect() || StringUtils.isEmpty(walletAddr)) {
            ZXLogger.e(TAG, "getIssueCurrencyBalance failed,not connect or wallet is null!");
            BalanceListInfo info = new BalanceListInfo();
            info.setBalanceHdt("0");
            info.setBalanceBtd("0");
            balanceInfoCallback.balanceInfo(-1, "getIssueCurrencyBalance failed,not connect or wallet is null!", info);
        } else {
            new Thread(new Runnable() {
                public void run() {
                    Gson gson = new Gson();
                    BalanceListInfo info = new BalanceListInfo();
                    info.setBalanceHdt("0");
                    info.setBalanceBtd("0");
                    try {
                        Request request = Chainsql.c.getConnection().getClient().requestAccountLines(AccountID.fromAddress(walletAddr));
                        if (request.response != null && request.response.result != null && !StringUtils.isEmpty(request.response.result.toString())) {
                            ZXLogger.i(ZXWalletManager.TAG, "getIssueCurrencyBalance request response result : " + request.response.result);
                            String balance = null;
                            boolean isFreezePeer = false;
                            boolean geted = false;
                            for (LinesBean item : ((AccountLineModel) gson.fromJson(request.response.result.toString(), AccountLineModel.class)).getLines()) {
                                if (item.getCurrency().equals(ZXWalletManager.CURRENCY_TYPE_HDT)) {
                                    info.setBalanceHdt(item.getBalance());
                                    info.setFreezePeerHdt(item.isFreezePeer());
                                    isFreezePeer = item.isFreezePeer();
                                    if (geted) {
                                        break;
                                    }
                                    geted = true;
                                    balance = item.getBalance();
                                } else if (item.getCurrency().equals(ZXWalletManager.CURRENCY_TYPE_BTD)) {
                                    info.setBalanceBtd(item.getBalance());
                                    info.setFreezePeerBtd(item.isFreezePeer());
                                    isFreezePeer = item.isFreezePeer();
                                    if (geted) {
                                        break;
                                    }
                                    geted = true;
                                    balance = item.getBalance();
                                } else {
                                    continue;
                                }
                            }
                            if (balance == null) {
                                balanceInfoCallback.balanceInfo(2, BuildConfig.FLAVOR, info);
                            } else if (isFreezePeer) {
                                balanceInfoCallback.balanceInfo(3, BuildConfig.FLAVOR, info);
                            } else {
                                balanceInfoCallback.balanceInfo(0, BuildConfig.FLAVOR, info);
                            }
                        } else if (request.response == null || !ZXWalletManager.ACCOUNT_NOT_FOUND.equals(request.response.error)) {
                            balanceInfoCallback.balanceInfo(-1, request.response != null ? request.response.error_message : BuildConfig.FLAVOR, info);
                        } else {
                            balanceInfoCallback.balanceInfo(1, BuildConfig.FLAVOR, info);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ZXLogger.e(ZXWalletManager.TAG, "getIssueCurrencyBalance,error:" + e.toString());
                        balanceInfoCallback.balanceInfo(-1, e.toString(), info);
                    }
                }
            }).start();
        }
    }

    public void getIssueCurrencyTxDetail(final String walletAddr, int limit, String marker, final CurrencyTxsInfoCallback currencyTxsInfoCallback) {
        if (currencyTxsInfoCallback == null) {
            ZXLogger.e(TAG, "getIssueCurrencyTxDetail failed,currencyTxsInfoCallback is null!");
        } else if (!isConnect() || StringUtils.isEmpty(walletAddr)) {
            ZXLogger.e(TAG, "getIssueCurrencyTxDetail failed,not connect or wallet is null!");
            currencyTxsInfoCallback.currencyTxsInfo(-1, "getIssueCurrencyTxDetail failed,not connect or wallet is null!", null);
        } else {
            JSONObject jsonMarker = null;
            try {
                if (!StringUtils.isEmpty(marker)) {
                    jsonMarker = new JSONObject(marker);
                }
                Chainsql.c.getConnection().getClient().getTransactions(walletAddr, limit, jsonMarker, new Callback<JSONObject>() {
                    public void called(JSONObject jsonObject) {
                        if (jsonObject != null) {
                            ZXLogger.d(ZXWalletManager.TAG, "getTransactions:" + jsonObject.toString());
                            CurrencyTxDetails currencyTxDetails = new CurrencyTxDetails();
                            try {
                                if (jsonObject.isNull("marker")) {
                                    currencyTxDetails.setMarker(BuildConfig.FLAVOR);
                                } else {
                                    JSONObject newJsonMarker = jsonObject.getJSONObject("marker");
                                    if (newJsonMarker == null) {
                                        currencyTxDetails.setMarker(BuildConfig.FLAVOR);
                                    } else {
                                        currencyTxDetails.setMarker(newJsonMarker.toString());
                                    }
                                }
                                ArrayList<CurrencyTxDetail> currencyTxDetailList = new ArrayList<>();
                                JSONArray txArray = jsonObject.getJSONArray("transactions");
                                if (txArray != null) {
                                    for (int i = 0; i < txArray.length(); i++) {
                                        JSONObject tempTxJson = txArray.getJSONObject(i);
                                        if (!tempTxJson.isNull("tx")) {
                                            JSONObject txJson = tempTxJson.getJSONObject("tx");
                                            if (txJson.getString("TransactionType").equals("Payment")) {
                                                boolean isSysCoinPayment = false;
                                                JSONObject amountJson = null;
                                                try {
                                                    amountJson = txJson.getJSONObject("Amount");
                                                } catch (Exception e) {
                                                    isSysCoinPayment = true;
                                                }
                                                if (amountJson != null && !isSysCoinPayment) {
                                                    String currency = amountJson.getString("currency");
                                                    if (currency.equals(ZXWalletManager.CURRENCY_TYPE_HDT) || currency.equals(ZXWalletManager.CURRENCY_TYPE_BTD)) {
                                                        String value = amountJson.getString("value");
                                                        String account = txJson.getString("Account");
                                                        String destination = txJson.getString("Destination");
                                                        String txId = txJson.getString("hash");
                                                        RippleDate fromSecondsSinceRippleEpoch = RippleDate.fromSecondsSinceRippleEpoch(Long.valueOf(txJson.getLong("date")));
                                                        CurrencyTxDetail currencyTxDetail = new CurrencyTxDetail();
                                                        currencyTxDetail.setTxId(txId);
                                                        currencyTxDetail.setDate(fromSecondsSinceRippleEpoch);
                                                        if (destination.equals(walletAddr)) {
                                                            currencyTxDetail.setTxType(2);
                                                        } else {
                                                            currencyTxDetail.setTxType(3);
                                                        }
                                                        currencyTxDetail.setFromAddr(account);
                                                        currencyTxDetail.setToAddr(destination);
                                                        currencyTxDetail.setAmount(value);
                                                        currencyTxDetail.setCoinType(currency);
                                                        if (txJson.has("SendMax")) {
                                                            BigDecimal bigDecimal = new BigDecimal(txJson.getJSONObject("SendMax").getString("value"));
                                                            BigDecimal bigDecimal2 = new BigDecimal(value);
                                                            currencyTxDetail.setTransferFee(bigDecimal.subtract(bigDecimal2).toPlainString());
                                                        } else {
                                                            currencyTxDetail.setTransferFee("0");
                                                        }
                                                        currencyTxDetailList.add(currencyTxDetail);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    currencyTxDetails.setCurrencyTxDetailList(currencyTxDetailList);
                                    if (currencyTxsInfoCallback != null) {
                                        currencyTxsInfoCallback.currencyTxsInfo(0, BuildConfig.FLAVOR, currencyTxDetails);
                                    }
                                }
                            } catch (Exception e2) {
                                e2.printStackTrace();
                                if (currencyTxsInfoCallback != null) {
                                    currencyTxsInfoCallback.currencyTxsInfo(-1, e2.toString(), null);
                                }
                            }
                        } else if (currencyTxsInfoCallback != null) {
                            currencyTxsInfoCallback.currencyTxsInfo(-1, "Called json is null,input param may be invalid!", null);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                currencyTxsInfoCallback.currencyTxsInfo(-1, e.toString(), null);
            }
        }
    }

    public void trustIssueCurrency(String currencyType, String privateKey, final CommonTransactionCallback commonTransactionCallback) {
        if (commonTransactionCallback == null) {
            ZXLogger.e(TAG, "trustIssueCurrency failed,commonTransactionCallback is null");
        } else if (!isConnect() || StringUtils.isEmpty(privateKey) || StringUtils.isEmpty(this.mIssueAddr)) {
            ZXLogger.e(TAG, "trustIssueCurrency failed,invalid param,is sdk inited?! or private key null");
            commonTransactionCallback.transactionResult(-1, "trustIssueCurrency failed,invalid param,is sdk inited?! or private key null", new CommonTransInfo(1, BuildConfig.FLAVOR));
        } else {
            try {
                Account account = Chainsql.c.getConnection().getClient().accountFromSeed(privateKey);
                TransactionManager tm = account.transactionManager();
                TrustSet trustSet = new TrustSet();
                trustSet.as(Amount.LimitAmount, new Amount(new BigDecimal(100000000), Currency.fromString(currencyType), AccountID.fromAddress(this.mIssueAddr)));
                trustSet.as(AccountID.Account, account.id());
                tm.queue(tm.manage(trustSet).onSubmitSuccess(new OnSubmitSuccess() {
                    public void called(Response response) {
                        ZXLogger.i(ZXWalletManager.TAG, "trustIssueCurrency,submit success!");
                    }
                }).onError(new Callback<Response>() {
                    public void called(Response response) {
                        ZXLogger.e(ZXWalletManager.TAG, "trustIssueCurrency,error:" + response.result.toString());
                        try {
                            if (String.valueOf(response.result.get("engine_result")).equals("terQUEUED")) {
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        commonTransactionCallback.transactionResult(-1, response.engineResult().human, new CommonTransInfo(1, BuildConfig.FLAVOR));
                    }
                }).onValidated(new Callback<ManagedTxn>() {
                    public void called(ManagedTxn managedTxn) {
                        TransactionResult result = managedTxn.result;
                        if (result.engineResult == EngineResult.tesSUCCESS) {
                            commonTransactionCallback.transactionResult(0, BuildConfig.FLAVOR, new CommonTransInfo(1, result.hash.toString()));
                            return;
                        }
                        commonTransactionCallback.transactionResult(-1, result.engineResult.human, new CommonTransInfo(1, BuildConfig.FLAVOR));
                    }
                }));
            } catch (Exception e) {
                e.printStackTrace();
                commonTransactionCallback.transactionResult(-1, e.toString(), new CommonTransInfo(1, BuildConfig.FLAVOR));
            }
        }
    }

    public void transferCurrency(String currencyType, String privateKey, String toWalletAddr, final String amount, String remark, String type, final AccountTransactionCallback accountTransactionCallback) {
        if (null == accountTransactionCallback) {
            ZXLogger.e(TAG, "transferCurrency failed,invalid param,accountTransactionCallback is null");
            return;
        }

        if (!isConnect() || StringUtils.isEmpty(privateKey) || StringUtils.isEmpty(mIssueAddr)) {
            ZXLogger.e(TAG, "transferCurrency failed,invalid param,is sdk inited?! or private key null");
            TransferInfo transferInfo = new TransferInfo();
            transferInfo.setType(HDTSdkApi.TRANS_TYPE_TRANSFER_OUT);
            accountTransactionCallback.accountTransactionResult(HDTSdkApi.CODE_FAIL,
                    "transferCurrency failed,invalid param,is sdk inited?! or private key null", transferInfo);
            return;
        }

        //预防私钥输入错误
        Account account;
        final String address;
        try {
            account = Chainsql.c.getConnection().getClient().accountFromSeed(privateKey);
            address = account.id().address;
        } catch (Exception e) {
            e.printStackTrace();
            TransferInfo transferInfo = new TransferInfo();
            transferInfo.setType(HDTSdkApi.TRANS_TYPE_TRANSFER_OUT);
            transferInfo.setFromAddr("");
            transferInfo.setToAddr(toWalletAddr);
            transferInfo.setAmount(amount);
            accountTransactionCallback.accountTransactionResult(HDTSdkApi.CODE_FAIL, e.toString(), transferInfo);
            return;
        }
        try {

            TransactionManager tm = account.transactionManager();
            Payment payment = new Payment();
            BigDecimal value = new BigDecimal(amount);
            Amount amountToSend = new Amount(value, Currency.fromString(currencyType),
                    AccountID.fromAddress(mIssueAddr));

            payment.as(AccountID.Account, account.id());
            payment.as(AccountID.Destination, toWalletAddr);
            payment.as(Amount.Amount, amountToSend);

            //新加备注字段 2018.7.12  start
            if (!StringUtils.isEmpty(type) || !StringUtils.isEmpty(remark)) {
                JSONArray array = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                JSONObject memo = new JSONObject();
                String strType = StringUtils.str2HexStr(type);
                remark = StringUtils.str2HexStr(remark);
                memo.put("MemoType", strType);
                memo.put("MemoData", remark);
                jsonObject.put("Memo", memo);
                array.put(jsonObject);
                STArray stArray = STArray.translate.fromJSONArray(array);
                payment.put(STArray.Memos, stArray);
            }
            //新加备注字段 2018.7.12  end

            if (!StringUtils.isEmpty(mTransferFee)) {
                BigDecimal transferFee = new BigDecimal(mTransferFee);
                BigDecimal sendMaxValue = value.add(transferFee);
                Amount sendMax = new Amount(sendMaxValue, Currency.fromString(currencyType),
                        AccountID.fromAddress(mIssueAddr));

                payment.as(Amount.SendMax, sendMax);
            }

            tm.queue(tm.manage(payment)
                    .onSubmitSuccess(new ManagedTxn.OnSubmitSuccess() {
                        @Override
                        public void called(Response response) {
                            ZXLogger.i(TAG, "transferCurrency,submit success!");
                        }
                    })
                    .onError(new Publisher.Callback<Response>() {
                        @Override
                        public void called(Response response) {
                            ZXLogger.e(TAG, "transferCurrency,error:" + response.result.toString());

                            try {
                                JSONObject result = response.result;
                                String engine_result = String.valueOf(result.get("engine_result"));

                                if (engine_result.equals("terQUEUED")) {
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            TransferInfo transferInfo = new TransferInfo();
                            transferInfo.setType(HDTSdkApi.TRANS_TYPE_TRANSFER_OUT);
                            accountTransactionCallback.accountTransactionResult(HDTSdkApi.CODE_FAIL, response.engineResult().human, transferInfo);
                        }
                    })
                    .onValidated(new Publisher.Callback<ManagedTxn>() {
                        @Override
                        public void called(ManagedTxn managedTxn) {
                            TransactionResult result = managedTxn.result;
                            EngineResult engineResult = result.engineResult;
                            if (engineResult == EngineResult.tesSUCCESS) {
                                TransferInfo transferInfo = new TransferInfo();
                                transferInfo.setType(HDTSdkApi.TRANS_TYPE_TRANSFER_OUT);
                                transferInfo.setFromAddr(address);
                                transferInfo.setTxId(result.hash.toString());
                                AccountID destination = result.txn.get(AccountID.Destination);
                                transferInfo.setToAddr(destination.address);
                                transferInfo.setAmount(amount);
                                accountTransactionCallback.accountTransactionResult(HDTSdkApi.CODE_SUCCESS, "", transferInfo);
                            } else {
                                TransferInfo transferInfo = new TransferInfo();
                                transferInfo.setType(HDTSdkApi.TRANS_TYPE_TRANSFER_OUT);
                                accountTransactionCallback.accountTransactionResult(HDTSdkApi.CODE_FAIL, result.engineResult.human, transferInfo);
                            }
                        }
                    }));

        } catch (Exception e) {
            e.printStackTrace();
            TransferInfo transferInfo = new TransferInfo();
            transferInfo.setType(HDTSdkApi.TRANS_TYPE_TRANSFER_OUT);
            transferInfo.setFromAddr(address);
            transferInfo.setToAddr(toWalletAddr);
            transferInfo.setAmount(amount);
            accountTransactionCallback.accountTransactionResult(HDTSdkApi.CODE_FAIL, e.toString(), transferInfo);
        }
    }

    public void offerCreate(String currencyType, String privateKey, String payAmount, String getAmount, String remark, String type, OfferCallback offerCallback) {
        if (offerCallback == null) {
            ZXLogger.e(TAG, "offerCreate failed,invalid param,accountTransactionCallback is null");
        } else if (!isConnect() || StringUtils.isEmpty(privateKey) || StringUtils.isEmpty(this.mIssueAddr)) {
            ZXLogger.e(TAG, "offerCreate failed,invalid param,is sdk inited?! or private key null");
            OfferInfo offerInfo = new OfferInfo();
            offerInfo.setType(1);
            offerCallback.offerResult(-1, "offerCreate failed,invalid param,is sdk inited?! or private key null", offerInfo);
        } else {
            try {
                Account account = Chainsql.c.getConnection().getClient().accountFromSeed(privateKey);
                final String addr = account.id().address;
                try {
                    TransactionManager tm = account.transactionManager();
                    OfferCreate offerCreate = new OfferCreate();
                    String payCurrentcyType = currencyType.equals(CURRENCY_TYPE_HDT) ? CURRENCY_TYPE_BTD : CURRENCY_TYPE_HDT;
                    BigDecimal bigDecimal = new BigDecimal(payAmount);
                    Amount amountPay = new Amount(bigDecimal, Currency.fromString(payCurrentcyType), AccountID.fromAddress(this.mIssueAddr));
                    offerCreate.as(AccountID.Account, account.id());
                    offerCreate.as(Amount.TakerPays, amountPay);
                    offerCreate.as(Amount.TakerGets, new Amount(new BigDecimal(getAmount), Currency.fromString(currencyType), AccountID.fromAddress(this.mIssueAddr)));
                    if (!StringUtils.isEmpty(type) || !StringUtils.isEmpty(remark)) {
                        JSONArray array = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        JSONObject memo = new JSONObject();
                        String strType = StringUtils.str2HexStr(type);
                        String remark2 = StringUtils.str2HexStr(remark);
                        memo.put("MemoType", strType);
                        memo.put("MemoData", remark2);
                        jsonObject.put("Memo", memo);
                        array.put(jsonObject);
                        offerCreate.put(STArray.Memos, STArray.translate.fromJSONArray(array));
                    }
                    final OfferCallback offerCallback2 = offerCallback;
                    final String str = payAmount;
                    final String str2 = getAmount;
                    final OfferCallback offerCallback3 = offerCallback;
                    tm.queue(tm.manage(offerCreate).onSubmitSuccess(new OnSubmitSuccess() {
                        public void called(Response response) {
                            ZXLogger.i(ZXWalletManager.TAG, "offerCreate,submit success!");
                        }
                    }).onError(new Callback<Response>() {
                        public void called(Response response) {
                            ZXLogger.e(ZXWalletManager.TAG, "offerCreate,error:" + response.result.toString());
                            try {
                                if (String.valueOf(response.result.get("engine_result")).equals("terQUEUED")) {
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            OfferInfo offerInfo = new OfferInfo();
                            offerInfo.setType(1);
                            offerCallback2.offerResult(-1, response.engineResult().human, offerInfo);
                        }
                    }).onValidated(new Callback<ManagedTxn>() {
                        public void called(ManagedTxn managedTxn) {
                            TransactionResult result = managedTxn.result;
                            if (result.engineResult == EngineResult.tesSUCCESS) {
                                OfferInfo offerInfo = new OfferInfo();
                                offerInfo.setType(1);
                                offerInfo.setAccount(addr);
                                offerInfo.setTxId(result.hash.toString());
                                AccountID accountID = result.txn.get(AccountID.Destination);
                                offerInfo.setTakerPays(str);
                                offerInfo.setTakerGets(str2);
                                offerInfo.setSequence(result.txn.sequence().value().longValue());
                                offerCallback3.offerResult(0, BuildConfig.FLAVOR, offerInfo);
                                return;
                            }
                            OfferInfo offerInfo2 = new OfferInfo();
                            offerInfo2.setType(1);
                            offerCallback3.offerResult(-1, result.engineResult.human, offerInfo2);
                        }
                    }));
                } catch (Exception e) {
                    e.printStackTrace();
                    OfferInfo offerInfo2 = new OfferInfo();
                    offerInfo2.setType(1);
                    offerInfo2.setTakerPays(payAmount);
                    offerInfo2.setTakerGets(getAmount);
                    offerInfo2.setAccount(addr);
                    offerCallback.offerResult(-1, e.toString(), offerInfo2);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                OfferInfo offerInfo3 = new OfferInfo();
                offerInfo3.setType(1);
                offerInfo3.setTakerPays(payAmount);
                offerInfo3.setTakerGets(getAmount);
                offerInfo3.setAccount(BuildConfig.FLAVOR);
                offerCallback.offerResult(-1, e2.toString(), offerInfo3);
            }
        }
    }

    public void offerCancel(String currencyType, String privateKey, String remark, String type, long offerSequence, OfferCallback offerCallback) {
        if (offerCallback == null) {
            ZXLogger.e(TAG, "offerCancel failed,invalid param,accountTransactionCallback is null");
        } else if (!isConnect() || StringUtils.isEmpty(privateKey) || StringUtils.isEmpty(this.mIssueAddr)) {
            ZXLogger.e(TAG, "offerCancel failed,invalid param,is sdk inited?! or private key null");
            OfferInfo offerInfo = new OfferInfo();
            offerInfo.setType(2);
            offerCallback.offerResult(-1, "offerCancel failed,invalid param,is sdk inited?! or private key null", offerInfo);
        } else {
            try {
                Account account = Chainsql.c.getConnection().getClient().accountFromSeed(privateKey);
                final String address = account.id().address;
                try {
                    TransactionManager tm = account.transactionManager();
                    OfferCancel offerCancel = new OfferCancel();
                    offerCancel.as(AccountID.Account, account.id());
                    offerCancel.put(Field.OfferSequence, UInt32.translate.fromLong(offerSequence));
                    if (!StringUtils.isEmpty(type) || !StringUtils.isEmpty(remark)) {
                        JSONArray array = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        JSONObject memo = new JSONObject();
                        String strType = StringUtils.str2HexStr(type);
                        String remark2 = StringUtils.str2HexStr(remark);
                        memo.put("MemoType", strType);
                        memo.put("MemoData", remark2);
                        jsonObject.put("Memo", memo);
                        array.put(jsonObject);
                        offerCancel.put(STArray.Memos, STArray.translate.fromJSONArray(array));
                    }
                    final OfferCallback offerCallback2 = offerCallback;
                    final OfferCallback offerCallback3 = offerCallback;
                    tm.queue(tm.manage(offerCancel).onSubmitSuccess(new OnSubmitSuccess() {
                        public void called(Response response) {
                            ZXLogger.i(ZXWalletManager.TAG, "offerCancel,submit success!" + response.result);
                        }
                    }).onError(new Callback<Response>() {
                        public void called(Response response) {
                            ZXLogger.e(ZXWalletManager.TAG, "offerCancel,error:" + response.result.toString());
                            try {
                                if (String.valueOf(response.result.get("engine_result")).equals("terQUEUED")) {
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            OfferInfo offerInfo = new OfferInfo();
                            offerInfo.setType(2);
                            offerCallback2.offerResult(-1, response.engineResult().human, offerInfo);
                        }
                    }).onValidated(new Callback<ManagedTxn>() {
                        public void called(ManagedTxn managedTxn) {
                            TransactionResult result = managedTxn.result;
                            if (result.engineResult == EngineResult.tesSUCCESS) {
                                OfferInfo offerInfo = new OfferInfo();
                                offerInfo.setType(2);
                                offerInfo.setAccount(address);
                                offerInfo.setTxId(result.hash.toString());
                                AccountID accountID = result.txn.get(AccountID.Destination);
                                offerCallback3.offerResult(0, BuildConfig.FLAVOR, offerInfo);
                                return;
                            }
                            OfferInfo offerInfo2 = new OfferInfo();
                            offerInfo2.setType(2);
                            offerCallback3.offerResult(-1, result.engineResult.human, offerInfo2);
                        }
                    }));
                } catch (Exception e) {
                    e.printStackTrace();
                    OfferInfo offerInfo2 = new OfferInfo();
                    offerInfo2.setType(2);
                    offerInfo2.setAccount(address);
                    offerCallback.offerResult(-1, e.toString(), offerInfo2);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                OfferInfo offerInfo3 = new OfferInfo();
                offerInfo3.setType(2);
                offerInfo3.setAccount(BuildConfig.FLAVOR);
                offerCallback.offerResult(-1, e2.toString(), offerInfo3);
            }
        }
    }

    public void accountOffers(String walletAddr, int limit, String marker, AccountOfferCallback accountOfferCallback) {
        if (accountOfferCallback == null) {
            ZXLogger.e(TAG, "accountOffers failed,invalid param,accountTransactionCallback is null");
        } else if (StringUtils.isEmpty(walletAddr) || StringUtils.isEmpty(this.mIssueAddr)) {
            ZXLogger.e(TAG, "accountOffers failed,invalid param,is sdk inited?! or private key null");
            accountOfferCallback.accountOfferCallback(-1, "accountOffers failed,invalid param,is sdk inited?! or private key null", null);
        } else {
            final String str = walletAddr;
            final int i = limit;
            final String str2 = marker;
            final AccountOfferCallback accountOfferCallback2 = accountOfferCallback;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Request request = Chainsql.c.getConnection().getClient().newRequest(Command.account_offers);
                        request.json("account", str);
                        request.json("limit", Integer.valueOf(i));
                        if (!StringUtils.isEmpty(str2)) {
                            request.json("marker", str2);
                        }
                        request.request();
                        ZXWalletManager.this.waiting(request);
                        if (request.response == null || request.response.result == null || StringUtils.isEmpty(request.response.result.toString())) {
                            ZXLogger.e(ZXWalletManager.TAG, "accountOffers,error:" + request.toString());
                            AccountOfferModel offerInfo = new AccountOfferModel();
                            offerInfo.setAccount(str);
                            accountOfferCallback2.accountOfferCallback(-1, request.toString(), offerInfo);
                            return;
                        }
                        ZXLogger.i(ZXWalletManager.TAG, "accountOffers request response result : " + request.response.result);
                        AccountOfferModel accountOfferModel = (AccountOfferModel) new Gson().fromJson(request.response.result.toString(), AccountOfferModel.class);
                        if (accountOfferModel != null) {
                            accountOfferCallback2.accountOfferCallback(0, BuildConfig.FLAVOR, accountOfferModel);
                        } else {
                            accountOfferCallback2.accountOfferCallback(-1, request.response.result.toString(), null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ZXLogger.e(ZXWalletManager.TAG, "accountOffers,error:" + e.toString());
                        AccountOfferModel offerInfo2 = new AccountOfferModel();
                        offerInfo2.setAccount(str);
                        accountOfferCallback2.accountOfferCallback(-1, e.toString(), offerInfo2);
                    }
                }
            }).start();
        }
    }

    public void subscribe(final String currencyType, final List<String> walletAddrs, final boolean isLisenOffer, boolean both, SubscribeResultCallback subscribeResultCallback, ReceiveListCallback receiveListCallback) {
        if (receiveListCallback == null || subscribeResultCallback == null || walletAddrs == null || walletAddrs.size() < 1 || StringUtils.isEmpty(this.mIssueAddr)) {
            ZXLogger.e(TAG, "subscribe failed,invalid param,is sdk inited?! or walletAddrs key null");
            new OfferInfo().setType(1);
            if (subscribeResultCallback != null) {
                subscribeResultCallback.subscribeResult(-1, BuildConfig.FLAVOR, -1);
                return;
            }
            return;
        }
        if (mIsSubscribeAccount && this.mReceiveListWalletAddrs != null && this.mReceiveListWalletAddrs.size() > 0) {
            getInstance().unSubscribeAccount(this.mReceiveListWalletAddrs);
        }
        final List<String> list = walletAddrs;
        final ReceiveListCallback receiveListCallback2 = receiveListCallback;
        final String str = currencyType;
        final boolean z = both;
        final SubscribeResultCallback subscribeResultCallback2 = subscribeResultCallback;
        Chainsql.c.getConnection().getClient().makeManagedRequest(Command.subscribe, new Manager<JSONObject>() {
            public boolean retryOnUnsuccessful(Response r) {
                return false;
            }

            public void cb(Response response, JSONObject jsonObject) throws JSONException {
                ZXLogger.d(ZXWalletManager.TAG, "subscribeAccountTransaction response jsonObject:" + jsonObject.toString());
                if (jsonObject == null || jsonObject.isNull("status")) {
                    subscribeResultCallback2.subscribeResult(-1, response.engineResult().human, -1);
                } else if (jsonObject.getString("status").equals("success")) {
                    ZXWalletManager.mIsSubscribeAccount = true;
                    ZXWalletManager.this.mReceiveListWalletAddrs = list;
                    ZXWalletManager.this.mReceiveListCallback = receiveListCallback2;
                    ZXWalletManager.this.mCurReceiveListCurrencyType = str;
                    ZXWalletManager.this.isBoth = z;
                    ZXWalletManager.this.isReceiveListLisenOffer = true;
                    subscribeResultCallback2.subscribeResult(0, BuildConfig.FLAVOR, 0);
                } else {
                    subscribeResultCallback2.subscribeResult(-1, response.engineResult().human, -1);
                }
            }
        }, new Builder<JSONObject>() {
            public void beforeRequest(Request request) {
                JSONArray accounts_arr = new JSONArray();
                for (String addr : walletAddrs) {
                    accounts_arr.put(addr);
                }
                if (isLisenOffer) {
                    try {
                        String otherCurrentType = currencyType.equals(ZXWalletManager.CURRENCY_TYPE_HDT) ? ZXWalletManager.CURRENCY_TYPE_BTD : ZXWalletManager.CURRENCY_TYPE_HDT;
                        JSONObject object = new JSONObject();
                        JSONArray books = new JSONArray();
                        JSONObject taker_pays = new JSONObject();
                        taker_pays.put("currency", currencyType);
                        taker_pays.put("issuer", ZXWalletManager.this.mIssueAddr);
                        object.put("taker_pays", taker_pays);
                        JSONObject taker_gets = new JSONObject();
                        taker_gets.put("currency", otherCurrentType);
                        taker_gets.put("issuer", ZXWalletManager.this.mIssueAddr);
                        object.put("taker_gets", taker_gets);
                        object.put("snapshot", true);
                        books.put(object);
                        request.json("books", books);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                request.json("accounts", accounts_arr);
            }

            public JSONObject buildTypedResponse(Response response) {
                return response.message;
            }
        });
    }

    /* access modifiers changed from: private */
    public void waiting(Request request) {
        int count = 50;
        while (request.response == null) {
            Util.waiting();
            count--;
            if (count == 0) {
                return;
            }
        }
    }

    public boolean isLegalAddress(String address) {
        try {
            if (AccountID.fromAddress(address).address.equals(address)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void getTransferFee(final TransferFeeCallback transferFeeCallback) {
        if (transferFeeCallback == null) {
            ZXLogger.e(TAG, "getTransferFee failed,invalid param,transferFeeCallback is null!");
        } else if (!isConnect() || StringUtils.isEmpty(this.mIssueAddr)) {
            ZXLogger.e(TAG, "getTransferFee failed,invalid param,is sdk inited?!");
            transferFeeCallback.transferFeeInfo(-1, "getTransferFee failed,invalid param,is sdk inited?!", "0");
        } else {
            new Thread(new Runnable() {
                public void run() {
                    Gson gson = new Gson();
                    try {
                        Request request = Chainsql.c.getConnection().getClient().accountInfo(AccountID.fromAddress(ZXWalletManager.this.mIssueAddr));
                        if (request.response == null || request.response.result == null || StringUtils.isEmpty(request.response.result.toString())) {
                            transferFeeCallback.transferFeeInfo(-1, request.response == null ? BuildConfig.FLAVOR : request.response.toString(), "0");
                            ZXWalletManager.this.mTransferFee = "0";
                            return;
                        }
                        ZXLogger.i(ZXWalletManager.TAG, "getTransferFee request response result : " + request.response.result);
                        String transferFeeMin = ((AccountInfoModel) gson.fromJson(request.response.result.toString(), AccountInfoModel.class)).getAccount_data().getTransferFeeMin();
                        ZXLogger.i(ZXWalletManager.TAG, "!!!!transferFeeFix:" + transferFeeMin);
                        if (!StringUtils.isEmpty(transferFeeMin)) {
                            ZXWalletManager.this.mTransferFee = transferFeeMin;
                        } else {
                            ZXWalletManager.this.mTransferFee = "0";
                        }
                        transferFeeCallback.transferFeeInfo(0, BuildConfig.FLAVOR, ZXWalletManager.this.mTransferFee);
                    } catch (Exception e) {
                        ZXWalletManager.this.mTransferFee = "0";
                        e.printStackTrace();
                        ZXLogger.e(ZXWalletManager.TAG, "getTransferFee,error:" + e.toString());
                        transferFeeCallback.transferFeeInfo(-1, e.toString(), "0");
                    }
                }
            }).start();
        }
    }

    public int getConnectState() {
        if (this.mIsWebSocketConnecting) {
            return 1;
        }
        if (isConnect()) {
            return 0;
        }
        return -1;
    }

    /* access modifiers changed from: private */
    public void setupChainsqlProcess() {
        Chainsql.c.getConnection().getClient().onConnected(new OnConnected() {
            public void called(Client client) {
                ZXLogger.d(ZXWalletManager.TAG, "Chainsql.c.getConnection().getClient().onConnected!");
                ZXWalletManager.this.mIsWebSocketConnecting = false;
                if (ZXWalletManager.this.mConnectDelegate != null) {
                    ZXWalletManager.this.mConnectDelegate.connectState(0);
                }
                if (ZXWalletManager.mIsSubscribeAccount && ZXWalletManager.this.mReceiveListWalletAddrs != null && ZXWalletManager.this.mReceiveListWalletAddrs.size() > 0) {
                    ZXWalletManager.getInstance().unSubscribeAccount(ZXWalletManager.this.mReceiveListWalletAddrs);
                }
                if (ZXWalletManager.this.mReceiveListWalletAddrs != null && ZXWalletManager.this.mReceiveListWalletAddrs.size() > 0) {
                    ZXWalletManager.getInstance().subscribeAccount(ZXWalletManager.this.mReceiveListWalletAddrs);
                }
                ZXWalletManager.this.getTransferFee(new TransferFeeCallback() {
                    public void transferFeeInfo(int code, String message, String transferFee) {
                    }
                });
            }
        });
        Chainsql.c.getConnection().getClient().onDisconnected(new OnDisconnected() {
            public void called(Client client) {
                ZXLogger.d(ZXWalletManager.TAG, "Chainsql.c.getConnection().getClient().onDisconnected!");
                ZXWalletManager.this.mIsWebSocketConnecting = false;
                if (ZXWalletManager.this.mConnectDelegate != null) {
                    ZXWalletManager.this.mConnectDelegate.connectState(-1);
                }
            }
        });
        Chainsql.c.getConnection().getClient().OnMessage(new OnMessage() {
            public void called(JSONObject jsonObject) {
                ZXLogger.i(ZXWalletManager.TAG, "onMessage json object " + jsonObject.toString());
                if (jsonObject != null && !StringUtils.isEmpty(ZXWalletManager.this.mCurReceiveListCurrencyType)) {
                    try {
                        if (jsonObject.getString("type").equals("transaction") && ZXWalletManager.this.mReceiveListWalletAddrs != null && ZXWalletManager.this.mReceiveListWalletAddrs.size() > 0) {
                            TransactionResult transactionResult = new TransactionResult(jsonObject, Source.transaction_subscription_notification);
                            if (transactionResult.validated && !ZXWalletManager.this.mLastTxId.equals(transactionResult.hash.toString())) {
                                Transaction txn = transactionResult.txn;
                                AccountID account = txn.get(AccountID.Account);
                                AccountID destination = txn.get(AccountID.Destination);
                                String txId = transactionResult.hash.toString();
                                if (ZXWalletManager.this.mReceiveListCallback != null && ZXWalletManager.this.mReceiveListWalletAddrs != null && ZXWalletManager.this.mReceiveListWalletAddrs.size() > 0) {
                                    if (transactionResult.transactionType() == TransactionType.Payment) {
                                        Amount recvAmount = txn.get(Amount.Amount);
                                        String coinType = recvAmount.currency().humanCode();
                                        if (coinType.equals(ZXWalletManager.CURRENCY_TYPE_HDT) || coinType.equals(ZXWalletManager.CURRENCY_TYPE_BTD)) {
                                            ReceiveInfo receiveInfo = new ReceiveInfo();
                                            receiveInfo.setType(2);
                                            receiveInfo.setFromAddr(account.address);
                                            receiveInfo.setTxId(txId);
                                            receiveInfo.setToAddr(destination.address);
                                            receiveInfo.setCurrencyType(coinType);
                                            receiveInfo.setAmount(recvAmount.value().toPlainString());
                                            if (ZXWalletManager.this.isBoth) {
                                                ZXWalletManager.this.mReceiveListCallback.receiveListCallback(0, BuildConfig.FLAVOR, receiveInfo);
                                            } else if (coinType.equals(ZXWalletManager.this.mCurReceiveListCurrencyType)) {
                                                ZXWalletManager.this.mReceiveListCallback.receiveListCallback(0, BuildConfig.FLAVOR, receiveInfo);
                                            }
                                            ZXWalletManager.this.mLastTxId = txId;
                                            return;
                                        }
                                        return;
                                    }
                                    if (transactionResult.transactionType() == TransactionType.OfferCreate && ZXWalletManager.this.isReceiveListLisenOffer) {
                                        if ("closed".equals(jsonObject.optString("status"))) {
                                            JSONObject transaction = jsonObject.optJSONObject("transaction");
                                            if (transaction != null) {
                                                String offerAccount = transaction.optString("Account");
                                                boolean isMyOfferCreate = false;
                                                Iterator it = ZXWalletManager.this.mReceiveListWalletAddrs.iterator();
                                                while (true) {
                                                    if (it.hasNext()) {
                                                        if (((String) it.next()).equals(offerAccount)) {
                                                            isMyOfferCreate = true;
                                                            break;
                                                        }
                                                    } else {
                                                        break;
                                                    }
                                                }
                                                if (isMyOfferCreate) {
                                                    String coinType2 = BuildConfig.FLAVOR;
                                                    JSONObject takerGets = transaction.optJSONObject("TakerGets");
                                                    if (takerGets != null) {
                                                        coinType2 = takerGets.optString("currency");
                                                    }
                                                    ArrayList<String> otherAccounts = new ArrayList<>();
                                                    Iterable<AffectedNode> affectedNodes = transactionResult.meta.affectedNodes();
                                                    while (affectedNodes.iterator().hasNext()) {
                                                        JSONObject modifiedNode = affectedNodes.iterator().next().toJSONObject().optJSONObject("ModifiedNode");
                                                        if (modifiedNode != null) {
                                                            JSONObject finalFields = modifiedNode.optJSONObject("FinalFields");
                                                            if (finalFields != null) {
                                                                String otherAccount = finalFields.optString("Account");
                                                                if (!offerAccount.equals(otherAccount) && !StringUtils.isEmpty(otherAccount)) {
                                                                    otherAccounts.add(otherAccount);
                                                                }
                                                            }
                                                        }
                                                    }
                                                    ReceiveInfo info = new ReceiveInfo();
                                                    info.setTxId(txId);
                                                    info.setType(4);
                                                    info.setOfferAddr(offerAccount);
                                                    info.setCurrencyType(coinType2);
                                                    info.setOtherOfferAddrs(otherAccounts);
                                                    if (ZXWalletManager.this.isBoth) {
                                                        ZXWalletManager.this.mReceiveListCallback.receiveListCallback(0, BuildConfig.FLAVOR, info);
                                                    } else if (coinType2.equals(ZXWalletManager.this.mCurReceiveListCurrencyType)) {
                                                        ZXWalletManager.this.mReceiveListCallback.receiveListCallback(0, BuildConfig.FLAVOR, info);
                                                    }
                                                    ZXWalletManager.this.mLastTxId = txId;
                                                    return;
                                                }
                                                return;
                                            }
                                            return;
                                        }
                                        ReceiveInfo info2 = new ReceiveInfo();
                                        Iterable<AffectedNode> affectedNodes2 = transactionResult.meta.affectedNodes();
                                        while (affectedNodes2.iterator().hasNext()) {
                                            JSONObject affecteObject = affectedNodes2.iterator().next().toJSONObject();
                                            JSONObject deletedNodeObject = affecteObject.optJSONObject("DeletedNode");
                                            if (deletedNodeObject != null) {
                                                JSONObject finalFieldsObject = deletedNodeObject.optJSONObject("FinalFields");
                                                if (finalFieldsObject != null) {
                                                    String accountStr = finalFieldsObject.optString("Account");
                                                    if (!StringUtils.isEmpty(accountStr)) {
                                                        JSONObject takerGetsObject = finalFieldsObject.optJSONObject("TakerGets");
                                                        if (takerGetsObject != null) {
                                                            String coinType3 = takerGetsObject.optString("currency");
                                                            if (coinType3.equals(ZXWalletManager.CURRENCY_TYPE_HDT) || coinType3.equals(ZXWalletManager.CURRENCY_TYPE_BTD)) {
                                                                Iterator it2 = ZXWalletManager.this.mReceiveListWalletAddrs.iterator();
                                                                while (true) {
                                                                    if (!it2.hasNext()) {
                                                                        break;
                                                                    }
                                                                    String add = (String) it2.next();
                                                                    if (add.equals(accountStr)) {
                                                                        info2.setTxId(txId);
                                                                        info2.setType(4);
                                                                        info2.setOfferAddr(add);
                                                                        info2.setCurrencyType(coinType3);
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if (!StringUtils.isEmpty(info2.getOfferAddr())) {
                                                ArrayList<String> otherAccounts2 = new ArrayList<>();
                                                JSONObject modifiedNode2 = affecteObject.optJSONObject("ModifiedNode");
                                                if (modifiedNode2 != null) {
                                                    JSONObject finalFields2 = modifiedNode2.optJSONObject("FinalFields");
                                                    if (finalFields2 != null) {
                                                        String otherAccount2 = finalFields2.optString("Account");
                                                        if (!info2.getOfferAddr().equals(otherAccount2) && !StringUtils.isEmpty(otherAccount2)) {
                                                            otherAccounts2.add(otherAccount2);
                                                        }
                                                    }
                                                }
                                                info2.setOtherOfferAddrs(otherAccounts2);
                                                if (ZXWalletManager.this.isBoth) {
                                                    ZXWalletManager.this.mReceiveListCallback.receiveListCallback(0, BuildConfig.FLAVOR, info2);
                                                } else if (info2.getCurrencyType().equals(ZXWalletManager.this.mCurReceiveListCurrencyType)) {
                                                    ZXWalletManager.this.mReceiveListCallback.receiveListCallback(0, BuildConfig.FLAVOR, info2);
                                                }
                                                ZXWalletManager.this.mLastTxId = txId;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public synchronized boolean subscribeAccount(List<String> walletAddrs) {
        boolean z;
        if (!isConnect() || walletAddrs == null || walletAddrs.size() < 1) {
            ZXLogger.e(TAG, "subscribeAccount failed invalid param!");
            z = false;
        } else {
            try {
                int size = walletAddrs.size();
                AccountID[] accountIDs = new AccountID[size];
                for (int i = 0; i < size; i++) {
                    accountIDs[i] = AccountID.fromAddress(walletAddrs.get(i));
                }
                Chainsql.c.getConnection().getClient().sendRequest(Chainsql.c.getConnection().getClient().subscribeAccount(accountIDs));
                mIsSubscribeAccount = true;
                ZXLogger.i(TAG, "subscribeAccount");
                z = true;
            } catch (Exception e) {
                e.printStackTrace();
                z = false;
            }
        }
        return z;
    }

    /* access modifiers changed from: private */
    public synchronized boolean unSubscribeAccount(List<String> walletAddrs) {
        boolean z;
        if (!isConnect() || walletAddrs == null || walletAddrs.size() < 1) {
            ZXLogger.e(TAG, "unSubscribeAccount failed invalid param!");
            z = false;
        } else {
            try {
                int size = walletAddrs.size();
                AccountID[] accountIDs = new AccountID[size];
                for (int i = 0; i < size; i++) {
                    accountIDs[i] = AccountID.fromAddress(walletAddrs.get(i));
                }
                Chainsql.c.getConnection().getClient().sendRequest(Chainsql.c.getConnection().getClient().unsubscribeAccount(accountIDs));
                mIsSubscribeAccount = false;
                ZXLogger.i(TAG, "unSubscribeAccount");
                z = true;
            } catch (Exception e) {
                e.printStackTrace();
                z = false;
            }
        }
        return z;
    }

    private boolean isConnect() {
        Connection connection = Chainsql.c.getConnection();
        if (connection == null) {
            return false;
        }
        Client client = connection.getClient();
        if (client == null || !client.connected) {
            return false;
        }
        return true;
    }
}
