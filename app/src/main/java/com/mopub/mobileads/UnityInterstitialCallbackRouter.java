//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mopub.mobileads;

import com.unity3d.ads.UnityAds.FinishState;
import com.unity3d.ads.UnityAds.PlacementState;
import com.unity3d.ads.UnityAds.UnityAdsError;
import com.unity3d.ads.mediation.IUnityAdsExtendedListener;
import java.util.HashMap;
import java.util.Map;

public class UnityInterstitialCallbackRouter implements IUnityAdsExtendedListener {
    private final Map<String, IUnityAdsExtendedListener> listeners = new HashMap();
    private String currentPlacementId;

    public UnityInterstitialCallbackRouter() {
    }

    public void onUnityAdsReady(String placementId) {
        IUnityAdsExtendedListener listener = (IUnityAdsExtendedListener)this.listeners.get(placementId);
        if (listener != null) {
            listener.onUnityAdsReady(placementId);
        }

    }

    public void onUnityAdsStart(String placementId) {
        IUnityAdsExtendedListener listener = (IUnityAdsExtendedListener)this.listeners.get(placementId);
        if (listener != null) {
            listener.onUnityAdsStart(placementId);
        }

    }

    public void onUnityAdsFinish(String placementId, FinishState finishState) {
        IUnityAdsExtendedListener listener = (IUnityAdsExtendedListener)this.listeners.get(placementId);
        if (listener != null) {
            listener.onUnityAdsFinish(placementId, finishState);
        }

    }

    public void onUnityAdsClick(String placementId) {
        IUnityAdsExtendedListener listener = (IUnityAdsExtendedListener)this.listeners.get(placementId);
        if (listener != null) {
            listener.onUnityAdsClick(placementId);
        }

    }

    public void onUnityAdsPlacementStateChanged(String placementId, PlacementState oldState, PlacementState newState) {
        IUnityAdsExtendedListener listener = (IUnityAdsExtendedListener)this.listeners.get(placementId);
        if (listener != null) {
            listener.onUnityAdsPlacementStateChanged(placementId, oldState, newState);
        }

    }

    public void onUnityAdsError(UnityAdsError unityAdsError, String message) {
        IUnityAdsExtendedListener listener = (IUnityAdsExtendedListener)this.listeners.get(this.currentPlacementId);
        if (listener != null) {
            listener.onUnityAdsError(unityAdsError, message);
        }

    }

    public void addListener(String placementId, IUnityAdsExtendedListener listener) {
        this.listeners.put(placementId, listener);
    }

    public void removeListener(String placementId) {
        this.listeners.remove(placementId);
    }

    public void setCurrentPlacementId(String placementId) {
        this.currentPlacementId = placementId;
    }
}
