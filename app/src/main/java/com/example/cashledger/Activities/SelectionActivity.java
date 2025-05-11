package com.example.cashledger.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.cashledger.adapter.BusinessAdapter;
import com.example.cashledger.databinding.ActivitySelectABusinessBinding;
import com.example.cashledger.modelClasses.Book;
import com.example.cashledger.modelClasses.Business;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;

public class SelectionActivity extends AppCompatActivity {
    private int pos=-1;
    private BusinessAdapter adapter;
    private Business business;
    private Book book;
    private ArrayList<Business> businessList;
    private ActivitySelectABusinessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        business=(Business)getIntent().getSerializableExtra("Business");
        book=(Book)getIntent().getSerializableExtra("Book");
        binding=ActivitySelectABusinessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        businessList=new ArrayList<>();
        fetchBusiness();
    }



    //fetching businessList
    private void fetchBusiness() {
        String userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                .child("business");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                        Business bs = snap.getValue(Business.class);
                        if(!bs.getId().equals(business.getId())) {
                            businessList.add(bs);
                        }
                }
                binding.recView.setLayoutManager(new LinearLayoutManager(SelectionActivity.this));
                adapter=new BusinessAdapter(SelectionActivity.this,businessList,business,"move");
                binding.recView.setAdapter(adapter);
                Toast.makeText(SelectionActivity.this, "Select Business to Move Book", Toast.LENGTH_SHORT).show();
                moveBook();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //moving book in selected Busiess
    private void moveBook(){
        binding.moveBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos=adapter.getAdapterPos();
                if(pos==-1){
                    Toast.makeText(SelectionActivity.this, "Please Select Business", Toast.LENGTH_SHORT).show();
                }else{
                    String userId=FirebaseAuth.getInstance().getUid();

                    //Moving bills
                    StorageReference storageReferenceFrom= FirebaseStorage.getInstance().getReference("users").child(userId)
                            .child(business.getId()).child(book.getBookID());

                    StorageReference storageReferenceTo= FirebaseStorage.getInstance().getReference("users").child(userId)
                            .child(businessList.get(pos).getId()).child(book.getBookID());

                    //moveBookRecords
                    DatabaseReference referenceBookElementsFrom=FirebaseDatabase.getInstance().getReference("users").child(userId)
                            .child(business.getId()).child(book.getBookID());

                    DatabaseReference referenceBookElementsTo=FirebaseDatabase.getInstance().getReference("users").child(userId)
                            .child(businessList.get(pos).getId()).child(book.getBookID());

                    referenceBookElementsFrom.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            referenceBookElementsTo.setValue(snapshot.getValue());
                            Toast.makeText(SelectionActivity.this, "Book Moved Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SelectionActivity.this,SplashActivity.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                    //moveBook
                    DatabaseReference referenceBookFrom=FirebaseDatabase.getInstance().getReference("users").child(userId)
                            .child(business.getId()).child("books").child(book.getBookID());

                    DatabaseReference referenceBookTo=FirebaseDatabase.getInstance().getReference("users").child(userId)
                            .child(businessList.get(pos).getId()).child("books").child(book.getBookID());

                    referenceBookFrom.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            referenceBookTo.setValue(snapshot.getValue());
                            Toast.makeText(SelectionActivity.this, "Book Moved Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(SelectionActivity.this,SplashActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

    }
}