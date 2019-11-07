package com.peersafe.hdtsdk.inner;

import java.util.List;

public class AccountOfferModel {
    private String account;
    private int ledger_current_index;
    private int limit;
    private String marker;
    private List<OffersEntity> offers;
    private boolean validated;

    public class OffersEntity {
        private int flags;
        private String quality;
        private int seq;
        private Taker_getsEntity taker_gets;
        private Taker_paysEntity taker_pays;

        public class Taker_getsEntity {
            private String currency;
            private String issuer;
            private String value;

            public Taker_getsEntity() {
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

        public class Taker_paysEntity {
            private String currency;
            private String issuer;
            private String value;

            public Taker_paysEntity() {
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

        public OffersEntity() {
        }

        public void setFlags(int flags2) {
            this.flags = flags2;
        }

        public void setTaker_gets(Taker_getsEntity taker_gets2) {
            this.taker_gets = taker_gets2;
        }

        public void setSeq(int seq2) {
            this.seq = seq2;
        }

        public void setQuality(String quality2) {
            this.quality = quality2;
        }

        public void setTaker_pays(Taker_paysEntity taker_pays2) {
            this.taker_pays = taker_pays2;
        }

        public int getFlags() {
            return this.flags;
        }

        public Taker_getsEntity getTaker_gets() {
            return this.taker_gets;
        }

        public int getSeq() {
            return this.seq;
        }

        public String getQuality() {
            return this.quality;
        }

        public Taker_paysEntity getTaker_pays() {
            return this.taker_pays;
        }
    }

    public void setLedger_current_index(int ledger_current_index2) {
        this.ledger_current_index = ledger_current_index2;
    }

    public void setOffers(List<OffersEntity> offers2) {
        this.offers = offers2;
    }

    public void setValidated(boolean validated2) {
        this.validated = validated2;
    }

    public void setMarker(String marker2) {
        this.marker = marker2;
    }

    public void setLimit(int limit2) {
        this.limit = limit2;
    }

    public void setAccount(String account2) {
        this.account = account2;
    }

    public int getLedger_current_index() {
        return this.ledger_current_index;
    }

    public List<OffersEntity> getOffers() {
        return this.offers;
    }

    public boolean isValidated() {
        return this.validated;
    }

    public String getMarker() {
        return this.marker;
    }

    public int getLimit() {
        return this.limit;
    }

    public String getAccount() {
        return this.account;
    }
}
