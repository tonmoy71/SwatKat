package co.gobd.tracker.model.job.order;

/**
 * Created by fahad on 10/25/16.
 */

public class BuyerInfo {

    private String Name;
    private String PhoneNumer;

    public BuyerInfo(String name, String phoneNumer) {
        Name = name;
        PhoneNumer = phoneNumer;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumer() {
        return PhoneNumer;
    }

    public void setPhoneNumer(String phoneNumer) {
        PhoneNumer = phoneNumer;
    }

}
