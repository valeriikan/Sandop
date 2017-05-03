package fi.oulu.mobisocial.sandop;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import fi.oulu.mobisocial.sandop.helpers.CircleTransform;

public class ProfileActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private MyProductFragment myProductFragment;
    private FavouriteFragment favouriteFragment;

    //declaration of variables for layout elements
    LinearLayout profileContent;
    ProgressBar profileLoading;
    TextView tvProfileName;
    ImageView mainUserpic, imgProfileBackground;

    //Firebase authentication objects
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Firebase elements declaration
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.containerProfile);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabsProfile);
        tabLayout.setupWithViewPager(mViewPager);

        myProductFragment = new MyProductFragment();
        favouriteFragment = new FavouriteFragment();

        //attaching layout elements to variables
        profileContent = (LinearLayout) findViewById(R.id.profileContent);
        profileLoading = (ProgressBar) findViewById(R.id.profileLoading);
        tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        imgProfileBackground = (ImageView) findViewById(R.id.imgProfileBackground);
        imgProfileBackground.setImageResource(R.drawable.ui_profile_background);
        mainUserpic = (ImageView) findViewById(R.id.profileUserpic);

        //content loading callback
        final Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                profileLoading.setVisibility(View.GONE);
                profileContent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {
            }
        };

        //loading username and image url from database
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                tvProfileName.setText(name);
                Picasso.with(ProfileActivity.this).load(imageUrl).fit().centerCrop()
                        .transform(new CircleTransform()).into(mainUserpic, callback);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_out) {
            mAuth.signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent logout = new Intent(ProfileActivity.this, LoginActivity.class);
            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logout);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment chosenFragment = new Fragment();
            switch (position)
            {
                case 0: chosenFragment = myProductFragment;
                    break;

                case 1: chosenFragment = favouriteFragment;
                    break;
            }
            return chosenFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "My products";
                case 1:
                    return "Favourites";
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(this, MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(main);
        finish();

    }
}
