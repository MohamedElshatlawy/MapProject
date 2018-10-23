package com.example.mohamed.mapproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class Adapter extends BaseAdapter {
    Context context;
    List<DamageModel> data;

    public Adapter(Context context, List<DamageModel> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public DamageModel getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView= LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);

        TextView tv1=convertView.findViewById(R.id.damage_name);
        TextView tv2=convertView.findViewById(R.id.damage_date);


        tv1.setText(data.get(position).getLocName());

        tv2.setText(data.get(position).getDate());

        return convertView;
    }
}
