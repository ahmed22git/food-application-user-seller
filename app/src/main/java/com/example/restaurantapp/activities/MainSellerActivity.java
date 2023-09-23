package com.example.restaurantapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restaurantapp.adapters.AdapterOrderShop;
import com.example.restaurantapp.adapters.AdapterOrderedItem;
import com.example.restaurantapp.models.ModelOrderShop;
import com.example.restaurantapp.models.ModelProduct;
import com.example.restaurantapp.R;
import com.example.restaurantapp.models.Constants;
import com.example.restaurantapp.adapters.AdapterProductSeller;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainSellerActivity extends AppCompatActivity {

    private TextView shopnameTv,emailTv,tabProductsTv,tabOrdersTv,filteredProductsTv,filteredOrdersTv;
    private EditText searchProductEt;


    private RelativeLayout productsRl,ordersRl;
    private RecyclerView productsRv,ordersRv;


    private ImageButton logoutBtn,editProfileBtn,addProductBtn,filterProductBtn,filterOrderBtn;
    private ImageView profileIv;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ArrayList<ModelProduct> productList;
    private AdapterProductSeller adapterProductSeller;
    private ArrayList<ModelOrderShop> orderShopArrayList;
    private AdapterOrderShop adapterOrderShop;
    private BottomNavigationView bottomNavigationView;
    final int MHOME_ITEM_ID = R.id.mhome;

    final int HOME_ITEM_ID = R.id.home;
    final int NHOME_ITEM_ID = R.id.nhome;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_seller);

        shopnameTv=findViewById(R.id.shopnameTv);
        emailTv=findViewById(R.id.emailTv);
        tabProductsTv=findViewById(R.id.tabProductsTv);
        tabOrdersTv=findViewById(R.id.tabOrdersTv);
        logoutBtn=findViewById(R.id.logoutBtn);
        editProfileBtn=findViewById(R.id.editProfileBtn);
        addProductBtn=findViewById(R.id.addProducteBtn);
        profileIv=findViewById(R.id.profileIv);
        productsRl=findViewById(R.id.productsRl);
        ordersRl=findViewById(R.id.ordersRl);
        filterProductBtn=findViewById(R.id.filterProductBtn);
        ordersRl=findViewById(R.id.ordersRl);
        searchProductEt=findViewById(R.id.seachProductEt);
        filteredProductsTv=findViewById(R.id.filteredProductsTv);
        productsRv=findViewById(R.id.productsRv);
        filteredOrdersTv = findViewById(R.id.filteredOrdersTv);
        filterOrderBtn = findViewById(R.id.filterOrderBtn);
        ordersRv = findViewById(R.id.ordersRv);
        bottomNavigationView = findViewById(R.id.bottomnav);


        progressDialog  = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadAllProducts();
        loadAllOrders();

        showProductsUI();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == MHOME_ITEM_ID) {
                // Start the HomeActivity
                makemeOffline();
                startActivity(new Intent(this, ProfileEditUserActivity.class));
                return true;
            } else if (itemId == NHOME_ITEM_ID) {
                // Call the makemeOffline() function
                startActivity(new Intent(this, ProfileEditUserActivity.class));
                // Handle any other actions related to "NHOME_ITEM_ID"
                return true;
            } else if (itemId == HOME_ITEM_ID) {
                // Call the makemeOffline() function
                startActivity(new Intent(this, AddProductActivity.class));
                // The extra curly braces here seem unnecessary and can be removed
                // return false; // This is the correct placement for the return statement
            }
            return false; // Add this line to cover the case where none of the if conditions match
        });


        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    adapterProductSeller.getFilter().filter(s);

                }
                catch (Exception e){
                    e.printStackTrace();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makemeOffline();
                firebaseAuth.signOut();
                checkUser();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSellerActivity.this, AddProductActivity.class));

            }
        });
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainSellerActivity.this,AddProductActivity.class));


            }
        });
        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductsUI();

            }
        });
        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrdersUI();

            }
        });
        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Filter Products")
                        .setItems(Constants.productCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected = Constants.productCategories1[which];
                                filteredProductsTv.setText(selected);
                                if(selected.equals("All")){
                                    loadAllProducts();
                                }
                                else{
                                    loadFilteredProducts(selected);
                                }

                            }
                        })
                        .show();
            }
        });

        filterOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = {"All", "In Progress","Completed","Cancelled"};
                AlertDialog.Builder builder =  new AlertDialog.Builder(MainSellerActivity.this);
                builder.setTitle("Filter Order")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    filteredOrdersTv.setText("Showing All Orders");
                                    adapterOrderShop.getFilter().filter("");
                                }else {
                                    String optionClicked = options[which];
                                    filteredOrdersTv.setText("Shwoing"+optionClicked+"Orders");
                                    adapterOrderShop.getFilter().filter(optionClicked);
                                }

                            }
                        })
                        .show();
            }
        });
    }

    private void loadAllOrders() {
        orderShopArrayList =  new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderShopArrayList.clear();
                        for (DataSnapshot ds:snapshot.getChildren()){
                            ModelOrderShop modelOrderShop = ds.getValue(ModelOrderShop.class);
                            orderShopArrayList.add(modelOrderShop);
                        }
                        adapterOrderShop =  new AdapterOrderShop(MainSellerActivity.this,orderShopArrayList);
                        ordersRv.setAdapter(adapterOrderShop);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadFilteredProducts(String selected) {
        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){

                            String productCategory = ""+ds.child("ProductCategory").getValue();


                                    if (selected.equals(productCategory)){
                                        ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                        productList.add(modelProduct);

                                    }



                        }
                        adapterProductSeller =  new AdapterProductSeller(MainSellerActivity.this,productList);
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadAllProducts() {
        productList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);

                        }
                        adapterProductSeller =  new AdapterProductSeller(MainSellerActivity.this,productList);
                        productsRv.setAdapter(adapterProductSeller);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void showOrdersUI() {
        productsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.white));
        tabProductsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrdersTv.setTextColor(getResources().getColor(R.color.black));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rec04);

    }

    private void showProductsUI() {
        productsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabProductsTv.setTextColor(getResources().getColor(R.color.black));
        tabProductsTv.setBackgroundResource(R.drawable.shape_rec04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.white));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));


        
    }

    private void makemeOffline() {
        progressDialog.setMessage("Logging out User.");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Online","false");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        firebaseAuth.signOut();
                        checkUser();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainSellerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainSellerActivity.this, LoginActivity.class));
            finish();

        }
        else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                      for (DataSnapshot ds: snapshot.getChildren()){
                          String shopname = ""+ds.child("shopname").getValue();
                          String Accounttype = ""+ds.child("Accounttype").getValue();
                          String email = ""+ds.child("email").getValue();
                          String profileImage = ""+ds.child("profileImage").getValue();


                          shopnameTv.setText(shopname);
                          emailTv.setText(email);
                          try{
                              Picasso.get().load(profileImage).placeholder(R.drawable.baseline_storefront_24).into(profileIv);

                          }
                          catch (Exception e){
                              profileIv.setImageResource(R.drawable.baseline_storefront_24);

                          }

                      }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}