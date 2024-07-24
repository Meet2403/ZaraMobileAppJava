package com.example.meet_kadiya_project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meet_kadiya_project2.adapter.ProductAdapter;
import com.example.meet_kadiya_project2.model.ZaraProducts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    ProductAdapter productAdapter;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    RecyclerView RVrecycleview;
    ArrayList<ZaraProducts> zaraProductsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            actionBar.setDisplayShowCustomEnabled(true);
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.title_image,null);

            ImageView customImageView = view.findViewById(R.id.customImageView);
            TextView customTextView = view.findViewById(R.id.customTextView);

            customTextView.setText("Products");

            customImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProductActivity.this, CartActivity.class));
                }
            });
            actionBar.setCustomView(view);

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        RVrecycleview = findViewById(R.id.RVrecycleview);
        RVrecycleview.setHasFixedSize(true);
        RVrecycleview.setLayoutManager(new GridLayoutManager(this, 2));

        zaraProductsArrayList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Products");

        productAdapter = new ProductAdapter(this, zaraProductsArrayList);
        RVrecycleview.setAdapter(productAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                zaraProductsArrayList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ZaraProducts ZaraProduct = dataSnapshot.getValue(ZaraProducts.class);
                    zaraProductsArrayList.add(ZaraProduct);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}