package com.example.cashledger.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cashledger.R;
import com.example.cashledger.databinding.ActivityAmountBinding;
import com.example.cashledger.modelClasses.Amount;
import com.example.cashledger.modelClasses.Book;
import com.example.cashledger.modelClasses.Business;
import com.example.cashledger.modelClasses.Customer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class AmountActivity extends AppCompatActivity {
    private ScaleGestureDetector scaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ActivityAmountBinding binding;
    private Amount amount;
    private Business business;
    private Book book;
    private Customer customer;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAmountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        binding.noBillTv.setSelected(false);

        //getting Amount from Intent
        Intent intent =getIntent();
        amount=(Amount) intent.getSerializableExtra("Amount");
        business=(Business) intent.getSerializableExtra("Business");
        book=(Book) intent.getSerializableExtra("Book");
        customer=(Customer) intent.getSerializableExtra("Customer");

        //Firebase Storage Ref
        String userId= FirebaseAuth.getInstance().getUid();
        StorageReference storageReference= FirebaseStorage.getInstance().getReference("users").child(userId).
                child(business.getId()).
                child(book.getBookID()).child(customer.getCustomerId()).child("khata").child(amount.getAmountId());
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(AmountActivity.this)
                        .load(uri)
                        .into(binding.image);}
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.noBillTv.setVisibility(View.VISIBLE);
                binding.billTv.setVisibility(View.GONE);
                binding.image.setVisibility(View.GONE);
            }
        });

        //setting Details
        if(amount.getTakenAmount()==0){
            String text="Given amount : "+amount.getGivenAmount();
            binding.amount.setText(text);
            binding.amount.setTextColor(Color.parseColor("#3F51B5"));
        }else if(amount.getGivenAmount()==0)
        {
            String text="Taken amount : "+amount.getTakenAmount();
            binding.amount.setText(text);
            binding.amount.setTextColor(Color.parseColor("#A63A19"));
        }
        binding.details.setText(amount.getDetails());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        //back
        backBtnClicked();
        ScaleListener scaleListener=new ScaleListener();
        saveBtnClicked();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));
            binding.image.setScaleX(mScaleFactor);
            binding.image.setScaleY(mScaleFactor);
            return true;
        }
    }

    //backClicked
    private void backBtnClicked(){
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    //save to gallery
    private void saveBtnClicked(){
        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap=loadBitmapFromView(binding.cardView);
                try {
                    saveBitmap(bitmap);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    //getting image of view
    private Bitmap loadBitmapFromView(View v) {
        askForPermissions();
        Bitmap bitmap;
        v.setDrawingCacheEnabled(true);
        binding.noBillTv.setVisibility(View.GONE);
        bitmap = Bitmap.createBitmap(v.getDrawingCache());
        binding.noBillTv.setVisibility(View.VISIBLE);
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }

    //save to gallery
    private void saveBitmap(Bitmap bmp) throws IOException {
        OutputStream fos;
        try{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                ContentResolver contentResolver=getContentResolver();
                ContentValues values=new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME,"Image"+".jpg");
                values.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg");
                Uri uri=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                fos=contentResolver.openOutputStream(Objects.requireNonNull(uri));
                bmp.compress(Bitmap.CompressFormat.JPEG,100,fos);
                Objects.requireNonNull(fos);
            }
        }catch (Exception e){

        }
    }

    //permission
    public void askForPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        }
    }
}