package fi.oulu.mobisocial.sandop.helpers;

import android.util.Log;
import android.widget.Spinner;

/**
 * Created by Majid on 4/22/2017.
 */

public class Product {
    private String owner;
    private String name;
    private String city;
    private String image;
    private String price;
    private String date;
    private String description;
    private String department;
    private String type;

    public Product()
    {
        owner = "";
        name = "";
        city = "";
        image = "";
        price = "";
        date = "";
        description = "";
        department = "";
        type = "";
    }
    public Product(String pOwner, String pName, String pCity,
                   String pImage, String pPrice, String pDate, String pDescription, String pDepartment, String pType)
    {
        owner = pOwner;
        name = pName;
        city = pCity;
        image = pImage;
        price = pPrice;
        date = pDate;
        description = pDescription;
        department = pDepartment;
        type = pType;
    }

    public String getOwner()
    {
        return owner;
    }
    public String getName()
    {
        return name;
    }
    public String getCity(){return city;}
    public String getImage()
    {
        return image;
    }
    public String getPrice()
    {
        return price;
    }
    public String getDate()
    {
        return date;
    }
    public String getDescription()
    {
        return description;
    }
    public String getDepartment(){return department;}
    public String getType(){return type;}
    public void setOwner(String pOwner)
    {
        owner = pOwner;
    }
    public void setName(String pName)
    {
        name = pName;
    }
    public void setCity(String pCity) {city = pCity;}
    public void setImage(String pImage)
    {
        image = pImage;
    }
    public void setPrice(String pPrice)
    {
        price = pPrice;
    }
    public void setDate(String pDate)
    {
        date = pDate;
    }
    public void setDescription(String pDescription)
    {
        description = pDescription;
    }
    public void setDepartment(String pDepartment) {department = pDepartment;}
    public void setType(String pType) {type = pType;}

    public boolean isEqual(Product product)
    {
        boolean state = false;

        if (product.getOwner().equals(owner))
        {
            if (product.getName().equals(name))
            {
                if (product.getCity().equals(city))
                {
                    if (product.getImage().equals(image))
                    {
                        if (product.getDepartment().equals(department))
                        {
                            if (product.getDescription().equals(description))
                            {
                                if (product.getPrice().equals(price))
                                {
                                    if (product.getDate().equals(date))
                                    {
                                        if (product.getType().equals(type)) state = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return state;
    }
}
