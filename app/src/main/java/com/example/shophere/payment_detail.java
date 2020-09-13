package com.example.shophere;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CvvEditText;
import com.braintreepayments.cardform.view.ExpirationDateEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class payment_detail extends AppCompatActivity {
    String pid, user_ID, history_id, product_id;
    int quantity, ProductQuantity;
    double ProductPrice, totalPrice, subPrice;

    Button buy;
    TextView p, cancel;
    CardEditText cardnum;
    CvvEditText cardcvv;
    ExpirationDateEditText cardexp;
    int num, gotShp, buyAll, quan, leftStock, pq;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference, dr, dr2, findNumHistory, myHistory, myAllHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        buy = (Button)findViewById(R.id.continuepay);
        p = (TextView)findViewById(R.id.priID);
        cancel = (TextView)findViewById(R.id.cancel);
        cardnum = findViewById(R.id.bt_card_form_card_number);
        cardcvv = findViewById(R.id.bt_card_form_cvv);
        cardexp = findViewById(R.id.bt_card_form_expiration);
        mFirebaseAuth = FirebaseAuth.getInstance();
        user_ID = mFirebaseAuth.getCurrentUser().getUid();

        buyAll = getIntent().getIntExtra("buyAll",0);
        if (buyAll != 0){
            subPrice = getIntent().getDoubleExtra("subPrice",0);
            p.setText(String.format("RM %.2f", subPrice));

        }else{
            pid = getIntent().getStringExtra("productid");
            gotShp = getIntent().getIntExtra("gotShopping",0);
            if(pid != null) {
                quantity = getIntent().getIntExtra("quan", 0);
            }
            String checkProduct = pid.substring(0, 2);
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
            databaseReference = dr.child(pid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ProductPrice = dataSnapshot.child("product_price").getValue(double.class);
                    ProductQuantity = dataSnapshot.child("product_stock").getValue(int.class);

                    totalPrice = ProductPrice * quantity;
                    p.setText(String.format("RM %.2f", totalPrice));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardNum = String.valueOf(cardnum.getText());
                String cardType = cardnum.getCardType().name();
                String cardCVV = cardcvv.getText().toString();
                String cardExp = cardexp.getText().toString();
                if(cardType == "UNKNOWN"){
                    Toast.makeText(payment_detail.this, "Credit Card UNKNOWN !!!", Toast.LENGTH_SHORT).show();
                }else if(cardType == "EMPTY"){
                    Toast.makeText(payment_detail.this, "Credit Card EMPTY !!!", Toast.LENGTH_SHORT).show();
                }else if(cardNum == null){
                    Toast.makeText(payment_detail.this, " UNKNOWN !!!", Toast.LENGTH_SHORT).show();
                }else if(cardNum.length()!=16){
                    Toast.makeText(payment_detail.this, " 16 Number of Credit Card !!!", Toast.LENGTH_SHORT).show();
                }else if(cardExp.isEmpty()){
                    Toast.makeText(payment_detail.this, "Expiration Date EMPTY !!!", Toast.LENGTH_SHORT).show();
                }else if(cardExp.length() != 4){
                    Toast.makeText(payment_detail.this, "Invalid Expiration Date !!!", Toast.LENGTH_SHORT).show();
                }else if(cardCVV.isEmpty()){
                    Toast.makeText(payment_detail.this, "CVV EMPTY !!!", Toast.LENGTH_SHORT).show();
                }else if(cardCVV.length() != 3){
                    Toast.makeText(payment_detail.this, "Invalid CVV !!!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(payment_detail.this, "We are under construction!!!", Toast.LENGTH_SHORT).show();
                    if (buyAll != 0) {
                        databaseReference = firebaseDatabase.getReference("users").child(user_ID).child("shopping_cart");
                        databaseReference.orderByChild("shoppingCart_id").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                                    history_id = "H" + (num);
                                    product_id = childSnapshot.child("product_id").getValue(String.class);
                                    quan = childSnapshot.child("quantity").getValue(int.class);
                                    double product_price = childSnapshot.child("product_price").getValue(double.class);

                                    myHistory = firebaseDatabase.getReference("users").child(user_ID).child("history").child(history_id);
                                    myHistory.child("history_id").setValue(history_id);
                                    myHistory.child("product_id").setValue(product_id);
                                    myHistory.child("quantity").setValue(quan);
                                    double totPri = quan * product_price;
                                    myHistory.child("payment_price").setValue(totPri);

                                    myAllHistory = firebaseDatabase.getReference("AllHistory").child(history_id);
                                    myAllHistory.child("history_id").setValue(history_id);
                                    myAllHistory.child("customer_id").setValue(user_ID);
                                    myAllHistory.child("product_id").setValue(product_id);
                                    myAllHistory.child("quantity").setValue(quan);
                                    myAllHistory.child("payment_price").setValue(totPri);

                                    String first = product_id.substring(0, 2);
                                    switch (first) {
                                        case "PA":
                                            dr2 = firebaseDatabase.getReference("product_artcraft");
                                            break;
                                        case "PB":
                                            dr2 = firebaseDatabase.getReference("product_book");
                                            break;
                                        case "PC":
                                            dr2 = firebaseDatabase.getReference("product_computer");
                                            break;
                                        case "PF":
                                            dr2 = firebaseDatabase.getReference("product_fashion");
                                            break;
                                        case "PH":
                                            dr2 = firebaseDatabase.getReference("product_healthhousehold");
                                            break;
                                        case "PK":
                                            dr2 = firebaseDatabase.getReference("product_homekitchen");
                                            break;
                                        case "PM":
                                            dr2 = firebaseDatabase.getReference("product_movietelevision");
                                            break;
                                        case "PP":
                                            dr2 = firebaseDatabase.getReference("product_petsupplies");
                                            break;
                                        case "PS":
                                            dr2 = firebaseDatabase.getReference("product_software");
                                            break;
                                        case "PV":
                                            dr2 = firebaseDatabase.getReference("product_videogames");
                                            break;
                                    }
                                    dr2.child(product_id).child("product_stock").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            pq = dataSnapshot.getValue(int.class);
                                            leftStock = pq - quan;
                                            dataSnapshot.getRef().setValue(leftStock);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    firebaseDatabase.getReference("IDNumStore").child("historyNum").setValue(num);
                                    num++;
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        firebaseDatabase.getReference("users").child(user_ID).child("shopping_cart").removeValue();
                        finish();
                        //Intent n = new Intent(payment_detail.this, PurchaseHistory.class);
                        //startActivity(n);
                    }else{
                        history_id = "H" + (num);
                        myHistory = firebaseDatabase.getReference("users").child(user_ID).child("history").child(history_id);
                        myHistory.child("history_id").setValue(history_id);
                        myHistory.child("product_id").setValue(pid);
                        myHistory.child("quantity").setValue(quantity);
                        myHistory.child("payment_price").setValue(totalPrice);
                        int leftStock = ProductQuantity - quantity;
                        databaseReference.child("product_stock").setValue(leftStock);
                        firebaseDatabase.getReference("IDNumStore").child("historyNum").setValue(num);

                        Intent intent = new Intent(payment_detail.this, PaymentComplete.class);//pass data to paymentcomplete
                        intent.putExtra("history", history_id);
                        intent.putExtra("productID", pid);
                        intent.putExtra("qt", quantity);

                        myAllHistory = firebaseDatabase.getReference("AllHistory").child(history_id);//write to database
                        myAllHistory.child("history_id").setValue(history_id);
                        myAllHistory.child("customer_id").setValue(user_ID);
                        myAllHistory.child("product_id").setValue(pid);
                        myAllHistory.child("quantity").setValue(quantity);
                        myAllHistory.child("payment_price").setValue(totalPrice);

                        if (gotShp != 0 ){
                            String shopID = getIntent().getStringExtra("shoppingID");
                            firebaseDatabase.getReference("users").child(user_ID).child("shopping_cart").child(shopID).removeValue();
                        }
                        finish();
                        startActivity(intent);
                    }
                }
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();

        findNumHistory = firebaseDatabase.getReference("IDNumStore").child("historyNum");
        findNumHistory.addValueEventListener(new ValueEventListener() {
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
}