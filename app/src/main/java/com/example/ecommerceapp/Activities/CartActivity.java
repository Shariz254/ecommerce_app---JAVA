package com.example.ecommerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Toast;

import com.example.ecommerceapp.Adapters.CartAdapter;
import com.example.ecommerceapp.Models.CartModel;
import com.example.ecommerceapp.Prevalent.Prevalent;
import com.example.ecommerceapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    Activity act;
    Context context;

    private RecyclerView cartList_recyclerview;
    private RecyclerView.LayoutManager layoutManager;

    private Button next_process_btn;
    private TextView total_price, msg1;

    public int allTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        act = this;
        context = this;

        cartList_recyclerview = findViewById(R.id.cartList_recyclerview);
        cartList_recyclerview.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        cartList_recyclerview.setLayoutManager(layoutManager);

        next_process_btn = findViewById(R.id.next_process_btn);
        next_process_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                total_price.setText("Total Price: " + Integer.valueOf(allTotalPrice));

                Intent intent = new Intent(act, ConfirmOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(allTotalPrice));
                startActivity(intent);
                finish();
            }
        });

        msg1 = findViewById(R.id.msg1);
        total_price = findViewById(R.id.total_price);
        total_price.setText("Total Price: " + Integer.valueOf(allTotalPrice));

    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("CartList");

        FirebaseRecyclerOptions<CartModel> options = new FirebaseRecyclerOptions.Builder<CartModel>()
                .setQuery(reference.child("User View")
                .child(Prevalent.CurrentOnlineUser.getPhone())
                .child("Products"), CartModel.class)
                .build();

        FirebaseRecyclerAdapter<CartModel, CartAdapter> adapter = new FirebaseRecyclerAdapter<CartModel, CartAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartAdapter holder, int position, @NonNull CartModel model) {
                holder.cart_product_name.setText(model.getPname());
                holder.cart_product_price.setText("Price: " + model.getPrice());
                holder.cart_product_quantity.setText("Quantity: " + model.getQuantity());

                int oneTypeProductTotalPrice = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                allTotalPrice = allTotalPrice + oneTypeProductTotalPrice;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]
                                {
                                    "Edit",
                                    "Remove"
                                };

                        AlertDialog.Builder builder = new AlertDialog.Builder(act);
                        builder.setTitle("Cart Options:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i == 0) //index 0 stands for the Edit
                                {
                                    Intent intent = new Intent(act, ProductDetailsActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }

                                if (i == 1)//index 0 stands for the Remove
                                {
                                    reference.child("User View").child(Prevalent.CurrentOnlineUser.getPhone())
                                            .child("Products")
                                            .child(model.getPid())
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        Toast.makeText(act, "Item Removed", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(act, CartActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        });

                        builder.show();

                    }
                });

            }

            @NonNull
            @Override
            public CartAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items, parent, false);
                CartAdapter holder = new CartAdapter(view);
                return holder;
            }
        };

        cartList_recyclerview.setAdapter(adapter);
        adapter.startListening();
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
                    String userName = snapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped"))
                    {
                        total_price.setText("Dear " +userName + "\n order is shipped Successfully.");
                        cartList_recyclerview.setVisibility(View.GONE);

                        msg1.setVisibility(View.VISIBLE);
                        msg1.setText("Congratulations! Your Final Order Has been Placed Successfully. Order will be shipped soon to your destionation.");
                        next_process_btn.setVisibility(View.GONE);

                        Toast.makeText(act, "Thankyou For Shopping WIth Us", Toast.LENGTH_SHORT).show();
                    }
                    else if (shippingState.equals("not shipped"))
                    {
                        total_price.setText("Shipping State = Not Shipped");
                        cartList_recyclerview.setVisibility(View.GONE);

                        msg1.setVisibility(View.VISIBLE);
                        next_process_btn.setVisibility(View.GONE);

                        Toast.makeText(act, "Thankyou For Shopping WIth Us", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}