package com.remote.pum.organizer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Główne Activity aplikacji
 *
 * @author Monika Jakubiec
 * @author Jakub Malinowski
 */
public class MainActivity extends AppCompatActivity implements RecyclerViewListener {
    private RecyclerView notesRecyclerView;
    private DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    private File data;
    private List<Device> devices;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new File(getApplicationContext().getFilesDir(), "notes_data.data");

        if (!data.exists()) {
            boolean created = false;

            try {
                created = data.createNewFile();
            } catch (IOException e) {
                closeAppWithFileError("Nie udało sie utworzyć pliku.").show();
            }

            if (created) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Brak pliku z danymi. Został utworzony nowy pusty plik.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Brak notatek", Toast.LENGTH_LONG).show();
                    }
                });
                builder.create().show();

                devices = new ArrayList<>();
                saveData();
            } else {
                closeAppWithFileError("Nie udało się utworzyć pliku.").show();
            }
        } else {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(data))) {
                devices = (List<Device>) objectInputStream.readObject();
                if (!devices.isEmpty()) {
                    Toast.makeText(this, "Załadowano notatki", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Brak notatek", Toast.LENGTH_LONG).show();
                }
            } catch (IOException | ClassNotFoundException e) {
                closeAppWithFileError("Błąd odczytu danych.").show();
            }
        }

        if (devices != null) {
            final SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

            notesRecyclerView = findViewById(R.id.list_of_notes_recycler_view);

            devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(this, preferences, devices);
            devicesRecyclerViewAdapter.setRecyclerViewListener(this);

            notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            notesRecyclerView.setAdapter(devicesRecyclerViewAdapter);
            notesRecyclerView.setItemAnimator(new DefaultItemAnimator());

            if (preferences.getBoolean("show_help", true)) {
                buildHelpBaseAlertDialogBuilder().setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).setNegativeButton("Nie pokazuj ponownie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        preferences.edit().putBoolean("show_help", false).apply();
                    }
                }).create().show();
            }
        }
    }

    /**
     * Metoda informująca użytkownika o błędzie i zamykająca aplikację
     *
     * @param additionalInfo dodatkowe informacje do wyświetlenia dla użytkownika
     * @return obiekt klasy AlertDialog, czyli okno dialogowe wyświetlające informacje dla użytkownika
     */
    private AlertDialog closeAppWithFileError(String additionalInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Błąd dostępu do danych, uruchom aplikację ponownie, a w przypadku niepowodzenia wyczyść dane aplikacji.\n" + additionalInfo);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        return builder.create();
    }

    /**
     * Zapis danych do pamięci
     */
    private void saveData() {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(data))) {
            objectOutputStream.writeObject(devices);
            Toast.makeText(this, "Zapisano", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            closeAppWithFileError("Błąd zapisu danych.").show();
        }
    }

    /**
     * Inicjalizacja zawartości menu opcji
     *
     * @param menu menu, w którym umieszczamy zawartość
     * @return true - aby pokazać menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Rozpoznanie wybranej opcji w menu
     *
     * @param item wybrana opcja
     * @return true - jezeli skonsumowane
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //nowa notatka
        if (item.getItemId() == R.id.new_note) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tytuł notatki");

            final View view = getLayoutInflater().inflate(R.layout.add_device_layout, null);
            builder.setView(view);

            builder.setPositiveButton("Zapisz", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText titleEditText = view.findViewById(R.id.title_edit_text);
                    String title = titleEditText.getText().toString();
                    if (!title.equals("")) {
                        devices.add(new Device(title));
                        saveData();
                    } else {
                        Toast.makeText(getApplicationContext(), "Anulowano. Wprowadzono pusty tytuł", Toast.LENGTH_LONG).show();
                    }
                }
            });

            builder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            builder.create().show();
        }

        //pogoda
        if (item.getItemId() == R.id.last_weather) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.weather_location_layout, null, false);
            final EditText editText = view.findViewById(R.id.weather_location_edit_text);
            final SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

            editText.setText(preferences.getString("weather_location", ""));

            builder.setTitle("Wpisz miejscowość (bez polskich znakow)")
                    .setView(view)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            preferences.edit().putString("weather_location", editText.getText().toString()).apply();
                            downloadWeather(editText.getText().toString());
                        }
                    })
                    .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create().show();

        }

        //wybór koloru tła notatek
        if (item.getItemId() == R.id.choose_color) {
            final SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

            ColorPickerDialogBuilder
                    .with(this)
                    .setTitle("Kolor notatek zmieni sie po restarcie aplikacji")
                    .initialColor(preferences.getInt("note_color", Color.parseColor("#FFFACD")))
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setPositiveButton("ok", new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                            preferences.edit().putInt("note_color", selectedColor).apply();
                            Toast.makeText(getApplicationContext(), "Kolor notatek zmieni się po ponownym uruchomieniu aplikacji", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("anuluj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .build()
                    .show();
        }

        //informacje i pomoc
        if (item.getItemId() == R.id.help) {
            buildHelpBaseAlertDialogBuilder().setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).create().show();
        }

        return true;
    }

    /**
     * Pobranie rezultatu z zamykanego Activity
     *
     * @param requestCode kod zapytania
     * @param resultCode  kod wyniku
     * @param data        Intent zawierające zwrócone dane
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            Device device = (Device) data.getSerializableExtra("return_note");

            if (device != null) {
                devices.get(requestCode).setName(device.getName());
                devices.get(requestCode).setNote(device.getNote());
                devices.get(requestCode).setPicture(device.getPicture());
                devices.get(requestCode).setDate(device.getDateDate());
                devices.get(requestCode).setLocation(device.getLocation());
            }
        }

        devicesRecyclerViewAdapter.notifyItemChanged(requestCode);
        saveData();

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Klikniecie powodujące przejście w tryb modyfikacji notatki
     *
     * @param view     element wywołujący
     * @param position pozycja notatki
     */
    @Override
    public void onModifyClick(View view, int position) {
        Intent intent = new Intent(this, DeviceActivity.class);
        intent.putExtra("current_note", devices.get(position));
        startActivityForResult(intent, position);
    }

    /**
     * Ruch powodujący usunięcie notatki
     *
     * @param view     element wywołujący
     * @param position pozycja notatki
     */
    @Override
    public void onDeleteMotion(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Usuwanie notatki")
                .setMessage("Czy na pewno chcesz usunąć tą notatkę?")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        devices.remove(position);
                        devicesRecyclerViewAdapter.notifyItemRemoved(position);
                        devicesRecyclerViewAdapter.notifyItemRangeChanged(0, devices.size());
                        saveData();
                    }
                })
                .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }

    /**
     * Zbudowanie bazowego okna dialogowego z informacjami i pomocą
     *
     * @return obiekt AlertDialog.Builder z którego należy utworzyć okno dialogowe i je wyświetlić
     */
    private AlertDialog.Builder buildHelpBaseAlertDialogBuilder() {
        return new AlertDialog.Builder(this).setTitle("Informacje i pomoc (proszę się dokładnie zapoznać)")
                .setMessage("Aplikacja umożliwiwa dodawanie różnego typu notatek, np. \"szybkich\" które są reprezentowane tylko tytułem. " +
                        "Po dotknięciu takiej notatki możliwa jest jej edycja i rozbudowanie jej o opis oraz dodanie/zmiana/usuwanie obrazka. " +
                        "Do danej notatki możliwe jest także dodawanie wydarzenia poprzez dotknięcie odpowiedniego przycisku. " +
                        "W wydarzeniu możemy modyfikować datę wraz z godziną i lokalizację. " +
                        "Zapis danej notatki po modyfikacjach następuje po dotknięciu przycisku powrotu, zrobiono tak ze względu na wygode i bezpieczeństwo użytkownika. " +
                        "Usunięcie następuje poprzez dłuższe przytrzymanie danej notatki w widoku ekranu głównego. " +
                        "Umożliwiono także zmianę koloru tła notatek. " +
                        "Z poziomu aplikacji możliwe jest także sprawdzenie ostatnich zanotowanych danych meteorologicznych dla wybranej miejscowości, po klinknięciu na odpowiednią opcję menu głównego. " +
                        "Aby zobaczyć opis danego przycisku należy go przytrzymać. " +
                        "W razie wątpliwości można wrócić do tej informacji poprzez wybranie opcji \"Informacje i pomoc\" lub (?) (w zależności sposobu wyświetlania) w górnej belce aplikacji. " +
                        "\n\nAutorzy:\nMonika Jakubiec\nJakub Malinowski\n\nVersion: 2.0");
    }

    /**
     * Pobieranie pogody dla podanej lokalizacji
     *
     * @param location lokalizacja dla której pobierania jest pogoda
     */
    private void downloadWeather(String location) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Czekaj, trwa pobieranie...");
        progressDialog.show();

        //usunięcie spacji i zamiana wszystkich liter na małe
        final String loc = location.toLowerCase().replaceAll("\\s", "");
        final Context context = this;

        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imgw;
                HttpURLConnection imgwConnection = null;

                try {
                    imgw = new URL("https://danepubliczne.imgw.pl/api/data/synop/station/" + loc + "/format/json");
                    imgwConnection = (HttpURLConnection) imgw.openConnection();
                    Gson gson = new Gson();
                    if (imgwConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStreamReader reader = new InputStreamReader(imgwConnection.getInputStream());
                        final Weather weather = gson.fromJson(reader, Weather.class);
                        final View view = getLayoutInflater().inflate(R.layout.weather_layout, null, false);

                        ((TextView) view.findViewById(R.id.weather_date_and_time)).setText(String.format(" Czas pomiaru: %s\t%s:00", weather.getData_pomiaru(), weather.getGodzina_pomiaru()));
                        ((TextView) view.findViewById(R.id.weather_temperature)).setText(String.format(" Temperatura: %s", weather.getTemperatura()));
                        ((TextView) view.findViewById(R.id.weather_wind_velocity)).setText(String.format(" Prędkość: %s", weather.getPredkosc_wiatru()));
                        ((TextView) view.findViewById(R.id.weather_wind_direction)).setText(String.format(" Kierunek: %s", weather.getKierunek_wiatru()));
                        ((TextView) view.findViewById(R.id.weather_humidity)).setText(String.format(" Wilgotność względna: %s", weather.getWilgotnosc_wzgledna()));
                        ((TextView) view.findViewById(R.id.weather_rain)).setText(String.format(" Suma opadu: %s", weather.getSuma_opadu()));
                        ((TextView) view.findViewById(R.id.weather_pressure)).setText(String.format(" Ciśnienie: %s", weather.getCisnienie()));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                new AlertDialog.Builder(context).setTitle("Pogoda dla: " + weather.getStacja())
                                        .setView(view)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).create().show();
                            }
                        });

                    } else if (imgwConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                new AlertDialog.Builder(context).setTitle("Błąd")
                                        .setMessage("Stacja nie została znaleziona. Proszę sprawdzić poprawność nazwy miejscowości lub użyć większej miejscowości.")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).create().show();
                            }
                        });
                    } else {
                        throw new IOException();
                    }
                } catch (IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            new AlertDialog.Builder(context).setTitle("Błąd")
                                    .setMessage("Bład przy połączeniu z serwerem dostarczającym informacje pogodowe. Pobranie danych jest niemożliwe.")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    }).create().show();
                        }
                    });
                } finally {
                    if (imgwConnection != null) {
                        imgwConnection.disconnect();
                    }

                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        }).start();
    }
}