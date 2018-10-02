package com.haringeymobile.ukweather.settings;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import com.haringeymobile.ukweather.R;

/**
 * An adapter to map the app theme colours to the preference list.
 */
public class AppThemeArrayAdapter extends ArrayAdapter<CharSequence> {

    class PreferenceViewHolder {
        private View colorView;
        private CheckedTextView nameTextView;
    }

    private Context context;
    /**
     * The position of user preferred (or default) app theme in the app theme entry list.
     */
    private int index;
    /**
     * Color resource ids referenced in {@link R.array#pref_color_values}.
     */
    private int[] resourceIds;

    public AppThemeArrayAdapter(Context context, int textViewResourceId, CharSequence[] objects,
                                int[] ids, int i) {
        super(context, textViewResourceId, objects);
        this.context = context;
        index = i;
        resourceIds = ids;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        com.mijack.Xlog.logMethodEnter("android.view.View com.haringeymobile.ukweather.settings.AppThemeArrayAdapter.getView(int,android.view.View,android.view.ViewGroup)",this,position,convertView,parent);try{PreferenceViewHolder holder;
        View rowView = convertView;

        if (rowView == null) {
            rowView = LayoutInflater.from(context).inflate(R.layout.row_app_theme_preference_list,
                    parent, false);
            holder = new PreferenceViewHolder();

            holder.colorView = rowView.findViewById(R.id.app_theme_color);
            holder.nameTextView = (CheckedTextView) rowView.findViewById(R.id.app_theme_name);
            rowView.setTag(holder);
        }

        holder = (PreferenceViewHolder) rowView.getTag();

        GradientDrawable gradientDrawable = (GradientDrawable) holder.colorView.getBackground();
        gradientDrawable.setColor(resourceIds[position]);

        holder.nameTextView.setText(getItem(position));
        holder.nameTextView.setChecked(position == index);
        {com.mijack.Xlog.logMethodExit("android.view.View com.haringeymobile.ukweather.settings.AppThemeArrayAdapter.getView(int,android.view.View,android.view.ViewGroup)",this);return rowView;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.haringeymobile.ukweather.settings.AppThemeArrayAdapter.getView(int,android.view.View,android.view.ViewGroup)",this,throwable);throw throwable;}
    }

}
