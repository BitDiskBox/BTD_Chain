package com.peersafe.hdtsdk.inner;

public class AccountInfoModel {
    private AccountDataBean account_data;
    private int ledger_current_index;
    private QueueDataBean queue_data;
    private boolean validated;

    public static class AccountDataBean {
        private String Account;
        private String Balance;
        private int Flags;
        private String LedgerEntryType;
        private int OwnerCount;
        private String PreviousTxnID;
        private int PreviousTxnLgrSeq;
        private int Sequence;
        private String TransferFeeMin;
        private long TransferRate;
        private String index;

        public String getAccount() {
            return this.Account;
        }

        public void setAccount(String Account2) {
            this.Account = Account2;
        }

        public String getBalance() {
            return this.Balance;
        }

        public void setBalance(String Balance2) {
            this.Balance = Balance2;
        }

        public int getFlags() {
            return this.Flags;
        }

        public void setFlags(int Flags2) {
            this.Flags = Flags2;
        }

        public String getLedgerEntryType() {
            return this.LedgerEntryType;
        }

        public void setLedgerEntryType(String LedgerEntryType2) {
            this.LedgerEntryType = LedgerEntryType2;
        }

        public int getOwnerCount() {
            return this.OwnerCount;
        }

        public void setOwnerCount(int OwnerCount2) {
            this.OwnerCount = OwnerCount2;
        }

        public String getPreviousTxnID() {
            return this.PreviousTxnID;
        }

        public void setPreviousTxnID(String PreviousTxnID2) {
            this.PreviousTxnID = PreviousTxnID2;
        }

        public int getPreviousTxnLgrSeq() {
            return this.PreviousTxnLgrSeq;
        }

        public void setPreviousTxnLgrSeq(int PreviousTxnLgrSeq2) {
            this.PreviousTxnLgrSeq = PreviousTxnLgrSeq2;
        }

        public int getSequence() {
            return this.Sequence;
        }

        public void setSequence(int Sequence2) {
            this.Sequence = Sequence2;
        }

        public long getTransferRate() {
            return this.TransferRate;
        }

        public void setTransferRate(int TransferRate2) {
            this.TransferRate = (long) TransferRate2;
        }

        public String getTransferFeeMin() {
            return this.TransferFeeMin;
        }

        public void setTransferFeeMin(String TransferFeeMin2) {
            this.TransferFeeMin = TransferFeeMin2;
        }

        public String getIndex() {
            return this.index;
        }

        public void setIndex(String index2) {
            this.index = index2;
        }
    }

    public static class QueueDataBean {
        private int txn_count;

        public int getTxn_count() {
            return this.txn_count;
        }

        public void setTxn_count(int txn_count2) {
            this.txn_count = txn_count2;
        }
    }

    public AccountDataBean getAccount_data() {
        return this.account_data;
    }

    public void setAccount_data(AccountDataBean account_data2) {
        this.account_data = account_data2;
    }

    public int getLedger_current_index() {
        return this.ledger_current_index;
    }

    public void setLedger_current_index(int ledger_current_index2) {
        this.ledger_current_index = ledger_current_index2;
    }

    public QueueDataBean getQueue_data() {
        return this.queue_data;
    }

    public void setQueue_data(QueueDataBean queue_data2) {
        this.queue_data = queue_data2;
    }

    public boolean isValidated() {
        return this.validated;
    }

    public void setValidated(boolean validated2) {
        this.validated = validated2;
    }
}
