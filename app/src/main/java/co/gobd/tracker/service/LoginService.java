package co.gobd.tracker.service;

import android.util.Log;

import co.gobd.tracker.callback.LoginCallback;
import co.gobd.tracker.model.login.AccessToken;
import co.gobd.tracker.model.user.User;
import co.gobd.tracker.network.LoginApi;
import co.gobd.tracker.network.RestClientLogin;
import co.gobd.tracker.utility.Constant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by fahad on 28-Mar-16.
 */
public class LoginService {

    private static final String TAG = "LoginService";
    LoginCallback loginCallback;
    AccessToken accessToken;

    public LoginService(LoginCallback loginCallback) {

        this.loginCallback = loginCallback;
    }

    public void login(String uname, String pass) {

        final LoginApi loginApi = new RestClientLogin().getLoginApi();

        Call<AccessToken> call = loginApi.login(uname, pass, Constant.grantType, Constant.clientId, Constant.clientSecret);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response.isSuccess()) {
                    try {
                        String accessToken = response.body().getAccessToken();
                        Log.i(TAG, accessToken);
                        final String bearer = "bearer "+accessToken; //FIXME: dumb code
                        Call<User> tokenCall = loginApi.sendToken(bearer);
                        tokenCall.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                if(response.isSuccess()){
                                    try {
                                        String clientId = response.body().getId();
                                        Log.i(TAG, clientId);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {

            }
        });

    }


}
