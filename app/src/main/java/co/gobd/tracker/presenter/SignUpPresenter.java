package co.gobd.tracker.presenter;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import co.gobd.tracker.model.register.Register;
import co.gobd.tracker.service.account.AccountService;
import co.gobd.tracker.service.account.RegistrationCallback;
import co.gobd.tracker.ui.view.SignUpView;

public class SignUpPresenter {

    private WeakReference<SignUpView> weakReference;
    private AccountService accountService;
    private SignUpView signUpView;

    @Inject
    public SignUpPresenter(AccountService accountService){
        this.accountService = accountService;
    }

    public void initialise(SignUpView View){
        this.weakReference = new WeakReference<SignUpView>(View);
        signUpView = weakReference.get();
    }

    public boolean isValidCredentials() {
        String userName = signUpView.getUserName();
        if (userName == null || userName.isEmpty()) {
            signUpView.showUserNameEmptyError();
            return false;
        }

        if (userName.contains(" ")) {
            signUpView.showUserNameHasSpaceError();
            return false;
        }


        String password = signUpView.getPassword();
        if (password == null || password.isEmpty()) {
            signUpView.showPasswordEmptyError();
            return false;
        }

        if (password.length() < 6) {
            signUpView.showPasswordLengthError();
            return false;
        }

        String confirmPassword = signUpView.getConfirmPassword();
        if (!password.equals(confirmPassword)) {
            signUpView.showPasswordMatchError();
            return false;
        }

        if (!signUpView.isPhoneNumberValid()) {
            signUpView.showInvalidPhoneNumberError();
            return false;
        }


        if (!signUpView.isEmailPatternValid()) {
            signUpView.showInvalidEmailPatterError();
            return false;
        }

        return true;
    }

    public void register() {

        signUpView.startProgress();

        Register register = createRegisterModel();

        accountService.register(register, new RegistrationCallback() {
            @Override
            public void onRegistrationSuccess() {
                signUpView.stopProgress();
                signUpView.startLoginActivity();
            }

            @Override
            public void onRegistrationFailure() {
                //FIXME Should notify the user about the error type, need to parse the error description
                signUpView.stopProgress();
                signUpView.showRegistrationError();
            }

            @Override
            public void onConnectionError() {
                signUpView.stopProgress();
                signUpView.showConnectionError();
            }
        });
    }

    private Register createRegisterModel(){

        //Register register = new Register(userName, password, confirmPassword, email, phoneNumber);

        return new Register(
                signUpView.getUserName(),
                signUpView.getPassword(),
                signUpView.getConfirmPassword(),
                signUpView.getEmail(),
                signUpView.getPhoneNumber());
    }

    public void onDestroy(){
        weakReference = null;
    }
}
