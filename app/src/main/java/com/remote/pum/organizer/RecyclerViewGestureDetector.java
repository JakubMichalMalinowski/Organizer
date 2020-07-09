package com.remote.pum.organizer;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class RecyclerViewGestureDetector extends GestureDetector.SimpleOnGestureListener {
    private static boolean toDelete = false;

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(e1.getX() - e2.getX()) > 10) {
            toDelete = true;
            return true;
        }

        return false;
    }

    public static void setToDelete(boolean toDelete) {
        RecyclerViewGestureDetector.toDelete = toDelete;
    }

    public static boolean isToDelete() {
        return toDelete;
    }
}
