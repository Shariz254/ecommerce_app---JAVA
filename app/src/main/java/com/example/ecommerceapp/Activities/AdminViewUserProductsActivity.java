package com.example.ecommerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.Adapters.CartAdapter;
import com.example.ecommerceapp.Models.CartModel;
import com.example.ecommerceapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminViewUserProductsActivity extends AppCompatActivity {

    Activity act;
    Context context;

    private RecyclerView admin_view_order_products_recyclerview;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartListReference;

    private String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user_products);


        act = this;
        context = this;

        userID = getIntent().getStringExtra("uid");

        admin_view_order_products_recyclerview = findViewById(R.id.admin_view_order_products_recyclerview);
        admin_view_order_products_recyclerview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        admin_view_order_products_recyclerview.setLayoutManager(layoutManager);


        cartListReference = FirebaseDatabase.getInstance().getReference().child("CartList")
        .child("Admin View").child(userID).child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<CartModel> options = new FirebaseRecyclerOptions.Builder<CartModel>()
                .setQuery(cartListReference, CartModel.class)
                .build();

        FirebaseRecyclerAdapter<CartModel, CartAdapter> adapter = new FirebaseRecyclerAdapter<CartModel, CartAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartAdapter holder, int position, @NonNull CartModel model) {
                holder.cart_product_name.setText(model.getPname());
                holder.cart_product_price.setText("Price: " + model.getPrice());
                holder.cart_product_quantity.setText("Quantity: " + model.getQuantity());

            }

            @NonNull
            @Override
            public CartAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items, parent, false);
                CartAdapter holder = new CartAdapter(view);
                return holder;
            }
        };

        admin_view_order_products_recyclerview.setAdapter(adapter);
        adapter.startListening();
    }
}