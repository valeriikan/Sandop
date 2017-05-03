package fi.oulu.mobisocial.sandop.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fi.oulu.mobisocial.sandop.R;

public class CommentAdapter extends ArrayAdapter<Comment>{

    private ArrayList<Comment> dataSet;
    private Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView tvCommentName;
        TextView tvCommentContent;

        public ViewHolder() {}

        public ViewHolder(View v) {
            this.tvCommentName = (TextView) v.findViewById(R.id.tvCommentName);
            this.tvCommentContent = (TextView) v.findViewById(R.id.tvCommentContent);
        }
    }

    public CommentAdapter(ArrayList<Comment> data, Context context) {
        super(context, R.layout.row_comment, data);
        this.dataSet = data;
        this.mContext=context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Comment dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        //final ViewHolder viewHolder; // view lookup cache stored in tag
        final ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_comment, parent, false);
            viewHolder.tvCommentName = (TextView) convertView.findViewById(R.id.tvCommentName);
            viewHolder.tvCommentContent = (TextView) convertView.findViewById(R.id.tvCommentContent);

            result = convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        String commentName = dataModel.getName() + ":";
        viewHolder.tvCommentName.setText(commentName);
        viewHolder.tvCommentContent.setText(dataModel.getContent());

        return convertView;
    }
}