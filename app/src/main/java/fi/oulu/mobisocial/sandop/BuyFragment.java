package fi.oulu.mobisocial.sandop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fi.oulu.mobisocial.sandop.helpers.CustomAdapter;
import fi.oulu.mobisocial.sandop.helpers.Product;


public class BuyFragment extends Fragment {

    private DatabaseReference sandOppDB;

    public ListView productListView;

    private ArrayList<String> link = new ArrayList<>();

    public BuyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_buy, container, false);

        productListView = (ListView) v.findViewById(R.id.lvBuyProducts);
        productListView.setScrollContainer(true);
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String ref = "buy/" + link.get(i);
                Intent intent = new Intent(getContext(), ProductActivity.class);
                intent.putExtra("PRODUCT", ref);
                startActivity(intent);
            }
        });

        sandOppDB = FirebaseDatabase.getInstance().getReference().child("products").child("buy");

        sandOppDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadItemsToListView(dataSnapshot, productListView);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

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

    private void loadItemsToListView(DataSnapshot dataSnapshot, ListView listView)
    {
        ArrayList<Product> list = new ArrayList<Product>();

        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            Product product = ds.getValue(Product.class);
            link.add(ds.getKey());
            list.add(product);
        }
        CustomAdapter adapter = new CustomAdapter(list,getContext());
        listView.setAdapter(adapter);
    }
}