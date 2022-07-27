package com.gitpro.gitidea.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Articles {

    @SerializedName("source")
    @Expose
    private Source source;

    @SerializedName("author")
    @Expose
    private String author;


    @SerializedName("title")
    @Expose
    private String  title;


    @SerializedName("description")
    @Expose
    private String description;


    @SerializedName("urlToImage")
    @Expose
    private String urlToImage;

    @SerializedName("publishedAt")
    @Expose
    private String publishedAt;


    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}
