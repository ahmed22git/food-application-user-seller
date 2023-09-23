package com.example.restaurantapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restaurantapp.R;
import com.example.restaurantapp.adapters.AdapterCartItem;
import com.example.restaurantapp.models.ModelCartItem;
import com.example.restaurantapp.models.ModelProduct;
import com.example.restaurantapp.models.Constants;
import com.example.restaurantapp.adapters.AdapterProductUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class ShopDetailsActivity extends AppCompatActivity {
    private ImageView shopIv;
    private TextView shopNameTv,phontTv,emailTv,openCloseTv,deliveryFeeTv,addressTv,filteredProductsTv,cartCountTv;

    private ImageButton callBtn,mapBtn,cartBtn,backBtn,filterproductBtn;
    private EditText searchProductEt;
    private RecyclerView productsRv;
    private String shopUid;
    public String deliveryFee;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productsList;
    private AdapterProductUser adapterProductUser;

    private String myLatitude,myLongitude,myPhone;
    private String shopName,shopPhone,shopAddress,shopEmail,shopLatitude,shopLongitude;
    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;
    private EasyDB easyDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        shopIv = findViewById(R.id.shopIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        phontTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        openCloseTv = findViewById(R.id.openCloseTv);
        deliveryFeeTv = findViewById(R.id.deliveryfeeTv);
        addressTv = findViewById(R.id.addressTv);
        callBtn = findViewById(R.id.callBtn);
        mapBtn = findViewById(R.id.mapBtn);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        cartBtn = findViewById(R.id.cartBtn);
        backBtn = findViewById(R.id.backBtn);
        filterproductBtn = findViewById(R.id.filterProductBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        productsRv = findViewById(R.id.productsRv);
        cartCountTv = findViewById(R.id.cartCountTv);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);


        shopUid = getIntent().getStringExtra("shopUid");
        firebaseAuth = FirebaseAuth.getInstance();
        loadMyInfo();
        loadShopDetails();
        loadShopProducts();

        easyDB = EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text","not null"}))
                .doneTableColumn();

        deleteCartData();
        cartCount();

        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProductUser.getFilter().filter(s);

                }
                catch (Exception e){
                    e.printStackTrace();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCartDialog();

            }
        });
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone();
            }
        });
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });
        filterproductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopDetailsActivity.this);
                builder.setTitle("Filter Produces")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected = Constants.productCategories1[which];
                                filteredProductsTv.setText(selected);
                                if(selected.equals("All")){
                                    loadShopProducts();
                                }
                                else{
                                    adapterProductUser.getFilter().filter(selected);
                                }

                            }
                        })
                        .show();

            }
        });



    }

    private void deleteCartData() {

        easyDB.deleteAllDataFromTable();

    }
    public void cartCount(){
        int count =easyDB.getAllData().getCount();
        if (count<=0){
            cartCountTv.setVisibility(View.GONE);

        }
        else{
            cartCountTv.setVisibility(View.VISIBLE);
            cartCountTv.setText(""+count);

        }
    }

    public double allTotalPrice = 0.00;
    public TextView sTotalTv,dFeeTv,allTotalPriceTv;


    private void showCartDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);

        cartItemList = new ArrayList<>();

        TextView shopNameTv = view.findViewById(R.id.shopNameTv);
        RecyclerView cartItemsRv = view.findViewById(R.id.cartItemsRv);
        sTotalTv = view.findViewById(R.id.sTotalTv);
        dFeeTv = view.findViewById(R.id.dFeeTv);
        allTotalPriceTv = view.findViewById(R.id.totalTv);
        Button checkoutBtn = view.findViewById(R.id.checkoutBtn);

        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setView(view);

        shopNameTv.setText(shopName);

        EasyDB easyDB = EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS TABLE")
                .addColumn(new Column("Item_Id", new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price", new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity", new String[]{"text","not null"}))
                .doneTableColumn();

        Cursor res = easyDB.getAllData();
        while (res.moveToNext()){
            String id = res.getString(1);
            String pid = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String cost = res.getString(5);
            String quantity = res.getString(6);

            allTotalPrice = allTotalPrice +  Double.parseDouble(cost);
            ModelCartItem modelCartItem =  new ModelCartItem(""+id
                    ,""+pid,""+name,""+price,""+cost,""+quantity);

            cartItemList.add(modelCartItem);
        }
        adapterCartItem = new AdapterCartItem(this,cartItemList);
        cartItemsRv.setAdapter(adapterCartItem);

        dFeeTv.setText("Rs"+deliveryFee);
        sTotalTv.setText("Rs"+String.format("%.2f",allTotalPrice));
        allTotalPriceTv.setText("Rs" + (allTotalPrice + Double.parseDouble(deliveryFee.replace("Rs", ""))));



        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice = 0.00;
            };



        });
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myLatitude.equals("")|| myLatitude.equals("null")|| myLongitude.equals("") || myLongitude.equals("null")){
                    Toast.makeText(ShopDetailsActivity.this, "Please enter Address in your profile before ordering !", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(myPhone.equals("")|| myPhone.equals("null")){
                    Toast.makeText(ShopDetailsActivity.this, "Please enter Phone number in your profile before ordering !", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cartItemList.size() == 0){
                    Toast.makeText(ShopDetailsActivity.this, "No Item In Cart", Toast.LENGTH_SHORT).show();
                    return;

                }
                submitOrder();
            }
        });

    }

    private void submitOrder() {
        progressDialog.setMessage("Placing Order");
        progressDialog.show();

        String timestamp = "" + System.currentTimeMillis();
        String cost = allTotalPriceTv.getText().toString().trim().replace("Rs", "");

        // Create an order object
        HashMap<String, String> orderData = new HashMap<>();
        orderData.put("orderId", "" + timestamp);
        orderData.put("orderTime", "" + timestamp);
        orderData.put("orderStatus", "In Progress");
        orderData.put("orderCost", "" + cost);
        orderData.put("orderBy", "" + firebaseAuth.getUid());
        orderData.put("orderTo", "" + shopUid);
        orderData.put("latitude", "" + myLatitude);
        orderData.put("longitude", "" + myLongitude);

        DatabaseReference sellerOrderRef = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        DatabaseReference userOrderRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getUid()).child("Orders");

        // Store the order in the seller's database
        sellerOrderRef.child(timestamp).setValue(orderData)


                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Store the reference to the order in the user's database
                        userOrderRef.child(timestamp).setValue(orderData)

                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
                                    @Override
                                    public void onSuccess(Void unused) {
                                        for (int i = 0; i < cartItemList.size(); i++) {
                                            // ...
                                            // Store order items in both the seller's and user's databases
                                            String pId = cartItemList.get(i).getpId();
                                            String id = cartItemList.get(i).getId();
                                            String cost = cartItemList.get(i).getCost();
                                            String name = cartItemList.get(i).getName();
                                            String price = cartItemList.get(i).getPrice();
                                            String quantity = cartItemList.get(i).getQuantity();

                                            HashMap<String, String> hashMap1 = new HashMap<>();
                                            hashMap1.put("pId",pId);
                                            hashMap1.put("name",name);
                                            hashMap1.put("cost",cost);
                                            hashMap1.put("price",price);
                                            hashMap1.put("quantity",quantity);

                                            ref.child(timestamp).child("Items").child(pId).setValue(hashMap1);

                                        }
                                        progressDialog.dismiss();
                                        Toast.makeText(ShopDetailsActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(ShopDetailsActivity.this, OrderDetailsUsersActivity.class);
                                        intent.putExtra("orderTo", shopUid);
                                        intent.putExtra("orderId", timestamp);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ShopDetailsActivity.this, "Failed to store order reference in user's database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ShopDetailsActivity.this, "Failed to place order in seller's database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void openMap() {
        String address = "https://maps.google.com/maps?saddr" + myLatitude + "," + myLongitude + "&daddr=" + shopLatitude + "," + shopLongitude;
        Intent intent =  new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private void dialPhone() {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(shopPhone))));
        Toast.makeText(this, ""+shopPhone, Toast.LENGTH_SHORT).show();
        
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String fullname = ""+ds.child("fullname").getValue();
                            String email = ""+ds.child("email").getValue();
                            myPhone = ""+ds.child("phone").getValue();
                            String deliveryfee = ""+ds.child("deliveryfee").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String city = ""+ds.child("city").getValue();
                            String Accounttype = ""+ds.child("Accounttype").getValue();

                            myLatitude = ""+ds.child("latitude").getValue();
                            myLongitude = ""+ds.child("longitude").getValue();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = ""+snapshot.child("name").getValue();
                shopName = ""+snapshot.child("shopname").getValue();
                shopEmail = ""+snapshot.child("email").getValue();
                shopPhone = ""+snapshot.child("phone").getValue();
                shopAddress = ""+snapshot.child("address").getValue();
                shopLatitude = ""+snapshot.child("latitude").getValue();
                shopLongitude = ""+snapshot.child("longitude").getValue();
                deliveryFee = ""+snapshot.child("deliveryfee").getValue();
                String profileImage = ""+snapshot.child("profileImage").getValue();
                String shopOpen = ""+snapshot.child("shopOpen").getValue();

                shopNameTv.setText(shopName);
                emailTv.setText(shopEmail);
                deliveryFeeTv.setText("Delivery Fee:Rs"+deliveryFee);
                addressTv.setText(shopAddress);
                phontTv.setText(shopPhone);
                if (shopOpen.equals("true")){
                    openCloseTv.setText("Open");

                }else{
                    openCloseTv.setText("Close");
                }try{
                    Picasso.get().load(profileImage).into(shopIv);

                }
                catch (Exception e ){


                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadShopProducts() {

        productsList =  new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productsList.clear();
                        for(DataSnapshot ds:snapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productsList.add(modelProduct);
                        }
                        adapterProductUser =  new AdapterProductUser(ShopDetailsActivity.this,productsList);
                        productsRv.setAdapter(adapterProductUser);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }




}