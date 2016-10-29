package com.otonio.sin.chamarras;

import java.util.concurrent.TimeUnit;
import java.util.Calendar;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import com.squareup.okhttp.OkHttpClient;

public class ConnectionManager {
    public interface Services{

        @FormUrlEncoded
        @POST("/webservice/wssearch/consulta_view_video")
        void consultaViewVideo(@Field("id") Integer id, Callback<ResponseViewVideo> response);

        @FormUrlEncoded
        @POST("/webservice/wssearch/consulta_ultimo_reto")
        void consultaUltimoReto(@Field("id") Integer id, Callback<ResponseUltimoReto> response);

        @FormUrlEncoded
        @POST("/webservice/wssearch/empieza_reto")
        void empiezaReto(@Field("id") Integer id, @Field("date_start") String date_start, Callback<ResponseEmpiezaReto> response);

        @FormUrlEncoded
        @POST("/webservice/wssearch/termina_reto")
        void terminaReto(@Field("id") Integer id, @Field("date_end") String date_end, Callback<ResponseTerminaReto> response);

        @FormUrlEncoded
        @POST("/webservice/wssearch/registra_usuario_otonio")
        void registraUsuarioOtonioService(@Field("user_name") String user_name,
                                          @Field("email") String email, @Field("password") String password,
                                          @Field("date_active") String date_active,
                                          Callback<ResponseRegistroUsuario> response);

        @FormUrlEncoded
        @POST("/webservice/wssearch/login_usuario_otonio")
        void loginUsuarioOtonioService(@Field("email") String email, @Field("password") String password, @Field("date_active") String date_active, Callback<ResponseLoginUsuario> response);
    }

    private static final String API_URL = "https://knyou.com";

    private static final RestAdapter REST_ADAPTER = new RestAdapter.Builder()
            .setEndpoint(API_URL).setClient(new OkClient(getClient())).setLogLevel(RestAdapter.LogLevel.FULL).build();

    private static final Services GIT_HUB_SERVICE = REST_ADAPTER.create(Services.class);

    private static OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(1, TimeUnit.MINUTES);
        client.setReadTimeout(1, TimeUnit.MINUTES);
        return client;
    }

    public static Services getService() {
        return GIT_HUB_SERVICE;
    }
}
