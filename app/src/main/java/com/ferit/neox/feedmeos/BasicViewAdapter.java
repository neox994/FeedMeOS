package com.ferit.neox.feedmeos;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class BasicViewAdapter extends BaseAdapter {

    ArrayList<BasicItem> mItems;

    public BasicViewAdapter(ArrayList<BasicItem> items) {
        this.mItems = items;
    }

    public BasicViewAdapter() {

    }


    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if (view == null)
        {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            view = inflater.inflate(R.layout.places, viewGroup, false);
            viewHolder  = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BasicItem item = this.mItems.get(i);
        viewHolder.tvTitle.setText(item.getName());
        byte[] imgBytes = Base64.decode(item.getImage().getBytes(), Base64.DEFAULT);
        Bitmap img = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
        viewHolder.ivImg.setImageBitmap(img);
        return view;
    }

    public void clear() {
        mItems.clear();
    }

    public void add(BasicItem basicItem) {
        mItems.add(basicItem);
        this.notifyDataSetChanged();
    }

    public String getName(int i){

        return this.mItems.get(i).getName();
    }
    private class ViewHolder {
        TextView tvTitle;
        ImageView ivImg;

        public ViewHolder(View view) {
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.ivImg = view.findViewById(R.id.ivImg);
        }
    }
}
