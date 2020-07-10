package com.remote.pum.organizer;

import java.io.Serializable;

public class Note implements Serializable {
    private String title;
    private String content;
    private String picture;

    public Note(String title) {
        this.title = title;
        this.content = "";
        this.picture = null;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
