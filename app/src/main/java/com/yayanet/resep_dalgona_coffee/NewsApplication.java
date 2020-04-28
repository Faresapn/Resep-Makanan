package com.yayanet.resep_dalgona_coffee;

import android.app.Application;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Jati on 2/16/2017.
 */

public class NewsApplication extends Application {
    InterstitialAd interstitialAd;

    synchronized public void loadInterstitial() {
        if (interstitialAd == null || !interstitialAd.isLoaded()) {
            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
            interstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    synchronized public InterstitialAd getInterstitialAd() {
        if (interstitialAd == null) {
            loadInterstitial();
        }
        return interstitialAd;
    }
}
