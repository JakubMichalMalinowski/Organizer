package com.remote.pum.organizer;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Urządzenie
 */
public class Device implements Serializable {
    private String name;
    private String note;
    private String picture;
    private Date date;
    private String location;
    private String credentials;

    public Device(String name) {
        this.name = name;
        this.note = "";
        this.picture = null;
        this.date = null;
        this.location = "";
        this.credentials = "";
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    /**
     * Pobranie daty z obiektu z formatowaniem jako String
     *
     * @return string zawierający zwróconą datę z formatowaniem
     */
    public String getDate() {
        if (date != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy (EEEE) HH:mm", new Locale("pl", "PL"));
            return simpleDateFormat.format(this.date);
        }

        return null;
    }

    /**
     * Ustawienie daty na podstawie podanych parametrów
     *
     * @param year  rok
     * @param month miesiąc
     * @param day   dzień
     * @param hour  godzina
     * @param min   minuta
     */
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

    /**
     * Zwrócenie roku daty
     *
     * @return rok
     */
    public int getDateYear() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * Zwrócenie miesiąca daty
     *
     * @return miesiąc
     */
    public int getDateMonth() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    /**
     * Zwrócenie dnia daty
     *
     * @return dzień
     */
    public int getDateDay() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Zwrócenie godziny daty
     *
     * @return godzina
     */
    public int getDateHour() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * Zwrócenie minuty daty
     *
     * @return minuta
     */
    public int getDateMin() {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * Zwrócenie daty jako obiektu typu Date
     *
     * @return data
     */
    public Date getDateDate() {
        return this.date;
    }
}