var exec = require('cordova/exec');

var PLUGIN_NAME = "iZettle";

module.exports = {
  initialize: function (clientId, callbackURL, success, error) {
    exec(success, error, PLUGIN_NAME, "initialize", [clientId, callbackURL]);
  },

  charge: function (amount, reference, success, error) {
    exec(success, error, PLUGIN_NAME, "charge", [amount, reference]);
  },

  refund: function (amount, reference, refundReference, success, error) {
    exec(success, error, PLUGIN_NAME, "refund", [amount, reference, refundReference]);
  },

  retrievePaymentInfo: function (reference, success, error) {
    exec(success, error, PLUGIN_NAME, "retrievePaymentInfo", [reference]);
  },

  presentSettings: function (success, error) {
    exec(success, error, PLUGIN_NAME, "presentSettings", null);
  },

  enforceAccount: function (email, clientId, callbackURL, success, error) {
    exec(success, error, PLUGIN_NAME, "enforceAccount", [email, clientId, callbackURL]);
  },

  logout: function (success, error) {
    exec(success, error, PLUGIN_NAME, "logout", null);
  },
};
