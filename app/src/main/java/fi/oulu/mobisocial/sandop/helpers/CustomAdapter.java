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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fi.oulu.mobisocial.sandop.R;

public class CustomAdapter extends ArrayAdapter<Product> implements View.OnClickListener{

    private ArrayList<Product> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView tvName;
        TextView tvCity;
        TextView tvOwner;
        TextView tvPrice;
        TextView tvDescription;
        ImageView ivImage;
    }



    public CustomAdapter(ArrayList<Product> data, Context context) {
        super(context, R.layout.row_model, data);
        this.dataSet = data;
        this.mContext=context;

    }


    @Override
    public void onClick(View v) {

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Product dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

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

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        //lastPosition = position;


        viewHolder.tvName.setText(dataModel.getName());
        viewHolder.tvCity.setText("City: " + dataModel.getCity());
        viewHolder.tvOwner.setText("Owner: " + dataModel.getOwner());
        viewHolder.tvPrice.setText("Price : " + dataModel.getPrice() + " euro(s)");
        viewHolder.tvDescription.setText("Description: " + dataModel.getDescription());
        Picasso.with(getContext()).load(dataModel.getImage()).resize(100,100).into(viewHolder.ivImage);
        //viewHolder.ivImage.setOnClickListener(this);
        //viewHolder.ivImage.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }


}
