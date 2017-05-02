package fi.oulu.mobisocial.sandop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import fi.oulu.mobisocial.sandop.helpers.CircleTransform;

public class ProductActivity extends AppCompatActivity {

    RelativeLayout productContent;
    ProgressBar productLoading;
    ImageView imgProduct;
    TextView tvDetailName, tvDetailDepartmentType, tvDetailCity, tvDetailSince, tvDetailOwner, tvDetailPrice, tvDetailDescription;

    String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        productContent = (RelativeLayout) findViewById(R.id.productContent);
        productLoading = (ProgressBar) findViewById(R.id.productLoading);
        imgProduct = (ImageView) findViewById(R.id.imgProduct);
        tvDetailName = (TextView) findViewById(R.id.tvDetailName);
        tvDetailDepartmentType = (TextView) findViewById(R.id.tvDetailDepartmentType);
        tvDetailCity = (TextView) findViewById(R.id.tvDetailCity);
        tvDetailSince = (TextView) findViewById(R.id.tvDetailSince);
        tvDetailOwner = (TextView) findViewById(R.id.tvDetailOwner);
        tvDetailPrice = (TextView) findViewById(R.id.tvDetailPrice);
        tvDetailDescription = (TextView) findViewById(R.id.tvDetailDescription);

        //Firebase authentication object declaration
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        productId = getIntent().getStringExtra("PRODUCT");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("products").child(productId);

        //content loading callback
        final Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                productLoading.setVisibility(View.GONE);
                productContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
            }
        };

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String department = dataSnapshot.child("department").getValue(String.class);
                String type = dataSnapshot.child("type").getValue(String.class);
                String city = dataSnapshot.child("city").getValue(String.class);
                String owner = dataSnapshot.child("owner").getValue(String.class);
                String price = dataSnapshot.child("price").getValue(String.class);
                String description = dataSnapshot.child("description").getValue(String.class);
                String imageUrl = dataSnapshot.child("image").getValue(String.class);

                tvDetailName.setText(name);
                tvDetailDepartmentType.setText(department + " - " + type);
                tvDetailCity.setText(city);
                tvDetailOwner.setText(owner);
                tvDetailPrice.setText(price);
                tvDetailDescription.setText(description);
                Picasso.with(ProductActivity.this).load(imageUrl).fit().transform(new CircleTransform()).into(imgProduct, callback);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
