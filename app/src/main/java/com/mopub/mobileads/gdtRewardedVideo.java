package com.mopub.mobileads;

import android.app.Activity;
import android.util.Log;

import com.mopub.common.LifecycleListener;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.util.AdError;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class gdtRewardedVideo extends CustomEventRewardedVideo {

    private static final String TAG = "gdt rv yjg";
    private RewardVideoAD rewardVideoAD;
    private boolean isGdtRewardLoaded=false;
    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    //1.初始化sdk
    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {

//        String appId = (String)serverExtras.get("appId");
//        String adUnitRewardID = (String)serverExtras.get("adUnitRewardID");

        String appId = "1101152570";
        String adUnitRewardID = "2090845242931421";
        rewardVideoAD = new RewardVideoAD(launcherActivity,appId,adUnitRewardID,gdtRewardVideoADListener);

        return false;
    }

    //2.请求广告
    @Override
    protected void loadWithSdkInitialized(@NonNull Activity activity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        rewardVideoAD.loadAD();
    }

    //3.判断广告是否已经加载
    @Override
    protected boolean hasVideoAvailable() {
        return isGdtRewardLoaded;
    }

    //4.实现show方法逻辑
    @Override
    protected void showVideo() {
        rewardVideoAD.showAD();
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        return null;
    }
    //
    @Override
    protected void onInvalidate() {

    }

    RewardVideoADListener gdtRewardVideoADListener = new RewardVideoADListener() {
        @Override
        public void onADLoad() {
            Log.i(TAG, "onADLoad: ");
            isGdtRewardLoaded = true;
        }

        @Override
        public void onVideoCached() {
            Log.i(TAG, "onVideoCached: ");
        }

        @Override
        public void onADShow() {
            Log.i(TAG, "onADShow: ");
        }

        @Override
        public void onADExpose() {
            Log.i(TAG, "onADExpose: ");
        }

        @Override
        public void onReward() {
            Log.i(TAG, "onReward: ");
        }

        @Override
        public void onADClick() {
            Log.i(TAG, "onADClick: ");
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
        }
    };
}
