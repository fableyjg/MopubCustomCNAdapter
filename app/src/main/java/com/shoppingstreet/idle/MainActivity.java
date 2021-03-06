package com.shoppingstreet.idle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.CSJAdapterConfiguration;
import com.mopub.mobileads.GDTAdapterConfiguration;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.mobileads.SigmobAdapterConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TTAdNative mTTAdNative;
    private TTRewardVideoAd mttRewardVideoAd;
    private TTFullScreenVideoAd mTTFullScreenVideoAd;
//Shopping mall 测试id
//    private String rewardId = "22bba3a92899446f9c8db8a43217b9c0";
//    private String interId = "bd4b5c57ec774540abb73dfa938a6a90";
//fish go 测试id
    private String rewardId = "39d12e9480d84b2a8da067d2554f390b";
    private String interId = "c6f312ab19024d1bb5b439523f1f2e2a";
    private boolean isRewardLoaded = false;

    private MoPubInterstitial moPubInterstitial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_inter_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(mTTFullScreenVideoAd!=null){
//                    mTTFullScreenVideoAd.showFullScreenVideoAd(MainActivity.this);
//                }else {
//                    Log.i(TAG, "onClick: inter ad not loaded");
//                }
                if(moPubInterstitial!=null){
                    moPubInterstitial.show();
                }else {
                    Log.i(TAG, "onClick: mopub inter ad not loaded");
                }
            }
        });

        findViewById(R.id.btn_reward_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(mttRewardVideoAd!=null){
//                    mttRewardVideoAd.showRewardVideoAd(MainActivity.this);
//                }else {
//                    Log.i(TAG, "onClick: reward ad not loaded");
//                }
                if(isRewardLoaded){
                    MoPubRewardedVideos.showRewardedVideo(rewardId);
                }else {
                    Log.i(TAG, "onClick: mopub reward ad not loaded");
                }
            }
        });

        MopubInit();
//        CSJInit(this);
//        CSJLoadRewardAd(this);
//        CSJLoadInterAd(this);
    }

    private void MopubInit(){
        //這裡不添加，不初始化 csj需要手動配置
        Map<String, String> mediatedNetworkConfiguration1 = new HashMap<>();
        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(rewardId)
                .withMediationSettings()
                .withAdditionalNetwork(CSJAdapterConfiguration.class.getName())
                .withAdditionalNetwork(GDTAdapterConfiguration.class.getName())
                .withAdditionalNetwork(SigmobAdapterConfiguration.class.getName())
                .withMediatedNetworkConfiguration(CSJAdapterConfiguration.class.getName(), mediatedNetworkConfiguration1)
                .withMediatedNetworkConfiguration(GDTAdapterConfiguration.class.getName(), mediatedNetworkConfiguration1)
                .withMediatedNetworkConfiguration(SigmobAdapterConfiguration.class.getName(), mediatedNetworkConfiguration1)
                .withLogLevel(MoPubLog.LogLevel.DEBUG)
                .withLegitimateInterestAllowed(false)
                .build();

        MoPub.setLocationAwareness(MoPub.LocationAwareness.TRUNCATED);
        MoPub.setLocationPrecision(4);
        MoPub.initializeSdk(this, sdkConfiguration, initSdkListener());
    }

    private SdkInitializationListener initSdkListener() {
        return new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
           /* MoPub SDK initialized.
           Check if you should show the consent dialog here, and make your ad requests. */
//                setRewardListener();
//                MopubLoadReward();

                setInterListener();
                MopubLoadInter();
            }
        };
    }

    private void MopubLoadReward(){
        MoPubRewardedVideos.loadRewardedVideo(rewardId);
    }

    private void setRewardListener(){
        MoPubRewardedVideos.setRewardedVideoListener(new MoPubRewardedVideoListener() {
            @Override
            public void onRewardedVideoLoadSuccess(String adUnitId) {
                Log.i(TAG, "onRewardedVideoLoadSuccess: ");
                isRewardLoaded = true;
            }

            @Override
            public void onRewardedVideoLoadFailure(String adUnitId, MoPubErrorCode errorCode) {
                Log.i(TAG, "onRewardedVideoLoadFailure: adUnitId:"+adUnitId + " errorCode:"+errorCode );
                isRewardLoaded = false;
            }

            @Override
            public void onRewardedVideoStarted(String adUnitId) {
                Log.i(TAG, "onRewardedVideoStarted: ");
            }

            @Override
            public void onRewardedVideoPlaybackError(String adUnitId, MoPubErrorCode errorCode) {
                Log.i(TAG, "onRewardedVideoPlaybackError: ");
            }

            @Override
            public void onRewardedVideoClicked(String adUnitId) {
                Log.i(TAG, "onRewardedVideoClicked: ");
            }

            @Override
            public void onRewardedVideoClosed(String adUnitId) {
                Log.i(TAG, "onRewardedVideoClosed: ");
                isRewardLoaded = false;
            }

            @Override
            public void onRewardedVideoCompleted(Set<String> adUnitIds, MoPubReward reward) {
                Log.i(TAG, "onRewardedVideoCompleted: ");
            }
        });
    }

    private void MopubLoadInter(){
        if(moPubInterstitial!=null){
            moPubInterstitial.load();
        }
    }

    private void setInterListener(){
        moPubInterstitial = new MoPubInterstitial(this,interId);
        moPubInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
            @Override
            public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                Log.i(TAG, "onInterstitialLoaded: ");
            }

            @Override
            public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                Log.i(TAG, "onInterstitialFailed: " + errorCode);
            }

            @Override
            public void onInterstitialShown(MoPubInterstitial interstitial) {
                Log.i(TAG, "onInterstitialShown: ");
            }

            @Override
            public void onInterstitialClicked(MoPubInterstitial interstitial) {
                Log.i(TAG, "onInterstitialClicked: ");
            }

            @Override
            public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                Log.i(TAG, "onInterstitialDismissed: ");
            }
        });
    }

    private void CSJInit(Context mContext){
        TTAdSdk.init(mContext,
                new TTAdConfig.Builder()
                        .appId("5001121")//应用ID
                        .useTextureView(false) // Use TextureView to play the video. The default setting is SurfaceView, when the context is in conflict with SurfaceView, you can use TextureView
                        .appName("FishgoCN#154#Android_android")
                        .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                        .allowShowPageWhenScreenLock(true) // Allow or deny permission to display the landing page ad in the lock screen
                        .debug(true) // Turn it on during the testing phase, you can troubleshoot with the log, remove it after launching the app
                        .coppa(0) // Fields to indicate whether you are a child or an adult ，0:adult ，1:child
                        .setGDPR(0)//Fields to indicate whether you are protected by GDPR,  the value of GDPR : 0 close GDRP Privacy protection ，1: open GDRP Privacy protection
                        .supportMultiProcess(false) // Whether to support multi-process, true indicates support
                        .build());
    }

    private void CSJLoadRewardAd(Activity mActivity){
        AdSlot adSlot = new AdSlot.Builder ()
                // Required parameter, set your CodeId
                .setCodeId ("901121430")
                // Required parameter, set the maximum size of the ad image and the desired aspect ratio of the image, in units of Px
                // Note: If you select a native ad on the Pangle, the returned image size may differ significantly from the size you expect
                .setImageAcceptedSize (640, 320)
                // Optional parameter, allow or deny permission to support deeplink
                .setSupportDeepLink (true)
                // Optional parameter, set the number of ads returned per request for in-feed ads, up to 3
                .setAdCount ( 2 )
                // Required parameter for native ad requests, choose TYPE_BANNER or TYPE_INTERACTION_AD
//                .setNativeAdType (AdSlot.TYPE_BANNER )
                // Parameter for rewarded video ad requests, name of the reward
                .setRewardName ( "gold coin" )
                // The number of rewards in rewarded video ad
                .setRewardAmount ( 3 )
                // User ID, a required parameter for rewarded video ads
                // It is developer's unique identifier for users; sdk pass-through is not necessary if the server is not in callback mode, it can be set to an empty string
                .setUserID ( "" )
                // Set how you wish the video ad to be displayed, choose from TTAdConstant.HORIZONTAL or TTAdConstant.VERTICAL
                .setOrientation (TTAdConstant.VERTICAL)
                // Pass-through parameters and strings of rewards in rewarded video ad, if you use json object, you must use serialization as String type, it can be empty
                .setMediaExtra ( "media_extra")
                .build ();

        mTTAdNative = TTAdSdk.getAdManager().createAdNative(mActivity);
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError: " + s);
            }

            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
                Log.i(TAG, "onRewardVideoAdLoad: ");
                mttRewardVideoAd = ttRewardVideoAd;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                        Log.i(TAG, "onAdShow: ");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Log.i(TAG, "onAdVideoBarClick: ");
                    }

                    @Override
                    public void onAdClose() {
                        Log.i(TAG, "onAdClose: ");
                    }

                    @Override
                    public void onVideoComplete() {
                        Log.i(TAG, "onVideoComplete: ");
                    }

                    @Override
                    public void onVideoError() {
                        Log.i(TAG, "onVideoError: ");
                    }

                    @Override
                    public void onRewardVerify(boolean b, int i, String s) {
                        Log.i(TAG, "onRewardVerify: ");
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.i(TAG, "onSkippedVideo: ");
                    }
                });
            }

            @Override
            public void onRewardVideoCached() {
                Log.i(TAG, "onRewardVideoCached: ");
            }
        });
    }

    private void CSJLoadInterAd(Activity mActivity){
        // Set the ad parameters
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("901121375")
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setOrientation(TTAdConstant.VERTICAL)
                .build();

        // Load full-screen video ad
        mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError: " + s);
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ttFullScreenVideoAd) {
                mTTFullScreenVideoAd = ttFullScreenVideoAd;
                mTTFullScreenVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                        Log.i(TAG, "onAdShow: ");
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Log.i(TAG, "onAdVideoBarClick: ");
                    }

                    @Override
                    public void onAdClose() {
                        Log.i(TAG, "onAdClose: ");
                    }

                    @Override
                    public void onVideoComplete() {
                        Log.i(TAG, "onVideoComplete: ");
                    }

                    @Override
                    public void onSkippedVideo() {
                        Log.i(TAG, "onSkippedVideo: ");
                    }
                });
            }

            @Override
            public void onFullScreenVideoCached() {
                Log.i(TAG, "onFullScreenVideoCached: ");
            }
        });
    }
}
