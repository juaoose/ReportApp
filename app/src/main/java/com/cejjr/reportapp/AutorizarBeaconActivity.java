package com.cejjr.reportapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mundo.Guardia;
import mundo.RestClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AutorizarBeaconActivity extends AppCompatActivity implements Handler.Callback{

    private static final int MSG_AUTH_UI_WITH_PW = 1001;

    private boolean onReadyIdentify = false;
    private SpassFingerprint mSpassFingerprint;
    private boolean isFeatureEnabled_index = false;
    private boolean isFeatureEnabled_fingerprint = false;
    private ArrayList<Integer> designatedFingersDialog = null;
    private Spass mSpass;
    private Context mContext;
    private boolean hasRegisteredFinger = false;
    private Handler mHandler;
    private String macAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autorizar_beacon);
        macAddress = getIntent().getStringExtra("macAddress");
        ((TextView)findViewById(R.id.txview)).setText(macAddress);
        mContext = this;
        mSpass = new Spass();
        mHandler = new Handler(this);

        try {
            mSpass.initialize(AutorizarBeaconActivity.this);
        } catch (SsdkUnsupportedException e) {
            log("Exception: " + e);
        } catch (UnsupportedOperationException e) {
            log("Fingerprint Service is not supported in the device");
        }
        isFeatureEnabled_fingerprint = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);

        if (isFeatureEnabled_fingerprint) {
            mSpassFingerprint = new SpassFingerprint(AutorizarBeaconActivity.this);
            log("Fingerprint Service is supported in the device.");
            log("SDK version : " + mSpass.getVersionName());
        } else {
            log("Fingerprint Service is not supported in the device.");
            return;
        }
        isFeatureEnabled_index = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_FINGER_INDEX);
        registerBroadcastReceiver();
    }

    private BroadcastReceiver mPassReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (SpassFingerprint.ACTION_FINGERPRINT_RESET.equals(action)) {
                Toast.makeText(mContext, "all fingerprints are removed", Toast.LENGTH_SHORT).show();
            } else if (SpassFingerprint.ACTION_FINGERPRINT_REMOVED.equals(action)) {
                int fingerIndex = intent.getIntExtra("fingerIndex", 0);
                Toast.makeText(mContext, fingerIndex + " fingerprints is removed", Toast.LENGTH_SHORT).show();
            } else if (SpassFingerprint.ACTION_FINGERPRINT_ADDED.equals(action)) {
                int fingerIndex = intent.getIntExtra("fingerIndex", 0);
                Toast.makeText(mContext, fingerIndex + " fingerprints is added", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_RESET);
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_REMOVED);
        filter.addAction(SpassFingerprint.ACTION_FINGERPRINT_ADDED);
        mContext.registerReceiver(mPassReceiver, filter);
    };

    @Override
    public boolean handleMessage(Message msg) {
        startIdentifyDialog(false);
        return true;
    }

    private void startIdentifyDialog(boolean backup) {
        if (onReadyIdentify == false) {
            onReadyIdentify = true;
            try {
                if (mSpassFingerprint != null) {
                    setIdentifyIndexDialog();
                    mSpassFingerprint.startIdentifyWithDialog(AutorizarBeaconActivity.this, mIdentifyListenerDialog, backup);
                }
                if (designatedFingersDialog != null) {
                    log("Please identify finger to verify you with " + designatedFingersDialog.toString() + " finger");
                } else {
                    log("Please identify finger to verify you");
                }
            } catch (IllegalStateException e) {
                onReadyIdentify = false;
                resetIdentifyIndexDialog();
                log("Exception: " + e);
            }
        } else {
            log("The previous request is remained. Please finished or cancel first");
        }
    }

    private void setIdentifyIndexDialog() {
        if (isFeatureEnabled_index) {
            if (mSpassFingerprint != null && designatedFingersDialog != null) {
                mSpassFingerprint.setIntendedFingerprintIndex(designatedFingersDialog);
            }
        }
    }

    private static String getEventStatusName(int eventStatus) {
        return "STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS";
    }

    private SpassFingerprint.IdentifyListener mIdentifyListenerDialog = new SpassFingerprint.IdentifyListener() {
        @Override
        public void onFinished(int eventStatus) {
            log("identify finished : reason =" + getEventStatusName(eventStatus));
            int FingerprintIndex = 0;
            boolean isFailedIdentify = false;
            onReadyIdentify = false;
            try {
                FingerprintIndex = mSpassFingerprint.getIdentifiedFingerprintIndex();
            } catch (IllegalStateException ise) {
                log(ise.getMessage());
            }
            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS || eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS ) {
                createReport(macAddress, Guardia.darGuardia().getIdGuardia());
            } else if (eventStatus == SpassFingerprint.STATUS_USER_CANCELLED
                    || eventStatus == SpassFingerprint.STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE) {
                log("onFinished() : User cancel this identify.");
            } else if (eventStatus == SpassFingerprint.STATUS_TIMEOUT_FAILED) {
                log("onFinished() : The time for identify is finished.");
            } else if (!mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_AVAILABLE_PASSWORD)) {
                if (eventStatus == SpassFingerprint.STATUS_BUTTON_PRESSED) {
                    log("onFinished() : User pressed the own button");
                    Toast.makeText(mContext, "Please connect own Backup Menu", Toast.LENGTH_SHORT).show();
                }
            } else {
                log("onFinished() : Authentification Fail for identify");
                isFailedIdentify = true;
            }
            if (!isFailedIdentify) {
                resetIdentifyIndexDialog();
            }
        }

        @Override
        public void onReady() {
            log("identify state is ready");
        }

        @Override
        public void onStarted() {
            log("User touched fingerprint sensor");
        }

        @Override
        public void onCompleted() {
            log("the identify is completed");
        }
    };

    private void createReport(String macAddress, int idGuardia)
    {
        try{
            final MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            final OkHttpClient client = new OkHttpClient();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("macAddress", macAddress);
            jsonObject.put("idGuardia",idGuardia);

            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            Request request = new Request.Builder()
                    .url(RestClient.BASE_URL+"/segGuar/"+macAddress+"-"+idGuardia)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override public void onResponse(Call call, Response response) throws IOException
                {
                    finish();
                }
            });
        }
        catch(Exception e)
        {

        }

    }

    private void log(String text) {
        final String txt = text;

        runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                Toast.makeText(mContext, txt, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetIdentifyIndexDialog() {
        designatedFingersDialog = null;
    }
    @Override
    protected void onResume() {
        setButtonEnable();
        super.onResume();
    }

    private void setButtonEnable() {
        if (mSpassFingerprint == null) {
            return;
        }
        try {
            hasRegisteredFinger = mSpassFingerprint.hasRegisteredFinger();
        } catch (UnsupportedOperationException e) {
            log("Fingerprint Service is not supported in the device");
        }
        if (hasRegisteredFinger) {
            log("The registered Fingerprint is existed");
        } else {
            log("Please register finger first");
        }
        Button huella = (Button) findViewById(R.id.btnId);
        if(huella != null)
        {
            huella.setOnClickListener(onButtonClick);
        }
    }
    private Button.OnClickListener onButtonClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            mHandler.sendEmptyMessage(MSG_AUTH_UI_WITH_PW);
        }
    };
}
