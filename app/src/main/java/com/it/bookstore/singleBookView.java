package com.it.bookstore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class singleBookView extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref = database.getReference("books");

    TextView titleTextView;
    TextView authorTextView;
    TextView priceTextView;
    TextView courseTextView;
    TextView yearTextView;
    TextView genreTextView;
    ImageView bookImageView;
    Button buyButton;
    books book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_book_view);
        setTitle("Book");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorAccent)));
        titleTextView = findViewById(R.id.titleTextView);
        courseTextView = findViewById(R.id.courseTextView);
        priceTextView = findViewById(R.id.priceTextView);
        authorTextView = findViewById(R.id.authorTextView);
        yearTextView = findViewById(R.id.yearTextView);
        genreTextView = findViewById(R.id.genreTextView);
        bookImageView = findViewById(R.id.bookImageView);
        buyButton = findViewById(R.id.buyButton);
        String s;
        book = (books) getIntent().getSerializableExtra("book");
        s = "TITLE : "+book.title;
        titleTextView.setText(s);
        s = "AUTHOR : "+book.author;
        authorTextView.setText(s);
        s = "PRICE : " + book.price;
        priceTextView.setText(s);
        s = "COURSE : "+book.course;
        courseTextView.setText(s);
        s = "ACADEMIC YEAR : " + book.year;
        yearTextView.setText(s);
        s = "GENRE : " + book.genre;
        genreTextView.setText(s);
        Picasso.get().load(book.imageUrl).into(bookImageView);

        if(book.user.equals(user.getUid())){
            buyButton.setText("DELETE");
        }

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!book.hasBuyer(user.getUid()) && !book.user.equals(user.getUid())) {
                    Map<String, String> map1 = new HashMap<>();
                    map1.put("recepID",book.user);
                    map1.put("bookName",book.title);
                    map1.put("bookID",book.id);
                    Map<String, String> map2 = new HashMap<>();
                    map2.put("recepID",user.getUid());
                    map2.put("bookName",book.title);
                    map2.put("bookID",book.id);
                    database.getReference("users").child(book.user).child("contacts").push().setValue(map2);
                    database.getReference("users").child(user.getUid()).child("contacts").push().setValue(map1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            book.addBuyer(user.getUid());
                            ref.child(book.id).child("buyers").setValue(book.buyers).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(singleBookView.this, singleChatActivity.class);
                                    intent.putExtra("book", book);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
                else if(book.user.equals(user.getUid())){
                    database.getReference("books").child(book.id).removeValue();
                    Toast.makeText(singleBookView.this,"Deleted",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(singleBookView.this,"Already contacted seller",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
