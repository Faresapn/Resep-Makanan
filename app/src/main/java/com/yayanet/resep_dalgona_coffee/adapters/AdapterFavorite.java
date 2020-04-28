package com.yayanet.resep_dalgona_coffee.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yayanet.resep_dalgona_coffee.Config;
import com.yayanet.resep_dalgona_coffee.R;
import com.yayanet.resep_dalgona_coffee.activities.ActivityNewsDetail;
import com.yayanet.resep_dalgona_coffee.json.JsonConfig;
import com.yayanet.resep_dalgona_coffee.models.ItemFavorite;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterFavorite extends RecyclerView.Adapter<AdapterFavorite.ViewHolder> {

    private Context context;
    private List<ItemFavorite> arrayItemFavorite;
    ItemFavorite itemFavorite;

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

    public AdapterFavorite(Context mContext, List<ItemFavorite> arrayItemFavorite) {
        this.context = mContext;
        this.arrayItemFavorite = arrayItemFavorite;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_news_list, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        itemFavorite = arrayItemFavorite.get(position);

        Typeface font1 = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface font2 = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        holder.title.setTypeface(font1);
        holder.date.setTypeface(font2);

        holder.title.setText(itemFavorite.getNewsHeading());
        holder.date.setText(itemFavorite.getNewsDate());

        Picasso.with(context).load(Config.SERVER_URL + "/upload/thumbs/" +
                itemFavorite.getNewsImage()).placeholder(R.drawable.ic_thumbnail).into(holder.image);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                itemFavorite = arrayItemFavorite.get(position);
                int pos = Integer.parseInt(itemFavorite.getCatId());

                Intent intent = new Intent(context, ActivityNewsDetail.class);
                intent.putExtra("POSITION", pos);
                JsonConfig.NEWS_ITEMID = itemFavorite.getCatId();

                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayItemFavorite.size();
    }

}
