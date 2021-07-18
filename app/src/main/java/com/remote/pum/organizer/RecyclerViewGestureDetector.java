package com.remote.pum.organizer;

import android.util.Log;
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

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
        recyclerViewListener.onDeleteMotion(view, position);
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