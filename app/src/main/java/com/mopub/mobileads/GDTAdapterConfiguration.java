package com.mopub.mobileads;

import android.content.Context;
import android.util.Log;

import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.common.Preconditions;
import com.mopub.common.logging.MoPubLog;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.comm.managers.GDTADManager;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GDTAdapterConfiguration extends BaseAdapterConfiguration {
    private static final String TAG = "GDT Adapter yjg";
    public static final String ADAPTER_VERSION = "4.191.1061.0";
    private static final String MOPUB_NETWORK_NAME = "gdt";
    private static final String ADAPTER_NAME = GDTAdapterConfiguration.class.getSimpleName();

    public static final String AD_UNIT_REWARD_ID_KEY = "placementId";
    public static final String APP_ID_KEY = "appId";

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
        return ADAPTER_NAME;
    }

    @NonNull
    @Override
    public String getNetworkSdkVersion() {
        String adapterVersion = this.getAdapterVersion();
        return adapterVersion.substring(0, adapterVersion.lastIndexOf(46));
    }

    @Override
    public void initializeNetwork(@NonNull Context context, @Nullable Map<String, String> configuration, @NonNull OnNetworkInitializationFinishedListener listener) {
        String appId = (String)configuration.get(APP_ID_KEY);
        String adUnitRewardID = (String)configuration.get(AD_UNIT_REWARD_ID_KEY);
        Log.i(TAG, "initializeNetwork: appId " + appId);
        Log.i(TAG, "initializeNetwork: adUnitRewardID " + adUnitRewardID);
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(listener);
        boolean networkInitializationSucceeded = false;
        Class var5 = GDTAdapterConfiguration.class;
        synchronized(GDTAdapterConfiguration.class) {
            try {
//                if (UnityAds.isInitialized()) {
//                    networkInitializationSucceeded = true;
//                } else if (configuration != null && context instanceof Activity) {
//                    UnityRouter.initUnityAds(configuration, (Activity)context);
//                    networkInitializationSucceeded = true;
//                } else {
//                    MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity Ads initialization not started. Context is not an Activity. Note that initialization on the first app launch is a no-op."});
//                }

                Log.i(TAG, "initializeNetwork: succcess");
                networkInitializationSucceeded = true;
            } catch (Exception var8) {
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM_WITH_THROWABLE, new Object[]{"Initializing GDT has encountered an exception.", var8});
            }
        }

        if (networkInitializationSucceeded) {
            listener.onNetworkInitializationFinished(GDTAdapterConfiguration.class, MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
        } else {
            listener.onNetworkInitializationFinished(GDTAdapterConfiguration.class, MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }
    }
}
