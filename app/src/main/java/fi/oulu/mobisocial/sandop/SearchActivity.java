package fi.oulu.mobisocial.sandop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

/**
 * Created by Majid on 4/29/2017.
 */

public class SearchActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle saveedInstanceState)
    {
        super.onCreate(saveedInstanceState);
        setContentView(R.layout.activity_search);

        ListView searchList = (ListView) findViewById(R.id.lvSearch);

    }
}
