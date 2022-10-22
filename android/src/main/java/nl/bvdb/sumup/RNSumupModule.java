
package nl.bvdb.sumup;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.sumup.merchant.api.SumUpAPI;
import com.sumup.merchant.api.SumUpLogin;
import com.sumup.merchant.api.SumUpPayment;
import com.sumup.merchant.api.SumUpState;

import java.math.BigDecimal;


public class RNSumupModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    private static final String EVENT_LOGGED_IN = "loggedIn";
    private static final String EVENT_PAYMENT_SUCCESS = "paymentSuccess";
    private static final String EVENT_PAYMENT_FAILED = "paymentFailed";

    private static final int REQUEST_CODE_LOGIN = 1;
    private static final int REQUEST_CODE_PAYMENT = 2;
    private static final int REQUEST_CODE_PAYMENT_SETTINGS = 3;
    private static final int REQUEST_CODE_LOGIN_PENDING = 4;

    public static String AFFILIATE_KEY = "3ea03525-8d68-4313-906d-72e738d34c6c";

    private String mPendingAmount = null;

    public RNSumupModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                SumUpState.init(reactContext.getApplicationContext());
            }
        });

        this.reactContext = reactContext;

        reactContext.addActivityEventListener(mActivityEventListener);
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
    public void isLoggedIn(Promise promise) {
        boolean loggedIn = SumUpAPI.isLoggedIn();

        WritableMap map = Arguments.createMap();
        map.putBoolean("loggedIn", loggedIn);

        promise.resolve(map);

    }

    @ReactMethod
    public void charge(String amount) {
        if (!SumUpAPI.isLoggedIn()) {
            mPendingAmount = amount;
            SumUpLogin sumUplogin = SumUpLogin.builder(AFFILIATE_KEY).build();
            SumUpAPI.openLoginActivity(getCurrentActivity(), sumUplogin, REQUEST_CODE_LOGIN_PENDING);
            return;
        }

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

    private void sendEvent(String eventName,
                           @Nullable WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    private void onLoggedIn() {
        WritableMap map = Arguments.createMap();
        sendEvent(EVENT_LOGGED_IN, map);
    }

    private void onPaymentSuccess() {
        WritableMap map = Arguments.createMap();
        sendEvent(EVENT_PAYMENT_SUCCESS, map);
    }

    private void onPaymentFailed() {
        WritableMap map = Arguments.createMap();
        sendEvent(EVENT_PAYMENT_FAILED, map);
    }

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {

        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case REQUEST_CODE_PAYMENT:
                    if (resultCode == SumUpAPI.Response.ResultCode.SUCCESSFUL) {
                        onPaymentSuccess();
                    } else {
                        onPaymentFailed();
                    }
                    /*if (data != null) {
                        Bundle extra = data.getExtras();
                        TransactionInfo transactionInfo = extra.getParcelable(SumUpAPI.Response.TX_INFO);

                    }*/
                    break;
                case REQUEST_CODE_LOGIN:
                    if (resultCode == SumUpAPI.Response.ResultCode.SUCCESSFUL || resultCode == SumUpAPI.Response.ResultCode.ERROR_ALREADY_LOGGED_IN) {
                        onLoggedIn();
                    }
                case REQUEST_CODE_LOGIN_PENDING:
                    if (resultCode == SumUpAPI.Response.ResultCode.SUCCESSFUL || resultCode == SumUpAPI.Response.ResultCode.ERROR_ALREADY_LOGGED_IN) {
                        if (mPendingAmount != null) {
                            charge(mPendingAmount);
                            mPendingAmount = null;
                        }
                    }
                default:
                    break;
            }
        }
    };
}