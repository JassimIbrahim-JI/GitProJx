package com.gitpro.gitidea.models;

public class Slider {

    private String imageLink;
    private boolean isEnabled;

    public Slider(){
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
