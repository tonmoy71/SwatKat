package co.gobd.tracker.di.module;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Named;
import javax.inject.Singleton;

import co.gobd.tracker.config.BackendUrl;
import co.gobd.tracker.model.job.AssignedJob;
import co.gobd.tracker.model.job.JobModel;
import co.gobd.tracker.model.job.User;
import co.gobd.tracker.model.job.order.BuyerInfo;
import co.gobd.tracker.model.job.order.Order;
import co.gobd.tracker.model.job.order.OrderCart;
import co.gobd.tracker.model.job.order.SellerInfo;
import co.gobd.tracker.network.AccountApi;
import co.gobd.tracker.network.JobApi;
import co.gobd.tracker.network.TrackerApi;
import co.gobd.tracker.utility.Constant;
import co.gobd.tracker.utility.deserializer.AssignedJobDeserializer;
import co.gobd.tracker.utility.deserializer.BuyerInfoDeserializer;
import co.gobd.tracker.utility.deserializer.JobModelDeserializer;
import co.gobd.tracker.utility.deserializer.OrderCartDeserializer;
import co.gobd.tracker.utility.deserializer.OrderDeserializer;
import co.gobd.tracker.utility.deserializer.SellerInfoDeserializer;
import co.gobd.tracker.utility.deserializer.UserDeserializer;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tonmoy on 07-Apr-16.
 */

@Module
public class ApiModule {

    @Provides
    @Singleton
    public OkHttpClient providesOkHttpClient() {
        // Enables to see retrofit request logs in the android monitor
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(interceptor);

        return httpClient.build();

    }

    @Provides
    @Singleton
    public Gson providesGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AssignedJob.class, new AssignedJobDeserializer());
        gsonBuilder.registerTypeAdapter(JobModel.class, new JobModelDeserializer());
        gsonBuilder.registerTypeAdapter(Order.class, new OrderDeserializer());
        gsonBuilder.registerTypeAdapter(SellerInfo.class, new SellerInfoDeserializer());
        gsonBuilder.registerTypeAdapter(BuyerInfo.class, new BuyerInfoDeserializer());
        gsonBuilder.registerTypeAdapter(OrderCart.class, new OrderCartDeserializer());
        gsonBuilder.registerTypeAdapter(User.class, new UserDeserializer());
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    public GsonConverterFactory providesGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }


    @Provides
    @Singleton
    @Named(Constant.BackendName.SHADOW_CAT)
    public Retrofit providesRetrofitForShadowCat(OkHttpClient okHttpClient,
                                                 GsonConverterFactory factory) {
        return new Retrofit.Builder()
                .baseUrl(BackendUrl.ShadowCat.BASE)
                .client(okHttpClient)
                .addConverterFactory(factory)
                .build();
    }

    @Provides
    @Singleton
    @Named(Constant.BackendName.TASK_CAT)
    public Retrofit providesRetrofitForTaskCat(OkHttpClient okHttpClient,
                                               GsonConverterFactory factory) {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(BackendUrl.TaskCat.BASE)
                .client(okHttpClient)
                .addConverterFactory(factory)
                .build();
    }

    @Singleton
    @Provides
    public TrackerApi providesTrackerApi(@Named(Constant.BackendName.SHADOW_CAT) Retrofit retrofit) {
        return retrofit.create(TrackerApi.class);
    }

    @Singleton
    @Provides
    public AccountApi providesAuthApi(@Named(Constant.BackendName.TASK_CAT) Retrofit retrofit) {
        return retrofit.create(AccountApi.class);
    }

    @Singleton
    @Provides
    public JobApi providesJobApi(@Named(Constant.BackendName.TASK_CAT) Retrofit retrofit) {
        return retrofit.create(JobApi.class);
    }

}
