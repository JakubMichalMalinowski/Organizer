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
    private RecyclerView deviceRecyclerView;
    private DevicesRecyclerViewAdapter devicesRecyclerViewAdapter;
    private File data;
    private List<Device> devices;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new File(getApplicationContext().getFilesDir(), "devices_data.data");

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
                        Toast.makeText(getApplicationContext(), "Brak urządzeń", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(this, "Załadowano urządzenia", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Brak urządzeń", Toast.LENGTH_LONG).show();
                }
            } catch (IOException | ClassNotFoundException e) {
                closeAppWithFileError("Błąd odczytu danych.").show();
            }
        }

        if (devices != null) {
            final SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

            deviceRecyclerView = findViewById(R.id.list_of_devices_recycler_view);

            devicesRecyclerViewAdapter = new DevicesRecyclerViewAdapter(this, preferences, devices);
            devicesRecyclerViewAdapter.setRecyclerViewListener(this);

            deviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            deviceRecyclerView.setAdapter(devicesRecyclerViewAdapter);
            deviceRecyclerView.setItemAnimator(new DefaultItemAnimator());

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
        //nowe urządzenie
        if (item.getItemId() == R.id.new_device) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Nazwa urządzenia");

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

        //wybór koloru tła listy
        if (item.getItemId() == R.id.choose_color) {
            final SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);

            ColorPickerDialogBuilder
                    .with(this)
                    .setTitle("Kolor listy zmieni się po restarcie aplikacji")
                    .initialColor(preferences.getInt("list_color", Color.parseColor("#FFFACD")))
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setPositiveButton("ok", new ColorPickerClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                            preferences.edit().putInt("list_color", selectedColor).apply();
                            Toast.makeText(getApplicationContext(), "Kolor listy zmieni się po ponownym uruchomieniu aplikacji", Toast.LENGTH_LONG).show();
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
            Device device = (Device) data.getSerializableExtra("return_device");

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
     * Klikniecie powodujące przejście w tryb modyfikacji urządzenia
     *
     * @param view     element wywołujący
     * @param position pozycja urządzenia
     */
    @Override
    public void onModifyClick(View view, int position) {
        Intent intent = new Intent(this, DeviceActivity.class);
        intent.putExtra("current_device", devices.get(position));
        startActivityForResult(intent, position);
    }

    /**
     * Ruch powodujący usunięcie urządzenia
     *
     * @param view     element wywołujący
     * @param position pozycja urządzenia
     */
    @Override
    public void onDeleteMotion(View view, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Usuwanie urządzenia")
                .setMessage("Czy na pewno chcesz usunąć to urządzenie?")
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
                .setMessage("Aplikacja umożliwiwa zapisywanie informacji o zarządznych urządzeniach sieciowych. " +
                        "Po dotknięciu takiego urządzenia możliwa jest jego edycja i rozbudowanie go o notatkę oraz dodanie/zmiana/usuwanie obrazka. " +
                        "Do danego urządzenia możliwe jest także dodawanie szczegółych informacji poprzez dotknięcie odpowiedniego przycisku. " +
                        "Można modyfikować datę wraz z godziną ostatneiego przeglądu technicznego i lokalizację danego urządzenia. Dodatkowo po kliknięciu w przycisk z lokalizacją, zostaniemy przekierowani do aplikacji map Google w celu szybkiego odnalezienia urządzenia. " +
                        "Zapis danego urządzenia po modyfikacjach następuje po dotknięciu przycisku powrotu, zrobiono tak ze względu na wygode i bezpieczeństwo użytkownika. " +
                        "Usunięcie następuje poprzez dłuższe przytrzymanie danego urządzenia w widoku ekranu głównego. " +
                        "Umożliwiono także zmianę koloru tła listy z urządzeniami. " +
                        "Aby zobaczyć opis danego przycisku należy go przytrzymać. " +
                        "W razie wątpliwości można wrócić do tej informacji poprzez wybranie opcji \"Informacje i pomoc\" lub (?) (w zależności sposobu wyświetlania) w górnej belce aplikacji. " +
                        "\n\nAutor:\nJakub Malinowski\n\nVersion: 3.0");
    }
}