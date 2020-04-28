package com.yayanet.resep_dalgona_coffee;

import java.io.Serializable;

public class Config implements Serializable {

    private static final long serialVersionUID = 1L;

    //your admin panel url
    public static final String SERVER_URL = "http://a02.rabikapanguys.com/android_news_app";
    public static final String CATEGORY= "Rabi";
    public static String INTERVAL_AD="1";

    //set true for enable ads, set false for disable ads
    public static final boolean ENABLE_STARTAPP_ADS = false;
    public static final boolean ENABLE_ADMOB_ADS = true;

    //set true if you want to enable RTL (Right To Left) mode, e.g : Arabic Language
    public static final boolean ENABLE_RTL_MODE = false;

    //set true for enable date display, set false for disable date display
    public static final boolean ENABLE_DATE_DISPLAY = false;


    public static int hitungBackStartApp = 0; //jangan diubah
    public static int hitungAdmob = 0; //jangan diubah

}