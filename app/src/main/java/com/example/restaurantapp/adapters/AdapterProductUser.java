package com.example.restaurantapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurantapp.activities.ShopDetailsActivity;
import com.example.restaurantapp.models.FilterProductUser;
import com.example.restaurantapp.R;
import com.example.restaurantapp.models.ModelProduct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable{

    private Context context;
    public ArrayList<ModelProduct> productsList,filterList;

    private FilterProductUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {

        ModelProduct modelProduct = productsList.get(position);

        String productCategory = modelProduct.getProductCategory();
        String price =  modelProduct.getProductprice();
        String productDescription = modelProduct.getProductDescription();
        String productTitle = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String productId = modelProduct.getProductID();
        String timestamp = modelProduct.getTimestamp();
        String productIcon = modelProduct.getProductIcon();

        holder.titleTv.setText(productTitle);
        holder.priceTv.setText("Rs"+price);
        holder.descriptionTv.setText(productDescription);

        holder.titleTv.setText(productTitle);
        holder.titleTv.setText(productTitle);

        try{
            Picasso.get().load(productIcon).placeholder(R.drawable.baseline_storefront_24).into(holder.productIconIv);
        }
        catch (Exception e) {
            holder.productIconIv.setImageResource(R.drawable.baseline_storefront_24);
        }
        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuantityDialog(modelProduct);

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });




    }
    private double cost = 0 ;
    private double finalCost = 0;
    private int quantity = 0;

    private void showQuantityDialog(ModelProduct modelProduct) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity,null);
        ImageView productIv = view.findViewById(R.id.productIv);
        TextView pQuantityTv = view.findViewById(R.id.pQuantityTv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView pDescriptionTv = view.findViewById(R.id.pDescriptionTv);
        TextView priceTv = view.findViewById(R.id.priceTv);
        TextView finalTv = view.findViewById(R.id.finalTv);
        ImageButton incrementBtn = view.findViewById(R.id.incrementBtn);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        ImageButton decrementBtn = view.findViewById(R.id.decrementBtn);
        Button continueBtn = view.findViewById(R.id.continueBtn);

        String productId = modelProduct.getProductID();
        String title =  modelProduct.getProductTitle();
        String description = modelProduct.getProductDescription();
        String productTitle = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String image = modelProduct.getProductIcon();
        String price = modelProduct.getProductprice();

        cost = Double.parseDouble(price.replaceAll("$",""));
        finalCost = Double.parseDouble(price.replaceAll("$",""));
        quantity = 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        try{
            Picasso.get().load(image).placeholder(R.drawable.baseline_storefront_24).into(productIv);

        }catch (Exception e){
            productIv.setImageResource(R.drawable.baseline_storefront_24);

        }
        titleTv.setText(""+title);
        pQuantityTv.setText(""+productQuantity);
        pDescriptionTv.setText(""+description);
        quantityTv.setText(""+quantity);
        priceTv.setText(""+modelProduct.getProductprice());
        finalTv.setText(""+finalCost);

        AlertDialog dialog = builder.create();
        dialog.show();

        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost + cost;
                quantity++;

                finalTv.setText("Rs"+finalCost);
                quantityTv.setText(""+quantity);
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity>1){
                    finalCost = finalCost -cost;
                    quantity --;

                    finalTv.setText("Rs"+finalCost);
                    quantityTv.setText(""+quantity);
                }
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTv.getText().toString().trim();
                String priceEach = price;
                String totalprice = finalTv.getText().toString().trim().replace("Rs","");;
                String quantity = quantityTv.getText().toString().trim();

                addToCart(productId,title,priceEach,totalprice,quantity);

                dialog.dismiss();


            }
        });


    }
    private int itemId = 1;

    private void addToCart(String productId, String title, String priceEach, String price, String quantity) {
        itemId++;

        EasyDB easyDB = EasyDB.init(context,"ITEMS_DB")
                .setTableName("ITEMS TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text","not null"}))
                .doneTableColumn();

        boolean b = easyDB.addData("Item_Id",itemId)
                .addData("Item_PID",productId)
                .addData("Item_Name",title)
                .addData("Item_Price_Each",priceEach)
                .addData("Item_Price",price)
                .addData("Item_Quantity",quantity)
                .doneDataAdding();

        Toast.makeText(context, "Added to cart..", Toast.LENGTH_SHORT).show();


        ((ShopDetailsActivity)context).cartCount();






    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if ( filter == null){
            filter = new FilterProductUser(this,filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        private ImageView productIconIv;

        private TextView titleTv,descriptionTv,addToCartTv,priceTv;

        public HolderProductUser(@NonNull View itemView) {
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);
            priceTv = itemView.findViewById(R.id.priceTv);
        }
    }
}
