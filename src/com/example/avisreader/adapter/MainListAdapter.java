package com.example.avisreader.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.avisreader.R;
import com.example.avisreader.data.Newspaper;
import com.example.avisreader.database.DatabaseHelper;
import com.example.avisreader.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainListAdapter extends ArrayAdapter<Newspaper> {

    private LayoutInflater inflater;
    private int resource;
    private Context context;
    private DatabaseHelper dbHelper;
    private List<Newspaper> newsPaperList;

    public MainListAdapter(Context context, int resource, List<Newspaper> objects) {
        super(context, resource, objects);
        inflater = LayoutInflater.from(context);
        this.resource = resource;
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context.getApplicationContext());
        newsPaperList = objects;

        // Initial sort of the dataset and notify the view of updates
        List<Newspaper> tempList = Utils.sortDataset(dbHelper.getAllNewspapers());
        newsPaperList.clear();
        newsPaperList.addAll(tempList);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView iconImageView;
        ImageView favoriteImageView;
        TextView titleTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);

            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            holder.favoriteImageView = (ImageView) convertView.findViewById(R.id.favoriteImageView);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.newsPaperTitle);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final Newspaper newspaper = getItem(position);

        holder.titleTextView.setText(newspaper.getTitle());

        //Typeface font = Typeface.createFromAsset(context.getApplicationContext().getAssets(), "fonts/calibri.ttf");
        //holder.titleTextView.setTypeface(font);


        holder.favoriteImageView.setImageResource(newspaper.isFavorite() ? R.drawable.ic_action_rating_important :
                R.drawable.ic_action_rating_not_important);

        // Find icon
        int resID = context.getApplicationContext().getResources().getIdentifier(newspaper.getIcon().trim(), "drawable",
                context.getApplicationContext().getPackageName());
        // If no valid icon was found
        if (resID == 0) {
            resID = context.getResources().getIdentifier("ic_action_collections_view_as_list",
                    "drawable", context.getApplicationContext().getPackageName());
        }

        Drawable icon = context.getResources().getDrawable(resID);

        if (newspaper.getIconBitmap() == null) {
            holder.iconImageView.setImageDrawable(icon);
        } else {
            holder.iconImageView.setImageBitmap(newspaper.getIconBitmap());
        }

        holder.favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newspaper.isFavorite()) {
                    holder.favoriteImageView.setImageResource(R.drawable.ic_action_rating_not_important);
                    newspaper.setFavorite(false);
                    dbHelper.updateNewspaperFavorite(newspaper);
                } else {
                    holder.favoriteImageView.setImageResource(R.drawable.ic_action_rating_important);
                    newspaper.setFavorite(true);
                    dbHelper.updateNewspaperFavorite(newspaper);
                }

                // Sort the dataset and notify the view of updates
                List<Newspaper> tempList = Utils.sortDataset(dbHelper.getAllNewspapers());
                newsPaperList.clear();
                newsPaperList.addAll(tempList);
                notifyDataSetChanged();
            }
        });


        return convertView;

    }



}
