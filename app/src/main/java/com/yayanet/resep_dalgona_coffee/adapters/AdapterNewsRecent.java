package com.yayanet.resep_dalgona_coffee.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.yayanet.resep_dalgona_coffee.Config;
import com.yayanet.resep_dalgona_coffee.R;
import com.yayanet.resep_dalgona_coffee.activities.ActivityNewsDetail;
import com.yayanet.resep_dalgona_coffee.json.JsonConfig;
import com.yayanet.resep_dalgona_coffee.models.ItemNewsList;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterNewsRecent extends RecyclerView.Adapter<AdapterNewsRecent.ViewHolder> {

    private Context context;
    private List<ItemNewsList> arrayItemNewsList;
    ItemNewsList itemNewsList;

    int counter = Integer.parseInt(Config.INTERVAL_AD);

    InterstitialAd interstitialAd;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView title;
        public TextView date;
        public LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.news_title);
            date = (TextView) view.findViewById(R.id.news_date);
            image = (ImageView) view.findViewById(R.id.news_image);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);

            if (Config.ENABLE_DATE_DISPLAY) {
                date.setVisibility(View.VISIBLE);
            } else {
                date.setVisibility(View.GONE);
            }

        }

    }

    public AdapterNewsRecent(Context context, List<ItemNewsList> arrayItemNewsList) {
        this.context = context;
        this.arrayItemNewsList = arrayItemNewsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_news_list, parent, false);
        //NewsApplication newsApplication = (NewsApplication) context.getApplicationContext();
        //newsApplication.loadInterstitial();
        loadInterstitialAd();
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        itemNewsList = arrayItemNewsList.get(position);

        Typeface font1 = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface font2 = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        holder.title.setTypeface(font1);
        holder.date.setTypeface(font2);

        holder.title.setText(itemNewsList.getNewsHeading());
        holder.date.setText(itemNewsList.getNewsDate());

        Picasso.with(context).load(Config.SERVER_URL + "/upload/thumbs/" +
                itemNewsList.getNewsImage()).placeholder(R.drawable.ic_thumbnail).into(holder.image);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemNewsList = arrayItemNewsList.get(position);

                int pos = Integer.parseInt(itemNewsList.getCatId());

                Intent intent = new Intent(context, ActivityNewsDetail.class);
                intent.putExtra("POSITION", pos);
                JsonConfig.NEWS_ITEMID = itemNewsList.getCatId();

                context.startActivity(intent);
                if (interstitialAd.isLoaded()) {
                    if (counter == Integer.parseInt(Config.INTERVAL_AD)) {
                        interstitialAd.show();
                        loadInterstitialAd();
                        counter = 0;
                    } else {
                        counter++;
                    }
                    //interstitialAd.show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayItemNewsList.size();
    }

    private void loadInterstitialAd() {
        Log.d("TAG", "showAd");
        interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getResources().getString(R.string.admob_interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void showInterstitialAd() {
        if (Config.ENABLE_ADMOB_ADS) {
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            }
        } else {
            Log.d("Fragment Category", "AdMob Interstitial is Disabled");
        }
    }



}
