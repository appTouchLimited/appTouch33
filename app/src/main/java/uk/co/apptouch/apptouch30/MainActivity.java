package uk.co.apptouch.apptouch30;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import android.widget.Button;


import com.braintreepayments.api.BraintreeFragment;


public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private static final String TEXTVIEW_FRAGTWO_1 = "txtViewFragTwo_text1";
    private static final String IMAGEVIEW_FRAGTWO_1 = "imageViewFragTwo_image1";
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    String remoteDataImageTwo;
    String remoteDataTxtTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fetchAll();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
         //   android.app.Fragment selectedFragment = null;
            android.app.FragmentManager fragmentManager = getFragmentManager();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //selectedFragment = FragmentItemOne.newInstance();
                    //fragmentManager.beginTransaction().replace(R.id.content_frame, FragmentItemOne).commit();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentItemOne()).commit();
                    break;
                case R.id.navigation_services:

                 //   selectedFragment = FragmentItemTwo.newInstance();

                    FragmentItemTwo fragment1 = new FragmentItemTwo();
                    Bundle b1 = new Bundle();
                    b1.putString("remoteconfigText", remoteDataTxtTwo);
                    b1.putString("remoteconfigImage", remoteDataImageTwo);
                    fragment1.setArguments(b1);
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment1).commit();

                    break;
                case R.id.navigation_samples:
                   //  selectedFragment = FragmentItemThree.newInstance();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentItemThree()).commit();
                    break;
                case R.id.navigation_contact:
                  //  selectedFragment = FragmentItemFour.newInstance();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, new FragmentItemFour()).commit();
                    break;
            }

          //  FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          //  transaction.replace(R.id.content_frame, selectedFragment);
          //  transaction.commit();
            return true;
        }
    };





    public void fetchAll() {

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_default);

        long cacheExpiration = 3600;
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           /* Toast.makeText(MainActivity.this, "Fetch Succeeded",
                                    Toast.LENGTH_SHORT).show();*/
                            displayWelcomeMessage();
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(MainActivity.this, "Fetch Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void displayWelcomeMessage() {
        this.remoteDataTxtTwo = mFirebaseRemoteConfig.getString(TEXTVIEW_FRAGTWO_1);
        this.remoteDataImageTwo = mFirebaseRemoteConfig.getString(IMAGEVIEW_FRAGTWO_1);



    }




}
