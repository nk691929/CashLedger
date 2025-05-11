package com.example.cashledger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cashledger.Activities.BusinessActivity1;
import com.example.cashledger.Activities.BusinessActivity2;
import com.example.cashledger.Activities.Login_Activity;
import com.example.cashledger.adapter.BusinessAdapter;
import com.example.cashledger.fragments.Book_Fragment;
import com.example.cashledger.fragments.account_frag;
import com.example.cashledger.modelClasses.Book;
import com.example.cashledger.modelClasses.Business;
import com.example.cashledger.modelClasses.OnSwipeListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout holder;
    private FirebaseAuth auth;
    private RelativeLayout switchBusiness;
    private TextView businessNameTv;
    private Business business;
    private ArrayList<Business> businessList;
    private Bundle bundle;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String BUSINESS_ID = "businessId";
    private String businessID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //checkUserLoggedInOrNot();
        setContentView(R.layout.activity_main);
        try {
            setFromSplash();

            businessList = new ArrayList<>();
            fetchBusinessList();


            //permission check
            String[] permission = {"android.permission.POST_NOTIFICATIONS",
                    "android.permission.MANAGE_EXTERNAL_STORAGE",
                    "android.permission.ACCESS_MEDIA_LOCATION",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.READ_CONTACTS"
            };
            checkPermission(permission[0], 11);
            checkPermission(permission[1], 12);
            checkPermission(permission[2], 13);
            checkPermission(permission[3], 14);
            checkPermission(permission[4], 15);
            checkPermission(permission[5], 16);

            if(!isNetworkAvailable()){
                Toast.makeText(this, "Network Not Available", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ignored){

        }

    }

    //setting bar
    private void setBar() {
        View customView = getLayoutInflater().inflate(R.layout.toolbar, null);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
        switchBusiness = customView.findViewById(R.id.switch_Business_layout_toll);
        businessNameTv = customView.findViewById(R.id.business_name_bar);
        businessNameTv.setText(business.getName());
        businessSwitchListener();
        getSupportActionBar().setCustomView(customView);
    }

    private void googleSignIn() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            startActivity(new Intent(getApplicationContext(), Login_Activity.class));
        }
    }


    void loadFragment(Fragment fragment, boolean flag) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        //transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_righ);
        if (flag) {
            transaction.add(R.id.container, fragment);
        } else {
            transaction.replace(R.id.container, fragment);
        }
        transaction.commit();
    }

    private void frameTouchListener() {
        holder.setOnTouchListener(new OnSwipeListener(MainActivity.this) {
            public void onSwipeTop() {
            }

            public void onSwipeRight() {
                bottomNavigationView.setSelectedItemId(R.id.Khata);
            }

            public void onSwipeLeft() {
                bottomNavigationView.setSelectedItemId(R.id.Account);
            }

            public void onSwipeBottom() {
            }

        });
    }

    //change Business
    private void businessSwitchListener() {
        switchBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBotSheetDialog(businessList);
            }
        });
    }


    //Bottom Layout Listener
    private void setBottomNavigationView() {
        try {
            bottomNavigationView = findViewById(R.id.bottomNavigationView);
            holder = findViewById(R.id.container);
            frameTouchListener();

            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.Khata:
                            Book_Fragment book_fragment = new Book_Fragment();
                            bundle = new Bundle();
                            bundle.putSerializable("Business", business);
                            book_fragment.setArguments(bundle);
                            loadFragment(book_fragment, false);
                            break;
                        case R.id.Account:
                            account_frag account_frag1 = new account_frag();
                            bundle = new Bundle();
                            bundle.putSerializable("Business", business);
                            account_frag1.setArguments(bundle);
                            loadFragment(account_frag1, false);
                            break;
                    }
                    return true;
                }
            });

            bottomNavigationView.setSelectedItemId(R.id.Khata);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function to check and request permission
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }


    //fetch Business
    private void fetchBusinessList() {
        String userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                .child("business");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Business bs = snap.getValue(Business.class);
                    businessList.add(bs);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setFromSplash() {
        business = (Business) getIntent().getSerializableExtra("Business");
        if(business.getId().equals("")){
            setBar();
            setBottomNavigationView();
            ArrayList<Business> tempList=new ArrayList<>();
            tempList=(ArrayList<Business>) getIntent().getSerializableExtra("BusinessList");
            setBotSheetDialog(tempList);
        }else{
            setBar();
            setBottomNavigationView();
        }
    }


    //bottomSheet Dialog
    private void setBotSheetDialog(ArrayList<Business> list) {
        BottomSheetDialog dialog = new BottomSheetDialog(MainActivity.this);
        dialog.setContentView(R.layout.switch_business_bottom_layout);
        if(business.getId().equals("")) {
            dialog.setCancelable(false);
        }
        else {
            dialog.setCancelable(true);
        }


        //cancel dialog
        LinearLayout cancelLayout = dialog.findViewById(R.id.cancel_layout);
        cancelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(business.getId().equals("")) {
                    dialog.setCancelable(false);
                    Toast.makeText(MainActivity.this, "Please Select A Business", Toast.LENGTH_SHORT).show();
                }
                else {
                    dialog.dismiss();
                }
            }
        });


        //setting Rec View
        String ref="switch";
        RecyclerView businessRecView = dialog.findViewById(R.id.business_rec_view);
        businessRecView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        BusinessAdapter adapter = new BusinessAdapter(MainActivity.this, list, business,ref);
        businessRecView.setAdapter(adapter);


        //new Business
        LinearLayout addNewBusiness = dialog.findViewById(R.id.add_new_business);
        addNewBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BusinessActivity1.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    //check network
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}