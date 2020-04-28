package com.yayanet.resep_dalgona_coffee.activities;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yayanet.resep_dalgona_coffee.Config;
import com.yayanet.resep_dalgona_coffee.R;
import com.yayanet.resep_dalgona_coffee.fragments.FragmentAbout;
import com.yayanet.resep_dalgona_coffee.utilities.AdManager;
import com.google.android.gms.ads.MobileAds;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;

    private WebView web;
    private String homeUrl = "file:///android_asset/contents/menu.html";

    AdManager AM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        StartAppSDK.init(this, getString(R.string.startapp_app_id), true);

        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, "ca-app-pub-1741485245670322~4154379267");

        // StartApp
        AM = new AdManager(this);
        //AM.show_startapp();
        AM.admob_banner();

        // Webview
        init_webview();

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("MainActivity", "Working in Normal Mode, RTL Mode is Disabled");
        }

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setTitle("");
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.main_drawer);
        mFragmentManager = getSupportFragmentManager();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                if (menuItem.getItemId() == R.id.drawer_home) {
                    init_webview();
                }

                if (menuItem.getItemId() == R.id.drawer_policy) {
                    removeFragment();
                    web.setVisibility(View.VISIBLE);
                    web.loadUrl("file:///android_asset/privacy.html");
                }

                if (menuItem.getItemId() == R.id.drawer_rate) {
                    final String appName = getPackageName();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                    } catch (ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                    }
                }

                if (menuItem.getItemId() == R.id.drawer_more) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.play_more_apps))));
                }

                if (menuItem.getItemId() == R.id.drawer_about) {
                    web.setVisibility(View.GONE);
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_container, new FragmentAbout(), "fragment").commit();
                }

                return false;
            }

        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }

    public void removeFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("fragment");
        if(fragment != null) getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    public void init_webview() {
        removeFragment();
        web = findViewById(R.id.web);
        web.setVisibility(View.VISIBLE);
        WebSettings ws = web.getSettings();
        //ws.setJavaScriptEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setDomStorageEnabled(true);
        web.setWebViewClient(wvc);
        web.loadUrl(homeUrl);
    }

    public static int hitungRate = 0; //jangan diubah
    public static boolean belumTampilRate = true;

    public boolean bolehTampilRate(){
        hitungRate++;
        if(hitungRate % Integer.parseInt(getString(R.string.counter_rate)) == 0){
            if(belumTampilRate){
                belumTampilRate = false;
                return true;
            }
        }
        return false;
    }

    WebViewClient wvc = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @SuppressWarnings("all")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("shouldOverrideUrl", "pass");

            if(url.contains("contents") && !url.equals(homeUrl)) {
                AM.admob_interstitial_show(MainActivity.this);



                if(bolehTampilRate()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setTitle("TOLONG BANTU!!!");
                    builder.setMessage("Kami telah menghabiskan banyak waktu untuk membuat app ini, maukah anda meluangkan waktu sejenak dan memberi RATE 5 di Play Store?");

                    builder.setNegativeButton("Cancel", (dialog, which) -> {});
                    builder.setPositiveButton("Rate", (dialog, which) -> {
                        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    });

                    AlertDialog diag = builder.create();
                    diag.show();
                }

            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest url) {
            Log.e("shouldOverrideUrl", "pass");
            return super.shouldOverrideUrlLoading(view, url);
        }

    };

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_share:

                String shareText = getString(R.string.app_name) + "\nhttps://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share app via..."));

                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
    */

    public void dialogExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit?").setMessage("Do you want to exit ?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(!web.getUrl().equals(homeUrl)){
            web.loadUrl(homeUrl);


            return;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            dialogExit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}