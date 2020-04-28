package com.mopub.mobileads;


import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPubReward;
import com.mopub.common.logging.MoPubLog;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CSJRewardedVideo extends CustomEventRewardedAd {
    private static final String TAG = "CSJRewardedVideo";

    public static final String AD_UNIT_REWARD_ID_KEY = "adUnitRewardID";
    public static final String APP_ID_KEY = "appId";
    public static final String APP_NAME_KEY = "appName";
    public static final String APP_AD_ORIENTATION = "adOrientation";

    private static final String ADAPTER_NAME = CSJRewardedVideo.class.getSimpleName();

    private Activity mActivity;
    private TTAdNative mTTAdNative;
    private TTRewardVideoAd mttRewardVideoAd;

    private boolean isTTRewardVideoLoaded = false;

    private static AtomicBoolean sIsInitialized;
    private String mAdUnitRewardId;


    private CSJAdapterConfiguration mCSJAdapterConfiguration;

    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        mActivity = launcherActivity;

        if (!sIsInitialized.getAndSet(true)) {
            //step1:初始化sdk
            String appid = serverExtras.get(APP_ID_KEY);
            String appName = serverExtras.get(APP_NAME_KEY);
            TTAdManagerHolder.init(mActivity, appid, appName);

            TTAdManager ttAdManager = TTAdManagerHolder.get();
            //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
            ttAdManager.requestPermissionIfNecessary(mActivity);
            //step3:创建TTAdNative对象,用于调用广告请求接口
            mTTAdNative = ttAdManager.createAdNative(mActivity);

            // TODO: 2020/4/28 解析服务端广告id
            mAdUnitRewardId = serverExtras.get(AD_UNIT_REWARD_ID_KEY);

            if (TextUtils.isEmpty(this.mAdUnitRewardId)) {
                MoPubLog.log(this.getAdNetworkId(), MoPubLog.AdapterLogEvent.LOAD_FAILED, new Object[]{ADAPTER_NAME, MoPubErrorCode.NETWORK_NO_FILL.getIntCode(), MoPubErrorCode.NETWORK_NO_FILL});
                MoPubRewardedVideoManager.onRewardedVideoLoadFailure(CSJRewardedVideo.class, this.getAdNetworkId(), MoPubErrorCode.NETWORK_NO_FILL);
                return false;
            } else {
                this.mCSJAdapterConfiguration.setCachedInitializationParameters(launcherActivity, serverExtras);
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void loadWithSdkInitialized(@NonNull Activity activity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        Log.i(TAG, "loadWithSdkInitialized: 11111111111");
        if (extrasAreValid(serverExtras)) {
            Log.i(TAG, "loadWithSdkInitialized: 2222222222222   mAdUnitRewardId:"+ mAdUnitRewardId);
            //4.解析出服务端id
            mAdUnitRewardId = serverExtras.get(AD_UNIT_REWARD_ID_KEY);
            String adOrientation = serverExtras.get(APP_AD_ORIENTATION).trim();
            loadAd(mAdUnitRewardId, Integer.parseInt(adOrientation));
//            loadAd(adUnitId, TTAdConstant.VERTICAL);
        }
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
        if(isReady()){
            mttRewardVideoAd.showRewardVideoAd(mActivity);
        }else {
            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(CSJRewardedVideo.class,mAdUnitRewardId,getMoPubErrorCode(0));
        }
    }

    //3.解析服务端id
    private boolean extrasAreValid(Map<String, String> serverExtras) {
        return serverExtras.containsKey(AD_UNIT_REWARD_ID_KEY);
    }

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

    private boolean mHasShowDownloadActive = false;

    private void loadAd(String codeId, int orientation) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(3)  //奖励的数量
                .setUserID("user123")//用户id,必传参数
                .setMediaExtra("media_extra") //附加参数，可选
                .setOrientation(orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.i(TAG, "onError: " + " errorcode:" + code + " errormessage:" + message);
                MoPubRewardedVideoManager.onRewardedVideoLoadFailure(CSJRewardedVideo.class, mAdUnitRewardId, getMoPubErrorCode(code));
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                Log.i(TAG, "onRewardVideoCached: ");
                isTTRewardVideoLoaded = true;
                MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(CSJRewardedVideo.class, mAdUnitRewardId);
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                Log.i(TAG, "onRewardVideoAdLoad: ");
                mttRewardVideoAd = ad;
//                MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(CSJRewardedVideo.class, mAdUnitRewardId);
                isTTRewardVideoLoaded = true;
//                mttRewardVideoAd.setShowDownLoadBar(false);
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                        Log.i(TAG, "onAdShow: ");
                        MoPubRewardedVideoManager.onRewardedVideoStarted(CSJRewardedVideo.class, mAdUnitRewardId);
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Log.i(TAG, "onAdVideoBarClick: ");
                    }

                    @Override
                    public void onAdClose() {
                        Log.i(TAG, "onAdClose: ");
                        MoPubRewardedVideoManager.onRewardedVideoClosed(CSJRewardedVideo.class, mAdUnitRewardId);
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        Log.i(TAG, "onVideoComplete: ");
                        MoPubRewardedVideoManager.onRewardedVideoCompleted(CSJRewardedVideo.class, mAdUnitRewardId, MoPubReward.success("钻石奖励", 5));
                    }

                    @Override
                    public void onVideoError() {
                        Log.i(TAG, "onVideoError: reward video play failed");
                        MoPubRewardedVideoManager.onRewardedVideoPlaybackError(CSJRewardedVideo.class,mAdUnitRewardId, MoPubErrorCode.VIDEO_PLAYBACK_ERROR);

                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        Log.i(TAG, "onRewardVerify: ");
                    }
                });
            }
        });
    }
}
