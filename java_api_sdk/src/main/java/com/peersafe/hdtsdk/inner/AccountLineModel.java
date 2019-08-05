package com.peersafe.hdtsdk.inner;

import java.util.List;

public class AccountLineModel {
    private String account;
    private int ledger_current_index;
    private List<LinesBean> lines;
    private boolean validated;

    public static class LinesBean {
        private String account;
        private String balance;
        private String currency;
        private boolean freeze_peer;
        private String limit;
        private String limit_peer;
        private List<MemosBean> memos;
        private boolean no_ripple;
        private boolean no_ripple_peer;
        private int quality_in;
        private int quality_out;

        public static class MemosBean {
            private MemoBean Memo;

            public static class MemoBean {
                private String MemoData;
                private String MemoType;

                public String getMemoData() {
                    return this.MemoData;
                }

                public void setMemoData(String MemoData2) {
                    this.MemoData = MemoData2;
                }

                public String getMemoType() {
                    return this.MemoType;
                }

                public void setMemoType(String MemoType2) {
                    this.MemoType = MemoType2;
                }
            }

            public MemoBean getMemo() {
                return this.Memo;
            }

            public void setMemo(MemoBean Memo2) {
                this.Memo = Memo2;
            }
        }

        public String getAccount() {
            return this.account;
        }

        public void setAccount(String account2) {
            this.account = account2;
        }

        public String getBalance() {
            return this.balance;
        }

        public void setBalance(String balance2) {
            this.balance = balance2;
        }

        public String getCurrency() {
            return this.currency;
        }

        public void setCurrency(String currency2) {
            this.currency = currency2;
        }

        public String getLimit() {
            return this.limit;
        }

        public void setLimit(String limit2) {
            this.limit = limit2;
        }

        public String getLimit_peer() {
            return this.limit_peer;
        }

        public void setLimit_peer(String limit_peer2) {
            this.limit_peer = limit_peer2;
        }

        public boolean isNo_ripple() {
            return this.no_ripple;
        }

        public void setNo_ripple(boolean no_ripple2) {
            this.no_ripple = no_ripple2;
        }

        public boolean isNo_ripple_peer() {
            return this.no_ripple_peer;
        }

        public void setNo_ripple_peer(boolean no_ripple_peer2) {
            this.no_ripple_peer = no_ripple_peer2;
        }

        public boolean isFreezePeer() {
            return this.freeze_peer;
        }

        public void setFreezePeer(boolean freeze_peer2) {
            this.freeze_peer = freeze_peer2;
        }

        public int getQuality_in() {
            return this.quality_in;
        }

        public void setQuality_in(int quality_in2) {
            this.quality_in = quality_in2;
        }

        public int getQuality_out() {
            return this.quality_out;
        }

        public void setQuality_out(int quality_out2) {
            this.quality_out = quality_out2;
        }

        public List<MemosBean> getMemos() {
            return this.memos;
        }

        public void setMemos(List<MemosBean> memos2) {
            this.memos = memos2;
        }
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account2) {
        this.account = account2;
    }

    public int getLedger_current_index() {
        return this.ledger_current_index;
    }

    public void setLedger_current_index(int ledger_current_index2) {
        this.ledger_current_index = ledger_current_index2;
    }

    public boolean isValidated() {
        return this.validated;
    }

    public void setValidated(boolean validated2) {
        this.validated = validated2;
    }

    public List<LinesBean> getLines() {
        return this.lines;
    }

    public void setLines(List<LinesBean> lines2) {
        this.lines = lines2;
    }
}
