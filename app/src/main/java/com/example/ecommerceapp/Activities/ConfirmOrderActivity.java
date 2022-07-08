package com.example.ecommerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerceapp.Prevalent.Prevalent;
import com.example.ecommerceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmOrderActivity extends AppCompatActivity {

    Activity act;
    Context context;

    private EditText shipment_name, shipment_phone_number, shipment_address, shipment_city;
    private Button confirm_order_btn;

    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        act = this;
        context = this;

        shipment_name = findViewById(R.id.shipment_name);
        shipment_phone_number = findViewById(R.id.shipment_phone_number);
        shipment_address = findViewById(R.id.shipment_address);
        shipment_city = findViewById(R.id.shipment_city);
        confirm_order_btn = findViewById(R.id.confirm_order_btn);

        totalAmount = getIntent().getStringExtra("Total Price");

        confirm_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });
    }

    private void check() {
        if (TextUtils.isEmpty(shipment_name.getText().toString()))
        {
            Toast.makeText(act, "Please provide Name",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(shipment_phone_number.getText().toString()))
        {
            Toast.makeText(act, "Please provide Number",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(shipment_address.getText().toString()))
        {
            Toast.makeText(act, "Please provide address",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(shipment_city.getText().toString()))
        {
            Toast.makeText(act, "Please provide city",Toast.LENGTH_SHORT).show();
        }

        else
        {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {
        final String saveCurrentTime , saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");

        saveCurrentDate = currentDate.format(calForDate.getTime());
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference ordersReference = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.CurrentOnlineUser.getPhone());

        HashMap<String, Object> ordersMap = new HashMap<>();

        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", shipment_name.getText().toString());
        ordersMap.put("phone", shipment_phone_number.getText().toString());
        ordersMap.put("address", shipment_address.getText().toString());
        ordersMap.put("city", shipment_city.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state", "not shipped");

        ordersReference.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference().child("CartList")
                            .child("User View")
                            .child(Prevalent.CurrentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(act, "Your Order Is Placed Successfully", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(act, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
            }
        });
    }
}