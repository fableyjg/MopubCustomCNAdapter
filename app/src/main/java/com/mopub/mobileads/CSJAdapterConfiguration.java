package com.mopub.mobileads;

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
    private static final String TAG = "CSJ con yjg";
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
//        synchronized(UnityAdsAdapterConfiguration.class) {
//            try {
//                if (UnityAds.isInitialized()) {
//                    networkInitializationSucceeded = true;
//                } else if (configuration != null && context instanceof Activity) {
//                    UnityRouter.initUnityAds(configuration, (Activity)context);
//                    networkInitializationSucceeded = true;
//                } else {
//                    MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity Ads initialization not started. Context is not an Activity. Note that initialization on the first app launch is a no-op."});
//                }
//            } catch (Exception var8) {
//                MoPubLog.log(AdapterLogEvent.CUSTOM_WITH_THROWABLE, new Object[]{"Initializing Unity Ads has encountered an exception.", var8});
//            }
//        }
//
//        if (networkInitializationSucceeded) {
//            listener.onNetworkInitializationFinished(UnityAdsAdapterConfiguration.class, MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
//        } else {
//            listener.onNetworkInitializationFinished(UnityAdsAdapterConfiguration.class, MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
//        }


        Log.i(TAG, "initializeNetwork: configuration " + configuration.toString());
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(listener);
        boolean networkInitializationSucceeded = false;
        synchronized(CSJAdapterConfiguration.class) {
            try {
                if (configuration != null){
                    //step1:初始化sdk
                    String appid = configuration.get(APP_ID_KEY);
                    String appName = configuration.get(APP_NAME_KEY);
                    Log.i(TAG, "initializeNetwork: appid:"+appid + " appName:"+appName);
                    if(appid!=null && appName!=null){
                        TTAdManagerHolder.init(context,appid,appName);
                    }
                }
                networkInitializationSucceeded = true;
            } catch (Exception var8) {
                Log.i(TAG, "initializeNetwork: Exception:"+var8);
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM_WITH_THROWABLE, new Object[]{"Initializing AdMob has encountered an exception.", var8});
            }
        }

        if (networkInitializationSucceeded) {
            listener.onNetworkInitializationFinished(CSJAdapterConfiguration.class, MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
        } else {
            listener.onNetworkInitializationFinished(CSJAdapterConfiguration.class, MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
        }
    }
}
