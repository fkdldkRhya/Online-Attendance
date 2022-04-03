package kro.kr.rhya_network.online_attendance.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import kro.kr.rhya_network.online_attendance.R;

public class RhyaTimeSelectSpinerAdapterForAttendance extends ArrayAdapter<String> {
    private final Context context;
    private final String[] items;

    public RhyaTimeSelectSpinerAdapterForAttendance(final Context context,
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
                    R.layout.item_spinner_attendance_select, parent, false);
        }

        String title = items[position];


        TextView tv = convertView.findViewById(R.id.spinnerMessageTextView);
        tv.setText(title);

        setIcon(title, convertView);

        return convertView;
    }

    /**
     * 기본 스피너 View 정의
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_spinner_attendance_selected, parent, false);
        }

        String title = items[position];

        TextView tv = convertView.findViewById(R.id.spinnerMessageTextView);
        tv.setText(title);

        setIcon(title, convertView);

        return convertView;
    }


    // 아이콘 설정
    private void setIcon(String input, View convertView) {
        int id = -1;

        switch (input) {
            case "출석":
                id = R.drawable.ic_baseline_check_black_24;
                break;

            case "결과 - 질병":
            case "결석 - 질병":
                id = R.drawable.ic_attandance_1_1;
                break;
            case "결과 - 무단":
            case "결석 - 무단":
                id = R.drawable.ic_attandance_1_2;
                break;
            case "결과 - 기타":
            case "결석 - 기타":
                id = R.drawable.ic_attandance_1_3;
                break;
            case "결과 - 인정":
            case "결석 - 인정":
                id = R.drawable.ic_attandance_1_4;
                break;

            case "지각 - 질병":
                id = R.drawable.ic_attandance_2_1;
                break;
            case "지각 - 무단":
                id = R.drawable.ic_attandance_2_2;
                break;
            case "지각 - 기타":
                id = R.drawable.ic_attandance_2_3;
                break;
            case "지각 - 인정":
                id = R.drawable.ic_attandance_2_4;
                break;

            case "조퇴 - 질병":
                id = R.drawable.ic_attandance_3_1;
                break;
            case "조퇴 - 무단":
                id = R.drawable.ic_attandance_3_2;
                break;
            case "조퇴 - 기타":
                id = R.drawable.ic_attandance_3_3;
                break;
            case "조퇴 - 인정":
                id = R.drawable.ic_attandance_3_4;
                break;
        }

        Glide.with(context)
                .load(id)
                .fallback(R.drawable.attendance_black)
                .fallback(R.drawable.attendance_black)
                .fallback(R.drawable.attendance_black)
                .into(((ImageView) convertView.findViewById(R.id.spinnerMessageIconImageView)));
    }
}

