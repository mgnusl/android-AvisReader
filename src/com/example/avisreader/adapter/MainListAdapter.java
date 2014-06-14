package com.example.avisreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.avisreader.R;
import com.example.avisreader.data.NewsPaper;

import java.util.List;

public class MainListAdapter extends ArrayAdapter<NewsPaper> {

    private LayoutInflater inflater;
    private int resource;
    private Context context;

    public MainListAdapter(Context context, int resource, List<NewsPaper> objects) {
        super(context, resource, objects);
        inflater = LayoutInflater.from(context);
        this.resource = resource;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = (RelativeLayout) inflater.inflate(resource, null);

        NewsPaper newsPaper = getItem(position);

        TextView rowText = (TextView) convertView.findViewById(R.id.newsPaperTitle);
        rowText.setText(newsPaper.getTitle());

        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
        iconImageView.setImageDrawable(newsPaper.getIcon());

        return convertView;
    }


}
