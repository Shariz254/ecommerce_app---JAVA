package com.example.ecommerceapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ecommerceapp.R;

public class AdminCategoryActivity extends AppCompatActivity {

    Activity act;
    Context context;

    private ImageView t_shirts, sports_tshirt, female_dresses, sweaters;
    private ImageView glasses, purses, hats, shoes;
    private ImageView headphones, laptops, watches, mobile;
    private Button check_orders_btn, admin_logout_btn, maintain_products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        act = this;
        context = this;

        check_orders_btn =  findViewById(R.id.check_orders_btn);
        admin_logout_btn =  findViewById(R.id.admin_logout_btn);
        maintain_products =  findViewById(R.id.maintain_products);

        admin_logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        check_orders_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });

        maintain_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, HomeActivity.class);
                intent.putExtra("Admin", "Admin");
                startActivity(intent);
            }
        });


        t_shirts = findViewById(R.id.t_shirts);
        t_shirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, AdminAddNewProductActivity.class);
                intent.putExtra("category", "t_shirts");
                startActivity(intent);
            }
        });

        sports_tshirt = findViewById(R.id.sports_tshirt);
        sports_tshirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, AdminAddNewProductActivity.class);
                intent.putExtra("category", "sports_tshirt");
                startActivity(intent);
            }
        });

        female_dresses = findViewById(R.id.female_dresses);
        female_dresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, AdminAddNewProductActivity.class);
                intent.putExtra("category", "female_dresses");
                startActivity(intent);
            }
        });

        sweaters = findViewById(R.id.sweaters);
        sweaters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, AdminAddNewProductActivity.class);
                intent.putExtra("category", "sweaters");
                startActivity(intent);
            }
        });

        glasses = findViewById(R.id.glasses);
        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, AdminAddNewProductActivity.class);
                intent.putExtra("category", "glasses");
                startActivity(intent);
            }
        });

        purses = findViewById(R.id.purses);
        purses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, AdminAddNewProductActivity.class);
                intent.putExtra("category", "purses");
                startActivity(intent);
            }
        });

        hats = findViewById(R.id.hats);
        hats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, AdminAddNewProductActivity.class);
                intent.putExtra("category", "hats");
                startActivity(intent);
            }
        });

        shoes = findViewById(R.id.shoes);
        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, AdminAddNewProductActivity.class);
                intent.putExtra("category", "shoes");
                startActivity(intent);
            }
        });

        headphones = findViewById(R.id.headphones);
        headphones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, AdminAddNewProductActivity.class);
                intent.putExtra("category", "headphones");
                startActivity(intent);
            }
        });

        laptops = findViewById(R.id.laptops);
        laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, AdminAddNewProductActivity.class);
                intent.putExtra("category", "laptops");
                startActivity(intent);
            }
        });

        watches = findViewById(R.id.watches);
        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, AdminAddNewProductActivity.class);
                intent.putExtra("category", "watches");
                startActivity(intent);
            }
        });

        mobile = findViewById(R.id.mobile);
        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, AdminAddNewProductActivity.class);
                intent.putExtra("category", "mobile");
                startActivity(intent);
            }
        });


    }
}