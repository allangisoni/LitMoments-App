package com.lit.litmoments.Main;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajts.androidmads.fontutils.FontUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.lit.litmoments.AboutusActivity;
import com.lit.litmoments.DashboardActivity;
import com.lit.litmoments.Favorites.FavoritesFragment;
import com.lit.litmoments.Login.LoginActivity;
import com.lit.litmoments.Photos.PhotosActivity;
import com.lit.litmoments.PrivacyActivity;
import com.lit.litmoments.R;
import com.lit.litmoments.RemoveAdsActivity;
import com.lit.litmoments.Settings.SettingsActivity;
import com.lit.litmoments.TermsActivity;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;
import com.thebluealliance.spectrum.SpectrumDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class TabbedMainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener  {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int [] tabIcons = {R.drawable.ic_home, R.drawable.ic_photos, R.drawable.ic_fav};
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtUserEmail;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_SETTINGS;



    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    Toolbar toolbar;
    ImageView navImageView;

    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;

    CharSequence[] values = {" Parisienne "," Patrick Hand"," Calligraffitti ", "Sans Serif",
    "Concert One", "Oleo Script", "Pt Sans", "Roboto", "Shadows into Light", "Slabo"};

    CharSequence[] uithemevalues = {"Magical Purple", "Strawberry Red", "Innocent Pink",
    "Sunny Yellow", "Brook Blue", "Pickle Green", "Resene Tacao", "Tyrian Purple"};
    AlertDialog alertDialog;
    SharedPreferences sharedPreferences;


     Boolean isWhite = false;
     Boolean isDarkTheme = false;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loadUiTheme(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        setContentView(R.layout.activity_tabbed_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Fade fade= new Fade();
            fade.setDuration(200);
            View view = getWindow().getDecorView();
            fade.excludeTarget(view.findViewById(R.id.action_bar_container), true);
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
            // getWindow().setSharedElementExitTransition(new Explode());

        }

        //loadPreferenceFont(sharedPreferences);
        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            //setUpThemeContent();
            loadWidgetColors(sharedPreferences);
        }

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtUserEmail = navHeader.findViewById(R.id.tvEmail);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);
        //navImageView = findViewById(R.id.nav_icon);


        // load nav menu header data
        loadNavHeader();
        loadPreferenceFont(sharedPreferences);

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_SETTINGS;
            //loadHomeFragment();
        }


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setUpThemeContent();

       /** navImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!drawer.isDrawerOpen(Gravity.START)){
                  drawer.openDrawer(Gravity.START);
                } else {
                    drawer.closeDrawer(Gravity.START);
                }
            }
        }); **/
       // Intent mintent = new Intent(TabbedMainActivity.this, DashboardActivity.class);
        //mintent.putExtra("JournalList", journalList);
       // startActivity(mintent);
    }

   private void setUpTabIcons(){

       Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, tabIcons[0]);
       Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
       DrawableCompat.setTint(wrappedDrawable, Color.BLACK);
       tabLayout.getTabAt(0).setIcon(tabIcons[0]);
       tabLayout.getTabAt(1).setIcon(tabIcons[1]);
       tabLayout.getTabAt(2).setIcon(tabIcons[2]);
   }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment(), "Home");
        adapter.addFragment(new PhotosActivity(), "Photos");
        adapter.addFragment(new FavoritesFragment(), "Favorites");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //loadPreferenceFont(sharedPreferences);
        TabbedMainActivity.this.recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return mFragmentTitleList.get(position);
            return null;
        }
    }


    private void loadNavHeader() {
        // name, profile
        String userName = "", userEmail ="";
        userName = mAuth.getCurrentUser().getDisplayName();
        userEmail = mAuth.getCurrentUser().getEmail();
        txtName.setText(userName);
        txtName.setTextColor(getResources().getColor(android.R.color.white));
        txtUserEmail.setText(userEmail);
        txtUserEmail.setTextColor(getResources().getColor(android.R.color.white));

        // loading header background image
       /** Picasso.with(this).load(urlNavHeaderBg)
                .into(imgNavHeaderBg); **/
        Drawable bitmapDrawable = getResources().getDrawable(R.drawable.ic_navpic2);
        imgNavHeaderBg.setBackground(bitmapDrawable);
        Picasso headerpic = Picasso.with(this);
        headerpic.setIndicatorsEnabled(false);
        //headerpic.load(getResources().getDrawable(R.drawable.ic_navpic2))
        //        .into(imgNavHeaderBg);

        // Loading profile image
        Picasso picasso = Picasso.with(this);
        picasso.setIndicatorsEnabled(false);
        picasso.load(mAuth.getCurrentUser().getPhotoUrl())
                .transform(new RoundedCornersTransformation(12, 8))
                .into(imgProfile);

    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;

                    case R.id.nav_dash:
                        //navItemIndex = 0;
                        //CURRENT_TAG = TAG_SETTINGS;
                        //viewPager.setCurrentItem(0);
                        drawer.closeDrawer(Gravity.START);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent mintent = new Intent(TabbedMainActivity.this, DashboardActivity.class);
                            //mintent.putExtra("JournalList", journalList);
                            startActivity(mintent);
                        }
                    }, 600);
                        break;
                    case R.id.nav_theme:
                        setuiTheme();
                        break;
                    case R.id.nav_font:
                         setFontFamily();
                        break;
                  /**  case R.id.nav_removeads:
                        drawer.closeDrawer(Gravity.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(TabbedMainActivity.this, RemoveAdsActivity.class));
                            }
                        }, 600);

                        break; **/

                    case R.id.nav_about:
                        drawer.closeDrawer(Gravity.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(TabbedMainActivity.this, AboutusActivity.class));
                            }
                        }, 600);

                        break;

                    case R.id.nav_feedback:
                        drawer.closeDrawer(Gravity.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"allangisoni@gmail.com"});
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Lit moments feedback");
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    startActivity(intent);
                                } else{
                                    //
                                    Toast.makeText(TabbedMainActivity.this, "No email client installed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, 400);


                        break;

                    case R.id.nav_share: ;
                        drawer.closeDrawer(Gravity.START);
                        String shareBody = "https://play.google.com/store/apps/details?id=com.lit.litmoments.free&hl=en";
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Lit Moments");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        break;

                    case R.id.nav_rateus:
                        drawer.closeDrawer(Gravity.START);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setPlaystoreRating();
                            }
                        }, 600);
                        break;

                    case R.id.nav_termsofuse:
                        startActivity(new Intent(TabbedMainActivity.this, TermsActivity.class));
                        break;

                    case R.id.nav_privacy:
                        startActivity(new Intent(TabbedMainActivity.this, PrivacyActivity.class));
                        break;

                    case R.id.nav_signout:
                        if(mAuth.getCurrentUser() != null){
                            mAuth.signOut();

                            mGoogleSignInClient.signOut().addOnCompleteListener(TabbedMainActivity.this,
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                            Intent sintent = new Intent(TabbedMainActivity.this, LoginActivity.class);
                            sintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(sintent);
                        }
                        break;
                    default:
                        navItemIndex = 0;
                        viewPager.setCurrentItem(0);
                }

                //Checking if the item is in checked state or not, if not make it in checked state

                //menuItem.setChecked(false);

                //drawer.closeDrawer(Gravity.START);
                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer,toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                int size = navigationView.getMenu().size();
                for (int i = 0; i < size; i++) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);
        drawer.addDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }


    private void setFontFamily(){
        TextView textView = new TextView(TabbedMainActivity.this);
        textView.setText("Font Family");
        textView.setPadding(70, 30, 20, 30);
        textView.setTextSize(22F);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextColor(Color.BLACK);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(textView);
        String currentFont = sharedPreferences.getString(getString(R.string.key_uiThemeFont), "6");
        int font = Integer.parseInt(currentFont);
        builder.setSingleChoiceItems(values, font, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TabbedMainActivity.this);
                SharedPreferences.Editor prefEdit = preferences.edit();
             switch (which){

                 case 0:
                     SharedPreferences themeSharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedMainActivity.this);
                     SharedPreferences.Editor editor = themeSharedPreferences.edit();
                     editor.putString(getString(R.string.key_uiThemeFont), "0");
                     editor.commit();
                     recreate();
                     break;

                 case 1:
                     SharedPreferences themeSharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(TabbedMainActivity.this);
                     SharedPreferences.Editor editor1 = themeSharedPreferences1.edit();
                     editor1.putString(getString(R.string.key_uiThemeFont), "1");
                     editor1.commit();
                     recreate();
                     break;
                 case 2:
                     SharedPreferences themeSharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(TabbedMainActivity.this);
                     SharedPreferences.Editor editor2 = themeSharedPreferences2.edit();
                     editor2.putString(getString(R.string.key_uiThemeFont), "2");
                     editor2.commit();
                     recreate();
                     break;
                 case 3:
                     prefEdit.putString(getString(R.string.key_uiThemeFont), "3");
                     prefEdit.commit();
                     recreate();
                     break;
                 case 4:
                     prefEdit.putString(getString(R.string.key_uiThemeFont), "4");
                     prefEdit.commit();
                     recreate();
                     break;
                 case 5:
                     prefEdit.putString(getString(R.string.key_uiThemeFont), "5");
                     prefEdit.commit();
                     recreate();
                     break;
                 case 6:
                     prefEdit.putString(getString(R.string.key_uiThemeFont), "6");
                     prefEdit.commit();
                     recreate();
                     break;
                 case 7:
                     prefEdit.putString(getString(R.string.key_uiThemeFont), "7");
                     prefEdit.commit();
                     recreate();
                     break;
                 case 8:
                     prefEdit.putString(getString(R.string.key_uiThemeFont), "8");
                     prefEdit.commit();
                     recreate();
                     break;
                 case 9:
                     prefEdit.putString(getString(R.string.key_uiThemeFont), "9");
                     prefEdit.commit();
                     recreate();
                     break;
             }
             alertDialog.dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
    }
    private  void loadPreferenceFont(SharedPreferences sharedPreferences){
        String selectedFont = sharedPreferences.getString(getString(R.string.key_uiThemeFont), "6");
        if(selectedFont.equals("0")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.parisienneregular);
            txtName.setTypeface(myCustomFont);
            txtUserEmail.setTypeface(myCustomFont);
        } else if(selectedFont.equals("1")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.patrick_hand_sc);
            txtName.setTypeface(myCustomFont);
            txtUserEmail.setTypeface(myCustomFont);
        } else if( selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.sofadi_one);
            txtName.setTypeface(myCustomFont);
            txtUserEmail.setTypeface(myCustomFont);
        } else if(selectedFont.equals("3")){
            Typeface myCustomFont = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
            txtName.setTypeface(myCustomFont);
            txtUserEmail.setTypeface(myCustomFont);
        }
        else if( selectedFont.equals("4")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.concert_one);
            txtName.setTypeface(myCustomFont);
            txtUserEmail.setTypeface(myCustomFont);

        }
        else if( selectedFont.equals("5")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.oleo_script);
            txtName.setTypeface(myCustomFont);
            txtUserEmail.setTypeface(myCustomFont);;
        }
        else if( selectedFont.equals("6")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.pt_sans_narrow);
            txtName.setTypeface(myCustomFont);
            txtUserEmail.setTypeface(myCustomFont);

        }  else if( selectedFont.equals("7")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.roboto_condensed_light);
            txtName.setTypeface(myCustomFont);
            txtUserEmail.setTypeface(myCustomFont);
        }  else if( selectedFont.equals("8")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.shadows_into_light);
            txtName.setTypeface(myCustomFont);
            txtUserEmail.setTypeface(myCustomFont);
        } else if( selectedFont.equals("9")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.slabo_13px);
            txtName.setTypeface(myCustomFont);
            txtUserEmail.setTypeface(myCustomFont);
        }else {

        }
    }

    private void setuiTheme(){
        TextView textView = new TextView(TabbedMainActivity.this);
        textView.setText("Ui Theme");
        textView.setPadding(70, 30, 20, 30);
        textView.setTextSize(22F);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        textView.setTextColor(Color.BLACK);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(textView);
        String currentTheme = sharedPreferences.getString(getString(R.string.key_uiTheme), "2");
        int theme = Integer.parseInt(currentTheme);
        builder.setSingleChoiceItems(uithemevalues, theme, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TabbedMainActivity.this);
                SharedPreferences.Editor prefEdit = preferences.edit();
                switch (which){
                    case 0:
                        prefEdit.putString(getString(R.string.key_uiTheme), "0");
                        prefEdit.commit();
                        setTheme(R.style.BlueLitStyle);
                        imgNavHeaderBg.setImageResource(R.color.bluecolorPrimary);
                        break;
                    case 1:
                        prefEdit.putString(getString(R.string.key_uiTheme), "1");
                        prefEdit.commit();
                        setTheme(R.style.ReddishLitStyle);
                        break;
                    case 2:
                        prefEdit.putString(getString(R.string.key_uiTheme), "2");
                        prefEdit.commit();
                        setTheme(R.style.LitStyle);
                        break;
                    case 3:
                        prefEdit.putString(getString(R.string.key_uiTheme), "3");
                        prefEdit.commit();
                        setTheme(R.style.YellowLitStyle);
                        break;
                    case 4:
                        prefEdit.putString(getString(R.string.key_uiTheme), "4");
                        prefEdit.commit();
                        setTheme(R.style.BluishLitStyle);
                        break;
                    case 5:
                        prefEdit.putString(getString(R.string.key_uiTheme), "5");
                        prefEdit.commit();
                        setTheme(R.style.GreenishLitStyle);
                        break;
                    case 6:
                        prefEdit.putString(getString(R.string.key_uiTheme), "6");
                        prefEdit.commit();
                        setTheme(R.style.TacaoLitStyle);
                        break;
                    case 7:
                        prefEdit.putString(getString(R.string.key_uiTheme), "8");
                        prefEdit.commit();
                        setTheme(R.style.TyrianLitStyle);
                        break;

                }
                alertDialog.dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();

    }

    private void setUpThemeContent (){
        if(isWhite == true) {
            // toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            //((TextView)toolbar.getChildAt(1)).setTextColor(getResources().getColor(android.R.color.white));
            //((ImageView) toolbar.getChildAt(0)).setColorFilter(getResources().getColor(android.R.color.white));

            for (int i =0 ; i<tabIcons.length ;i++) {
                Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, tabIcons[i]);
                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                DrawableCompat.setTint(wrappedDrawable, Color.WHITE);
            }
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        } else {
            for (int i =0 ; i<tabIcons.length ;i++) {
                Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, tabIcons[i]);
                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                DrawableCompat.setTint(wrappedDrawable, Color.BLACK);
            }
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);
            toolbar.getOverflowIcon().setColorFilter(ContextCompat.getColor(this,R.color.black), PorterDuff.Mode.SRC_ATOP);
            //toolbar.getMenu().getItem(0).getIcon().setColorFilter(ContextCompat.getColor(this,R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
        if(isWhite == true && isDarkTheme== true) {
           // toolbar.setTitleTextColor(getResources().getColor(R.color.blackthemeAccent));((TextView)toolbar.getChildAt(1)).setTextColor(getResources().getColor(R.color.blackthemeAccent));
            //((ImageView) toolbar.getChildAt(0)).setColorFilter(getResources().getColor(R.color.blackthemeAccent));
           // coordinatorLayout.setBackground(getResources().getDrawable(R.drawable.maincoordinatorlayoutscrim));

        }
    }
    private void loadUiTheme (SharedPreferences sharedPreferences){

        String userTheme = sharedPreferences.getString(getResources().getString(R.string.key_uiTheme), "2");
        if (userTheme.equals("2")) {
            setTheme(R.style.LitStyle);
            isWhite = false;
            //imgNavHeaderBg.setImageResource(R.color.colorPrimary);
        }
        else if (userTheme.equals("1")) {
            setTheme(R.style.ReddishLitStyle);
            isWhite = true;
           // imgNavHeaderBg.setImageResource(R.color.reddishcolorPrimary);
        }
        else if (userTheme.equals("0")) {
            setTheme(R.style.BlueLitStyle);
            isWhite = true;
           // imgNavHeaderBg.setImageResource(R.color.bluecolorPrimary);
        }
        else if (userTheme.equals("3")) {
            setTheme(R.style.YellowLitStyle);
            isWhite=false;
            //imgNavHeaderBg.setImageResource(R.color.yellowcolorPrimary);
        } else if (userTheme.equals("4")) {
            setTheme(R.style.BluishLitStyle);
            isWhite=true;
            //imgNavHeaderBg.setImageResource(R.color.bluishcolorPrimary);
        } else if (userTheme.equals("5")) {
            setTheme(R.style.GreenishLitStyle);
            isWhite=false;
            //imgNavHeaderBg.setImageResource(R.color.greenishcolorPrimary);
        } else if (userTheme.equals("6")) {
            setTheme(R.style.TacaoLitStyle);
            isWhite=false;
           // imgNavHeaderBg.setImageResource(R.color.tacaocolorPrimary);
        }
        else if (userTheme.equals("8")) {
            setTheme(R.style.TyrianLitStyle);
            isWhite = true;
           // imgNavHeaderBg.setImageResource(R.color.tyriancolorPrimary);
        }
        else if (userTheme.equals("7")) {
            setTheme(R.style.DarkLitStyle);
            isWhite=true;
            isDarkTheme = true;
        }

    }

    private   void loadWidgetColors(SharedPreferences sharedPreferences){
        String selectedFont = sharedPreferences.getString(getString(R.string.key_uiThemeFont), "6");
        if(selectedFont.equals("0")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.parisienneregular);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
        } else if(selectedFont.equals("1")){
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.patrick_hand_sc);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
        } else if( selectedFont.equals("2")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.sofadi_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
        } else if(selectedFont.equals("3")){
            FontUtils fontUtils = new FontUtils();
            Typeface myCustomFont = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
        }
        else if( selectedFont.equals("4")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.concert_one);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);

        }
        else if( selectedFont.equals("5")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.oleo_script);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
        }
        else if( selectedFont.equals("6")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.pt_sans_narrow);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);

        }  else if( selectedFont.equals("7")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.roboto_condensed_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
        }  else if( selectedFont.equals("8")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.shadows_into_light);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
        } else if( selectedFont.equals("9")) {
            Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.slabo_13px);
            FontUtils fontUtils = new FontUtils();
            fontUtils.applyFontToToolbar(toolbar, myCustomFont);
        }else {

        }
    }

    private void setPlaystoreRating(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate Lit Moments");
        builder.setMessage("Enjoying the app? How about a rating on play store?");
        builder.setPositiveButton("Ok, sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.lit.litmoments.free")));
            }
        }).setNegativeButton("No, thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog =builder.create();
        alertDialog.show();

    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        //Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        TastyToast.makeText(this, "Press BACK again to exit", TastyToast.LENGTH_SHORT, TastyToast.INFO);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 3000);
    }


}
