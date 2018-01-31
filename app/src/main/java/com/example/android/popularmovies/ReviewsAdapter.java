package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

/**
 * Created by РЕПКА on 25.11.2017.
 */

public class ReviewsAdapter extends ArrayAdapter<String[]> {
    public ReviewsAdapter(Context context, List<String[]> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.review_view, parent, false);
        }

        String[] reviewBlock = getItem(position);

        if (reviewBlock != null && reviewBlock.length == 2) {
            TextView authorTv = (TextView) convertView.findViewById(R.id.author_tv);
            authorTv.setText(reviewBlock[0]);

            ExpandableTextView reviewContentTv = (ExpandableTextView ) convertView.findViewById(R.id.review_content_tv);
            reviewContentTv.setText(reviewBlock[1]);
        }

        return convertView;
    }
}
