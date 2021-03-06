package fi.oulu.mobisocial.sandop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fi.oulu.mobisocial.sandop.helpers.ProductAdapter;
import fi.oulu.mobisocial.sandop.helpers.Product;


public class SellFragment extends Fragment{

    private DatabaseReference sandOppDB;

    public ListView productListView;

    private ArrayList<String> link = new ArrayList<>();

    public SellFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sell, container, false);

        productListView = (ListView) v.findViewById(R.id.lvSellProducts);
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String ref = "sell/" + link.get(i);
                Intent intent = new Intent(getContext(), ProductActivity.class);
                intent.putExtra("PRODUCT", ref);
                startActivity(intent);
            }
        });

        sandOppDB = FirebaseDatabase.getInstance().getReference().child("products").child("sell");

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        sandOppDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadItemsToListView(dataSnapshot, productListView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        ProductAdapter adapter = new ProductAdapter(list, getContext());
        listView.setAdapter(adapter);
    }
}