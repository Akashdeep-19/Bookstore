package com.it.bookstore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class searchFragment extends Fragment {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref = database.getReference("users");
    private FirebaseListAdapter<chatClass> adapter;
    ListView listOfMessages;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null);
        getActivity().setTitle("Requests");
        listOfMessages = view.findViewById(R.id.list_of_messages);
//        Intent intent = new Intent(getActivity(),chatViewActivity.class);
//        startActivity(intent);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        displayChats();
        final EditText input = view.findViewById(R.id.input);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!input.getText().toString().equals("")) {
                    FirebaseDatabase.getInstance()
                            .getReference().child("chats")
                            .push()
                            .setValue(new chatClass(input.getText().toString(), user.getUid()));
                    input.setText("");
                    displayChats();
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    void displayChats () {

        FirebaseListOptions<chatClass> options = new FirebaseListOptions.Builder<chatClass>()
                .setLayout(R.layout.message)
                .setQuery(FirebaseDatabase.getInstance().getReference().child("chats"), chatClass.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new FirebaseListAdapter<chatClass>(options) {
            @Override
            protected void populateView(View v, final chatClass model, int position) {
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)",
                        model.getMessageTime()));

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, String> map1 = new HashMap<>();
                        map1.put("recepID",model.getMessageUser());
                        map1.put("bookName","Request");
                        map1.put("bookID","noID");
                        Map<String, String> map2 = new HashMap<>();
                        map2.put("recepID",user.getUid());
                        map2.put("bookName","Request");
                        map2.put("bookID","noID");
                        Log.i("id",model.getMessageUser());
                        database.getReference("users").child(model.getMessageUser()).child("contacts").push().setValue(map2);
                        database.getReference("users").child(user.getUid()).child("contacts").push().setValue(map1).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(getActivity(), singleChatActivity.class);
                                intent.putExtra("receiver",model.getMessageUser());
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        };
        listOfMessages.setAdapter(adapter);
    }
}
