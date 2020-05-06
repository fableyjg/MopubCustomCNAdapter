package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.common.Preconditions;
import com.mopub.common.logging.MoPubLog;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CSJAdapterConfiguration extends BaseAdapterConfiguration {
    private static final String TAG = "CSJ con";
    private static final String ADAPTER_VERSION = "2.1.5.0";
    private static final String MOPUB_NETWORK_NAME = "csj";

    // TODO: 2020/4/28 测试是否在adapter中初始化
    public static final String APP_ID_KEY = "appId";
    public static final String APP_NAME_KEY = "appName";

    public CSJAdapterConfiguration(){

    }
    @NonNull
    @Override
    public String getAdapterVersion() {
        return ADAPTER_VERSION;
    }

    @Nullable
    @Override
    public String getBiddingToken(@NonNull Context context) {
        return null;
    }

    @NonNull
    @Override
    public String getMoPubNetworkName() {
        return MOPUB_NETWORK_NAME;
    }

    @NonNull
    @Override
    public String getNetworkSdkVersion() {
        String adapterVersion = this.getAdapterVersion();
        return !TextUtils.isEmpty(adapterVersion) ? adapterVersion.substring(0, adapterVersion.lastIndexOf(46)) : "";
    }

    @Override
    public void initializeNetwork(@NonNull Context context, @Nullable Map<String, String> configuration, @NonNull OnNetworkInitializationFinishedListener listener) {
        Log.i(TAG, "initializeNetwork: configuration " + configuration.toString());
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(listener);
        boolean networkInitializationSucceeded = false;
        Class var5 = CSJAdapterConfiguration.class;
        synchronized(CSJAdapterConfiguration.class) {
            try {
                if (configuration != null && context instanceof Activity){
                    //step1:初始化sdk
                    String appid = configuration.get(APP_ID_KEY);
                    String appName = configuration.get(APP_NAME_KEY);
                    Log.i(TAG, "initializeNetwork: appid:"+appid + " appName:"+appName);
                    if (TextUtils.isEmpty(appid)) {
                        MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{"csj's initialization not started. Ensure csj's applicationKey is populated on the MoPub dashboard."});
                    }else if(TextUtils.isEmpty(appName)){
                        MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{"csj's initialization not started. Ensure csj's appName is populated on the MoPub dashboard."});
                    }else {
                        TTAdManagerHolder.init(context,appid,appName);
                        networkInitializationSucceeded = true;
                    }
                }
            } catch (Exception var8) {
                Log.i(TAG, "initializeNetwork: Exception:"+var8);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM_WITH_THROWABLE, new Object[]{"Initializing CSJ has encountered an exception.", var8});
            }
        }

        if (networkInitializationSucceeded) {
            listener.onNetworkInitializationFinished(CSJAdapterConfiguration.class, MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
        } else {
            listener.onNetworkInitializationFinished(CSJAdapterConfiguration.class, MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }
    }
}