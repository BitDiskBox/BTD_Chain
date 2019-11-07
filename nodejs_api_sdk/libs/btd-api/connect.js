'use strict'

const Connection = function() {

};

Connection.prototype.connect = function() {
  let that = this;
  return new Promise(function(resolve, reject) {
    that.api.connect().then(function() {
      resolve(that);
    }).catch(function(e) {
      reject(e);
    });
  })
}

Connection.prototype.disconnect = function() {
  this.api.disconnect();
}

module.exports = Connection;