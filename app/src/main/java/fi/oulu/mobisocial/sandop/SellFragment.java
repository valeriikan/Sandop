package fi.oulu.mobisocial.sandop;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import fi.oulu.mobisocial.sandop.helpers.Product;


public class SellFragment extends Fragment{

    private DatabaseReference sandOppDB;

    public ListView productListView;

    public SellFragment() {
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
        View v = inflater.inflate(R.layout.fragment_sell, container, false);
        productListView = (ListView) v.findViewById(R.id.lvSellProducts);

        sandOppDB = FirebaseDatabase.getInstance().getReference().child("products").child("sell");

        FirebaseListAdapter<Product> listAdapter = new FirebaseListAdapter<Product>(getActivity(),Product.class,
                R.layout.row_model,sandOppDB) {
            @Override
            protected void populateView(View v, Product model, int position) {

                TextView tvName = (TextView) v.findViewById(R.id.tvProductName);
                TextView tvCity = (TextView) v.findViewById(R.id.tvProductCity);
                TextView tvPrice = (TextView) v.findViewById(R.id.tvProductPrice);
                TextView tvOwner = (TextView) v.findViewById(R.id.tvProductOwner);
                TextView tvDescription = (TextView) v.findViewById(R.id.tvProductDescription);
                ImageView ivImage = (ImageView) v.findViewById(R.id.ivProductImage);

                tvName.setText(model.getName());
                tvCity.setText("City: " + model.getCity());
                tvPrice.setText("Price: " + model.getPrice() + " euro(s)");
                tvOwner.setText("Seller: " + model.getOwner());
                tvDescription.setText("Description: " + model.getDescription());
                Picasso.with(getContext())
                        .load(model.getImage())
                        .resize(100,100).into(ivImage);
            }
        };
        productListView.setAdapter(listAdapter);
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
}