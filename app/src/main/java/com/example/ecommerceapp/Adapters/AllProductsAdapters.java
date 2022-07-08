package com.example.ecommerceapp.Adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.Interface.ItemClickListener;
import com.example.ecommerceapp.R;

public class AllProductsAdapters extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView product_Nm, product_Des, product_Price;
    public ImageView imageProduct;
    public ItemClickListener listener;

    public AllProductsAdapters(@NonNull View itemView) {
        super(itemView);

        imageProduct = (ImageView) itemView.findViewById(R.id.productImage);
        product_Nm = (TextView) itemView.findViewById(R.id.productName);
        product_Des = (TextView) itemView.findViewById(R.id.productDescription);
        product_Price = (TextView) itemView.findViewById(R.id.productPrice);
    }

    public void ItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(), false);
    }
}
