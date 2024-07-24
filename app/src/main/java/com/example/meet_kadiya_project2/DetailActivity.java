package com.example.meet_kadiya_project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.meet_kadiya_project2.model.ProductWithQuantity;
import com.example.meet_kadiya_project2.model.ZaraProducts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailActivity extends AppCompatActivity {

    ImageView PImage;
    TextView PName, PPrice, Description;
    Button BTNAddtoCart;
    ZaraProducts products;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {

            actionBar.setDisplayShowCustomEnabled(true);
            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.title_image,null);

            ImageView customImageView = view.findViewById(R.id.customImageView);
            TextView customTextView = view.findViewById(R.id.customTextView);

            customTextView.setText("Product Details");

            customImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(DetailActivity.this, CartActivity.class));
                }
            });
            actionBar.setCustomView(view);

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
        }

        PImage = findViewById(R.id.PImage);
        PName = findViewById(R.id.PName);
        PPrice = findViewById(R.id.PPrice);
        Description = findViewById(R.id.Description);
        BTNAddtoCart = findViewById(R.id.BTNAddtoCart);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Cart");

        products = (ZaraProducts) getIntent().getSerializableExtra("product");

        Glide.with(DetailActivity.this).load(products.getPImage()).into(PImage);
        PName.setText(products.getPName());
        PPrice.setText(String.valueOf(" $" +products.getPPrice()));
        Description.setText(products.getDescription());

        BTNAddtoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
                startActivity(new Intent(DetailActivity.this, CartActivity.class));
            }
        });
    }

    private void addProduct() {

        ProductWithQuantity productWithQuantity = new ProductWithQuantity(products, 1 , "");
        try{

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            RadioGroup sizeRadio = findViewById(R.id.sizeRadio);

            int checkedRadioButtonId = sizeRadio.getCheckedRadioButtonId();

            if (checkedRadioButtonId == -1) {
                // No size selected
                Toast.makeText(DetailActivity.this, "Please select a size", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadioButton = findViewById(checkedRadioButtonId);
            String selectedSize = selectedRadioButton.getText().toString();
            productWithQuantity.setSize(selectedSize);

            databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean exist = false;
                    for(DataSnapshot data: snapshot.getChildren()){

                        if (data.hasChild("zaraProducts/pname")) {
                            String name = data.child("zaraProducts/pname").getValue(String.class);
                            if (TextUtils.equals(name, products.getPName())) {
                                int qty = data.child("quantity").getValue(Integer.class);
                                productWithQuantity.setQuantity(qty + 1);

                                // Update the existing product in the cart
                                databaseReference.child(uid).child(data.getKey()).setValue(productWithQuantity);

                                Toast.makeText(DetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                                exist = true;
                                break;
                            }
                        }
                    }

                    if(!exist){
                        String id = databaseReference.push().getKey();
                        databaseReference.child(uid).child(id).setValue(productWithQuantity);

                        Toast.makeText(DetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(String.valueOf(DetailActivity.this), "onCancelled: "+error);

                    Toast.makeText(DetailActivity.this, "Fail to add Course..", Toast.LENGTH_SHORT).show();
                }
            });

        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "Error while adding the product"+e, Toast.LENGTH_LONG).show();
        }
    }
}