//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.logging.MoPubLog.AdapterLogEvent;
import com.mopub.mobileads.CustomEventInterstitial.CustomEventInterstitialListener;
import com.mopub.mobileads.UnityRouter.UnityAdsUtils;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAds.FinishState;
import com.unity3d.ads.UnityAds.PlacementState;
import com.unity3d.ads.UnityAds.UnityAdsError;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;
import com.unity3d.ads.metadata.MediationMetaData;
import java.util.Map;

public class UnityInterstitial extends CustomEventInterstitial implements IUnityAdsExtendedListener {
    private static final String TAG = "Unity yjg";
    private static final String ADAPTER_NAME = UnityInterstitial.class.getSimpleName();
    private CustomEventInterstitialListener mCustomEventInterstitialListener;
    private Context mContext;
    private String mPlacementId = "video";
    private int impressionOrdinal;
    private int missedImpressionOrdinal;
    @NonNull
    private UnityAdsAdapterConfiguration mUnityAdsAdapterConfiguration = new UnityAdsAdapterConfiguration();

    public UnityInterstitial() {
    }

    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        Log.i(TAG, "loadInterstitial: ");
        this.mPlacementId = UnityRouter.placementIdForServerExtras(serverExtras, this.mPlacementId);
        this.mCustomEventInterstitialListener = customEventInterstitialListener;
        this.mContext = context;
        UnityAds.load(this.mPlacementId);
        this.mUnityAdsAdapterConfiguration.setCachedInitializationParameters(context, serverExtras);
        UnityRouter.getInterstitialRouter().addListener(this.mPlacementId, this);
        UnityRouter.getInterstitialRouter().setCurrentPlacementId(this.mPlacementId);
        this.initializeUnityAdsSdk(serverExtras);
    }

    private void initializeUnityAdsSdk(Map<String, String> serverExtras) {
        if (!UnityAds.isInitialized()) {
            if (!(this.mContext instanceof Activity)) {
                MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Context is null or is not an instanceof Activity."});
                return;
            }

            UnityRouter.initUnityAds(serverExtras, (Activity)this.mContext);
        }

    }

    protected void showInterstitial() {
        MoPubLog.log(AdapterLogEvent.SHOW_ATTEMPTED, new Object[]{ADAPTER_NAME});
        MediationMetaData metadata;
        if (UnityAds.isReady(this.mPlacementId) && this.mContext != null) {
            metadata = new MediationMetaData(this.mContext);
            metadata.setOrdinal(++this.impressionOrdinal);
            metadata.commit();
            UnityAds.show((Activity)this.mContext, this.mPlacementId);
        } else {
            metadata = new MediationMetaData(this.mContext);
            metadata.setMissedImpressionOrdinal(++this.missedImpressionOrdinal);
            metadata.commit();
            MoPubLog.log(AdapterLogEvent.SHOW_FAILED, new Object[]{ADAPTER_NAME, MoPubErrorCode.NETWORK_NO_FILL.getIntCode(), MoPubErrorCode.NETWORK_NO_FILL});
            MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Attempted to show Unity interstitial video before it was available."});
        }

    }

    protected void onInvalidate() {
        UnityRouter.getInterstitialRouter().removeListener(this.mPlacementId);
        this.mCustomEventInterstitialListener = null;
    }

    public void onUnityAdsReady(String placementId) {
        if (this.mCustomEventInterstitialListener != null) {
            this.mCustomEventInterstitialListener.onInterstitialLoaded();
            MoPubLog.log(AdapterLogEvent.LOAD_SUCCESS, new Object[]{ADAPTER_NAME});
        }

    }

    public void onUnityAdsStart(String placementId) {
        if (this.mCustomEventInterstitialListener != null) {
            this.mCustomEventInterstitialListener.onInterstitialShown();
        }

        MoPubLog.log(AdapterLogEvent.SHOW_SUCCESS, new Object[]{ADAPTER_NAME});
    }

    public void onUnityAdsFinish(String placementId, FinishState finishState) {
        if (this.mCustomEventInterstitialListener != null) {
            if (finishState == FinishState.ERROR) {
                MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity interstitial video encountered a playback error for placement " + placementId});
                this.mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
                MoPubLog.log(AdapterLogEvent.SHOW_FAILED, new Object[]{ADAPTER_NAME, MoPubErrorCode.NETWORK_NO_FILL.getIntCode(), MoPubErrorCode.NETWORK_NO_FILL});
            } else {
                MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity interstitial video completed for placement " + placementId});
                this.mCustomEventInterstitialListener.onInterstitialDismissed();
            }
        }

        UnityRouter.getInterstitialRouter().removeListener(placementId);
    }

    public void onUnityAdsClick(String placementId) {
        if (this.mCustomEventInterstitialListener != null) {
            this.mCustomEventInterstitialListener.onInterstitialClicked();
        }

        MoPubLog.log(AdapterLogEvent.CLICKED, new Object[]{ADAPTER_NAME});
    }

    public void onUnityAdsPlacementStateChanged(String placementId, PlacementState oldState, PlacementState newState) {
        if (placementId.equals(this.mPlacementId) && this.mCustomEventInterstitialListener != null && newState == PlacementState.NO_FILL) {
            this.mCustomEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.NETWORK_NO_FILL);
            UnityRouter.getInterstitialRouter().removeListener(this.mPlacementId);
            MoPubLog.log(AdapterLogEvent.LOAD_FAILED, new Object[]{ADAPTER_NAME, MoPubErrorCode.NETWORK_NO_FILL.getIntCode(), MoPubErrorCode.NETWORK_NO_FILL});
        }

    }

    public void onUnityAdsError(UnityAdsError unityAdsError, String message) {
        if (this.mCustomEventInterstitialListener != null) {
            MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity interstitial video cache failed for placement " + this.mPlacementId + "." + message});
            MoPubErrorCode errorCode = UnityAdsUtils.getMoPubErrorCode(unityAdsError);
            this.mCustomEventInterstitialListener.onInterstitialFailed(errorCode);
            MoPubLog.log(AdapterLogEvent.LOAD_FAILED, new Object[]{ADAPTER_NAME, errorCode.getIntCode(), errorCode});
        }

    }
}
