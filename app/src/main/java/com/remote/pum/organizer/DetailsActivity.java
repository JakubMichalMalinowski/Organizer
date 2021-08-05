package com.remote.pum.organizer;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Activity obsługujące wydarzenia
 */
public class DetailsActivity extends AppCompatActivity {
    //tablice i lista przechowująca elementy do spinnerów
    private static final Integer[] YEARS = new Integer[100];
    private static final Integer[] MONTHS = new Integer[12];
    private static final List<Integer> DAYS = new ArrayList<>();
    private static final Integer[] HOURS = new Integer[24];
    private static final Integer[] MINS = new Integer[60];

    private Device device;
    private TextView dateTextView;
    private TextView locationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        dateTextView = findViewById(R.id.date_text_view);
        locationButton = findViewById(R.id.location_button);

        device = (Device) getIntent().getSerializableExtra("current_device_for_details");

        if (device != null) {
            if (device.getDate() != null) {
                dateTextView.setText(device.getDate());
            } else {
                dateTextView.setText(R.string.no_data);
            }

            if (device.getLocation() != null && !device.getLocation().equals("")) {
                setLocation();
            } else {
                unsetLocation();
            }

            //uzupełnianie tablic i listy danymi

            for (int i = 0; i < YEARS.length; ++i) {
                YEARS[i] = i + 2000;
            }

            for (int i = 0; i < MONTHS.length; ++i) {
                MONTHS[i] = i + 1;
            }

            if (DAYS.isEmpty()) {
                for (int i = 0; i < 31; ++i) {
                    DAYS.add(i + 1);
                }
            }

            for (int i = 0; i < HOURS.length; ++i) {
                HOURS[i] = i;
            }

            for (int i = 0; i < MINS.length; ++i) {
                MINS[i] = i;
            }
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
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
        //edycja daty
        if (item.getItemId() == R.id.edit_date_menu_item) {
            View view = LayoutInflater.from(this).inflate(R.layout.edit_date_layout, null, false);

            final Spinner yearSpinner = view.findViewById(R.id.year_spinner);
            yearSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, YEARS));

            final Spinner monthSpinner = view.findViewById(R.id.month_spinner);
            monthSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MONTHS));

            final Spinner daySpinner = view.findViewById(R.id.day_spinner);
            setDaysNumber(yearSpinner, monthSpinner, daySpinner);

            if (device.getDate() == null) {
                yearSpinner.setSelection(Calendar.getInstance().get(Calendar.YEAR) - 2000);
                monthSpinner.setSelection(Calendar.getInstance().get(Calendar.MONTH));
            } else {
                yearSpinner.setSelection(device.getDateYear() - 2000);
                monthSpinner.setSelection(device.getDateMonth());
            }

            yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    setDaysNumber(yearSpinner, monthSpinner, daySpinner);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    setDaysNumber(yearSpinner, monthSpinner, daySpinner);
                }
            });

            monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    setDaysNumber(yearSpinner, monthSpinner, daySpinner);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    setDaysNumber(yearSpinner, monthSpinner, daySpinner);
                }
            });

            final Spinner hourSpinner = view.findViewById(R.id.hour_spinner);
            hourSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, HOURS));

            final Spinner minSpinner = view.findViewById(R.id.min_spinner);
            minSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MINS));

            if (device.getDate() == null) {
                hourSpinner.setSelection(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                minSpinner.setSelection(Calendar.getInstance().get(Calendar.MINUTE));
            } else {
                hourSpinner.setSelection(device.getDateHour());
                minSpinner.setSelection(device.getDateMin());
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edycja daty")
                    .setView(view)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            device.setDate((int) yearSpinner.getSelectedItem(), (int) monthSpinner.getSelectedItem(), (int) daySpinner.getSelectedItem(), (int) hourSpinner.getSelectedItem(), (int) minSpinner.getSelectedItem());
                            dateTextView.setText(device.getDate());
                        }
                    })
                    .setNegativeButton("Usuń datę", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            device.setDate(null);
                            dateTextView.setText(R.string.no_data);
                        }
                    }).create().show();
        }

        //edycja lokalizacji
        if (item.getItemId() == R.id.edit_location_menu_item) {
            final View view = getLayoutInflater().inflate(R.layout.edit_location_layout, null, false);
            final EditText locationEditText = view.findViewById(R.id.location_edit_text);

            if (device.getLocation() != null && !device.getLocation().equals("")) {
                locationEditText.setText(device.getLocation());
            }

            new AlertDialog.Builder(this).setTitle("Lokalizacja")
                    .setView(view)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            device.setLocation(locationEditText.getText().toString());

                            if (device.getLocation().equals("")) {
                                unsetLocation();
                            } else {
                                setLocation();
                            }
                        }
                    })
                    .setNegativeButton("Usuń lokalizację", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            device.setLocation("");
                            unsetLocation();
                        }
                    }).create().show();
        }

        return true;
    }

    private void setLocation() {
        final String url = "https://www.google.com/maps/search/?api=1&query=" + device.getLocation();
        locationButton.setText(device.getLocation());
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
    }

    private void unsetLocation() {
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), R.string.no_data, Toast.LENGTH_LONG).show();
            }
        });
        locationButton.setText(R.string.no_data);
    }

    /**
     * Ustawienie ilości dni w zależności od miesiąca i roku
     *
     * @param yearSpinner  spinner wskazujący rok
     * @param monthSpinner spinner wskazujący miesiąc
     * @param daySpinner   spinner wskazujący dzień
     */
    private void setDaysNumber(Spinner yearSpinner, Spinner monthSpinner, Spinner daySpinner) {
        int month = ((int) monthSpinner.getSelectedItem());
        int year = ((int) yearSpinner.getSelectedItem());

        //miesiące z 31 dniami
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            daySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DAYS));
        } else {
            //luty
            if (month == 2) {
                //przestępny
                if ((year % 4) == 0) {
                    daySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DAYS.subList(0, 29)));
                } else {
                    daySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DAYS.subList(0, 28)));
                }
            } else {
                daySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DAYS.subList(0, 30)));
            }
        }

        if (device.getDate() == null) {
            daySpinner.setSelection(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1);
        } else {
            daySpinner.setSelection(device.getDateDay() - 1);
        }
    }

    /**
     * Wywoływana gdy użytkownik naciśnie przycisk powrotu, zwracjąca wartość do Activity-rodzica, zwraca urządzenie z modyfikacjami szczegółów
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("device_return_from_details", device);
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }
}