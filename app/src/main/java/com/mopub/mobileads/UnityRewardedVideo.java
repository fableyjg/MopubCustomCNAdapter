//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mopub.mobileads;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mopub.common.BaseLifecycleListener;
import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPubReward;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.logging.MoPubLog.AdapterLogEvent;
import com.mopub.mobileads.UnityRouter.UnityAdsUtils;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAds.FinishState;
import com.unity3d.ads.UnityAds.PlacementState;
import com.unity3d.ads.UnityAds.UnityAdsError;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;
import com.unity3d.ads.metadata.MediationMetaData;
import java.util.Map;

public class UnityRewardedVideo extends CustomEventRewardedVideo implements IUnityAdsExtendedListener {
    private static final String TAG = "Unity RV yjg";
    private static final LifecycleListener sLifecycleListener = new UnityRewardedVideo.UnityLifecycleListener();
    private static final String ADAPTER_NAME = UnityRewardedVideo.class.getSimpleName();
    @NonNull
    private String mPlacementId = "";
    @NonNull
    private UnityAdsAdapterConfiguration mUnityAdsAdapterConfiguration = new UnityAdsAdapterConfiguration();
    @Nullable
    private Activity mLauncherActivity;
    private int impressionOrdinal;
    private int missedImpressionOrdinal;

    @NonNull
    public LifecycleListener getLifecycleListener() {
        Log.i(TAG, "getLifecycleListener: ");
        return sLifecycleListener;
    }

    @NonNull
    public String getAdNetworkId() {
        Log.i(TAG, "getAdNetworkId: ");
        return this.mPlacementId;
    }

    public UnityRewardedVideo() {
    }

    public boolean checkAndInitializeSdk(@NonNull Activity launcherActivity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        Log.i(TAG, "checkAndInitializeSdk: ");
        Class var4 = UnityRewardedVideo.class;
        synchronized(UnityRewardedVideo.class) {
            this.mPlacementId = UnityRouter.placementIdForServerExtras(serverExtras, this.mPlacementId);
            if (UnityAds.isInitialized()) {
                return false;
            } else {
                this.mUnityAdsAdapterConfiguration.setCachedInitializationParameters(launcherActivity, serverExtras);
                UnityRouter.getInterstitialRouter().setCurrentPlacementId(this.mPlacementId);
                if (UnityRouter.initUnityAds(serverExtras, launcherActivity)) {
                    UnityRouter.getInterstitialRouter().addListener(this.mPlacementId, this);
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    protected void loadWithSdkInitialized(@NonNull Activity activity, @NonNull Map<String, Object> localExtras, @NonNull Map<String, String> serverExtras) throws Exception {
        Log.i(TAG, "loadWithSdkInitialized: ");
        this.mPlacementId = UnityRouter.placementIdForServerExtras(serverExtras, this.mPlacementId);
        this.mLauncherActivity = activity;
        UnityRouter.getInterstitialRouter().addListener(this.mPlacementId, this);
        UnityAds.load(this.mPlacementId);
    }

    public boolean hasVideoAvailable() {
        Log.i(TAG, "hasVideoAvailable: ");
        return UnityAds.isReady(this.mPlacementId);
    }

    public void showVideo() {
        Log.i(TAG, "showVideo: ");
        MoPubLog.log(AdapterLogEvent.SHOW_ATTEMPTED, new Object[]{ADAPTER_NAME});
        MediationMetaData metadata;
        if (UnityAds.isReady(this.mPlacementId) && this.mLauncherActivity != null) {
            metadata = new MediationMetaData(this.mLauncherActivity);
            metadata.setOrdinal(++this.impressionOrdinal);
            metadata.commit();
            UnityAds.show(this.mLauncherActivity, this.mPlacementId);
        } else {
            metadata = new MediationMetaData(this.mLauncherActivity);
            metadata.setMissedImpressionOrdinal(++this.missedImpressionOrdinal);
            metadata.commit();
            MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Attempted to show Unity rewarded video before it was available."});
            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(UnityRewardedVideo.class, this.mPlacementId, MoPubErrorCode.NETWORK_NO_FILL);
            MoPubLog.log(AdapterLogEvent.SHOW_FAILED, new Object[]{ADAPTER_NAME, MoPubErrorCode.NETWORK_NO_FILL.getIntCode(), MoPubErrorCode.NETWORK_NO_FILL});
        }

    }

    protected void onInvalidate() {
        Log.i(TAG, "onInvalidate: ");
        UnityRouter.getInterstitialRouter().removeListener(this.mPlacementId);
    }

    public void onUnityAdsClick(String placementId) {
        Log.i(TAG, "onUnityAdsClick: ");
        MoPubRewardedVideoManager.onRewardedVideoClicked(UnityRewardedVideo.class, placementId);
        MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity rewarded video clicked for placement " + placementId + "."});
        MoPubLog.log(AdapterLogEvent.CLICKED, new Object[]{ADAPTER_NAME});
    }

    public void onUnityAdsPlacementStateChanged(String placementId, PlacementState oldState, PlacementState newState) {
        if (placementId.equals(this.mPlacementId) && newState == PlacementState.NO_FILL) {
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(UnityRewardedVideo.class, this.mPlacementId, MoPubErrorCode.NETWORK_NO_FILL);
            MoPubLog.log(AdapterLogEvent.LOAD_FAILED, new Object[]{ADAPTER_NAME, MoPubErrorCode.NETWORK_NO_FILL.getIntCode(), MoPubErrorCode.NETWORK_NO_FILL});
            UnityRouter.getInterstitialRouter().removeListener(this.mPlacementId);
        }

    }

    public void onUnityAdsReady(String placementId) {
        if (placementId.equals(this.mPlacementId)) {
            MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity rewarded video cached for placement " + placementId + "."});
            MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(UnityRewardedVideo.class, placementId);
            MoPubLog.log(AdapterLogEvent.LOAD_SUCCESS, new Object[]{ADAPTER_NAME});
        }
    }

    public void onUnityAdsStart(String placementId) {
        MoPubRewardedVideoManager.onRewardedVideoStarted(UnityRewardedVideo.class, this.mPlacementId);
        MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity rewarded video started for placement " + this.mPlacementId + "."});
        MoPubLog.log(AdapterLogEvent.SHOW_SUCCESS, new Object[]{ADAPTER_NAME});
    }

    public void onUnityAdsFinish(String placementId, FinishState finishState) {
        MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity Ad finished with finish state = " + finishState});
        if (finishState == FinishState.ERROR) {
            MoPubRewardedVideoManager.onRewardedVideoPlaybackError(UnityRewardedVideo.class, this.mPlacementId, MoPubErrorCode.NETWORK_NO_FILL);
            MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity rewarded video encountered a playback error for placement " + placementId});
            MoPubLog.log(AdapterLogEvent.SHOW_FAILED, new Object[]{ADAPTER_NAME, MoPubErrorCode.NETWORK_NO_FILL.getIntCode(), MoPubErrorCode.NETWORK_NO_FILL});
        } else if (finishState == FinishState.COMPLETED) {
            MoPubLog.log(AdapterLogEvent.SHOULD_REWARD, new Object[]{ADAPTER_NAME, -123, ""});
            MoPubRewardedVideoManager.onRewardedVideoCompleted(UnityRewardedVideo.class, this.mPlacementId, MoPubReward.success("", -123));
            MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity rewarded video completed for placement " + placementId});
        } else if (finishState == FinishState.SKIPPED) {
            MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity ad was skipped, no reward will be given."});
        }

        MoPubRewardedVideoManager.onRewardedVideoClosed(UnityRewardedVideo.class, this.mPlacementId);
        UnityRouter.getInterstitialRouter().removeListener(placementId);
    }

    public void onUnityAdsError(UnityAdsError unityAdsError, String message) {
        MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity rewarded video cache failed for placement " + this.mPlacementId + "." + message});
        MoPubErrorCode errorCode = UnityAdsUtils.getMoPubErrorCode(unityAdsError);
        MoPubRewardedVideoManager.onRewardedVideoLoadFailure(UnityRewardedVideo.class, this.mPlacementId, errorCode);
        MoPubLog.log(this.getAdNetworkId(), AdapterLogEvent.LOAD_FAILED, new Object[]{ADAPTER_NAME, errorCode.getIntCode(), errorCode});
    }

    private static final class UnityLifecycleListener extends BaseLifecycleListener {
        private UnityLifecycleListener() {
        }

        public void onCreate(@NonNull Activity activity) {
            super.onCreate(activity);
        }

        public void onResume(@NonNull Activity activity) {
            super.onResume(activity);
        }
    }
}
