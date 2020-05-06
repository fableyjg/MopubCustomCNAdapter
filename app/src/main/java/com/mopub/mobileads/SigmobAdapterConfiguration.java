package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.common.Preconditions;
import com.mopub.common.logging.MoPubLog;
import com.sigmob.windad.WindAdOptions;
import com.sigmob.windad.WindAds;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SigmobAdapterConfiguration extends BaseAdapterConfiguration {

    private static final String TAG = "Sigmob con yjg";
    private static final String ADAPTER_VERSION = "2.17.1.0";
    private static final String MOPUB_NETWORK_NAME = "sigmob";

    public static final String APP_ID_KEY = "appId";
    public static final String APP_NAME_KEY = "appName";
    
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
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(listener);
        boolean networkInitializationSucceeded = false;
        Class var5 = SigmobAdapterConfiguration.class;
        synchronized(SigmobAdapterConfiguration.class) {
            try {
                if (configuration != null && context instanceof Activity){
                    //step1:初始化sdk
                    String appid = configuration.get(APP_ID_KEY);
                    String appKey = configuration.get(APP_NAME_KEY);
                    Log.i(TAG, "initializeNetwork: appid:"+appid + " appKey:"+appKey);
                    if (TextUtils.isEmpty(appid)) {
                        MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{"Sigmob's initialization not started. Ensure Sigmob's applicationKey is populated on the MoPub dashboard."});
                    }else if(TextUtils.isEmpty(appKey)){
                        MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{"Sigmob's initialization not started. Ensure Sigmob's appName is populated on the MoPub dashboard."});
                    }else {
                        WindAds ads = WindAds.sharedAds();
                        ads.startWithOptions(context,new WindAdOptions(appid,appKey));
                        networkInitializationSucceeded = true;
                    }
                }
            } catch (Exception var8) {
                Log.i(TAG, "initializeNetwork: Exception:"+var8);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM_WITH_THROWABLE, new Object[]{"Initializing Sigmob has encountered an exception.", var8});
            }
        }

        if (networkInitializationSucceeded) {
            listener.onNetworkInitializationFinished(SigmobAdapterConfiguration.class, MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
        } else {
            listener.onNetworkInitializationFinished(SigmobAdapterConfiguration.class, MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }
    }
}
