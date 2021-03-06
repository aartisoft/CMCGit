package com.clubmycab.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by newpc on 3/3/16.
 */
public class ContactData implements Parcelable {
    private String profilePic;
    private String phoneNumber;
    private String name;

    public String getSearchstring() {
        return searchstring;
    }

    public void setSearchstring(String searchstring) {
        this.searchstring = searchstring;
    }

    private String searchstring;
    public ContactData()
    { 

    }


    private ContactData(Parcel in){
        name = in.readString();
        phoneNumber = in.readString();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNumber);

    }

  public  static final Creator<ContactData> CREATOR
            = new Creator<ContactData>() {

      public ContactData createFromParcel(Parcel in) {
            return new ContactData(in);
        }

      public ContactData[] newArray(int size) {
            return new ContactData[size];
        }
    };
}
