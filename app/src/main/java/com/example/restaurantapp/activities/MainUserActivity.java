package com.example.restaurantapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.restaurantapp.adapters.AdapterOrderUser;
import com.example.restaurantapp.models.ModelOrderUser;
import com.example.restaurantapp.models.ModelShop;
import com.example.restaurantapp.R;
import com.example.restaurantapp.adapters.AdapterShop;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class MainUserActivity extends AppCompatActivity {

    private TextView nameTv,emailTv,phoneTv,tabShopsTv,tabOrdersTv;
    private RelativeLayout shopsRl,ordersRl;

    private ImageButton logoutBtn,editProfileBtn;
    private ImageView profileIv,yourimageview,yourimageview2,yourimageview3,yourimageview4;
    private RecyclerView shopsRv,ordersRv;
    private ArrayList<ModelShop> shopList;
    private ArrayList<ModelOrderUser> ordersList;
    private AdapterOrderUser adapterOrderUser;
    private AdapterShop adapterShop;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private BottomNavigationView bottomNavigationView;
    final int MHOME_ITEM_ID = R.id.mhome;
    final int NHOME_ITEM_ID = R.id.nhome;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        nameTv=findViewById(R.id.nameTv);
        emailTv=findViewById(R.id.emailTv);
        phoneTv=findViewById(R.id.phoneTv);
        tabShopsTv=findViewById(R.id.tabShopsTv);
        tabOrdersTv=findViewById(R.id.tabOrdersTv);
        logoutBtn=findViewById(R.id.logoutBtn);
        editProfileBtn=findViewById(R.id.editProfileBtn);
        profileIv=findViewById(R.id.profileIv);
        shopsRl=findViewById(R.id.shopsRl);
        ordersRl=findViewById(R.id.ordersRl);
        shopsRv=findViewById(R.id.shopsRv);
        ordersRv=findViewById(R.id.ordersRv);
        bottomNavigationView = findViewById(R.id.bottomnav);
        yourimageview = findViewById(R.id.yourImageView);
        yourimageview2 = findViewById(R.id.yourImageView2);
        yourimageview3 = findViewById(R.id.yourImageView3);
        yourimageview4 = findViewById(R.id.yourImageView4);


        progressDialog  = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        showShopsUI();

        yourimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainUserActivity.this, "ImageView clicked", Toast.LENGTH_SHORT).show();
                int desiredItemPosition = 5;
                Log.d("ScrollDebug", "Desired Position: " + desiredItemPosition);

                if (shopList != null && desiredItemPosition >= 0 && desiredItemPosition < shopList.size()) {
                    shopsRv.scrollToPosition(desiredItemPosition);
                } else {
                    Log.e("ScrollError", "Invalid position or shopList is null/empty.");
                }
                // Scroll to or select a specific item in the RecyclerView
                // Replace with the position of the item you want to open
                shopsRv.smoothScrollToPosition(desiredItemPosition);
            }
        });
        yourimageview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainUserActivity.this, "ImageView clicked", Toast.LENGTH_SHORT).show();
                int desiredItemPosition = 3;
                Log.d("ScrollDebug", "Desired Position: " + desiredItemPosition);

                if (shopList != null && desiredItemPosition >= 0 && desiredItemPosition < shopList.size()) {
                    shopsRv.scrollToPosition(desiredItemPosition);
                } else {
                    Log.e("ScrollError", "Invalid position or shopList is null/empty.");
                }
                // Scroll to or select a specific item in the RecyclerView
                // Replace with the position of the item you want to open
                shopsRv.smoothScrollToPosition(desiredItemPosition);
            }
        });
        yourimageview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainUserActivity.this, "ImageView clicked", Toast.LENGTH_SHORT).show();
                int desiredItemPosition = 1;
                Log.d("ScrollDebug", "Desired Position: " + desiredItemPosition);

                if (shopList != null && desiredItemPosition >= 0 && desiredItemPosition < shopList.size()) {
                    shopsRv.scrollToPosition(desiredItemPosition);
                } else {
                    Log.e("ScrollError", "Invalid position or shopList is null/empty.");
                }
                // Scroll to or select a specific item in the RecyclerView
                // Replace with the position of the item you want to open
                shopsRv.smoothScrollToPosition(desiredItemPosition);
            }
        });
        yourimageview4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainUserActivity.this, "ImageView clicked", Toast.LENGTH_SHORT).show();
                int desiredItemPosition = 6;
                Log.d("ScrollDebug", "Desired Position: " + desiredItemPosition);

                if (shopList != null && desiredItemPosition >= 0 && desiredItemPosition < shopList.size()) {
                    shopsRv.scrollToPosition(desiredItemPosition);
                } else {
                    Log.e("ScrollError", "Invalid position or shopList is null/empty.");
                }
                // Scroll to or select a specific item in the RecyclerView
                // Replace with the position of the item you want to open
                shopsRv.smoothScrollToPosition(desiredItemPosition);
            }
        });


        ArrayList<ModelShop> shopList = new ArrayList<>();
        AdapterShop adapterShop = new AdapterShop(this, shopList);

// Set up your RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        shopsRv.setLayoutManager(layoutManager);

// Set the adapter for your RecyclerView
        shopsRv.setAdapter(adapterShop);

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
                // Handle any other actions related to "MHOME_ITEM_ID"
                return true;
            } {
                return false;
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makemeOffline();
            }
        });
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainUserActivity.this, ProfileEditUserActivity.class));

            }
        });
        tabShopsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShopsUI();
            }
        });
        tabOrdersTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrdersUI();
            }
        });
    }

    private void showShopsUI() {
        shopsRl.setVisibility(View.VISIBLE);
        ordersRl.setVisibility(View.GONE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.black));
        tabShopsTv.setBackgroundResource(R.drawable.shape_rec04);

        tabOrdersTv.setTextColor(getResources().getColor(R.color.white));
        tabOrdersTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
    private  void showOrdersUI(){
        shopsRl.setVisibility(View.GONE);
        ordersRl.setVisibility(View.VISIBLE);

        tabShopsTv.setTextColor(getResources().getColor(R.color.white));
        tabShopsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabOrdersTv.setTextColor(getResources().getColor(R.color.black));
        tabOrdersTv.setBackgroundResource(R.drawable.shape_rec04);

    }

    private void makemeOffline() {
        progressDialog.setMessage("Logging out User.");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Online", "false");

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
                        Toast.makeText(MainUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }
    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(MainUserActivity.this, LoginActivity.class));
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
                            String fullname = ""+ds.child("fullname").getValue();
                            String email = ""+ds.child("email").getValue();
                            String phone = ""+ds.child("phone").getValue();
                            String profileImage = ""+ds.child("profileImage").getValue();
                            String city = ""+ds.child("city").getValue();
                            String Accounttype = ""+ds.child("Accounttype").getValue();

                            nameTv.setText(fullname);
                            emailTv.setText(email);
                            phoneTv.setText(phone);
                            try{
                                Picasso.get().load(profileImage).placeholder(R.drawable.baseline_storefront_24).into(profileIv);

                            }catch (Exception e){
                                profileIv.setImageResource(R.drawable.baseline_storefront_24);

                            }
                            loadShops(city);
                            loadOrders();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrders() {

        ordersList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordersList = new ArrayList<>();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users")
                        .child(firebaseAuth.getUid())  // Query orders for the current user only
                        .child("Orders");

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ordersList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelOrderUser modelOrderUser = ds.getValue(ModelOrderUser.class);
                            ordersList.add(modelOrderUser);
                        }
                        adapterOrderUser = new AdapterOrderUser(MainUserActivity.this, ordersList);
                        ordersRv.setAdapter(adapterOrderUser);
                    }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle onCancelled
                                }
                            });
                }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadShops(String city) {
        shopList =  new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("Accounttype").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        shopList.clear();
                        for(DataSnapshot ds : snapshot.getChildren()){
                             ModelShop modelShop = ds.getValue(ModelShop.class);
                             shopList.add(modelShop);

                             }
                        adapterShop = new AdapterShop(MainUserActivity.this,shopList);
                        shopsRv.setAdapter(adapterShop);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}