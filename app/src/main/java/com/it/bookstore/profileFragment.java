package com.it.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class profileFragment extends Fragment {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref = database.getReference("users").child(user.getUid()).child("contacts");
    ListView listView;
    TextView profile;
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> UIDs = new ArrayList<>();
    ArrayList<String> bookIDs = new ArrayList<>();
    ArrayList<books> bookList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, null);
        getActivity().setTitle("Chats");
        listView = view.findViewById(R.id.chatListView);
        profile = view.findViewById(R.id.profileTextView);
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,names);
        listView.setAdapter(adapter);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final String u = dataSnapshot.child("recepID").getValue().toString();
                final String bn = dataSnapshot.child("bookName").getValue().toString();
                final String bi = dataSnapshot.child("bookID").getValue().toString();

                FirebaseDatabase.getInstance().getReference("users").child(u).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String s = dataSnapshot.child("email").getValue().toString() + " ("+bn+")";
                        if(!names.contains(s)) {
                            names.add(s);
                            UIDs.add(u);
                            bookIDs.add(bi);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("click","clicked");
                Intent intent = new Intent(getActivity(),singleChatActivity.class);
                intent.putExtra("receiver",UIDs.get(position));
                intent.putExtra("bookID",bookIDs.get(position));
                startActivity(intent);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),profileBooks.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
