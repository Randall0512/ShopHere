package com.example.shophere;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Categories extends AppCompatActivity {

    String message;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    TextView no;
    ScrollView yes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        recyclerView = findViewById(R.id.recyclerView);
        no = (TextView)findViewById(R.id.noItem);
        yes = (ScrollView)findViewById(R.id.categories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("product_videogames");

        Intent intent = getIntent();
        message = intent.getStringExtra(MenuBar.EXTRA_MESSAGE);
        switch (message){
            case "artcraft":
                databaseReference = firebaseDatabase.getReference("product_artcraft");
                break;
            case "books":
                databaseReference = firebaseDatabase.getReference("product_book");
                break;
            case "computers":
                databaseReference = firebaseDatabase.getReference("product_computer");
                break;
            case "fashion":
                databaseReference = firebaseDatabase.getReference("product_fashion");
                break;
            case "healthhousehold":
                databaseReference = firebaseDatabase.getReference("product_healthhousehold");
                break;
            case "homekitchen":
                databaseReference = firebaseDatabase.getReference("product_homekitchen");
                break;
            case "movietelevision":
                databaseReference = firebaseDatabase.getReference("product_movietelevision");
                break;
            case "petsupplies":
                databaseReference = firebaseDatabase.getReference("product_petsupplies");
                break;
            case "software":
                databaseReference = firebaseDatabase.getReference("product_software");
                break;
            case "videogames":
                databaseReference = firebaseDatabase.getReference("product_videogames");
                break;
        }
        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_menu);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()){
                case R.id.nav_home:
                    Intent choose = new Intent(Categories.this, MainStore.class);
                    startActivity(choose);
                    break;
                case R.id.nav_restore:
                    Intent restore = new Intent(Categories.this, PurchaseHistory.class);
                    startActivity(restore);
                    break;
                case R.id.nav_shopping_cart:
                    Intent shopping = new Intent(Categories.this, Shopping_cart.class);
                    startActivity(shopping);
                    break;
                case R.id.nav_menu:
                    Intent menu = new Intent(Categories.this, MenuBar.class);
                    startActivity(menu);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    yes.setVisibility(View.VISIBLE);
                    no.setVisibility(View.GONE);
                }else{
                    yes.setVisibility(View.GONE);
                    no.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseRecyclerAdapter<product, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<product, ViewHolder>(
                        product.class,
                        R.layout.list_categories,
                        ViewHolder.class,
                        databaseReference
                ){
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, product product, int i) {
                        viewHolder.setdetails(getApplicationContext(), product.getProduct_id(), product.getProduct_name(), product.getProduct_image(),product.getProduct_price());
                    }

                    @Override
                    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        ViewHolder viewHolder = super.onCreateViewHolder(parent,viewType);
                        viewHolder.setOnclickListener(new ViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                TextView id = view.findViewById(R.id.productID);
                                String mid = id.getText().toString();

                                Intent intent = new Intent(view.getContext(), ProductOverview.class);
                                intent.putExtra("id", mid);
                                intent.putExtra("type", message);
                                startActivity(intent);
                            }
                            @Override
                            public void onItemLongClick(View view, int position) {

                            }
                        });
                        return viewHolder;
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}