package com.example.ecommerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerceapp.Models.Products;
import com.example.ecommerceapp.Prevalent.Prevalent;
import com.example.ecommerceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    Activity act;
    Context context;

    private ImageView product_image_details, addItem, removeItem;
    private Button add_to_cart;
    private TextView product_name_details, product_description_details, product_price_details, quantity;

    int totalQuantity = 1;

    private String productId = "", state = "Normal" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        act = this;
        context = this;

        productId = getIntent().getStringExtra("pid");

        product_image_details = findViewById(R.id.product_image_details);
        product_name_details = findViewById(R.id.product_name_details);
        product_description_details = findViewById(R.id.product_description_details);
        product_price_details = findViewById(R.id.product_price_details);
        quantity = findViewById(R.id.quantity);
        add_to_cart = findViewById(R.id.add_to_cart);
        addItem = findViewById(R.id.add_item);
        removeItem = findViewById(R.id.remove_item);


        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQuantity < 10){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));

                }
            }
        });

        removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalQuantity > 1){
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });

        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (state.equals("Order Placed") || state.equals("Order Shipped"))
                {
                    Toast.makeText(act, "You Can Purchase more Products Once Your Order is confirmed", Toast.LENGTH_LONG).show();
                }
                else
                {
                    addingToCart();
                }
            }
        });


        getProductDetails(productId);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        CheckOrderState();
    }

    private void addingToCart() {
        String saveCurrentTime , saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");

        saveCurrentDate = currentDate.format(calForDate.getTime());
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("CartList");

        HashMap<String, Object> cartMap = new HashMap<>();

        cartMap.put("pid", productId);
        cartMap.put("pname", product_name_details.getText().toString());
        cartMap.put("price", product_price_details.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", quantity.getText().toString());
        cartMap.put("discount", "");

        ref.child("User View").child(Prevalent.CurrentOnlineUser.getPhone())
                .child("Products").child(productId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    ref.child("Admin View").child(Prevalent.CurrentOnlineUser.getPhone())
                            .child("Products").child(productId)
                            .updateChildren(cartMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(act, "Added to Cart Successfully", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(act, HomeActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
            }
        });

    }

    private void getProductDetails(String productId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Products");

        ref.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Products products = snapshot.getValue(Products.class);

                    product_name_details.setText(products.getPname());
                    product_description_details.setText(products.getDescription());
                    product_price_details.setText(products.getPrice());

                    Picasso.get().load(products.getImage()).into(product_image_details);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void CheckOrderState()
    {
        DatabaseReference ordersReference = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.CurrentOnlineUser.getPhone());
        ordersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String shippingState = snapshot.child("state").getValue().toString();

                    if (shippingState.equals("shipped"))
                    {
                        state = "Order Shipped";
                    }
                    else if (shippingState.equals("not shipped"))
                    {
                        state = "Order Placed";

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}