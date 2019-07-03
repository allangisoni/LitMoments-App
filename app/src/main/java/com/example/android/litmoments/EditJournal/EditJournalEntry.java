package com.example.android.litmoments.EditJournal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.view.ActionMode;
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

import com.ajts.androidmads.fontutils.FontUtils;
import com.example.android.litmoments.AddJournal.AddJournalEntry;
import com.example.android.litmoments.AddJournal.CustomAdapter;
import com.example.android.litmoments.AddJournal.CustomMoodAdapter;
import com.example.android.litmoments.AddJournal.JournalEntryAdapater;
import com.example.android.litmoments.AddJournal.JournalEntryModel;
import com.example.android.litmoments.AddJournal.JournalEntryViewHolder;
import com.example.android.litmoments.AddJournal.JournalPhotoModel;
import com.example.android.litmoments.R;
import com.example.android.litmoments.Settings.SettingsActivity;
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
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;

public class EditJournalEntry extends AppCompatActivity implements DisplayImagesAdapter.OnClickAction, SharedPreferences.OnSharedPreferenceChangeListener {


    private static final String TAG = EditJournalEntry.class.getName();

    @BindView(R.id.entrytoolbar) Toolbar entryToolbar;
    @BindView(R.id.etJournalMessage) EditText etJournalMessage;
    @BindView(R.id.etJournalLocation) EditText etJournalLocation;
    @BindView(R.id.etJournalTitle) EditText etJournalTitle;
    @BindView(R.id.tvetJournalDate) TextView tvJournalDate;
   // @BindView(R.id.photojournal_fab) FloatingActionButton photoJournalFab;
   // @BindView(R.id.savejournal_fab) FloatingActionButton saveJournalFab;
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

    Boolean urlExists =true, isWhite=false, isNewImage=false;

    File  file;
    public static final String IMAGE_DIRECTORY = "Lit Moments";
    private SimpleDateFormat dateFormatter;

    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_CONTENT = "content";
    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    ActionMode actionMode;


    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_action_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item_delete:
                    // Toast.makeText(AddJournalEntry.this, journalEntryAdapater.getSelected().size() + " selected", Toast.LENGTH_SHORT).show();
                    deleteItemFromList();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences;
        sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);
        loadUiTheme(sharedPreferences);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journal_entry);
        ButterKnife.bind(this);

        if (entryToolbar != null) {
            setSupportActionBar(entryToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setUpThemeContent();
            loadWidgetColors(sharedPreferences);

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

        retrievedjournalEntryAdapater = new DisplayImagesAdapter(retrievedphotoList, getApplicationContext());
        Bundle extras = getIntent().getExtras();
        journalEntry = extras.getParcelable("journalEntry");


        if(journalEntry != null){
            setJournal();
        }

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, numberOfColumns);
        rvPhotos.setLayoutManager(mLayoutManager);
        rvPhotos.setItemAnimator(new DefaultItemAnimator());

     /**   photoJournalFab.setOnClickListener(new View.OnClickListener() {
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
        }); **/

        retrievedjournalEntryAdapater.setActionModeReceiver((DisplayImagesAdapter.OnClickAction) EditJournalEntry.this);

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


        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }




        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);



    }
    public void setUpThemeContent (){
        if(isWhite == true) {
            entryToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            ((TextView)entryToolbar.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.white));
        } else {
            entryToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
            ((TextView)entryToolbar.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    public  void  openImage(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(items[i].equals("Camera")){
                    //

                    if(isWriteStoragePermissionGranted() && isReadStoragePermissionGranted()) {

                        EasyImage.openCameraForImage(EditJournalEntry.this, REQUEST_CODE_CAMERA);
                    }

                }else {
                    if(isReadStoragePermissionGranted() && isWriteStoragePermissionGranted()) {

                        Matisse.from(EditJournalEntry.this)
                                .choose(MimeType.ofImage())
                                .countable(true)
                                .maxSelectable(9)
                                .theme(R.style.Matisse_Dracula)
                                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                                .thumbnailScale(0.85f)
                                .imageEngine(new PicassoEngine())
                                .forResult(REQUEST_CODE_GALLERY);
                    }

                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

 /***   @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
**/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {

            List<Uri> mSelected;
            mSelected = Matisse.obtainResult(data);

            for (Uri uri:mSelected){

                File  file = new File(Environment.getExternalStorageDirectory()
                        + "/" + IMAGE_DIRECTORY);
                if (!file.exists()) {
                    file.mkdirs();
                }

                JournalPhotoModel journalImage = new JournalPhotoModel();
                DisplayImagesModel displayImage = new DisplayImagesModel();
                File uriFile =new File(uri.getPath());
               // journalImage.setJournalImage(uri);
                photoList.add(journalImage);
                fileImages.add(uriFile);
                displayImage.setJournalImageView(uri.getPath());
                retrievedphotoList.add(displayImage);

                //imagesUri.add(uri);

                Log.d("Matisse", "selected file: " + uri.getPath());

                File sourceFile = new File(getPathFromGooglePhotosUri(uri));
                Random random = new Random();
                int a = random.nextInt(100);
                File destFile = new File(file, "img_"+a+ dateFormatter.format(new Date()).toString() + ".png");






                Log.d(TAG, "Source File Path :" + sourceFile);
                Log.d(TAG, "Destination File Path :" + destFile);

                try {
                    copyFile(sourceFile, destFile);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }

                destFile.delete();

            }
            if(retrievedphotoList.size() != 0){

                rvPhotos.setVisibility(View.VISIBLE);
                Log.d("Matisse", "mSelected: " + photoList.size());
            }

            retrievedjournalEntryAdapater.notifyDataSetChanged();


        } else{


            EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {

                @Override
                public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {

                    if(type == REQUEST_CODE_CAMERA){


                    for(File file:imageFiles){
                        JournalPhotoModel journalImage = new JournalPhotoModel();
                        DisplayImagesModel displayImage = new DisplayImagesModel();
                        //   journalImage.setJournalImage(file);
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


            if (fileImages.size() != 0) {
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


                for ( int count =0; count < fileImages.size(); count++ ) {
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

                    if(imageUploads.size() >0 && isNewImage == false){

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
              //  photojournalImage.setJournalImage(file);
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


/**
 *
 * Request permissions
 *
 * **/

    public  boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted1");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            return true;
        }
    }

    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            return true;
        }
    }





    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.add_journal, menu);

        MenuItem menuItem = menu.findItem(R.id.item_save);
        MenuItem menuPhotoItem = menu.findItem(R.id.item_image);

        if (menuItem != null) {
            tintMenuIcon(EditJournalEntry.this, menuItem);
        }
        if (menuPhotoItem!= null) {
            tintMenuIcon(EditJournalEntry.this, menuPhotoItem);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item_save:
                uploadJournal();
                return true;
            case R.id.item_image:
                openImage();
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }

    public void selectAll(View v) {
        retrievedjournalEntryAdapater.selectAll();

        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
            actionMode.setTitle("Selected: " + retrievedjournalEntryAdapater.getSelected().size());
        }
    }

    public void deselectAll(View v) {
        retrievedjournalEntryAdapater.clearSelected();
        if (actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }
    }

    public void onClickAction() {
        int selected = retrievedjournalEntryAdapater.getSelected().size();
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
            actionMode.setTitle("Selected: " + selected);
        } else {
            if (selected == 0) {
                actionMode.finish();
            } else {
                actionMode.setTitle("Selected: " + selected);
            }
        }
    }

    // confirmation dialog box to delete an unit
    private void deleteItemFromList() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Delete selected items ?")
                .setCancelable(false)
                .setPositiveButton("CONFIRM",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                List<DisplayImagesModel> selected = new ArrayList<>();
                                selected = retrievedjournalEntryAdapater.getSelected();
                                for (DisplayImagesModel journalPhotoModel : selected) {
                                    retrievedphotoList.remove(journalPhotoModel);

                                    for(int i=0; i<fileImages.size(); i++){
                                        File file = fileImages.get(i);
                                        String filename = file.getAbsolutePath().substring(1);
                                        filename = insertString(filename,"/",6);

                                        if(filename.equals( journalPhotoModel.getJournalImageView())){
                                           // Log.d("journalPath", journalPhotoModel.getJournalImageView());

                                            StorageReference imageRef = storageReference.child(STORAGE_PATH_UPLOADS);
                                            String[] subfilename = fileImages.get(i).getAbsolutePath().split("%");
                                            String fsname = " ";
                                            String imageName = " ";
                                            if(subfilename.length >1) {
                                                fsname = subfilename[1];
                                                imageName = fsname;
                                                imageName = imageName.substring(2,20);
                                                Log.d("imageName",imageName);
                                            } else{
                                                fsname = subfilename[0];
                                                imageName = fsname;
                                                Log.d("imageNameF",imageName);

                                            }
                                                       String deleteImage = imageName;
                                                        imageRef.child(imageName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("Image",deleteImage + "has been deleted successfully");
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("Image",deleteImage + " deletion failed");
                                                            }
                                                        });

                                            fileImages.remove(i);

                                        } else{
                                            Log.d("FilePath",filename);
                                            //Log.d("journalPath", journalPhotoModel.getJournalImageView());

                                        }
                                    }
                                }
                                retrievedjournalEntryAdapater.clearSelected();
                                retrievedjournalEntryAdapater.notifyDataSetChanged();


                            }
                        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        List<DisplayImagesModel> selected = new ArrayList<>();
                        selected = retrievedjournalEntryAdapater.getSelected();
                        for(int i=0; i< selected.size(); i++) {
                            retrievedjournalEntryAdapater.unhighlightViewOnCancel((DisplayImagesViewHolder) rvPhotos.findViewHolderForAdapterPosition(i));
                            retrievedjournalEntryAdapater.clearSelected();
                        }
                    }
                });

        builder.show();

    }

    // Function to insert string
    public static String insertString(String originalString, String stringToBeInserted, int index)
    {

        // Create a new string
        String newString = new String();

        for (int i = 0; i < originalString.length(); i++) {

            // Insert the original string character
            // into the new string
            newString += originalString.charAt(i);

            if (i == index) {

                // Insert the string to be inserted
                // into the new string
                newString += stringToBeInserted;
            }
        }

        // return the modified String
        return newString;
    }


    public String getPathFromGooglePhotosUri(Uri uriPhoto) {
        if (uriPhoto == null)
            return null;

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uriPhoto, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);

            String tempFilename = getTempFilename(this);
            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return tempFilename;
        } catch (IOException ignored) {
            // Nothing we can do
        } finally {
            closeSilently(input);
            closeSilently(output);
        }
        return null;
    }

    public static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    private static String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("image", "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        File imgcompressed = sourceFile;
        try {
            File compressedImageFile = new Compressor(this).compressToFile(sourceFile);
            imgcompressed=compressedImageFile;
        } catch (IOException e) {
            e.printStackTrace();
        }

        source = new FileInputStream(imgcompressed).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }

        if (source.size() != 0) {
            source.close();
        }
        if (destination.size() != 0) {
            destination.close();
        }


    }

    public  void loadWidgetColors(SharedPreferences sharedPreferences){
        String selectedFont = sharedPreferences.getString(getString(R.string.key_uiThemeFont), "0");
        if(selectedFont.equals("0")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.parisienneregular);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
        } else if(selectedFont.equals("1")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.patrick_hand_sc);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
        } else if( selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.sofadi_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
        } else {

        }
    }
    private void loadUiTheme(SharedPreferences sharedPreferences){

        String userTheme = sharedPreferences.getString(getString(R.string.key_uiTheme), "2");
        if (userTheme.equals("2")) {
            setTheme(R.style.LitStyle);
            isWhite = false;
        }
        else if (userTheme.equals("1")) {
            setTheme(R.style.ReddishLitStyle);
            isWhite = false;
        }
        else if (userTheme.equals("0")) {
            setTheme(R.style.BlueLitStyle);
            isWhite = false;
        } else{

        }
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //prefMethods.changeListenerTheme(sharedPreferences, this);
        if(key.equals(getResources().getString(R.string.key_uiTheme))) {
            loadUiTheme(sharedPreferences);
            EditJournalEntry.this.recreate();
        } else if(key.equals(getResources().getString(R.string.key_uiThemeFont))){
            loadWidgetColors(sharedPreferences);
            EditJournalEntry.this.recreate();
        }
    }

    public  void tintMenuIcon(Context context, MenuItem item) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        if(isWhite) {
            DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(android.R.color.white));
            item.setIcon(wrapDrawable);
        } else if (!isWhite){
            DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(R.color.colorAccent));
            item.setIcon(wrapDrawable);
        } else {

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

    }



}
