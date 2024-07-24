package com.example.meet_kadiya_project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.meet_kadiya_project2.model.ZaraProducts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckoutActivity extends AppCompatActivity {

    EditText cardNumber,firstname,lastname,streetaddress,city,delInc,postalCode,cvv,month,year;
    Button btnPlaceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Check Out");
        }

        cardNumber = findViewById(R.id.cardNumber);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        streetaddress = findViewById(R.id.streetaddress);
        city = findViewById(R.id.city);
        delInc = findViewById(R.id.delInc);
        postalCode = findViewById(R.id.postalCode);
        cvv = findViewById(R.id.cvv);
        month = findViewById(R.id.month);
        year = findViewById(R.id.year);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName = firstname.getText().toString().trim();
                String lastName = lastname.getText().toString().trim();
                String street = streetaddress.getText().toString().trim();
                String cityText = city.getText().toString().trim();
                String deliveryInstructions = delInc.getText().toString().trim();
                String postalcode = postalCode.getText().toString().trim();

                if (firstName.isEmpty() || lastName.isEmpty() || street.isEmpty() || cityText.isEmpty() || deliveryInstructions.isEmpty()) {
                    Toast.makeText(CheckoutActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isValidCanadianPostalCode(postalcode)) {
                    Toast.makeText(CheckoutActivity.this, "Invalid postal code", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(cardNumber.length() != 16){
                    Toast.makeText(CheckoutActivity.this, "Invalid card number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(cvv.length() != 3)
                {
                    Toast.makeText(CheckoutActivity.this, "Invalid CVV", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(month.length() != 2 || year.length() != 2)
                {
                    Toast.makeText(CheckoutActivity.this, "Invalid expiration date", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                        .child("Cart")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                cartRef.removeValue();

                Toast.makeText(CheckoutActivity.this, "Order has been placed. Thank you!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(CheckoutActivity.this, ThanksActivity.class));
            }
        });

    }
    public boolean isValidCanadianPostalCode(String postalCode) {
        String postalCodePattern = "[A-Za-z]\\d[A-Za-z] \\d[A-Za-z]\\d";
        return postalCode.matches(postalCodePattern);
    }
}