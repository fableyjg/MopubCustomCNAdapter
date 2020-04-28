package com.mopub.mobileads;


import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPubReward;
import com.mopub.common.logging.MoPubLog;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.mopub.common.logging.MoPubLog.AdapterLogEvent.CUSTOM;

public class CSJRewardedVideo extends CustomEventRewardedAd {
    private static final String TAG = "CSJRewardedVideo";

    public static final String AD_UNIT_REWARD_ID_KEY = "adUnitRewardID";
    public static final String APP_ID_KEY = "appId";
    public static final String APP_NAME_KEY = "appName";
    public static final String APP_AD_ORIENTATION = "adOrientation";

    private static final String ADAPTER_NAME = CSJRewardedVideo.class.getSimpleName();

    private Activity mActivity;
//    private TTAdNative mTTAdNative;
    private TTRewardVideoAd mttRewardVideoAd;

    private boolean isTTRewardVideoLoaded = false;

    /**
     * Flag to determine whether or not the adapter has been  initialized.
     */
    private static AtomicBoolean sIsInitialized;
    private String mAdUnitRewardId;


    private CSJAdapterConfiguration mCSJAdapterConfiguration;
    private WeakReference<Activity> mWeakActivity;

    public CSJRewardedVideo(){
        sIsInitialized = new AtomicBoolean(false);
        mCSJAdapterConfiguration = new CSJAdapterConfiguration();
        Log.i(TAG, "CSJRewardedVideo: has been create ....");
    }

    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        mActivity = launcherActivity;

        if(MoPubLog.getLogLevel() == MoPubLog.LogLevel.DEBUG){
            Set<Map.Entry<String, Object>> set = localExtras.entrySet();
            for (Map.Entry<String, Object> entry : set) {
                Log.d(TAG,"localExtras => key=" + entry.getKey() + ",value=" + entry.getValue());
                MoPubLog.log(CUSTOM, ADAPTER_NAME, "localExtras => key=" + entry.getKey() + ",value=" + entry.getValue());
            }

            Set<Map.Entry<String, String>> set2 = serverExtras.entrySet();
            for (Map.Entry<String, String> entry : set2) {
                Log.d(TAG,"serverExtras => key=" + entry.getKey() + ",value=" + entry.getValue());
                MoPubLog.log(CUSTOM, ADAPTER_NAME, "serverExtras => key=" + entry.getKey() + ",value=" + entry.getValue());
            }
        }

        if(!sIsInitialized.getAndSet(true)){
            //step1:初始化sdk
            String appid = serverExtras.get(APP_ID_KEY);
            String appName = serverExtras.get(APP_NAME_KEY);
            TTAdManagerHolder.init(mActivity, appid, appName);
            mAdUnitRewardId = serverExtras.get(AD_UNIT_REWARD_ID_KEY);

            Log.i(TAG, "checkAndInitializeSdk: appid " + appid + " mAdUnitRewardId:"+mAdUnitRewardId);
            mCSJAdapterConfiguration.setCachedInitializationParameters(launcherActivity,serverExtras);
            return true;
        }

        return false;
    }

    @Override
    protected void loadWithSdkInitialized(@NonNull Activity activity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        Log.i(TAG, "loadWithSdkInitialized: 11111111111");

        mWeakActivity = new WeakReference<>(activity);
        TTAdManager mTTAdManager = TTAdManagerHolder.get();
        TTAdNative mTTAdNative = mTTAdManager.createAdNative(activity.getApplicationContext());
        //step4:Create a parameter AdSlot for reward ad request type,
        //      refer to the document for meanings of specific parameters
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(mAdUnitRewardId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName("gold coin") //Parameter for rewarded video ad requests, name of the reward
                .setRewardAmount(3)  // The number of rewards in rewarded video ad
                .setUserID("user123")//User ID, a required parameter for rewarded video ads
                .setMediaExtra("media_extra") //optional parameter
                .setOrientation(TTAdConstant.VERTICAL) //Set how you wish the video ad to be displayed, choose from TTAdConstant.HORIZONTAL or TTAdConstant.VERTICAL
                .build();

        //load ad
        mTTAdNative.loadRewardVideoAd(adSlot, mLoadRewardVideoAdListener);


//        if (extrasAreValid(serverExtras)) {
//            Log.i(TAG, "loadWithSdkInitialized: 2222222222222   mAdUnitRewardId:"+ mAdUnitRewardId);
//            //4.解析出服务端id
//            mAdUnitRewardId = serverExtras.get(AD_UNIT_REWARD_ID_KEY);
//            String adOrientation = serverExtras.get(APP_AD_ORIENTATION).trim();
//            loadAd(mAdUnitRewardId, Integer.parseInt(adOrientation));
////            loadAd(adUnitId, TTAdConstant.VERTICAL);
//        }
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return mAdUnitRewardId;
    }

    @Override
    protected void onInvalidate() {
        if (mttRewardVideoAd != null) {
            mttRewardVideoAd.setRewardAdInteractionListener((TTRewardVideoAd.RewardAdInteractionListener) null);
            mttRewardVideoAd = null;
        }
    }

    @Override
    protected boolean isReady() {
        return mttRewardVideoAd!=null && isTTRewardVideoLoaded;
    }

    @Override
    protected void show() {
        Log.i(TAG, "show: reward video");
        if(isReady() && mWeakActivity != null && mWeakActivity.get()!=null){
            mttRewardVideoAd.setRewardAdInteractionListener(mRewardAdInteractionListener);
            mttRewardVideoAd.showRewardVideoAd(mActivity);
        }else {
            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(CSJRewardedVideo.class,mAdUnitRewardId,MoPubErrorCode.NETWORK_NO_FILL);
        }
    }

    //3.解析服务端id
    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(AD_UNIT_REWARD_ID_KEY);
    }

    private TTAdNative.RewardVideoAdListener mLoadRewardVideoAdListener = new TTAdNative.RewardVideoAdListener() {

        @Override
        public void onError(int code, String message) {
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(CSJRewardedVideo.class, getAdNetworkId(), getMoPubErrorCode(code));
            Log.i(TAG, "onError: Loading Rewarded Video creative encountered an error:"+code + " message:"+message);
//            MoPubLog.log(LOAD_FAILED, ADAPTER_NAME, "Loading Rewarded Video creative encountered an error: " + mapErrorCode(code).toString() + ",error message:" + message);
        }

        @Override
        public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
            Log.d(TAG,"onRewardVideoAdLoad method execute ......ad = " + ad);
            if (ad != null) {
                isTTRewardVideoLoaded = true;
                mttRewardVideoAd = ad;
                MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(
                        CSJRewardedVideo.class,
                        getAdNetworkId());
            } else {
                MoPubRewardedVideoManager.onRewardedVideoLoadFailure(CSJRewardedVideo.class, getAdNetworkId(),MoPubErrorCode.NETWORK_NO_FILL);
//                MoPubLog.log(LOAD_FAILED, ADAPTER_NAME, " TTRewardVideoAd is null !");
                Log.i(TAG, "onRewardVideoAdLoad: TTRewardVideoAd is null");
            }
        }

        @Override
        public void onRewardVideoCached() {
            MoPubLog.log(CUSTOM, ADAPTER_NAME, "TTRewardVideoAd onRewardVideoCached...");
            if (mWeakActivity != null && mWeakActivity.get() != null)
//                Toast.makeText(mWeakActivity.get(), "onRewardVideoCached.....", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onRewardVideoCached: ");

        }
    };

    private TTRewardVideoAd.RewardAdInteractionListener mRewardAdInteractionListener = new TTRewardVideoAd.RewardAdInteractionListener() {
        @Override
        public void onAdShow() {
            MoPubRewardedVideoManager.onRewardedVideoStarted(CSJRewardedVideo.class,mAdUnitRewardId);
            if (mWeakActivity != null && mWeakActivity.get() != null)
//                Toast.makeText(mWeakActivity.get(), "TTRewardVideoAd onAdShow.....", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onAdShow: ");
            MoPubLog.log(CUSTOM, ADAPTER_NAME, "TTRewardVideoAd onAdShow...");
        }

        @Override
        public void onAdVideoBarClick() {
            MoPubRewardedVideoManager.onRewardedVideoClicked(CSJRewardedVideo.class,mAdUnitRewardId);
            if (mWeakActivity != null && mWeakActivity.get() != null)
                Log.i(TAG, "onAdVideoBarClick: ");
            MoPubLog.log(CUSTOM, ADAPTER_NAME, "TTRewardVideoAd onAdVideoBarClick...");
        }

        @Override
        public void onAdClose() {
            MoPubRewardedVideoManager.onRewardedVideoClosed(CSJRewardedVideo.class,mAdUnitRewardId);
            if (mWeakActivity != null && mWeakActivity.get() != null)
                Log.i(TAG, "onAdClose: ");
            MoPubLog.log(CUSTOM, ADAPTER_NAME, "TTRewardVideoAd onAdClose...");
        }

        @Override
        public void onVideoComplete() {
            MoPubRewardedVideoManager.onRewardedVideoCompleted(CSJRewardedVideo.class,mAdUnitRewardId, MoPubReward.success(MoPubReward.NO_REWARD_LABEL, MoPubReward.DEFAULT_REWARD_AMOUNT));
            if (mWeakActivity != null && mWeakActivity.get() != null)
                Log.i(TAG, "onVideoComplete: ");
            MoPubLog.log(CUSTOM, ADAPTER_NAME, "TTRewardVideoAd onVideoComplete...");
        }

        @Override
        public void onVideoError() {
            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(CSJRewardedVideo.class,mAdUnitRewardId, MoPubErrorCode.UNSPECIFIED);
            if (mWeakActivity != null && mWeakActivity.get() != null)
                Log.i(TAG, "onVideoError: ");
            MoPubLog.log(CUSTOM, ADAPTER_NAME, "TTRewardVideoAd onVideoError...");
        }

        @Override
        public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
            if (mWeakActivity != null && mWeakActivity.get() != null)
                Log.i(TAG, "onRewardVerify: ");
            MoPubLog.log(CUSTOM, ADAPTER_NAME, "TTRewardVideoAd onRewardVerify...rewardVerify：" + rewardVerify + "，rewardAmount=" + rewardAmount + "，rewardName=" + rewardName);
        }
    };


    private MoPubErrorCode getMoPubErrorCode(int error) {
        MoPubErrorCode errorCode;
        switch (error) {
            case 0:
                errorCode = MoPubErrorCode.INTERNAL_ERROR;
                break;
            case 1:
                errorCode = MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR;
                break;
            case 2:
                errorCode = MoPubErrorCode.NO_CONNECTION;
                break;
            case 3:
                errorCode = MoPubErrorCode.NO_FILL;
                break;
            default:
                errorCode = MoPubErrorCode.UNSPECIFIED;
        }

        return errorCode;
    }
}
