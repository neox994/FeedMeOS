package com.ferit.neox.feedmeos;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    EditText etAddTitle, etAddAddress, etAddCity, etAddDescription, etAddPost;
    Bitmap bm1stImg,bm2ndImg;
    Button btn1stImg, btn2ndImg, btnAdd;
    ImageView iv1stImg, iv2ndImg;
    boolean load1st = false, load2nd = false;
    String img1, img2;
    public int loadCheck1 = 1, loadCheck2 = 1;
    final DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        setUpUI();
    }

    private void setUpUI() {
        etAddTitle = findViewById(R.id. etAddTitle);
        etAddAddress = findViewById(R.id. etAddAddress);
        etAddCity = findViewById(R.id. etAddCity);
        etAddDescription = findViewById(R.id. etAddDescription);
        btn1stImg = findViewById(R.id.btn1stImg);
        btn2ndImg = findViewById(R.id.btn2ndImg);
        btnAdd = findViewById(R.id.btnAdd);
        etAddPost = findViewById(R.id.etAddZIP);
        iv1stImg = findViewById(R.id.iv1stImg);
        iv2ndImg = findViewById(R.id.iv2ndImg);

        btn1stImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                load1st = true;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, loadCheck1);

            }
        });

        btn2ndImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load2nd = true;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, loadCheck2);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etAddAddress.getText().toString().equals("") || etAddCity.getText().toString().equals("") || etAddDescription.getText().toString().equals("") || etAddTitle.getText().toString().equals("") || etAddPost.getText().toString().equals(""))
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Nisu sva polja popunjena", Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    Map<String, Object> mapAdv = new HashMap<String, Object>();
                    Map<String, Object> mapBas = new HashMap<String, Object>();
                    String tempKey = root.child("Advanced").push().getKey();

                    mapAdv.put("Adress", etAddAddress.getText().toString().trim());
                    mapAdv.put("City", etAddCity.getText().toString().trim());
                    mapAdv.put("Description", etAddDescription.getText().toString().trim());
                    mapAdv.put("Image", img2);
                    mapAdv.put("AAName", etAddTitle.getText().toString().trim());
                    mapAdv.put("Post", etAddPost.getText().toString().trim());
                    root.child("Advanced").child(tempKey).updateChildren(mapAdv);
                    mapBas.put("Image", img1);
                    mapBas.put("Name", etAddTitle.getText().toString().trim());
                    root.child("Basic").child(tempKey).updateChildren(mapBas);

                    etAddCity.setText("");
                    etAddDescription.setText("");
                    etAddTitle.setText("");
                    etAddAddress.setText("");
                    etAddPost.setText("");
                    iv1stImg.setImageBitmap(null);
                    iv2ndImg.setImageBitmap(null);
                    bm1stImg = null;
                    bm2ndImg = null;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == loadCheck1 && resultCode == RESULT_OK && data != null && load1st) {
            Uri uri = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes;
            bm1stImg = BitmapFactory.decodeFile(filePath);
            bm1stImg.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            bytes = baos.toByteArray();
            img1 = Base64.encodeToString(bytes, Base64.DEFAULT);
            iv1stImg.setImageBitmap(Bitmap.createScaledBitmap(bm1stImg, 50, 50, false));
            load1st = false;
        } else if (requestCode == loadCheck1 && resultCode == RESULT_OK && data != null && load2nd) {
            Uri uri = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes;
            bm2ndImg = BitmapFactory.decodeFile(filePath);
            bm2ndImg.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            bytes = baos.toByteArray();
            img2 = Base64.encodeToString(bytes, Base64.DEFAULT);
            iv2ndImg.setImageBitmap(Bitmap.createScaledBitmap(bm2ndImg, 50, 50, false));
            load2nd = false;
        }
    }
}