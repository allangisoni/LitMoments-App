package com.lit.litmoments;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.sdsmdg.tastytoast.TastyToast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpdateSubscriptionDetailsService extends IntentService {

    Context context;
    public static final String ACTION = "com.lit.litmoments.UpdateSubscriptionDetailsService";

    private BillingClient billingClient;
    private PurchasesUpdatedListener purchasesUpdatedListener;

    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    String isAcknowlege =" ";

    private static  final String SUBSCRIBER_UPLOADS = "Subscription Details";


   public UpdateSubscriptionDetailsService(){
       super("UpdateSubscriptionDetails");
   }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //startForeground(1,new Notification());


        FirebaseApp.initializeApp(context);

        mAuth = FirebaseAuth.getInstance();
        String currentUid = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference(SUBSCRIBER_UPLOADS).child(currentUid);

        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

            }
        };
        billingClient = BillingClient.newBuilder(this).setListener(purchasesUpdatedListener).enablePendingPurchases().build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });


    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

       String skuproduct = "monthlysub";


      billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS, new PurchaseHistoryResponseListener() {
          @Override
          public void onPurchaseHistoryResponse(BillingResult billingResult, List<PurchaseHistoryRecord> purchaseHistoryRecordList) {
              if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                      && purchaseHistoryRecordList != null) {

                  PurchaseHistoryRecord purchase = purchaseHistoryRecordList.get(0);

                  //Log.d("premium", purchase.getDeveloperPayload());

                  SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");

                      mDatabase.addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              if(dataSnapshot.exists()){
                                  for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                                      SubscribersModelActivity subscribersModel = snapshot.getValue(SubscribersModelActivity.class);


                                      try {
                                          if (format.parse(convertTime(purchase.getPurchaseTime())).after(format.parse(subscribersModel.getPurchaseDate()))){

                                              String purchaseTime = convertTime(purchase.getPurchaseTime());
                                              String expiryTime =" ";
                                              String product = purchase.getSku();

                                              if(product.equals("monthlysub")){
                                                  //expiryTime = purchaseTime + 2678400000L;
                                                  expiryTime =calculateExpiryTime(purchase.getPurchaseTime());
                                              }
                                              else {
                                                  expiryTime =calculateExpiryTime(purchase.getPurchaseTime());
                                                  // expiryTime = purchaseTime + 31708800000L;
                                              }

                                              SubscribersModelActivity subscribersModelActivity;
                                              subscribersModelActivity= new SubscribersModelActivity("true",purchaseTime,expiryTime, product);
                                              DatabaseReference databaseReference = mDatabase;
                                              databaseReference.setValue(subscribersModelActivity);

                                              isAcknowlege = "true";
                                          }
                                      }catch (Exception e){

                                      }
                                  }
                              }

                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      });




                  } else if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchaseHistoryRecordList == null){

                  PurchaseHistoryRecord purchase = purchaseHistoryRecordList.get(0);
                  mDatabase.addValueEventListener(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                          if(dataSnapshot.exists()){
                              for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                                  SubscribersModelActivity subscribersModel = snapshot.getValue(SubscribersModelActivity.class);



                                          SubscribersModelActivity subscribersModelActivity;
                                          subscribersModelActivity= new SubscribersModelActivity(null, null, null, null);
                                          DatabaseReference databaseReference = mDatabase;
                                          databaseReference.setValue(subscribersModelActivity);

                                          isAcknowlege = "true";

                                  }
                          }

                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });

              } else {

              }
          }
      });



        /**purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && purchases != null) {
                    for (Purchase purchase : purchases) {
                        //  handlePurchase(purchase);

                        SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");

                        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){

                            mDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {

                                            SubscribersModelActivity subscribersModel = snapshot.getValue(SubscribersModelActivity.class);


                                            try {
                                                if (format.parse(convertTime(purchase.getPurchaseTime())).after(format.parse(subscribersModel.getPurchaseDate()))){

                                                    String purchaseTime = convertTime(purchase.getPurchaseTime());
                                                    String expiryTime =" ";
                                                    String product = purchase.getSku();
                                                    if(product.equals("monthlysub")){
                                                        //expiryTime = purchaseTime + 2678400000L;
                                                        expiryTime =calculateExpiryTime(purchase.getPurchaseTime());
                                                    }
                                                    else {
                                                        expiryTime =calculateExpiryTime(purchase.getPurchaseTime());

                                                        // expiryTime = purchaseTime + 31708800000L;
                                                    }

                                                    SubscribersModelActivity subscribersModelActivity;
                                                    subscribersModelActivity= new SubscribersModelActivity(true,purchaseTime,expiryTime, product);
                                                    DatabaseReference databaseReference = mDatabase;
                                                    databaseReference.setValue(subscribersModelActivity);

                                                    isAcknowlege = "true";
                                                }
                                            }catch (Exception e){

                                            }
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });





                            if (!purchase.isAcknowledged()) {
                                AcknowledgePurchaseParams acknowledgePurchaseParams =
                                        AcknowledgePurchaseParams.newBuilder()
                                                .setPurchaseToken(purchase.getPurchaseToken())
                                                .build();
                                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);

                                String purchaseTime = convertTime(purchase.getPurchaseTime());
                                String expiryTime =" ";
                                String product = purchase.getSku();
                                if(product.equals("monthlysub")){
                                    //expiryTime = purchaseTime + 2678400000L;
                                    expiryTime =calculateExpiryTime(purchase.getPurchaseTime());
                                }
                                else {
                                    expiryTime =calculateExpiryTime(purchase.getPurchaseTime());

                                    // expiryTime = purchaseTime + 31708800000L;
                                }

                                SubscribersModelActivity subscribersModelActivity;
                                subscribersModelActivity= new SubscribersModelActivity(true,purchaseTime,expiryTime, product);
                                DatabaseReference databaseReference = mDatabase;
                                databaseReference.setValue(subscribersModelActivity);

                                isAcknowlege = "true";

                            }
                        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                            // Here you can confirm to the user that they've started the pending
                            // purchase, and to complete it, they should follow instructions that
                            // are given to them. You can also choose to remind the user in the
                            // future to complete the purchase if you detect that it is still
                            // pending.
                        }

                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                } else {
                    // Handle any other error codes.
                }
            }
        };
        // billingClient = BillingClient.newBuilder(this).setListener(purchasesUpdatedListener).enablePendingPurchases().build();

        acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

                //getMessage("Purchase acknowledged");
                // TastyToast.makeText(getApplicationContext(), "You are now a premium member...Thanks!!!", TastyToast.LENGTH_SHORT, TastyToast.INFO);


            }

        };  **/





        Intent in = new Intent(ACTION);
        // Put extras into the intent as usual
        in.putExtra("resultCode", Activity.RESULT_OK);
        in.putExtra("istoAcknowlege",isAcknowlege);

        // Fire the broadcast with intent packaged
        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
    }


    public void destroy() {
        billingClient.endConnection();
    }

    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }

    public String calculateExpiryTime(long time){
        Date date = new Date(time);
        Calendar calendarInstance = Calendar.getInstance();
        calendarInstance.setTime(date);
        calendarInstance.add(Calendar.DATE, 31);
        date = calendarInstance.getTime();
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }

}
