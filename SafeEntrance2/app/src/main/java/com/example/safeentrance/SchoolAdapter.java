package com.example.safeentrance;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SchoolAdapter extends ArrayAdapter<String> {
    private List <String> FullSchools;

    public SchoolAdapter(@NonNull Context context, @NonNull List<String> Schools) {
        super(context, 0, Schools);
        FullSchools=new ArrayList<>(Schools);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return schoolfilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.listdesign,parent,false);
        }
        TextView textViewName=convertView.findViewById(R.id.text_view_name);

        String y=getItem(position);


        if (y != null) {
            textViewName.setText(y);
        }

        return convertView;

    }

    private Filter schoolfilter=new Filter() {
        @Override
        public FilterResults performFiltering(CharSequence constraint) {
            FilterResults results=new FilterResults();
            List<String> suggestions=new ArrayList<>();

            if(constraint==null||constraint.length()==0){
                suggestions.addAll(FullSchools);
            }
            else{
                String filterpattern=constraint.toString().toLowerCase().trim();
                for(String x:FullSchools){
                    if(x.toLowerCase().contains(filterpattern)){
                        suggestions.add(x);
                    }
                }

            }
            results.values=suggestions;
            results.count=suggestions.size();
            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List)results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((String)resultValue);
        }
    };
}
