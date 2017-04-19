package fi.oulu.mobisocial.sandop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    //declaration of variables for layout elements
    ImageView imgFacebook, imgGoogle;
    TextView tvSwitch, tvSocial;
    Switch switchLogin;
    EditText etLoginEmail, etLoginPassword, etSignupEmail, etSignupPassword, etSignupFirstname, etSignupSecondname;
    Button btnLogin, btnSignup;
    LinearLayout layoutLogin, layoutSignup, layoutUnitour;
    ProgressDialog mProgress;

    //Authentication objects
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Authentication elements declaration
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        //attaching layout elements to variables
        imgFacebook = (ImageView) findViewById(R.id.imgFacebook);
        imgGoogle = (ImageView) findViewById(R.id.imgGoogle);
        tvSocial = (TextView) findViewById(R.id.tvSocial);
        tvSwitch = (TextView) findViewById(R.id.tvSwitch);
        switchLogin = (Switch) findViewById(R.id.switchLogin);
        etLoginEmail = (EditText) findViewById(R.id.etLoginEmail);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);
        etSignupEmail = (EditText) findViewById(R.id.etSignupEmail);
        etSignupPassword = (EditText) findViewById(R.id.etSignupPassword);
        etSignupFirstname = (EditText) findViewById(R.id.etSignupFirstname);
        etSignupSecondname = (EditText) findViewById(R.id.etSignupSecondname);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        layoutLogin = (LinearLayout) findViewById(R.id.layoutLogin);
        layoutSignup = (LinearLayout) findViewById(R.id.layoutSignup);
        layoutUnitour = (LinearLayout) findViewById(R.id.layoutSandop);
        mProgress = new ProgressDialog(this);

        //attaching images to imageViews and applying listeners to them
        imgFacebook.setImageResource(R.drawable.ui_social_facebook);
        imgGoogle.setImageResource(R.drawable.ui_social_google);
        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "To be implemeted", Toast.LENGTH_SHORT).show();
            }
        });
        imgGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "To be implemeted", Toast.LENGTH_SHORT).show();
            }
        });

        switchLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tvSocial.setText("You can sign in with your own accounts");
                    tvSwitch.setText("I have Sandop account");
                    layoutSignup.setVisibility(View.INVISIBLE);
                    btnSignup.setVisibility(View.INVISIBLE);
                    layoutLogin.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ABOVE, R.id.layoutLogin);
                    layoutUnitour.setLayoutParams(params);

                } else {
                    tvSocial.setText("You can sign up with your own accounts");
                    tvSwitch.setText("I don't have Sandop account");
                    layoutLogin.setVisibility(View.INVISIBLE);
                    btnLogin.setVisibility(View.INVISIBLE);
                    layoutSignup.setVisibility(View.VISIBLE);
                    btnSignup.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ABOVE, R.id.layoutSignup);
                    layoutUnitour.setLayoutParams(params);
                }
            }
        });

        //attaching listener to LogIn button
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailSignup();
            }
        });

        //attaching listener to LogIn button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailLogin();
            }
        });

    }

    //method to SIGN UP with email:password
    private void emailSignup() {

        String email = etSignupEmail.getText().toString();
        String password = etSignupPassword.getText().toString();
        final String firstname = etSignupFirstname.getText().toString();
        final String secondname = etSignupSecondname.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(firstname) || TextUtils.isEmpty(secondname)) {
            Toast.makeText(LoginActivity.this, "Fill all the fields", Toast.LENGTH_SHORT).show();

        } else {
            mProgress = ProgressDialog.show(LoginActivity.this, "Please wait...",null,true,true);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        DatabaseReference currentUserDb = mDatabase.child(userId);
                        currentUserDb.child("name").setValue(firstname + " " + secondname);
                        currentUserDb.child("imageUrl").setValue("https://firebasestorage.googleapis.com/v0/b/unitour-7b1ed.appspot.com/o/userimage.png?alt=media&token=e4a941cc-26a3-4484-9c7d-642524960872");

                        Toast.makeText(LoginActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this, "Your email is already registered or your password is less than 6 symbols", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }
                }
            });
        }

    }

    //method to LOG IN with email:password
    private void emailLogin(){

        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Fill all the fields", Toast.LENGTH_SHORT).show();

        } else {
            mProgress = ProgressDialog.show(LoginActivity.this, "Please wait...",null,true,true);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}