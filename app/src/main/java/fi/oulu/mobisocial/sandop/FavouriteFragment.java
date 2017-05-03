package fi.oulu.mobisocial.sandop;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FavouriteFragment extends Fragment {

    public FavouriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favourite, container, false);

        ListView searchList = (ListView) v.findViewById(R.id.lvSearch1);
        loadMyFavouriteItems(searchList);

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

    private void loadMyFavouriteItems(final ListView listView)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userID = auth.getCurrentUser().getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("favourite");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Product> list = new ArrayList<Product>();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    list.add(ds.getValue(Product.class));
                }
                ProductAdapter adapter = new ProductAdapter(list, getContext());
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Product> list = new ArrayList<Product>();
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    list.add(ds.getValue(Product.class));
                }
                ProductAdapter adapter = new ProductAdapter(list, getActivity());
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

}