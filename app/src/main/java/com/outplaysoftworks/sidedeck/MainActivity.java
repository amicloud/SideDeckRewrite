package com.outplaysoftworks.sidedeck;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String SKU_ONE_TIME = "support_one_time_1";
    private static final String DEVELOPER_PAYLOAD = "310212447F8B2767D872F433FBF2A98DA83F2A1C";
    private static final String SKU_RECURRING = "support_recurring_1";
    private static boolean IS_BETA_RELEASE = true;

    public static CalculatorFragment mCalculatorFragment;
    public static LogFragment mLogFragment;
    private ViewPager mViewPager;
    private static Context context;
    private static PopupMenu popupMenu;
    public static SharedPreferences sharedPreferences;

    private GoogleApiClient client;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference databaseReference = database.getReference();
    private FirebaseAnalytics mFirebaseAnalytics;

    IInAppBillingService mService;
    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCalculatorFragment = new CalculatorFragment();
        mLogFragment = new LogFragment();
        context = this;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        IS_BETA_RELEASE = checkIfIsBeta();

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        assert mViewPager != null;
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //Setup the tab layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(mViewPager);
        trackAppOpens();
        checkFirebaseForUpdate();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void trackAppOpens() {
        int timesOpened = sharedPreferences.getInt("TIMES_OPENED", 0); //NON-NLS
        timesOpened++;
        sharedPreferences.edit().putInt("TIMES_OPENED", timesOpened).apply();//NON-NLS
        boolean shouldAskUser = sharedPreferences.getBoolean("KEY_ASK_USER_TO_RATE", true);//NON-NLS
        if (timesOpened > 10 && shouldAskUser) {
            askUserToRate();
        }
        boolean shouldAskUserForSupport = sharedPreferences.getBoolean("KEY_ASK_USER_TO_SUPPORT", true);//NON-NLS
        if (timesOpened > 15 && shouldAskUserForSupport) {
            askUserToSupport();
        }
    }

    private void askUserToSupport() {
        new AlertDialog.Builder(this, R.style.MyDialog)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.supportDeveloper)
                .setMessage(R.string.supportDeveloperMessage)
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    takeUserToSupportPurchase();
                    sharedPreferences.edit().putBoolean("KEY_ASK_USER_TO_SUPPORT", false).apply();//NON-NLS
                })
                .setNegativeButton(getString(R.string.noThanks), (dialog, which) -> {
                    sharedPreferences.edit().putBoolean("KEY_ASK_USER_TO_SUPPORT", false).apply();//NON-NLS
                })
                .show();
    }

    private synchronized void takeUserToSupportPurchase() {
        ArrayList<String> skuList = new ArrayList<>();
        skuList.add("support_one_time_1");
        skuList.add("support_recurring_1");
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        Thread networkThread1 = new Thread(() -> {
            try {
                Bundle skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
                int response = skuDetails.getInt("RESPONSE_CODE");
                if (response == 0) {
                    ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                    assert responseList != null;
                    for (String thisResponse : responseList) {
                        JSONObject object = new JSONObject(thisResponse);
                        String sku = object.getString("productId");
                        String price = object.getString("price");
                    }
                    new AlertDialog.Builder(this, R.style.MyDialog)
                            .setIcon(android.R.drawable.ic_secure)
                            .setTitle("Thanks! How would you like to donate?")
                            .setMessage("You can donate one time, or if you are feeling more generous" +
                                    "you can go monthly!")
                            .setPositiveButton("Monthly!", (dialog, which) -> purchaseMonthlySubscription())
                            .setNeutralButton("Just once.", (dialog, which) -> purchaseOneTime())
                            /*.setNegativeButton("Actually, never mind.", (dialog, which) ->{
                                //Cancel
                            })*/
                            .show();
                }
            } catch (RemoteException | JSONException e) {
                e.printStackTrace();
            }
        });
        if(mService != null) {
            networkThread1.run();
        } else{
            Thread wtfThread = new Thread(() -> {
                while (mService == null){
                    try {
                        wait(200L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                networkThread1.run();
            });
            wtfThread.run();
        }
    }

    private synchronized void  purchaseOneTime() {
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), SKU_ONE_TIME, "inapp",DEVELOPER_PAYLOAD);//NON-NLS
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            if (pendingIntent != null) {
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
            } else {
                Log.d("Purchase one time: ", "pendingIntent is null wtf");//NON-NLS
            }

        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("Purchase one time: ", "Remote exception wtf look at e");//NON-NLS
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            Log.d("Purchase one time: ", "Some intent error, check e");//NON-NLS
        }
    }

    private synchronized void purchaseMonthlySubscription() {
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), SKU_RECURRING, "subs",DEVELOPER_PAYLOAD);//NON-NLS
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            if (pendingIntent != null) {
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), 0, 0, 0);
            } else {
                Log.d("Purchase recurring: ", "pendingIntent is null wtf");//NON-NLS
            }

        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d("Purchase recurring: ", "Remote exception wtf look at e");//NON-NLS
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            Log.d("Purchase recurring: ", "Some intent error, check e");//NON-NLS
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    Toast toast = new Toast(context);
                    toast.setText("Thanks!");
                    toast.setDuration(Toast.LENGTH_SHORT);
                }
                catch (JSONException e) {
                    Log.d("On Activity result: ","Failed to parse purchase data.");
                    e.printStackTrace();
                }
            }
        }
    }

    private void askUserToRate() {
        new AlertDialog.Builder(this, R.style.MyDialog)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.rateMe)
                .setMessage(R.string.rateMeMessage)
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    takeUserToPlayStore();
                    sharedPreferences.edit().putBoolean("KEY_ASK_USER_TO_RATE", false).apply();//NON-NLS
                })
                .setNegativeButton(getString(R.string.noThanks), (dialog, which) -> {
                    sharedPreferences.edit().putBoolean("KEY_ASK_USER_TO_RATE", false).apply();//NON-NLS
                })
                .show();
    }

    private boolean checkIfIsBeta() {
        if (BuildConfig.VERSION_NAME.contains("b") || BuildConfig.VERSION_NAME.contains("B")) { //NON-NLS
            Log.d("BETA: ", "YOU ARE CURRENTLY USING A BETA RELEASE"); //NON-NLS
            return true;
        } else {
            return false;
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            switch (position) {
                case 0:
                    return mCalculatorFragment;
                case 1:
                    return mLogFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getText(R.string.calculator);
                case 1:
                    return getResources().getText(R.string.duelLog);
            }
            return null;
        }
    }

    @SuppressWarnings("unused")
    public void showPopupMenu(View view) {
        final Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        popupMenu = new PopupMenu(context, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.actions, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuItemUndo:
                    mCalculatorFragment.onUndoClicked();
                    break;
                case R.id.menuItemReset:
                    mCalculatorFragment.onResetClick();
                    break;
                case R.id.menuItemSettings:
                    startActivity(preferencesIntent);
                    break;
                case R.id.menuItemCalculator:
                    mCalculatorFragment.showCalculator();
                    break;
            }
            return false;
        });
    }

    private void checkFirebaseForUpdate() {
        Log.d("DB: ", "CHECKING FOR NEW VERSION"); //NON-NLS
        databaseReference.child("currentRelease").addListenerForSingleValueEvent(new ValueEventListener() { //NON-NLS
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Version version = dataSnapshot.getValue(Version.class);
                onGotNewVersion(version);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FireBase", " couldn't get version information"); //NON-NLS
            }
        });
        if (IS_BETA_RELEASE) {
            databaseReference.child("currentBeta").addListenerForSingleValueEvent(new ValueEventListener() { //NON-NLS
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Version version = dataSnapshot.getValue(Version.class);
                    onGotNewVersionBeta(version);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("FireBase", " couldn't get version information"); //NON-NLS
                }
            });
        }
    }

    private void onGotNewVersionBeta(Version newVersion) {
        Version localVersion = new Version(System.currentTimeMillis() / 1000L, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME);
        Log.d("Got version: ", "newest beta version is " + newVersion.getVersionCode()); //NON-NLS
        //Log.d("Got version: ", "current beta is " + newVersion.getVersionCode());
        int remoteVersionCode = newVersion.getVersionCode();
        String remoteVersionName = newVersion.getVersionName();
        Long remoteVersionAvailableAt = newVersion.getAvailableAt();
        Log.d("FB: ", "Remote avail: " + remoteVersionAvailableAt.toString() + ", local time: " + System.currentTimeMillis() / 1000L); //NON-NLS
        if (localVersion.getVersionCode() < remoteVersionCode &&
                System.currentTimeMillis() / 1000L > remoteVersionAvailableAt) {
            Bundle bundle = new Bundle();
            bundle.putString("got_new_version", remoteVersionName); //NON-NLS
            bundle.putString("old_version", BuildConfig.VERSION_NAME); //NON-NLS
            new AlertDialog.Builder(this, R.style.MyDialog)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(R.string.newVersionAvailable)
                    .setMessage(getString(R.string.newestVersionIs) + remoteVersionName + "\n" +
                            getString(R.string.youHaveVersion) + BuildConfig.VERSION_NAME + "\n" +
                            getString(R.string.goToPlayStore))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        takeUserToPlayStore();
                        bundle.putString("user_accepted", "yes");//NON-NLS
                        mFirebaseAnalytics.logEvent("beta_user_checked_update", bundle);//NON-NLS
                    })
                    .setNegativeButton(getString(R.string.noThanks), (dialog, which) -> {
                        bundle.putString("user_accepted", "no");//NON-NLS
                        mFirebaseAnalytics.logEvent("beta_user_checked_update", bundle);//NON-NLS
                    })
                    .show();
        }
    }

    private void onGotNewVersion(Version newVersion) {
        Version localVersion = new Version(System.currentTimeMillis() / 1000L, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME);
        Log.d("Got version: ", "newest release version: " + newVersion.getVersionCode()); //NON-NLS
        //Log.d("Got version: ", "current beta is " + newVersion.getVersionCode());
        int remoteVersionCode = newVersion.getVersionCode();
        String remoteVersionName = newVersion.getVersionName();
        Long remoteVersionAvailableAt = newVersion.getAvailableAt();
        if (localVersion.getVersionCode() < remoteVersionCode &&
                System.currentTimeMillis() / 1000L > remoteVersionAvailableAt) {
            Bundle bundle = new Bundle();
            bundle.putString("got_new_version", remoteVersionName);//NON-NLS
            bundle.putString("old_version", BuildConfig.VERSION_NAME);//NON-NLS
            new AlertDialog.Builder(this, R.style.MyDialog)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(getString(R.string.newVersionAvailable))
                    .setMessage(getString(R.string.newestVersionIs) + remoteVersionName + "\n" +
                            getString(R.string.youHaveVersion) + BuildConfig.VERSION_NAME + "\n" +
                            getString(R.string.goToPlayStore))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        takeUserToPlayStore();
                        bundle.putString("user_accepted", "yes");//NON-NLS
                        mFirebaseAnalytics.logEvent("user_checked_update", bundle);//NON-NLS
                    })
                    .setNegativeButton(getString(R.string.noThanks), (dialog, which) -> {
                        bundle.putString("user_accepted", "no");//NON-NLS
                        mFirebaseAnalytics.logEvent("user_checked_update", bundle);//NON-NLS
                    })
                    .show();
        }
    }

    private void takeUserToPlayStore() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName))); //NON-NLS
        } catch (ActivityNotFoundException ActivityNotFound) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName))); //NON-NLS
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (mCalculatorFragment.holderCalculator.getVisibility() == View.VISIBLE) {
                mCalculatorFragment.holderCalculator.setVisibility(View.GONE);
            } else if (mCalculatorFragment.holderTimer.getVisibility() == View.VISIBLE) {
                mCalculatorFragment.holderTimer.setVisibility(View.GONE);
            } else if (mCalculatorFragment.player1Name.isFocused()) {
                mCalculatorFragment.player1Name.clearFocus();
            } else if (mCalculatorFragment.player2Name.isFocused()) {
                mCalculatorFragment.player2Name.clearFocus();
            } else {
                super.onBackPressed();
            }
        } catch (NullPointerException e) {
            Log.e("On back pressed: ", "wtf"); //NON-NLS
        }
    }
    @Override
    public void onDestroy(){
        if(mService != null) {
            unbindService(mServiceConn);
        }
        super.onDestroy();
    }
}

class Version{
    private int versionCode;
    private String versionName;
    private Long availableAt;
    public int getVersionCode() {
        return versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public Long getAvailableAt(){
        return this.availableAt;
    }

    @SuppressWarnings("SameParameterValue")
    public Version(Long availableAt, int versionCode, String versionName){
        this.availableAt = availableAt;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }

    public Version(){

    }
}