package fi.oulu.mobisocial.sandop.helpers;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import fi.oulu.mobisocial.sandop.R;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static fi.oulu.mobisocial.sandop.R.id.ivProductImage;

/**
 * Created by Majid on 4/27/2017.
 */

public class UploadImageActivity extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView ivImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);

        ivImage = (ImageView) findViewById(R.id.ivProductImage);

        Button uploadImage = (Button) findViewById(R.id.btnUpload);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }


}
