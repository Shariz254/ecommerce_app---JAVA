package com.example.ecommerceapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ecommerceapp.Adapters.AllProductsAdapters;
import com.example.ecommerceapp.Models.Products;
import com.example.ecommerceapp.Prevalent.Prevalent;
import com.example.ecommerceapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Activity act;
    Context context;

    private DatabaseReference databaseReference;
    private RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    private String type = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        act = this;
        context = this;

        Intent intent =  getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null)
        {
            type = getIntent().getExtras().get("Admin").toString();
        }

        Paper.init(this);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!type.equals("Admin"))
                {
                    Intent intent = new Intent(act, CartActivity.class);
                    startActivity(intent);
                }

            }


        });


        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);
        TextView userNameTxt = headerView.findViewById(R.id.userName);
        CircleImageView ProfileImage = headerView.findViewById(R.id.profile_image);

        if (!type.equals("Admin"))
        {
            userNameTxt.setText(Prevalent.CurrentOnlineUser.getName());
            Picasso.get().load(Prevalent.CurrentOnlineUser.getImage()).placeholder(R.drawable.profile).into(ProfileImage);
        }


        recycler_menu = findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(databaseReference, Products.class).build();

        FirebaseRecyclerAdapter<Products, AllProductsAdapters> adapter = new FirebaseRecyclerAdapter<Products, AllProductsAdapters>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllProductsAdapters holder, int position, @NonNull Products model) {
                holder.product_Nm.setText(model.getPname());
                holder.product_Des.setText(model.getDescription());
                holder.product_Price.setText("Price: " + model.getPrice());
//                holder.product_Price.setText("Price: " + model.getPrice() + "$");

                Picasso.get().load(model.getImage()).into(holder.imageProduct);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (type.equals("Admin"))
                        {
                            Intent intent = new Intent(act, AdminMaintainProductsActivity.class);
                            intent.putExtra("pid", model.getId());
                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(act, ProductDetailsActivity.class);
                            intent.putExtra("pid", model.getId());
                            startActivity(intent);
                        }


                    }
                });

            }

            @NonNull
            @Override
            public AllProductsAdapters onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items, parent,false);
                AllProductsAdapters holder = new AllProductsAdapters(view);
                return holder;
            }
        };

        recycler_menu.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

//        if (id == R.id.action_settings)
//        {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_cart)
        {
            if (!type.equals("Admin"))
            {
                Intent intent = new Intent(act, CartActivity.class);
                startActivity(intent);
            }

        }
        else if (id == R.id.nav_search)
        {
            if (!type.equals("Admin"))
            {
                Intent intent = new Intent(act, SearchProductsActivity.class);
                startActivity(intent);
            }

        }
        else if (id == R.id.nav_categories)
        {

        }
        else if (id == R.id.nav_settings)
        {
            if (!type.equals("Admin"))
            {
                Intent intent = new Intent(act, SettingsActivity.class);
                startActivity(intent);
            }

        }
        else if (id == R.id.nav_logout)
        {
            if (!type.equals("Admin"))
            {
                Paper.book().destroy();

                Intent intent = new Intent(act, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}