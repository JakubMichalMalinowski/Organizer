package com.remote.pum.organizer;

import java.io.Serializable;

public class Note implements Serializable {
    private String title;
    private String content;

    public Note(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Note setTitle(String title) {
        this.title = title;
        return this;
    }

    public Note setContent(String content) {
        this.content = content;
        return this;
    }
}
