package com.remote.pum.organizer;

public class Weather {
    private String id_stacji;
    private String stacja;
    private String data_pomiaru;
    private String godzina_pomiaru;
    private String temperatura;
    private String predkosc_wiatru;
    private String kierunek_wiatru;
    private String wilgotnosc_wzgledna;
    private String suma_opadu;
    private String cisnienie;

    public String getId_stacji() {
        return id_stacji;
    }

    public String getStacja() {
        return stacja;
    }

    public String getData_pomiaru() {
        return data_pomiaru;
    }

    public String getGodzina_pomiaru() {
        return godzina_pomiaru;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public String getPredkosc_wiatru() {
        return predkosc_wiatru;
    }

    public String getKierunek_wiatru() {
        return kierunek_wiatru;
    }

    public String getWilgotnosc_wzgledna() {
        return wilgotnosc_wzgledna;
    }

    public String getSuma_opadu() {
        return suma_opadu;
    }

    public String getCisnienie() {
        return cisnienie;
    }
}
