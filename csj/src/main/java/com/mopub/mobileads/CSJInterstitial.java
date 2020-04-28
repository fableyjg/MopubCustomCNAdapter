package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.bytedance.sdk.openadsdk.TTInteractionAd;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CSJInterstitial extends CustomEventInterstitial {

    private static final String TAG = "CSJInterstitial";

    public static final String AD_UNIT_ID_KEY = "adUnitID";
    public static final String APP_ID_KEY = "appId";
    public static final String APP_NAME_KEY = "appName";
    public static final String APP_AD_ORIENTATION = "adOrientation";

    private static AtomicBoolean sIsInitialized = new AtomicBoolean(false);

    private TTAdNative mTTAdNative;
    private TTFullScreenVideoAd mTTFullScreenVideoAd;
    private Context mContext;

    //1.定义一个插屏回调的接口
    private CustomEventInterstitialListener mInterstitialListener;

    @Override
    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {

        mContext = context;
        mInterstitialListener = customEventInterstitialListener;
        if (!sIsInitialized.getAndSet(true)) {
            //step1:初始化sdk
            String appid = serverExtras.get(APP_ID_KEY);
            String appName = serverExtras.get(APP_NAME_KEY);
            TTAdManagerHolder.init(mContext, appid, appName);

            TTAdManager ttAdManager = TTAdManagerHolder.get();
            //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
            ttAdManager.requestPermissionIfNecessary(mContext);
            //step3:创建TTAdNative对象,用于调用广告请求接口
            mTTAdNative = ttAdManager.createAdNative(mContext);

            if(extrasAreValid(serverExtras)){
                //4.解析出服务端id
                String adUnitId = serverExtras.get(AD_UNIT_ID_KEY);
                String adOrientation = serverExtras.get(APP_AD_ORIENTATION).trim();
                loadAd(adUnitId, Integer.parseInt(adOrientation));
            }
        }
    }

    @Override
    protected void showInterstitial() {
        if(mTTFullScreenVideoAd != null){
            //step6:在获取到广告后展示
            mTTFullScreenVideoAd.showFullScreenVideoAd((Activity) mContext);
            mTTFullScreenVideoAd = null;
        }else {
            Log.d(TAG, "showInterstitial: not loaded toutiao inter");
        }
    }

    @Override
    protected void onInvalidate() {

    }

    //加载广告
    private void loadAd(String codeId, int orientation){
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setOrientation(orientation)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        // Load full-screen video ad
        mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "onError: " + s);
                if(mInterstitialListener!=null){
                    mInterstitialListener.onInterstitialFailed(getMoPubErrorCode(i));
                }
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ttFullScreenVideoAd) {
                //这里代表广告加载，告知mopub广告加载
                if(mInterstitialListener != null){
                    mInterstitialListener.onInterstitialLoaded();
                }
                mTTFullScreenVideoAd = ttFullScreenVideoAd;
                mTTFullScreenVideoAd.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                        Log.i(TAG, "onAdShow: ");
                        if(mInterstitialListener != null){
                            mInterstitialListener.onInterstitialShown();
                        }
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Log.i(TAG, "onAdVideoBarClick: ");
                        if(mInterstitialListener != null){
                            mInterstitialListener.onInterstitialClicked();
                        }
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
                        if(mInterstitialListener!=null){
                            mInterstitialListener.onInterstitialDismissed();
                        }
                    }
                });
            }

            @Override
            public void onFullScreenVideoCached() {
                Log.i(TAG, "onFullScreenVideoCached: ");

            }
        });
    }

    //3.解析服务端id
    private boolean extrasAreValid(Map<String, String> serverExtras){
        return serverExtras.containsKey(AD_UNIT_ID_KEY);
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
