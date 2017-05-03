package fi.oulu.mobisocial.sandop;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;

import fi.oulu.mobisocial.sandop.helpers.CircleTransform;
import fi.oulu.mobisocial.sandop.helpers.Comment;
import fi.oulu.mobisocial.sandop.helpers.CommentAdapter;
import fi.oulu.mobisocial.sandop.helpers.ProductAdapter;
import fi.oulu.mobisocial.sandop.helpers.Product;
import fi.oulu.mobisocial.sandop.helpers.RoundedCornersTransform;

public class ProductActivity extends AppCompatActivity {

    RelativeLayout productContent;
    ProgressBar productLoading;
    ImageView imgProduct;
    TextView tvDetailName, tvDetailCity, tvDetailSince, tvDetailOwner, tvDetailPrice, tvDetailDescription;
    ListView lvComments;
    EditText etComment;
    Button btnComment;

    String username;
    DatabaseReference comRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        productContent = (RelativeLayout) findViewById(R.id.productContent);
        productLoading = (ProgressBar) findViewById(R.id.productLoading);
        imgProduct = (ImageView) findViewById(R.id.imgProduct);
        tvDetailName = (TextView) findViewById(R.id.tvDetailName);
        tvDetailCity = (TextView) findViewById(R.id.tvDetailCity);
        tvDetailSince = (TextView) findViewById(R.id.tvDetailSince);
        tvDetailOwner = (TextView) findViewById(R.id.tvDetailOwner);
        tvDetailPrice = (TextView) findViewById(R.id.tvDetailPrice);
        tvDetailDescription = (TextView) findViewById(R.id.tvDetailDescription);
        lvComments = (ListView) findViewById(R.id.lvComments);
        etComment = (EditText) findViewById(R.id.etComment);
        btnComment = (Button) findViewById(R.id.btnComment);

        //Firebase authentication object declaration
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        String productId = getIntent().getStringExtra("PRODUCT");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("name");
        DatabaseReference prodRef = FirebaseDatabase.getInstance().getReference().child("products").child(productId);
        comRef = prodRef.child("comments");

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

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        prodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String city = dataSnapshot.child("city").getValue(String.class);
                String owner = dataSnapshot.child("owner").getValue(String.class);
                String price = dataSnapshot.child("price").getValue(String.class);
                String description = dataSnapshot.child("description").getValue(String.class);
                String imageUrl = dataSnapshot.child("image").getValue(String.class);

                tvDetailName.setText(name);
                tvDetailCity.setText(city);
                tvDetailOwner.setText(owner);
                tvDetailPrice.setText(price);
                tvDetailDescription.setText(description);
                Picasso.with(ProductActivity.this).load(imageUrl).fit().centerCrop().
                        transform(new RoundedCornersTransform(50,10)).into(imgProduct, callback);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        comRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadItemsToListView(dataSnapshot, lvComments);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Add comment button
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                String content = etComment.getText().toString();
                if (!content.equals("")) {
                    if (isOnline()) {
                        Comment newComment = new Comment(username, content);
                        comRef.push().setValue(newComment);
                        etComment.setText("");
                    } else {
                        Toast.makeText(ProductActivity.this, R.string.noInternet, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(ProductActivity.this, R.string.emptyComment, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void loadItemsToListView(DataSnapshot dataSnapshot, ListView listView)
    {
        ArrayList<Comment> list = new ArrayList<Comment>();

        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            Comment comment = ds.getValue(Comment.class);
            list.add(comment);
        }
        CommentAdapter adapter = new CommentAdapter(list,this);
        listView.setAdapter(adapter);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // hide keyboard on comment added
    private void hideKeyboard() {
        // Check if no view has focus
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}