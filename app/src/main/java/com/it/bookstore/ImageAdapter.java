package com.it.bookstore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    ArrayList<books> allBooks;
    LayoutInflater inflater;

    // Constructor
    public ImageAdapter(Context c,ArrayList<books> allBooks) {
        mContext = c;
        this.allBooks = allBooks;
        inflater = LayoutInflater.from(c);
    }

    public int getCount() {
        return this.allBooks.size();
    }

    public Object getItem(int position) {
        return this.allBooks.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.grid_cell, null);
        ImageView imageView;
        String s = this.allBooks.get(position).title.toUpperCase() + "\n" + "by "  + this.allBooks.get(position).author.toUpperCase() + "\n"
                + "Rs " + this.allBooks.get(position).price.toUpperCase();
        TextView textView = view.findViewById(R.id.titleTextView);
        textView.setText(s);
        imageView = view.findViewById(R.id.bookImage);
        Picasso.get().load(this.allBooks.get(position).imageUrl).resize(150,160).centerCrop().into(imageView);
        //imageView.setImageBitmap(this.images.get(position));
        //Log.i("image","downloaded");
        return view;
    }


}
