package com.example.android.litmoments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kd.dynamic.calendar.generator.ImageGenerator;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
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

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class AddJournalEntry extends AppCompatActivity {

    private static final String TAG = AddJournalEntry.class.getName();
    @BindView(R.id.entrytoolbar) Toolbar entryToolbar;
    @BindView(R.id.etJournalMessage) EditText etJournalMessage;
    @BindView(R.id.etJournalLocation) EditText etJournalLocation;
   // @BindView(R.id.etJournalWeather) EditText etJournalWeather;
    //@BindView(R.id.etJournalMood) EditText etJournalMood;
    @BindView(R.id.etJournalTitle) EditText etJournalTitle;
    @BindView(R.id.tvJournalDate) TextView tvJournalDate;
    @BindView(R.id.photojournal_fab) FloatingActionButton photoJournalFab;
    @BindView(R.id.savejournal_fab) FloatingActionButton saveJournalFab;
    @BindView(R.id.rvPhotos) RecyclerView rvPhotos;
    @BindView(R.id.spinner_weather) Spinner spinnerWeather;
    @BindView(R.id.spinner_mood) Spinner spinnerMood;
    private JournalEntryAdapater journalEntryAdapater;
    private List<JournalPhotoModel> photoList = new ArrayList<>();
    final int numberOfColumns = 2;
    private   String journalMonth, journalDay;

    private ArrayList<String> pathList = new ArrayList<>();
    private List<File> fileImages = new ArrayList<>();
    private static final int REQUEST_CODE_CHOOSE = 23;
    public static final int REQUEST_CODE_CAMERA = 0012;
    public static final int REQUEST_CODE_GALLERY = 0013;
    private String [] items = {"Camera","Gallery"};
    Calendar mCurrentDate;
    ImageGenerator mImageGenerator;
    Bitmap mGeneratedDateIcon;
    private Date date;
    DateFormat df;

    List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME);

    String apiKey = "AIzaSyCZIBeTApR20-ChOZNsKTYN57IlcYsftEk";


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




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal_entry);
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

        journalEntryAdapater = new JournalEntryAdapater(photoList, getApplicationContext(), new JournalEntryAdapater.OnItemClickListener() {
            @Override
            public void onItemClick(JournalPhotoModel photoItem) {

            }
        });


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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CustomAdapter customAdapter=new CustomAdapter(getApplicationContext(),images,weather);
        spinnerWeather.setAdapter(customAdapter);


        spinnerMood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, "You Select Position: "+position+" "+fruits[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CustomMoodAdapter custommoodAdapter=new CustomMoodAdapter(getApplicationContext(),moodImages,mood);
        spinnerMood.setAdapter(custommoodAdapter);



        rvPhotos.setAdapter(journalEntryAdapater);


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
        defaultDay = defaultDay.replace("0"," ");
        tvJournalDate.setText(defaultDate);
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

                DatePickerDialog mDatePicker = new DatePickerDialog(AddJournalEntry.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        mCurrentDate.set(selectedYear, selectedMonth, selectedDay);
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


        getUserLocation();

    }


/**
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
        //pathList.add()

        // TODO do something with the bitmap
    }

    public void onPickImage(View view) {
        // Click on image button
        ImagePicker.pickImage(this, "Select your image:");
    } **/



public  void  openImage(){

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Pick Image From");
    builder.setItems(items, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int i) {
            if(items[i].equals("Camera")){
                EasyImage.openCameraForImage(AddJournalEntry.this, REQUEST_CODE_CAMERA);

            }else {

                EasyImage.openGallery(AddJournalEntry.this, REQUEST_CODE_GALLERY);
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
                            journalImage.setJournalImage(file);
                            photoList.add(journalImage);
                            fileImages.add(file);
                            Toast.makeText(AddJournalEntry.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                            if(photoList != null){

                                rvPhotos.setVisibility(View.VISIBLE);
                            }
                        }
                        journalEntryAdapater.notifyDataSetChanged();




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
                            journalImage.setJournalImage(file);
                            photoList.add(journalImage);
                            fileImages.add(file);
                           // Toast.makeText(AddJournalEntry.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                            if(photoList != null){

                                rvPhotos.setVisibility(View.VISIBLE);
                                Toast.makeText(AddJournalEntry.this, photoList.size(), Toast.LENGTH_SHORT).show();

                            }

                        }
                        journalEntryAdapater.notifyDataSetChanged();


                        /** Glide.with(AddJournalActivity.this)
                         .load(imageFile)
                         .centerCrop()
                         .diskCacheStrategy(DiskCacheStrategy.ALL)
                         .into();
                         //tvPath.setText(imageFile.getAbsolutePath());

                         **/
                        break;
                }
            }


            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA_IMAGE) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(AddJournalEntry.this);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }


    public void getUserLocation(){

        Places.initialize(getApplicationContext(), apiKey);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FindCurrentPlaceResponse response = task.getResult();

                    List<String> myUserLocation = new ArrayList<>();

                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                        myUserLocation.add(placeLikelihood.getPlace().getName());
                    }

                    etJournalLocation.setText(myUserLocation.get(0));

                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            getLocationPermission();
        }


    }

   public void getLocationPermission() {


       if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

           ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
       }

      getUserLocation();

   }


   public void uploadJournal() {

       if (TextUtils.isEmpty(etJournalTitle.getText().toString()))
       {
           Toast.makeText(AddJournalEntry.this, "Journal title is empty", Toast.LENGTH_SHORT).show();
       }

       else if (TextUtils.isEmpty(etJournalMessage.getText().toString() ))
       {
           Toast.makeText(AddJournalEntry.this, "Journal message is empty", Toast.LENGTH_SHORT).show();
       }

       else
           {
           // Toast.makeText(AddJournalEntry.this, " " +  photoList.size() + " ", Toast.LENGTH_SHORT).show();

               if (photoList.size() != 0) {

                   //Toast.makeText(AddJournalEntry.this, " " +  photoList.size() + " ", Toast.LENGTH_SHORT).show();

               //getting the storage reference
                 // StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(Uri.fromFile(fileImages.get(0))));

                   final ProgressDialog progressDialog = new ProgressDialog(this);
                   progressDialog.setTitle("Saving Data");
                   progressDialog.show();
                  //adding the file to reference
                   DatabaseReference databaseReference = mDatabase;
                   String refKey = databaseReference.push().getKey();
                   //databaseReference.child(refKey);
                   ObjectMapper oMapper = new ObjectMapper();


                 //  databaseReference.setValue(journalEntryModel);
                   HashMap<String, Object> myFilePath = new HashMap<String, Object>();
                       for ( int count =0; count < photoList.size(); count++ ) {

                        File myFile = fileImages.get(count);
                          StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(Uri.fromFile(fileImages.get(count))));
                          mUploadTask =  sRef.putFile(Uri.fromFile(fileImages.get(count)));


                          mUploadTask.addOnFailureListener(exception -> Log.i("It didn't work", "double check"))
                                  .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                              @Override
                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                  sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                      @Override
                                      public void onSuccess(Uri uri) {

                                          Uri downloadUrl = uri;
                                          uploadedImages.add(downloadUrl.toString());

                                          JournalEntryModel journalEntryModel = new JournalEntryModel(tvJournalDate.getText().toString(), etJournalLocation.getText().toString(), "",
                                                  " ", etJournalTitle.getText().toString(), etJournalMessage.getText().toString(), journalMonth, journalDay,
                                                  uploadedImages);

                                          Map<String, Object> productValues = oMapper.convertValue(journalEntryModel, Map.class);

                                          Map<String, Object> childUpdates = new HashMap<>();
                                          childUpdates.put(refKey, productValues);
                                          //  childUpdates.put("/user-products/" + "userId" + "/" + refKey, productValues);

                                          databaseReference.updateChildren(childUpdates);

                                          progressDialog.dismiss();
                                      }
                                  });



                              }
                          });

                         /** mUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                              @Override
                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                  progressDialog.setTitle("Saving Data");
                                  progressDialog.show();
                                  sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                      @Override
                                      public void onSuccess(Uri uri) {
                                          final String downloadUri = uri.toString();
                                          Toast.makeText(AddJournalEntry.this, downloadUri, Toast.LENGTH_LONG).show();
                                          pathList.add(downloadUri);

                                          myFilePath.put(downloadUri, downloadUri);

                                          JournalEntryModel journalEntryModel = new JournalEntryModel(tvJournalDate.getText().toString(), etJournalLocation.getText().toString(), "",
                                                  " ", etJournalTitle.getText().toString(), etJournalMessage.getText().toString(), journalMonth, journalDay,
                                                  pathList);
                                          databaseReference.setValue(journalEntryModel);
                                         // databaseReference.child("JournalImagePath").setValue(downloadUri);

                                        //  JournalEntryModel journalEntryModel = new JournalEntryModel(tvJournalDate.getText().toString(), etJournalLocation.getText().toString(), etJournalWeather.getText().toString(),
                                          //        etJournalMood.getText().toString(), etJournalTitle.getText().toString(), etJournalMessage.getText().toString(),
                                            //      pathList);



                                          progressDialog.dismiss();

                                          //displaying success toast
                                          Toast.makeText(getApplicationContext(), "Data Saved Successfully", Toast.LENGTH_LONG).show();
                                      }
                                  });


                                  //  imagePaths.put(sRef.getDownloadUrl().toString(), sRef.getDownloadUrl().toString()) ;
                                  // pathList.add(sRef.getDownloadUrl().toString());


                              }
                             }).addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception exception) {
                                          progressDialog.dismiss();
                                          Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                                      }
                                  }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                      @Override
                                      public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                          //displaying the upload progress
                                          double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                          progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                                      }
                                  }); **/


                      }




                      //dismissing the progress dialog

                   pathList.clear();
                  // fileImages.clear();
                  }


             else {
               //HashMap<String, String> myFilePath = new HashMap<String, String>();
               JournalEntryModel journalEntryModel = new JournalEntryModel(tvJournalDate.getText().toString(), etJournalLocation.getText().toString(), " ",
                   " ", etJournalTitle.getText().toString(), etJournalMessage.getText().toString(), journalMonth, journalDay,
                   null);

           //adding an upload to firebase database
           DatabaseReference databaseReference = mDatabase.push();
           databaseReference.setValue(journalEntryModel);
           //displaying success toast
           Toast.makeText(getApplicationContext(), "Data Saved Successfully with No Image", Toast.LENGTH_LONG).show();
           }

       }
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
