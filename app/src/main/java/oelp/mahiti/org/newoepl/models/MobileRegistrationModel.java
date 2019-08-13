package oelp.mahiti.org.newoepl.models;

/**
 * Created by sandeep HR on 23/04/19.
 */
public class MobileRegistrationModel {
    String mobileNumber;

    public MobileRegistrationModel(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
