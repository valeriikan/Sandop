package fi.oulu.mobisocial.sandop;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import fi.oulu.mobisocial.sandop.helpers.CustomAdapter;
import fi.oulu.mobisocial.sandop.helpers.Product;

import static fi.oulu.mobisocial.sandop.R.id.spDepartment;


public class SearchFragment extends Fragment {

    //two spinners for departments and categories of different items
    Spinner spDepartments;
    Spinner spCategory;
    Spinner spCity;

    //two radio buttons for buy or sell search
    RadioButton rbBuy;
    RadioButton rbSell;

    //a reference to firebase DB
    DatabaseReference dbRef;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);

        //declaration of view components inside search layout
        final ListView lvSearch = (ListView) v.findViewById(R.id.lvSearchedItems);
        spDepartments = (Spinner) v.findViewById(spDepartment);
        spCategory = (Spinner) v.findViewById(R.id.spCategory);
        spCity = (Spinner) v.findViewById(R.id.spCity);
        rbBuy = (RadioButton) v.findViewById(R.id.rbForBuy);
        rbSell = (RadioButton) v.findViewById(R.id.rbForSell);
        Button btnSearch = (Button) v.findViewById(R.id.btnSearch);

        DatabaseReference subRef = dbRef.child("items");

        //new event listener to load items to department spinner
        subRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> itemList = new ArrayList<String>();
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    itemList.add(ds.getKey());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, itemList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spDepartments.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //new event listener on selecting a department by user and loading corresponding category to category spinner
        spDepartments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String selectedDepartment = parent.getItemAtPosition(position).toString();
                final ArrayList<String> categoryList = new ArrayList<String>();

                DatabaseReference childRef = dbRef.child("items").child(selectedDepartment);

                childRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        categoryList.clear();

                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            categoryList.add(ds.getValue(String.class));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,categoryList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spCategory.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //new listener for checking buy radio button that loads all cities in firebase buy subtree to city spinner
        rbBuy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rbBuy.isChecked())
                {
                    rbSell.setChecked(false);
                }
                final ArrayList<String> cityList = new ArrayList<String>();
                String searchType;
                if (rbBuy.isChecked()) searchType = "buy";
                else searchType = "sell";

                DatabaseReference childRef = dbRef.child("products").child(searchType);

                childRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cityList.clear();

                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            Product product = ds.getValue(Product.class);
                            if (!cityList.contains(product.getCity())) cityList.add(product.getCity());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cityList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spCity.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        //
        rbSell.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (rbSell.isChecked())
                {
                    rbBuy.setChecked(false);
                }
                final ArrayList<String> cityList = new ArrayList<String>();
                String searchType;
                if (rbBuy.isChecked()) searchType = "buy";
                else searchType = "sell";

                DatabaseReference childRef = dbRef.child("products").child(searchType);

                childRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        cityList.clear();

                        for(DataSnapshot ds : dataSnapshot.getChildren())
                        {
                            Product product = ds.getValue(Product.class);
                            if (!cityList.contains(product.getCity())) cityList.add(product.getCity());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, cityList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spCity.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


        //what happens when user taps on search button
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String department = spDepartments.getSelectedItem().toString();
                final String category = spCategory.getSelectedItem().toString();
                String searchType;
                if (rbBuy.isChecked()) searchType = "buy";
                else searchType = "sell";
                final String city = spCity.getSelectedItem().toString();

                final DatabaseReference searchRef = dbRef.child("products").child(searchType);
                final ArrayList<Product> searchList = new ArrayList<Product>();

                searchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Product product = ds.getValue(Product.class);

                            if (department.equals(product.getDepartment()) && category.equals(product.getType())
                                    && city.equals(product.getCity()))
                            {
                                Log.i("Product: " , product.getName());
                                searchList.add(product);
                            }
                        }

                        CustomAdapter adapter = new CustomAdapter(searchList,getContext());
                        lvSearch.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

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
}