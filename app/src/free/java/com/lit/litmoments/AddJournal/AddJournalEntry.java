package com.lit.litmoments.AddJournal;

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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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

import com.ajts.androidmads.fontutils.FontUtils;
import com.lit.litmoments.BuildConfig;
import com.lit.litmoments.R;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kd.dynamic.calendar.generator.ImageGenerator;
import com.lit.litmoments.Main.MainActivity;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.zelory.compressor.Compressor;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static java.util.concurrent.TimeUnit.SECONDS;


public class AddJournalEntry extends AppCompatActivity implements JournalEntryAdapater.OnClickAction, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = AddJournalEntry.class.getName();
    @BindView(R.id.entrytoolbar)
    Toolbar entryToolbar;
    @BindView(R.id.etJournalMessage)
    EditText etJournalMessage;
    @BindView(R.id.etJournalLocation)
    EditText etJournalLocation;
    // @BindView(R.id.etJournalWeather) EditText etJournalWeather;
    //@BindView(R.id.etJournalMood) EditText etJournalMood;
    @BindView(R.id.etJournalTitle)
    EditText etJournalTitle;
    @BindView(R.id.tvJournalDate)
    TextView tvJournalDate;
    //   @BindView(R.id.photojournal_fab) FloatingActionButton photoJournalFab;
    //  @BindView(R.id.savejournal_fab) FloatingActionButton saveJournalFab;
    @BindView(R.id.rvPhotos)
    RecyclerView rvPhotos;
    @BindView(R.id.spinner_weather)
    Spinner spinnerWeather;
    @BindView(R.id.spinner_mood)
    Spinner spinnerMood;
    @BindView(R.id.addRlayout)
    RelativeLayout relativeLayout;
    private JournalEntryAdapater journalEntryAdapater;
    List<JournalPhotoModel> photoList = new ArrayList<>();
    final int numberOfColumns = 2;
    private String journalMonth, journalDay;

    ArrayList<String> pathList = new ArrayList<>();
    ArrayList<File> fileImages = new ArrayList<>();
    ArrayList<Uri> imagesUri = new ArrayList<>();
    private static final int REQUEST_CODE_CHOOSE = 23;
    public static final int REQUEST_CODE_CAMERA = 0012;
    public static final int REQUEST_CODE_GALLERY = 0013;
    private String[] items = {"Camera", "Gallery"};
    Calendar mCurrentDate;
    ImageGenerator mImageGenerator;
    Bitmap mGeneratedDateIcon;
    private Date date;
    DateFormat df;


    List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME);

    String apiKey = BuildConfig.TESTADKEY;
    String refKey = " ", currentWeather, currentMood;


    public static final String STORAGE_PATH_UPLOADS = "uploads/";
    public static final String DATABASE_UPLOADS = "User's Journal Entries";

    private StorageReference storageReference;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    private StorageTask mUploadTask;
    private ArrayList<String> uploadedImages = new ArrayList<>();

    String[] weather = {"Sunny", "Cloudy", "Rainy"};
    String[] weatherprem = {"Sunny", "Cloudy", "Rainy", "Windy","Snowy", "Foggy"};
    int imagesprem[] = {R.drawable.ic_sunny, R.drawable.ic_cloudy, R.drawable.ic_rainy, R.drawable.ic_windy,  R.drawable.ic_snowy, R.drawable.ic_foggy};
    int images[] = {R.drawable.ic_sunny, R.drawable.ic_cloudy, R.drawable.ic_rainy};

    String[] mood = {"Happy", "Sad", "Amused"};
    String[] moodprem = {"Happy", "Sad", "Amused", "Dreamy", "Mysterious", "Romantic"};
    int moodImages[] = {R.drawable.ic_happy, R.drawable.ic_sad, R.drawable.ic_surprised};
    int moodImagesprem[] = {R.drawable.ic_happy, R.drawable.ic_sad, R.drawable.ic_surprised, R.drawable.ic_dreamy, R.drawable.ic_mysterious, R.drawable.ic_romantic };

    ActionMode actionMode;

    Boolean isWhite = false, isLit = false;
    MenuItem copyMenuItem;

    File file;
    public static final String IMAGE_DIRECTORY = "Lit Moments";
    private SimpleDateFormat dateFormatter;

    double longitude = 0.0, latitude = 0.0;

    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_CONTENT = "content";
    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    private static final int MY_WRITESTORAGE_REQUEST_CODE = 100;
    private static final int MY_CAMERA_REQUEST_CODE = 102;
    private static final int MY_LOCATION_REQUEST_CODE = 104;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath = " ";
    Uri photoURI;

    int photocount = 0, jumpsize = 0;

    Handler mHandlerThread;
    Thread locationThread;
    private boolean isPremiumUser;
    Boolean isDarkTheme = false;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadUiTheme(sharedPreferences);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal_entry);
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
        //entryToolbar.setTitle(getResources().getString(R.string.add_entry));
        /**  Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.parisienneregular);
         FontUtils fontUtils = new FontUtils();
         fontUtils.applyFontToToolbar(entryToolbar, myCustomFont); **/

        FirebaseApp.initializeApp(getApplicationContext());


        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        String currentUid = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference(DATABASE_UPLOADS).child(currentUid);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            isPremiumUser = bundle.getBoolean("isPremium", false);
        }


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, numberOfColumns);


        rvPhotos.setLayoutManager(mLayoutManager);
        // FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        //layoutManager.setFlexDirection(FlexDirection.ROW);
        //layoutManager.setFlexWrap(FlexWrap.WRAP);
        //layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        //rvPhotos.setLayoutManager(layoutManager);
        rvPhotos.setItemAnimator(new DefaultItemAnimator());
        rvPhotos.setNestedScrollingEnabled(false);


        if (savedInstanceState != null) {

            //  Icepick.restoreInstanceState(this, savedInstanceState);

            tvJournalDate.setText(savedInstanceState.getString("JournalDate"));
            etJournalLocation.setText(savedInstanceState.getString("JournalLocation"));
            etJournalTitle.setText(savedInstanceState.getString("JournalTitle"));
            etJournalMessage.setText(savedInstanceState.getString("JournalMessage"));
            pathList = savedInstanceState.getStringArrayList("PathList");
            photoList = savedInstanceState.getParcelableArrayList("PhotoList");
            imagesUri = savedInstanceState.getParcelableArrayList("ImagesUri");
            fileImages = (ArrayList<File>) savedInstanceState.getSerializable("FileImages");
            journalEntryAdapater = new JournalEntryAdapater(photoList, getApplicationContext());

            rvPhotos.setVisibility(View.VISIBLE);
            rvPhotos.setAdapter(journalEntryAdapater);

        } else {
            photoList.clear();
            fileImages.clear();
            journalEntryAdapater = new JournalEntryAdapater(photoList, getApplicationContext());
            rvPhotos.setAdapter(journalEntryAdapater);
        }
        // width and height will be at least 600px long (optional).
        //  EasyImage.configuration(this).setImagesFolderName("Journal Images").setAllowMultiplePickInGallery(true);


        /**   photoJournalFab.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {

        // onPickImage(v);
        openImage();

        }
        });

         saveJournalFab.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        uploadJournal();
        }
        });
         **/
        spinnerWeather.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(AddJournalEntry.this, "You Select Position: "+position+" "+weather[position], Toast.LENGTH_SHORT).show();

                if(isPremiumUser) {
                    currentWeather = weatherprem[position];
                }else {
                    currentWeather = weather[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(isPremiumUser) {
            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), imagesprem, weatherprem);
            spinnerWeather.setAdapter(customAdapter);
        } else {
            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), images, weather);
            spinnerWeather.setAdapter(customAdapter);
        }

        spinnerMood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, "You Select Position: "+position+" "+fruits[position], Toast.LENGTH_SHORT).show();
               if(isPremiumUser) {
                   currentMood = moodprem[position];
               } else {
                   currentMood = mood[position];
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(isPremiumUser){
            CustomMoodAdapter custommoodAdapter = new CustomMoodAdapter(getApplicationContext(), moodImagesprem, moodprem);
            spinnerMood.setAdapter(custommoodAdapter);
        }else {
            CustomMoodAdapter custommoodAdapter = new CustomMoodAdapter(getApplicationContext(), moodImages, mood);
            spinnerMood.setAdapter(custommoodAdapter);
        }

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
        if (Integer.parseInt(defaultDay) <= 9) {
            defaultDay = defaultDay.replace("0", " ");
        } else {
            defaultDay = defaultDay;
        }
        tvJournalDate.setText(defaultDate);
        journalDay = defaultDay;
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
                        selectedMonth = selectedMonth + 1;
                        journalDay = Integer.toString(selectedDay);
                        journalMonth = Integer.toString(selectedMonth);
                        String selectedDate = Integer.toString(selectedYear) + "-" + Integer.toString(selectedMonth) + "-" + Integer.toString(selectedDay);
                        df = new SimpleDateFormat("EEE, d MMM yyyy");
                        //String formatedDate = df.format("2018-06-09");
                        tvJournalDate.setText(selectedDate);
                        journalDay = Integer.toString(month);
                        journalMonth = Integer.toString(day);


                    }


                }, year, month, day);
                mDatePicker.show();


                date = Calendar.getInstance().getTime();
                String formattedDate = df.format(date);

            }
        });

        journalEntryAdapater.setActionModeReceiver((JournalEntryAdapater.OnClickAction) AddJournalEntry.this);
        // getUserLocation();
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(AddJournalEntry.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, MY_LOCATION_REQUEST_CODE);

            } else {
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    try {
                        LocationManager loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location = loc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                        getAddress(this, latitude, longitude);
                    } catch (Exception e) {
                        //etJournalLocation.setText("Lit Place");
                     //   TastyToast.makeText(getApplicationContext(), "Couldn't get your location", TastyToast.LENGTH_SHORT, TastyToast.INFO);

                        CurrentUserLocation.LocationResult locationResult = new CurrentUserLocation.LocationResult(){
                            @Override
                            public void gotLocation(Location location){
                                //Got the location!
                                longitude = location.getLongitude();
                                latitude = location.getLatitude();
                                getAddress(getApplicationContext(), latitude, longitude);
                            }
                        };
                        CurrentUserLocation myLocation = new CurrentUserLocation();
                        myLocation.getLocation(this, locationResult);



                    }
                } else {
                    TastyToast.makeText(getApplicationContext(), " Location is turned off", TastyToast.LENGTH_SHORT, TastyToast.INFO);
                }
            }
        }


        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }


        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);

        if (Build.VERSION.SDK_INT >= 23) {
            if (isLit == false) {
                etJournalLocation.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.bluecolorAccent)));
            } else if (isLit == true) {
                etJournalLocation.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.reddishcolorAccent)));
            }
        }

    }



    /**
     @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
     //pathList.add()

     // TODO do something with the bitmap
     }

     public void onPickImage(View view) {
     // Click on image button
     ImagePicker.pickImage(this, "Select your image:");
     } **/
    public void setUpThemeContent() {
        if (isWhite == true) {
            entryToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            ((TextView) entryToolbar.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.white));
            spinnerMood.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            spinnerWeather.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            relativeLayout.setBackgroundColor(getResources().getColor(R.color.background));


        } else {
            entryToolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
            ((TextView) entryToolbar.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorAccent));
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


    public void lookupLocation() {
        final Runnable beeper = new Runnable() {
            public void run() {
                if (etJournalLocation.getText().toString().trim().isEmpty()) {
                    // getUserLocation();

                    Log.d("place", "running");
                }
            }
        };
        final ScheduledFuture<?> beeperHandle =
                scheduler.scheduleAtFixedRate(beeper, 10, 10, SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                beeperHandle.cancel(true);
            }
        }, 30 * 30, SECONDS);
    }

    private void openImage() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (items[i].equals("Camera")) {
                    //

                    /**  if(isWriteStoragePermissionGranted() && isReadStoragePermissionGranted()) {

                     EasyImage.openCameraForImage(AddJournalEntry.this, REQUEST_CODE_CAMERA);
                     } **/

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddJournalEntry.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_REQUEST_CODE);

                    } else {
                     //   EasyImage.openCameraForImage(AddJournalEntry.this, REQUEST_CODE_CAMERA);
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                               photoURI  = FileProvider.getUriForFile(getApplicationContext(),
                                        "com.lit.litmoments.fileprovider",
                                        photoFile);
                               Log.d("photouri", photoURI.getPath());
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }
                        }
                    }

                } else {

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AddJournalEntry.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITESTORAGE_REQUEST_CODE);

                    } else {
                        Matisse.from(AddJournalEntry.this)
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

                    // EasyImage.openGallery(AddJournalEntry.this, REQUEST_CODE_GALLERY);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

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

                Matisse.from(AddJournalEntry.this)
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
               // EasyImage.openCameraForImage(AddJournalEntry.this, REQUEST_CODE_CAMERA);
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
                                "com.lit.litmoments.fileprovider",
                                photoFile);
                        Log.d("photouri", photoURI.getPath());
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }

            } else {

            }
        } else if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return; }
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                        getAddress(this, latitude, longitude);
                    } catch (Exception e) {
                       // TastyToast.makeText(getApplicationContext(), "Couldn't get your location", TastyToast.LENGTH_SHORT, TastyToast.INFO);
                    }
                }
            }

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                File uriFile =new File(uri.getPath());
                journalImage.setJournalImage(uri);
                photoList.add(journalImage);
               // fileImages.add(new File(uri.getPath()));
                imagesUri.add(uri);
                //fileImages.add(uriFile);
                Log.d("Matisse", "selected file: " + uri.getPath());
                 File litfile;
                //litfile =file;

                File sourceFile = new File(getPathFromGooglePhotosUri(uri));
                Random random = new Random();
                int a = random.nextInt(100);
                File destFile = new File(file, "img_"+a+ dateFormatter.format(new Date()).toString() + ".png");

               // fileImages.add(destFile);

                fileImages.add(uriFile);



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
            if(photoList.size() != 0){

                rvPhotos.setVisibility(View.VISIBLE);
                Log.d("Matisse", "mSelected: " + photoList.size());
            }

            journalEntryAdapater.notifyDataSetChanged();


        } else{


            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
              //  Bundle extras = data.getExtras();
                Uri myuri = photoURI;
                JournalPhotoModel journalImage = new JournalPhotoModel();
                journalImage.setJournalImage(myuri);
                photoList.add(journalImage);
                fileImages.add(new File(myuri.getPath()));
                imagesUri.add(myuri);
                // Toast.makeText(AddJournalEntry.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, " uri from file" + myuri);

            }

            if(photoList.size() != 0){

                rvPhotos.setVisibility(View.VISIBLE);
            }
            journalEntryAdapater.notifyDataSetChanged();


          /**  EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {

                @Override
                public void onImagesPicked(List<File> imageFiles, EasyImage.ImageSource source, int type) {
                    Log.d("cameradata", imageFiles.toString());

                    if(requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK){

                        Uri myuri = data.getData();
                            for(File file:imageFiles){
                                JournalPhotoModel journalImage = new JournalPhotoModel();
                                journalImage.setJournalImage(myuri);
                                photoList.add(journalImage);
                                fileImages.add(file);
                               // Toast.makeText(AddJournalEntry.this, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                                Log.d(TAG, " uri from file" + Uri.fromFile(file));
                             //   Log.d(TAG, " uri from file" + myuri);


                            }
                        if(photoList.size() != 0){

                            rvPhotos.setVisibility(View.VISIBLE);
                        }
                            journalEntryAdapater.notifyDataSetChanged();

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
            }); **/

        }
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

    public  void getAddress(Context context, double LATITUDE, double LONGITUDE) {

//Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {


                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                Log.d(TAG, "getAddress:  address" + address);
                Log.d(TAG, "getAddress:  city" + city);
                Log.d(TAG, "getAddress:  state" + state);
                Log.d(TAG, "getAddress:  country" + country);
                Log.d(TAG, "getAddress:  postalCode" + postalCode);
                Log.d(TAG, "getAddress:  knownName" + knownName);

                if(city != null && !city.isEmpty() && city.trim() != " ") {
                    if(etJournalLocation.getText().toString().trim() == "" || etJournalLocation.getText().toString().trim().isEmpty())
                    etJournalLocation.setText(city +","+" "+ country);
                }
                //else if (postalCode !=null){
               //     etJournalLocation.setText(postalCode+","+ " "+  country);
               // }
                else{
                    etJournalLocation.setText("Lit Place");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            etJournalLocation.setText("Lit Place");
        }
        return;
    }

   public void getLocationPermission() {


       if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

           ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
       }

    //  getUserLocation();

   }


   public void uploadJournal() {



       if (TextUtils.isEmpty(etJournalTitle.getText().toString()))
       {
           //Toast.makeText(AddJournalEntry.this, "Journal title is empty", Toast.LENGTH_SHORT).show();
           TastyToast.makeText(getApplicationContext(), "Journal title is empty", TastyToast.LENGTH_SHORT, TastyToast.INFO);
       }

       else if (TextUtils.isEmpty(etJournalMessage.getText().toString() ))
       {
          // Toast.makeText(AddJournalEntry.this, "Journal message is empty", Toast.LENGTH_SHORT).show();
           TastyToast.makeText(getApplicationContext(), "Journal message is empty", TastyToast.LENGTH_SHORT, TastyToast.INFO);
       }

       else
           {
           // Toast.makeText(AddJournalEntry.this, " " +  photoList.size() + " ", Toast.LENGTH_SHORT).show();

               if (photoList.size() != 0) {

                   if(isOnline()){
                   //Toast.makeText(AddJournalEntry.this, " " +  photoList.size() + " ", Toast.LENGTH_SHORT).show();

                  //getting the storage reference
                 // StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(Uri.fromFile(fileImages.get(0))));

                   final ProgressDialog progressDialog = new ProgressDialog(this);
                   //  progressDialog.setTitle("Saving Data");
                  // progressDialog.show();

                   progressDialog.setMessage("Uploading Data");
                   progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                   progressDialog.setIndeterminate(true);
                   progressDialog.setProgress(0);
                   progressDialog.show();

                  //adding the file to reference
                   DatabaseReference databaseReference = mDatabase;
                   refKey = databaseReference.push().getKey();
                   //databaseReference.child(refKey);
                   ObjectMapper oMapper = new ObjectMapper();

                   int  progresssize= 0;
                   progresssize = photoList.size();

                    jumpsize = 100 / progresssize;


                 //  databaseReference.setValue(journalEntryModel);
                   HashMap<String, Object> myFilePath = new HashMap<String, Object>();
                       for ( int count =0; count < photoList.size(); count++ ) {


                           photocount = 0;
                       // File myFile = fileImages.get(count);
                           String path =imagesUri.get(count).toString();
                           File myFile = new File(path);

                           File sourceFile = new File(getPathFromGooglePhotosUri(imagesUri.get(count)));
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
                         //  StorageReference sRef = storageReference.child(STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(imagesUri.get(count)));


                           //mUploadTask =  sRef.putFile(Uri.fromFile(fileImages.get(count)));

                           //mUploadTask =  sRef.putFile(Uri.fromFile(myFile));

                           mUploadTask=sRef.putFile(Uri.fromFile(destFile));


                          mUploadTask.addOnFailureListener(exception -> Log.i("It didn't work", "double check"))
                                  .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
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
                                          if(jumpsize < 100){

                                              progressDialog.setProgress(jumpsize);
                                              jumpsize = jumpsize + jumpsize;

                                          } else {

                                              progressDialog.dismiss();
                                              TastyToast.makeText(getApplicationContext(), "Saved successfully", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                              startActivity(new Intent(AddJournalEntry.this, MainActivity.class));
                                              finish();
                                          }
                                         //Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                                      }



                                  }) .addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          TastyToast.makeText(getApplicationContext(), "Image upload failed", TastyToast.LENGTH_SHORT, TastyToast.ERROR);
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
                           //progressDialog.dismiss();
                           //Toasty.success(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT, true).show();

                      }
                     // progressDialog.dismiss();
                      //TastyToast.makeText(getApplicationContext(), "Saved successfully ", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);

                      //dismissing the progress dialog

                   pathList.clear();
                  // fileImages.clear();

                   //TastyToast.makeText(getApplicationContext(), "Saved successfully ", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                  }

                   else{
                    TastyToast.makeText(getApplicationContext(), "Please check your internet connection", TastyToast.LENGTH_SHORT, TastyToast.INFO);
                   }

           }



             else {


           //adding an upload to firebase database
           DatabaseReference databaseReference = mDatabase.push();
           refKey = databaseReference.getKey();
           //HashMap<String, String> myFilePath = new HashMap<String, String>();
                   JournalEntryModel journalEntryModel = new JournalEntryModel(tvJournalDate.getText().toString(), etJournalLocation.getText().toString(), currentWeather,
                           currentMood, etJournalTitle.getText().toString(), etJournalMessage.getText().toString(), journalMonth, journalDay,
                           null, refKey);

           databaseReference.setValue(journalEntryModel);
           //displaying success toast
           //Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
           //Toasty.success(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT, true).show();
           TastyToast.makeText(getApplicationContext(), "Saved successfully ", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
          /** currentMood = " ";
           currentWeather = " ";
           spinnerMood.setSelection(0);
           spinnerMood.setSelection(0);
           etJournalTitle.setText("");
           etJournalMessage.setText(" ");
           journalMonth="";
           journalDay = " ";
           refKey = " "; **/
           startActivity(new Intent(AddJournalEntry.this, MainActivity.class));
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

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


// Request permissions

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
            tintMenuIcon(AddJournalEntry.this, menuItem);
        }
        if (menuPhotoItem!= null) {
            tintMenuIcon(AddJournalEntry.this, menuPhotoItem);
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
        journalEntryAdapater.selectAll();

        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
            actionMode.setTitle("Selected: " + journalEntryAdapater.getSelected().size());
        }
    }

    public void deselectAll(View v) {
        journalEntryAdapater.clearSelected();
        if (actionMode != null) {
            actionMode.finish();
            actionMode = null;
        }
    }

    public void onClickAction() {
        int selected = journalEntryAdapater.getSelected().size();
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

                               // journalEntryAdapater.getSelected().remove(true);
                                List<JournalPhotoModel> selected = new ArrayList<>();
                                selected = journalEntryAdapater.getSelected();
                                //journalEntryAdapater.clearAll(true);
                                for (JournalPhotoModel journalPhotoModel : selected) {
                                    photoList.remove(journalPhotoModel);
                                }
                                journalEntryAdapater.clearSelected();
                                journalEntryAdapater.notifyDataSetChanged();


                            }
                        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        List<JournalPhotoModel> selected = new ArrayList<>();
                        selected = journalEntryAdapater.getSelected();
                        for(int i=0; i< selected.size(); i++) {
                            journalEntryAdapater.unhighlightViewOnCancel((JournalEntryViewHolder) rvPhotos.findViewHolderForAdapterPosition(i));
                            journalEntryAdapater.clearSelected();
                            //journalEntryAdapater.unhighlightView((JournalEntryViewHolder) rvPhotos.findViewHolderForAdapterPosition(i));
                        }
                    }
                });

        builder.show();

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
        } else if(selectedFont.equals("3")){
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
        }else if (userTheme.equals("3")) {
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
        }else if (userTheme.equals("7")) {
            setTheme(R.style.DarkLitStyle);
            isWhite=true;
            isDarkTheme = true;
        }
        else if (userTheme.equals("8")) {
            setTheme(R.style.TyrianLitStyle);
            isWhite = true;
        }
        else{

        }
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //prefMethods.changeListenerTheme(sharedPreferences, this);
        if(key.equals(getResources().getString(R.string.key_uiTheme))) {
            loadUiTheme(sharedPreferences);
            AddJournalEntry.this.recreate();
        } else if(key.equals(getResources().getString(R.string.key_uiThemeFont))){
            loadWidgetColors(sharedPreferences);
            AddJournalEntry.this.recreate();
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
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("JournalDate", tvJournalDate.getText().toString());
        outState.putString("JournalLocation", etJournalLocation.getText().toString());
        //outState.putString("JournalMood", currentMood);
        //outState.putString("JournalWeather", currentWeather);
        outState.putString("JournalTitle", etJournalTitle.getText().toString());
        outState.putString("JournalMessage", etJournalMessage.getText().toString());
        outState.putStringArrayList("PathList", pathList);
        outState.putParcelableArrayList("PhotoList", (ArrayList) photoList);
        outState.putParcelableArrayList("ImagesUri", imagesUri);
        outState.putSerializable("FileImages", fileImages);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        //Icepick.restoreInstanceState(this, savedInstanceState);
        tvJournalDate.setText(savedInstanceState.getString("JournalDate"));
        etJournalLocation.setText(savedInstanceState.getString("JournalLocation"));
        etJournalTitle.setText(savedInstanceState.getString("JournalTitle"));
        etJournalMessage.setText(savedInstanceState.getString("JournalMessage"));
        pathList = savedInstanceState.getStringArrayList("PathList");
        photoList = savedInstanceState.getParcelableArrayList("PhotoList");
        imagesUri =savedInstanceState.getParcelableArrayList("ImagesUri");
        fileImages = (ArrayList<File>) savedInstanceState.getSerializable("FileImages");

        super.onRestoreInstanceState(savedInstanceState);

    }
}
