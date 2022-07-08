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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ecommerceapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity {

    Activity act;
    Context context;

    private String categoryName, Description, Price, Name, saveCurrentDate, saveCurrentTime;
    private ImageView select_product_image;
    private Button add_new_product;
    private EditText product_name, product_description, product_price;

    private static final int GalleryPic = 1;
    private Uri ImageUri;
    private String ProductRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;

    private DatabaseReference databaseReference;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        act = this;
        context = this;

        pd = new ProgressDialog(this);

        select_product_image = findViewById(R.id.select_product_image);
        add_new_product = findViewById(R.id.add_new_product);
        product_name = findViewById(R.id.product_name);
        product_description = findViewById(R.id.product_description);
        product_price = findViewById(R.id.product_price);

        categoryName = getIntent().getExtras().get("category").toString();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");


        select_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        add_new_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });
    }



    private void OpenGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GalleryPic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPic && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
            select_product_image.setImageURI(ImageUri);
        }
    }

    private void ValidateProductData() {
        Description = product_description.getText().toString();
        Price = product_price.getText().toString();
        Name = product_name.getText().toString();

        if (ImageUri == null){
            Toast.makeText(act, "Image is REQUIRED", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(act, "Description is REQUIRED", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Price))
        {
            Toast.makeText(act, "Price is REQUIRED", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Name))
        {
            Toast.makeText(act, "Product Name is REQUIRED", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInformation();
        }

    }

    private void StoreProductInformation()
    {

        pd.setTitle("Adding New Product..Please Wait");
        pd.setMessage("You are being Logged In. Please Wait..");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        ProductRandomKey = saveCurrentDate + saveCurrentTime;

        //storing image into firebase storage
        StorageReference filePth = ProductImagesRef.child(ImageUri.getLastPathSegment() + ProductRandomKey + ".jpg");

        final UploadTask uploadTask = filePth.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(act, "Error: " + message, Toast.LENGTH_SHORT).show();

                pd.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(act, "Product Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePth.getDownloadUrl().toString();
                        return filePth.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(act, "Got Product Image URL", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });

    }

    private void SaveProductInfoToDatabase() {

        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("id", ProductRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("price", Price);
        productMap.put("pname", Name);

        databaseReference.child(ProductRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Intent intent = new Intent(act, AdminCategoryActivity.class);
                    startActivity(intent);

                    pd.dismiss();
                    Toast.makeText(act, "Product Added Successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    pd.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(act, "Error: " +message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}