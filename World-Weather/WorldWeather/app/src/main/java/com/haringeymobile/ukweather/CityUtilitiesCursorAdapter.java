package com.haringeymobile.ukweather;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haringeymobile.ukweather.database.CityTable;
import com.haringeymobile.ukweather.utils.SharedPrefsHelper;

/**
 * An adapter to map the cities stored in the database to the city list rows with buttons
 * requesting utility features, such as removing or renaming the city.
 */
class CityUtilitiesCursorAdapter extends BaseCityCursorAdapter {

    /**
     * A listener for city reordering and deletion-by-swiping requests.
     */
    interface Listener {

        /**
         * Removes the specified city from the database.
         *
         * @param cityId OpenWeatherMap ID for the city to be deleted
         */
        void removeCityById(int cityId);

        /**
         * If user rearranges cities in the ordered city list by dragging them in City Management
         * screen, updates ordering values for all affected cities.
         *
         * @param cityOrderFrom old position of the dragged city
         * @param cityOrderTo   new position of the dragged city
         */
        void dragCity(int cityOrderFrom, int cityOrderTo);

    }

    /**
     * A helper to implement the "view holder" design pattern.
     */
    private static class CityRowUtilitiesViewHolder {

        TextView cityNameTextView;
        ImageView buttonRename;
        ImageView buttonDelete;
    }

    CityUtilitiesCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
                               int flags, OnClickListener onClickListener) {
        super(context, layout, c, from, to, flags, onClickListener);
        listener = (Listener) context;
        isRemovalModeButton = SharedPrefsHelper.isRemovalModeButton(context);
    }

    private Listener listener;
    private boolean isRemovalModeButton;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        com.mijack.Xlog.logMethodEnter("android.view.View com.haringeymobile.ukweather.CityUtilitiesCursorAdapter.newView(android.content.Context,android.database.Cursor,android.view.ViewGroup)",this,context,cursor,parent);try{View rowView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.row_city_list_with_utils_buttons, parent, false);

        CityRowUtilitiesViewHolder holder = new CityRowUtilitiesViewHolder();
        holder.cityNameTextView = (TextView) rowView
                .findViewById(R.id.city_name_in_list_row_text_view);
        holder.buttonRename = (ImageView) rowView.findViewById(R.id.city_rename_button);
        holder.buttonRename.setOnClickListener(onClickListener);
        holder.buttonDelete = (ImageView) rowView.findViewById(R.id.city_remove_button);
        if (isRemovalModeButton) {
            holder.buttonDelete.setVisibility(View.VISIBLE);
            holder.buttonDelete.setOnClickListener(onClickListener);
        } else {
            holder.buttonDelete.setVisibility(View.GONE);
        }

        rowView.setTag(holder);
        {com.mijack.Xlog.logMethodExit("android.view.View com.haringeymobile.ukweather.CityUtilitiesCursorAdapter.newView(android.content.Context,android.database.Cursor,android.view.ViewGroup)",this);return rowView;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.view.View com.haringeymobile.ukweather.CityUtilitiesCursorAdapter.newView(android.content.Context,android.database.Cursor,android.view.ViewGroup)",this,throwable);throw throwable;}
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityUtilitiesCursorAdapter.bindView(android.view.View,android.content.Context,android.database.Cursor)",this,view,context,cursor);try{CityRowUtilitiesViewHolder holder = (CityRowUtilitiesViewHolder) view.getTag();
        int nameColumnsIndex = cursor.getColumnIndexOrThrow(CityTable.COLUMN_NAME);
        holder.cityNameTextView.setText(cursor.getString(nameColumnsIndex));com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityUtilitiesCursorAdapter.bindView(android.view.View,android.content.Context,android.database.Cursor)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityUtilitiesCursorAdapter.bindView(android.view.View,android.content.Context,android.database.Cursor)",this,throwable);throw throwable;}
    }

    @Override
    public void drop(int from, int to) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityUtilitiesCursorAdapter.drop(int,int)",this,from,to);try{super.drop(from, to);
        listener.dragCity(from, to);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityUtilitiesCursorAdapter.drop(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityUtilitiesCursorAdapter.drop(int,int)",this,throwable);throw throwable;}
    }

    @Override
    public void remove(int which) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CityUtilitiesCursorAdapter.remove(int)",this,which);try{super.remove(which);
        listener.removeCityById(getCityId(which));com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CityUtilitiesCursorAdapter.remove(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CityUtilitiesCursorAdapter.remove(int)",this,throwable);throw throwable;}
    }

}