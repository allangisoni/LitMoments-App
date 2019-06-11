package com.example.android.litmoments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kd.dynamic.calendar.generator.ImageGenerator;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;

public class EditJournalEntry extends AppCompatActivity {


    private static final String TAG = AddJournalEntry.class.getName();

    @BindView(R.id.entrytoolbar) Toolbar entryToolbar;
    @BindView(R.id.etJournalMessage) EditText etJournalMessage;
    @BindView(R.id.etJournalLocation) EditText etJournalLocation;
    @BindView(R.id.etJournalTitle) EditText etJournalTitle;
    @BindView(R.id.tvetJournalDate) TextView tvJournalDate;
    @BindView(R.id.photojournal_fab) FloatingActionButton photoJournalFab;
    @BindView(R.id.savejournal_fab) FloatingActionButton saveJournalFab;
    @BindView(R.id.rvetPhotos) RecyclerView rvPhotos;
    @BindView(R.id.spinner_etweather) Spinner spinnerWeather;
    @BindView(R.id.spinner_etmood) Spinner spinnerMood;

    private JournalEntryAdapater journalEntryAdapater;
    private DisplayImagesAdapter retrievedjournalEntryAdapater;
    private List<JournalPhotoModel> photoList = new ArrayList<>();
    private List<DisplayImagesModel> retrievedphotoList = new ArrayList<>();
    final int numberOfColumns = 2;
    private   String journalMonth, journalDay,  currentWeather, currentMood;

    private ArrayList<String> pathList = new ArrayList<>();
    private ArrayList<String> imageUploads = new ArrayList<>();
    private List<File> fileImages = new ArrayList<>();
    private static final int REQUEST_CODE_CHOOSE = 23;
    public static final int REQUEST_CODE_CAMERA = 0012;
    public static final int REQUEST_CODE_GALLERY = 0013;
    private String [] items = {"Camera","Gallery"};
    ImageGenerator mImageGenerator;
    Bitmap mGeneratedDateIcon;
    private Date date;
    DateFormat df;
    Calendar mCurrentDate;

    List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME);

    String apiKey = "AIzaSyCZIBeTApR20-ChOZNsKTYN57IlcYsftEk";
    int myfile=0;


    public static final String STORAGE_PATH_UPLOADS = "uploads/";
    public static final String DATABASE_UPLOADS = "User's Journal Entries";

    private StorageReference storageReference;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    private StorageTask mUploadTask;
    private ArrayList<String> uploadedImages = new ArrayList<>();

    String[] weather = {"Sunny","Cloudy","Rainy"};
    int images[] = {R.drawable.ic_sunny, R.drawable.ic_cloudy, R.drawable.ic_rainy};

    String[] mood = {"Happy","Sad","Surprised"};
    int moodImages[] = {R.drawable.ic_happy, R.drawable.ic_sad, R.drawable.ic_surprised};

    JournalEntryModel journalEntry;

    String refKey;

    CustomAdapter customAdapter;
    CustomMoodAdapter custommoodAdapter;

    Boolean urlExists =true, isNewImage=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journal_entry);
        ButterKnife.bind(this);

        if (entryToolbar != null) {
            setSupportActionBar(entryToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }
        FirebaseApp.initializeApp(getApplicationContext());


        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        String currentUid = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference(DATABASE_UPLOADS).child(currentUid);


        photoList.clear();
        fileImages.clear();


        // width and height will be at least 600px long (optional).
        EasyImage.configuration(this).setImagesFolderName("Journal Images").setAllowMultiplePickInGallery(true);

      /**  journalEntryAdapater = new JournalEntryAdapater(photoList, getApplicationContext(), new JournalEntryAdapater.OnItemClickListener() {
            @Override
            public void onItemClick(JournalPhotoModel photoItem) {

            }
        });
    **/

        retrievedjournalEntryAdapater = new DisplayImagesAdapter(retrievedphotoList, getApplicationContext(), new DisplayImagesAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(DisplayImagesModel photoItem) {

            }
        });

        Bundle extras = getIntent().getExtras();
        journalEntry = extras.getParcelable("journalEntry");


        if(journalEntry != null){
            setJournal();
        }

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, numberOfColumns);
        rvPhotos.setLayoutManager(mLayoutManager);
        rvPhotos.setItemAnimator(new DefaultItemAnimator());

        photoJournalFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // onPickImage(v);
                openImage();

            }
        });

        saveJournalFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadJournal();
            }
        });


        spinnerWeather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, "You Select Position: "+position+" "+fruits[position], Toast.LENGTH_SHORT).show();
                currentWeather = weather[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        customAdapter=new CustomAdapter(getApplicationContext(),images,weather);
        spinnerWeather.setAdapter(customAdapter);


        spinnerMood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, "You Select Position: "+position+" "+fruits[position], Toast.LENGTH_SHORT).show();
                currentMood = mood[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        custommoodAdapter=new CustomMoodAdapter(getApplicationContext(),moodImages,mood);
        spinnerMood.setAdapter(custommoodAdapter);



        rvPhotos.setAdapter(retrievedjournalEntryAdapater);


        mImageGenerator = new ImageGenerator(this);

        // Set the icon size to the generated in dip.
        mImageGenerator.setIconSize(80, 80);

        // Set the size of the date and month font in dip.
        mImageGenerator.setDateSize(30);
        mImageGenerator.setMonthSize(20);

        // Set the position of the date and month in dip.
        mImageGenerator.setDatePosition(52);
        mImageGenerator.setMonthPosition(24);

        // Set the color of the font to be generated
        mImageGenerator.setDateColor(Color.parseColor("#3c6eaf"));
        mImageGenerator.setMonthColor(Color.WHITE);
        String defaultDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String defaultMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
        String defaultDay = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
        if(Integer.parseInt(defaultDay) <=9) {
            defaultDay = defaultDay.replace("0", " ");
        } else {
            defaultDay = defaultDay;
        }
       // defaultDay = defaultDay.replace("0"," ");
        //tvJournalDate.setText(defaultDate);
        journalDay =defaultDay;
        journalMonth = defaultMonth;

        df = new SimpleDateFormat("EEE, d MMM yyyy");

        tvJournalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCurrentDate = Calendar.getInstance();
                int year = mCurrentDate.get(Calendar.YEAR);
                int month = mCurrentDate.get(Calendar.MONTH);
                int day = mCurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(EditJournalEntry.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        mCurrentDate.set(selectedYear, selectedMonth, selectedDay);
                        selectedMonth = selectedMonth + 1;
                        journalDay = Integer.toString(selectedDay);
                        journalMonth = Integer.toString(selectedMonth);
                        String selectedDate = Integer.toString(selectedYear) + "-" + Integer.toString(selectedMonth) + "-" + Integer.toString(selectedDay);
                        df = new SimpleDateFormat("EEE, d MMM yyyy");
                        //String formatedDate = df.format("2018-06-09");
                        tvJournalDate.setText(selectedDate);

                    }


                }, year, month, day);
                mDatePicker.show();


                date = Calendar.getInstance().getTime();
                String formattedDate = df.format(date);

            }
        });




    }


    public  void  openImage(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(items[i].equals("Camera")){
                    EasyImage.openCameraForImage(EditJournalEntry.this, REQUEST_CODE_CAMERA);

                }else {

                    EasyImage.openGallery(EditJournalEntry.this, REQUEST_CODE_GALLERY);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {



            @Override
            public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {

                switch (type){
                    case REQUEST_CODE_CAMERA:


                        for(File file:imageFiles){
                            JournalPhotoModel journalImage = new JournalPhotoModel();
                            DisplayImagesModel displayImage = new DisplayImagesModel();
                            journalImage.setJournalImage(file);
                            displayImage.setJournalImageView(file.getAbsolutePath());
                            retrievedphotoList.add(displayImage);
                            photoList.add(journalImage);
                            fileImages.add(file);
                            Toast.makeText(EditJournalEntry.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                            if(photoList != null){
                                myfile=1;
                                rvPhotos.setVisibility(View.VISIBLE);
                            }
                        }
                        //journalEntryAdapater.notifyDataSetChanged();
                        retrievedjournalEntryAdapater.notifyDataSetChanged();




                        /**
                         Glide.with(AddJournalActivity.this)
                         .load(imageFile)
                         .asBitmap()
                         .centerCrop()
                         .diskCacheStrategy(DiskCacheStrategy.ALL);
                         .into();
                         tvPath.setText(imageFile.getAbsolutePath());
                         **/

                        break;
                    case REQUEST_CODE_GALLERY:
                        for(File file:imageFiles){
                            JournalPhotoModel journalImage = new JournalPhotoModel();
                            DisplayImagesModel displayImage = new DisplayImagesModel();
                            journalImage.setJournalImage(file);
                            photoList.add(journalImage);
                            fileImages.add(file);
                            displayImage.setJournalImageView(file.getAbsolutePath());
                            retrievedphotoList.add(displayImage);
                            // Toast.makeText(AddJournalEntry.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                            if(photoList != null){
                               myfile=1;
                                rvPhotos.setVisibility(View.VISIBLE);
                                Toast.makeText(EditJournalEntry.this, " "+ photoList.get(0) + " ", Toast.LENGTH_LONG).show();
                                //Toast.makeText(EditJournalEntry.this, photoList.size(), Toast.LENGTH_SHORT).show();

                            }

                        }
                        //journalEntryAdapater.notifyDataSetChanged();
                        retrievedjournalEntryAdapater.notifyDataSetChanged();


                        break;
                }
            }


            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(EditJournalEntry.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }




    public void uploadJournal() {

        if (TextUtils.isEmpty(etJournalTitle.getText().toString()))
        {
            Toast.makeText(EditJournalEntry.this, "Journal title is empty", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(etJournalMessage.getText().toString() ))
        {
            Toast.makeText(EditJournalEntry.this, "Journal message is empty", Toast.LENGTH_SHORT).show();
        }

        else
        {


            if (photoList.size() != 0) {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Saving Data");
                progressDialog.show();
                //adding the file to reference
                DatabaseReference databaseReference = mDatabase;
               // String refKey = databaseReference.push().getKey();
                //databaseReference.child(refKey);
                ObjectMapper oMapper = new ObjectMapper();

                HashMap<String, Object> myFilePath = new HashMap<String, Object>();

               /** for(String imageinUploads : imageUploads ){
                    StorageReference photoRef = storageReference.child(STORAGE_PATH_UPLOADS).child(imageinUploads);
                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                            Log.d(TAG, "onSuccess: deleted file");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                            Log.d(TAG, "onFailure: did not delete file");
                        }
                    });
                } **/


                for ( int count =0; count < photoList.size(); count++ ) {
                    int imagecount =count;


                    File myFile = fileImages.get(count);
                    StorageReference imageRef = storageReference.child(STORAGE_PATH_UPLOADS);

                   // Toast.makeText(getApplicationContext(), " "+ fileImages.get(0).getAbsolutePath() +" ", Toast.LENGTH_LONG).show();
                    Log.i("fileImage", fileImages.get(count).getAbsolutePath());
                    String[] subfilename = fileImages.get(count).getAbsolutePath().split("%");
                    String fsname = " ";
                    String imageName = " ";
                    if(subfilename.length >1) {
                        fsname = subfilename[1];
                        imageName = fsname;
                        imageName = imageName.substring(2,20);
                        isNewImage = false;
                    } else{
                        fsname = subfilename[0];
                        imageName = fsname;
                        isNewImage =true;

                    }

                    Log.i("imageurl", imageName );

                    if(imageUploads.size() >0 & isNewImage == false){

                       // Toast.makeText(getApplicationContext(), " "+ imageUploads.get(0) +" ", Toast.LENGTH_LONG).show();

                      //  String imageName = imageUploads.get(count);

                        imageRef.child(imageName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'users/me/profile.png'
                                //Toast.makeText(getApplicationContext(), " " + imageUploads.size()+ " ", Toast.LENGTH_SHORT).show();
                                String downloadUrl = uri.toString();
                                uploadedImages.add(downloadUrl);
                                JournalEntryModel journalEntryModel = new JournalEntryModel(tvJournalDate.getText().toString(), etJournalLocation.getText().toString(), currentWeather,
                                        currentMood, etJournalTitle.getText().toString(), etJournalMessage.getText().toString(), journalMonth, journalDay,uploadedImages,
                                        refKey);

                                Map<String, Object> productValues = oMapper.convertValue(journalEntryModel, Map.class);

                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put(refKey, productValues);
                                //  childUpdates.put("/user-products/" + "userId" + "/" + refKey, productValues);

                                databaseReference.updateChildren(childUpdates);

                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exceptionn) {

                                urlExists =false;
                                // File not found

                                //progressDialog.dismiss();
                                //Toast.makeText(getApplicationContext(), " Not Updated", Toast.LENGTH_SHORT).show();
                            }
                        });


                        if(urlExists == false ){
                      StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(Uri.fromFile(fileImages.get(count))));
                      mUploadTask = sRef.putFile(Uri.fromFile(fileImages.get(count)));


                      mUploadTask.addOnFailureListener(exception -> Log.i("It didn't work", "double check"))
                              .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                  @Override
                                  public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                      sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                          @Override
                                          public void onSuccess(Uri uri) {

                                              Uri downloadUrl = uri;
                                              uploadedImages.add(downloadUrl.toString());

                                              JournalEntryModel journalEntryModel = new JournalEntryModel(tvJournalDate.getText().toString(), etJournalLocation.getText().toString(), currentWeather,
                                                      currentMood, etJournalTitle.getText().toString(), etJournalMessage.getText().toString(), journalMonth, journalDay,
                                                      uploadedImages, refKey);

                                              Map<String, Object> productValues = oMapper.convertValue(journalEntryModel, Map.class);

                                              Map<String, Object> childUpdates = new HashMap<>();
                                              childUpdates.put(refKey, productValues);
                                              //  childUpdates.put("/user-products/" + "userId" + "/" + refKey, productValues);

                                              databaseReference.updateChildren(childUpdates);

                                              progressDialog.dismiss();
                                              Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                                              urlExists = true;


                                          }
                                      });

                                  }
                              });

                      if(isNewImage == true){

                          StorageReference secRef = storageReference.child(STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(Uri.fromFile(fileImages.get(count))));
                          mUploadTask = secRef.putFile(Uri.fromFile(fileImages.get(count)));

                          mUploadTask.addOnFailureListener(exception -> Log.i("It didn't work", "double check"))
                                  .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                      @Override
                                      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                          secRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                              @Override
                                              public void onSuccess(Uri uri) {

                                                  Uri downloadUrl = uri;
                                                  uploadedImages.add(downloadUrl.toString());

                                                  JournalEntryModel journalEntryModel = new JournalEntryModel(tvJournalDate.getText().toString(), etJournalLocation.getText().toString(), currentWeather,
                                                          currentMood, etJournalTitle.getText().toString(), etJournalMessage.getText().toString(), journalMonth, journalDay,
                                                          uploadedImages, refKey);

                                                  Map<String, Object> productValues = oMapper.convertValue(journalEntryModel, Map.class);

                                                  Map<String, Object> childUpdates = new HashMap<>();
                                                  childUpdates.put(refKey, productValues);
                                                  //  childUpdates.put("/user-products/" + "userId" + "/" + refKey, productValues);

                                                  databaseReference.updateChildren(childUpdates);

                                                  progressDialog.dismiss();
                                                  Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                                                  urlExists = true;
                                                  isNewImage = false;


                                              }
                                          });

                                      }
                                  });


                      }


                }
                    }
                else{


                        StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(Uri.fromFile(fileImages.get(count))));
                        mUploadTask = sRef.putFile(Uri.fromFile(fileImages.get(count)));

                        mUploadTask.addOnFailureListener(exception -> Log.i("It didn't work", "double check"))
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                Uri downloadUrl = uri;
                                                uploadedImages.add(downloadUrl.toString());

                                                JournalEntryModel journalEntryModel = new JournalEntryModel(tvJournalDate.getText().toString(), etJournalLocation.getText().toString(), currentWeather,
                                                        currentMood, etJournalTitle.getText().toString(), etJournalMessage.getText().toString(), journalMonth, journalDay,
                                                        uploadedImages, refKey);

                                                Map<String, Object> productValues = oMapper.convertValue(journalEntryModel, Map.class);

                                                Map<String, Object> childUpdates = new HashMap<>();
                                                childUpdates.put(refKey, productValues);
                                                //  childUpdates.put("/user-products/" + "userId" + "/" + refKey, productValues);

                                                databaseReference.updateChildren(childUpdates);

                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                                                urlExists = true;


                                            }
                                        });

                                    }
                                });

                }

                }

                pathList.clear();
            }


            else {
                //HashMap<String, String> myFilePath = new HashMap<String, String>();
                JournalEntryModel journalEntryModel = new JournalEntryModel(tvJournalDate.getText().toString(), etJournalLocation.getText().toString(),currentWeather ,
                        currentMood, etJournalTitle.getText().toString(), etJournalMessage.getText().toString(), journalMonth, journalDay,
                        null, refKey);

                //adding an upload to firebase database
                DatabaseReference databaseReference = mDatabase;
                databaseReference.child(refKey).setValue(journalEntryModel);
                //displaying success toast
                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
            }

        }
    }

     public void setJournal(){

        tvJournalDate.setText(journalEntry.getJournalDate());
        etJournalLocation.setText(journalEntry.getJournalLocation());
        etJournalTitle.setText(journalEntry.getJournalTitle());
        etJournalMessage.setText(journalEntry.getJournalMessage());

         if(journalEntry.getJournalWeather().equalsIgnoreCase("Sunny")){
            //spinnerWeather.setSelection(0);
             spinnerWeather.post(new Runnable() {
                 @Override
                 public void run() {
                     spinnerWeather.setSelection(0);
                     currentWeather=  "Sunny";

                 }
             });
         } else if(journalEntry.getJournalWeather().equalsIgnoreCase("Cloudy")){
             spinnerWeather.post(new Runnable() {
                 @Override
                 public void run() {
                     spinnerWeather.setSelection(1);
                     currentWeather=  "Cloudy";
                 }
             });
         }else if(journalEntry.getJournalWeather().equalsIgnoreCase("Rainy")){
             spinnerWeather.post(new Runnable() {
                 @Override
                 public void run() {
                     spinnerWeather.setSelection(2);
                     currentWeather=  "Rainy";
                 }
             });
         } else{

         }

         if(journalEntry.getJournalMood().equalsIgnoreCase("Happy")){
             spinnerWeather.post(new Runnable() {
                 @Override
                 public void run() {
                     spinnerMood.setSelection(0);
                     currentMood = "Happy";
                 }
             });
         } else if(journalEntry.getJournalMood().equalsIgnoreCase("Sad")){
             spinnerWeather.post(new Runnable() {
                 @Override
                 public void run() {
                     spinnerMood.setSelection(1);
                     currentMood = "Sad";

                 }
             });

         }else if(journalEntry.getJournalMood().equalsIgnoreCase("Surprised")){

             spinnerWeather.post(new Runnable() {
                 @Override
                 public void run() {
                     spinnerMood.setSelection(2);
                     currentMood = "Surprised";
                 }
             });

         } else{

         }

        if(journalEntry.getJournalImagePath().size() != 0){
            rvPhotos.setVisibility(View.VISIBLE);
            myfile=0;

            List<String> imageList = new ArrayList<>();
            imageList.addAll(journalEntry.getJournalImagePath());
            for (String imagePath : imageList){
                DisplayImagesModel journalImage = new DisplayImagesModel();
                JournalPhotoModel photojournalImage = new JournalPhotoModel();
                 File file = new File(imagePath);
                 fileImages.add(file);
                photojournalImage.setJournalImage(file);
                journalImage.setJournalImageView(imagePath);
                retrievedphotoList.add(journalImage);
                photoList.add(photojournalImage);
                imageUploads.add(imagePath);


            }

            retrievedjournalEntryAdapater.notifyDataSetChanged();
        }

        refKey = journalEntry.getKey();


     }


    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.add_journal_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item_delete:

                return true;

            default:

                return super.onOptionsItemSelected(item);
        }
    }



}
