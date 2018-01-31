package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by РЕПКА on 25.11.2017.
 */

public class TrailersAdapter extends ArrayAdapter<String[]> {
    public TrailersAdapter(Context context, List<String[]> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.trailer_view, parent,false);
        }

        String trailerName = getItem(position)[1];
        TextView trailerNameTv = (TextView) convertView.findViewById(R.id.trailer_tv);
        trailerNameTv.setText(trailerName);

        return convertView;
    }
}
