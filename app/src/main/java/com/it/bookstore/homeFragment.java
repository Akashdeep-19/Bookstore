package com.it.bookstore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class homeFragment extends Fragment {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref = database.getReference("books");

    ArrayList<books> bookList = new ArrayList<>();
    ImageAdapter ia;
    GridView gridview;
    boolean loaded = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);

        getActivity().setTitle("Library");

        gridview = view.findViewById(R.id.gridView);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot c: snapshot.getChildren()) {
                    books b;
                    if(c.getValue() != null) {
                        b = c.getValue(books.class);
                        bookList.add(b);
                    }
                }
                loaded = true;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("error","error");
            }
        });

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable(){
            @Override
            public void run() {
                if(loaded){
                    ia = new ImageAdapter(getActivity(),bookList);
                    gridview.setAdapter(ia);
                    loaded = false;
                }
                handler.postDelayed(this,50);
            }
        };
        handler.post(runnable);

        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(runnable);
            }
        },5000);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), singleBookView.class);
             //   intent.putExtra("image", images.get(position));
                intent.putExtra("book", bookList.get(position));
                startActivity(intent);
            }
        });


        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.refresh_button, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.refreshButton:
                Log.i("refere","refreshed");
                ia = new ImageAdapter(getActivity(),bookList);
                gridview.setAdapter(ia);
                break;
            case R.id.searchButton:
                Intent intent = new Intent(getActivity(),searchBar.class);
                intent.putExtra("bookList",bookList);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}


