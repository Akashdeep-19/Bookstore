package com.it.bookstore;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    EditText emailEditText;
    EditText nameEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setTitle("Sign Up");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorAccent)));

        emailEditText = findViewById(R.id.emailSUEditText);
        nameEditText = findViewById(R.id.nameSUEditText);
        passwordEditText = findViewById(R.id.passwordSUEditText);
    }

    public void signupClicked(View view){
        if(emailEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")){
            Toast.makeText(this,"Enter email and password",Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString(), passwordEditText.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid())
                                    .child("email").setValue(emailEditText.getText().toString());
                            login();
                            Log.i("login","succcessful");

                        } else {
                            Toast.makeText(SignupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    public void login (){
        Intent intent = new Intent(this, booksViewActivity.class);
        startActivity(intent);
    }
}
