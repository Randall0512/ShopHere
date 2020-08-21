package com.example.shophere;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Shopping_cart extends AppCompatActivity {
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, dr, dRef;
    TextView item, tolPrice, noItem;
    ConstraintLayout bil;
    ScrollView list;
    String currentUserID, shoppingID;
    String pn,pi, message;
    double totalPrice;
    int numStock, totalItem;
    FirebaseAuth mFirebaseAuth;

    public Shopping_cart() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        recyclerView = findViewById(R.id.HistoryCartList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = mFirebaseAuth.getCurrentUser().getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users").child(currentUserID).child("shopping_cart");

        bil = findViewById(R.id.bill);
        noItem = (TextView) findViewById(R.id.no);
        list = (ScrollView) findViewById(R.id.listCart);
        item = (TextView) findViewById(R.id.numSubtotal);
        tolPrice = (TextView) findViewById(R.id.SubTotalPrice);

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_shopping_cart);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()){
                case R.id.nav_home:
                    Intent choose = new Intent(Shopping_cart.this, MainStore.class);
                    startActivity(choose);
                    break;
                case R.id.nav_restore:
                    Intent history = new Intent(Shopping_cart.this, PurchaseHistory.class);
                    startActivity(history);
                    break;
                case R.id.nav_shopping_cart:
                    break;
                case R.id.nav_menu:
                    Intent menu = new Intent(Shopping_cart.this, MenuBar.class);
                    startActivity(menu);
                    break;
            }
            return true;
        }
    };
    public void delete( String sID){
        databaseReference.child(sID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    double pri = dataSnapshot.child("product_price").getValue(double.class);
                    int quan = dataSnapshot.child("quantity").getValue(int.class);
                    totalItem -= quan;
                    totalPrice -= pri * quan;
                    String it;
                    if (totalItem > 1) {
                        it = " item ) ";
                    } else {
                        it = " items ) ";
                    }
                    item.setText("( " + String.valueOf(totalItem) + it);
                    tolPrice.setText(String.format("RM %.2f", totalPrice));
                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child(sID).removeValue();
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    bil.setVisibility(View.VISIBLE);
                    list.setVisibility(View.VISIBLE);
                    noItem.setVisibility(View.GONE);
                }else{
                    bil.setVisibility(View.GONE);
                    list.setVisibility(View.GONE);
                    noItem.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final FirebaseRecyclerAdapter<product_ShoppingCart, ShoppingViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<product_ShoppingCart, ShoppingViewHolder>(
                        product_ShoppingCart.class,
                        R.layout.list_shopping_cart,
                        ShoppingViewHolder.class,
                        databaseReference
                ) {
                    @Override
                    public void populateViewHolder(final ShoppingViewHolder shoppingViewHolder, final product_ShoppingCart product, int i) {
                        String checkProduct = product.getProduct_id().substring(0, 2);
                        switch (checkProduct) {
                            case "PA":
                                dr = firebaseDatabase.getReference("product_artcraft");
                                break;
                            case "PB":
                                dr = firebaseDatabase.getReference("product_book");
                                break;
                            case "PC":
                                dr = firebaseDatabase.getReference("product_computer");
                                break;
                            case "PF":
                                dr = firebaseDatabase.getReference("product_fashion");
                                break;
                            case "PH":
                                dr = firebaseDatabase.getReference("product_healthhousehold");
                                break;
                            case "PK":
                                dr = firebaseDatabase.getReference("product_homekitchen");
                                break;
                            case "PM":
                                dr = firebaseDatabase.getReference("product_movietelevision");
                                break;
                            case "PP":
                                dr = firebaseDatabase.getReference("product_petsupplies");
                                break;
                            case "PS":
                                dr = firebaseDatabase.getReference("product_software");
                                break;
                            case "PV":
                                dr = firebaseDatabase.getReference("product_videogames");
                                break;
                        }
                        dRef = dr.child(product.getProduct_id());
                        dRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                pn = dataSnapshot.child("product_name").getValue(String.class);
                                pi = dataSnapshot.child("product_image").getValue(String.class);
                                numStock = dataSnapshot.child("product_stock").getValue(int.class);
                                shoppingViewHolder.setShopping(getApplicationContext(), product.getProduct_id(), product.getShoppingCart_id(), product.getQuantity(), pn, pi, product.getProduct_price(), numStock);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    @Override
                    public ShoppingViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
                        final ShoppingViewHolder viewHolder = super.onCreateViewHolder(parent,viewType);
                        viewHolder.setOnclickListener(new ShoppingViewHolder.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }
                            @Override
                            public void onItemLongClick(View view, int position) {
                                String productID = getItem(position).getProduct_id();
                                String first = productID.substring(0, 2);
                                switch (first){
                                    case "PA":
                                        message = "artcraft";
                                        break;
                                    case "PB":
                                        message = "books";
                                        break;
                                    case "PC":
                                        message = "computers";
                                        break;
                                    case "PF":
                                        message = "fashion";
                                        break;
                                    case "PH":
                                        message = "healthhousehold";
                                        break;
                                    case "PK":
                                        message = "homekitchen";
                                        break;
                                    case "PM":
                                        message = "movietelevision";
                                        break;
                                    case "PP":
                                        message = "petsupplies";
                                        break;
                                    case "PS":
                                        message = "software";
                                        break;
                                    case "PV":
                                        message = "videogames";
                                        break;
                                }
                                Intent intent = new Intent(view.getContext(), ProductOverview.class);
                                intent.putExtra("id", productID);
                                intent.putExtra("type", message);
                                intent.putExtra("quan", getItem(position).getQuantity());
                                intent.putExtra("shoppingID", getItem(position).getShoppingCart_id());
                                startActivity(intent);
                            }
                            @Override
                            public void onDeleteClick(View view, int position) {
                                shoppingID = getItem(position).getShoppingCart_id();
                                AlertDialog.Builder builder = new AlertDialog.Builder(Shopping_cart.this);
                                builder.setMessage("Are you sure you want DELETE this item?").setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                delete(shoppingID);
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                            }

                            @Override
                            public void onChangeQuantity(View view, int position, String quantityNum){
                                String productID = getItem(position).getProduct_id();
                                String SC = getItem(position).getShoppingCart_id();
                                databaseReference.child(SC).child("quantity").setValue(Integer.valueOf(quantityNum));

                            }

                        });
                        return viewHolder;
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        databaseReference.orderByChild("shoppingCart_id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalItem = 0;
                totalPrice = 0.00;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if(childSnapshot.child("quantity").exists() && childSnapshot.child("product_price").exists()) {
                        int quan = childSnapshot.child("quantity").getValue(int.class);
                        double pric = childSnapshot.child("product_price").getValue(double.class);
                        totalItem += quan;
                        totalPrice += pric * quan;
                        String it;
                        if (totalItem < 1) {
                            it = " item ) ";
                        } else {
                            it = " items ) ";
                        }
                        item.setText("( " + String.valueOf(totalItem) + it);
                        tolPrice.setText(String.format("RM %.2f", totalPrice));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void onClickBuy(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(Shopping_cart.this);
        builder.setMessage("Are you sure you want BUY ALL Item?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseReference.orderByChild("shoppingCart_id").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                                    int q = childSnapshot.child("quantity").getValue(int.class);
                                    if(q <=0){
                                        childSnapshot.getRef().removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        Intent intent=new Intent(Shopping_cart.this, payment_detail.class);
                        intent.putExtra("buyAll", 1);
                        intent.putExtra("subPrice",totalPrice);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}