package kro.kr.rhya_network.online_attendance.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import kro.kr.rhya_network.online_attendance.R;

public class RhyaSnackBarStyle {
    public Snackbar getSnackBar(View mainLayout, String message, Context context) {
        Snackbar snackbar = Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.blue_1));
        snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.black_2));
        snackbar.setTextColor(Color.WHITE);

        return snackbar;
    }
}
