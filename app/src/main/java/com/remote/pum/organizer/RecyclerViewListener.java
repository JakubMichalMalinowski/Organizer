package com.remote.pum.organizer;

import android.view.View;

public interface RecyclerViewListener {
    void onModifyClick(View view, int position);
    void onDeleteMotion(View view, final int position);
}
