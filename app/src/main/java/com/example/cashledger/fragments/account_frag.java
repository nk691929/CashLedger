package com.example.cashledger.fragments;

import static android.app.Activity.RESULT_OK;

import android.animation.LayoutTransition;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cashledger.Activities.AmountActivity;
import com.example.cashledger.Activities.BusinessActivity1;
import com.example.cashledger.Activities.BusinessActivity2;
import com.example.cashledger.Activities.Login_Activity;
import com.example.cashledger.Activities.SplashActivity;
import com.example.cashledger.MainActivity;
import com.example.cashledger.R;
import com.example.cashledger.adapter.BusinessAdapter;
import com.example.cashledger.databinding.FragmentAccountFragBinding;
import com.example.cashledger.modelClasses.Business;
import com.example.cashledger.modelClasses.Profile;
import com.google.android.datatransport.BuildConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class account_frag extends Fragment {

    private static final int PICK_IMAGE = 123;
    private LinearLayout logOut;
    private LinearLayout about;
    private LinearLayout share;
    //private LinearLayout appSetting;
    private ImageView edit, dialogProfile;
    private ImageView profilePic;
    private TextView userFullName;
    private ProgressBar progressBar;
    Uri imageUri;
    private RelativeLayout businessProfileExpand;
    private Profile preProfile;
    private Business business;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String BUSINESS_ID = "businessId";
    FragmentAccountFragBinding binding;
    private RelativeLayout businessProfileClick;
    private ArrayList<Business> businessList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fetchProfile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        business = (Business) getArguments().getSerializable("Business");
        binding = FragmentAccountFragBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            initViews();
            fetchProfile();
            fetchProfilePic();
            expandBusinessProfileCard();
            setLogOutListener();
            setAboutListener();
            setShareListener();
            setSettingListener();
            editUserName();
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Exception")
                    .setMessage(e.toString())
                    .show();
        }
    }

    //init views
    void initViews() {
        businessList = new ArrayList<>();
        progressBar = getView().findViewById(R.id.logout_progress_bar);
        progressBar.setVisibility(View.GONE);
        logOut = getView().findViewById(R.id.logout_layout);
        about = getView().findViewById(R.id.about_layout);
        share = getView().findViewById(R.id.share_layout);
        businessProfileExpand = getView().findViewById(R.id.business_profile_expand_view);
        businessProfileClick = getView().findViewById(R.id.business_profile_click);
        //businessProfileClick.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        //appSetting=getView().findViewById(R.id.setting_layout);
        edit = getView().findViewById(R.id.user_edit);
        profilePic = getView().findViewById(R.id.user_pic);
        userFullName = getView().findViewById(R.id.user_name);
    }

    //Logout clickListener
    private void setLogOutListener() {
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Logout");
                builder.setMessage("Dou You Want to Logout?");
                builder.setIcon(R.drawable.book);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        progressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Logout Successfully", Toast.LENGTH_SHORT).show();
                        removeBusinessIdFromSharedPref();
                        Intent intent = new Intent(getContext(), Login_Activity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });
    }

    //Setting About Button Listener
    private void setAboutListener() {
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("About Us");
                builder.setMessage("This App is created by \nNoshad Ahmad");
                builder.setIcon(R.drawable.book);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
        });
    }


    //Setting Share Button Listener
    private void setShareListener() {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Khata Book");
                    String shareMessage = "\nLet me recommend you this application KhataBook\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }
            }
        });
    }


    //Setting Share Button Listener
    private void setSettingListener() {
//        appSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getContext(), "Setting", Toast.LENGTH_SHORT).show();
//            }
//        });
    }


    //Edit User Profile
    void editUserName() {
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.user_profile_dialog);
                ProgressBar bar = dialog.findViewById(R.id.profile_load_bar);
                bar.setVisibility(View.GONE);
                EditText name = dialog.findViewById(R.id.get_user_name);
                name.setText(preProfile.getUserName());
                ImageView editBtnProfile = dialog.findViewById(R.id.pofile_edit);
                dialogProfile = dialog.findViewById(R.id.get_profile_pic);
                Button update = dialog.findViewById(R.id.get_user_btn);

                //Loading Img From Device
                editBtnProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadImageFromDevice();
                    }
                });

                //Updating Profile
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateProfile(name, bar);

                        dialog.dismiss();
                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }


    //loadImageFromDevice
    void loadImageFromDevice() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    ;


    private void fetchProfile() {
        String uId = FirebaseAuth.getInstance().getUid();
        assert uId != null;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(uId).child("profile");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                preProfile = snapshot.getValue(Profile.class);
                userFullName.setText(preProfile.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Updating profile Record
    private void updateProfile(EditText name, ProgressBar bar) {
        if (TextUtils.isEmpty(String.valueOf(name.getText()))) {
            name.setError("Required");
            return;
        }

        userFullName.setText(name.getText().toString());
        bar.setVisibility(View.VISIBLE);
        String uId = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(uId);
        if (imageUri != null) {

            StorageReference storageReference = FirebaseStorage.getInstance().getReference("users").child(uId)
                    .child("ProfilePic");
            storageReference.putFile(imageUri);
            profilePic.setImageURI(imageUri);
        }
        reference = reference.child("profile");
        Profile profile = new Profile();
        profile.setUserName(name.getText().toString());
        reference.setValue(profile);
    }


    //Bitmap to String
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 5, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }


    //String to bitmap
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void removeBusinessIdFromSharedPref() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(BUSINESS_ID).apply();
    }


    //setting business Profile
    private void expandBusinessProfileCard() {
        businessProfileClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int v = ((businessProfileExpand).getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
                TransitionManager.beginDelayedTransition(businessProfileClick, new AutoTransition());
                businessProfileExpand.setVisibility(v);
                setBusinessInformation();
                allEditClickListener();
            }
        });
    }

    //setting business Information
    private void setBusinessInformation() {
        binding.businessNameText.setText("Name : " + business.getName());
        binding.businessEmailText.setText("Email : " + business.getEmail());
        binding.businessPhoneText.setText("Phone : " + business.getPhone());
        binding.businessStaffText.setText("Staff Size : " + business.getStaffSize());
    }

    //All edit Click Listener
    private void allEditClickListener() {
        binding.businessNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog("name", "Enter Name");
            }
        });


        binding.businessEmailEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog("email", "Enter Email");
            }
        });

        binding.businessPhoneEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog("phone", "Enter Phone");
            }
        });

        binding.businessStaffEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog("staff", "Enter Staff Size");
            }
        });


        //delete button
        binding.deleteBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Delete")
                        .setMessage("Are You Sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String userId = FirebaseAuth.getInstance().getUid();

                                DatabaseReference bookReference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                                        .child(business.getId());
                                bookReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        snapshot.getRef().removeValue();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                                        .child("business").child(business.getId());
                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        snapshot.getRef().removeValue();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                Toast.makeText(getContext(), "Successfully Deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getContext(), SplashActivity.class);
                                startActivity(intent);
                            }
                        }).setNegativeButton("No", null)
                        .show();
            }
        });
    }

    //creating a dialog for getting input
    private void createDialog(String type, String setText) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.add_book_layout);
        EditText getText = dialog.findViewById(R.id.book_name_dialog);
        getText.setHint(setText);
        Button addBtn = dialog.findViewById(R.id.book_add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = getText.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    getText.setError("required");
                } else {
                    if (type.equals("name")) {
                        business.setName(text);
                        binding.businessNameText.setText("Name : " + text);
                    } else if (type.equals("email")) {
                        business.setEmail(text);
                        binding.businessEmailText.setText("Email : " + text);
                    } else if (type.equals("phone")) {
                        business.setPhone(text);
                        binding.businessPhoneText.setText("Phone : " + text);
                    } else if (type.equals("staff")) {
                        business.setStaffSize(text);
                        binding.businessStaffText.setText("Staff Size : " + text);
                    }
                    saveBtnClickListener();
                    dialog.dismiss();
                }
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && data != null) {
            imageUri = data.getData();
            dialogProfile.setImageURI(imageUri);
        }
    }


    //fetchProfilePic
    private void fetchProfilePic() {
        String userId = FirebaseAuth.getInstance().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("users").child(userId)
                .child("ProfilePic");
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Glide.with(getContext())
                            .load(uri)
                            .into(profilePic);
                } catch (Exception e) {
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }


    private void saveBtnClickListener() {
        binding.saveBusinessInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = FirebaseAuth.getInstance().getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId)
                        .child("business").child(business.getId());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().setValue(business);
                        Toast.makeText(getContext(), "Business Profile Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), SplashActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}