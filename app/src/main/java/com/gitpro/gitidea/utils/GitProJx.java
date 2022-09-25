package com.gitpro.gitidea.utils;

import android.app.Application;
import android.os.StrictMode;

import androidx.core.provider.FontRequest;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;

import com.gitpro.gitidea.BuildConfig;

import java.security.cert.Certificate;

public class GitProJx extends Application {
    public GitProJx(){
        if (BuildConfig.DEBUG){
            StrictMode.enableDefaults();
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();


        FontRequest fontRequest = new FontRequest(
                "com.gitpro.gitidea.fontprovider",
                "com.gitpro.gitidea",
                "emoji compat Font Query",
                Certificate.class.getModifiers());
        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this, fontRequest);
        EmojiCompat.init(config);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

    }
}
