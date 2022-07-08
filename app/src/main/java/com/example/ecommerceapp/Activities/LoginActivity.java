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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerceapp.Models.Users;
import com.example.ecommerceapp.Prevalent.Prevalent;
import com.example.ecommerceapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    Activity act;
    Context context;

    private Button login;
    private EditText phone_no, login_password;
    private String parentDbName = "Users";
    private CheckBox remember_me_check;
    private TextView admin_panel_link, not_admin_panel_link;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        act = this;
        context = this;

        phone_no = findViewById(R.id.phone_no);
        login_password = findViewById(R.id.password);
        admin_panel_link = findViewById(R.id.admin_panel_link);
        not_admin_panel_link = findViewById(R.id.not_admin_panel_link);

        pd = new ProgressDialog(this);

        remember_me_check = findViewById(R.id.remember_me_check);
        Paper.init(this);

        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        admin_panel_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setText("Admin Login");
                admin_panel_link.setVisibility(View.INVISIBLE);
                not_admin_panel_link.setVisibility(View.VISIBLE);

                parentDbName = "Admins";
            }
        });

        not_admin_panel_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setText("Login");
                admin_panel_link.setVisibility(View.VISIBLE);
                not_admin_panel_link.setVisibility(View.INVISIBLE);

                parentDbName = "Users";
            }
        });
    }

    private void loginUser() {
        String phone = phone_no.getText().toString();
        String password = login_password.getText().toString();

        if (TextUtils.isEmpty(phone)){
            phone_no.setError("Required");
        }
        else if (TextUtils.isEmpty(password)){
            login_password.setError("Required");
        }

        else {
            pd.setTitle("Login Account");
            pd.setMessage("You are being Logged In. Please Wait..");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

            AllowAccessToAccount(phone, password);
        }
    }

    private void AllowAccessToAccount(String phone, String password)
    {

        if (remember_me_check.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(phone).exists()) {

                    Users userData = snapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (userData.getPhone().equals(phone))
                    {
                        if (userData.getPassword().equals(password))
                        {
                            if (parentDbName.equals("Admins"))
                            {
                                Toast.makeText(act, "Admin Logged In Successfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                                Intent intent = new Intent(act, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users"))
                            {
                                Toast.makeText(act, "User Logged In Successfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();

                                Intent intent = new Intent(act, HomeActivity.class);
                                Prevalent.CurrentOnlineUser = userData;
                                startActivity(intent);
                            }
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