package com.it.bookstore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class profileBooks extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref = database.getReference("books");

    ListView listView;
    ArrayList<books> bookList = new ArrayList<>();
    ArrayList<String> bookName = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_books);
        listView = findViewById(R.id.uploadedList);
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,bookName);
        listView.setAdapter(adapter);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot c: snapshot.getChildren()) {
                    books b;
                    if(c.getValue() != null) {
                        b = c.getValue(books.class);
                        if(b.user.equals(user.getUid())) {
                            bookList.add(b);
                            bookName.add(b.title);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("error","error");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(profileBooks.this,singleBookView.class);
                intent.putExtra("book",bookList.get(position));
                startActivity(intent);
            }
        });
    }
}
