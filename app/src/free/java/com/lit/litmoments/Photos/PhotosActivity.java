package com.lit.litmoments.Photos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.libraries.places.api.internal.impl.net.pablo.PlaceResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lit.litmoments.AddJournal.JournalEntryModel;
import com.lit.litmoments.R;

import java.util.ArrayList;
import java.util.List;

public class PhotosActivity extends Fragment implements PhotosAdapter.OnItemClickListener {

    private PhotosAdapter mPhotosAdapter;
    private List<PhotoModel> mPhotoModelList;
    private List<JournalEntryModel>  mJournalList;
    private RecyclerView mRecyclerview;

    private DatabaseReference mDatabase;
    public static final String DATABASE_UPLOADS = "User's Journal Entries";
    private FirebaseAuth mAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_photos, container, false);
        mRecyclerview = view.findViewById(R.id.rvPhotos);
        mPhotoModelList = new ArrayList<>();
        mJournalList = new ArrayList<>();

        FirebaseApp.initializeApp(getActivity().getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            //  Toast.makeText(MainActivity.this, "Welcome back "+" "+ mAuth.getCurrentUser().getDisplayName()+" ", Toast.LENGTH_SHORT).show();
        }

        String currentUid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        mDatabase = firebaseDatabase.getReference(DATABASE_UPLOADS).child(currentUid);


        mPhotosAdapter = new PhotosAdapter(getContext(), mPhotoModelList, this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerview.setNestedScrollingEnabled(false);
        mRecyclerview.setLayoutManager(layoutManager);
        mRecyclerview.setAdapter(mPhotosAdapter);

        getPhotos();

        return view;
    }

    private void getPhotos() {


        //final ProgressDialog progressDialog = new ProgressDialog(this);
        //progressDialog.setMessage("Please wait...........");
        // progressDialog.setTitle("Retrieving data");
        // progressDialog.show();
        //progressBar.setVisibility(View.VISIBLE);
        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mJournalList.clear();
                mPhotoModelList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                        JournalEntryModel journalEntryModel = npsnapshot.getValue(JournalEntryModel.class);
                         if(journalEntryModel.getJournalImagePath().size() > 0){
                             for (String imageUrl : journalEntryModel.getJournalImagePath()){
                                 PhotoModel photoModel = new PhotoModel();
                                 photoModel.setImageUrl(imageUrl);
                                 photoModel.setJournalKey(journalEntryModel.getKey());
                                 mPhotoModelList.add(photoModel);
                                 Log.d("photoSize", " " + mPhotoModelList.size());
                                 mPhotosAdapter.notifyDataSetChanged();
                             }
                         }
                        //mJournalList.add(journalEntryModel);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onItemClick(PhotoModel photoModel) {

    }
}
