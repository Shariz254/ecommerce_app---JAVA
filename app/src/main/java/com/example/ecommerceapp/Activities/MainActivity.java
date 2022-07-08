package com.example.ecommerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerceapp.Activities.LoginActivity;
import com.example.ecommerceapp.Activities.RegisterActivity;
import com.example.ecommerceapp.Models.Users;
import com.example.ecommerceapp.Prevalent.Prevalent;
import com.example.ecommerceapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Activity act;
    Context context;

    ProgressDialog pd;

    private Button login_btn, register_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        act = this;
        context = this;

        pd = new ProgressDialog(this);

        login_btn = findViewById(R.id.login_btn);
        register_btn = findViewById(R.id.register_btn);

        Paper.init(this);

        
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, LoginActivity.class);
                startActivity(intent);
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, RegisterActivity.class);
                startActivity(intent);
            }
        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);
        
        if (UserPhoneKey != null && UserPasswordKey != null)
        {
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){

                AllowAccess(UserPhoneKey, UserPasswordKey);

                pd.setTitle("Already Logged In");
                pd.setMessage("Please Wait..");
                pd.setCanceledOnTouchOutside(false);
                pd.show();
            }
        }
    }

    private void AllowAccess(final String phone, final String password) {

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(phone).exists()) {

                    Users userData = snapshot.child("Users").child(phone).getValue(Users.class);

                    if (userData.getPhone().equals(phone))
                    {
                        if (userData.getPassword().equals(password))
                        {
                            Toast.makeText(act, "You are Already Logged In..", Toast.LENGTH_SHORT).show();
                            pd.dismiss();

                            Intent intent = new Intent(act, HomeActivity.class);
                            Prevalent.CurrentOnlineUser = userData;
                            startActivity(intent);
                        } else {
                            pd.dismiss();
                            Toast.makeText(act, "Password is Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(act, "Account with " +phone+ " does not Exist", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}