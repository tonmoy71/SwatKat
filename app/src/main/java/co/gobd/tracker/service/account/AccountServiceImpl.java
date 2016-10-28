package co.gobd.tracker.service.account;

import co.gobd.tracker.model.login.AccessToken;
import co.gobd.tracker.model.register.Register;
import co.gobd.tracker.model.user.User;
import co.gobd.tracker.network.AccountApi;
import co.gobd.tracker.utility.Constant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observer;
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
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void getRegister(Register register, final RegistrationCallback registrationCallback) {

        Call<Void> call = accountApi.register(register);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccess()){
                    registrationCallback.onRegistrationSuccess();
                } else {
                    registrationCallback.onRegistrationFailure();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                registrationCallback.onConnectionError();
            }
        });

    }

    @Override
    public void login(String userName, String password, final LoginCallback callback) {

        Call<AccessToken> call = accountApi.login(userName, password,
                Constant.Login.grantType,
                Constant.Login.clientId,
                Constant.Login.clientSecret);

        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response.isSuccess()) {
                    try {
                        String accessToken = response.body().getAccessToken();

                        // Authorization header for the request
                        String bearer = "bearer " + accessToken;

                        String refreshToken = response.body().getRefreshToken();
                        callback.onLoginSuccess(accessToken, refreshToken, bearer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    callback.onLoginFailure();
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                callback.onConnectionError();
            }
        });

    }

    @Override
    public void getProfile(String bearer, final ProfileCallback callback) {

        /* RxJava implementation */
        accountApi.getProfile(bearer)
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

    }

}
