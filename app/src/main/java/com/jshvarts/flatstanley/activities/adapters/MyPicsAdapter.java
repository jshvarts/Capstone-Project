package com.jshvarts.flatstanley.activities.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jshvarts.flatstanley.R;
import com.jshvarts.flatstanley.model.FlatStanley;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MyPicsAdapter extends BaseAdapter {
    private static final String TAG = "MyPicsAdapter";

    private Context context;
    private List<FlatStanley> flatStanleys;

    public MyPicsAdapter(Context context, List<FlatStanley> flatStanleys) {
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
        return Long.valueOf(flatStanleys.get(position).getId()).longValue();
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

        //byte[] decodedBytes = Base64.decode(flatStanleys.get(position).getImageData(),Base64.DEFAULT);
        //Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        Uri picUri = Uri.parse(flatStanleys.get(position).getImageData());
        InputStream inputStream = loadBitmap(picUri);

        if (inputStream != null) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            holder.imageView.setImageBitmap(bitmap);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(bitmap.getWidth()/2, bitmap.getHeight()/2);
            holder.imageView.setLayoutParams(layoutParams);

            String caption = flatStanleys.get(position).getCaption();
            if (TextUtils.isEmpty(caption)) {
                holder.caption.setVisibility(View.GONE);
            } else {
                holder.caption.setText(caption);
            }

            holder.timestamp.setText("Created on: " + flatStanleys.get(position).getTimestamp());
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView imageView;
        private TextView caption;
        private TextView timestamp;
    }

    private InputStream loadBitmap(Uri photoUri) {
        InputStream is;
        try {
            is = context.getContentResolver().openInputStream(photoUri);
        } catch (IOException e) {
            Log.e(TAG, "Unable to open photo for uri " + photoUri);
            return null;
        }
        return is;
    }
}
