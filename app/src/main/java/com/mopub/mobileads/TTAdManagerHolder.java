package com.mopub.mobileads;

import android.content.Context;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;

public class TTAdManagerHolder {

    private static final String TAG = "TTAdManagerHolder_rv";
    private static boolean sInit;

    public static TTAdManager get() {
        if (!sInit) {
            throw new RuntimeException("TTAdSdk is not init, please check.");
        }
        return TTAdSdk.getAdManager();
    }

    public static void init(Context context, String appId, String appName) {
        doInit(context,appId,appName);
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(Context context,String appId,String appName) {
        Log.i(TAG, "doInit: ");
        if (!sInit) {
            Log.i(TAG, "real init ");
            TTAdSdk.init(context, buildConfig(context,appId,appName));
            sInit = true;
        }
    }

    private static TTAdConfig buildConfig(Context context, String appId, String appName) {
        return new TTAdConfig.Builder()
                .appId(appId)//应用ID
                .useTextureView(false) // Use TextureView to play the video. The default setting is SurfaceView, when the context is in conflict with SurfaceView, you can use TextureView
                .appName(appName)
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                .allowShowPageWhenScreenLock(true) // Allow or deny permission to display the landing page ad in the lock screen
                .debug(true) // Turn it on during the testing phase, you can troubleshoot with the log, remove it after launching the app
                .coppa(0) // Fields to indicate whether you are a child or an adult ，0:adult ，1:child
                .setGDPR(0)//Fields to indicate whether you are protected by GDPR,  the value of GDPR : 0 close GDRP Privacy protection ，1: open GDRP Privacy protection
                .supportMultiProcess(false) // Whether to support multi-process, true indicates support
                .build();


    }
}
