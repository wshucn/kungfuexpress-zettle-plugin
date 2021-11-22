function iZettle() {}

var PLUGIN_NAME = "iZettle";

iZettle.prototype.initialize = function (clientId, callbackURL, success, error) {
  cordova.exec(success, error, PLUGIN_NAME, "initialize", [clientId, callbackURL]);
  };

  iZettle.prototype.charge = function (amount, reference, success, error) {
    cordova.exec(success, error, PLUGIN_NAME, "charge", [amount, reference]);
  };

iZettle.prototype.refund = function (amount, reference, refundReference, success, error) {
  cordova.exec(success, error, PLUGIN_NAME, "refund", [amount, reference, refundReference]);
  };

iZettle.prototype.retrievePaymentInfo = function (reference, success, error) {
  cordova.exec(success, error, PLUGIN_NAME, "retrievePaymentInfo", [reference]);
  };

iZettle.prototype.presentSettings = function (success, error) {
  cordova.exec(success, error, PLUGIN_NAME, "presentSettings", null);
  };

iZettle.prototype.enforceAccount = function (email, clientId, callbackURL, success, error) {
  cordova.exec(success, error, PLUGIN_NAME, "enforceAccount", [email, clientId, callbackURL]);
  };

iZettle.prototype.logout = function (success, error) {
  cordova.exec(success, error, PLUGIN_NAME, "logout", null);
  };

iZettle.install = function() {
  if (!window.plugins) {
    window.plugins = {};
  }
  window.plugins.iZettle = new iZettle();
  return window.plugins.iZettle;
};

cordova.addConstructor(iZettle.install);
