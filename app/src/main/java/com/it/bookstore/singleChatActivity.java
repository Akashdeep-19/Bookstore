package com.it.bookstore;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class singleChatActivity extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref = database.getReference("users");
    FirebaseRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    books book;
    String receiver,recieverName = "",recieverID,bookID;
    RecyclerView messageList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);
        setTitle("Chat");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorAccent)));
        messageList = findViewById(R.id.chatRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        messageList.setLayoutManager(linearLayoutManager);
        messageList.setHasFixedSize(false);

        final TextView recieverTextView = findViewById(R.id.receiverTextView);
        book = (books) getIntent().getSerializableExtra("book");
        recieverID = getIntent().getStringExtra("receiver");
        bookID = getIntent().getStringExtra("bookID");

        if(book != null) {
            recieverID = book.user;
            ref.child(book.user).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    recieverName = dataSnapshot.child("email").getValue().toString();
                    recieverTextView.setText(recieverName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            ref.child(recieverID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    recieverName = dataSnapshot.child("email").getValue().toString();
                    Log.i("name",recieverName);
                    recieverTextView.setText(recieverName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        displayChats();
        scrollDown();
        final EditText input = findViewById(R.id.input);
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayChats();
                scrollDown();
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!input.getText().toString().equals("")) {
                    ref.child(user.getUid()).child("personalChats")
                            .push()
                            .setValue(new chatClass(input.getText().toString(), user.getEmail(), recieverName, user.getEmail()));
                    ref.child(recieverID).child("personalChats")
                            .push()
                            .setValue(new chatClass(input.getText().toString(), recieverName, user.getEmail(), user.getEmail()));
                    input.setText("");

                    displayChats();
                    scrollDown();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    private void displayChats() {

        FirebaseRecyclerOptions<chatClass> options = new FirebaseRecyclerOptions.Builder<chatClass>()
                .setQuery(ref.child(user.getUid()).child("personalChats"), chatClass.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new FirebaseRecyclerAdapter<chatClass, ChatHolder>(options) {
            @Override
            public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_message, parent, false);

                return new ChatHolder(view);
            }
            @Override
            protected void onBindViewHolder(ChatHolder holder, int position, chatClass model) {
                if(!model.getMessageRecep().equals(recieverName)) {
                    holder.Layout_hide();
                }
                else{
                    if (model.getMessageSender().equals(user.getEmail())) {
                        holder.setUTxtTitle(model.getMessageText());
                        holder.setUTxtTime(DateFormat.format("dd-MM(HH:mm)",
                                model.getMessageTime()).toString());
                    }
                    if (model.getMessageSender().equals(recieverName)) {
                        holder.setSTxtTitle(model.getMessageText());
                        holder.setSTxtTime(DateFormat.format("dd-MM(HH:mm)",
                                model.getMessageTime()).toString());
                    }
                }
            }
        };
        messageList.setAdapter(adapter);
    }

    void scrollDown (){
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                messageList.smoothScrollToPosition(adapter.getItemCount());
            }
        });
    }

    public class ChatHolder extends RecyclerView.ViewHolder {
        //public RelativeLayout root;
        public TextView txtUTitle;
        public TextView txtUTime;
        public TextView txtSTitle;
        public TextView txtSTime;
        RelativeLayout layout;
        RelativeLayout.LayoutParams params;



        public ChatHolder(View itemView) {
            super(itemView);
            //root = itemView.findViewById(R.id.list_root);
            txtUTitle = itemView.findViewById(R.id.message_user_text);
            txtUTime = itemView.findViewById(R.id.message_user_time);
            txtSTitle = itemView.findViewById(R.id.message_sender_text);
            txtSTime = itemView.findViewById(R.id.message_sender_time);
            layout = itemView.findViewById(R.id.rl_messages);
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public void setUTxtTitle(String string) {
            txtUTitle.setVisibility(View.VISIBLE);
            txtUTitle.setText(string);
            txtSTitle.setVisibility(View.INVISIBLE);

        }


        public void setUTxtTime(String string) {
            txtUTime.setVisibility(View.VISIBLE);
            txtUTime.setText(string);
            txtSTime.setVisibility(View.INVISIBLE);
        }

        public void setSTxtTitle(String string) {
            txtSTitle.setVisibility(View.VISIBLE);
            txtSTitle.setText(string);
            txtUTitle.setVisibility(View.INVISIBLE);

        }


        public void setSTxtTime(String string) {
            txtSTime.setVisibility(View.VISIBLE);
            txtSTime.setText(string);
            txtUTime.setVisibility(View.INVISIBLE);
        }

        public void Layout_hide() {
            params.height = 0;
            //itemView.setLayoutParams(params); //This One.
            layout.setLayoutParams(params);   //Or This one.

        }

    }

}
