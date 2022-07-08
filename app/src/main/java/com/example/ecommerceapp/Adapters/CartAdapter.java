package com.example.ecommerceapp.Adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.Interface.ItemClickListener;
import com.example.ecommerceapp.R;

public class CartAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView cart_product_name, cart_product_quantity, cart_product_price;
    private ItemClickListener itemClickListener;

    public CartAdapter(@NonNull View itemView) {
        super(itemView);

        cart_product_name = itemView.findViewById(R.id.cart_product_name);
        cart_product_quantity = itemView.findViewById(R.id.cart_product_quantity);
        cart_product_price = itemView.findViewById(R.id.cart_product_price);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
