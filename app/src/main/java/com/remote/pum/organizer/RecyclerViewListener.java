package com.remote.pum.organizer;

import android.view.View;

/**
 * Interfejs pozwalający zaimplementować sposób zachowania przy gestach wywołanych na RecyclerView
 */
public interface RecyclerViewListener {
    /**
     * Działania przy akcji powodującej modyfikację notatki
     *
     * @param view     view - źródło zdarzenia
     * @param position pozycja w zbiorze danych
     */
    void onModifyClick(View view, int position);

    /**
     * Działania przy zdarzeniu powodującym usunięcie notatki
     *
     * @param view     view - źródło zdarzenia
     * @param position pozycja w zbiorze danych
     */
    void onDeleteMotion(View view, final int position);
}