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
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecommerceapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    Activity act;
    Context context;

    private Button register;
    private EditText register_user_name,register_phone_no, register_password;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        act = this;
        context = this;

        register_user_name = findViewById(R.id.register_user_name);
        register_phone_no = findViewById(R.id.register_phone_no);
        register_password = findViewById(R.id.register_password);
        pd = new ProgressDialog(this);

        register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {

        String name = register_user_name.getText().toString();
        String phone = register_phone_no.getText().toString();
        String password = register_password.getText().toString();

        if (TextUtils.isEmpty(name)){
            register_user_name.setError("Required");
        }
        else if (TextUtils.isEmpty(phone)){
            register_phone_no.setError("Required");
        }
        else if (TextUtils.isEmpty(password)){
            register_password.setError("Required");
        }

        else {
            pd.setTitle("Create Account");
            pd.setMessage("Account is being Set Up. Please Wait..");
            pd.setCanceledOnTouchOutside(false);
            pd.show();


            ValidatePhoneNumber(name, phone, password);
        }
    }

    private void ValidatePhoneNumber(String name, String phone, String password)
    {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("Users").child(phone).exists()))
                {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phone);
                    userDataMap.put("name", name);
                    userDataMap.put("password", password);

                    rootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(act, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                        pd.dismiss();

                                        Intent intent = new Intent(act, LoginActivity.class);
                                        startActivity(intent);

                                    } else {
                                        pd.dismiss();
                                        Toast.makeText(act, "Check Network and Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(act, "This " + phone + " already exists!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();

                    Intent intent = new Intent(act, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}