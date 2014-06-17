package com.example.avisreader.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.avisreader.R;
import com.example.avisreader.data.Newspaper;
import com.example.avisreader.database.DatabaseHelper;
import org.w3c.dom.Text;

import java.util.List;

public class MainListAdapter extends ArrayAdapter<Newspaper> {

    private LayoutInflater inflater;
    private int resource;
    private Context context;
    private DatabaseHelper dbHelper;

    public MainListAdapter(Context context, int resource, List<Newspaper> objects) {
        super(context, resource, objects);
        inflater = LayoutInflater.from(context);
        this.resource = resource;
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context.getApplicationContext());
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
            convertView = inflater.inflate(resource, null);

            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            holder.favoriteImageView = (ImageView) convertView.findViewById(R.id.favoriteImageView);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.newsPaperTitle);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        final Newspaper newspaper = getItem(position);

        holder.titleTextView.setText(newspaper.getTitle());

        holder.favoriteImageView.setImageResource(newspaper.isFavorite() ? R.drawable.ic_action_rating_important :
                R.drawable.ic_action_rating_not_important);

        // Find icon
        int resID = context.getApplicationContext().getResources().getIdentifier(newspaper.getIcon(), "drawable",
                context.getApplicationContext().getPackageName());
        // If no valid icon was found
        Log.d("APP", "RES ID FOR " + newspaper.getIcon() + ": " + resID);
        Log.d("APP", context.getApplicationContext().getPackageName());
        if (resID == 0) {
            resID = context.getResources().getIdentifier("no_icon", "drawable", context.getApplicationContext().getPackageName());
            Log.d("APP", "RES ID FOR " + newspaper.getIcon() + " noicon: " + resID);

        }

        Drawable icon = context.getResources().getDrawable(resID);
        holder.iconImageView.setImageDrawable(icon);

        holder.favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newspaper.isFavorite()) {
                    holder.favoriteImageView.setImageResource(R.drawable.ic_action_rating_not_important);
                    newspaper.setFavorite(false);
                    dbHelper.updateNewspaper(newspaper);
                } else {
                    holder.favoriteImageView.setImageResource(R.drawable.ic_action_rating_important);
                    newspaper.setFavorite(true);
                    dbHelper.updateNewspaper(newspaper);
                }
            }
        });

        return convertView;

    }
}
