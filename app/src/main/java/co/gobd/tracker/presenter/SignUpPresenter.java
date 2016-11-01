package co.gobd.tracker.presenter;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import co.gobd.tracker.model.register.Registration;
import co.gobd.tracker.network.AccountApi;
import co.gobd.tracker.ui.view.SignUpView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class SignUpPresenter {

    private WeakReference<SignUpView> weakReference;
    private AccountApi mAccountApi;
    private SignUpView signUpView;
    private CompositeSubscription mSubscription;

    @Inject
    public SignUpPresenter(AccountApi api) {
        this.mAccountApi = api;
        this.mSubscription = new CompositeSubscription();
    }

    public void initialise(SignUpView View) {
        this.weakReference = new WeakReference<>(View);
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
        Registration registrationModel = createRegistrationModel();
        Subscription subscription = mAccountApi.register(registrationModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    signUpView.stopProgress();
                    signUpView.startLoginActivity();
                }, throwable -> {
                    signUpView.stopProgress();
                    signUpView.showRegistrationError(throwable.getMessage());
                });
        mSubscription.add(subscription);
    }

    private Registration createRegistrationModel() {
        return new Registration(
                signUpView.getUserName(),
                signUpView.getPassword(),
                signUpView.getConfirmPassword(),
                signUpView.getEmail(),
                signUpView.getPhoneNumber());
    }

    public void onDestroy() {
        weakReference = null;
        mSubscription.unsubscribe();
    }
}
