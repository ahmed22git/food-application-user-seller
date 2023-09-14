package com.example.restaurantapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.models.ModelShop;
import com.example.restaurantapp.R;
import com.example.restaurantapp.activities.ShopDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.HolderShop> {

    public Context context;
    public ArrayList<ModelShop> shopList;

    public AdapterShop(Context context, ArrayList<ModelShop> shopList) {
        this.context = context;
        this.shopList = shopList;
    }

    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_shop,parent,false);

        return new HolderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderShop holder, int position) {
        ModelShop modelShop =  shopList.get(position);
        String Accounttype = modelShop.getAccounttype();
        String address =  modelShop.getAddress();
        String city = modelShop.getCity();
        String country = modelShop.getCountry();
        String deliveryfee = modelShop.getDeliveryfee();
        String email = modelShop.getEmail();
        String latitude = modelShop.getLatitude();
        String longitude = modelShop.getLongitude();
        String online = modelShop.getOnline();
        String phone = modelShop.getPhone();
        String uid = modelShop.getUid();
        String timestamp = modelShop.getTimestamp();
        String shopOpen = modelShop.getShopOpen();
        String state = modelShop.getState();
        String profileImage = modelShop.getProfileImage();
        String shopName = modelShop.getShopname();

        holder.shopNametv.setText(shopName);
        holder.phoneTv.setText(phone);
        holder.addressTv.setText(address);
//        if(online.equals("true")){
//            holder.onlineIv.setVisibility(View.VISIBLE);
//        }else{
//            holder.onlineIv.setVisibility(View.GONE);
//        }
//        if (shopOpen.equals("true")){
//            holder.shopClosedTv.setVisibility(View.GONE);
//        }else{
//            holder.shopClosedTv.setVisibility(View.VISIBLE);


        try {
            Picasso.get().load(profileImage).placeholder(R.drawable.baseline_storefront_24).into(holder.shopIv);

        }catch (Exception e){
            holder.shopIv.setImageResource(R.drawable.baseline_storefront_24);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShopDetailsActivity.class);
                intent.putExtra("shopUid",uid);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    class HolderShop extends RecyclerView.ViewHolder{

        private ImageView shopIv,onlineIv;
        private TextView shopClosedTv,shopNametv,phoneTv,addressTv;
        private RatingBar ratingBar;

        public HolderShop(@NonNull View itemView) {
            super(itemView);


            shopIv = itemView.findViewById(R.id.shopIv);
            shopClosedTv = itemView.findViewById(R.id.shopClosedTv);
            onlineIv = itemView.findViewById(R.id.onlineIv);
            shopNametv = itemView.findViewById(R.id.shopNametv);
            phoneTv = itemView.findViewById(R.id.phoneTv);
            addressTv = itemView.findViewById(R.id.addressTv);
            ratingBar = itemView.findViewById(R.id.ratingBar);



        }



    }
}
