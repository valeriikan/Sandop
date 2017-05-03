package fi.oulu.mobisocial.sandop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by Majid on 4/29/2017.
 */

public class ShowFavouriteActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle saveedInstanceState)
    {
        super.onCreate(saveedInstanceState);
        setContentView(R.layout.activity_favourite);
        setTitle("Favourite");

        ListView searchList = (ListView) findViewById(R.id.lvSearch);
        loadMyFavouriteItems(searchList);
    }

    private void loadMyFavouriteItems(final ListView listView)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userID = auth.getCurrentUser().getUid().toString();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("favourite");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Product> list = new ArrayList<Product>();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    list.add(ds.getValue(Product.class));
                }
                ProductAdapter adapter = new ProductAdapter(list, ShowFavouriteActivity.this);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
