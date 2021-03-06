package fi.oulu.mobisocial.sandop.helpers;

/**
 * Created by Majid on 4/24/2017.
 */

import android.content.Context;
import android.graphics.drawable.Icon;
import android.view.View;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fi.oulu.mobisocial.sandop.R;

import static android.R.id.list;

public class ProductAdapter extends ArrayAdapter<Product> implements View.OnClickListener{

    private ArrayList<Product> dataSet;
    private Context mContext;

    // View lookup cache
    private static class ViewHolder {
        int position;
        TextView tvName;
        TextView tvCity;
        TextView tvOwner;
        TextView tvPrice;
        TextView tvDate;
        ImageView ivImage;
        ImageButton ibtnFav;

        public ViewHolder()
        {
            //position = 0;
        }

        public ViewHolder(View v)
        {
            this.tvName = (TextView) v.findViewById(R.id.tvProductName);
            this.tvCity = (TextView) v.findViewById(R.id.tvProductCity);
            this.tvOwner = (TextView) v.findViewById(R.id.tvProductOwner);
            this.tvPrice = (TextView) v.findViewById(R.id.tvProductPrice);
            this.tvDate = (TextView) v.findViewById(R.id.tvProductDate);
            this.ivImage = (ImageView) v.findViewById(R.id.ivProductImage);
            this.ibtnFav = (ImageButton) v.findViewById(R.id.ibtnFavourite);

        }
    }

    public ProductAdapter(ArrayList<Product> data, Context context) {
        super(context, R.layout.row_product, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        // Get the data item for this position
        Product dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        //final ViewHolder viewHolder; // view lookup cache stored in tag
        final ViewHolder viewHolder;

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_product, parent, false);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvProductName);
            viewHolder.tvCity = (TextView) convertView.findViewById(R.id.tvProductCity);
            viewHolder.tvOwner = (TextView) convertView.findViewById(R.id.tvProductOwner);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tvProductPrice);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvProductDate);
            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivProductImage);
            viewHolder.ibtnFav = (ImageButton) convertView.findViewById(R.id.ibtnFavourite);
            viewHolder.position = position;
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        //setFavouriteStatus(viewHolder, dataSet.get(position));
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;


        viewHolder.tvName.setText(dataModel.getName());
        viewHolder.tvCity.setText("City: " + dataModel.getCity());
        viewHolder.tvOwner.setText("Owner: " + dataModel.getOwner());
        viewHolder.tvPrice.setText("Price : " + dataModel.getPrice() + " €");
        viewHolder.tvDate.setText("Available: " + dataModel.getDate());
        Picasso.with(getContext()).load(dataModel.getImage()).resize(100,100).into(viewHolder.ivImage);

        // Return the completed view to render on screen
        setFavouriteStatus(viewHolder, dataModel);
        viewHolder.ibtnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //int position = (Integer) v.getTag();
                Log.i("onClick", String.valueOf(viewHolder.position));
                Product product = (Product) getItem(viewHolder.position);
                addToFavourite(product, viewHolder);
            }
        });
        return result;
    }

    private void addToFavourite(final Product product, final ViewHolder holder)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userID = auth.getCurrentUser().getUid().toString();

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("favourite");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isExisted = false;
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String key = ds.getKey();
                    Product inFavProduct = ds.getValue(Product.class);
                    if (inFavProduct.isEqual(product)) {
                        isExisted = true;
                        dbRef.child(key).removeValue();
                        holder.ibtnFav.setImageResource(R.drawable.empty_star);
                        break;
                    }
                }
                if (!isExisted)
                {
                    dbRef.push().setValue(product);
                    holder.ibtnFav.setImageResource(R.drawable.yellow_star);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setFavouriteStatus(final ViewHolder holder , final Product product)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userID = auth.getCurrentUser().getUid().toString();
        holder.ibtnFav.setBackgroundColor(10);

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("favourite");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isExisted = false;
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    Product inFavProduct = ds.getValue(Product.class);
                    if (inFavProduct.isEqual(product)) {
                        isExisted = true;
                        holder.ibtnFav.setImageResource(R.drawable.yellow_star);
                        break;
                    }
                }
                if (!isExisted) holder.ibtnFav.setImageResource(R.drawable.empty_star);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
