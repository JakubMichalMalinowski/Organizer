package com.remote.pum.organizer;

import android.content.DialogInterface;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Activity obsługujące wydarzenia
 */
public class EventActivity extends AppCompatActivity {
    //tablice i lista przechowująca elementy do spinnerów
    private static final Integer[] YEARS = new Integer[100];
    private static final Integer[] MONTHS = new Integer[12];
    private static final List<Integer> DAYS = new ArrayList<>();
    private static final Integer[] HOURS = new Integer[24];
    private static final Integer[] MINS = new Integer[60];

    private Note note;
    private TextView dateTextView;
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        dateTextView = findViewById(R.id.date_text_view);
        locationTextView = findViewById(R.id.location_text_view);

        note = (Note) getIntent().getSerializableExtra("current_note_for_event");

        if (note != null) {
            if (note.getDate() != null) {
                dateTextView.setText(note.getDate());
            } else {
                dateTextView.setText(R.string.no_data);
            }

            if (note.getLocation() != null && !note.getLocation().equals("")) {
                locationTextView.setText(note.getLocation());
            } else {
                locationTextView.setText(R.string.no_data);
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
        inflater.inflate(R.menu.event_menu, menu);
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

            if (note.getDate() == null) {
                yearSpinner.setSelection(Calendar.getInstance().get(Calendar.YEAR) - 2000);
                monthSpinner.setSelection(Calendar.getInstance().get(Calendar.MONTH));
            } else {
                yearSpinner.setSelection(note.getDateYear() - 2000);
                monthSpinner.setSelection(note.getDateMonth());
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

            if (note.getDate() == null) {
                hourSpinner.setSelection(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
                minSpinner.setSelection(Calendar.getInstance().get(Calendar.MINUTE));
            } else {
                hourSpinner.setSelection(note.getDateHour());
                minSpinner.setSelection(note.getDateMin());
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edycja daty")
                    .setView(view)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            note.setDate((int) yearSpinner.getSelectedItem(), (int) monthSpinner.getSelectedItem(), (int) daySpinner.getSelectedItem(), (int) hourSpinner.getSelectedItem(), (int) minSpinner.getSelectedItem());
                            dateTextView.setText(note.getDate());
                        }
                    })
                    .setNegativeButton("Usuń datę", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            note.setDate(null);
                            dateTextView.setText(R.string.no_data);
                        }
                    }).create().show();
        }

        //edycja lokalizacji
        if (item.getItemId() == R.id.edit_location_menu_item) {
            final View view = getLayoutInflater().inflate(R.layout.edit_location_layout, null, false);
            final EditText locationEditText = view.findViewById(R.id.location_edit_text);

            if (note.getLocation() != null && !note.getLocation().equals("")) {
                locationEditText.setText(note.getLocation());
            }

            new AlertDialog.Builder(this).setTitle("Lokalizacja")
                    .setView(view)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            note.setLocation(locationEditText.getText().toString());

                            if (note.getLocation().equals("")) {
                                locationTextView.setText(R.string.no_data);
                            } else {
                                locationTextView.setText(note.getLocation());
                            }
                        }
                    })
                    .setNegativeButton("Usuń lokalizację", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            note.setLocation("");
                            locationTextView.setText(R.string.no_data);
                        }
                    }).create().show();
        }

        return true;
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

        if (note.getDate() == null) {
            daySpinner.setSelection(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1);
        } else {
            daySpinner.setSelection(note.getDateDay() - 1);
        }
    }

    /**
     * Wywoływana gdy użytkownik naciśnie przycisk powrotu, zwracjąca wartość do Activity-rodzica, zwraca notatkę z modyfikacjami wydarzenia
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("note_return_from_event", note);
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }
}