package fi.oulu.mobisocial.sandop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    //declaration of variables for layout elements
    RelativeLayout profileContent;
    ProgressBar profileLoading;
    TextView tvProfileName;
    ImageView mainUserpic, imgProfileBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Firebase authentication object declaration
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        //attaching layout elements to variables
        profileContent = (RelativeLayout) findViewById(R.id.profileContent);
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

}