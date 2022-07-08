package com.example.ecommerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.ecommerceapp.Adapters.AllProductsAdapters;
import com.example.ecommerceapp.Models.Products;
import com.example.ecommerceapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchProductsActivity extends AppCompatActivity {

    Activity act;
    Context context;

    private Button search_btn;
    private EditText search_product_name;
    private RecyclerView search_list_recyclerView;

    private String searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        act = this;
        context = this;

        search_btn = findViewById(R.id.search_btn);
        search_product_name = findViewById(R.id.search_product_name);
        search_list_recyclerView = findViewById(R.id.search_list_recyclerView);

        search_list_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchInput = search_product_name.getText().toString();

                onStart();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference.orderByChild("pname").startAt(searchInput), Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, AllProductsAdapters> adapter =  new FirebaseRecyclerAdapter<Products, AllProductsAdapters>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllProductsAdapters holder, int position, @NonNull Products model) {
                holder.product_Nm.setText(model.getPname());
                holder.product_Des.setText(model.getDescription());
                holder.product_Price.setText("Price: " + model.getPrice());
//                holder.product_Price.setText("Price: " + model.getPrice() + "$");

                Picasso.get().load(model.getImage()).into(holder.imageProduct);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(act, ProductDetailsActivity.class);
                        intent.putExtra("pid", model.getId());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public AllProductsAdapters onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items, parent,false);
                AllProductsAdapters holder = new AllProductsAdapters(view);
                return holder;            }
        };
        search_list_recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}