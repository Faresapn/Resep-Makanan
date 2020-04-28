package com.yayanet.resep_dalgona_coffee.fragments;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yayanet.resep_dalgona_coffee.R;
import com.yayanet.resep_dalgona_coffee.adapters.AdapterFavorite;
import com.yayanet.resep_dalgona_coffee.json.JsonUtils;
import com.yayanet.resep_dalgona_coffee.models.ItemFavorite;
import com.yayanet.resep_dalgona_coffee.utilities.DatabaseHandler;
import com.yayanet.resep_dalgona_coffee.utilities.DatabaseHandler.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class FragmentFavorite extends Fragment {

    RecyclerView recyclerView;
    DatabaseHandler databaseHandler;
    private DatabaseManager databaseManager;
    AdapterFavorite adapterFavorite;
    TextView textView;
    JsonUtils jsonUtils;
    List<ItemFavorite> arrayItemFavorite;
    ArrayList<String> array_news, array_news_cat_name, array_cid, array_cat_id, array_cat_name, array_title, array_image, array_desc, array_date;
    String[] str_news, str_news_cat_name, str_cid, str_cat_id, str_cat_name, str_title, str_image, str_desc, str_date;
    int textLength = 0;
    ItemFavorite itemFavorite;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news_favorite, container, false);
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        textView = (TextView) v.findViewById(R.id.textView1);
        databaseHandler = new DatabaseHandler(getActivity());
        databaseManager = DatabaseManager.INSTANCE;
        databaseManager.init(getActivity());
        jsonUtils = new JsonUtils(getActivity());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(3), true));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrayItemFavorite = databaseHandler.getAllData();
        adapterFavorite = new AdapterFavorite(getActivity(), arrayItemFavorite);
        recyclerView.setAdapter(adapterFavorite);

        if (arrayItemFavorite.size() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }

        return v;
    }

    public void onDestroy() {
        // Log.e("OnDestroy", "called");
        if (!databaseManager.isDatabaseClosed())
            databaseManager.closeDatabase();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Log.e("OnPaused", "called");
        if (!databaseManager.isDatabaseClosed())
            databaseManager.closeDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Log.e("OnResume", "called");
        // when back key pressed or go one tab to another we update the favorite
        // item so put in resume
        arrayItemFavorite = databaseHandler.getAllData();
        adapterFavorite = new AdapterFavorite(getActivity(), arrayItemFavorite);
        recyclerView.setAdapter(adapterFavorite);

        if (arrayItemFavorite.size() == 0) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.INVISIBLE);
        }

        array_news = new ArrayList<String>();
        array_news_cat_name = new ArrayList<String>();
        array_cid = new ArrayList<String>();
        array_cat_id = new ArrayList<String>();
        array_cat_name = new ArrayList<String>();
        array_title = new ArrayList<String>();
        array_image = new ArrayList<String>();
        array_desc = new ArrayList<String>();
        array_date = new ArrayList<String>();

        str_news = new String[array_news.size()];
        str_news_cat_name = new String[array_news_cat_name.size()];
        str_cid = new String[array_cid.size()];
        str_cat_id = new String[array_cat_id.size()];
        str_cat_name = new String[array_cat_name.size()];
        str_title = new String[array_title.size()];
        str_image = new String[array_image.size()];
        str_desc = new String[array_desc.size()];
        str_date = new String[array_date.size()];

        for (int j = 0; j < arrayItemFavorite.size(); j++) {
            ItemFavorite objAllBean = arrayItemFavorite.get(j);

            array_cat_id.add(objAllBean.getCatId());
            str_cat_id = array_cat_id.toArray(str_cat_id);

            array_cid.add(String.valueOf(objAllBean.getCId()));
            str_cid = array_cid.toArray(str_cid);

            array_cat_name.add(objAllBean.getCategoryName());
            str_cat_name = array_cat_name.toArray(str_cat_name);

            array_title.add(String.valueOf(objAllBean.getNewsHeading()));
            str_title = array_title.toArray(str_title);

            array_image.add(String.valueOf(objAllBean.getNewsImage()));
            str_image = array_image.toArray(str_image);

            array_desc.add(String.valueOf(objAllBean.getNewsDesc()));
            str_desc = array_desc.toArray(str_desc);

            array_date.add(String.valueOf(objAllBean.getNewsDate()));
            str_date = array_date.toArray(str_date);
        }
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
                arrayItemFavorite.clear();

                for (int i = 0; i < str_title.length; i++) {
                    if (textLength <= str_title[i].length()) {
                        if (str_title[i].toLowerCase().contains(newText.toLowerCase())) {

                            ItemFavorite objItem = new ItemFavorite();

                            objItem.setCatId(str_cat_id[i]);
                            objItem.setCId(str_cid[i]);
                            objItem.setCategoryName(str_cat_name[i]);
                            objItem.setNewsHeading(str_title[i]);
                            objItem.setNewsImage(str_image[i]);
                            objItem.setNewsDesc(str_desc[i]);
                            objItem.setNewsDate(str_date[i]);

                            arrayItemFavorite.add(objItem);

                        }
                    }
                }

                adapterFavorite = new AdapterFavorite(getActivity(), arrayItemFavorite);
                recyclerView.setAdapter(adapterFavorite);

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Do something
                return true;
            }
        });
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
