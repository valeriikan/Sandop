package fi.oulu.mobisocial.sandop;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import fi.oulu.mobisocial.sandop.helpers.Product;

import static android.R.attr.cacheColorHint;
import static android.R.attr.type;

public class NewAdvertismentActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE =5000;
    private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1000;
    private String imagePath = "";

    //firebase database reference
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    //declaration of spinners
    Spinner spDep;
    Spinner spCat;
    Spinner spCity;
    Spinner spType;

    //declaration of edit texts
    EditText etName;
    EditText etPrice;
    EditText etDate;
    EditText etDesc;

    //declaration of text views
    TextView tvImagePath;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_advertisment);
        setTitle("New Advertisment");

        // getting current date to show it in DatePickerDialog
        String month, day;
        final Calendar cal = Calendar.getInstance();
        final int currentYear = cal.get(Calendar.YEAR);
        final int currentMonth = cal.get(Calendar.MONTH);
        final int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        if (currentMonth < 10) {
            month = 0 + String.valueOf(currentMonth+ 1);
        } else {
            month = String.valueOf(currentMonth + 1);
        }
        if (currentDay < 10) {
            day = 0 + String.valueOf(currentDay);
        } else {
            day = String.valueOf(currentDay);
        }

        //declaration of textviews
        tvImagePath = (TextView) findViewById(R.id.tvAdImagePath);

        //declaration of spinners
        spDep = (Spinner) findViewById(R.id.spAdDepartment);
        spCat = (Spinner) findViewById(R.id.spAdCategory);
        spCity = (Spinner) findViewById(R.id.spAdCity);
        spType = (Spinner) findViewById(R.id.spAdType);

        //declaration of edittexts
        etName = (EditText) findViewById(R.id.etAdName);
        etPrice = (EditText) findViewById(R.id.etAdPrice);
        etDesc = (EditText) findViewById(R.id.etAdDescription);
        etDate = (EditText) findViewById(R.id.etAdDate);

        //declaration of submit button and its action
        Button btnSubmit = (Button) findViewById(R.id.btnAdSubmit);
        Button btnUploadImage = (Button) findViewById(R.id.btnLoadImage);

        //click listener for available time et
        etDate.setText(day + "." + month + "." + currentYear);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(NewAdvertismentActivity.this, datePickerListener,
                        currentYear, currentMonth, currentDay);
                dialog.show();
            }
        });

        //click listener for submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spDep.getSelectedItemPosition() >= 0 && spCat.getSelectedItemPosition() >= 0 && spCity.getSelectedItemPosition() >= 0) {
                    if (!etName.getText().equals("") && !etPrice.getText().equals("") && !etDate.getText().equals("") && !etDesc.getText().equals("")) {
                        switch (spType.getSelectedItem().toString())
                        {
                            case "sell": addNewSellAdvertisment(imagePath);
                                break;
                            case "buy" : addNewBuyAdvertisment();
                                break;
                        }

                    } else
                        Toast.makeText(getApplicationContext(), "One of fields is empty! Please fill them all and try again", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Department or Category is not selected! Please specify them and try again", Toast.LENGTH_LONG).show();
            }
        });

        //click listener for Upload button
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        //adding buy or sell options to advertisement type spinner
        setTypeItems();

        //if user selects buy the upload image will be disabled
        disableUploadImage(spType, btnUploadImage);

        //load departments to department spinner
        loadDepartments();

        //load cities in Finland into city spinner
        loadCities();

        //event listener for department spinner in order to enable and load categories to category spinner
        //when user selects one specific department
        spDep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadCategories(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setTypeItems()
    {
        String[] types = {"sell","buy"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewAdvertismentActivity.this, android.R.layout.simple_spinner_item,types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(adapter);
    }

    private void loadDepartments()
    {
        final ArrayList<String> list = new ArrayList<>();
        DatabaseReference childRef = dbRef.child("items");
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    list.add(ds.getKey());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewAdvertismentActivity.this, android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spDep.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadCities()
    {
        final ArrayList<String> list = new ArrayList<>();
        DatabaseReference childRef = dbRef.child("city");

        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    list.add(ds.getValue(String.class));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewAdvertismentActivity.this, android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCity.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadCategories(String selectedItem)
    {
        final ArrayList<String> list = new ArrayList<>();
        DatabaseReference childRef = dbRef.child("items").child(selectedItem);

        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    list.add(ds.getValue(String.class));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewAdvertismentActivity.this, android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCat.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String setAttributesAndInsert(final String type, final String name, final String city,
                                         final String image, final String price, final String date,
                                         final String description, final String department, final String category)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("name").getValue(String.class);
                DatabaseReference childRef = FirebaseDatabase.getInstance().getReference().child("products").child(type);
                Product newProduct = new Product(userName, name, city, image, price, date, description, department, category);
                childRef.push().setValue(newProduct);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return user.getDisplayName();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imagePath = cursor.getString(columnIndex);
            cursor.close();
            tvImagePath.setText("Image location: " + imagePath);
        }
    }

    private void addNewSellAdvertisment(String filePath)
    {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }

        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Uri file = Uri.fromFile(new File(filePath));
        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests") Uri productImageURL = taskSnapshot.getDownloadUrl();

                String type, department, category, name, owner, price, city, description, image, date;

                type = spType.getSelectedItem().toString();
                department = spDep.getSelectedItem().toString();
                category = spCat.getSelectedItem().toString();
                name = etName.getText().toString();
                image = productImageURL.toString();
                owner = "";
                price = etPrice.getText().toString();
                date = etDate.getText().toString();

                city = spCity.getSelectedItem().toString();
                description = etDesc.getText().toString();

                if (type.equals("buy")) image = "https://firebasestorage.googleapis.com/v0/b/sandop-7935b.appspot.com/o/images%2Fwanted.jpg?alt=media&token=e51ba39a-73a0-4b39-a1eb-43e50311deb4";
                setAttributesAndInsert(type, name, city, image, price, date, description, department, category);
            }
        });
    }

    private void addNewBuyAdvertisment() {
        String department, category, name, owner, price, date, city, description, image;

        department = spDep.getSelectedItem().toString();
        category = spCat.getSelectedItem().toString();
        name = etName.getText().toString();
        image = "https://firebasestorage.googleapis.com/v0/b/sandop-7935b.appspot.com/o/images%2Fwanted.jpg?alt=media&token=e51ba39a-73a0-4b39-a1eb-43e50311deb4";
        owner = "";
        price = etPrice.getText().toString();
        date = etDate.getText().toString();
        city = spCity.getSelectedItem().toString();
        description = etDesc.getText().toString();

        setAttributesAndInsert("buy", name, city, image, price, date, description, department, category);
    }
    private void disableUploadImage(Spinner spinner, final Button button)
    {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItem().equals("buy")) {
                    button.setVisibility(View.INVISIBLE);
                    tvImagePath.setText("");
                }
                else button.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // set value to tripYear, tripMonth, tripDay
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            String tripYear, tripMonth, tripDay;
            tripYear = String.valueOf(selectedYear);
            if (selectedMonth < 10) {
                tripMonth = 0 + String.valueOf(selectedMonth + 1);
            } else {
                tripMonth = String.valueOf(selectedMonth + 1);
            }

            if (selectedDay < 10) {
                tripDay = 0 + String.valueOf(selectedDay);
            } else {
                tripDay = String.valueOf(selectedDay);
            }

            etDate.setText(tripDay + "." + tripMonth + "." + tripYear);
        }
    };

}