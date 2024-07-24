package com.example.meet_kadiya_project2;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ThanksActivity extends AppCompatActivity {

    TextView thankYouMessage,btnGoToProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Thank You");
        }

        thankYouMessage = findViewById(R.id.thankYouMessage);
        btnGoToProducts = findViewById(R.id.btnGoToProducts);

        btnGoToProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ThanksActivity.this, ProductActivity.class));
            }
        });
    }
}