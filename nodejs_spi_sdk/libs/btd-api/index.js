'use strict'

const ChainsqlAPI = new require('chainsql-lib').ChainsqlLibAPI;
const Connection = require('./connect');
const EventManager = require('./eventManager')

const keypairs = require('chainsql-keypairs');
const addressCodec = require('chainsql-address-codec');
const FloatOperation = require('./floatOperation');

const BitRiceAPI = function() {
    this.walletInfo = {};
    this.issuer = {};
    this.bitType = 'HDT';
};

BitRiceAPI.prototype.sdkInit = function(walletInfo,issuer){
    this.walletInfo = walletInfo;
    this.issuer = issuer;
}

BitRiceAPI.prototype.setBitType = function(type){
    if(type === 'HDT' || type === 'BTD'){
        this.bitType = type;
    }
}

BitRiceAPI.prototype.connect = function(url){
    let ra = new ChainsqlAPI({
        server: url
    });
    let con = new Connection();
    con.api = ra;
    this.api = ra;
    this.connection = con;
    this.event = new EventManager(this);

    return new Promise(function(resolve,reject){
        //connect to chainsql-node
        con.connect().then(function(data) {
            resolve(data)
        }).catch(function(err) {
            reject(err);
        });
    })
}

BitRiceAPI.prototype.subscribeAccountTx = function(address,cb){
    //account tx
    this.event.subscribeAccountTx(this.walletInfo.address,function(err,data){
        cb(err,data);
    })
}

BitRiceAPI.prototype.subscribeAccountOfferTx = function(address,cb){
    let self = this;
    return new Promise(function(resolve, reject){
        self.event.subscribeAccountOfferTx(address || self.walletInfo.address,async function(err,data){
            if(err){
                reject(err)
            }
            try{
                let hash = data.txId;
                let txDtail = await self.getOfferTxDetail(hash);
                console.log(JSON.stringify(txDtail));
            }catch(err){
                reject(err)
            }
        })
    })
}

BitRiceAPI.prototype.sdkClose = function(){
    this.connection.disconnect();
}

BitRiceAPI.prototype.setTransferFee = function(fee){
    var chainsql = this;
    return new Promise(function(resolve,reject){
        const address = chainsql.issuer.address;
        const settings = {
            "transferFeeMin": fee,
            "transferFeeMax": fee,
        };
        // const settings = {
        //     "transferRate":1.1
        // };
        var hash;
        var instructions = {
            maxLedgerVersionOffset : 8
        }
        chainsql.api.prepareSettings(address, settings,instructions)
        .then(function(prepared){
            try{
                let signedRet = chainsql.api.sign(prepared.txJSON, chainsql.issuer.secret);
                hash = signedRet.id;
                return chainsql.api.submit(signedRet.signedTransaction);
            }catch(error){
                reject(error);
            }
        })
        .then(function(data){
            if (data.resultCode === 'tesSUCCESS') {
                data.tx_hash = hash;
                resolve(data);
            } else {
                reject(data);
            }
        }).catch(function(err){
            reject(err);
        })
    })
}

BitRiceAPI.prototype.getTransferFee = function(){
    var chainsql = this;
    return new Promise(function(resolve,reject){
        chainsql.api.getAccountInfo(chainsql.issuer.address)
        .then(function(data){
            if(data.transferFeeMin){
                resolve(data.transferFeeMin);
            }else{
                resolve(0);
            }
        })
        .catch(function(err){
            reject(err);
        })
    });
}


BitRiceAPI.prototype.generateWallet = function(){
    var account = null;
	var keypair = null;
	let ripple = new ChainsqlAPI();
	if(arguments.length == 0){
		account = ripple.generateAddress();
		keypair = keypairs.deriveKeypair(account.secret);
	}else{
		keypair = keypairs.deriveKeypair(arguments[0]);
		account = {
			secret : arguments[0],
			address : keypairs.deriveAddress(keypair.publicKey)
		}
	}
	var opt = {
		version:35
	} 
	var buf = new Buffer(keypair.publicKey,'hex');
    account.publicKey = addressCodec.encode(buf, opt);
    
    return account;
}

// trust bitrice
BitRiceAPI.prototype.trustIssueCurrency = function(){
    let trustline = {
        currency: this.bitType,
        counterparty: this.issuer.address,
        limit: '100000000',
        qualityIn: 1,
        qualityOut: 1,
        frozen: false,
        memos: [{
          type: 'TRUSTLINE',
          format: '',
          data: ''
        }]
    };

    console.log(trustline)
    var chainsql = this;
    return new Promise(function(resolve,reject){
        var hash;
        var instructions = {
            maxLedgerVersionOffset : 8
        }
        chainsql.api.prepareTrustline(chainsql.walletInfo.address, trustline,instructions)
        .then(function(prepared){
            try{
                let signedRet = chainsql.api.sign(prepared.txJSON, chainsql.walletInfo.secret);
                hash = signedRet.id;
                return chainsql.api.submit(signedRet.signedTransaction);
            }catch(error){
                reject(error);
            }
        })
        .then(function(data){
            if (data.resultCode === 'tesSUCCESS' || data.resultCode === 'terQUEUED') {
                data.tx_hash = hash;
                resolve(data);
            } else {
                reject(data);
            }
        })
        .catch(function(error){
            reject(error);
        });
    });
}

function getBTRBalance(self,address){
    return new Promise(function(resolve, reject) {
        self.api.getBalances(address).then(function(data){
            var balance = null;
            for(var i=0; i<data.length; i++){
                if(data[i].currency == self.bitType){
                    balance=data[i].value
                }
            }
            if(balance)
                resolve(self.bitType + ':' + balance);
            else
                reject(self.bitType + " balance not found");
        }).catch(function(err){
            reject(err);
        });
    });
}
// get systemcurrency balance
BitRiceAPI.prototype.getSysCoinBalance = function(){
    var self = this;
    return new Promise(function(resolve, reject) {
        self.api.getAccountInfo(self.walletInfo.address).then(function(data){
            resolve(data.zxcBalance);
        }).catch(function(err){
            reject(err);
        });
    });
}

// get bitrice balance
BitRiceAPI.prototype.getIssueCurrencyBalance = function(){
    return getBTRBalance(this,this.walletInfo.address);
}

BitRiceAPI.prototype.getAccountCurrencyBalance = function(walletAddr){
    return getBTRBalance(this,walletAddr);
}

BitRiceAPI.prototype.getIssueCurrencyTxDetail = function(limit,start){
    var opts = {
        types:['payment']
    }
    // opts.earliestFirst = true;
    if(start)
        opts.start = start;
    if(limit)
        opts.limit = limit;

    // return getAccountTxs(this,opts,this.walletInfo.address);

    return getAccountTxsRecur(this,opts,this.walletInfo.address);

}

function formatResponse(data,address){
    var sendMax = parseFloat(data.specification.source.maxAmount.value);
    var amount = parseFloat(data.specification.destination.amount.value);
    var fee = FloatOperation.accSub(sendMax , amount);
    var ret = {
        txId:data.id,
        txType:data.address === address ? 3 : 2,
        fromAddr : data.address,
        toAddr : data.specification.destination.address,
        amount : data.specification.destination.amount.value,
        createTime : data.outcome.timestamp,
        transferFee : fee,
        currency: data.currency
    }
    // console.log(data);
    return ret;
}
function getAccountTxsRecur(self,opts,address){
    var final = [];
    return self.api.getTransactions(address,opts).then(function(data){
        for(var i=0; i<data.length; i++){
            if((data[i].specification.destination.amount.currency === 'HDT' || data[i].specification.destination.amount.currency === 'BTD')
                && data[i].outcome.result === 'tesSUCCESS'){
                 data[i].currency = data[i].specification.destination.amount.currency
                 final.push(formatResponse(data[i],address));
            }
        }
        
        if(opts.limit){
            var remaining = opts.limit - final.length;
            if (remaining > 0 && data.length == opts.limit) {
                opts.start = data[data.length - 1].id;
                return getAccountTxsRecur(self, opts, address).then(function (results) {
                    for(var i=0; i<results.length; i++){
                        if(final.length < opts.limit){
                            final.push(results[i]);
                        }
                    }
                    return final;
                });
            }
        }
        
        return final.slice(0, opts.limit);
      });
}

BitRiceAPI.prototype.getAccountIssueCurrencyTxDetail = function(limit,start,walletAddr){
    var opts = {
        types:['payment']
    }
    if(start)
        opts.start = start;
    if(limit)
        opts.limit = limit;

    return getAccountTxsRecur(this,opts,walletAddr);
}

BitRiceAPI.prototype.transferCurrency = function(toWalletAddr,amount,memos){
    console.log(this.bitType)
    var self = this;
	return new Promise(function(resolve, reject) {
		preparePayment(self, toWalletAddr, amount,self.bitType, self.issuer.address,resolve, reject,memos);
	});
}

function preparePayment(ChainSQL, account, amount,currency, issuer,resolve, reject,memos) {
    let address = ChainSQL.walletInfo.address;
    let secret = ChainSQL.walletInfo.secret;
    let payment = {
        source: {
            address: address, // root account
            maxAmount: {
                value: amount.toString(),
                currency: currency
            }
        },
        destination: {
            address: account,
            amount: {
                value: amount.toString(),
                currency: currency
            }
        },
        memos: memos
    };
    if(issuer){
        payment.source.maxAmount.counterparty = issuer;
        payment.destination.amount.counterparty = issuer;
    }
	
	try {
		var hash;
        ChainSQL.api.getAccountInfo(ChainSQL.issuer.address)
        .then(function(accountInfo){
            if(issuer && accountInfo.transferFeeMin){
                payment.source.maxAmount.value = (FloatOperation.accAdd(parseFloat(amount) , parseFloat(accountInfo.transferFeeMin))).toString();
                console.log(payment.source.maxAmount);
            }
            var instructions = {
                maxLedgerVersionOffset : 8
            }
            return ChainSQL.api.preparePayment(payment.source.address, payment,instructions);
        })
		.then(function (data) {
			try {
				let signedRet = ChainSQL.api.sign(data.txJSON, secret);
				hash = signedRet.id;
				return ChainSQL.api.submit(signedRet.signedTransaction);
			} catch (error) {
				//console.log('sign preparePayment failure.', JSON.stringify(error));
				reject(error);
			}
		})
		.then(function(data) {
			if (data.resultCode === 'tesSUCCESS' || data.resultCode === 'terQUEUED') {
				//paymentSetting(ChainSQL, account, resolve, reject);
				data.tx_hash = hash;
				resolve(data);
			} else {
				reject(data);
			}
		})
		.catch(function(error) {
			reject(error);
		});		
	}
	catch (error) {
		reject(error);
	}
}

BitRiceAPI.prototype.activateWallet = function(walletAddr){
    var self = this;
	return new Promise(function(resolve, reject) {
		preparePayment(self, walletAddr,40,'ZXC',null, resolve, reject);
	});
}

BitRiceAPI.prototype.sleep = function(time){
    return new Promise(function(resolve, reject) {
		setTimeout(function() {
			resolve(null);
		}, time);
	})
}

BitRiceAPI.prototype.verifyTransaction = function(hash){
    return verifyTransaction(this,hash,{});
}

/* Verify a transaction is in a validated ZXC Ledger version */
function verifyTransaction(self,hash, options) {
    console.log('Verifing Transaction');
    return self.api.getTransaction(hash, options).then(data => {
      console.log('Final Result: ', data.outcome.result);
      console.log('Validated in Ledger: ', data.outcome.ledgerVersion);
      console.log('Sequence: ', data.sequence);
      return data.outcome.result === 'tesSUCCESS';
    }).catch(error => {
      /* If transaction not in latest validated ledger,
         try again until max ledger hit */
      if (error instanceof self.api.errors.PendingLedgerVersionError ||
          error instanceof self.api.errors.NotFoundError ||
          error instanceof self.api.errors.MissingLedgerHistoryError
        ) {
        return new Promise((resolve, reject) => {
          setTimeout(() => verifyTransaction(self,hash, options)
          .then(resolve, reject), 1000);
        });
      }
      return error;
    });
}

BitRiceAPI.prototype.getTxDetail = function(hash) {
    var self = this;
    return new Promise(function(resolve, reject) {
        self.api.getTransaction(hash).then(function(data) {
            var sendMax = parseFloat(data.specification.source.maxAmount.value);
            var amount = parseFloat(data.specification.destination.amount.value);
            var fee = FloatOperation.accSub(sendMax , amount);
            console.log(JSON.stringify(data))
            var ret = {
                txId:data.id,
                fromAddr : data.address,
                toAddr : data.specification.destination.address,
                amount : data.specification.destination.amount.value,
                transferFee:fee,
                createTime : data.outcome.timestamp,
                memos: data.specification.memos,
                currency: data.outcome.deliveredAmount.currency
            }
            resolve(ret);
		}).catch(function(err) {
			reject(err);
		});
    });
}

exports.BitRiceAPI = BitRiceAPI;