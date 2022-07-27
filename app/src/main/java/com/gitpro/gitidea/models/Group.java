package com.gitpro.gitidea.models;

public class Group {

    private String groupTitle;
    private String groupButtonTitle;

    public Group(String groupTitle, String groupButtonTitle) {
        this.groupTitle = groupTitle;
        this.groupButtonTitle = groupButtonTitle;

    }

    public Group(String groupTitle) {
        this.groupTitle=groupTitle;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public String getGroupButtonTitle() {
        return groupButtonTitle;
    }

}
