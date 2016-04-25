package com.mentionsandroid.mention;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ningsuhen on 4/24/16.
 */
public class MentionAdaptor extends ArrayAdapter<MentionSuggestible> implements AdapterView.OnItemClickListener{


    private final int layout;
    private final int resourceId;
    public MentionAdaptorDelegate delegate;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (delegate != null){
            MentionSuggestible suggestible = getItem(position);
            delegate.onSuggestionSelected(suggestible);
        }
    }


    private static class ViewHolder {
        private TextView itemView;
    }

    public MentionAdaptor(Context context, int resource, int textViewResourceId, List<MentionSuggestible> items) {
        super(context, resource, textViewResourceId, items);
        this.layout = resource;
        this.resourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(this.layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.itemView = (TextView) convertView.findViewById(this.resourceId);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MentionSuggestible item = getItem(position);
        if (item != null) {
            // My layout has only one TextView
            // do whatever you want with your string and long
            viewHolder.itemView.setText(item.getText());
        }
        return convertView;
    }

     public interface MentionAdaptorDelegate{
         void onSuggestionSelected(MentionSuggestible suggestible);
    }
}
