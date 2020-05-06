package com.mopub.mobileads;

import android.content.Context;

import java.util.Map;

public class SigmobInterstitial extends CustomEventInterstitial {

    private static final String TAG = "SigmobInterstitial yjg";

    public static final String AD_UNIT_ID_KEY = "adUnitID";
    public static final String APP_ID_KEY = "appId";
    public static final String APP_NAME_KEY = "appName";
    public static final String APP_AD_ORIENTATION = "adOrientation";

    @Override
    protected void loadInterstitial(Context context, CustomEventInterstitialListener customEventInterstitialListener, Map<String, Object> localExtras, Map<String, String> serverExtras) {

    }

    @Override
    protected void showInterstitial() {

    }

    @Override
    protected void onInvalidate() {

    }
}
