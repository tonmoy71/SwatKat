package co.gobd.tracker.presenter;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import co.gobd.tracker.service.account.AccountService;
import co.gobd.tracker.service.account.LoginCallback;
import co.gobd.tracker.service.account.ProfileCallback;
import co.gobd.tracker.ui.view.LoginView;
import co.gobd.tracker.utility.SessionManager;

/**
 * Created by fahadwajed on 6/22/16.
 */
public class LoginPresenter {

    private WeakReference<LoginView> loginViewWeakReference;
    private AccountService accountService;
    private LoginView loginView;

    @Inject
    SessionManager sessionManager;

    @Inject
    public LoginPresenter(AccountService accountService){
        this.accountService = accountService;
    }

    public void initialise(LoginView view){
        this.loginViewWeakReference = new WeakReference<>(view);

        loginView = this.loginViewWeakReference.get();
    }

    public boolean isValidCredentials(){
        if(loginView != null){
            String userName = loginView.getUserName();
            if (userName == null || userName.isEmpty()){
                loginView.showUserNameEmptyError();
                return false;
            }

            String password = loginView.getPassword();
            if (password == null || password.isEmpty()){
                loginView.showPasswordEmptyError();
                return false;
            }
            if(password.length() < 6){
                loginView.showPasswordLengthError();
                return false;
            }

            return true;
        }

        return false;
    }

    public void login() {
        final String userName = loginView.getUserName();
        final String password = loginView.getPassword();

        loginView.startProgress();

        accountService.login(userName, password, new LoginCallback() {
            @Override
            public void onLoginSuccess(final String accessToken, final String refreshToken, final String bearer) {
                sessionManager.setUsername(userName);
                sessionManager.setPassword(password);
                accountService.getProfile(bearer, new ProfileCallback() {
                    @Override
                    public void onLoadProfileSuccess(String assetId) {
                        loginView.stopProgress();
                        loginView.saveAssetInformation(assetId, accessToken, refreshToken, bearer);
                        loginView.startMainActivity();

                    }

                    @Override
                    public void onLoadProfileFailure() {
                        loginView.stopProgress();
                        loginView.showProfileLoadError();

                    }

                    @Override
                    public void onConnectionError() {
                        loginView.stopProgress();
                        loginView.showConnectionError();

                    }
                });
            }

            @Override
            public void onLoginFailure() {

                loginView.stopProgress();
                loginView.showLoginError();

            }

            @Override
            public void onConnectionError() {
                loginView.stopProgress();
                loginView.showConnectionError();

            }
        });
    }

    public void onDestroy()   {
        loginViewWeakReference = null;
    }
}
