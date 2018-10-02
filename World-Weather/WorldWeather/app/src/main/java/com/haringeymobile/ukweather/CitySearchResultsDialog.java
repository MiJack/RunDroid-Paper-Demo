package com.haringeymobile.ukweather;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.haringeymobile.ukweather.utils.ItemDecorationListDivider;

import java.util.ArrayList;
import java.util.List;

/**
 * A dialog displaying the list of found cities in response to the user's search query.
 */
public class CitySearchResultsDialog extends DialogFragment {

    private static final String TITLE_TEXT_LINE_SEPARATOR = "\n--------------\n";

    /**
     * A listener for the found city list item clicks.
     */
    public interface OnCityNamesListItemClickedListener {

        /**
         * Reacts to the city list item clicks.
         *
         * @param position clicked item position in the city list
         */
        void onFoundCityNamesItemClicked(int position);
    }

    static final String CITY_NAME_LIST = "city names";

    private OnCityNamesListItemClickedListener onCityNamesListItemClickedListener;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    /**
     * Creates a new dialog with the city list.
     *
     * @param cityNames an array of city names (including location coordinates) to be
     *                  displayed as a list
     * @return a dialog displaying the list of specified city names
     */
    static CitySearchResultsDialog newInstance(ArrayList<String> cityNames) {
        com.mijack.Xlog.logStaticMethodEnter("com.haringeymobile.ukweather.CitySearchResultsDialog com.haringeymobile.ukweather.CitySearchResultsDialog.newInstance(java.util.ArrayList)",cityNames);try{CitySearchResultsDialog dialog = new CitySearchResultsDialog();
        Bundle args = new Bundle();
        args.putStringArrayList(CITY_NAME_LIST, cityNames);
        dialog.setArguments(args);
        {com.mijack.Xlog.logStaticMethodExit("com.haringeymobile.ukweather.CitySearchResultsDialog com.haringeymobile.ukweather.CitySearchResultsDialog.newInstance(java.util.ArrayList)");return dialog;}}catch(Throwable throwable){com.mijack.Xlog.logStaticMethodExitWithThrowable("com.haringeymobile.ukweather.CitySearchResultsDialog com.haringeymobile.ukweather.CitySearchResultsDialog.newInstance(java.util.ArrayList)",throwable);throw throwable;}
    }

    @Override
    public void onAttach(Activity activity) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CitySearchResultsDialog.onAttach(android.app.Activity)",this,activity);try{super.onAttach(activity);
        try {
            onCityNamesListItemClickedListener = (OnCityNamesListItemClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCityNamesListItemClickedListener");
        }}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CitySearchResultsDialog.onAttach(android.app.Activity)",this,throwable);throw throwable;}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("android.support.v7.widget.RecyclerView com.haringeymobile.ukweather.CitySearchResultsDialog.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,inflater,container,savedInstanceState);try{View view = inflater.inflate(R.layout.fragment_search_results, container);

        createCustomDialogTitle(view);

        recyclerView = (RecyclerView) view.findViewById(R.id.general_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        int listDividerHeight = (int) getResources().getDimension(R.dimen.list_divider_height);
        recyclerView.addItemDecoration(new ItemDecorationListDivider(listDividerHeight));

        {com.mijack.Xlog.logMethodExit("android.support.v7.widget.RecyclerView com.haringeymobile.ukweather.CitySearchResultsDialog.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this);return view;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("android.support.v7.widget.RecyclerView com.haringeymobile.ukweather.CitySearchResultsDialog.onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)",this,throwable);throw throwable;}
    }

    /**
     * Replaces the default dialog's title with the custom one.
     *
     * @param view custom dialog fragment's view
     */
    private void createCustomDialogTitle(View view) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CitySearchResultsDialog.createCustomDialogTitle(android.support.v7.widget.RecyclerView)",this,view);try{getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        TextView customDialogTitle = (TextView) view.findViewById(R.id.city_search_dialog_title);
        String citySearchResultsDialogTitle = getCitySearchResultsDialogTitle();
        customDialogTitle.setText(citySearchResultsDialogTitle);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CitySearchResultsDialog.createCustomDialogTitle(android.support.v7.widget.RecyclerView)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CitySearchResultsDialog.createCustomDialogTitle(android.support.v7.widget.RecyclerView)",this,throwable);throw throwable;}
    }

    private String getCitySearchResultsDialogTitle() {
        com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.CitySearchResultsDialog.getCitySearchResultsDialogTitle()",this);try{Resources res = getResources();
        String dialogTitle = res.getString(R.string.dialog_title_search_results_part_1);
        dialogTitle += TITLE_TEXT_LINE_SEPARATOR;
        dialogTitle += res.getString(R.string.dialog_title_search_results_part_2);
        {com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.CitySearchResultsDialog.getCitySearchResultsDialogTitle()",this);return dialogTitle;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.CitySearchResultsDialog.getCitySearchResultsDialogTitle()",this,throwable);throw throwable;}
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CitySearchResultsDialog.onActivityCreated(android.os.Bundle)",this,savedInstanceState);try{super.onActivityCreated(savedInstanceState);
        if (adapter == null) {
            initialiseRecyclerViewAdapter();
        }
        recyclerView.setAdapter(adapter);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CitySearchResultsDialog.onActivityCreated(android.os.Bundle)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CitySearchResultsDialog.onActivityCreated(android.os.Bundle)",this,throwable);throw throwable;}
    }

    /**
     * Creates a new adapter to map city names to the list rows.
     */
    private void initialiseRecyclerViewAdapter() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CitySearchResultsDialog.initialiseRecyclerViewAdapter()",this);try{Bundle args = getArguments();
        ArrayList<String> cityNames = args.getStringArrayList(CITY_NAME_LIST);
        adapter = new CityNameAdapter(cityNames);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CitySearchResultsDialog.initialiseRecyclerViewAdapter()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CitySearchResultsDialog.initialiseRecyclerViewAdapter()",this,throwable);throw throwable;}
    }

    @Override
    public void onDestroyView() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CitySearchResultsDialog.onDestroyView()",this);try{recyclerView.setAdapter(null);
        super.onDestroyView();com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CitySearchResultsDialog.onDestroyView()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CitySearchResultsDialog.onDestroyView()",this,throwable);throw throwable;}
    }

    @Override
    public void onDetach() {
        com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CitySearchResultsDialog.onDetach()",this);try{super.onDetach();
        onCityNamesListItemClickedListener = null;com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CitySearchResultsDialog.onDetach()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CitySearchResultsDialog.onDetach()",this,throwable);throw throwable;}
    }

    /**
     * A helper to implement the "view holder" design pattern.
     */
    private static class CityNameViewHolder extends RecyclerView.ViewHolder {
        private TextView cityNameTextView;

        public CityNameViewHolder(TextView view) {
            super(view);
            cityNameTextView = view;
        }
    }

    /**
     * An adapter to map city names to the list rows.
     */
    private class CityNameAdapter extends RecyclerView.Adapter<CityNameViewHolder> {
        final List<String> cityNames;

        public CityNameAdapter(List<String> cityNames) {
            this.cityNames = cityNames;
        }

        @Override
        public CityNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            com.mijack.Xlog.logMethodEnter("com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameViewHolder com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter.onCreateViewHolder(android.view.ViewGroup,int)",this,parent,viewType);try{TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_city_search_list, parent, false);
            {com.mijack.Xlog.logMethodExit("com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameViewHolder com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter.onCreateViewHolder(android.view.ViewGroup,int)",this);return new CityNameViewHolder(v);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameViewHolder com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter.onCreateViewHolder(android.view.ViewGroup,int)",this,throwable);throw throwable;}
        }

        @Override
        public void onBindViewHolder(CityNameViewHolder holder, final int position) {
            com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter.onBindViewHolder(com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameViewHolder,int)",this,holder,position);try{String cityName = cityNames.get(position);
            holder.cityNameTextView.setText(cityName);

            setBackgroundForListRow(position, holder.cityNameTextView);

            int padding = (int) getResources().getDimension(R.dimen.padding_very_large);
            holder.cityNameTextView.setPadding(padding, padding, padding, padding);

            holder.cityNameTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter$1.onClick(android.support.v7.widget.RecyclerView)",this,v);try{getDialog().dismiss();
                    onCityNamesListItemClickedListener.onFoundCityNamesItemClicked(position);com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter$1.onClick(android.support.v7.widget.RecyclerView)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter$1.onClick(android.support.v7.widget.RecyclerView)",this,throwable);throw throwable;}
                }
            });com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter.onBindViewHolder(com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameViewHolder,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter.onBindViewHolder(com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameViewHolder,int)",this,throwable);throw throwable;}
        }

        /**
         * Makes the list to look nicer by setting alternating backgrounds to it's items (rows).
         *
         * @param position city list position
         * @param rowView  a view displaying a single list item
         */
        private void setBackgroundForListRow(int position, View rowView) {
            com.mijack.Xlog.logMethodEnter("void com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter.setBackgroundForListRow(int,android.support.v7.widget.RecyclerView)",this,position,rowView);try{if (position % 2 == 1) {
                rowView.setBackgroundResource(BaseCityCursorAdapter.BACKGROUND_RESOURCE_ODD);
            } else {
                rowView.setBackgroundResource(BaseCityCursorAdapter.BACKGROUND_RESOURCE_EVEN);
            }com.mijack.Xlog.logMethodExit("void com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter.setBackgroundForListRow(int,android.support.v7.widget.RecyclerView)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter.setBackgroundForListRow(int,android.support.v7.widget.RecyclerView)",this,throwable);throw throwable;}
        }

        @Override
        public int getItemCount() {
            com.mijack.Xlog.logMethodEnter("int com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter.getItemCount()",this);try{com.mijack.Xlog.logMethodExit("int com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter.getItemCount()",this);return cityNames.size();}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.haringeymobile.ukweather.CitySearchResultsDialog$CityNameAdapter.getItemCount()",this,throwable);throw throwable;}
        }
    }

}
