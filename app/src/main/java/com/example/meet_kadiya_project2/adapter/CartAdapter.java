package com.example.meet_kadiya_project2.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meet_kadiya_project2.R;
import com.example.meet_kadiya_project2.model.ProductWithQuantity;
import com.example.meet_kadiya_project2.model.ZaraProducts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    DatabaseReference databaseReference;
    ProductWithQuantity product;
    private Context context;
    private ArrayList<ProductWithQuantity> productWithQuantityArrayList;

    public CartAdapter(Context context, ArrayList<ProductWithQuantity> productWithQuantityArrayList) {
        this.context = context;
        this.productWithQuantityArrayList = productWithQuantityArrayList;
    }

    @NonNull
    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Cart");
        View view = LayoutInflater.from(context).inflate(R.layout.cart_row_layout, parent, false);
        return new CartAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.MyViewHolder holder, int position) {

        product = productWithQuantityArrayList.get(position);
        ProductWithQuantity product = productWithQuantityArrayList.get(position);

        holder.PName.setText(product.getZaraProducts().getPName());
        holder.Size.setText("Size: " + product.getSize());
        String formattedPrice = String.format("%.2f", product.getQuantity() * product.getZaraProducts().getPPrice());
        holder.PPrice.setText("$ " + formattedPrice);
        holder.itemQuantity.setText(String.valueOf(product.getQuantity()));

        Glide.with(context).load(product.getZaraProducts().getPImage()).into(holder.PImage);

        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct(holder.getAdapterPosition());
            }
        });

        holder.subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subProduct(holder.getAdapterPosition());
            }
        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct(holder.getAdapterPosition());
            }
        });
    }

    public double calculateTotalPrice() {
        double totalPrice = 0.0;
        for (ProductWithQuantity product : productWithQuantityArrayList) {
            totalPrice += product.getQuantity() * product.getZaraProducts().getPPrice();
        }
        Log.d("CartAdapter", "Total Price: " + totalPrice);
        return totalPrice;
    }

    private void deleteProduct(int adapterPosition) {
        ProductWithQuantity product = productWithQuantityArrayList.get(adapterPosition);
        productWithQuantityArrayList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        removeProductFromDatabase(product);
        calculateTotalPrice();
    }
    private void addProduct(int adapterPosition) {
        if (adapterPosition >= 0 && adapterPosition < productWithQuantityArrayList.size()) {
            ProductWithQuantity product = productWithQuantityArrayList.get(adapterPosition);
            int currentQuantity = product.getQuantity();
            product.setQuantity(currentQuantity + 1);

            productWithQuantityArrayList.set(adapterPosition, product);
            notifyItemChanged(adapterPosition);
            calculateTotalPrice();

            updateQuantityInDatabase(product, adapterPosition, true);
        } else {
            Log.e("CartAdapter", "Invalid adapterPosition in addProduct: " + adapterPosition);
        }
    }

    private void updateQuantityInDatabase(ProductWithQuantity product, int position, boolean isIncrement) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (position >= 0 && position < productWithQuantityArrayList.size()) {
            Query query = databaseReference.child(uid).orderByChild("zaraProducts/pname").equalTo(product.getZaraProducts().getPName());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ProductWithQuantity existingProduct = snapshot.getValue(ProductWithQuantity.class);

                        if (existingProduct != null && TextUtils.equals(existingProduct.getSize(), product.getSize())) {
                            int newQuantity;
                            if (isIncrement) {
                                newQuantity = existingProduct.getQuantity() + 1;
                            } else {
                                newQuantity = existingProduct.getQuantity() - 1;
                            }
                            double newPrice = newQuantity * existingProduct.getZaraProducts().getPPrice();

                            existingProduct.setQuantity(newQuantity);
                            productWithQuantityArrayList.set(position, existingProduct);

                            databaseReference.child(uid).child(snapshot.getKey()).child("quantity").setValue(newQuantity)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Database update successful, calculate total price
                                            calculateTotalPrice();
                                        } else {
                                            Log.e("CartAdapter", "Error updating quantity in database: " + task.getException());
                                        }
                                    });

                            databaseReference.child(uid).child(snapshot.getKey()).child("totalPrice").setValue(newPrice);
                            return;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("CartAdapter", "Error updating quantity in database: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e("CartAdapter", "Invalid position: " + position);
        }
    }

    private void subProduct(int adapterPosition) {
        if (adapterPosition >= 0 && adapterPosition < productWithQuantityArrayList.size()) {
            ProductWithQuantity product = productWithQuantityArrayList.get(adapterPosition);
            int currentQuantity = product.getQuantity();
            Log.d("CartAdapter", "Current quantity before subtraction: " + currentQuantity);
            if (currentQuantity > 1) {
                product.setQuantity(currentQuantity - 1);
                notifyItemChanged(adapterPosition);
                updateQuantityInDatabase(product, adapterPosition, false);
                calculateTotalPrice();
            } else {
                Log.e("CartAdapter", "Quantity is already 1. No further subtraction is allowed.");
            }


            Log.d("CartAdapter", "New quantity after subtraction: " + product.getQuantity());
            Log.d("CartAdapter", "Product list size after subtraction: " + productWithQuantityArrayList.size());
        } else {
            Log.e("CartAdapter", "Invalid adapterPosition in subProduct: " + adapterPosition);
        }
    }
    private void removeProductFromDatabase(ProductWithQuantity product) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = databaseReference.child(uid).orderByChild("zaraProducts/pname").equalTo(product.getZaraProducts().getPName());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ProductWithQuantity existingProduct = snapshot.getValue(ProductWithQuantity.class);

                    if (existingProduct != null && TextUtils.equals(existingProduct.getSize(), product.getSize())) {
                        snapshot.getRef().removeValue();

                        Log.d("CartAdapter", "Product removed from the database.");

                        productWithQuantityArrayList.remove(product);
                        notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CartAdapter", "Error removing product from database: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productWithQuantityArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView PImage,deleteBtn;
        TextView PName;
        TextView PPrice,Size;
        TextView itemQuantity;
        Button addBtn;
        Button subBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            PImage = itemView.findViewById(R.id.PImage);
            PName = itemView.findViewById(R.id.PName);
            Size = itemView.findViewById(R.id.Size);
            PPrice = itemView.findViewById(R.id.PPrice);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
            addBtn = itemView.findViewById(R.id.addBtn);
            subBtn = itemView.findViewById(R.id.removeBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
