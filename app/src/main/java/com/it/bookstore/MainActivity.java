package com.it.bookstore;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    EditText nameEditText;
    EditText passwordEditText;
    TextView signupTextView;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();



    public void onClick(View view){
        if(view.getId() == R.id.signUpTextView){
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            Log.i("signup","clicked");
        }
    }

    public void loginClicked(View view){
        Log.i("login","clicked");
        if(nameEditText.getText().toString().equals("") || passwordEditText.getText().toString().equals("")){
            Toast.makeText(MainActivity.this,"Enter email and password",Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(nameEditText.getText().toString(), passwordEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            login();
                        } else {
                            Log.i("login","unsuccesfull");
                            Toast.makeText(MainActivity.this, "Account doesn't exist ,SignUp instead",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Login");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorAccent)));
        nameEditText = findViewById(R.id.nameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signupTextView = findViewById(R.id.signUpTextView);
        signupTextView.setOnClickListener(this);
    }
    //@Override



     public void login (){
         Intent intent = new Intent(this, booksViewActivity.class);
         startActivity(intent);
    }
}
