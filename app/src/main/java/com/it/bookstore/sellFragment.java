package com.it.bookstore;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class sellFragment extends Fragment {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference ref = database.getReference(),bookRef;


    EditText titleEditText;
    EditText authorEditText;
    EditText courseEditText;
    EditText priceEditText;
    EditText yearEditText;
    EditText genreEditText;
    ImageView bookImage;
    boolean imageSet = false;
    Button ib;
    String imageUrl = "";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sell, container, false);
        View view = inflater.inflate(R.layout.fragment_sell, null);
        getActivity().setTitle("Sell");
        titleEditText = view.findViewById(R.id.titleEditText);
        authorEditText = view.findViewById(R.id.authorEditText);
        courseEditText = view.findViewById(R.id.courseEditText);
        priceEditText = view.findViewById(R.id.priceEditText);
        yearEditText = view.findViewById(R.id.yearEditText);
        genreEditText = view.findViewById(R.id.genreEditText);
        bookImage = view.findViewById(R.id.bookImageView);
        ib = view.findViewById(R.id.imageButton);

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else
                    getPhoto();
            }
        });
        Button sb = view.findViewById(R.id.sellButton);
        sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check()) {
                    Toast.makeText(getActivity(),"Uploading Don't Exit",Toast.LENGTH_LONG).show();
                    upload(bookImage);
                }
                else
                    Toast.makeText(getActivity(),"Enter the details properly",Toast.LENGTH_LONG).show();
                Log.i("sell", "clicked");
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImage = data.getData();
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                bookImage.setImageBitmap(bitmap);
                imageSet = true;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    void upload(ImageView imageView) {
        final String imageName = UUID.randomUUID().toString() + ".jpg";
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("images").child(imageName).putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FirebaseStorage.getInstance().getReference().child("images/"+imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl = uri.toString();
                        DatabaseReference b = ref.child("books").push();
                        b.setValue(new books(user.getUid(),b.getKey(),titleEditText.getText().toString(), authorEditText.getText().toString()
                       , courseEditText.getText().toString(),imageUrl,genreEditText.getText().toString(), priceEditText.getText().toString(),yearEditText.getText().toString()));
                        Toast.makeText(getActivity(),"UPLOADED",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    boolean check (){
        if(authorEditText.getText().toString().equals("") || titleEditText.getText().toString().equals("") || priceEditText.getText().toString().equals("") ||
                !imageSet)
            return false;
        if((!courseEditText.getText().toString().equals("") && !yearEditText.getText().toString().equals("")) || !genreEditText.getText().toString().equals(""))
            return true;
        else
            return false;
    }
}



