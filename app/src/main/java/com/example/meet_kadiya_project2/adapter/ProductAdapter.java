package com.example.meet_kadiya_project2.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meet_kadiya_project2.DetailActivity;
import com.example.meet_kadiya_project2.R;
import com.example.meet_kadiya_project2.model.ZaraProducts;

import java.io.Serializable;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<ZaraProducts> zaraProductsArrayList;


    public ProductAdapter(Context context, ArrayList<ZaraProducts> zaraProductsArrayList) {
        this.context = context;
        this.zaraProductsArrayList = zaraProductsArrayList;
    }

    @NonNull
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutInflater = LayoutInflater.from(context).inflate(R.layout.row_layout,parent,false);
        return new MyViewHolder(layoutInflater);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.MyViewHolder holder, int position) {
        ZaraProducts product = zaraProductsArrayList.get(position);
        holder.PName.setText(product.getPName());
        holder.PPrice.setText("$" + product.getPPrice());


        Glide.with(context).load(product.getPImage()).into(holder.PImage);

        holder.PCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("product", (Serializable) product);
//                view.getContext().startActivity(intent);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return zaraProductsArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView PImage;
        CardView PCardview;
        TextView PPrice,PName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            PCardview = itemView.findViewById(R.id.PCardview);
            PImage = itemView.findViewById(R.id.PImage);
            PName = itemView.findViewById(R.id.PName);
            PPrice = itemView.findViewById(R.id.PPrice);
        }
    }
}
