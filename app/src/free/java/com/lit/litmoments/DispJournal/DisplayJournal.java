package com.lit.litmoments.DispJournal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajts.androidmads.fontutils.FontUtils;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.lit.litmoments.AddJournal.JournalEntryModel;
import com.lit.litmoments.BuildConfig;
import com.lit.litmoments.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lit.litmoments.EditJournal.EditJournalEntry;
import com.lit.litmoments.Main.MainActivity;
import com.lit.litmoments.SubscribersModelActivity;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplayJournal extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener  {
    @BindView(R.id.entrytoolbar)  Toolbar entryToolbar;
    @BindView(R.id.tvdpJournalDate)  TextView tvJournalDate;
    @BindView(R.id.tvdpJournalLocation)  TextView tvJournalLocation;
    @BindView(R.id.tvdpJournalWeather)  TextView tvJournalWeather;
    @BindView(R.id.tvdpJournalMood)  TextView tvJournalMood;
    @BindView(R.id.tvdpJournalTitle)  TextView tvJournalTitle;
    @BindView(R.id.tvdpJournalMessage)  TextView tvJournalMessage;
    @BindView(R.id.ivdpJournalPhoto)  ImageView ivJournalPhoto;
    @BindView(R.id.ivWeather)  ImageView ivJournalWeather;
    @BindView(R.id.ivMood)  ImageView ivJournalMood;
  //  @BindView(R.id.filterview) ImageView filterView;

    JournalEntryModel journalEntry;
    Intent intent;

    String journalKey = " ";
    private DatabaseReference mDatabase;
    public static final String DATABASE_UPLOADS = "User's Journal Entries";
    private FirebaseAuth mAuth;
    private MenuItem mMenuItem;
    Target loadtarget;

    private boolean isPremiumUser;

    private DatabaseReference sDatabase;

    private static  final String SUBSCRIBER_UPLOADS = "Subscription Details";



    String AdMobId = " ";


    String AdMobInterstitialId = " ";
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
     //   getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        SharedPreferences sharedPreferences;
        sharedPreferences  = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_journal);
        ButterKnife.bind(this);




        if (entryToolbar != null) {
            setSupportActionBar(entryToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            loadWidgetColors(sharedPreferences);
        }




        //make translucent statusBar on kitkat devices
      if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
          //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
           setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);

           getWindow().setStatusBarColor(Color.BLACK);

        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /** Slide fadein = new Slide();
             fadein.setDuration(3500);
             Slide fadeout = new Slide();
             fadeout.setDuration(3500);
             // set an enter transition
             getWindow().setSharedElementEnterTransition(fadein);
             // set an exit transition
             getWindow().setSharedElementExitTransition(fadeout);
             // supportPostponeEnterTransition();
             supportPostponeEnterTransition();
             postponeEnterTransition();

             **/
            Fade fade= new Fade();
            View view = getWindow().getDecorView();
            fade.excludeTarget(view.findViewById(R.id.action_bar_container), true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
           // supportPostponeEnterTransition();

        }
        supportPostponeEnterTransition();


        AdMobId = BuildConfig.TESTADKEY;
        AdMobInterstitialId = BuildConfig.TESTADINTERSTITIALKEY;
        mAuth = FirebaseAuth.getInstance();
        String currentUid = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference(DATABASE_UPLOADS).child(currentUid);
        sDatabase = FirebaseDatabase.getInstance().getReference(SUBSCRIBER_UPLOADS).child(currentUid);
        getUserData(sDatabase);

            MobileAds.initialize(this, AdMobId);

            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(AdMobInterstitialId);
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    //super.onAdClosed();
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }
            });

        Bundle extras = getIntent().getExtras();

        if(savedInstanceState != null){
            journalEntry = savedInstanceState.getParcelable("JournalParcelable");
        } else {

            journalEntry = extras.getParcelable("journalItems");
        }

        if(journalEntry != null){
           getJournalDetails();

           //  ArrayList<String> arrimageFile = new ArrayList<>();
           // arrimageFile.addAll(journalEntry.getJournalImagePath());
           // Toast.makeText(DisplayJournal.this, "No is " + " "+ arrimageFile.size()+" ", Toast.LENGTH_SHORT).show();
        }
        if(journalEntry.getJournalImagePath() != null) {

            for (String imagePath: journalEntry.getJournalImagePath()) {

                new ImageLoadAsyncTask().execute(imagePath);

            }

        }


        ivJournalPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (journalEntry.getJournalImagePath().size() == 0) {

                }
                else {
                    ArrayList<String> arrimageFile = new ArrayList<>();
                    arrimageFile.addAll(journalEntry.getJournalImagePath());
                    Intent intent = new Intent(DisplayJournal.this, ImageSliderActivity.class);
                    // intent.putExtra("displayjournal", journalEntry.getJournalImagePath()));
                    intent.putStringArrayListExtra("imagefiles", arrimageFile);
                    startActivity(intent);
                }
            }
        });

        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .icon(getResources().getDrawable(R.drawable.ic_journal))
                .session(2)
                .threshold(3)
                .title("How is your experience with us?")
                .titleTextColor(getResources().getColor(R.color.colorAccent))
                .positiveButtonText("Maybe Later")
                .negativeButtonText("Never")
                .positiveButtonTextColor(getResources().getColor(R.color.colorAccent))
                .negativeButtonTextColor(getResources().getColor(R.color.colorAccent))
                .formTitle("Submit Feedback")
                .formHint("Tell us where we can improve")
                .formSubmitText("Submit")
                .formCancelText("Cancel")
                .ratingBarColor(R.color.yellowcolorPrimary)
                .playstoreUrl("YOUR_URL")
                .onThresholdCleared(new RatingDialog.Builder.RatingThresholdClearedListener() {
                    @Override
                    public void onThresholdCleared(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                        //do something
                        ratingDialog.dismiss();
                    }
                })
                .onThresholdFailed(new RatingDialog.Builder.RatingThresholdFailedListener() {
                    @Override
                    public void onThresholdFailed(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                        //do something
                        ratingDialog.dismiss();
                    }
                })
                .onRatingChanged(new RatingDialog.Builder.RatingDialogListener() {
                    @Override
                    public void onRatingSelected(float rating, boolean thresholdCleared) {

                    }
                })
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {

                    }
                }).build();

              // ratingDialog.show();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        supportFinishAfterTransition();
        return true;
    }


    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    public void getJournalDetails (){

      tvJournalDate.setText(journalEntry.getJournalDate());
      tvJournalLocation.setText(journalEntry.getJournalLocation());
      tvJournalWeather.setText(journalEntry.getJournalWeather());
      tvJournalMood.setText(journalEntry.getJournalMood());
      if(journalEntry.getJournalWeather().equalsIgnoreCase("Sunny")){
          Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sunny);
          ivJournalWeather.setImageBitmap(bitmap);
      } else if(journalEntry.getJournalWeather().equalsIgnoreCase("Cloudy")){
          Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_cloudy);
          ivJournalWeather.setImageBitmap(bitmap);
      }else if(journalEntry.getJournalWeather().equalsIgnoreCase("Rainy")){
          Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_rainy);
          ivJournalWeather.setImageBitmap(bitmap);
      } else if(journalEntry.getJournalWeather().equalsIgnoreCase("Windy")){
          Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_windy);
          ivJournalWeather.setImageBitmap(bitmap);
      }else if(journalEntry.getJournalWeather().equalsIgnoreCase("Snowy")){
          Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_snowy);
          ivJournalWeather.setImageBitmap(bitmap);
      }else if(journalEntry.getJournalWeather().equalsIgnoreCase("Foggy")){
          Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_foggy);
          ivJournalWeather.setImageBitmap(bitmap);
      } else{

      }

        if(journalEntry.getJournalMood().equalsIgnoreCase("Happy")){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_happy);
            ivJournalMood.setImageBitmap(bitmap);
        } else if(journalEntry.getJournalMood().equalsIgnoreCase("Sad")){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_sad);
            ivJournalMood.setImageBitmap(bitmap);
        }else if(journalEntry.getJournalMood().equalsIgnoreCase("Amused")){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_surprised);
            ivJournalMood.setImageBitmap(bitmap);
        } else if(journalEntry.getJournalMood().equalsIgnoreCase("Dreamy")){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_dreamy);
            ivJournalMood.setImageBitmap(bitmap);
        }else if(journalEntry.getJournalMood().equalsIgnoreCase("Mysterious")){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mysterious);
            ivJournalMood.setImageBitmap(bitmap);
        }else if(journalEntry.getJournalMood().equalsIgnoreCase("Romantic")){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_romantic);
            ivJournalMood.setImageBitmap(bitmap);
        } else{

        }

      //filterView.setColorFilter(getResources().getColor(R.color.filter), PorterDuff.Mode.DARKEN);

      tvJournalTitle.setText(journalEntry.getJournalTitle());
      tvJournalMessage.setText(journalEntry.getJournalMessage());
        try {
            if (journalEntry.getJournalImagePath().get(0) != null) {
               // ivJournalPhoto.setColorFilter(getResources().getColor(R.color.filter), PorterDuff.Mode.DARKEN);
                Picasso.with(DisplayJournal.this).load(journalEntry.getJournalImagePath().get(0)).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_vectorjournal).error(R.drawable.ic_offline).into(ivJournalPhoto, new Callback() {
                    @Override
                    public void onSuccess() {
                       // supportPostponeEnterTransition();
                       // scheduleStartPostponedTransition(ivJournalPhoto);
                        supportStartPostponedEnterTransition();

                    }

                    @Override
                    public void onError() {
                        Picasso.with(DisplayJournal.this).load(journalEntry.getJournalImagePath().get(0))
                                .placeholder(R.drawable.ic_vectorjournal).error(R.drawable.ic_offline).into(ivJournalPhoto, new Callback() {
                            @Override
                            public void onSuccess() {

                               // scheduleStartPostponedTransition(ivJournalPhoto);
                                supportStartPostponedEnterTransition();
                            }

                            @Override
                            public void onError() {
                                Log.v("Picasso","Could not fetch image");
                                supportStartPostponedEnterTransition();
                            }
                        });
                    }
                });
            } else {
                Picasso.with(DisplayJournal.this).load(R.drawable.ic_vectorjournal).placeholder(R.drawable.ic_vectorjournal).error(R.drawable.ic_offline).into(ivJournalPhoto);
                supportStartPostponedEnterTransition();
               // scheduleStartPostponedTransition(ivJournalPhoto);
            }
        }
         catch (Exception e){
                Picasso.with(DisplayJournal.this).load(R.drawable.ic_vectorjournal).placeholder(R.drawable.ic_vectorjournal).error(R.drawable.ic_offline).into(ivJournalPhoto);
                supportStartPostponedEnterTransition();
                //scheduleStartPostponedTransition(ivJournalPhoto);
            }
      journalKey = journalEntry.getKey();

       // if(entryToolbar != null){
        //getSupportActionBar().setHomeAsUpIndicator(changeBackArrowColor(this, journalEntry.getIconColor()));
       // supportInvalidateOptionsMenu();
      // }

        }
    public void loadBitmap(String url) {

        if (loadtarget == null) loadtarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // do something with the Bitmap
                // handleLoadedBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        };

        Picasso.with(this).load(url).into(loadtarget);
    }


    private class ImageLoadAsyncTask extends AsyncTask<String, Void, Bitmap> {
        protected void onPreExecute() {
            // Runs on the UI thread before doInBackground
            // Good for toggling visibility of a progress indicator

        }

        protected Bitmap doInBackground(String... strings) {
            // Some long-running task like downloading an image.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadBitmap(strings[0]);
                }
            });

            return null;
        }



        protected void onPostExecute(Bitmap result) {

        }
    }

    public Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }

    public static Drawable changeBackArrowColor(Context context, int color) {
        String resName;
        int res;

        resName = Build.VERSION.SDK_INT >= 23 ? "abc_ic_ab_back_material" : "abc_ic_ab_back_mtrl_am_alpha";
        res = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());

        final Drawable upArrow = context.getResources().getDrawable(res);
        upArrow.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        return upArrow;
    }




    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            supportStartPostponedEnterTransition();
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
    }

    public void deleteJournal(){

        AlertDialog.Builder builder = new AlertDialog.Builder(DisplayJournal.this);
        builder.setTitle(getString(R.string.label_delete_journal));
        builder.setMessage(getString(R.string.description_delete_journal));

        String positiveText = getString(android.R.string.ok);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss alert dialog, update preferences with game score and restart play fragment
                        mDatabase.child(journalKey).removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                TastyToast.makeText(getApplicationContext(), "Deleted successfully", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
                                Intent intent = new Intent(DisplayJournal.this, MainActivity.class);
                                dialog.dismiss();
                                startActivity(intent);
                            }
                        });
                        Log.d("myTag", "positive button clicked");

                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // dismiss dialog, start counter again
                        dialog.dismiss();
                        Log.d("myTag", "negative button clicked");
                    }
                });

        AlertDialog dialog = builder.create();
       // display dialog
        dialog.show();



    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setCount(this, Integer.toString(journalEntry.getJournalImagePath().size()));
        return  true;
    }

    public void setCount(Context context, String count) {
        MenuItem menuItem = mMenuItem;
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_group_count);
        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_group_count, badge);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.display_journal_menu, menu);
        mMenuItem = menu.findItem(R.id.ic_photosize);
        if(journalEntry != null) {

            if (ivJournalPhoto != null) {
                for (int i = 0; i < menu.size(); i++) {
                    Drawable drawable = menu.getItem(i).getIcon();
                    if (drawable != null) {
                        //drawable.mutate();
                      //  drawable.setColorFilter(getResources().getColor(journalEntry.getIconColor()), PorterDuff.Mode.SRC_ATOP);
                    }
                }
            }

        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            // launch settings activity
            Intent intent = new Intent(DisplayJournal.this, EditJournalEntry.class);
            intent.putExtra("isPremium", isPremiumUser);
            intent.putExtra("journalEntry", journalEntry);
            startActivity(intent);

            if(isPremiumUser == false) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            } else{
                Log.d("Premium", "user is premium.");
            }
            return true;
        } else if(id == R.id.action_delete){
           deleteJournal();
           return true;

        } else  if(id == R.id.ic_photosize){

            if(journalEntry.getJournalImagePath().size() !=0 ) {
                ArrayList<String> arrimageFile = new ArrayList<>();
                arrimageFile.addAll(journalEntry.getJournalImagePath());
                Intent intent = new Intent(DisplayJournal.this, ImageSliderActivity.class);
                // intent.putExtra("displayjournal", journalEntry.getJournalImagePath()));
                intent.putStringArrayListExtra("imagefiles", arrimageFile);
                startActivity(intent);
                return true;
            } else {

            }
        }

        else{

        }
        return super.onOptionsItemSelected(item);
    }

    public  void loadWidgetColors(SharedPreferences sharedPreferences){
        String selectedFont = sharedPreferences.getString(getString(R.string.key_uiThemeFont), "6");
        if(selectedFont.equals("0")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.parisienneregular);
            FontUtils fontUtils = new FontUtils();
           // fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
            fontUtils.applyFontToView(tvJournalMood, myCustomFont);
            fontUtils.applyFontToView(tvJournalWeather, myCustomFont);
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);
            tvJournalDate.setTextSize(10f);

            tvJournalDate.setTextSize(14f);
        } else if(selectedFont.equals("1")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.patrick_hand_sc);
            FontUtils fontUtils = new FontUtils();
           // fontUtils.applyFontToToolbar(entryToolbar, myCustomFont);
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
            fontUtils.applyFontToView(tvJournalMood, myCustomFont);
            fontUtils.applyFontToView(tvJournalWeather, myCustomFont);
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);

        } else if( selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.sofadi_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
            fontUtils.applyFontToView(tvJournalMood, myCustomFont);
            fontUtils.applyFontToView(tvJournalWeather, myCustomFont);
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);

        }else if(selectedFont.equals("3")){
            FontUtils fontUtils = new FontUtils();
            Typeface myCustomFont = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
            fontUtils.applyFontToView(tvJournalMood, myCustomFont);
            fontUtils.applyFontToView(tvJournalWeather, myCustomFont);
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);

        }
        else if( selectedFont.equals("4")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.concert_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
            fontUtils.applyFontToView(tvJournalMood, myCustomFont);
            fontUtils.applyFontToView(tvJournalWeather, myCustomFont);
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);
            tvJournalDate.setTextSize(12f);


        }
        else if( selectedFont.equals("5")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.oleo_script);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
            fontUtils.applyFontToView(tvJournalMood, myCustomFont);
            fontUtils.applyFontToView(tvJournalWeather, myCustomFont);
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);

        }
        else if( selectedFont.equals("6")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.pt_sans_narrow);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
            fontUtils.applyFontToView(tvJournalMood, myCustomFont);
            fontUtils.applyFontToView(tvJournalWeather, myCustomFont);
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);



        }  else if( selectedFont.equals("7")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.roboto_condensed_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
            fontUtils.applyFontToView(tvJournalMood, myCustomFont);
            fontUtils.applyFontToView(tvJournalWeather, myCustomFont);
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);

        }  else if( selectedFont.equals("8")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.shadows_into_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
            fontUtils.applyFontToView(tvJournalMood, myCustomFont);
            fontUtils.applyFontToView(tvJournalWeather, myCustomFont);
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);

        } else if( selectedFont.equals("9")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.slabo_13px);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToView(tvJournalDate, myCustomFont);
            fontUtils.applyFontToView(tvJournalLocation, myCustomFont);
            fontUtils.applyFontToView(tvJournalMood, myCustomFont);
            fontUtils.applyFontToView(tvJournalWeather, myCustomFont);
            fontUtils.applyFontToView(tvJournalTitle, myCustomFont);
            fontUtils.applyFontToView(tvJournalMessage, myCustomFont);

        }
        else {

        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //prefMethods.changeListenerTheme(sharedPreferences, this);
        if(key.equals(getResources().getString(R.string.key_uiThemeFont))){
            loadWidgetColors(sharedPreferences);
            DisplayJournal.this.recreate();
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
        super.onSaveInstanceState(outState);
        outState.putParcelable("JournalParcelable", journalEntry);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        journalEntry = savedInstanceState.getParcelable("JournalParcelable");
        getJournalDetails();
    }


    private void getUserData(DatabaseReference mdefaultdatabase) {
        Boolean isactive = false;

        mdefaultdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    SubscribersModelActivity subscribersModel = dataSnapshot.getValue(SubscribersModelActivity.class);
                    if(subscribersModel != null){
                        if(subscribersModel.isActiveMember().equals("true")){
                            isPremiumUser = true;
                        } else
                        {
                            isPremiumUser = false;
                        }
                    }
                    //subscribersList.add(subscribersModel);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
