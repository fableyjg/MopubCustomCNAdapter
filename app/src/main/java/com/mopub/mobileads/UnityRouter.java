//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mopub.common.MoPub;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.logging.MoPubLog.AdapterLogEvent;
import com.mopub.common.privacy.ConsentStatus;
import com.mopub.common.privacy.PersonalInfoManager;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAds.UnityAdsError;
import com.unity3d.ads.metadata.MediationMetaData;
import com.unity3d.ads.metadata.MetaData;
import java.util.Map;

public class UnityRouter {
    private static final String GAME_ID_KEY = "gameId";
    private static final String ZONE_ID_KEY = "zoneId";
    private static final String PLACEMENT_ID_KEY = "placementId";
    private static final String ADAPTER_NAME = UnityRouter.class.getSimpleName();
    private static final UnityInterstitialCallbackRouter interstitialRouter = new UnityInterstitialCallbackRouter();

    public UnityRouter() {
    }

    static boolean initUnityAds(Map<String, String> serverExtras, Activity launcherActivity) {
        initGdpr(launcherActivity);
        String gameId = (String)serverExtras.get("gameId");
        Log.i("unity init ads yjg", "initUnityAds: gameId" + gameId);
        if (gameId != null && !gameId.isEmpty()) {
            initMediationMetadata(launcherActivity);
            boolean testMode = false;
            boolean enablePerPlacementLoad = true;
            UnityAds.initialize(launcherActivity, gameId, interstitialRouter, testMode, enablePerPlacementLoad);
            return true;
        } else {
            MoPubLog.log(AdapterLogEvent.CUSTOM, new Object[]{ADAPTER_NAME, "gameId is missing or entered incorrectly in the MoPub UI"});
            return false;
        }
    }

    static void initGdpr(Activity activity) {
        PersonalInfoManager personalInfoManager = MoPub.getPersonalInformationManager();
        boolean canCollectPersonalInfo = MoPub.canCollectPersonalInformation();
        boolean shouldAllowLegitimateInterest = MoPub.shouldAllowLegitimateInterest();
        if (personalInfoManager != null && personalInfoManager.gdprApplies() == Boolean.TRUE) {
            MetaData gdprMetaData = new MetaData(activity.getApplicationContext());
            if (shouldAllowLegitimateInterest) {
                if (personalInfoManager.getPersonalInfoConsentStatus() != ConsentStatus.EXPLICIT_NO && personalInfoManager.getPersonalInfoConsentStatus() != ConsentStatus.DNT) {
                    gdprMetaData.set("gdpr.consent", true);
                } else {
                    gdprMetaData.set("gdpr.consent", false);
                }
            } else {
                gdprMetaData.set("gdpr.consent", canCollectPersonalInfo);
            }

            gdprMetaData.commit();
        }

    }

    static void initMediationMetadata(Context context) {
        MediationMetaData mediationMetaData = new MediationMetaData(context);
        mediationMetaData.setName("MoPub");
        mediationMetaData.setVersion("5.10.0");
        mediationMetaData.set("adapter_version", "3.4.2.0");
        mediationMetaData.commit();
    }

    static String placementIdForServerExtras(Map<String, String> serverExtras, String defaultPlacementId) {
        String placementId = null;
        if (serverExtras.containsKey("placementId")) {
            placementId = (String)serverExtras.get("placementId");
        } else if (serverExtras.containsKey("zoneId")) {
            placementId = (String)serverExtras.get("zoneId");
        }

        return TextUtils.isEmpty(placementId) ? defaultPlacementId : placementId;
    }

    static UnityInterstitialCallbackRouter getInterstitialRouter() {
        return interstitialRouter;
    }

    static final class UnityAdsUtils {
        UnityAdsUtils() {
        }

        static MoPubErrorCode getMoPubErrorCode(UnityAdsError unityAdsError) {
            MoPubErrorCode errorCode;
            switch(unityAdsError) {
            case VIDEO_PLAYER_ERROR:
                errorCode = MoPubErrorCode.VIDEO_PLAYBACK_ERROR;
                break;
            case INVALID_ARGUMENT:
                errorCode = MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR;
                break;
            case INTERNAL_ERROR:
                errorCode = MoPubErrorCode.NETWORK_INVALID_STATE;
                break;
            default:
                errorCode = MoPubErrorCode.UNSPECIFIED;
            }

            return errorCode;
        }
    }
}
