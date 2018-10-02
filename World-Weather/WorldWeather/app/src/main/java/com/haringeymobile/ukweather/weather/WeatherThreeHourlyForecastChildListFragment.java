package com.haringeymobile.ukweather.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.haringeymobile.ukweather.R;
import com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast;
import com.haringeymobile.ukweather.utils.ItemDecorationListDivider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Fragment displaying a list of three-hourly forecasts for one day.
 */
public class WeatherThreeHourlyForecastChildListFragment extends Fragment {

    public interface IconCacheRequestListener {

        /**
         * Obtains the memory cache storing weather icon bitmaps.
         */
        LruCache<String, Bitmap> getIconMemoryCache();

    }

    private static final String JSON_STRING_LIST = "json string list";

    private IconCacheRequestListener iconCacheRequestListener;

    /**
     * Creates and sets {@link WeatherThreeHourlyForecastChildListFragment}.
     *
     * @param threeHourlyForecastJsonStrings JSON strings obtained from the OWM, containing weather
     *                                       information
     * @return fragment, which should display the three-hourly forecasts for one day
     */
    public static WeatherThreeHourlyForecastChildListFragment newInstance(
            ArrayList<String> threeHourlyForecastJsonStrings) {
        com.mijack.Xlog.logStaticMethodEnter("com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment.newInstance(java.util.ArrayList)",threeHourlyForecastJsonStrings);try{WeatherThreeHourlyForecastChildListFragment fragment =
                new WeatherThreeHourlyForecastChildListFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(JSON_STRING_LIST, threeHourlyForecastJsonStrings);
        fragment.setArguments(args);
        {com.mijack.Xlog.logStaticMethodExit("com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment.newInstance(java.util.ArrayList)");return fragment;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment.newInstance(java.util.ArrayList)",throwable);throw throwable;}
    }

    @Override
    public void onAttach(Context context) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment.onAttach(android.content.Context)",this,context);try{super.onAttach(context);
        iconCacheRequestListener = (IconCacheRequestListener) context;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment.onAttach(android.content.Context)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment.onAttach(android.content.Context)",this,throwable);throw throwable;}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.support.v7.widget.RecyclerView com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,inflater,container,savedInstanceState);try{View view = inflater.inflate(R.layout.general_recycler_view, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.general_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        int listDividerHeight = (int) getResources().getDimension(R.dimen.list_divider_height);
        recyclerView.addItemDecoration(new ItemDecorationListDivider(listDividerHeight));

        ThreeHourlyForecastAdapter adapter = new ThreeHourlyForecastAdapter(getArguments()
                .getStringArrayList(JSON_STRING_LIST));
        recyclerView.setAdapter(adapter);

        {com.mijack.Xlog.logMethodExit("android.support.v7.widget.RecyclerView com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this);return view;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.support.v7.widget.RecyclerView com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,throwable);throw throwable;}
    }

    private class ThreeHourlyForecastViewHolder extends RecyclerView.ViewHolder {
        private TextView forecastStartHourTextView;
        private TextView temperatureTextView;
        private TextView conditionsTextView;
        private ImageView conditionsImageView;
        private TextView pressureTextView;
        private TextView humidityTextView;
        private TextView windTextView;

        ThreeHourlyForecastViewHolder(View itemView) {
            super(itemView);
            forecastStartHourTextView = (TextView) itemView.findViewById(R.id.forecast_hour_start);
            temperatureTextView = (TextView) itemView.findViewById(R.id.temperature_text_view);
            conditionsTextView = (TextView) itemView.findViewById(
                    R.id.weather_conditions_text_view);
            conditionsImageView = (ImageView) itemView.findViewById(
                    R.id.weather_conditions_image_view);
            pressureTextView = (TextView) itemView.findViewById(
                    R.id.atmospheric_pressure_text_view);
            humidityTextView = (TextView) itemView.findViewById(R.id.humidity_text_view);
            windTextView = (TextView) itemView.findViewById(R.id.wind_text_view);
        }
    }

    /**
     * An adapter to bind three-hourly forecast data to the respective rows of the daily
     * three-hourly forecast lists.
     */
    private class ThreeHourlyForecastAdapter extends
            RecyclerView.Adapter<ThreeHourlyForecastViewHolder> {

        private static final String TIME_TEMPLATE = "HH:mm";

        private ArrayList<String> threeHourlyForecastJsonStrings;
        private WeatherInformationDisplayer weatherInformationDisplayer;

        ThreeHourlyForecastAdapter(ArrayList<String> threeHourlyForecastJsonStrings) {
            this.threeHourlyForecastJsonStrings = threeHourlyForecastJsonStrings;
            weatherInformationDisplayer = new WeatherInformationDisplayer(getContext(),
                    iconCacheRequestListener.getIconMemoryCache());
        }

        @Override
        public ThreeHourlyForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastViewHolder com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastAdapter.onCreateViewHolder(android.view.ViewGroup,int)",this,parent,viewType);try{View rowView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_three_hourly_forecast, parent, false);
            {com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastViewHolder com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastAdapter.onCreateViewHolder(android.view.ViewGroup,int)",this);return new ThreeHourlyForecastViewHolder(rowView);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastViewHolder com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastAdapter.onCreateViewHolder(android.view.ViewGroup,int)",this,throwable);throw throwable;}
        }

        @Override
        public void onBindViewHolder(ThreeHourlyForecastViewHolder holder, int position) {
            com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastAdapter.onBindViewHolder(com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastViewHolder,int)",this,holder,position);try{String jsonString = threeHourlyForecastJsonStrings.get(position);
            Gson gson = new Gson();
            CityThreeHourlyWeatherForecast forecast = gson.fromJson(jsonString,
                    CityThreeHourlyWeatherForecast.class);

            displayForecastTime(holder, forecast);
            weatherInformationDisplayer.displayConditions(forecast, holder.conditionsTextView,
                    holder.conditionsImageView);
            weatherInformationDisplayer.displayWeatherNumericParametersText(forecast,
                    holder.temperatureTextView, holder.pressureTextView, holder.humidityTextView);
            weatherInformationDisplayer.displayWindInfo(forecast, holder.windTextView);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastAdapter.onBindViewHolder(com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastViewHolder,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastAdapter.onBindViewHolder(com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastViewHolder,int)",this,throwable);throw throwable;}
        }

        /**
         * Obtains and displays the forecast start and end times.
         *
         * @param holder   helper class, holding various views
         * @param forecast weather forecast for one three hour period
         */
        private void displayForecastTime(ThreeHourlyForecastViewHolder holder,
                                         CityThreeHourlyWeatherForecast forecast) {
            com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastAdapter.displayForecastTime(com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastViewHolder,com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast)",this,holder,forecast);try{@SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.applyLocalizedPattern(TIME_TEMPLATE);

            long startTime = forecast.getDate() * 1000;
            Date date = new Date(startTime);
            holder.forecastStartHourTextView.setText(simpleDateFormat.format(date));com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastAdapter.displayForecastTime(com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastViewHolder,com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastAdapter.displayForecastTime(com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastViewHolder,com.haringeymobile.ukweather.data.objects.CityThreeHourlyWeatherForecast)",this,throwable);throw throwable;}
        }

        @Override
        public int getItemCount() {
            com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastAdapter.getItemCount()",this);try{com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastAdapter.getItemCount()",this);return threeHourlyForecastJsonStrings.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.weather.WeatherThreeHourlyForecastChildListFragment$ThreeHourlyForecastAdapter.getItemCount()",this,throwable);throw throwable;}
        }
    }

}