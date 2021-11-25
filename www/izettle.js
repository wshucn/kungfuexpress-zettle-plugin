var execAsPromise = function (command, args) {
  if (args === void 0) {
    args = [];
  }
  return new Promise(function (resolve, reject) {
    cordova.exec(resolve, reject, 'iZettle', command, args);
  });
};

function iZettle() {
}

iZettle.prototype.initialize = function (clientId, callbackURL) {
  return execAsPromise('initialize', [clientId, callbackURL]);
};

iZettle.prototype.charge = function (amount, reference) {
  return execAsPromise('charge', [amount, reference]);
};

iZettle.prototype.refund = function (amount, reference, refundReference) {
  return execAsPromise('refund', [amount, reference, refundReference]);
};

iZettle.prototype.retrievePaymentInfo = function (reference) {
  return execAsPromise('retrievePaymentInfo', [reference]);
};

iZettle.prototype.presentSettings = function () {
  return execAsPromise('presentSettings');
};

iZettle.prototype.enforceAccount = function (email, clientId, callbackURL) {
  return execAsPromise('enforceAccount', [email, clientId, callbackURL]);
};

iZettle.prototype.logout = function () {
  return execAsPromise('logout');
};

iZettle.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }
  window.plugins.iZettle = new iZettle();
  return window.plugins.iZettle;
};

cordova.addConstructor(iZettle.install);
