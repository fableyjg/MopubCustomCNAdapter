//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.logging.MoPubLog.AdapterLogEvent;
import com.mopub.mobileads.CustomEventBanner.CustomEventBannerListener;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import com.unity3d.services.banners.BannerView.IListener;
import java.util.Map;

public class UnityBanner extends CustomEventBanner implements IListener {
    private static final String ADAPTER_NAME = UnityBanner.class.getSimpleName();
    private static String placementId = "banner";
    private CustomEventBannerListener customEventBannerListener;
    private BannerView mBannerView;
    private int adWidth;
    private int adHeight;
    @NonNull
    private UnityAdsAdapterConfiguration mUnityAdsAdapterConfiguration = new UnityAdsAdapterConfiguration();

    public UnityBanner() {
    }

    protected void loadBanner(Context context, CustomEventBannerListener customEventBannerListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        if (!(context instanceof Activity)) {
            MoPubLog.log(getAdNetworkId(), AdapterLogEvent.LOAD_FAILED, new Object[]{ADAPTER_NAME, MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR.getIntCode(), MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR});
            customEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
        } else {
            this.mUnityAdsAdapterConfiguration.setCachedInitializationParameters(context, serverExtras);
            placementId = UnityRouter.placementIdForServerExtras(serverExtras, placementId);
            this.customEventBannerListener = customEventBannerListener;
            String format = (String)serverExtras.get("adunit_format");
            boolean isMediumRectangleFormat = format.contains("medium_rectangle");
            if (isMediumRectangleFormat) {
                MoPubLog.log(getAdNetworkId(), AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Unity Ads does not support medium rectangle ads."});
                if (customEventBannerListener != null) {
                    customEventBannerListener.onBannerFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
                }

            } else {
                if (UnityRouter.initUnityAds(serverExtras, (Activity)context)) {
                    if (localExtras != null && !localExtras.isEmpty()) {
                        UnityBannerSize bannerSize = this.unityAdsAdSizeFromLocalExtras(context, localExtras);
                        if (this.mBannerView != null) {
                            this.mBannerView.destroy();
                            this.mBannerView = null;
                        }

                        this.mBannerView = new BannerView((Activity)context, placementId, bannerSize);
                        this.mBannerView.setListener(this);
                        this.mBannerView.load();
                        MoPubLog.log(getAdNetworkId(), AdapterLogEvent.LOAD_ATTEMPTED, new Object[]{ADAPTER_NAME});
                    } else {
                        MoPubLog.log(getAdNetworkId(), AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Failed to get banner size because the localExtras is empty."});
                        if (customEventBannerListener != null) {
                            customEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
                        }
                    }
                } else {
                    MoPubLog.log(getAdNetworkId(), AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "Failed to initialize Unity Ads"});
                    MoPubLog.log(getAdNetworkId(), AdapterLogEvent.LOAD_FAILED, new Object[]{ADAPTER_NAME, MoPubErrorCode.NETWORK_NO_FILL.getIntCode(), MoPubErrorCode.NETWORK_NO_FILL});
                    if (customEventBannerListener != null) {
                        customEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
                    }
                }

            }
        }
    }

    private UnityBannerSize unityAdsAdSizeFromLocalExtras(Context context, Map<String, Object> localExtras) {
        Object adWidthObject = localExtras.get("com_mopub_ad_width");
        if (adWidthObject instanceof Integer) {
            this.adWidth = (Integer)adWidthObject;
        }

        Object adHeightObject = localExtras.get("com_mopub_ad_height");
        if (adHeightObject instanceof Integer) {
            this.adHeight = (Integer)adHeightObject;
        }

        if (this.adWidth >= 728 && this.adHeight >= 90) {
            return new UnityBannerSize(728, 90);
        } else {
            return this.adWidth >= 468 && this.adHeight >= 60 ? new UnityBannerSize(468, 60) : new UnityBannerSize(320, 50);
        }
    }

    protected void onInvalidate() {
        if (this.mBannerView != null) {
            this.mBannerView.destroy();
        }

        this.mBannerView = null;
        this.customEventBannerListener = null;
    }

    public void onBannerLoaded(BannerView bannerView) {
        MoPubLog.log(getAdNetworkId(), AdapterLogEvent.LOAD_SUCCESS, new Object[]{ADAPTER_NAME});
        MoPubLog.log(getAdNetworkId(), AdapterLogEvent.SHOW_ATTEMPTED, new Object[]{ADAPTER_NAME});
        MoPubLog.log(getAdNetworkId(), AdapterLogEvent.SHOW_SUCCESS, new Object[]{ADAPTER_NAME});
        if (this.customEventBannerListener != null) {
            this.customEventBannerListener.onBannerLoaded(bannerView);
            this.mBannerView = bannerView;
        }

    }

    public void onBannerClick(BannerView bannerView) {
        MoPubLog.log(getAdNetworkId(), AdapterLogEvent.CLICKED, new Object[]{ADAPTER_NAME});
        if (this.customEventBannerListener != null) {
            this.customEventBannerListener.onBannerClicked();
        }

    }

    public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo errorInfo) {
        MoPubLog.log(getAdNetworkId(), AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, String.format("Banner did error for placement %s with error %s", placementId, errorInfo.errorMessage)});
        if (this.customEventBannerListener != null) {
            this.customEventBannerListener.onBannerFailed(MoPubErrorCode.NETWORK_NO_FILL);
        }

    }

    public void onBannerLeftApplication(BannerView bannerView) {
        MoPubLog.log(getAdNetworkId(), AdapterLogEvent.WILL_LEAVE_APPLICATION, new Object[]{ADAPTER_NAME});
        if (this.customEventBannerListener != null) {
            this.customEventBannerListener.onLeaveApplication();
        }

    }

    private static String getAdNetworkId() {
        return placementId;
    }
}
