package com.mentionsandroid.mention;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mentionsandroid.R;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by ningsuhen on 4/24/16.
 */
public class MentionAdaptor extends ArrayAdapter<MentionSuggestible> implements AdapterView.OnItemClickListener {


    private final int layout;
    private final int resourceId;
    private final int imgResourceId;
    public MentionAdaptorDelegate delegate;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (delegate != null) {
            MentionSuggestible suggestible = getItem(position);
            delegate.onSuggestionSelected(suggestible);
        }
    }


    private static class ViewHolder {
        private ImageView imageView;
        private TextView itemView;
    }

    public MentionAdaptor(Context context, int resource, int textViewResourceId, int imageViewResourceId, List<MentionSuggestible> items) {
        super(context, resource, textViewResourceId, items);
        this.layout = resource;
        this.resourceId = textViewResourceId;
        this.imgResourceId = imageViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(this.layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) convertView.findViewById(resourceId);
            viewHolder.imageView = (ImageView) convertView.findViewById(imgResourceId);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MentionSuggestible item = getItem(position);
        if (item != null) {
            // My layout has only one TextView
            // do whatever you want with your string and long
            viewHolder.itemView.setText(item.getText());
            Picasso.with(getContext()).load(item.getImageUrl()).into(viewHolder.imageView);
//            viewHolder.imageView.setImageBitmap(getImageBitmap(item.getImageUrl()));
        }
        return convertView;
    }

    public interface MentionAdaptorDelegate {
        void onSuggestionSelected(MentionSuggestible suggestible);
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("SomeTag", "Error getting bitmap", e);
        }
        return bm;
    }
}
