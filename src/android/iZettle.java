package com.kungfuexpress.restaurant.user;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.izettle.payments.android.payment.TransactionReference;
import com.izettle.payments.android.payment.refunds.CardPaymentPayload;
import com.izettle.payments.android.payment.refunds.RefundsManager;
import com.izettle.payments.android.payment.refunds.RetrieveCardPaymentFailureReason;
import com.izettle.payments.android.sdk.IZettleSDK;
import com.izettle.payments.android.ui.SdkLifecycle;
import com.izettle.payments.android.ui.payment.CardPaymentActivity;
import com.izettle.payments.android.ui.payment.CardPaymentResult;
import com.izettle.payments.android.ui.readers.CardReadersActivity;
import com.izettle.payments.android.ui.refunds.RefundResult;
import com.izettle.payments.android.ui.refunds.RefundsActivity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigDecimal;

public class iZettle extends CordovaPlugin {

//    private static final String githubAccessToken = "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";
//    private static final String clientId = "26ae8eed-6ad1-4be4-a2f2-61b066877efa";
//    private static final String redirectUrlScheme = "kungfu-express";
//    private static final String redirectUrlHost = "login.callback";

    private static final String FUNCTION_INIT = "initialize";
    private static final String FUNCTION_CHARGE = "charge";
    private static final String FUNCTION_REFUND = "refund";
    private static final String FUNCTION_RETRIEVE_PAYMENTINFO = "retrievePaymentInfo";
    private static final String FUNCTION_PRESENT_SETTING = "presentSettings";
    private static final String FUNCTION_ENFORCE_ACCOUNT = "enforceAccount";
    private static final String FUNCTION_LOGOUT = "logout";

    private static int REQUEST_CODE_PAYMENT = 1001;
    private static int REQUEST_CODE_REFUND = 1002;

    private CallbackContext callbackContext;


    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d("stone", "execute function is " + action);
        this.callbackContext = callbackContext;
        switch (action) {
            case FUNCTION_INIT:
                try {
                    String clientId = args.getString(0);
                    String callbackUrl = args.getString(1);
                    this.initIZettleSDK(clientId, callbackUrl);
                    callbackContext.success();
                } catch (Exception ex) {
                    callbackContext.error(Log.getStackTraceString(ex));
                }
                return true;
            case FUNCTION_CHARGE:
                try {
                    double amount = Double.parseDouble(args.getString(0));
                    String refId = args.getString(1);
                    this.charge(amount, refId);
                } catch (Exception ex) {
                    callbackContext.error(Log.getStackTraceString(ex));
                }
                return true;
            case FUNCTION_REFUND:
                try {
                    double amount = Double.parseDouble(args.getString(0));
                    String refId = args.getString(1);
                    String refundRefId = args.getString(2);
                    this.refund(amount, refId, refundRefId);
                } catch (Exception ex) {
                    callbackContext.error(Log.getStackTraceString(ex));
                }
                return true;
            case FUNCTION_PRESENT_SETTING:
                try {
                    this.presentSetting();
                } catch (Exception ex) {
                    callbackContext.error(Log.getStackTraceString(ex));
                }
                return true;
            case FUNCTION_LOGOUT:
                try {
                    this.logout();
                } catch (Exception ex) {
                    callbackContext.error(Log.getStackTraceString(ex));
                }
                return true;
            default:
                return false;
        }
    }


    public void initIZettleSDK(String clientId, String callbackUrl) {
        Log.d("stone", "initIZettleSDK");
        IZettleSDK.Instance.init(cordova.getActivity(), clientId, callbackUrl);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new SdkLifecycle(IZettleSDK.Instance));
    }

    // todo double to long
    public void charge(double amount, String referenceId) {
        Log.d("stone", "charge");
        Log.d("stone", "amount is " + amount);
        Log.d("stone", "reference id  is " + referenceId);
        boolean enableTipping = false;
        boolean enableInstallments = false;
        boolean enableLogin = true;
        long amountL = BigDecimal.valueOf(amount * 100).longValue();
        TransactionReference reference = new TransactionReference.Builder(referenceId)
                .put("PAYMENT_EXTRA_INFO", "Started from home screen")
                .build();
        Intent intent = new CardPaymentActivity.IntentBuilder(cordova.getActivity())
                .amount(amountL)
                .reference(reference)
                .enableTipping(enableTipping) // Only for markets with tipping support
                .enableInstalments(enableInstallments) // Only for markets with installments support
                .enableLogin(enableLogin) // Mandatory to set
                .build();
        cordova.setActivityResultCallback(iZettle.this);
        cordova.getActivity().startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    public void refund(double amount, String refId, String refundRefId) {
        Log.d("stone", "refund");
        Log.d("stone", "amount is " + amount);
        Log.d("stone", "refId is " + refId);
        Log.d("stone", "refundRefId is " + refundRefId);
        IZettleSDK.Instance.getRefundsManager().retrieveCardPayment(refId, new RefundsManager.Callback<CardPaymentPayload, RetrieveCardPaymentFailureReason>() {

            @Override
            public void onFailure(RetrieveCardPaymentFailureReason reason) {
                Toast.makeText(cordova.getActivity(), "Refund failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(CardPaymentPayload payload) {
                long amountL = BigDecimal.valueOf(amount * 100).longValue();
                TransactionReference reference = new TransactionReference.Builder(refundRefId)
                        .put("REFUND_EXTRA_INFO", "Started from home screen")
                        .build();
                Intent intent = new RefundsActivity.IntentBuilder(cordova.getActivity())
                        .refundAmount(amountL)
                        .reference(reference)
                        .build();
                cordova.setActivityResultCallback(iZettle.this);
                cordova.getActivity().startActivityForResult(intent, REQUEST_CODE_REFUND);
            }
        });
    }

    public void presentSetting() {
        // todo activity declare
        cordova.getActivity().startActivity(CardReadersActivity.newIntent(cordova.getContext()));
    }

    public void logout() {
        IZettleSDK.Instance.getUser().logout();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("stone", "onActivityResult");
        Log.d("stone", "onActivityResult requestCode is " + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT && data != null) {
            CardPaymentResult result = data.getParcelableExtra(CardPaymentActivity.RESULT_EXTRA_PAYLOAD);
            if (result instanceof CardPaymentResult.Completed) {
                Toast.makeText(cordova.getActivity(), "Payment completed", Toast.LENGTH_SHORT).show();
                this.callbackContext.success();
            } else if (result instanceof CardPaymentResult.Canceled) {
                Toast.makeText(cordova.getActivity(), "Payment canceled", Toast.LENGTH_SHORT).show();
                this.callbackContext.error("Payment canceled");
            } else if (result instanceof CardPaymentResult.Failed) {
                Toast.makeText(cordova.getActivity(), "Payment failed", Toast.LENGTH_SHORT).show();
                this.callbackContext.error("Payment failed");
            }
        }

        if (requestCode == REQUEST_CODE_REFUND && data != null) {
            RefundResult result = data.getParcelableExtra(RefundsActivity.RESULT_EXTRA_PAYLOAD);
            if (result instanceof RefundResult.Completed) {
                Toast.makeText(cordova.getActivity(), "Refund completed", Toast.LENGTH_SHORT).show();
                this.callbackContext.success();
            } else if (result instanceof RefundResult.Canceled) {
                Toast.makeText(cordova.getActivity(), "Refund canceled", Toast.LENGTH_SHORT).show();
                this.callbackContext.error("Payment canceled");
            } else if (result instanceof RefundResult.Failed) {
                Toast.makeText(cordova.getActivity(), "Refund failed ", Toast.LENGTH_SHORT).show();
                this.callbackContext.error("Payment canceled");
            }
        }
    }
}
