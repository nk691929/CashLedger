package com.example.cashledger.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cashledger.Activities.CustomerActivity;
import com.example.cashledger.Activities.SelectionActivity;
import com.example.cashledger.R;
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

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    private int books;
    private Context context;
    private ArrayList<Book> bookList;
    private Business business,moveToBusiness;
    private ArrayList<Business> businessList;
    private  int loc=-1;

    public BookAdapter(Context context, ArrayList<Book> bookList,Business business,int Books) {
        this.context = context;
        this.bookList = bookList;
        this.business=business;
        books=Books;
        businessList=new ArrayList<>();
    }

    public void setBooks(int books) {
        this.books = books;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_rec_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;
        holder.bookName.setText(bookList.get(position).getBookName());
        holder.bookUpdateTime.setText(bookList.get(position).getBookUpdatedTime());
        holder.bookOverAllAmount.setText(bookList.get(position).getBookOverAllAMount());
        holder.bookMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu menu = new PopupMenu(context, view);
                menu.inflate(R.menu.book_menu);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.edit_book:
                                editBook(pos);
                                return true;
                            case R.id.delete_book:
                                deleteBook(pos);
                                return true;
                            case R.id.duplicate_book:
                                duplicateBook(pos);
                                return true;
                            case R.id.move_book:
                                moveBook(pos);
                                return true;
                            case R.id.add_member:
                                addMember();
                                return true;
                        }
                        return false;
                    }
                });
                menu.show();
            }
        });

        holder.itemHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CustomerActivity.class);
                intent.putExtra("Book", bookList.get(pos));
                intent.putExtra("Business", business);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView bookName, bookUpdateTime, bookOverAllAmount;
        private ImageView bookMoreButton;
        private LinearLayout itemHolder;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookName = itemView.findViewById(R.id.book_name);
            bookUpdateTime = itemView.findViewById(R.id.book_update_time);
            bookOverAllAmount = itemView.findViewById(R.id.book_overAll_amount);
            bookMoreButton = itemView.findViewById(R.id.book_more);
            itemHolder = itemView.findViewById(R.id.book_holder);
        }
    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<Book> filterlist) {
        bookList = filterlist;
        notifyDataSetChanged();
    }

    //Edit book Name
    private void editBook(int pos) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.add_book_layout);
        EditText name = dialog.findViewById(R.id.book_name_dialog);
        name.setText(bookList.get(pos).getBookName());
        Button add = dialog.findViewById(R.id.book_add_btn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameTxt = String.valueOf(name.getText());
                if (TextUtils.isEmpty(nameTxt)) {
                    name.setError("Required");
                } else {
                    Book newBook = new Book();
                    newBook.setBookID(bookList.get(pos).getBookID());
                    newBook.setBookName(nameTxt);
                    String userId = FirebaseAuth.getInstance().getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users")
                            .child(userId).child(business.getId()).child("books").child(bookList.get(pos).getBookID());
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().setValue(newBook);
                            bookList.remove(pos);
                            bookList.add(pos, newBook);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    //Duplicating book
    private void duplicateBook(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Duplicate Book");
        builder.setMessage("Are You Sure?");
        builder.setIcon(R.drawable.book);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String userKey = FirebaseAuth.getInstance().getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
                reference = reference.child(userKey).child(business.getId()).child("books");
                String bookId = reference.push().getKey();
                reference = reference.child(bookId);
                Book newBook = bookList.get(pos);


                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("users")
                        .child(userKey).child(business.getId()).child(bookList.get(pos).getBookID());

                DatabaseReference refer=FirebaseDatabase.getInstance().getReference("users")
                        .child(userKey).child(business.getId()).child(bookId);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        refer.setValue(snapshot.getValue());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                newBook.setBookID(bookId);
                bookList.add(newBook);
                books=books+1;
                business.setBooks(books);
                notifyDataSetChanged();
                updateBusinessBooks();
                reference.setValue(newBook).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Book Duplicated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Book Not Duplicated", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    //Delete Book
    private void deleteBook(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete");
        builder.setMessage("Are You Sure?");
        builder.setIcon(R.drawable.book);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String userKey = FirebaseAuth.getInstance().getUid();

                //deleting Book
                DatabaseReference bookReference = FirebaseDatabase.getInstance().getReference("users");
                bookReference = bookReference.child(userKey).child(business.getId())
                        .child("books").child(bookList.get(pos).getBookID());
                bookReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().removeValue();
                        bookList.remove(pos);
                        books=books-1;
                        business.setBooks(books);
                        updateBusinessBooks();
                        notifyDataSetChanged();
                        Toast.makeText(context, "Book Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Book Not Deleted", Toast.LENGTH_SHORT).show();
                    }
                });

                //deleting customer and their khata
                DatabaseReference khataReference = FirebaseDatabase.getInstance().getReference("users").child(userKey)
                        .child(business.getId()).
                        child(bookList.get(pos).getBookID());
                khataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                        for (DataSnapshot snap1 : snapshot1.getChildren()) {
                            snap1.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
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


    //MoveBook from business to another
    private void moveBook(int pos){
        Intent intent=new Intent(context, SelectionActivity.class);
        intent.putExtra("Business",business);
        intent.putExtra("Book",bookList.get(pos));
        context.startActivity(intent);
    }

    //adding accessing to another member
    private void addMember(){

    }
}
