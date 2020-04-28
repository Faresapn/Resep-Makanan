package com.yayanet.resep_dalgona_coffee.fragments;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yayanet.resep_dalgona_coffee.Config;
import com.yayanet.resep_dalgona_coffee.R;
import com.yayanet.resep_dalgona_coffee.adapters.AdapterCategory;
import com.yayanet.resep_dalgona_coffee.json.JsonConfig;
import com.yayanet.resep_dalgona_coffee.json.JsonUtils;
import com.yayanet.resep_dalgona_coffee.models.ItemCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentNewsCategory extends Fragment {

    RecyclerView recyclerView;
    List<ItemCategory> arrayItemCategory;
    AdapterCategory adapterCategory;
    private ItemCategory itemCategory;
    ArrayList<String> array_cat_id, array_cat_name, array_cat_image;
    String[] str_cat_id, str_cat_name, str_cat_image;
    int textLength = 0;
    SwipeRefreshLayout swipeRefreshLayout = null;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news_category, container, false);
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue, R.color.red);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(3), true));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrayItemCategory = new ArrayList<ItemCategory>();

        array_cat_id = new ArrayList<String>();
        array_cat_name = new ArrayList<String>();
        array_cat_image = new ArrayList<String>();

        str_cat_id = new String[array_cat_id.size()];
        str_cat_name = new String[array_cat_image.size()];
        str_cat_image = new String[array_cat_name.size()];

        if (JsonUtils.isNetworkAvailable(getActivity())) {
            new MyTask().execute(Config.SERVER_URL + "/api.php");
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        clearData();
                        new RefreshTask().execute(Config.SERVER_URL + "/api.php");
                    }
                }, 3000);
            }
        });

        return v;
    }

    public void clearData() {
        int size = this.arrayItemCategory.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.arrayItemCategory.remove(0);
            }

            adapterCategory.notifyItemRangeRemoved(0, size);
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
                Toast.makeText(getActivity(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConfig.CATEGORY_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {

                        objJson = jsonArray.getJSONObject(i);
                        ItemCategory objItem = new ItemCategory();
                        objItem.setCategoryName(objJson.getString(JsonConfig.CATEGORY_NAME));
                        objItem.setCategoryId(objJson.getInt(JsonConfig.CATEGORY_CID));
                        objItem.setCategoryImageurl(objJson.getString(JsonConfig.CATEGORY_IMAGE));
                        arrayItemCategory.add(objItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                for (int j = 0; j < arrayItemCategory.size(); j++) {
                    itemCategory = arrayItemCategory.get(j);

                    array_cat_id.add(String.valueOf(itemCategory.getCategoryId()));
                    str_cat_id = array_cat_id.toArray(str_cat_id);

                    array_cat_image.add(itemCategory.getCategoryName());
                    str_cat_name = array_cat_image.toArray(str_cat_name);

                    array_cat_name.add(itemCategory.getCategoryImageurl());
                    str_cat_image = array_cat_name.toArray(str_cat_image);
                }

                setAdapterToRecyclerView();
            }

        }
    }

    private class RefreshTask extends AsyncTask<String, Void, String> {

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
                Toast.makeText(getActivity(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(JsonConfig.CATEGORY_ARRAY_NAME);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {

                        objJson = jsonArray.getJSONObject(i);
                        ItemCategory objItem = new ItemCategory();
                        objItem.setCategoryName(objJson.getString(JsonConfig.CATEGORY_NAME));
                        objItem.setCategoryId(objJson.getInt(JsonConfig.CATEGORY_CID));
                        objItem.setCategoryImageurl(objJson.getString(JsonConfig.CATEGORY_IMAGE));
                        arrayItemCategory.add(objItem);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                for (int j = 0; j < arrayItemCategory.size(); j++) {
                    itemCategory = arrayItemCategory.get(j);

                    array_cat_id.add(String.valueOf(itemCategory.getCategoryId()));
                    str_cat_id = array_cat_id.toArray(str_cat_id);

                    array_cat_image.add(itemCategory.getCategoryName());
                    str_cat_name = array_cat_image.toArray(str_cat_name);

                    array_cat_name.add(itemCategory.getCategoryImageurl());
                    str_cat_image = array_cat_name.toArray(str_cat_image);
                }

                setAdapterToRecyclerView();
            }
        }
    }

    public void setAdapterToRecyclerView() {
        adapterCategory = new AdapterCategory(getActivity(), arrayItemCategory);
        recyclerView.setAdapter(adapterCategory);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
                arrayItemCategory.clear();

                for (int i = 0; i < str_cat_name.length; i++) {
                    if (textLength <= str_cat_name[i].length()) {
                        if (str_cat_name[i].toLowerCase().contains(newText.toLowerCase())) {

                            ItemCategory objItem = new ItemCategory();
                            objItem.setCategoryId(Integer.parseInt(str_cat_id[i]));
                            objItem.setCategoryName(str_cat_name[i]);
                            objItem.setCategoryImageurl(str_cat_image[i]);

                            arrayItemCategory.add(objItem);
                        }
                    }
                }

                setAdapterToRecyclerView();

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:

                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
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
}