package co.gobd.tracker.model.register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Registration {

    @SerializedName("UserName")
    @Expose
    private String UserName;
    @SerializedName("Password")
    @Expose
    private String Password;
    @SerializedName("ConfirmPassword")
    @Expose
    private String ConfirmPassword;
    @SerializedName("Email")
    @Expose
    private String Email;
    @SerializedName("PhoneNumber")
    @Expose
    private String PhoneNumber;
    @SerializedName("PicUri")
    @Expose
    private String PicUri;
    @SerializedName("Type")
    @Expose
    private String Type;

    /**
     *
     * @param ConfirmPassword
     * @param Email
     * @param Password
     * @param PhoneNumber
     * @param UserName
     */
    public Registration(String UserName, String Password, String ConfirmPassword, String Email, String PhoneNumber) {
        this.UserName = UserName;
        this.Password = Password;
        this.ConfirmPassword = ConfirmPassword;
        this.Email = Email;
        this.PhoneNumber = PhoneNumber;
        PicUri = "Empty";
        Type = "BIKE_MESSENGER";
    }

    /**
     * @return The UserName
     */
    public String getUserName() {
        return UserName;
    }

    /**
     * @param UserName The UserName
     */
    public void setUserName(String UserName) {
        this.UserName = UserName;
    }

    /**
     * @return The Password
     */
    public String getPassword() {
        return Password;
    }

    /**
     * @param Password The Password
     */
    public void setPassword(String Password) {
        this.Password = Password;
    }

    /**
     * @return The ConfirmPassword
     */
    public String getConfirmPassword() {
        return ConfirmPassword;
    }

    /**
     * @param ConfirmPassword The ConfirmPassword
     */
    public void setConfirmPassword(String ConfirmPassword) {
        this.ConfirmPassword = ConfirmPassword;
    }

    /**
     * @return The Email
     */
    public String getEmail() {
        return Email;
    }

    /**
     * @param Email The Email
     */
    public void setEmail(String Email) {
        this.Email = Email;
    }

    /**
     * @return The PhoneNumber
     */
    public String getPhoneNumber() {
        return PhoneNumber;
    }

    /**
     * @param PhoneNumber The PhoneNumber
     */
    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    /**
     * @return The PicUri
     */
    public String getPicUri() {
        return PicUri;
    }

    /**
     * @param PicUri The PicUri
     */
    public void setPicUri(String PicUri) {
        this.PicUri = PicUri;
    }

    /**
     * @return The Type
     */
    public String getType() {
        return Type;
    }

    /**
     * @param Type The Type
     */
    public void setType(String Type) {
        this.Type = Type;
    }

}
