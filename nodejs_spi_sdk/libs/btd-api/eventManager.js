'use strict'


function EventManager(chainsql) {
  this.connect = chainsql.connection.api.connection;
  this.chainsql = chainsql;
  this.onMessage = false;
  this.cache = {};
};

EventManager.prototype.subscribeAccountOfferTx = function(account, cb){
  if((typeof cb) != 'function'){
    throw Error("Please supply a callback function!");
  }

  var that = this;
  var message = {
    "id": "Example subscribe to XRP/GateHub USD order book",
    "command": "subscribe",
    "books": [
        {
            "taker_pays": {
                "currency": "BTD",
                "issuer": "zPCNUvT6fsCA97nMSfi6ZMrT2uxDivCDek"
            },
            "taker_gets": {
                "currency": "HDT",
                "issuer": "zPCNUvT6fsCA97nMSfi6ZMrT2uxDivCDek"
            },
            "snapshot": true
        }
    ]
  }
  if(!that.message){
    _onOfferMessage(that, cb);
    that.onMessage = true;
  }
  var promise = that.connect.request(message);
  return promise;

}

EventManager.prototype.subscribeAccountTx = function(account,cb){
  if((typeof cb) != 'function'){
    throw Error("Please supply a callback function!");
  }
  var that = this;
  var message = {
    "command":"subscribe",
    "accounts":[account]
  }

  if(!that.message){
    _onMessage(that);
    that.onMessage = true;
  }
  var promise = that.connect.request(message);
  that.cache[account] = cb;
  return promise;
}

function _onMessage(that) {
  that.connect._ws.on('message', function(data) {
    var data = JSON.parse(data);
    if(data.type == 'transaction' && data.transaction.TransactionType == 'Payment'){
      var transaction = data.transaction;
      if(transaction.Amount && (transaction.Amount.currency == 'BTD' || transaction.Amount.currency == 'HDT')){
          //only those who send money to wallet
          var key = transaction.Destination;
          if(that.cache.hasOwnProperty(key)){
            var ret = {
              type:2,
              fromAddr:transaction.Account,
              toAddr:transaction.Destination,
              amount:transaction.Amount.value,
              txId:transaction.hash
            }
            that.cache[key](null,ret);
          }
      }
    }
  });
}
function _onOfferMessage(that, cb) {
  that.connect._ws.on('message', function(data) {
    var data = JSON.parse(data);
    console.log(data)
    if(data.type == 'transaction' && data.transaction.TransactionType == 'OfferCreate'){
      var transaction = data.transaction;
      var ret = {
        type: 'OfferCreate',
        address:transaction.Account,
        txId:transaction.hash
      }
      cb && cb(null,ret);
    }
  });
}

module.exports = EventManager;