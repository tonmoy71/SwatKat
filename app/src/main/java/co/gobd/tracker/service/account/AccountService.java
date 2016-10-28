package co.gobd.tracker.service.account;

import co.gobd.tracker.model.register.Register;

/**
 * Created by tonmoy on 12-Apr-16.
 */
public interface AccountService {
    void register(Register register, final RegistrationCallback registrationCallback);
    void login(String userName, String password, LoginCallback callback);
    void getProfile(String bearer, ProfileCallback callback);
}
