package com.mobio.analytics.client.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobio.analytics.R;
import com.mobio.analytics.client.models.NotiResponseObject;
import com.mobio.analytics.client.utility.LogMobio;

import java.util.ArrayList;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.AdsViewHolder>{
    private ArrayList<NotiResponseObject> notiResponseObjects;
    private OnItemClick onItemClick;

    public AdsAdapter(ArrayList<NotiResponseObject> notiResponseObjects, OnItemClick onItemClick){
        this.notiResponseObjects = notiResponseObjects;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public AdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_ad, parent, false);
        return new AdsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdsViewHolder holder, int position) {
        NotiResponseObject notiResponseObject = notiResponseObjects.get(position);
        holder.tvAds.setText(notiResponseObject.getContent());
        if(position == 0){
            holder.imvAds.setImageResource(R.mipmap.discount);
        }
        else if(position == 1){
            holder.imvAds.setImageResource(R.mipmap.voucher);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onClick(notiResponseObject);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notiResponseObjects.size();
    }

    class AdsViewHolder extends RecyclerView.ViewHolder{

        private TextView tvAds;
        private TextView tvAdsExpire;
        private ImageView imvAds;

        public AdsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvAds = itemView.findViewById(R.id.tv_ad_title);
            imvAds = itemView.findViewById(R.id.imv_ad);
            tvAdsExpire = itemView.findViewById(R.id.tv_ad_expire);
        }
    }

    public interface OnItemClick{
        void onClick(NotiResponseObject notiResponseObject);
    }
}
