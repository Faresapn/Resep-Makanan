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
import com.yayanet.resep_dalgona_coffee.activities.ActivityNewsListByCategory;
import com.yayanet.resep_dalgona_coffee.json.JsonConfig;
import com.yayanet.resep_dalgona_coffee.models.ItemCategory;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.ViewHolder> {

    ItemCategory itemCategory;
    private Context context;
    private List<ItemCategory> arrayItemCategory;
    private InterstitialAd interstitialAd;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public ImageView image;
        public LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.category_title);
            image = (ImageView) view.findViewById(R.id.category_image);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);

        }

    }

    public AdapterCategory(Context mContext, List<ItemCategory> arrayItemCategory) {
        this.context = mContext;
        this.arrayItemCategory = arrayItemCategory;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_category, parent, false);

        loadInterstitialAd();

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        itemCategory = arrayItemCategory.get(position);

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        holder.title.setTypeface(font);

        holder.title.setText(itemCategory.getCategoryName());

        Picasso.with(context).load(Config.SERVER_URL + "/upload/category/" +
                itemCategory.getCategoryImageurl()).placeholder(R.drawable.ic_thumbnail).into(holder.image);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                itemCategory = arrayItemCategory.get(position);
                int catId = itemCategory.getCategoryId();
                JsonConfig.CATEGORY_IDD = itemCategory.getCategoryId();
                Log.e("cat_id", "" + catId);
                JsonConfig.CATEGORY_TITLE = itemCategory.getCategoryName();

                Intent intent = new Intent(context, ActivityNewsListByCategory.class);
                context.startActivity(intent);

                showInterstitialAd();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayItemCategory.size();
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
