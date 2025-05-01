package com.practo.proxy.PractoProxy.client;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

import java.util.Map;

public interface InternalServiceHttpClient {
    @GET
    Call<Object> get(@Url String url, @QueryMap Map<String, String> queryParams);

    @POST
    Call<Object> post(@Url String url, @Body Object body);

    @PUT
    Call<Object> put(@Url String url, @Body Object body);

    @PATCH
    Call<Object> patch(@Url String url, @Body Object body);

    @DELETE
    Call<Object> delete(@Url String url);
} 