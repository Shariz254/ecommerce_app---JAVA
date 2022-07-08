package com.example.ecommerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ecommerceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {

    Activity act;
    Context context;

    private Button product_apply_changes_btn, delete_product;
    private EditText productDescription_maintain, product_Name_maintain, productPrice_maintain;
    private ImageView productImage_maintain;

    private String productId = "", state = "Normal" ;
    private DatabaseReference productsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);

        act = this;
        context = this;

        productId = getIntent().getStringExtra("pid");
        productsReference = FirebaseDatabase.getInstance().getReference().child("Products").child(productId);


        productDescription_maintain = findViewById(R.id.productDescription_maintain);
        product_Name_maintain = findViewById(R.id.product_Name_maintain);
        productPrice_maintain = findViewById(R.id.productPrice_maintain);
        productImage_maintain = findViewById(R.id.productImage_maintain);
        product_apply_changes_btn = findViewById(R.id.product_apply_changes_btn);
        delete_product = findViewById(R.id.delete_product);

        product_apply_changes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyChanges();
            }
        });

        delete_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteProduct();
            }
        });


        displaySpecificProductInfo();
    }

    private void deleteProduct() {
        productsReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Intent intent = new Intent(act, AdminCategoryActivity.class);
                startActivity(intent);
                finish();

                Toast.makeText(act, "Product Has Been Deleted Successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void applyChanges() {
        String Pname = product_Name_maintain.getText().toString();
        String Pprice = productPrice_maintain.getText().toString();
        String Pdescription= productDescription_maintain.getText().toString();

        if (Pname.equals(""))
        {
            Toast.makeText(act, "Please Input Product Name", Toast.LENGTH_SHORT).show();
        }
        else if (Pprice.equals(""))
        {
            Toast.makeText(act, "Please Input Product Price", Toast.LENGTH_SHORT).show();
        }
        else if (Pdescription.equals(""))
        {
            Toast.makeText(act, "Please Input Product Description", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, Object> productMap = new HashMap<>();

            productMap.put("id", productId);
            productMap.put("description", Pdescription);
            productMap.put("price", Pprice);
            productMap.put("pname", Pname);

            productsReference.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(act, "Changes applied successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(act, AdminCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displaySpecificProductInfo() {
        productsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String pName = snapshot.child("pname").getValue().toString();
                    String pPrice = snapshot.child("price").getValue().toString();
                    String pDescription = snapshot.child("description").getValue().toString();
                    String pImage = snapshot.child("image").getValue().toString();

                    product_Name_maintain.setText(pName);
                    productPrice_maintain.setText(pPrice);
                    productDescription_maintain.setText(pDescription);
                    Picasso.get().load(pImage).into(productImage_maintain);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}