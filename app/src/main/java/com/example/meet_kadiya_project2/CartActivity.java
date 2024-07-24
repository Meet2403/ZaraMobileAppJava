package com.example.meet_kadiya_project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meet_kadiya_project2.adapter.CartAdapter;
import com.example.meet_kadiya_project2.model.ProductWithQuantity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    RecyclerView RVCart;
    CartAdapter cartAdapter;
    ArrayList<ProductWithQuantity> productWithQuantityArrayList;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    Button CheckoutBtn;
    TextView totalPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Cart");
        }

        RVCart = findViewById(R.id.RVCart);
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        CheckoutBtn = findViewById(R.id.CheckoutBtn);

        productWithQuantityArrayList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = firebaseDatabase.getReference("Cart").child(uid);

        CheckoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productWithQuantityArrayList.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Your cart is empty. Add products before checkout.", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
                }
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productWithQuantityArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ProductWithQuantity productWithQuantity = dataSnapshot.getValue(ProductWithQuantity.class);

                    productWithQuantityArrayList.add(productWithQuantity);
                }
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cartAdapter = new CartAdapter(this, productWithQuantityArrayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        RVCart.setLayoutManager(linearLayoutManager);
        RVCart.setAdapter(cartAdapter);
    }

    private void updateTotalPrice() {
        double totalPrice = cartAdapter.calculateTotalPrice();
        Log.d("CartActivity", "Total Price: " + totalPrice);
        String formattedTotalPrice = String.format("Total Price: $ %.2f", totalPrice);
        totalPriceTextView.setText(formattedTotalPrice);
    }
}