package kro.kr.rhya_network.online_attendance.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import kro.kr.rhya_network.online_attendance.R;
import kro.kr.rhya_network.online_attendance.core.RhyaApplication;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceInfoVO;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceStudentVO;

public class RhyaAttendanceTableRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Class name data
    private ArrayList<OnlineAttendanceInfoVO> inputValue;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == -2)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_table_column, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_table_row, parent, false);

        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RhyaAttendanceTableRecyclerViewAdapter.ViewHolder) {
            ((RhyaAttendanceTableRecyclerViewAdapter.ViewHolder) holder).onBind(inputValue.get(position));
        }else {
            ((RhyaAttendanceTableRecyclerViewAdapter.ViewHolder2) holder).onBind();
        }
    }



    @Override
    public int getItemViewType(int position) {
        if (inputValue.get(position) == null) {
            return -2;
        }else {
            return position;
        }
    }



    @Override
    public int getItemCount() {
        return inputValue == null ? 0 : inputValue.size();
    }



    @SuppressLint("NotifyDataSetChanged")
    public void setInputValue(ArrayList<OnlineAttendanceInfoVO> inputValue) {
        this.inputValue = inputValue;

        notifyDataSetChanged();
    }




    private class ViewHolder extends RecyclerView.ViewHolder {
        // UI Object
        private final TextView studentName;
        private final TextView studentValue1;
        private final TextView studentValue2;
        private final TextView studentValue3;
        private final TextView studentValue4;
        private final TextView studentValue5;
        // ViewHolder
        private int setValue1;
        private int setValue2;
        private int setValue3;
        private int setValue4;
        private int setValue5;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // UI Object
            studentName = itemView.findViewById(R.id.studentName);
            studentValue1 = itemView.findViewById(R.id.studentValue1);
            studentValue2 = itemView.findViewById(R.id.studentValue2);
            studentValue3 = itemView.findViewById(R.id.studentValue3);
            studentValue4 = itemView.findViewById(R.id.studentValue4);
            studentValue5 = itemView.findViewById(R.id.studentValue5);
        }


        public void onBind(OnlineAttendanceInfoVO onlineAttendanceInfoVO) {
            try {
                studentName.setText(Objects.requireNonNull(RhyaApplication.getOnlineAttendanceStudentVOHashMap().get(onlineAttendanceInfoVO.getUuid())).getName());

                setValue1 = -1;
                setValue2 = -1;
                setValue3 = -1;
                setValue4 = -1;
                setValue5 = -1;

                setValue(onlineAttendanceInfoVO);

                if (setValue1 != -1)
                    studentValue1.setText(String.valueOf(setValue1));
                else
                    studentValue1.setText(R.string.null_text);

                if (setValue2 != -1)
                    studentValue2.setText(String.valueOf(setValue2));
                else
                    studentValue2.setText(R.string.null_text);

                if (setValue3 != -1)
                    studentValue3.setText(String.valueOf(setValue3));
                else
                    studentValue3.setText(R.string.null_text);

                if (setValue4 != -1)
                    studentValue4.setText(String.valueOf(setValue4));
                else
                    studentValue4.setText(R.string.null_text);

                if (setValue5 != -1)
                    studentValue5.setText(String.valueOf(setValue5));
                else
                    studentValue5.setText(R.string.null_text);
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        /*
            value - 0  : 출석
            - 1  : 결석 - 질병
            - 2  : 결석 - 무단
            - 3  : 결석 - 기타
            - 4  : 결석 - 인정

            - 5  : 지각 - 질병
            - 6  : 지각 - 무단
            - 7  : 지각 - 기타
            - 8  : 지각 - 인정

            - 9  : 조퇴 - 질병
            - 10 : 조퇴 - 무단
            - 11 : 조퇴 - 기타
            - 12 : 조퇴 - 인정

            - 13 : 결과 - 질병
            - 14 : 결과 - 무단
            - 15 : 결과 - 기타
            - 16 : 결과 - 인정
         */
        private void setValue(OnlineAttendanceInfoVO onlineAttendanceInfoVO) {
            int value1 = onlineAttendanceInfoVO.getValue_1();
            int value2 = onlineAttendanceInfoVO.getValue_2();
            int value3 = onlineAttendanceInfoVO.getValue_3();
            int value4 = onlineAttendanceInfoVO.getValue_4();
            int value5 = onlineAttendanceInfoVO.getValue_5();
            int value6 = onlineAttendanceInfoVO.getValue_6();
            int value7 = onlineAttendanceInfoVO.getValue_7();
            int value8 = onlineAttendanceInfoVO.getValue_8();
            int value9 = onlineAttendanceInfoVO.getValue_9();
            int value10 = onlineAttendanceInfoVO.getValue_10();

            resultExec(value1);
            resultExec(value2);
            resultExec(value3);
            resultExec(value4);
            resultExec(value5);
            resultExec(value6);
            resultExec(value7);
            resultExec(value8);
            resultExec(value9);
            resultExec(value10);
        }

        private void resultExec(int input) {
            switch (input) {
                case 0:
                    if (setValue1 == -1)
                        setValue1 = 0;

                    setValue1 ++;
                    break;

                case 1:
                case 2:
                case 3:
                case 4: {
                    if (setValue2 == -1)
                        setValue2 = 0;

                    setValue2 ++;
                    break;
                }

                case 5:
                case 6:
                case 7:
                case 8: {
                    if (setValue3 == -1)
                        setValue3 = 0;

                    setValue3 ++;
                    break;
                }

                case 9:
                case 10:
                case 11:
                case 12: {
                    if (setValue4 == -1)
                        setValue4 = 0;

                    setValue4 ++;
                    break;
                }

                case 13:
                case 14:
                case 15:
                case 16: {
                    if (setValue5 == -1)
                        setValue5 = 0;

                    setValue5 ++;
                    break;
                }
            }
        }
    }




    private class ViewHolder2 extends RecyclerView.ViewHolder {
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
        }
        public void onBind() {}
    }
}
