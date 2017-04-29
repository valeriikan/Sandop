package fi.oulu.mobisocial.sandop.helpers;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Majid on 4/27/2017.
 */

public class User {
    private String name;
    private String imageURL;

    public User()
    {}
    public User(String uName, String uImagePath)
    {
        name = uName;
        imageURL = uImagePath;
    }

    public String getName(){return name;}
    public String getImageURL(){return imageURL;}

    public void setName(String uName){name = uName;}
    public void setImageURL(String uImagePath){imageURL = uImagePath;}
}
