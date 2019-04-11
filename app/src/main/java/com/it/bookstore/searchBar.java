package com.it.bookstore;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

public class searchBar extends AppCompatActivity {

    EditText searchEditText;
    ListView searchResultList;
    Spinner filterSpinner;
    EditText editText;
    ArrayList<books> bookList = new ArrayList<>();
    ArrayList<books> bookCopyList = new ArrayList<>();
    ArrayList<String> searchResults = new ArrayList<>();
    ArrayAdapter adapter;
    String se;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bar);
        setTitle("Search");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorAccent)));

        searchEditText = findViewById(R.id.searchEditText);
        searchResultList = findViewById(R.id.searchResultList);
        filterSpinner = findViewById(R.id.filterSpinner);
        editText = findViewById(R.id.editText);
        bookList =  (ArrayList<books>) getIntent().getSerializableExtra("bookList");
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,searchResults);
        filterSpinner.setPrompt("Filters");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.filters));
        filterSpinner.setAdapter(spinnerAdapter);
        searchResultList.setAdapter(adapter);
        searchEditText.addTextChangedListener(textWatch);


        searchResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(searchBar.this, singleBookView.class);
                //   intent.putExtra("image", images.get(position));
                intent.putExtra("book", bookCopyList.get(position));
                startActivity(intent);
            }
        });
    }

    TextWatcher textWatch = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchResults.clear();
                bookCopyList.clear();
                int price;

                if(editText.getText().toString().equals(""))
                    price = -1;
                else
                    price = Integer.parseInt(editText.getText().toString());
                if(!s.toString().equals("")) {
                    se = s.toString().toUpperCase();
                    for (books b : bookList) {
                        int bprice = Integer.parseInt(b.price);
                        String bt = b.title.toUpperCase();
                        if(filterSpinner.getSelectedItem().toString().equals("Title"))
                            bt = b.title.toUpperCase();
                        else if(filterSpinner.getSelectedItem().toString().equals("Author"))
                            bt = b.author.toUpperCase();
                        else if(filterSpinner.getSelectedItem().toString().equals("Course"))
                            bt = b.course.toUpperCase();
                        else if(filterSpinner.getSelectedItem().toString().equals("Genre"))
                            bt = b.genre.toUpperCase();


                        if ((bt.contains(se) && bprice <= price) || (bt.contains(se) && price == -1)) {
                            searchResults.add(b.title);
                            bookCopyList.add(b);
                        }

                        if(filterSpinner.getSelectedItem().toString().equals("Academic Year")){
                            if(se.matches("[0-9]+")) {
                                int byear = Integer.parseInt(b.year);
                                if ((byear == Integer.parseInt(se) && bprice <= price) || (byear == Integer.parseInt(se) && price == -1)) {
                                    searchResults.add(b.title);
                                    bookCopyList.add(b);
                                }
                            }
                        }

                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
}
