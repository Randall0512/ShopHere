package com.example.shophere;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductOverview extends AppCompatActivity {
    private Spinner spinner;
    private ArrayList<String> arrayList = new ArrayList<>();
    TextView name, price, id, stock, aboutUs, description, detail, featuresNDetails;
    ImageView image;
    Spinner q;
    int stockQ, i, num, gotQuan, productQuantity, addQ;
    double pri;
    String shopID, productID, userID, shoppingCart_id;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference, dataRef, findNumShoppingCart;
    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase db;
    DatabaseReference myShoppingCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_overview);

        mFirebaseAuth = FirebaseAuth.getInstance();
        id = (TextView)findViewById(R.id.productOverviewID);
        name = (TextView)findViewById(R.id.productOverviewName);
        price = (TextView)findViewById(R.id.productOverviewPrice);
        image = (ImageView)findViewById(R.id.productOverviewImage);
        stock = (TextView)findViewById(R.id.quanlityStock);
        aboutUs = (TextView)findViewById(R.id.id_aboutUs);
        description = (TextView)findViewById(R.id.id_description);
        detail = (TextView)findViewById(R.id.id_detail);
        featuresNDetails = (TextView)findViewById(R.id.id_featuresndetail);
        q = (Spinner)findViewById(R.id.quantity);
        String productid = getIntent().getStringExtra("id");
        String message = getIntent().getStringExtra("type");
        gotQuan = getIntent().getIntExtra("quan", 0);
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
        dataRef = databaseReference.child(productid);
        id.setText(productid);

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
                    Intent choose = new Intent(ProductOverview.this, MainStore.class);
                    startActivity(choose);
                    break;
                case R.id.nav_restore:
                    Intent history = new Intent(ProductOverview.this, PurchaseHistory.class);
                    startActivity(history);
                    break;
                case R.id.nav_shopping_cart:
                    Intent shopping = new Intent(ProductOverview.this, Shopping_cart.class);
                    startActivity(shopping);
                    break;
                case R.id.nav_menu:
                    Intent menu = new Intent(ProductOverview.this, MenuBar.class);
                    startActivity(menu);
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String nm = dataSnapshot.child("product_name").getValue(String.class);
                String im = dataSnapshot.child("product_image").getValue(String.class);
                double pr = dataSnapshot.child("product_price").getValue(double.class);
                String ab = dataSnapshot.child("product_aboutus").getValue(String.class);
                String ds = dataSnapshot.child("product_description").getValue(String.class);
                String dt = dataSnapshot.child("product_detail").getValue(String.class);
                String fnd = dataSnapshot.child("product_featuresNdetails").getValue(String.class);
                stockQ = dataSnapshot.child("product_stock").getValue(int.class);
                name.setText(nm);
                Picasso.get().load(im).into(image);
                price.setText(String.format("RM %.2f", pr));
                aboutUs.setText(ab.replace("\\n", "\n"));
                description.setText(ds);
                detail.setText(dt.replace("\\n","\n"));
                featuresNDetails.setText("• "+fnd.replace("&", "\n• "));
                stock.setText(String.valueOf(stockQ));
                TextView textStock = (TextView) findViewById(R.id.id_gotStock);
                LinearLayout hs1 = findViewById(R.id.haveShow1), hs2 = findViewById(R.id.haveShow2);
                if (stockQ == 0){
                    textStock.setText(R.string.outstock_text);
                    hs1.setVisibility(View.GONE);
                    hs2.setVisibility(View.GONE);
                } else{
                    textStock.setText(R.string.instock_text);
                    hs1.setVisibility(View.VISIBLE);
                    hs2.setVisibility(View.VISIBLE);
                    spinner = findViewById(R.id.quantity);
                    arrayList.clear();
                    for (i = 1; i<=stockQ; i++){
                        arrayList.add(String.valueOf(i));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ProductOverview.this, R.layout.style_spinner, arrayList);
                    spinner.setAdapter(arrayAdapter);
                    if(gotQuan != 0){
                        spinner.setSelection(gotQuan-1);
                        shopID = getIntent().getStringExtra("shoppingID");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        findNumShoppingCart = firebaseDatabase.getReference("IDNumStore").child("shoppingCartNum");
        findNumShoppingCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                num = dataSnapshot.getValue(int.class);
                num += 1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void onAddCart(View view){

        userID = mFirebaseAuth.getCurrentUser().getUid();
        productID = id.getText().toString();
        pri = Double.parseDouble(price.getText().toString().replace("RM ", ""));
        productQuantity = Integer.valueOf(q.getSelectedItem().toString());
        db = FirebaseDatabase.getInstance();
        if(gotQuan != 0){
            myShoppingCart = db.getReference("users").child(userID).child("shopping_cart").child(shopID);
            myShoppingCart.child("product_id").setValue(productID);
            myShoppingCart.child("quantity").setValue(productQuantity);
            myShoppingCart.child("product_price").setValue(pri);
        }else {
            db.getReference("users").child(userID).child("shopping_cart").orderByChild("shoppingCart_id").
                    addListenerForSingleValueEvent(new ValueEventListener() {
                                              @Override
                                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                  boolean same = false;
                                                  for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                      String p = childSnapshot.child("product_id").getValue(String.class);
                                                      String s = childSnapshot.child("shoppingCart_id").getValue(String.class);
                                                      int qq = childSnapshot.child("quantity").getValue(int.class);
                                                      if (p.equals(productID)){
                                                          same = true;
                                                          shoppingCart_id = s;
                                                          addQ = qq;
                                                      }
                                                  }
                                                  if (same){
                                                      productQuantity += addQ;
                                                  }else{
                                                      shoppingCart_id = "SC" + (num);
                                                      db.getReference("IDNumStore").child("shoppingCartNum").setValue(num);
                                                  }
                                                  dataSnapshot.child(shoppingCart_id).child("shoppingCart_id").getRef().setValue(shoppingCart_id);
                                                  dataSnapshot.child(shoppingCart_id).child("product_id").getRef().setValue(productID);
                                                  dataSnapshot.child(shoppingCart_id).child("quantity").getRef().setValue(productQuantity);
                                                  dataSnapshot.child(shoppingCart_id).child("product_price").getRef().setValue(pri);
                                              }

                                              @Override
                                              public void onCancelled(@NonNull DatabaseError databaseError) {

                                              }
                                          });
            
        }
        finish();
    }
    public void onBuyNow(View view){

        Intent intent=new Intent(ProductOverview.this, payment_detail.class);
        productID = id.getText().toString();
        int quan = Integer.parseInt(q.getSelectedItem().toString());
        intent.putExtra("productid", productID);
        intent.putExtra("quan", quan);
        if(gotQuan != 0) {
            intent.putExtra("gotShopping", 1);
            intent.putExtra("shoppingID", shopID);
        }
        finish();
        startActivity(intent);
    }
}