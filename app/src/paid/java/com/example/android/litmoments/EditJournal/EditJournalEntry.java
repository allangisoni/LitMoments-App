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
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.fontutils.FontUtils;
import com.example.android.litmoments.AddJournal.CustomAdapter;
import com.example.android.litmoments.AddJournal.CustomMoodAdapter;

import com.example.android.litmoments.Main.MainActivity;

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
import com.lit.litmoments.AddJournal.JournalEntryAdapater;
import com.lit.litmoments.AddJournal.JournalEntryModel;
import com.lit.litmoments.AddJournal.JournalPhotoModel;
import com.lit.litmoments.EditJournal.DisplayImagesAdapter;
import com.lit.litmoments.EditJournal.DisplayImagesModel;
import com.lit.litmoments.EditJournal.DisplayImagesViewHolder;
import com.lit.litmoments.R;
import com.sdsmdg.tastytoast.TastyToast;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    @BindView(R.id.addRlayout) RelativeLayout relativeLayout;

    private JournalEntryAdapater journalEntryAdapater;
    private DisplayImagesAdapter retrievedjournalEntryAdapater;
    private List<JournalPhotoModel> photoList = new ArrayList<>();
     List<DisplayImagesModel> retrievedphotoList = new ArrayList<>();
    final int numberOfColumns = 2;
    private   String journalMonth, journalDay,  currentWeather, currentMood;

    private ArrayList<String> pathList = new ArrayList<>();
    private ArrayList<String> imageUploads = new ArrayList<>();
    ArrayList<File> fileImages = new ArrayList<>();
    private ArrayList<Uri> imagesUri = new ArrayList<>();
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

    String[] weather = {"Sunny","Cloudy","Rainy", "Windy", "Snowy", "Foggy"};
    int images[] = {R.drawable.ic_sunny, R.drawable.ic_cloudy, R.drawable.ic_rainy, R.drawable.ic_windy, R.drawable.ic_snowy, R.drawable.ic_foggy};

    String[] mood = {"Happy","Sad","Surprised", "Dreamy", "Mysterious", "Romantic"};
    int moodImages[] = {R.drawable.ic_happy, R.drawable.ic_sad, R.drawable.ic_surprised, R.drawable.ic_dreamy, R.drawable.ic_mysterious, R.drawable.ic_romantic};

    JournalEntryModel journalEntry;

    String refKey;

    CustomAdapter customAdapter;
    CustomMoodAdapter custommoodAdapter;

    Boolean urlExists =true, isWhite=false, isNewImage=false, isLit= false;

    File  file;
    public static final String IMAGE_DIRECTORY = "Lit Moments";
    private SimpleDateFormat dateFormatter;

    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_CONTENT = "content";
    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    private static final int MY_WRITESTORAGE_REQUEST_CODE = 100;
    private static final int MY_CAMERA_REQUEST_CODE = 102;
    private static final int MY_LOCATION_REQUEST_CODE = 104;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath = " ";
    Uri photoURI;

    Boolean exitact = false;
    Boolean isDarkTheme = false;

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





        // width and height will be at least 600px long (optional).
      //  EasyImage.configuration(this).setImagesFolderName("Journal Images").setAllowMultiplePickInGallery(true);

      /**  journalEntryAdapater = new JournalEntryAdapater(photoList, getApplicationContext(), new JournalEntryAdapater.OnItemClickListener() {
            @Override
            public void onItemClick(JournalPhotoModel photoItem) {

            }
        });
    **/

        retrievedjournalEntryAdapater = new DisplayImagesAdapter(retrievedphotoList, getApplicationContext());
        Bundle extras = getIntent().getExtras();
        journalEntry = extras.getParcelable("journalEntry");


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, numberOfColumns);
        rvPhotos.setLayoutManager(mLayoutManager);
        rvPhotos.setNestedScrollingEnabled(false);
        rvPhotos.setItemAnimator(new DefaultItemAnimator());
       ViewCompat.setNestedScrollingEnabled(rvPhotos, false);

        if(savedInstanceState != null){
            //journalEntry = savedInstanceState.getParcelable("JournalEntry");
            tvJournalDate.setText(savedInstanceState.getString("JournalDate"));
            etJournalLocation.setText(savedInstanceState.getString("JournalLocation"));
            etJournalTitle.setText(savedInstanceState.getString("JournalTitle"));
            etJournalMessage.setText(savedInstanceState.getString("JournalMessage"));
            journalDay = savedInstanceState.getString("JournalDay");
            journalMonth = savedInstanceState.getString("JournalMonth");
            pathList =  savedInstanceState.getStringArrayList("PathList");
            imageUploads = savedInstanceState.getStringArrayList("ImageUploads");
            imagesUri = savedInstanceState.getParcelableArrayList("ImagesUri");
            fileImages =(ArrayList<File>) savedInstanceState.getSerializable("FileImages");
            Log.d("File size" , Integer.toString(fileImages.size()));
            retrievedphotoList = savedInstanceState.getParcelableArrayList("RetrievedPhotoList");
            photoList = savedInstanceState.getParcelableArrayList("PhotoList");
            uploadedImages = savedInstanceState.getStringArrayList("UploadedImages");
          //  getImagePaths();
            retrievedjournalEntryAdapater = new DisplayImagesAdapter(retrievedphotoList, getApplicationContext());

            rvPhotos.setVisibility(View.VISIBLE);
            rvPhotos.setAdapter(retrievedjournalEntryAdapater);
        }
        else {
            uploadedImages.clear();
            photoList.clear();
            fileImages.clear();
            retrievedphotoList.clear();
            if(journalEntry != null){
                setJournal();
            }



            retrievedjournalEntryAdapater = new DisplayImagesAdapter(retrievedphotoList, getApplicationContext());
            rvPhotos.setAdapter(retrievedjournalEntryAdapater);

        }

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



     //   rvPhotos.setAdapter(retrievedjournalEntryAdapater);


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
        //journalDay =journalEntry.getDay();
        //journalMonth = journalEntry.getMonth();

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

        if(Build.VERSION.SDK_INT >= 23) {
            if (isLit == false) {
                etJournalLocation.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.bluecolorAccent)));
            } else if(isLit == true){
                etJournalLocation.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reddishcolorAccent)));
            }
        }


    }
    public void setUpThemeContent (){
        if(isWhite == true) {
            entryToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            ((TextView)entryToolbar.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.white));
            spinnerMood.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            spinnerWeather.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.background));
        } else {
            entryToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
            ((TextView)entryToolbar.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorAccent));
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.background));
        }

        if(isWhite == true && isDarkTheme== true) {
            entryToolbar.setTitleTextColor(getResources().getColor(R.color.blackthemeAccent));
            ((TextView)entryToolbar.getChildAt(0)).setTextColor(getResources().getColor(R.color.blackthemeAccent));
            relativeLayout.setBackground(getResources().getDrawable(R.drawable.maincoordinatorlayoutscrim));
            spinnerMood.getBackground().setColorFilter(getResources().getColor(R.color.blackthemeAccent), PorterDuff.Mode.SRC_ATOP);
            spinnerWeather.getBackground().setColorFilter(getResources().getColor(R.color.blackthemeAccent), PorterDuff.Mode.SRC_ATOP);
            tvJournalDate.setHintTextColor(getResources().getColor(R.color.blackthemeAccent));
            etJournalLocation.setHintTextColor(getResources().getColor(R.color.blackthemeAccent));
            etJournalTitle.setHintTextColor(getResources().getColor(R.color.blackthemeAccent));
            etJournalMessage.setHintTextColor(getResources().getColor(R.color.blackthemeAccent));
            tvJournalDate.setTextColor(getResources().getColor(R.color.cardviewtitle));
            etJournalLocation.setTextColor(getResources().getColor(R.color.cardviewtitle));
            etJournalTitle.setTextColor(getResources().getColor(R.color.cardviewtitle));
            etJournalMessage.setTextColor(getResources().getColor(R.color.cardviewtitle));

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

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditJournalEntry.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_REQUEST_CODE);

                    } else {
                   //     EasyImage.openCameraForImage(EditJournalEntry.this, REQUEST_CODE_CAMERA);
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the Fil
                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                photoURI  = FileProvider.getUriForFile(getApplicationContext(),
                                        "com.example.android.litmoments.fileprovider",
                                        photoFile);
                                Log.d("photouri", photoURI.getPath());
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }
                        }
                    }
                }else {

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditJournalEntry.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITESTORAGE_REQUEST_CODE);

                    } else {
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


 private File createImageFile() throws IOException {
     // Create an image file name
     String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
     String imageFileName = "JPEG_" + timeStamp + "_";
     File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
     File image = File.createTempFile(
             imageFileName,  /* prefix */
             ".jpg",         /* suffix */
             storageDir      /* directory */
     );

     // Save a file: path for use with ACTION_VIEW intents
     currentPhotoPath = image.getAbsolutePath();
     return image;
 }

 @Override
 public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
     super.onRequestPermissionsResult(requestCode, permissions, grantResults);

     if (requestCode == MY_WRITESTORAGE_REQUEST_CODE) {
         if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

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

         } else {

         }
     } else if (requestCode == MY_CAMERA_REQUEST_CODE) {
         if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          //   EasyImage.openCameraForImage(EditJournalEntry.this, REQUEST_CODE_CAMERA);
             Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
             if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                 // Create the File where the photo should go
                 File photoFile = null;
                 try {
                     photoFile = createImageFile();
                 } catch (IOException ex) {
                     // Error occurred while creating the Fil
                 }
                 // Continue only if the File was successfully created
                 if (photoFile != null) {
                     photoURI  = FileProvider.getUriForFile(getApplicationContext(),
                             "com.example.android.litmoments.fileprovider",
                             photoFile);
                     Log.d("photouri", photoURI.getPath());
                     takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                     startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                 }
             }
         } else {

         }
     }

 }

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
                File uriFile =new File(getPathFromGooglePhotosUri(uri));
               // journalImage.setJournalImage(uri);
                photoList.add(journalImage);
                fileImages.add(uriFile);
                displayImage.setJournalImageView(uri.toString());
                retrievedphotoList.add(displayImage);

                imagesUri.add(uri);

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

            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                //  Bundle extras = data.getExtras();
                Uri myuri = photoURI;
                JournalPhotoModel journalImage = new JournalPhotoModel();
                // journalImage.setJournalImage(myuri);
                DisplayImagesModel displayImage = new DisplayImagesModel();
                displayImage.setJournalImageView(myuri.toString());
                photoList.add(journalImage);
                retrievedphotoList.add(displayImage);
                fileImages.add(new File(myuri.toString()));

                imagesUri.add(myuri);
                // Toast.makeText(AddJournalEntry.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, " uri from file" + myuri);

            }

            if(photoList.size() != 0){
                myfile=1;
                rvPhotos.setVisibility(View.VISIBLE);
            }
            retrievedjournalEntryAdapater.notifyDataSetChanged();



            /** EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {

                 @Override
                 public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {

                     if(type == REQUEST_CODE_CAMERA){


                     for(File file:imageFiles){
                         JournalPhotoModel journalImage = new JournalPhotoModel();
                         DisplayImagesModel displayImage = new DisplayImagesModel();
                         //   journalImage.setJournalImage(file);
                         displayImage.setJournalImageView(Uri.fromFile(file).toString());
                         retrievedphotoList.add(displayImage);
                         photoList.add(journalImage);
                         fileImages.add(file);
                        // Toast.makeText(EditJournalEntry.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

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
 **/
        }
    }




    public void uploadJournal() {

        if (TextUtils.isEmpty(etJournalTitle.getText().toString()))
        {
            Toast.makeText(EditJournalEntry.this, "Journal title is empty", Toast.LENGTH_SHORT).show();
            TastyToast.makeText(getApplicationContext(), "Journal title is empty", TastyToast.LENGTH_SHORT, TastyToast.INFO);
        }

        else if (TextUtils.isEmpty(etJournalMessage.getText().toString() ))
        {
            //Toast.makeText(EditJournalEntry.this, "Journal message is empty", Toast.LENGTH_SHORT).show();
            TastyToast.makeText(getApplicationContext(), "Journal message is empty", TastyToast.LENGTH_SHORT, TastyToast.INFO);
        }

        else
        {



            if (fileImages.size() != 0) {

                if (isOnline()) {

                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Saving Data");
                progressDialog.show();
                //adding the file to reference
                DatabaseReference databaseReference = mDatabase;
                // String refKey = databaseReference.push().getKey();
                //databaseReference.child(refKey);
                ObjectMapper oMapper = new ObjectMapper();

                HashMap<String, Object> myFilePath = new HashMap<String, Object>();

                 int i =0;

                for (int count = 0; count < fileImages.size(); count++) {  //changed
                    int imagecount = count;
                    i = count;
                   if( count == fileImages.size()-1){
                      exitact = true;
                   }

                    File myFile = fileImages.get(count);
                    StorageReference imageRef = storageReference.child(STORAGE_PATH_UPLOADS);

                    // Toast.makeText(getApplicationContext(), " "+ fileImages.get(0).getAbsolutePath() +" ", Toast.LENGTH_LONG).show();
                    Log.i("fileImage", fileImages.get(count).getAbsolutePath());
                   // Log.i("fileImageuri", imagesUri.get(count).toString());
                    String[] subfilename = fileImages.get(count).getAbsolutePath().split("%");

                    String fsname = " ";
                    String imageName = " ";
                    if (subfilename.length > 1) {
                        fsname = subfilename[1];
                        imageName = fsname;
                        imageName = imageName.substring(2, 20);
                        isNewImage = false;
                    } else {
                        fsname = subfilename[0];
                        imageName = fsname;
                        isNewImage = true;

                    }

                    Log.i("imageurl", imageName);

                    if (imageUploads.size() > 0 && isNewImage == false) {

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
                                        currentMood, etJournalTitle.getText().toString(), etJournalMessage.getText().toString(), journalMonth, journalDay, uploadedImages,
                                        refKey);

                                Map<String, Object> productValues = oMapper.convertValue(journalEntryModel, Map.class);

                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put(refKey, productValues);
                                //  childUpdates.put("/user-products/" + "userId" + "/" + refKey, productValues);

                                databaseReference.updateChildren(childUpdates);

                                progressDialog.dismiss();
                                //Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                               // TastyToast.makeText(getApplicationContext(), "Updated", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                if(exitact == true){
                                    TastyToast.makeText(getApplicationContext(), "Updated", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                    pathList.clear();
                                    startActivity(new Intent(EditJournalEntry.this, MainActivity.class));
                                    finish();
                                }



                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exceptionn) {

                                urlExists = false;
                                // File not found

                                //progressDialog.dismiss();
                                //Toast.makeText(getApplicationContext(), " Not Updated", Toast.LENGTH_SHORT).show();
                            }
                        });


                        if (urlExists == false) {

                            File sourceFile = new File(getPathFromGooglePhotosUri(Uri.fromFile(fileImages.get(count))));
                            Random random = new Random();
                            int a = random.nextInt(100);
                            File destFile = new File(file, "img_"+a+ dateFormatter.format(new Date()).toString() + ".png");

                            try {
                                copyFile(sourceFile, destFile);

                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {

                            }
                            StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(Uri.fromFile(fileImages.get(count))));

                            //mUploadTask = sRef.putFile(Uri.fromFile(fileImages.get(count)));
                            mUploadTask = sRef.putFile(Uri.fromFile(destFile));

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
                                                  //  Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                                                   // TastyToast.makeText(getApplicationContext(), "Updated", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                                    urlExists = true;

                                                    if(exitact == true){
                                                        TastyToast.makeText(getApplicationContext(), "Updated", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                                        pathList.clear();
                                                        startActivity(new Intent(EditJournalEntry.this, MainActivity.class));
                                                        finish();
                                                    }



                                                }
                                            });

                                        }
                                    });

                            if (isNewImage == true) {

                                File newsourceFile = new File(getPathFromGooglePhotosUri(Uri.fromFile(fileImages.get(count))));
                                Random newrandom = new Random();
                                int b = newrandom.nextInt(100);
                                File newdestFile = new File(file, "img_"+b+ dateFormatter.format(new Date()) + ".png");

                                try {
                                    copyFile(newsourceFile, newdestFile);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {

                                }

                                StorageReference secRef = storageReference.child(STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(Uri.fromFile(fileImages.get(count))));
                               // mUploadTask = secRef.putFile(Uri.fromFile(fileImages.get(count)));
                                mUploadTask = sRef.putFile(Uri.fromFile(newdestFile));

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
                                                       // Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                                                      //  TastyToast.makeText(getApplicationContext(), "Updated", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                                        urlExists = true;
                                                        isNewImage = false;

                                                        if(exitact == true){
                                                            TastyToast.makeText(getApplicationContext(), "Updated", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                                            pathList.clear();
                                                            startActivity(new Intent(EditJournalEntry.this, MainActivity.class));
                                                            finish();
                                                        }


                                                    }
                                                });

                                            }
                                        });


                            }


                        }
                    } else {


                            File sourceFile = new File(getPathFromGooglePhotosUri(Uri.fromFile(fileImages.get(count))));
                            Random random = new Random();
                            int a = random.nextInt(100);
                            File destFile = new File(file, "img_" + a + dateFormatter.format(new Date()).toString() + ".png");

                            try {
                                copyFile(sourceFile, destFile);

                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {

                            }

                            StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(Uri.fromFile(fileImages.get(count))));
                            //  mUploadTask = sRef.putFile(Uri.fromFile(fileImages.get(count)));
                            mUploadTask = sRef.putFile(Uri.fromFile(destFile));

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
                                                    //Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                                                    //  TastyToast.makeText(getApplicationContext(), "Updated", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                                    urlExists = true;


                                                    if (exitact == true) {
                                                        TastyToast.makeText(getApplicationContext(), "Updated", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                                        pathList.clear();
                                                        startActivity(new Intent(EditJournalEntry.this, MainActivity.class));
                                                        finish();
                                                    }
                                                }
                                            });

                                        }
                                    });




                    }


                }



            }
            else {

                    TastyToast.makeText(getApplicationContext(), " please check your internet connection", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
                }
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
               // Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                TastyToast.makeText(getApplicationContext(), "Updated", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                startActivity(new Intent(EditJournalEntry.this, MainActivity.class));
                finish();
            }

        }
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static boolean isInternetAvailable() {
        Boolean isConnection = false;
        int connectTimeout = 5000; // in ms
        int readTimeout = 5000; // in ms
        String ip204 = "http://clients3.google.com/generate_204";

        try {
            URL url = new URL(ip204);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setRequestMethod("HEAD");
            InputStream in = conn.getInputStream();
            int status = conn.getResponseCode();
            in.close();
            conn.disconnect();
            if (status == HttpURLConnection.HTTP_NO_CONTENT) {
                isConnection = true;
            }
        } catch (Exception e) {
            isConnection = false;
        }
        return isConnection;
    }



    public void setJournal(){

        tvJournalDate.setText(journalEntry.getJournalDate());
        etJournalLocation.setText(journalEntry.getJournalLocation());
        etJournalTitle.setText(journalEntry.getJournalTitle());
        etJournalMessage.setText(journalEntry.getJournalMessage());
        journalDay = journalEntry.getDay();
        journalMonth = journalEntry.getMonth();

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
         } else if(journalEntry.getJournalWeather().equalsIgnoreCase("Windy")){
             spinnerWeather.post(new Runnable() {
                 @Override
                 public void run() {
                     spinnerWeather.setSelection(3);
                     currentWeather=  "Windy";
                 }
             });
         } else if(journalEntry.getJournalWeather().equalsIgnoreCase("Snowy")){
             spinnerWeather.post(new Runnable() {
                 @Override
                 public void run() {
                     spinnerWeather.setSelection(4);
                     currentWeather=  "Snowy";
                 }
             });
         } else if(journalEntry.getJournalWeather().equalsIgnoreCase("Foggy")){
             spinnerWeather.post(new Runnable() {
                 @Override
                 public void run() {
                     spinnerWeather.setSelection(5);
                     currentWeather=  "Foggy";
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

         }else if(journalEntry.getJournalMood().equalsIgnoreCase("Amused")){

             spinnerWeather.post(new Runnable() {
                 @Override
                 public void run() {
                     spinnerMood.setSelection(2);
                     currentMood = "Amused";
                 }
             });

         }else if(journalEntry.getJournalMood().equalsIgnoreCase("Dreamy")){

             spinnerWeather.post(new Runnable() {
                 @Override
                 public void run() {
                     spinnerMood.setSelection(3);
                     currentMood = "Dreamy";
                 }
             });

         }else if(journalEntry.getJournalMood().equalsIgnoreCase("Mysterious")){

             spinnerWeather.post(new Runnable() {
                 @Override
                 public void run() {
                     spinnerMood.setSelection(4);
                     currentMood = "Mysterious";
                 }
             });

         }else if(journalEntry.getJournalMood().equalsIgnoreCase("Romantic")){

             spinnerWeather.post(new Runnable() {
                 @Override
                 public void run() {
                     spinnerMood.setSelection(5);
                     currentMood = "Romantic";
                 }
             });

         }


         else{

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
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
        String selectedFont = sharedPreferences.getString(getString(R.string.key_uiThemeFont), "6");
        if(selectedFont.equals("0")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.parisienneregular);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(etJournalLocation, myCustomFont);
            fontUtils.applyFontToView(etJournalTitle, myCustomFont);
            fontUtils.applyFontToView(etJournalMessage, myCustomFont);

            tvJournalDate.setTextSize(14f);
        } else if(selectedFont.equals("1")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.patrick_hand_sc);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(etJournalLocation, myCustomFont);
            fontUtils.applyFontToView(etJournalTitle, myCustomFont);
            fontUtils.applyFontToView(etJournalMessage, myCustomFont);
        } else if( selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.sofadi_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(etJournalLocation, myCustomFont);
            fontUtils.applyFontToView(etJournalTitle, myCustomFont);
            fontUtils.applyFontToView(etJournalMessage, myCustomFont);
        }else if(selectedFont.equals("3")){
            FontUtils fontUtils = new FontUtils();
            Typeface myCustomFont = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(etJournalLocation, myCustomFont);
            fontUtils.applyFontToView(etJournalTitle, myCustomFont);
            fontUtils.applyFontToView(etJournalMessage, myCustomFont);
        }
        else if( selectedFont.equals("4")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.concert_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(etJournalLocation, myCustomFont);
            fontUtils.applyFontToView(etJournalTitle, myCustomFont);
            fontUtils.applyFontToView(etJournalMessage, myCustomFont);

        }
        else if( selectedFont.equals("5")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.oleo_script);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(etJournalLocation, myCustomFont);
            fontUtils.applyFontToView(etJournalTitle, myCustomFont);
            fontUtils.applyFontToView(etJournalMessage, myCustomFont);
        }
        else if( selectedFont.equals("6")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.pt_sans_narrow);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(etJournalLocation, myCustomFont);
            fontUtils.applyFontToView(etJournalTitle, myCustomFont);
            fontUtils.applyFontToView(etJournalMessage, myCustomFont);


        }  else if( selectedFont.equals("7")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.roboto_condensed_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(etJournalLocation, myCustomFont);
            fontUtils.applyFontToView(etJournalTitle, myCustomFont);
            fontUtils.applyFontToView(etJournalMessage, myCustomFont);
        }  else if( selectedFont.equals("8")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.shadows_into_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(etJournalLocation, myCustomFont);
            fontUtils.applyFontToView(etJournalTitle, myCustomFont);
            fontUtils.applyFontToView(etJournalMessage, myCustomFont);
        } else if( selectedFont.equals("9")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.slabo_13px);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(etJournalLocation, myCustomFont);
            fontUtils.applyFontToView(etJournalTitle, myCustomFont);
            fontUtils.applyFontToView(etJournalMessage, myCustomFont);
        }
        else {

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
            isWhite = true;
            isLit = true;
        }
        else if (userTheme.equals("0")) {
            setTheme(R.style.BlueLitStyle);
            isWhite = true;
            isLit =  false;
        } else if (userTheme.equals("3")) {
            setTheme(R.style.YellowLitStyle);
            isWhite=false;
        } else if (userTheme.equals("4")) {
            setTheme(R.style.BluishLitStyle);
            isWhite=true;
        } else if (userTheme.equals("5")) {
            setTheme(R.style.GreenishLitStyle);
            isWhite=false;
        } else if (userTheme.equals("6")) {
            setTheme(R.style.TacaoLitStyle);
            isWhite=false;
        }else if (userTheme.equals("8")) {
            setTheme(R.style.TyrianLitStyle);
            isWhite=true;
        }else if (userTheme.equals("7")) {
            setTheme(R.style.DarkLitStyle);
            isWhite=true;
            isDarkTheme = true;
        }

        else{

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //outState.putParcelable("JournalEntry", journalEntry);
        Log.d("File size" , Integer.toString(fileImages.size()));
        outState.putString("JournalDate", tvJournalDate.getText().toString());
        outState.putString("JournalLocation", etJournalLocation.getText().toString());
        outState.putString("JournalTitle", etJournalTitle.getText().toString());
        outState.putString("JournalMessage", etJournalMessage.getText().toString());
        outState.putString("JournalDay", journalDay);
        outState.putString("JournalMonth", journalMonth);
        outState.putStringArrayList("PathList", pathList);
        outState.putStringArrayList("ImageUploads", imageUploads);
        outState.putParcelableArrayList("ImagesUri", imagesUri);
        outState.putParcelableArrayList("RetrievedPhotoList", (ArrayList) retrievedphotoList);
        outState.putSerializable("FileImages", fileImages);
       outState.putParcelableArrayList("PhotoList", (ArrayList) photoList);
       outState.putStringArrayList("UploadedImages", uploadedImages);
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
     //journalEntry = savedInstanceState.getParcelable("JournalEntry");
        tvJournalDate.setText(savedInstanceState.getString("JournalDate"));
        etJournalLocation.setText(savedInstanceState.getString("JournalLocation"));
        etJournalTitle.setText(savedInstanceState.getString("JournalTitle"));
        etJournalMessage.setText(savedInstanceState.getString("JournalMessage"));
        journalDay = savedInstanceState.getString("JournalDay");
        journalMonth = savedInstanceState.getString("JournalMonth");
        pathList =  savedInstanceState.getStringArrayList("PathList");
        imageUploads = savedInstanceState.getStringArrayList("ImageUploads");
        imagesUri = savedInstanceState.getParcelableArrayList("ImagesUri");
        retrievedphotoList = savedInstanceState.getParcelableArrayList("RetrievedPhotoList");
        fileImages =(ArrayList<File>) savedInstanceState.getSerializable("FileImages");
        uploadedImages = savedInstanceState.getStringArrayList("UploadedImages");

        photoList = savedInstanceState.getParcelableArrayList("PhotoList");
       // getImagePaths();
        super.onRestoreInstanceState(savedInstanceState);
    }
}
