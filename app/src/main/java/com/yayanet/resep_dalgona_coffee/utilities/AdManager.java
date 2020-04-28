package com.yayanet.resep_dalgona_coffee.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.yayanet.resep_dalgona_coffee.Config;
import com.yayanet.resep_dalgona_coffee.R;
import com.startapp.android.publish.ads.banner.Banner;
import com.startapp.android.publish.adsCommon.StartAppAd;

public class AdManager {
    private Context context;
    private SharedPreferences appData;
    private SharedPreferences.Editor appDataEditor;

    // AdMob
    private String ADMOB_INTERSTITIAL_ID, ADMOB_BANNER_ID;
    private InterstitialAd interstitialAd;
    private AdView adMobBannerMain, adMobBannerExit;
    private FrameLayout bannerMainLayout, bannerExitLayout;
    private View app_bar_main, content_main;
    private Integer COUNTER, MAX_COUNTERs, current_date, date_ads, admob_clicked, max_ads_clicked;

    // StartApp
    private String STARTAPP_ID;
   // private StartAppAd startAppAd;
    private Banner startAppBannerMain, startAppBannerTop, startAppBannerBottom, startAppBannerExit;
    private FrameLayout StartAppBannerTopLayout, startAppBannerBottomLayout;
    private FrameLayout.LayoutParams bannerParam;
    private Integer startapp_clicked, adtype_count;

    public AdManager(Context context){
        super();
        this.context = context;

        // AdMob
        ADMOB_INTERSTITIAL_ID = context.getString(R.string.admob_interstitial_id);
        ADMOB_BANNER_ID = context.getString(R.string.admob_banner_id);
       // MAX_COUNTER = Integer.valueOf(context.getString(R.string.counter_admob));

        // StartApp
        STARTAPP_ID = context.getString(R.string.startapp_app_id);

        // Shared Preferences
        appData = context.getSharedPreferences("APP_DATA", Context.MODE_PRIVATE);
        appDataEditor = appData.edit();

        // Initialize Interstitial
        init_startapp();
        admob_interstitial_request();

    }

    public void admob_interstitial_request(){
        if(ADMOB_INTERSTITIAL_ID.equals("")) return;

        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(ADMOB_INTERSTITIAL_ID);
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {}
            @Override
            public void onAdLeftApplication(){}
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
            @Override
            public void onAdFailedToLoad(int errorCode) {
                interstitialAd.loadAd(new AdRequest.Builder().build());
                Log.e("adMobInt", "onAdFailedToLoad=" + errorCode);
            }
        });
    }

    public void admob_interstitial_show(Context context){
//        if(ADMOB_INTERSTITIAL_ID.equals("")) return;
//
//        COUNTER = appData.getInt("COUNTER", 1);
//        Log.e("COUNTER", "val=" + COUNTER);
//        appDataEditor.putInt("COUNTER", COUNTER+1).apply(); // Counter
//        if(COUNTER >= MAX_COUNTER) appDataEditor.putInt("COUNTER", 1).apply(); // Reset Counter
//        if(COUNTER != 1) return;

        if(Config.hitungAdmob % Integer.parseInt(context.getString(R.string.counter_admob)) == 0) {
            if(interstitialAd.isLoaded()){
                interstitialAd.show();
            }else{
                StartAppAd.showAd(context);
            }
        }
        Config.hitungAdmob++;

//        if (interstitialAd != null && interstitialAd.isLoaded()) {
//            interstitialAd.show();
//        }else{
//            StartAppAd.showAd(context);
//        }
    }

    public void admob_banner(){
        if(ADMOB_BANNER_ID.equals("")) return;

        adMobBannerMain = new AdView(context);
        adMobBannerMain.setAdUnitId(ADMOB_BANNER_ID);
        adMobBannerMain.setAdSize(AdSize.SMART_BANNER);
        adMobBannerMain.loadAd(new AdRequest.Builder().build());
        bannerMainLayout = ((Activity) context).findViewById(R.id.banner_main);
        bannerMainLayout.removeAllViews();
        bannerMainLayout.addView(adMobBannerMain);
        // Ad Listener
        adMobBannerMain.setAdListener(new AdListener() {
            @Override
            public void onAdLeftApplication (){}
            @Override
            public void onAdFailedToLoad (int errorCode){
                Log.e("admob_banner", "AdMob/onAdFailedToLoad=" + errorCode);
                bannerMainLayout.removeAllViews(); // Clear ad space
                startAppBanner(); // Recover to StartApp banner
            }
        });

    }

    public void init_startapp(){
//        if(STARTAPP_ID.equals("")) return;
//        startAppAd = new StartAppAd(context);
//        StartAppSDK.init(context, STARTAPP_ID, true);
//        startAppAd.disableSplash();
    }


    public void startAppBanner(){
        if(STARTAPP_ID.equals("")) return;
        startAppBannerMain = new Banner(context);
        bannerMainLayout = ((Activity) context).findViewById(R.id.banner_main);
        bannerParam = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        bannerParam.gravity = Gravity.BOTTOM;
        bannerMainLayout.removeAllViews();
        // Add banner and remove bottom banner
        bannerMainLayout.addView(startAppBannerMain, bannerParam);
    }



}
