package fi.oulu.mobisocial.sandop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fi.oulu.mobisocial.sandop.helpers.ProductAdapter;
import fi.oulu.mobisocial.sandop.helpers.Product;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static java.security.AccessController.getContext;

/**
 * Created by Majid on 5/2/2017.
 */

public class UserOffersActivity extends AppCompatActivity{

    private ArrayList<String> link = new ArrayList<>();

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.activity_user_offers);
        setTitle("My Offers");

        ListView lvUserOffers = (ListView) findViewById(R.id.lvUserOffers);
        lvUserOffers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ref = "sell/" + link.get(position);
                Intent intent = new Intent(UserOffersActivity.this, ProductActivity.class);
                intent.putExtra("PRODUCT", ref);
                startActivity(intent);
            }
        });
        loadUserOffers();
    }

    private void loadUserOffers()
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userID = auth.getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("name").getValue(String.class);
                fetchUserOffers(userName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void fetchUserOffers(final String name)
    {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("products");
        final ArrayList<Product> list = new ArrayList<>();
        final ListView lvUserOffers = (ListView) findViewById(R.id.lvUserOffers);

        dbRef.child("sell").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Product product = ds.getValue(Product.class);
                    if (product.getOwner().equals(name))
                    {
                        list.add(product);
                        link.add(ds.getKey());
                    }
                }
                ProductAdapter adapter = new ProductAdapter(list, UserOffersActivity.this);
                lvUserOffers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("buy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Product product = ds.getValue(Product.class);
                    if (product.getOwner().equals(name))
                    {
                        list.add(product);
                        link.add(ds.getKey());
                    }
                }
                ProductAdapter adapter = new ProductAdapter(list, UserOffersActivity.this);
                lvUserOffers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
