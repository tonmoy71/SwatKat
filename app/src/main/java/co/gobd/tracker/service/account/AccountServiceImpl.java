package co.gobd.tracker.service.account;

import co.gobd.tracker.model.login.AccessToken;
import co.gobd.tracker.model.register.Registration;
import co.gobd.tracker.model.user.User;
import co.gobd.tracker.network.AccountApi;
import co.gobd.tracker.utility.Constant;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by fahadwajed on 6/22/16.
 */
public class AccountServiceImpl implements AccountService {

    private AccountApi accountApi;
    private CompositeSubscription mSubscription;

    public AccountServiceImpl(AccountApi api) {
        this.accountApi = api;
        this.mSubscription = new CompositeSubscription();
    }

    @Override
    public void login(String userName, String password, final LoginCallback callback) {
        Subscription subscription = accountApi.login(userName, password,
                Constant.Login.GRANT_TYPE, Constant.Login.CLIENT_ID, Constant.Login.CLIENT_SECRET)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AccessToken>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onLoginFailure();
                    }

                    @Override
                    public void onNext(AccessToken response) {
                        String accessToken = response.getAccessToken();
                        // Authorization header for the request
                        String bearer = "bearer " + accessToken;
                        String refreshToken = response.getRefreshToken();
                        callback.onLoginSuccess(accessToken, refreshToken, bearer);
                    }
                });
        mSubscription.add(subscription);
    }

    @Override
    public void getProfile(String bearer, final ProfileCallback callback) {
        Subscription subscription = accountApi.getProfile(bearer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onLoadProfileFailure();
                    }

                    @Override
                    public void onNext(User user) {
                        callback.onLoadProfileSuccess(user.getId());
                    }
                });
        mSubscription.add(subscription);
    }

}
