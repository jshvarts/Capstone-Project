package com.jshvarts.flatstanley.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jshvarts.flatstanley.R;
import com.jshvarts.flatstanley.model.FlatStanley;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FlatStanleyAdapter extends BaseAdapter {

    private Context context;
    private List<FlatStanley> flatStanleys;

    public FlatStanleyAdapter(Context context, List<FlatStanley> flatStanleys) {
        this.context = context;
        this.flatStanleys = flatStanleys;
    }

    @Override
    public int getCount() {
        return flatStanleys.size();
    }

    @Override
    public Object getItem(int position) {
        return flatStanleys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null || convertView.getId() == -1) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.browse_flat_stanley_list_item, null);

            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.flatStanleyImage);
            holder.caption = (TextView) convertView.findViewById(R.id.caption);
            holder.timestamp = (TextView) convertView.findViewById(R.id.timestamp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(context).load(R.drawable.flat_stanley_logo).into(holder.imageView);
        holder.caption.setText(flatStanleys.get(position).getCaption());
        holder.timestamp.setText(flatStanleys.get(position).getTimestamp());

        return convertView;
    }

    private class ViewHolder {
        private ImageView imageView;
        private TextView caption;
        private TextView timestamp;
    }
}
