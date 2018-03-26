package com.vvahe.aramis2is70;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    private CircleImageView profilePicture;
    private Button changeImagebtn;
    private Button galleryTestbtn;
    private ImageButton back;
    private EditText firstName;
    private EditText middleName;
    private EditText lastName;
    private EditText study;
    private EditText email;
    private EditText year;
    private User userObj = User.getInstance();

    private static final int TAKE_PICTURE = 1;
    private static final int GALLERY_INTENT = 2;
    private StorageReference mStorage;  // reference to root firebaseStorage
    private StorageReference picStorage;    // reference to profile picture in storage
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        profilePicture = findViewById(R.id.profilePicture);
        changeImagebtn = findViewById(R.id.change_picture_button);
        galleryTestbtn = findViewById(R.id.buttonGalleryTest);
        back = (ImageButton)findViewById(R.id.backButton);
        firstName = (EditText)findViewById(R.id.profileFieldFirstname) ;
        middleName = (EditText)findViewById (R.id.profileFieldMiddlename);
        lastName = (EditText)findViewById (R.id.profileFieldLastname);
        email = (EditText)findViewById (R.id.profileFieldEmail);
        study = (EditText)findViewById (R.id.profileFieldStudy);
        year = (EditText)findViewById (R.id.profileFieldYear);
        mStorage = FirebaseStorage.getInstance().getReference();
        picStorage = mStorage.child(userObj.userID).child("Profile Picture");
        mProgressDialog = new ProgressDialog(this);

        userObj.firebaseThisUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.getKey().equals("firstName")){
                        Integer cursor = firstName.getSelectionStart();
                        firstName.setText(ds.getValue(String.class));
                        firstName.setSelection(cursor);
                    } else if (ds.getKey().equals("middleName")){
                        Integer cursor = middleName.getSelectionStart();
                        middleName.setText(ds.getValue(String.class));
                        middleName.setSelection(cursor);
                    } else if (ds.getKey().equals("lastName")){
                        Integer cursor = lastName.getSelectionStart();
                        lastName.setText(ds.getValue(String.class));
                        lastName.setSelection(cursor);
                    } else if (ds.getKey().equals("study")){
                        Integer cursor = study.getSelectionStart();
                        study.setText(ds.getValue(String.class));
                        study.setSelection(cursor);
                    } else if (ds.getKey().equals("email")){
                        Integer cursor = email.getSelectionStart();
                        email.setText(ds.getValue(String.class));
                        email.setSelection(cursor);
                    } else if (ds.getKey().equals("year")){
                        Integer cursor = year.getSelectionStart();
                        year.setText(ds.getValue(Integer.class).toString());
                        year.setSelection(cursor);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firstName();
        middleName();
        lastName();
        study();
        email();
        year();
        back();
        setPicture();

        changeImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        galleryTestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromGallery();
            }
        });


    }

    public  void setPicture() {
        if(picStorage != null){
            picStorage.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    profilePicture.setImageBitmap(bmp);
                }
            });
        }
    }

    public void firstName(){
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                userObj.setFirstName(s.toString());
            }
        });
    }

    public void middleName(){
        middleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                userObj.setMiddleName(s.toString());
            }
        });
    }

    public void lastName(){
        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                userObj.setLastName(s.toString());
            }
        });
    }

    public void study(){
        study.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                userObj.setStudy(s.toString());
            }
        });
    }

    public void year(){
        year.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                userObj.setYear(Integer.parseInt(s.toString()));
            }
        });
    }

    public void email(){
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                userObj.setEmail(s.toString());
            }
        });
    }

    public void back(){
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    public void fromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");

        startActivityForResult(intent, GALLERY_INTENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    mProgressDialog.setMessage("Uploading ...");
                    mProgressDialog.show();

                    //get the camera image
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dataBAOS = baos.toByteArray();

                    // upload to firebase
                    StorageReference filepath = mStorage.child(userObj.userID).child("Profile Picture");
                    UploadTask uploadTask = filepath.putBytes(dataBAOS);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ProfileActivity.this, "Upload complete", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    });
                }
                break;
            case GALLERY_INTENT:
                if(resultCode == Activity.RESULT_OK) {
                    mProgressDialog.setMessage("Uploading ...");
                    mProgressDialog.show();

                    Uri galleyUri = data.getData();
                    StorageReference filepath = mStorage.child(userObj.userID).child("Profile Picture");
                    filepath.putFile(galleyUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ProfileActivity.this, "Upload done", Toast.LENGTH_LONG).show();
                            mProgressDialog.dismiss();
                        }
                    });
                }
        }
    }
}
