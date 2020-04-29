//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.common.Preconditions;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.logging.MoPubLog.AdapterLogEvent;
import com.mopub.common.logging.MoPubLog.LogLevel;
import com.unity3d.ads.UnityAds;
import java.util.Map;

public class UnityAdsAdapterConfiguration extends BaseAdapterConfiguration {
    private static final String TAG = "UnityA con yjg";
    public static final String ADAPTER_VERSION = "3.4.2.0";
    private static final String MOPUB_NETWORK_NAME = "unityads";
    private static final String ADAPTER_NAME = UnityAdsAdapterConfiguration.class.getSimpleName();

    public UnityAdsAdapterConfiguration() {
    }

    @NonNull
    public String getAdapterVersion() {
        Log.i(TAG, "getAdapterVersion: ");
        return "3.4.2.0";
    }

    @Nullable
    public String getBiddingToken(@NonNull Context context) {
        Log.i(TAG, "getBiddingToken: ");
        return null;
    }

    @NonNull
    public String getMoPubNetworkName() {
        Log.i(TAG, "getMoPubNetworkName: ");
        return "unityads";
    }

    @NonNull
    public String getNetworkSdkVersion() {
        Log.i(TAG, "getNetworkSdkVersion: ");
        String sdkVersion = UnityAds.getVersion();
        if (!TextUtils.isEmpty(sdkVersion)) {
            return sdkVersion;
        } else {
            String adapterVersion = this.getAdapterVersion();
            return adapterVersion.substring(0, adapterVersion.lastIndexOf(46));
        }
    }

    public void initializeNetwork(@NonNull Context context, @Nullable Map<String, String> configuration, @NonNull OnNetworkInitializationFinishedListener listener) {
        Log.i(TAG, "initializeNetwork: ");
        String gameId = (String)configuration.get("gameId");
        Log.i(TAG, "initializeNetwork: gameId " + gameId);
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(listener);
        boolean networkInitializationSucceeded = false;
        Class var5 = UnityAdsAdapterConfiguration.class;
        synchronized(UnityAdsAdapterConfiguration.class) {
            try {
                if (UnityAds.isInitialized()) {
                    networkInitializationSucceeded = true;
                } else if (configuration != null && context instanceof Activity) {
                    UnityRouter.initUnityAds(configuration, (Activity)context);
                    networkInitializationSucceeded = true;
                } else {
                    MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity Ads initialization not started. Context is not an Activity. Note that initialization on the first app launch is a no-op."});
                }
            } catch (Exception var8) {
                MoPubLog.log(AdapterLogEvent.CUSTOM_WITH_THROWABLE, new Object[]{"Initializing Unity Ads has encountered an exception.", var8});
            }
        }

        if (networkInitializationSucceeded) {
            listener.onNetworkInitializationFinished(UnityAdsAdapterConfiguration.class, MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
        } else {
            listener.onNetworkInitializationFinished(UnityAdsAdapterConfiguration.class, MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }

        LogLevel logLevel = MoPubLog.getLogLevel();
        boolean debugModeEnabled = logLevel == LogLevel.DEBUG;
        UnityAds.setDebugMode(debugModeEnabled);
    }
}
