package co.gobd.tracker.service.account;

/**
 * Created by tonmoy on 12-Apr-16.
 */
public interface AccountService {

    void login(String userName, String password, LoginCallback callback);

    void getProfile(String bearer, ProfileCallback callback);
}
