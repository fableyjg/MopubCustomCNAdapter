package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.sigmob.windad.WindAdError;
import com.sigmob.windad.WindAdOptions;
import com.sigmob.windad.WindAds;
import com.sigmob.windad.fullscreenvideo.WindFullScreenAdRequest;
import com.sigmob.windad.fullscreenvideo.WindFullScreenVideoAd;
import com.sigmob.windad.fullscreenvideo.WindFullScreenVideoAdListener;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SigmobInterstitial extends CustomEventInterstitial {

    private static final String TAG = "SigmobInterstitial";

    public static final String AD_UNIT_ID_KEY = "placementId";
    public static final String APP_ID_KEY = "appId";
    public static final String APP_KEY = "appKey";

    private static AtomicBoolean sIsInitialized = new AtomicBoolean(false);
    //1.定义一个插屏回调的接口
    private CustomEventInterstitialListener mInterstitialListener;
    private SigmobAdapterConfiguration mSigmobAdapterConfiguration = new SigmobAdapterConfiguration();

    private Context mContext;
    private WindFullScreenVideoAd windFullScreenVideoAd;
    private String placementId;
    private WindFullScreenAdRequest request;

    @Override
    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {
        Log.i(TAG, "loadInterstitial: ");
        mContext = context;
        mInterstitialListener = customEventInterstitialListener;
        if (!sIsInitialized.getAndSet(true)) {
            Log.i(TAG, "loadInterstitial: 请求插屏");
            //step1:初始化sdk
            String appid = serverExtras.get(APP_ID_KEY);
            String appKey = serverExtras.get(APP_KEY);
            placementId = serverExtras.get(AD_UNIT_ID_KEY);

            Log.i(TAG, "loadInterstitial: appid:"+appid);

//            WindAds ads = WindAds.sharedAds();
//            ads.startWithOptions(mContext,new WindAdOptions(appid,appKey));

            windFullScreenVideoAd = WindFullScreenVideoAd.sharedInstance();

            windFullScreenVideoAd.setWindFullScreenVideoAdListener(new WindFullScreenVideoAdListener() {
                @Override
                public void onFullScreenVideoAdLoadSuccess(String s) {
                    Log.i(TAG, "onFullScreenVideoAdLoadSuccess: ");
                    if(mInterstitialListener != null){
                        mInterstitialListener.onInterstitialLoaded();
                    }
                }

                @Override
                public void onFullScreenVideoAdPreLoadSuccess(String s) {
                    Log.i(TAG, "onFullScreenVideoAdPreLoadSuccess: ");
                }

                @Override
                public void onFullScreenVideoAdPreLoadFail(String s) {
                    Log.i(TAG, "onFullScreenVideoAdPreLoadFail: ");
                }

                @Override
                public void onFullScreenVideoAdPlayStart(String s) {
                    Log.i(TAG, "onFullScreenVideoAdPlayStart: ");
                    if(mInterstitialListener != null){
                        mInterstitialListener.onInterstitialShown();
                    }
                }

                @Override
                public void onFullScreenVideoAdPlayEnd(String s) {
                    Log.i(TAG, "onFullScreenVideoAdPlayEnd: ");
                }

                @Override
                public void onFullScreenVideoAdClicked(String s) {
                    Log.i(TAG, "onFullScreenVideoAdClicked: ");
                    if(mInterstitialListener != null){
                        mInterstitialListener.onInterstitialClicked();
                    }
                }

                @Override
                public void onFullScreenVideoAdClosed(String s) {
                    Log.i(TAG, "onFullScreenVideoAdClosed: ");
                    if(mInterstitialListener != null){
                        mInterstitialListener.onInterstitialDismissed();
                    }
                }

                @Override
                public void onFullScreenVideoAdLoadError(WindAdError windAdError, String s) {
                    Log.i(TAG, "onFullScreenVideoAdLoadError: " +s  + "windAdError:" + windAdError.getMessage().toString());
                    if(mInterstitialListener!=null){
                        mInterstitialListener.onInterstitialFailed(getMoPubErrorCode(3));
                    }
                }

                @Override
                public void onFullScreenVideoAdPlayError(WindAdError windAdError, String s) {
                    Log.i(TAG, "onFullScreenVideoAdPlayError: " + s);
                    if(mInterstitialListener!=null){
                        mInterstitialListener.onInterstitialFailed(getMoPubErrorCode(0));
                    }
                }
            });

            if(serverExtras.containsKey(AD_UNIT_ID_KEY)){
                Log.i(TAG, "loadInterstitial: 加载插屏");
                mSigmobAdapterConfiguration.setCachedInitializationParameters(context, serverExtras);

                //4.解析出服务端id
                String adUnitId = serverExtras.get(AD_UNIT_ID_KEY);

                request = new WindFullScreenAdRequest(adUnitId,null,null);
                windFullScreenVideoAd.loadAd(request);
            }else {
                Log.i(TAG, "loadInterstitial: 没有加载插屏");
            }
        }else {
            Log.i(TAG, "loadInterstitial: 没有请求插屏");
        }
    }

    @Override
    protected void showInterstitial() {
        Log.i(TAG, "showInterstitial: ");
        if(windFullScreenVideoAd.isReady(placementId)){
            windFullScreenVideoAd.show((Activity) mContext,request);
        }else {
            Log.i(TAG, "showInterstitial: failed");
            if(mInterstitialListener!=null){
                mInterstitialListener.onInterstitialFailed(getMoPubErrorCode(0));
            }
        }
    }

    @Override
    protected void onInvalidate() {

    }

    //6.定义mopub的errorcode
    private MoPubErrorCode getMoPubErrorCode(int error) {
        MoPubErrorCode errorCode;
        switch(error) {
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
}
