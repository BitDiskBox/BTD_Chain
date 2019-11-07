package com.peersafe.hdtsdk.inner;

import java.util.List;

public class TransferModel {
    private String engine_result;
    private int engine_result_code;
    private String engine_result_message;
    private String ledger_hash;
    private int ledger_index;
    private MetaEntity meta;
    private String status;
    private TransactionEntity transaction;
    private String type;
    private boolean validated;

    public class MetaEntity {
        private List<AffectedNodesEntity> AffectedNodes;
        private int TransactionIndex;
        private String TransactionResult;

        public class AffectedNodesEntity {
            private ModifiedNodeEntity ModifiedNode;

            public class ModifiedNodeEntity {
                private FinalFieldsEntity FinalFields;
                private String LedgerEntryType;
                private String LedgerIndex;
                private PreviousFieldsEntity PreviousFields;
                private String PreviousTxnID;
                private int PreviousTxnLgrSeq;

                public class FinalFieldsEntity {
                    private String Account;
                    private String Balance;
                    private int Flags;
                    private int OwnerCount;
                    private int Sequence;

                    public FinalFieldsEntity() {
                    }

                    public void setAccount(String Account2) {
                        this.Account = Account2;
                    }

                    public void setOwnerCount(int OwnerCount2) {
                        this.OwnerCount = OwnerCount2;
                    }

                    public void setFlags(int Flags2) {
                        this.Flags = Flags2;
                    }

                    public void setSequence(int Sequence2) {
                        this.Sequence = Sequence2;
                    }

                    public void setBalance(String Balance2) {
                        this.Balance = Balance2;
                    }

                    public String getAccount() {
                        return this.Account;
                    }

                    public int getOwnerCount() {
                        return this.OwnerCount;
                    }

                    public int getFlags() {
                        return this.Flags;
                    }

                    public int getSequence() {
                        return this.Sequence;
                    }

                    public String getBalance() {
                        return this.Balance;
                    }
                }

                public class PreviousFieldsEntity {
                    private String Balance;
                    private int Sequence;

                    public PreviousFieldsEntity() {
                    }

                    public void setSequence(int Sequence2) {
                        this.Sequence = Sequence2;
                    }

                    public void setBalance(String Balance2) {
                        this.Balance = Balance2;
                    }

                    public int getSequence() {
                        return this.Sequence;
                    }

                    public String getBalance() {
                        return this.Balance;
                    }
                }

                public ModifiedNodeEntity() {
                }

                public void setLedgerIndex(String LedgerIndex2) {
                    this.LedgerIndex = LedgerIndex2;
                }

                public void setFinalFields(FinalFieldsEntity FinalFields2) {
                    this.FinalFields = FinalFields2;
                }

                public void setPreviousFields(PreviousFieldsEntity PreviousFields2) {
                    this.PreviousFields = PreviousFields2;
                }

                public void setPreviousTxnLgrSeq(int PreviousTxnLgrSeq2) {
                    this.PreviousTxnLgrSeq = PreviousTxnLgrSeq2;
                }

                public void setLedgerEntryType(String LedgerEntryType2) {
                    this.LedgerEntryType = LedgerEntryType2;
                }

                public void setPreviousTxnID(String PreviousTxnID2) {
                    this.PreviousTxnID = PreviousTxnID2;
                }

                public String getLedgerIndex() {
                    return this.LedgerIndex;
                }

                public FinalFieldsEntity getFinalFields() {
                    return this.FinalFields;
                }

                public PreviousFieldsEntity getPreviousFields() {
                    return this.PreviousFields;
                }

                public int getPreviousTxnLgrSeq() {
                    return this.PreviousTxnLgrSeq;
                }

                public String getLedgerEntryType() {
                    return this.LedgerEntryType;
                }

                public String getPreviousTxnID() {
                    return this.PreviousTxnID;
                }
            }

            public AffectedNodesEntity() {
            }

            public void setModifiedNode(ModifiedNodeEntity ModifiedNode2) {
                this.ModifiedNode = ModifiedNode2;
            }

            public ModifiedNodeEntity getModifiedNode() {
                return this.ModifiedNode;
            }
        }

        public MetaEntity() {
        }

        public void setAffectedNodes(List<AffectedNodesEntity> AffectedNodes2) {
            this.AffectedNodes = AffectedNodes2;
        }

        public void setTransactionResult(String TransactionResult2) {
            this.TransactionResult = TransactionResult2;
        }

        public void setTransactionIndex(int TransactionIndex2) {
            this.TransactionIndex = TransactionIndex2;
        }

        public List<AffectedNodesEntity> getAffectedNodes() {
            return this.AffectedNodes;
        }

        public String getTransactionResult() {
            return this.TransactionResult;
        }

        public int getTransactionIndex() {
            return this.TransactionIndex;
        }
    }

    public class TransactionEntity {
        private String Account;
        private AmountEntity Amount;
        private String Destination;
        private String Fee;
        private long Flags;
        private int LastLedgerSequence;
        private SendMaxEntity SendMax;
        private int Sequence;
        private String SigningPubKey;
        private String TransactionType;
        private String TxnSignature;
        private int date;
        private String hash;

        public class AmountEntity {
            private String currency;
            private String issuer;
            private String value;

            public AmountEntity() {
            }

            public void setCurrency(String currency2) {
                this.currency = currency2;
            }

            public void setValue(String value2) {
                this.value = value2;
            }

            public void setIssuer(String issuer2) {
                this.issuer = issuer2;
            }

            public String getCurrency() {
                return this.currency;
            }

            public String getValue() {
                return this.value;
            }

            public String getIssuer() {
                return this.issuer;
            }
        }

        public class SendMaxEntity {
            private String currency;
            private String issuer;
            private String value;

            public SendMaxEntity() {
            }

            public void setCurrency(String currency2) {
                this.currency = currency2;
            }

            public void setValue(String value2) {
                this.value = value2;
            }

            public void setIssuer(String issuer2) {
                this.issuer = issuer2;
            }

            public String getCurrency() {
                return this.currency;
            }

            public String getValue() {
                return this.value;
            }

            public String getIssuer() {
                return this.issuer;
            }
        }

        public TransactionEntity() {
        }

        public void setDate(int date2) {
            this.date = date2;
        }

        public void setAccount(String Account2) {
            this.Account = Account2;
        }

        public void setDestination(String Destination2) {
            this.Destination = Destination2;
        }

        public void setTransactionType(String TransactionType2) {
            this.TransactionType = TransactionType2;
        }

        public void setSigningPubKey(String SigningPubKey2) {
            this.SigningPubKey = SigningPubKey2;
        }

        public void setAmount(AmountEntity Amount2) {
            this.Amount = Amount2;
        }

        public void setFee(String Fee2) {
            this.Fee = Fee2;
        }

        public void setSendMax(SendMaxEntity SendMax2) {
            this.SendMax = SendMax2;
        }

        public void setFlags(long Flags2) {
            this.Flags = Flags2;
        }

        public void setSequence(int Sequence2) {
            this.Sequence = Sequence2;
        }

        public void setLastLedgerSequence(int LastLedgerSequence2) {
            this.LastLedgerSequence = LastLedgerSequence2;
        }

        public void setTxnSignature(String TxnSignature2) {
            this.TxnSignature = TxnSignature2;
        }

        public void setHash(String hash2) {
            this.hash = hash2;
        }

        public int getDate() {
            return this.date;
        }

        public String getAccount() {
            return this.Account;
        }

        public String getDestination() {
            return this.Destination;
        }

        public String getTransactionType() {
            return this.TransactionType;
        }

        public String getSigningPubKey() {
            return this.SigningPubKey;
        }

        public AmountEntity getAmount() {
            return this.Amount;
        }

        public String getFee() {
            return this.Fee;
        }

        public SendMaxEntity getSendMax() {
            return this.SendMax;
        }

        public long getFlags() {
            return this.Flags;
        }

        public int getSequence() {
            return this.Sequence;
        }

        public int getLastLedgerSequence() {
            return this.LastLedgerSequence;
        }

        public String getTxnSignature() {
            return this.TxnSignature;
        }

        public String getHash() {
            return this.hash;
        }
    }

    public void setValidated(boolean validated2) {
        this.validated = validated2;
    }

    public void setLedger_index(int ledger_index2) {
        this.ledger_index = ledger_index2;
    }

    public void setLedger_hash(String ledger_hash2) {
        this.ledger_hash = ledger_hash2;
    }

    public void setMeta(MetaEntity meta2) {
        this.meta = meta2;
    }

    public void setEngine_result_code(int engine_result_code2) {
        this.engine_result_code = engine_result_code2;
    }

    public void setEngine_result(String engine_result2) {
        this.engine_result = engine_result2;
    }

    public void setType(String type2) {
        this.type = type2;
    }

    public void setTransaction(TransactionEntity transaction2) {
        this.transaction = transaction2;
    }

    public void setEngine_result_message(String engine_result_message2) {
        this.engine_result_message = engine_result_message2;
    }

    public void setStatus(String status2) {
        this.status = status2;
    }

    public boolean isValidated() {
        return this.validated;
    }

    public int getLedger_index() {
        return this.ledger_index;
    }

    public String getLedger_hash() {
        return this.ledger_hash;
    }

    public MetaEntity getMeta() {
        return this.meta;
    }

    public int getEngine_result_code() {
        return this.engine_result_code;
    }

    public String getEngine_result() {
        return this.engine_result;
    }

    public String getType() {
        return this.type;
    }

    public TransactionEntity getTransaction() {
        return this.transaction;
    }

    public String getEngine_result_message() {
        return this.engine_result_message;
    }

    public String getStatus() {
        return this.status;
    }
}
