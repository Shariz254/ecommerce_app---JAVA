package com.example.ecommerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ecommerceapp.Models.AdminOrdersModel;
import com.example.ecommerceapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrdersActivity extends AppCompatActivity {

    Activity act;
    Context context;

    private RecyclerView admin_new_orders_recyclerview;
    private DatabaseReference adminOrdersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        act = this;
        context = this;

        adminOrdersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        admin_new_orders_recyclerview =  findViewById(R.id.admin_new_orders_recyclerview);
        admin_new_orders_recyclerview.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrdersModel> options = new FirebaseRecyclerOptions.Builder<AdminOrdersModel>()
                .setQuery(adminOrdersRef, AdminOrdersModel.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrdersModel, AdminOrdersViewHolder> adapter = new FirebaseRecyclerAdapter<AdminOrdersModel, AdminOrdersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull AdminOrdersModel model) {
                holder.order_username.setText("Name: " +model.getName());
                holder.order_phoneno.setText("Phone No: " +model.getPhone());
                holder.order_date_time.setText("Ordered On: " +model.getDate() + " " + model.getTime());
                holder.order_total_price.setText("Total Amount: " +model.getTotalAmount());
                holder.order_address.setText("Address: " +model.getAddress() + ", " +model.getCity());

                holder.show_all_products.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String uid = getRef(position).getKey();

                        Intent intent = new Intent(act, AdminViewUserProductsActivity.class);
                        intent.putExtra("uid", uid);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Yes",
                                        "No"
                                };

                        AlertDialog.Builder builder = new AlertDialog.Builder(act);
                        builder.setTitle("Have You Shipped This Product.?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0)
                                {
                                    String uid = getRef(position).getKey();

                                    RemoveOrder(uid);
                                }
                                else
                                {
                                    finish();
                                }
                            }
                        });
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_orders, parent, false);
                return new AdminOrdersViewHolder(view);
            }
        };

        admin_new_orders_recyclerview.setAdapter(adapter);
        adapter.startListening();
    }



    public  static  class AdminOrdersViewHolder extends RecyclerView.ViewHolder{

        public TextView order_username, order_phoneno, order_total_price, order_address, order_date_time;
        public Button show_all_products;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            order_username = itemView.findViewById(R.id.order_username);
            order_phoneno = itemView.findViewById(R.id.order_phoneno);
            order_total_price = itemView.findViewById(R.id.order_total_price);
            order_address = itemView.findViewById(R.id.order_address);
            order_date_time = itemView.findViewById(R.id.order_date_time);
            show_all_products = itemView.findViewById(R.id.show_all_products);

        }
    }

    private void RemoveOrder(String uid) {
        adminOrdersRef.child(uid).removeValue();

    }
}