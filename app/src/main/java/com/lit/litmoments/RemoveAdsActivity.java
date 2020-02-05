package com.lit.litmoments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sdsmdg.tastytoast.TastyToast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RemoveAdsActivity extends AppCompatActivity{

    @BindView(R.id.ivCancel)
    ImageView ivCancel;
    @BindView(R.id.btnMonthlySub)
    Button btnMonthlySub;
    @BindView(R.id.btnYearlySub)
    Button btnYearlySub;
    @BindView(R.id.btnBuyPremium)
    Button btnBuyPremium;

    boolean isMonthlySub = true, isYearlySub = false;
    private BillingClient billingClient;
    private PurchasesUpdatedListener purchasesUpdatedListener;

    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

    private Activity mActivity;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private DatabaseReference mDatabase;

    private static  final String SUBSCRIBER_UPLOADS = "Subscription Details";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_ads);
        ButterKnife.bind(this);

        mActivity = this;

        FirebaseApp.initializeApp(getApplicationContext());


        mAuth = FirebaseAuth.getInstance();
        String currentUid = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference(SUBSCRIBER_UPLOADS).child(currentUid);

        purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

                if (billingResult.getResponseCode() == BillingResponseCode.OK
                        && purchases != null) {
                    for (Purchase purchase : purchases) {
                      //  handlePurchase(purchase);
                        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED){


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
                                subscribersModelActivity= new SubscribersModelActivity("true",purchaseTime,expiryTime, product);
                                DatabaseReference databaseReference = mDatabase;
                                databaseReference.setValue(subscribersModelActivity);

                            }
                        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                            // Here you can confirm to the user that they've started the pending
                            // purchase, and to complete it, they should follow instructions that
                            // are given to them. You can also choose to remind the user in the
                            // future to complete the purchase if you detect that it is still
                            // pending.
                        }

                    }
                } else if (billingResult.getResponseCode() == BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                } else {
                    // Handle any other error codes.
                }
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
        setPremLayout();

        acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

                //getMessage("Purchase acknowledged");
                TastyToast.makeText(getApplicationContext(), "You are now a premium member...Thanks!!!", TastyToast.LENGTH_SHORT, TastyToast.INFO);


            }

        };
        btnMonthlySub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnYearlySub.setBackground(getResources().getDrawable(R.drawable.login_rounded));
                btnMonthlySub.setBackground(getResources().getDrawable(R.drawable.prembutton));
                btnYearlySub.setTextColor(getResources().getColor(R.color.colorAccent));
                btnBuyPremium.setText(getResources().getString(R.string.monthlySubscription));
                isMonthlySub = true;
                isYearlySub = false;
            }
        });


        btnYearlySub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnMonthlySub.setBackground(getResources().getDrawable(R.drawable.login_rounded));
                btnYearlySub.setBackground(getResources().getDrawable(R.drawable.prembutton));
                btnMonthlySub.setTextColor(getResources().getColor(R.color.colorAccent));
                btnBuyPremium.setText(getResources().getString(R.string.yearlySubscription));
                isMonthlySub= false;
                isYearlySub = true;

            }
        });


        btnBuyPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // TastyToast.makeText(getApplicationContext(), "Thanks but this feature is still under development...", TastyToast.LENGTH_SHORT, TastyToast.INFO);


               if(isMonthlySub){
                   billingClient.startConnection(new BillingClientStateListener() {
                       @Override
                       public void onBillingSetupFinished(BillingResult billingResult) {
                           if(billingResult.getResponseCode() == BillingResponseCode.OK){

                               List<String> skuList = new ArrayList<>();
                               skuList.add("monthlysub");
                              // skuList.add("yearlysub");
                               SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                               params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);

                               billingClient.querySkuDetailsAsync(params.build(),
                                       new SkuDetailsResponseListener() {
                                           @Override
                                           public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                                               // Process the result.

                                               if (billingResult.getResponseCode() == BillingResponseCode.OK && skuDetailsList != null) {
                                                   for (SkuDetails skuDetails : skuDetailsList) {
                                                       String sku = skuDetails.getSku();
                                                       String price = skuDetails.getPrice();
                                                       String subPrice = " ";

                                                           BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                                   .setSkuDetails(skuDetails)
                                                                   .build();

                                                           billingClient.launchBillingFlow( mActivity, flowParams);



                                                   }
                                               } else if(billingResult.getResponseCode() == BillingResponseCode.ITEM_ALREADY_OWNED
                                                       ||billingResult.getResponseCode() ==  BillingResponseCode.ERROR){
                                                   Log.i("BillingError", "onPurchasesUpdated: The user already owns this item");
                                               }

                                           }
                                       });
                           }
                       }

                       @Override
                       public void onBillingServiceDisconnected() {

                       }
                   });
               }
               else{

                   billingClient.startConnection(new BillingClientStateListener() {
                       @Override
                       public void onBillingSetupFinished(BillingResult billingResult) {
                           if(billingResult.getResponseCode() == BillingResponseCode.OK){

                               List<String> skuList = new ArrayList<>();
                               //skuList.add("monthlysub");
                               skuList.add("yearlysub");
                               SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                               params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);

                               billingClient.querySkuDetailsAsync(params.build(),
                                       new SkuDetailsResponseListener() {
                                           @Override
                                           public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                                               // Process the result.

                                               if (billingResult.getResponseCode() == BillingResponseCode.OK && skuDetailsList != null) {
                                                   for (SkuDetails skuDetails : skuDetailsList) {
                                                       String sku = skuDetails.getSku();
                                                       String price = skuDetails.getPrice();
                                                       String subPrice = " ";

                                                           BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                                   .setSkuDetails(skuDetails)
                                                                   .build();

                                                           billingClient.launchBillingFlow( mActivity, flowParams);

                                                   }
                                               }

                                           }
                                       });
                           }
                       }

                       @Override
                       public void onBillingServiceDisconnected() {

                       }
                   });

               }


            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();
            }
        });

    }






    private  void setPremLayout(){

        btnBuyPremium.setText(getResources().getString(R.string.monthlySubscription));
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
