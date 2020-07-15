package com.remote.pum.organizer;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Note implements Serializable {
    private String title;
    private String content;
    private String picture;
    private Date date;
    private String location;

    public Note(String title) {
        this.title = title;
        this.content = "";
        this.picture = null;
        this.date = null;
        this.location = "";
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy (EEEE) HH:mm", new Locale("pl", "PL"));
            return simpleDateFormat.format(this.date);
        }

        return null;
    }

    public void setDate(int year, int month, int day, int hour, int min) {
        Calendar calendar = new GregorianCalendar(year, month - 1, day, hour, min);
        if (date == null) {
            this.date = new Date(calendar.getTimeInMillis());
        } else {
            this.date.setTime(calendar.getTimeInMillis());
        }
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getDateYear() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public int getDateMonth() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    public int getDateDay() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getDateHour() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getDateMin() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    public Date getDateDate() {
        return this.date;
    }
}
