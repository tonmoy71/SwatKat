package co.gobd.tracker.network;

import co.gobd.tracker.config.BackendUrl;
import co.gobd.tracker.model.login.AccessToken;
import co.gobd.tracker.model.register.Register;
import co.gobd.tracker.model.user.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by fahad on 28-Mar-16.
 */
public interface AccountApi {

    @FormUrlEncoded
    @POST(BackendUrl.TaskCat.LOGIN)
    Call<AccessToken> login(@Field("userName") String userName, @Field("password") String password,
                            @Field("grant_type") String grantType, @Field("client_Id") String clientId,
                            @Field("client_secret") String clientSecret);

    @GET(BackendUrl.TaskCat.GET_PROFILE)
    Observable<User> getProfile(@Header("Authorization") String token);

    @POST(BackendUrl.TaskCat.GET_REGISTER)
    Observable<Void> register(@Body Register register);



}