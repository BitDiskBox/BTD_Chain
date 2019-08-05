const express = require('express');
const router = express.Router();
const config = require('config');
const issuer = config.get('issuer');
const url = config.get('chain-url');
const axios = require('axios');
const baseUrl = config.get('chain-base-url');
const activationAccount = config.get('activation-account')

const HdtAPI = require('../libs/btd-api').BitRiceAPI;
let c;
let accountC;
let trustC;


router.get('/', function(req, res, nex){
  res.render('index')
});


let activationP = [];
let activating = false;
let acti = (transferObj) => {
  return new Promise((resolve, reject) => {
    transferObj.cb = (err, data) => {
      if(err){
        reject(err);
      }
      resolve(data)
    }
    activationP.push(transferObj);
    actidisQueue();
  });
}
let actidisQueue = async () => {
  console.log('activating---->', activating)
  if(activating) return;
  let transferObj = activationP.shift();
  if(!transferObj) return;
  activating = true;
  let cb = transferObj.cb || (() => {});
  try{
    let transferRet = await accountC.activateWallet(transferObj.address)
    cb(null, transferRet);
  }catch(err){
    activating = false;
    console.log('errrrrrrrrr-----')
    cb(err);
  }
  activating = false;
  actidisQueue();
}


let trustP = [];
let trusting = false;
let trust = (transferObj) => {
  return new Promise((resolve, reject) => {
    transferObj.cb = (err, data) => {
      if(err){
        reject(err);
      }
      resolve(data)
    }
    trustP.push(transferObj);
    trustdisQueue();
  });
}
let trustdisQueue = async () => {
  console.log('trusting---->', trusting)
  if(trusting) return;
  let transferObj = trustP.shift();
  if(!transferObj) return;
  trusting = true;
  let cb = transferObj.cb || (() => {});
  try{
    trustC.setBitType('BTD');
    trustC.sdkInit(transferObj, issuer);
    let transferRet = await trustC.trustIssueCurrency(transferObj.address)
    // let ret = await trustC.verifyTransaction(transferRet.tx_hash);
    // if(!ret){
    //   throw new Error('信任btd失败')
    // }
    // trustC.setBitType('HDT');
    // transferRet = await trustC.trustIssueCurrency(transferObj.address)
    cb(null, transferRet);
  }catch(err){
    trusting = false;
    console.log('errrrrrrrrr-----')
    cb(err);
  }
  trusting = false;
  trustdisQueue();
}


/**
 * 新建账号
 */
router.post('/new-account', async function(req, res, next) {
  try{
    if(!accountC){
      accountC = new HdtAPI();
      await accountC.connect(url);
      accountC.sdkInit(activationAccount, issuer);
    }
    if(!trustC){
      trustC = new HdtAPI();
      await trustC.connect(url);
      //trustC.sdkInit(activationAccount, issuer);
    }
    let newAccount = accountC.generateWallet();
  
    //激活
    let ret = await acti(newAccount);
    ret = await accountC.verifyTransaction(ret.tx_hash)
    if(!ret){
      throw new Error('共识失败')
    }
    //信任
    ret = await trust(newAccount);
    ret = await accountC.verifyTransaction(ret.tx_hash);
    if(!ret){
      throw new Error('共识失败')
    }
    res.json(newAccount);
  }catch(e){
    console.log(e)
    res.status(500);
    res.send();
  }


});


let disposing = false;
let transferQueue = [];
let transfer = (transferObj) => {
  return new Promise((resolve, reject) => {
    transferObj.cb = (err, data) => {
      if(err){
        reject(err);
      }
      resolve(data)
    }
    transferQueue.push(transferObj);
    disQueue();
  });
}
//排队处理转账 防止同账号并发链拒绝操作问题
let disQueue = async () => {
  if(disposing) return;
  console.log('disposing---->', disposing)
  let transferObj = transferQueue.shift();
  if(!transferObj) return;
  disposing = true;
  let cb = transferObj.cb || (() => {});
  c.sdkInit(transferObj.user, issuer);
  //设置币种
  c.setBitType(transferObj.currency);
  try{
    let transferRet = await c.transferCurrency(transferObj.address, transferObj.amount, [
      {
          data: transferObj.msg,
          type: transferObj.msgType
      }
    ])
    cb(null, transferRet);
  }catch(err){
    disposing = false;
    console.log('errrrrrrrrr-----')
    cb(err);
  }
  disposing = false;
  disQueue();
}
/** 
 * 转账
 */
router.post('/transfer', async function(req, res, next) {
  // { 
  //   user, //转账用户
  //   address, //接受转账用户
  //   amount, //转载数
  //   currency,
  //   msg, //信息
  //   msgType //信息类型
  // }
  let transferObj = req.body;
  let { currency } = transferObj;
  
  try{
    if(!(currency === 'HDT' || currency === 'BTD')){
      throw '币种不正确'
    }
    if(!c){
      c = new HdtAPI();
      await c.connect(url);
    }

    //存到队列
    let transferRet = await transfer(transferObj);
    
    console.log(transferRet);
    //等待共识
    let v = await c.verifyTransaction(transferRet.tx_hash);
    res.send({
      code: v?1:-1,
      data: {
        tx_hash: transferRet.tx_hash
      },
      info: v?'操作成功':'转账失败'
    });
  }catch(e){
    res.send({
      code: -1,
      data: {},
      info: e.message || JSON.stringify(e)
    });
  }
});

let hexToStr = (hex) => {
  return new Buffer(hex + '', 'hex').toString('utf-8')
}

let getMsg = (msg, isTpye) => {
  let result = ''
  try{
    if(isTpye){
      result = hexToStr(msg[0].Memo.MemoType)
    }else{
      result = hexToStr(msg[0].Memo.MemoData) 
    }
  }catch(e){
    console.log(e)
  }
  return result;
}

let forMatData = (rows) => {
  let result = []
  rows.map((row, index) => {
    if(row.tx.Amount && row.tx.Amount.currency){
      result.push({
        fromAddr: row.tx.Account,
        toAddr: row.tx.Destination,
        hash: row.tx.hash,
        currency: row.tx.Amount.currency,
        amount: row.tx.Amount.value,
        msg: {
          msg: getMsg(row.tx.Memos),
          msgTpye: getMsg(row.tx.Memos, true)
        }
      })
    }
  })
  return result
} 

/**
 * 查看交易记录
 */
router.post('/get-transaction-list', async function(req, res, next) {
  let {
    ledger,
    seq,
    address,
    pageSize
  } = req.body;
  pageSize = pageSize || 20
  let marker = ''
  if(ledger){
    marker = {
      ledger,
      seq
    }
  }
  let params = {
    method: 'account_tx',
    params: [
      {
        account: address,
        binary: false,
        forward: false,
        ledger_index_max: -1,
        ledger_index_min: -1,
        limit: pageSize,
        marker: marker
      }
    ]
  };
  let txs;
  try {
    console.log('从链上获取交易详情信息')
    //从链上获取交易详情信息
    await axios.post(baseUrl, params).then(function(res) {
      //console.log(res)
      if (res && res.data && res.data.result.status == 'error') {
        throw '查询账号出错';
      } else {
        txs = {
          code: 1,
          data: {
            rows: forMatData(res.data.result.transactions),
            marker: res.data.result.marker || undefined
          },
          info: '操作成功'
        };
        txs = JSON.parse(JSON.stringify(txs));
      }
    });
  } catch (e) {
    res.send({
      code: -1,
      data: {},
      info: e.message || JSON.stringify(e)
    });
  }
  res.json(txs);
});


module.exports = router;
