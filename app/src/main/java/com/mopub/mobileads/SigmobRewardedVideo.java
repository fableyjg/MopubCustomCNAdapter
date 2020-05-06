package com.mopub.mobileads;

import android.app.Activity;
import android.util.Log;

import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPubReward;
import com.mopub.common.logging.MoPubLog;

import com.sigmob.windad.WindAdError;
import com.sigmob.windad.WindAdOptions;
import com.sigmob.windad.WindAds;
import com.sigmob.windad.rewardedVideo.WindRewardAdRequest;
import com.sigmob.windad.rewardedVideo.WindRewardInfo;
import com.sigmob.windad.rewardedVideo.WindRewardedVideoAd;
import com.sigmob.windad.rewardedVideo.WindRewardedVideoAdListener;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SigmobRewardedVideo extends CustomEventRewardedAd {

    private static final String TAG = "sigmob rv yjg";
    private static final String ADAPTER_NAME = SigmobRewardedVideo.class.getSimpleName();

    public static final String APP_ID_KEY = "appId";
    public static final String APP_NAME_KEY = "appName";
    public static final String AD_UNIT_ID_KEY = "placementId";

    private WindRewardedVideoAd windRewardedVideoAd;
    private WindRewardAdRequest request;
    private String mAdUnitRewardId;
    private boolean isSigmobRewardLoaded =false;
    private Activity mActivity;

    private SigmobAdapterConfiguration mSigmobAdapterConfiguration = new SigmobAdapterConfiguration();

    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    //1.初始化sdk
    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        synchronized(SigmobRewardedVideo.class) {
            mActivity = launcherActivity;
            String appId = (String)serverExtras.get(APP_ID_KEY);
            mAdUnitRewardId = (String)serverExtras.get(AD_UNIT_ID_KEY);
            String appKey = serverExtras.get(APP_NAME_KEY);
            Log.i(TAG, "checkAndInitializeSdk: appId:"+appId + " adUnitRewardID:"+ mAdUnitRewardId);

            if (WindAds.isInited) {
                return false;
            } else {
                mSigmobAdapterConfiguration.setCachedInitializationParameters(launcherActivity, serverExtras);
                if (appId != null && !appId.isEmpty() && mAdUnitRewardId !=null && !mAdUnitRewardId.isEmpty()) {
                    WindAds ads = WindAds.sharedAds();
                    ads.startWithOptions(launcherActivity,new WindAdOptions(appId,appKey));

                    windRewardedVideoAd = WindRewardedVideoAd.sharedInstance();
                    windRewardedVideoAd.setWindRewardedVideoAdListener(windRewardedVideoAdListener);
                    return true;
                } else {
                    MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "gameId is missing or entered incorrectly in the MoPub UI"});
                    return false;
                }
            }
        }
    }

    //2.请求广告
    @Override
    protected void loadWithSdkInitialized(@NonNull Activity activity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        if(windRewardedVideoAd!=null){
            request = new WindRewardAdRequest(mAdUnitRewardId,null,null);
            windRewardedVideoAd.loadAd(request);
        }
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return mAdUnitRewardId;
    }

    @Override
    protected void onInvalidate() {

    }

    //3.判断广告是否已经加载
    @Override
    protected boolean isReady() {
        return isSigmobRewardLoaded;
    }

    //4.实现show方法逻辑
    @Override
    protected void show() {
        if(isReady()){
            windRewardedVideoAd.show(mActivity,request);
        }else {
            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(SigmobRewardedVideo.class,mAdUnitRewardId,MoPubErrorCode.NETWORK_NO_FILL);
        }
    }

    WindRewardedVideoAdListener windRewardedVideoAdListener = new WindRewardedVideoAdListener() {
        @Override
        public void onVideoAdLoadSuccess(String s) {
            Log.i(TAG, "onVideoAdLoadSuccess: ");
            isSigmobRewardLoaded = true;

            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Sigmob rewarded video cached for placement " + mAdUnitRewardId + "."});
            MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(SigmobRewardedVideo.class, mAdUnitRewardId);
            MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_SUCCESS, new Object[]{ADAPTER_NAME});
        }

        @Override
        public void onVideoAdPreLoadSuccess(String s) {
            Log.i(TAG, "onVideoAdPreLoadSuccess: ");
        }

        @Override
        public void onVideoAdPreLoadFail(String s) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Sigmob rewarded video PreLoadFail for placement " + mAdUnitRewardId});
            MoPubErrorCode errorCode = MoPubErrorCode.VIDEO_PLAYBACK_ERROR;
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(SigmobRewardedVideo.class, mAdUnitRewardId, errorCode);
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.LOAD_FAILED, new Object[]{ADAPTER_NAME, errorCode.getIntCode(), errorCode});
        }

        @Override
        public void onVideoAdPlayStart(String s) {
            MoPubRewardedVideoManager.onRewardedVideoStarted(SigmobRewardedVideo.class, mAdUnitRewardId);
            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Sigmob rewarded video started for placement " + mAdUnitRewardId + "."});
            MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_SUCCESS, new Object[]{ADAPTER_NAME});
        }

        @Override
        public void onVideoAdPlayEnd(String s) {

        }

        @Override
        public void onVideoAdClicked(String s) {
            MoPubRewardedVideoManager.onRewardedVideoClicked(SigmobRewardedVideo.class, mAdUnitRewardId);
            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Sigmob rewarded video clicked for placement " + mAdUnitRewardId + "."});
            MoPubLog.log(MoPubLog.AdapterLogEvent.CLICKED, new Object[]{ADAPTER_NAME});
        }

        @Override
        public void onVideoAdClosed(WindRewardInfo windRewardInfo, String s) {
            isSigmobRewardLoaded = false;
            if(windRewardInfo.isComplete()){
                MoPubLog.log(MoPubLog.AdapterLogEvent.SHOULD_REWARD, new Object[]{ADAPTER_NAME, -123, ""});
                MoPubRewardedVideoManager.onRewardedVideoCompleted(SigmobRewardedVideo.class, mAdUnitRewardId, MoPubReward.success("", -123));
                MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Sigmob rewarded video completed for placement " + mAdUnitRewardId});
            }
            MoPubLog.log(MoPubLog.AdapterLogEvent.SHOULD_REWARD, new Object[]{ADAPTER_NAME, -123, ""});
            MoPubRewardedVideoManager.onRewardedVideoClosed(SigmobRewardedVideo.class, mAdUnitRewardId);
            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Sigmob rewarded video completed for placement " + mAdUnitRewardId});
        }

        @Override
        public void onVideoAdLoadError(WindAdError windAdError, String s) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Sigmob rewarded video cache failed for placement " + mAdUnitRewardId});
            MoPubErrorCode errorCode = MoPubErrorCode.VIDEO_PLAYBACK_ERROR;
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(SigmobRewardedVideo.class, mAdUnitRewardId, errorCode);
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.LOAD_FAILED, new Object[]{ADAPTER_NAME, errorCode.getIntCode(), errorCode});
        }

        @Override
        public void onVideoAdPlayError(WindAdError windAdError, String s) {
            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Sigmob rewarded video cache failed for placement " + mAdUnitRewardId});
            MoPubErrorCode errorCode = MoPubErrorCode.VIDEO_PLAYBACK_ERROR;
            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(SigmobRewardedVideo.class, mAdUnitRewardId, errorCode);
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.SHOW_FAILED, new Object[]{ADAPTER_NAME, errorCode.getIntCode(), errorCode});
        }
    };
}
