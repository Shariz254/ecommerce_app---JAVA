package com.example.ecommerceapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerceapp.Prevalent.Prevalent;
import com.example.ecommerceapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    Activity act;
    Context context;

    private CircleImageView settings_profile_image;
    private EditText settings_phone_number, settings_full_name, settings_address;
    private TextView close_settings, update_settings, profile_image_change;

    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private String checker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        act = this;
        context = this;

        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Pics");

        settings_profile_image = findViewById(R.id.settings_profile_image);
        settings_phone_number = findViewById(R.id.settings_phone_number);
        settings_full_name = findViewById(R.id.settings_full_name);
        settings_address = findViewById(R.id.settings_address);
        close_settings = findViewById(R.id.close_settings);
        update_settings = findViewById(R.id.update_settings);
        profile_image_change = findViewById(R.id.profile_image_change);

        userInfoDisplay(settings_profile_image,settings_phone_number,settings_full_name,settings_address);

        close_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        update_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (checker.equals("clicked"))
                if (checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo(); //when user only want to update one thing, e.g username
                }
            }
        });

        settings_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";

//                CropImage.activity(imageUri)
//                        .start(this);
            }
        });

    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();

        userMap.put("name", settings_full_name.getText().toString());
        userMap.put("phone", settings_phone_number.getText().toString());
        userMap.put("address", settings_address.getText().toString());

        ref.child(Prevalent.CurrentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(act, HomeActivity.class));
        Toast.makeText(act, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null )
//        {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            imageUri = result.getUri();
//
//            settings_profile_image.setImageURI(imageUri);
//        }
//        else
//        {
//            Toast.makeText(act, "Error, Try Again", Toast.LENGTH_SHORT).show();
//
//            startActivity(new Intent(act, SettingsActivity.class));
//            finish();
//        }

   // }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(settings_full_name.getText().toString()))
        {
           Toast.makeText(act, "Name Required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(settings_address.getText().toString()))
        {
            Toast.makeText(act, "Address Required", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(settings_phone_number.getText().toString()))
        {
            Toast.makeText(act, "Phone Required", Toast.LENGTH_SHORT).show();
        }
        else if (checker.equals("clicked"))
        {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait as You Profile is being updated");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageReference.child(Prevalent.CurrentOnlineUser.getPhone() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
                        Uri dowloadUrl = task.getResult();
                        myUrl = dowloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();

                        userMap.put("name", settings_full_name.getText().toString());
                        userMap.put("phone", settings_phone_number.getText().toString());
                        userMap.put("address", settings_address.getText().toString());
                        userMap.put("image", myUrl);

                        ref.child(Prevalent.CurrentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(act, HomeActivity.class));
                        Toast.makeText(act, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        Toast.makeText(act, "Error in updating profile", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        else
        {
            Toast.makeText(act, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void userInfoDisplay(CircleImageView settings_profile_image, EditText settings_phone_number, EditText settings_full_name, EditText settings_address) {

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.CurrentOnlineUser.getPhone());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    if (snapshot.child("image").exists())
                    {
                        String image = snapshot.child("image").getValue().toString();
                        String name = snapshot.child("name").getValue().toString();
                        String phone = snapshot.child("phone").getValue().toString();
                        String address = snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(settings_profile_image);
                        settings_full_name.setText(name);
                        settings_phone_number.setText(phone);
                        settings_address.setText(address);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}