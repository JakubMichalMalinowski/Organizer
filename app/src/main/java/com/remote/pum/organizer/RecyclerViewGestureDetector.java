package com.remote.pum.organizer;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Rozpoznawanie gestów na RecyclerView
 */
public class RecyclerViewGestureDetector extends GestureDetector.SimpleOnGestureListener {
    private RecyclerViewListener recyclerViewListener;
    private View view;
    private int position;

    /**
     * Przesunięcie notatki w lewo lub prawo
     *
     * @param e1        dotknięcie, rozpoczynające ruch
     * @param e2        przesunięcie
     * @param velocityX predkość wzdłuż osi x
     * @param velocityY predkosc wzdłuż osi y
     * @return true, jeżeli skonsumowane
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(e1.getX() - e2.getX()) > 10) {
            recyclerViewListener.onDeleteMotion(view, position);
            return true;
        }

        return false;
    }

    /**
     * Wystąpienie pojedynczego dotknięcia
     *
     * @param e dotkniecie
     * @return true, jeżeli skonsumowane
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        recyclerViewListener.onModifyClick(view, position);
        return true;
    }

    /**
     * Ustawienie obiektu implementującego interfejs RecyclerViewListener
     *
     * @param recyclerViewListener obiekt implementujący RecyclerViewListener
     */
    public void setRecyclerViewListener(RecyclerViewListener recyclerViewListener) {
        this.recyclerViewListener = recyclerViewListener;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}