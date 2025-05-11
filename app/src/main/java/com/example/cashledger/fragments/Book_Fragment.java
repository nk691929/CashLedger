package com.example.cashledger.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.cashledger.R;
import com.example.cashledger.adapter.BookAdapter;
import com.example.cashledger.databinding.FragmentBookBinding;
import com.example.cashledger.modelClasses.Book;
import com.example.cashledger.modelClasses.Business;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Book_Fragment extends Fragment {

    private int booksCount=0;
    private RecyclerView bookRecView;
    private Button addCustomer;
    private SearchView searchCustomer;
    private ArrayList<Book> bookList;
    private BookAdapter adapter;
    private DatabaseReference reference;
    private Business business;
    private FragmentBookBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {;
        business=(Business) getArguments().getSerializable("Business");
        binding=FragmentBookBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            initViews();
            fetchBooks();
            settingRecView();
            addBookLayout();
            searchBook();
        }catch (Exception ignored){}
    }


    //Initialize views
    private void initViews()
    {
        bookList=new ArrayList<>();
        bookRecView=getView().findViewById(R.id.book_rec_view);
        addCustomer=getView().findViewById(R.id.add_book);
        searchCustomer=getView().findViewById(R.id.searchBook);
        bookRecView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    //Fetching data from firebase
    private void fetchBooks()
    {
        String userKey=FirebaseAuth.getInstance().getUid();
        reference=FirebaseDatabase.getInstance().getReference("users").child(userKey)
                .child(business.getId()).child("books");
        reference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (bookList.size() == 0) {
                    for (DataSnapshot ss : snapshot.getChildren()) {
                        Book customer = ss.getValue(Book.class);
                        bookList.add(customer);
                        booksCount=booksCount+1;
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error : "+ error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //setting RecyclerView
    private void settingRecView()
    {
        adapter=new BookAdapter(getContext(),bookList,business,booksCount);
        bookRecView.setAdapter(adapter);
    }

    //adding Customers
    private void addBookLayout()
    {
        addCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog customDialog = new Dialog(getActivity());
                customDialog.setContentView(R.layout.add_book_layout);
                EditText bookName;
                Button addCustomerBtn = customDialog.findViewById(R.id.book_add_btn);
                bookName = customDialog.findViewById(R.id.book_name_dialog);
                onClickOnAddButton(addCustomerBtn, customDialog, bookName);
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialog.show();

            }
        });
    }

    //Adding customer in firebase
    private void onClickOnAddButton(Button buttonAdd,Dialog dialogBuild,EditText bookName)
    {
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = bookName.getText().toString();
                if(TextUtils.isEmpty(name)) {
                    bookName.setError("Required");
                } else{
                    String userKey=FirebaseAuth.getInstance().getUid();
                    reference=FirebaseDatabase.getInstance().getReference("users");
                    reference=reference.child(userKey).child(business.getId()).child("books");
                    String bookId=reference.push().getKey();
                    reference=reference.child(bookId);
                    Book book = new Book();
                    book.setBookID(bookId);
                    book.setBookName(name);
                    booksCount=booksCount+1;
                    bookList.add(book);
                    business.setBooks(booksCount);
                    updateBusinessBooks();
                    adapter.setBooks(booksCount);
                    adapter.notifyDataSetChanged();
                    reference.setValue(book).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Book Added Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Book Not Added", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialogBuild.dismiss();
                }
            }
        });
    }


    //Search Customer
    private void searchBook()
    {
        searchCustomer.setIconifiedByDefault(false);
        searchCustomer.setQueryHint("Search Ledger");
        searchCustomer.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    //Perform Filtering
    private void filter(String text) {
        ArrayList<Book> filteredlist = new ArrayList<Book>();

        for (Book item : bookList) {
            if (item.getBookName().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
        } else {
            adapter.filterList(filteredlist);
        }
    }


    private void updateBusinessBooks(){
        String userId=FirebaseAuth.getInstance().getUid();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("users").child(userId)
                .child("business").child(business.getId());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().setValue(business);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void moreBtnClick(){
        binding.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu=new PopupMenu(getContext(),view);
                menu.getMenuInflater().inflate(R.menu.book_frag_menu,menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(
                                getContext(),
                                "You Clicked : " + item.getTitle(),
                                Toast.LENGTH_SHORT
                        ).show();
                        return true;
                    }
                });
            }
        });
    }
}
