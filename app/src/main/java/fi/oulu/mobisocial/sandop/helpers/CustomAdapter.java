package fi.oulu.mobisocial.sandop.helpers;

/**
 * Created by Majid on 4/24/2017.
 */

import android.content.Context;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fi.oulu.mobisocial.sandop.R;

public class CustomAdapter extends ArrayAdapter<Product> implements View.OnClickListener{

    private ArrayList<Product> dataSet;
    private Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView tvName;
        TextView tvCity;
        TextView tvOwner;
        TextView tvPrice;
        TextView tvDescription;
        ImageView ivImage;
        ImageButton ibtnFav;

        public ViewHolder()
        {}

        public ViewHolder(View v)
        {
            this.tvName = (TextView) v.findViewById(R.id.tvProductName);
            this.tvCity = (TextView) v.findViewById(R.id.tvProductCity);
            this.tvOwner = (TextView) v.findViewById(R.id.tvProductOwner);
            this.tvPrice = (TextView) v.findViewById(R.id.tvProductPrice);
            this.tvDescription = (TextView) v.findViewById(R.id.tvProductDescription);
            this.ivImage = (ImageView) v.findViewById(R.id.ivProductImage);
            this.ibtnFav = (ImageButton) v.findViewById(R.id.ibtnFavourite);
        }
    }

    public CustomAdapter(ArrayList<Product> data, Context context) {
        super(context, R.layout.row_model, data);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Product dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        //final ViewHolder viewHolder; // view lookup cache stored in tag
        final ViewHolder viewHolder;

        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_model, parent, false);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvProductName);
            viewHolder.tvCity = (TextView) convertView.findViewById(R.id.tvProductCity);
            viewHolder.tvOwner = (TextView) convertView.findViewById(R.id.tvProductOwner);
            viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tvProductPrice);
            viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tvProductDescription);
            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivProductImage);
            viewHolder.ibtnFav = (ImageButton) convertView.findViewById(R.id.ibtnFavourite);
            viewHolder.ibtnFav.setBackgroundColor(10);
            viewHolder.ibtnFav.setTag(position);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;


        viewHolder.tvName.setText(dataModel.getName());
        viewHolder.tvCity.setText("City: " + dataModel.getCity());
        viewHolder.tvOwner.setText("Owner: " + dataModel.getOwner());
        viewHolder.tvPrice.setText("Price : " + dataModel.getPrice() + " euro(s)");
        viewHolder.tvDescription.setText("Description: " + dataModel.getDescription());
        Picasso.with(getContext()).load(dataModel.getImage()).resize(100,100).into(viewHolder.ivImage);

        viewHolder.ivImage.setOnClickListener(this);
        viewHolder.ivImage.setTag(position);
        // Return the completed view to render on screen

        viewHolder.ibtnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                Product product = (Product) getItem(position);
                addToFavourite(product);
                viewHolder.ibtnFav.setBackgroundColor(10);
            }
        });
        return convertView;
    }

    private void addToFavourite(Product product)
    {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userID = auth.getCurrentUser().getUid().toString();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("favourite");
        dbRef.push().setValue(product);
    }
}
