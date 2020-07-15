package com.remote.pum.organizer;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerViewGestureDetector extends GestureDetector.SimpleOnGestureListener {
    private RecyclerViewListener recyclerViewListener;
    private View view;
    private int position;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(e1.getX() - e2.getX()) > 10) {
            recyclerViewListener.onDeleteMotion(view, position);
            return true;
        }

        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        recyclerViewListener.onModifyClick(view, position);
        return true;
    }

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
