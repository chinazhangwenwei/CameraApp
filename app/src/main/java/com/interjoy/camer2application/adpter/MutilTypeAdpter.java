package com.interjoy.camer2application.adpter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class MutilTypeAdpter<T> extends BaseAdapter {
    private List<T> data;
    private int typeMax = 2;

    public MutilTypeAdpter(List<T> data, int typeMax) {
        this.data = data;
        this.typeMax = typeMax;
    }

    @Override
    public int getItemViewType(int position) {
        return getType(position);
    }

    @Override
    public int getViewTypeCount() {
        return typeMax;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewholder = initMutilHolder(getItemViewType(position), convertView, parent);

        convert(viewholder, getItem(position));

        return viewholder.getConvertView();
    }

    public abstract int getType(int position);

    public abstract void convert(ViewHolder helper, T item);

    public abstract ViewHolder initMutilHolder(int type, View convertView, ViewGroup viewGroup);
}