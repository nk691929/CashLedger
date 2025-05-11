package com.example.cashledger.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cashledger.R;
import com.example.cashledger.adapter.KhataRecordAdapter;
import com.example.cashledger.databinding.ActivityKhataBinding;
import com.example.cashledger.modelClasses.Amount;
import com.example.cashledger.modelClasses.Book;
import com.example.cashledger.modelClasses.Business;
import com.example.cashledger.modelClasses.Customer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class KhataActivity extends AppCompatActivity {

    private com.example.cashledger.databinding.ActivityKhataBinding binding;
    private Business business;
    private Customer customer;
    private Book book;
    private ArrayList<Amount> amountList;
    private KhataRecordAdapter adapter;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private ArrayList<Amount> reverseAmountList;
    private String userId;
    private Uri imageUri;
    private ImageView billImg;
    private TextView billImgTxt;
    private RelativeLayout switchBusiness;
    private TextView businessNameTv, businessSwitchTv;
    private ImageView businessNameImg,businessNameImg2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKhataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setBar();
        try {
            initViews();
            getIntentExtras();
            gettingFirebaseReference();
            fetchCustomerKhata();
            allBtnClickListener();
            setMoreBtn();
            settingRecViewAdapter();
            setCustomerDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //getIntent
    private void getIntentExtras() {
        Intent intent = getIntent();
        customer = (Customer) intent.getSerializableExtra("Customer");
        book = (Book) intent.getSerializableExtra("Book");
        business = (Business) intent.getSerializableExtra("Business");
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
        businessSwitchTv = customView.findViewById(R.id.toolbar_switch_tv);
        businessNameImg = customView.findViewById(R.id.toolbar_icon);
        businessNameImg2 = customView.findViewById(R.id.switch_Bussiness);
        businessNameImg.setImageResource(R.drawable.aounts_flat_icon);
        businessNameImg2.setVisibility(View.GONE);
        businessSwitchTv.setVisibility(View.GONE);
        businessNameTv.setText("Amount Records");
        getSupportActionBar().setCustomView(customView);
    }


    //Initializing views
    private void initViews() {
        customer = new Customer();
        amountList = new ArrayList<>();

        //RecyclerView
        binding.khataRecView.setLayoutManager(new GridLayoutManager(this, 1));
    }


    //Getting Reference
    private void gettingFirebaseReference() {
        //Firebase db Access
        auth = FirebaseAuth.getInstance();
        userId = auth.getUid();
    }


    //Setting adapter
    public void settingRecViewAdapter() {
        adapter = new KhataRecordAdapter(KhataActivity.this, amountList, business, book, customer);
        binding.khataRecView.setAdapter(adapter);
    }


    //set Customer Name details
    private void setCustomerDetails() {
        binding.customerLayoutName.setText(customer.getCustomerName());
        long takenAmount = calculateTakenAmount();
        long givenAmount = calculateGivenAmount();
        if (takenAmount > givenAmount) {
            long resAmount = takenAmount - givenAmount;
            String resText = "OverAll " + resAmount + " Given to " + customer.getCustomerName();
            binding.overAllAmount.setBackgroundResource(R.drawable.liye_res);
            binding.overAllAmount.setTextColor(Color.parseColor("#EDEFF0"));
            binding.overAllAmount.setText(resText);
            binding.overAllAmount.setSelected(true);
        } else if (givenAmount > takenAmount) {
            long resAmount = givenAmount - takenAmount;
            String res = "OverAll " + resAmount + " Taken from " + customer.getCustomerName();
            binding.overAllAmount.setBackgroundResource(R.drawable.diye_res);
            binding.overAllAmount.setTextColor(Color.parseColor("#EDEFF0"));
            binding.overAllAmount.setText(res);
            binding.overAllAmount.setSelected(true);
        } else {
            String res = "Clear with " + customer.getCustomerName();
            binding.overAllAmount.setBackgroundResource(R.drawable.search_res);
            binding.overAllAmount.setText(res);
            binding.overAllAmount.setTextColor(Color.parseColor("#3F51B5"));
            binding.overAllAmount.setSelected(true);
        }
    }


    //Btn Click Listeners
    private void allBtnClickListener() {
        diyeBtnClick();
        liyeBtnClick();
        binding.customerLayoutPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(amountList.size()>0) {
                        String directory=createPdfInvoice();
                        viewPdf(directory);
                    }
                    else {
                        Toast.makeText(KhataActivity.this, "No Record Exist", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(KhataActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
            }
        });
        binding.customerLayoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(amountList.size()>0) {
                        String directory=createPdfInvoice();
                        sharePdf(directory);
                    }
                    else {
                        Toast.makeText(KhataActivity.this, "No Record Exist", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(KhataActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
            }
        });
    }


    //Adding amount in Khata Table
    private void diyeBtnClick() {
        binding.customerDiyeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog customDialog = new Dialog(KhataActivity.this);
                customDialog.setContentView(R.layout.get_amount_dialog);
                TextView getAmountTv = customDialog.findViewById(R.id.get_amount_tv);
                TextView getDetailTv = customDialog.findViewById(R.id.get_amount_details);
                getAmountTv.setHint("Input Given Amount?");
                Button saveBtn = customDialog.findViewById(R.id.get_amount_btn);

                //gettImage
                LinearLayout addBillImageLayout = customDialog.findViewById(R.id.add_image_bill);
                billImg = customDialog.findViewById(R.id.bill_image);
                billImgTxt = customDialog.findViewById(R.id.bill_txt);
                addBillImageLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getBillImage();
                    }
                });
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String amount = getAmountTv.getText().toString();
                        String detail = getDetailTv.getText().toString();
                        if (amount.equals("")) {
                            getAmountTv.setError("required");
                        } else {
                            saveGivenAmount(amount, detail);
                            customDialog.dismiss();
                        }
                    }
                });
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialog.show();
            }
        });
    }


    //Adding amount in Khata Table
    private void liyeBtnClick() {
        binding.customerLiyeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog customDialog = new Dialog(KhataActivity.this);
                customDialog.setContentView(R.layout.get_amount_dialog);
                TextView getAmountTv = customDialog.findViewById(R.id.get_amount_tv);
                TextView getDetailTv = customDialog.findViewById(R.id.get_amount_details);
                getAmountTv.setHint("Input Taken Amount?");
                Button saveBtn = customDialog.findViewById(R.id.get_amount_btn);

                //gettImage
                LinearLayout addBillImageLayout = customDialog.findViewById(R.id.add_image_bill);
                billImg = customDialog.findViewById(R.id.bill_image);
                billImgTxt = customDialog.findViewById(R.id.bill_txt);
                addBillImageLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getBillImage();
                    }
                });
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String amount = getAmountTv.getText().toString();
                        String detail = getDetailTv.getText().toString();
                        if (amount.equals("")) {
                            getAmountTv.setError("required");
                        } else {
                            saveTakenAmount(amount, detail);
                            customDialog.dismiss();
                        }
                    }
                });
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialog.show();
            }
        });
    }


    //Saving given amount
    private void saveGivenAmount(String amount, String details) {

        reference = FirebaseDatabase.getInstance().getReference("users").child(userId).
                child(business.getId()).
                child(book.getBookID()).child(customer.getCustomerId()).child("khata");
        String currDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(new Date());
        String currTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String amountId = reference.push().getKey();
        Amount am = new Amount();
        am.setAmountId(amountId);
        am.setGivenAmount(Long.parseLong(amount));
        am.setCustomerId(customer.getCustomerId());
        am.setTakenAmount(Long.parseLong("0"));
        am.setDate(currDate);
        am.setTime(currTime);
        am.setDetails(details);
        if (imageUri != null) {
            uploadImageToFirebaseStorage(amountId);
        }
        amountList.add(am);
        reference = reference.child(amountId);
        reference.setValue(am);
        setCustomerDetails();
        adapter.notifyDataSetChanged();
    }


    //Saving Taken amount
    private void saveTakenAmount(String amount, String details) {


        reference = FirebaseDatabase.getInstance().getReference("users").child(userId).
                child(business.getId()).
                child(book.getBookID()).child(customer.getCustomerId()).child("khata");
        String currDate = new SimpleDateFormat("dd-MM-yy", Locale.getDefault()).format(new Date());
        String currTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        String amountId = reference.push().getKey();
        Amount am = new Amount();
        am.setAmountId(amountId);
        am.setCustomerId(customer.getCustomerId());
        am.setGivenAmount(Long.parseLong("0"));
        am.setTakenAmount(Long.parseLong(amount));
        am.setDate(currDate);
        am.setTime(currTime);
        am.setDetails(details);
        if (imageUri != null) {
            uploadImageToFirebaseStorage(amountId);
        }
        amountList.add(am);
        reference = reference.child(amountId);
        reference.setValue(am);
        setCustomerDetails();
        adapter.notifyDataSetChanged();
    }


    //Fetching khata of customer from firebase
    private void fetchCustomerKhata() {
        reference = FirebaseDatabase.getInstance().getReference("users").child(userId).
                child(business.getId())
                .child(book.getBookID()).child(customer.getCustomerId()).child("khata");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (amountList.size() == 0) {
                    for (DataSnapshot ss : snapshot.getChildren()) {
                        Amount amount1 = ss.getValue(Amount.class);
                        amountList.add(amount1);
                    }
                    adapter.notifyDataSetChanged();
                    setCustomerDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Creating More Option ClickListenere
    private void setMoreBtn() {
        binding.customerLayoutMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reverseAmountList = new ArrayList<>();
                reverseList();
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.inflate(R.menu.menu_button_customer_activity);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.sort_by_latest:
                                adapter.setAmounts(reverseAmountList);
                                adapter.notifyDataSetChanged();
                                return true;
                            case R.id.sort_by_earliest:
                                adapter.setAmounts(amountList);
                                adapter.notifyDataSetChanged();
                                return true;
                        }
                        return false;
                    }

                });
                popupMenu.show();
            }
        });
    }


    //Reversing List
    private void reverseList() {
        int size = amountList.size();
        for (int i = size - 1; i > -1; i--) {
            reverseAmountList.add(amountList.get(i));
        }
    }


    //calculate diye amount
    private long calculateGivenAmount() {
        long sum = 0;
        for (Amount amount : amountList) {
            sum = sum + amount.getGivenAmount();
        }
        return sum;
    }


    //calculate diye amount
    private long calculateTakenAmount() {
        long sum = 0;
        for (Amount amount : amountList) {
            sum = sum + amount.getTakenAmount();
        }
        return sum;
    }


    //getting bill from device
    private void getBillImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 10);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && data != null) {
            imageUri = data.getData();
            billImg.setImageURI(imageUri);
            billImgTxt.setText(imageUri.getEncodedPath());
        }
    }


    //uploading Image
    private void uploadImageToFirebaseStorage(String amountId) {
        String userId = FirebaseAuth.getInstance().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("users").child(userId).
                child(business.getId()).
                child(book.getBookID()).child(customer.getCustomerId()).child("khata").child(amountId);
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }


    //pdf invoice creator
    private String createPdfInvoice() throws FileNotFoundException {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
        File file = new File(pdfPath +"/"+customer.getCustomerName()+ "invoice.pdf");
        OutputStream outputStream = new FileOutputStream(file);

        PdfWriter pdfWriter = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document document = new Document(pdfDocument);


        //table
        float columnWidth[] = {140, 140, 140, 140};
        Table table1 = new Table(columnWidth);

        //table 1
        Drawable drawable=getDrawable(R.drawable.book);
        table1.addCell(new Cell(4, 2).add(drawableToImg(drawable)).setBorder(Border.NO_BORDER)
                .add(new Paragraph("\t\tCash Ledger")).setFontColor(new DeviceRgb(63,81,181)).setFontSize(26f));
        //table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell(1, 2).add(new Paragraph("Report")).setBorder(Border.NO_BORDER).setFontSize(26f).setFontColor(new DeviceRgb(63,81,181)));
        //table1.addCell(new Cell().add(new Paragraph("")));


        //table 2
        //table1.addCell(new Cell().add(new Paragraph("")));
        //table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("Customer Name:")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph(customer.getCustomerName())).setBorder(Border.NO_BORDER));


        //table 3
        //table1.addCell(new Cell().add(new Paragraph("")));
        //table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("Customer Phone")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph(customer.getCustomerPhone())).setBorder(Border.NO_BORDER));


        Date date; // your date
// Choose time zone in which you want to interpret your Date
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        //table 3
        //table1.addCell(new Cell().add(new Paragraph("")));
        //table1.addCell(new Cell().add(new Paragraph("")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph("Report Generated on ")).setBorder(Border.NO_BORDER));
        table1.addCell(new Cell().add(new Paragraph(day+"-"+month+"-"+year)).setBorder(Border.NO_BORDER));


        //table2
        float columnWidth2[]={80,180,150,150};
        Table table2=new Table(columnWidth2);

        //table 2.....1
        table2.addCell(new Cell().add(new Paragraph("Time / Date")).setBackgroundColor(new DeviceRgb(63,81,181)).setFontColor(new DeviceRgb(255,255,255)));
        table2.addCell(new Cell().add(new Paragraph("Details")).setBackgroundColor(new DeviceRgb(63,81,181)).setFontColor(new DeviceRgb(255,255,255)));
        table2.addCell(new Cell().add(new Paragraph("Cash Out")).setBackgroundColor(new DeviceRgb(63,81,181)).setFontColor(new DeviceRgb(255,255,255)));
        table2.addCell(new Cell().add(new Paragraph("Cash In")).setBackgroundColor(new DeviceRgb(63,81,181)).setFontColor(new DeviceRgb(255,255,255)));


        //table 2.....1
        int i=1;
        for(Amount amount:amountList) {
            table2.addCell(new Cell().add(new Paragraph(amount.getTime()+"\n"+amount.getDate())));
            table2.addCell(new Cell().add(new Paragraph(amount.getDetails())));
            if(amount.getTakenAmount()==0) {
                table2.addCell(new Cell().add(new Paragraph(amount.getGivenAmount() + "")).setFontColor(new DeviceRgb(63,81,181)));
                table2.addCell(new Cell().add(new Paragraph("")));
            }else{
                table2.addCell(new Cell().add(new Paragraph("")));
                table2.addCell(new Cell().add(new Paragraph(amount.getTakenAmount()+"")).setFontColor(new DeviceRgb(166,58,25)));
            }
            i++;
        }

        table2.addCell(new Cell().add(new Paragraph("")));
        table2.addCell(new Cell().add(new Paragraph("")));
        table2.addCell(new Cell().add(new Paragraph("Total Out : "+calculateGivenAmount())).setFontColor(new DeviceRgb(63,81,181)));
        table2.addCell(new Cell().add(new Paragraph("Total In : "+calculateTakenAmount())).setFontColor(new DeviceRgb(166,58,25)));

        if(calculateTakenAmount()>calculateGivenAmount()) {
            table2.addCell(new Cell().add(new Paragraph("")).setBackgroundColor(new DeviceRgb(63,81,181)));
            table2.addCell(new Cell().add(new Paragraph("")).setBackgroundColor(new DeviceRgb(63,81,181)));
            table2.addCell(new Cell().add(new Paragraph("Final Amount" )).setBackgroundColor(new DeviceRgb(63,81,181)).setFontColor(new DeviceRgb(255,255,255)));
            table2.addCell(new Cell().add(new Paragraph(calculateTakenAmount()-calculateGivenAmount()+" in")).setBackgroundColor(new DeviceRgb(63,81,181)).setFontColor(new DeviceRgb(255,255,255)));
        }else if(calculateGivenAmount()>calculateTakenAmount()) {
            table2.addCell(new Cell().add(new Paragraph("")).setBackgroundColor(new DeviceRgb(63,81,181)));
            table2.addCell(new Cell().add(new Paragraph("")).setBackgroundColor(new DeviceRgb(63,81,181)));
            table2.addCell(new Cell().add(new Paragraph("Final Amount")).setBackgroundColor(new DeviceRgb(63,81,181)).setFontColor(new DeviceRgb(255,255,255)));
            table2.addCell(new Cell().add(new Paragraph(calculateGivenAmount()-calculateTakenAmount()+" out")).setBackgroundColor(new DeviceRgb(63,81,181)).setFontColor(new DeviceRgb(255,255,255)));
        }else {
            table2.addCell(new Cell().add(new Paragraph("")).setBackgroundColor(new DeviceRgb(63,81,181)));
            table2.addCell(new Cell().add(new Paragraph("")).setBackgroundColor(new DeviceRgb(63,81,181)));
            table2.addCell(new Cell().add(new Paragraph("Final Amount")).setBackgroundColor(new DeviceRgb(63,81,181)).setFontColor(new DeviceRgb(255,255,255)));
            table2.addCell(new Cell().add(new Paragraph("0")).setBackgroundColor(new DeviceRgb(63,81,181)).setFontColor(new DeviceRgb(255,255,255)));
        }

        document.add(table1);
        document.add(new Paragraph("\n"));
        document.add(table2);
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("\n"));


        //tab 3
        float columnWidth3[]={280,280};
        Table table3=new Table(columnWidth3);

        document.close();
        Toast.makeText(this, "PdF Created", Toast.LENGTH_SHORT).show();

        return pdfPath+"/"+customer.getCustomerName()+ "invoice.pdf";
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






    // Method for opening a pdf file
    private void viewPdf(String directory) {

        File pdfFile = new File(directory);  // -> filename = manual.pdf

        Uri excelPath;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            excelPath = FileProvider.getUriForFile(this,
                    "com.example.cashledger.provider", pdfFile);
        } else {
            excelPath = Uri.fromFile(pdfFile);
        }
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(excelPath, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try{
            startActivity(pdfIntent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(KhataActivity.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }


    //drawable to image
    private Image drawableToImg(Drawable drawable){
        Bitmap bitmap=((BitmapDrawable)drawable).getBitmap();
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] bitmapData=stream.toByteArray();

        ImageData imageData= ImageDataFactory.create(bitmapData);
        Image image=new Image(imageData);
        image.setHeight(120);
        return image;
    }

    // Method for opening a pdf file
    private void sharePdf(String directory) {

        File pdfFile = new File(directory);  // -> filename = manual.pdf

        Uri excelPath;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            excelPath = FileProvider.getUriForFile(this,
                    "com.example.cashledger.provider", pdfFile);
        } else {
            excelPath = Uri.fromFile(pdfFile);
        }
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, excelPath);

        startActivity(share);
        try{
            startActivity(share);
        }catch(ActivityNotFoundException e){
            Toast.makeText(KhataActivity.this, "No Application to share", Toast.LENGTH_SHORT).show();
        }
    }


}