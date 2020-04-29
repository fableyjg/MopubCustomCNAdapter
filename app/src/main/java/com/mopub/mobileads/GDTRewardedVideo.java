package com.mopub.mobileads;

import android.app.Activity;
import android.util.Log;

import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPubReward;
import com.mopub.common.logging.MoPubLog;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.managers.GDTADManager;
import com.qq.e.comm.util.AdError;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class GDTRewardedVideo extends CustomEventRewardedVideo {

    private static final String TAG = "gdt rv yjg";
    private static final String ADAPTER_NAME = GDTRewardedVideo.class.getSimpleName();
    private RewardVideoAD rewardVideoAD;
    private String mAdUnitRewardId;
    private boolean isGdtRewardLoaded=false;

    private GDTAdapterConfiguration mGDTAdapterConfiguration = new GDTAdapterConfiguration();
    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    //1.初始化sdk
    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
//        String appId = "1101152570";
//        String adUnitRewardID = "2090845242931421";

        synchronized(GDTRewardedVideo.class) {
            String appId = (String)serverExtras.get("appId");
            mAdUnitRewardId = (String)serverExtras.get("adUnitRewardID");
            Log.i(TAG, "checkAndInitializeSdk: appId:"+appId + " adUnitRewardID:"+ mAdUnitRewardId);

            if (GDTADManager.getInstance().isInitialized()) {
                return false;
            } else {
                mGDTAdapterConfiguration.setCachedInitializationParameters(launcherActivity, serverExtras);
                if (appId != null && !appId.isEmpty() && mAdUnitRewardId !=null && !mAdUnitRewardId.isEmpty()) {
                    rewardVideoAD = new RewardVideoAD(launcherActivity,appId, mAdUnitRewardId,gdtRewardVideoADListener);
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
        if(rewardVideoAD!=null){
            rewardVideoAD.loadAD();
        }
    }

    //3.判断广告是否已经加载
    @Override
    protected boolean hasVideoAvailable() {
        return isGdtRewardLoaded;
    }

    //4.实现show方法逻辑
    @Override
    protected void showVideo() {
        if(isReady()){
            rewardVideoAD.showAD();
        }else {
            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(GDTRewardedVideo.class,mAdUnitRewardId,MoPubErrorCode.NETWORK_NO_FILL);
        }
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return mAdUnitRewardId;
    }
    //
    @Override
    protected void onInvalidate() {

    }

    RewardVideoADListener gdtRewardVideoADListener = new RewardVideoADListener() {
        @Override
        public void onADLoad() {
            Log.i(TAG, "onADLoad: ");
        }

        @Override
        public void onVideoCached() {
            Log.i(TAG, "onVideoCached: ");
            isGdtRewardLoaded = true;

            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "GDT rewarded video cached for placement " + mAdUnitRewardId + "."});
            MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(GDTRewardedVideo.class, mAdUnitRewardId);
            MoPubLog.log(MoPubLog.AdapterLogEvent.LOAD_SUCCESS, new Object[]{ADAPTER_NAME});
        }

        @Override
        public void onADShow() {
            Log.i(TAG, "onADShow: ");

        }

        @Override
        public void onADExpose() {
            Log.i(TAG, "onADExpose: ");
            MoPubRewardedVideoManager.onRewardedVideoStarted(GDTRewardedVideo.class, mAdUnitRewardId);
            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "GDT rewarded video started for placement " + mAdUnitRewardId + "."});
            MoPubLog.log(MoPubLog.AdapterLogEvent.SHOW_SUCCESS, new Object[]{ADAPTER_NAME});
        }

        @Override
        public void onReward() {
            Log.i(TAG, "onReward: ");
            MoPubLog.log(MoPubLog.AdapterLogEvent.SHOULD_REWARD, new Object[]{ADAPTER_NAME, -123, ""});
            MoPubRewardedVideoManager.onRewardedVideoCompleted(GDTRewardedVideo.class, mAdUnitRewardId, MoPubReward.success("", -123));
            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "GDT rewarded video completed for placement " + mAdUnitRewardId});
        }

        @Override
        public void onADClick() {
            Log.i(TAG, "onADClick: ");
            MoPubRewardedVideoManager.onRewardedVideoClicked(GDTRewardedVideo.class, mAdUnitRewardId);
            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "GDT rewarded video clicked for placement " + mAdUnitRewardId + "."});
            MoPubLog.log(MoPubLog.AdapterLogEvent.CLICKED, new Object[]{ADAPTER_NAME});
        }

        @Override
        public void onVideoComplete() {
            Log.i(TAG, "onVideoComplete: ");
        }

        @Override
        public void onADClose() {
            Log.i(TAG, "onADClose: ");
            isGdtRewardLoaded = false;
        }

        @Override
        public void onError(AdError adError) {
            Log.i(TAG, "onError: ");
            MoPubLog.log(MoPubLog.AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "GDT rewarded video cache failed for placement " + mAdUnitRewardId});
            MoPubErrorCode errorCode = MoPubErrorCode.VIDEO_PLAYBACK_ERROR;
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(GDTRewardedVideo.class, mAdUnitRewardId, errorCode);
            MoPubLog.log(getAdNetworkId(), MoPubLog.AdapterLogEvent.LOAD_FAILED, new Object[]{ADAPTER_NAME, errorCode.getIntCode(), errorCode});
        }
    };
}
