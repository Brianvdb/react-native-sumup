
package nl.bvdb.sumup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ReactMethod;
import com.sumup.merchant.Models.TransactionInfo;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpLogin;
import com.sumup.merchant.api.SumUpPayment;
import com.sumup.merchant.api.SumUpState;

import java.math.BigDecimal;


public class RNSumupModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_PAYMENT = 2;
    private static final int REQUEST_CODE_PAYMENT_SETTINGS = 3;

    public static String AFFILIATE_KEY = "3ea03525-8d68-4313-906d-72e738d34c6c";

    public RNSumupModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                SumUpState.init(reactContext.getApplicationContext());
            }
        });

        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNSumup";
    }

    @ReactMethod
    public void login() {
        SumUpLogin sumUplogin = SumUpLogin.builder(AFFILIATE_KEY).build();
        SumUpAPI.openLoginActivity(getCurrentActivity(), sumUplogin, REQUEST_CODE_LOGIN);
    }

    @ReactMethod
    public void charge(String amount) {
        SumUpPayment payment = SumUpPayment.builder()
                .total(new BigDecimal(amount))
                .currency(SumUpPayment.Currency.EUR)
                .skipSuccessScreen()
                .build();
        SumUpAPI.checkout(getCurrentActivity(), payment, REQUEST_CODE_PAYMENT);
    }

    @ReactMethod
    public void paymentSettings() {
        SumUpAPI.openPaymentSettingsActivity(getCurrentActivity(), REQUEST_CODE_PAYMENT_SETTINGS);
    }

    @ReactMethod
    public void prepareCardTerminal() {
        SumUpAPI.prepareForCheckout();
    }

    @ReactMethod
    public void logout() {
        SumUpAPI.logout();
    }

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case REQUEST_CODE_PAYMENT:
                    if (data != null) {
                        Bundle extra = data.getExtras();
                        TransactionInfo transactionInfo = extra.getParcelable(SumUpAPI.Response.TX_INFO);

                    }
                    break;

                default:
                    break;
            }
        }
    };
}