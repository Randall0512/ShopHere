package com.example.shophere;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShoppingViewHolder extends RecyclerView.ViewHolder{
    View view;
    Button del;
    Spinner quanChange;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ArrayList<String> arrayList = new ArrayList<>();
    public ShoppingViewHolder (@NonNull View itemView) {
        super(itemView);
        view = itemView;
        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mClicklistener.onItemClick(view, getAdapterPosition());
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view){
                mClicklistener.onItemLongClick(view, getAdapterPosition());
                return false;
            }
        });
        del = view.findViewById(R.id.id_delete);
        del.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mClicklistener.onDeleteClick(view, getAdapterPosition());
            }
        });

        quanChange = view.findViewById(R.id.spinner);
        quanChange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String s = adapterView.getItemAtPosition(i).toString();
                mClicklistener.onChangeQuantity(view, getAdapterPosition(), s);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    public void setShopping(Context ct, String pID, String sID, int quantity, String pn, String pi, double pp, int numStock){
        CardView have = view.findViewById(R.id.card);
        have.setVisibility(View.VISIBLE);
        TextView text_shoppingID = view.findViewById(R.id.shoppingID);
        TextView text_productID = view.findViewById(R.id.productID);
        TextView text_productName = view.findViewById(R.id.productName);
        TextView text_productPrice = view.findViewById(R.id.productPrice);
        ImageView image_product = view.findViewById(R.id.productImage);
        TextView text_quantity = view.findViewById(R.id.textQuantity);
        Spinner spinner = view.findViewById(R.id.spinner);

        text_productID.setText(pID);
        text_shoppingID.setText(sID);
        text_productName.setText(pn);
        text_productPrice.setText(String.format("RM %.2f", pp));
        Picasso.get().load(pi).into(image_product);
        if(numStock == 0){
            text_quantity.setVisibility(View.INVISIBLE);
            spinner.setVisibility(View.INVISIBLE);
            arrayList.clear();
            arrayList.add(String.valueOf(0));
            firebaseDatabase.getReference("users").child(user).child("shopping_cart").child(sID).child("quantity").setValue(0);
            quantity = 0;
        }else if(numStock < quantity) {
            text_quantity.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            arrayList.clear();
            for (int i = 1; i<=numStock; i++){
                arrayList.add(String.valueOf(i));
            }
            quantity = numStock;
        }else{
            text_quantity.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.VISIBLE);
            arrayList.clear();
            for (int i = 1; i<=numStock; i++){
                arrayList.add(String.valueOf(i));
            }

        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ct, R.layout.style_spinner, arrayList);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(quantity - 1);

        Animation animation = AnimationUtils.loadAnimation(ct, android.R.anim.slide_in_left);
        itemView.startAnimation(animation);
    }
    private ShoppingViewHolder.ClickListener mClicklistener;
    public interface ClickListener {
        void onDeleteClick(View view, int position);
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
        void onChangeQuantity(View view, int position, String quantityNum);
    }
    public void setOnclickListener(ShoppingViewHolder.ClickListener clickListener){
        mClicklistener = clickListener;
    }
}
