package fi.oulu.mobisocial.sandop;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fi.oulu.mobisocial.sandop.helpers.Product;
import fi.oulu.mobisocial.sandop.helpers.ProductAdapter;

public class MyProductFragment extends Fragment {

    private ArrayList<String> link = new ArrayList<>();
    ListView lvUserOffers;

    public MyProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_product, container, false);

        lvUserOffers = (ListView) v.findViewById(R.id.lvUserOffers1);
        lvUserOffers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ref = link.get(position);
                Intent intent = new Intent(getContext(), ProductActivity.class);
                intent.putExtra("PRODUCT", ref);
                startActivity(intent);
            }
        });
        loadUserOffers();

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

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

        dbRef.child("sell").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Product product = ds.getValue(Product.class);
                    if (product.getOwner().equals(name))
                    {
                        list.add(product);
                        link.add("sell/" + ds.getKey());
                    }
                }
                ProductAdapter adapter = new ProductAdapter(list, getContext());
                lvUserOffers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dbRef.child("buy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Product product = ds.getValue(Product.class);
                    if (product.getOwner().equals(name))
                    {
                        list.add(product);
                        link.add("buy/" + ds.getKey());
                    }
                }
                ProductAdapter adapter = new ProductAdapter(list, getContext());
                lvUserOffers.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}