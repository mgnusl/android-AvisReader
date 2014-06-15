package com.example.avisreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.avisreader.R;
import com.example.avisreader.data.Newspaper;

import java.util.List;

public class MainListAdapter extends ArrayAdapter<Newspaper> {

    private LayoutInflater inflater;
    private int resource;
    private Context context;

    public MainListAdapter(Context context, int resource, List<Newspaper> objects) {
        super(context, resource, objects);
        inflater = LayoutInflater.from(context);
        this.resource = resource;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(resource, null);

        final Newspaper newspaper = getItem(position);

        TextView title = (TextView) convertView.findViewById(R.id.newsPaperTitle);
        title.setText(newspaper.getTitle());

        final ImageView favoriteImage = (ImageView) convertView.findViewById(R.id.favoriteImageView);
        favoriteImage.setImageResource(newspaper.isFavorite() ? R.drawable.ic_action_rating_important :
                R.drawable.ic_action_rating_not_important);

        favoriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newspaper.isFavorite()) {
                    favoriteImage.setImageResource(R.drawable.ic_action_rating_not_important);
                    newspaper.setFavorite(false);
                }
                else {
                    favoriteImage.setImageResource(R.drawable.ic_action_rating_important);
                    newspaper.setFavorite(true);
                }
            }
        });

        ImageView iconImage = (ImageView) convertView.findViewById(R.id.iconImageView);
        iconImage.setImageDrawable(newspaper.getIcon());

        return convertView;
    }


}
