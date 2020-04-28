package com.yayanet.resep_dalgona_coffee.activities;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yayanet.resep_dalgona_coffee.Config;
import com.yayanet.resep_dalgona_coffee.R;
import com.yayanet.resep_dalgona_coffee.adapters.AdapterNewsByCategory;
import com.yayanet.resep_dalgona_coffee.json.JsonConfig;
import com.yayanet.resep_dalgona_coffee.json.JsonUtils;
import com.yayanet.resep_dalgona_coffee.models.ItemNewsList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityNewsListByCategory extends AppCompatActivity {

    RecyclerView recyclerView;
    List<ItemNewsList> arrayItemNewsList;
    AdapterNewsByCategory adapterNewsByCategory;
    ArrayList<String> array_news, array_news_cat_name, array_cid, array_cat_id, array_cat_image, array_cat_name, array_title, array_image, array_desc, array_date;
    String[] str_news, str_news_cat_name;
    String[] str_cid, str_cat_id, str_cat_image, str_cat_name, str_title, str_image, str_desc, str_date;
    ItemNewsList itemNewsList;
    JsonUtils util;
    int textLength = 0;
    private AdView adView;
    SwipeRefreshLayout swipeRefreshLayout = null;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list_by_category);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        } else {
            Log.d("ActivityNewsList", "Working in Normal Mode, RTL Mode is Disabled");
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.red);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(3), true));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        loadAdMobBannerAd();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(JsonConfig.CATEGORY_TITLE);
        }

        arrayItemNewsList = new ArrayList<ItemNewsList>();
        array_news = new ArrayList<String>();
        array_news_cat_name = new ArrayList<String>();
        array_cid = new ArrayList<String>();
        array_cat_id = new ArrayList<String>();
        array_cat_image = new ArrayList<String>();
        array_cat_name = new ArrayList<String>();
        array_title = new ArrayList<String>();
        array_image = new ArrayList<String>();
        array_desc = new ArrayList<String>();
        array_date = new ArrayList<String>();

        str_news = new String[array_news.size()];
        str_news_cat_name = new String[array_news_cat_name.size()];
        str_cid = new String[array_cid.size()];
        str_cat_id = new String[array_cat_id.size()];
        str_cat_image = new String[array_cat_image.size()];
        str_cat_name = new String[array_cat_name.size()];
        str_title = new String[array_title.size()];
        str_image = new String[array_image.size()];
        str_desc = new String[array_desc.size()];
        str_date = new String[array_date.size()];

        util = new JsonUtils(getApplicationContext());

        if (JsonUtils.isNetworkAvailable(ActivityNewsListByCategory.this)) {
            new MyTask().execute(Config.SERVER_URL + "/api.php?cat_id=" + JsonConfig.CATEGORY_IDD);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
        }

        // Using to refresh webpage when user swipes the screen
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        clearData();
                        new MyTask().execute(Config.SERVER_URL + "/api.php?cat_id=" + JsonConfig.CATEGORY_IDD);
                    }
                }, 3000);
            }
        });

    }

    public void clearData() {
        int size = this.arrayItemNewsList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.arrayItemNewsList.remove(0);
            }

            adapterNewsByCategory.notifyItemRangeRemoved(0, size);
        }
    }

    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressBar.setVisibility(View.GONE);

            if (null == result || result.length() == 0) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConfig.CATEGORY_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemNewsList objItem = new ItemNewsList();

                        objItem.setCId(objJson.getString(JsonConfig.CATEGORY_ITEM_CID));
                        objItem.setCategoryName(objJson.getString(JsonConfig.CATEGORY_ITEM_NAME));
                        objItem.setCategoryImage(objJson.getString(JsonConfig.CATEGORY_ITEM_IMAGE));
                        objItem.setCatId(objJson.getString(JsonConfig.CATEGORY_ITEM_CAT_ID));
                        objItem.setNewsImage(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSIMAGE));
                        objItem.setNewsHeading(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSHEADING));
                        objItem.setNewsDescription(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSDESCRI));
                        objItem.setNewsDate(objJson.getString(JsonConfig.CATEGORY_ITEM_NEWSDATE));

                        arrayItemNewsList.add(objItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < arrayItemNewsList.size(); j++) {

                    itemNewsList = arrayItemNewsList.get(j);

                    array_cat_id.add(itemNewsList.getCatId());
                    str_cat_id = array_cat_id.toArray(str_cat_id);

                    array_cat_name.add(itemNewsList.getCategoryName());
                    str_cat_name = array_cat_name.toArray(str_cat_name);

                    array_cid.add(String.valueOf(itemNewsList.getCId()));
                    str_cid = array_cid.toArray(str_cid);

                    array_image.add(String.valueOf(itemNewsList.getNewsImage()));
                    str_image = array_image.toArray(str_image);

                    array_title.add(String.valueOf(itemNewsList.getNewsHeading()));
                    str_title = array_title.toArray(str_title);

                    array_desc.add(String.valueOf(itemNewsList.getNewsDescription()));
                    str_desc = array_desc.toArray(str_desc);

                    array_date.add(String.valueOf(itemNewsList.getNewsDate()));
                    str_date = array_date.toArray(str_date);

                }

                setAdapterToRecyclerView();
            }
        }
    }

    public void setAdapterToRecyclerView() {
        adapterNewsByCategory = new AdapterNewsByCategory(this, arrayItemNewsList);
        recyclerView.setAdapter(adapterNewsByCategory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));

        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchView.setQueryHint(getResources().getString(R.string.search_query_text));

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {

                textLength = newText.length();
                arrayItemNewsList.clear();

                for (int i = 0; i < str_title.length; i++) {
                    if (textLength <= str_title[i].length()) {
                        if (str_title[i].toLowerCase().contains(newText.toLowerCase())) {

                            ItemNewsList objItem = new ItemNewsList();

                            objItem.setCategoryName(str_cat_name[i]);
                            objItem.setCatId(str_cat_id[i]);
                            objItem.setCId(str_cid[i]);
                            objItem.setNewsDate(str_date[i]);
                            objItem.setNewsDescription(str_desc[i]);
                            objItem.setNewsHeading(str_title[i]);
                            objItem.setNewsImage(str_image[i]);

                            arrayItemNewsList.add(objItem);

                        }
                    }
                }

                setAdapterToRecyclerView();
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do something
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

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
        adViewOnPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adViewOnResume();
    }

    @Override
    protected void onDestroy() {
        adViewOnDestroy();
        super.onDestroy();
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void loadAdMobBannerAd() {
        if (Config.ENABLE_ADMOB_ADS) {
            adView = (AdView) findViewById(R.id.adView);
            adView.loadAd(new AdRequest.Builder().build());
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    adView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLeftApplication() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onAdLoaded() {
                    adView.setVisibility(View.VISIBLE);
                }
            });
            Log.d("ActivityNewsList", "AdMob Banner is Enabled");

        } else {
            Log.d("ActivityNewsList", "AdMob Banner is Disabled");
        }
    }

    private void adViewOnPause() {
        if (Config.ENABLE_ADMOB_ADS) {
            adView.pause();
        } else {
            Log.d("ActivityNewsList", "adView onPause is Disabled");
        }
    }

    private void adViewOnResume() {
        if (Config.ENABLE_ADMOB_ADS) {
            adView.resume();
        } else {
            Log.d("ActivityNewsList", "adView onResume is Disabled");
        }
    }

    private void adViewOnDestroy() {
        if (Config.ENABLE_ADMOB_ADS) {
            adView.destroy();
        } else {
            Log.d("ActivityNewsList", "adView onDestroy is Disabled");
        }
    }

}
