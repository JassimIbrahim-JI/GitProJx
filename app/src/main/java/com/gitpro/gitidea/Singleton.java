package com.gitpro.gitidea;

import android.net.Uri;
import java.util.List;

public class Singleton {

    private static Singleton instance;

    public void initList(List<Uri>uris) {
        if (instance == null) {
            instance = new Singleton(uris);
        }
    }

    public static Singleton getInstance() {
        return instance;
    }

    private final List<Uri> uris;

    private Singleton(List<Uri> uris) {
        this.uris = uris;
    }

    public List<Uri> getUris() {
        return this.uris;
    }
}
