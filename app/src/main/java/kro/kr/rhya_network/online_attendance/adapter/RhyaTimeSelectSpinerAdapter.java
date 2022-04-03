package kro.kr.rhya_network.online_attendance.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import kro.kr.rhya_network.online_attendance.R;

public class RhyaTimeSelectSpinerAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] items;

    public RhyaTimeSelectSpinerAdapter(final Context context,
                          final int textViewResourceId, final String[] objects) {
        super(context, textViewResourceId, objects);
        this.items = objects;
        this.context = context;
    }

    /**
     * 스피너 클릭시 보여지는 View의 정의
     */
    @Override
    public View getDropDownView(int position, View convertView,
                                @NonNull ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(
                    R.layout.item_spinner_time_select, parent, false);
        }

        TextView tv = convertView.findViewById(R.id.spinnerMessageTextView);
        tv.setText(items[position]);


        return convertView;
    }

    /**
     * 기본 스피너 View 정의
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_spinner_time_selected, parent, false);
        }

        TextView tv = convertView.findViewById(R.id.spinnerMessageTextView);
        tv.setText(items[position]);


        return convertView;
    }
}
