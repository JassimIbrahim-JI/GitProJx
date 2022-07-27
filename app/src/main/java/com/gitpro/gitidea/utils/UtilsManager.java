package com.gitpro.gitidea.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import com.gitpro.gitidea.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class UtilsManager {
    private Context context;
    private static Retrofit retrofit = null;

    public UtilsManager(Context context) {
        this.context = context;
    }

    public Retrofit getClient(String url) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor=new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(httpLoggingInterceptor);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit=new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(builder.build())
                .build();
        return retrofit;
    }

    //region check internet connection availability
    public static boolean hasConnection(Context context) {
        boolean checkInternet = false;
        try {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return true;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities cap = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (cap == null) {
                    return true;
                }

                return !cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = connectivityManager.getAllNetworks();
                for (Network n : networks) {
                    NetworkInfo nInfo = connectivityManager.getNetworkInfo(n);
                    if (nInfo != null && nInfo.isConnected()) checkInternet = true;
                }
            } else {
                NetworkInfo networks = connectivityManager.getActiveNetworkInfo();
                if (networks.isAvailable() && networks.isConnected())
                    checkInternet = true;
            }

        } catch (Exception e) {
            Log.e("Error network", e.getMessage());
        }
        return !checkInternet;
        //endregion
    }
    //region get internet connection alert
    public static void internetErrorDialog(Context context){
        SweetAlertDialog alert = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.showCancelButton(false);
        alert.setTitleText(context.getString(R.string.no_internet_title));
        alert.setContentText(context.getString(R.string.no_internet_message));
        alert.setConfirmText(context.getString(R.string.dialog_ok));
        alert.show();
    }
}