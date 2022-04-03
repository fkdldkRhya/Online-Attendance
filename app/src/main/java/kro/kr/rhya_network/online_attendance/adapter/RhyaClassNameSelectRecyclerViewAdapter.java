package kro.kr.rhya_network.online_attendance.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import kro.kr.rhya_network.online_attendance.R;
import kro.kr.rhya_network.online_attendance.core.RhyaApplication;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceClassVO;

public class RhyaClassNameSelectRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Class name data
    private final ArrayList<OnlineAttendanceClassVO> inputValue;
    // Context
    private Context context;
    // Select class name
    public int selectedPos = -1;




    public RhyaClassNameSelectRecyclerViewAdapter(ArrayList<OnlineAttendanceClassVO> inputValue) {
        this.inputValue = inputValue;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_name_select, parent, false);
        context = view.getContext();

        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).onBind(inputValue.get(position));
    }



    @Override
    public int getItemCount() {
        return inputValue == null ? 0 : inputValue.size();
    }




    private class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView className;
        private final TextView teacherNameTextView;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;

            className = itemView.findViewById(R.id.classNameTextView);
            teacherNameTextView = itemView.findViewById(R.id.teacherNameTextView);

            itemView.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    OnlineAttendanceClassVO onlineAttendanceClassVO = inputValue.get(pos);

                    if (selectedPos == pos) {
                        selectedPos = -1;
                        onlineAttendanceClassVO.setChecked(false);

                        notifyItemChanged(pos);

                        return;
                    }

                    if (selectedPos != -1) {
                        OnlineAttendanceClassVO onlineAttendanceClassVOSelected = inputValue.get(selectedPos);
                        onlineAttendanceClassVOSelected.setChecked(false);

                        notifyItemChanged(selectedPos);
                    }

                    selectedPos = pos;

                    onlineAttendanceClassVO.setChecked(!onlineAttendanceClassVO.getChecked());

                    notifyItemChanged(pos);
                }
            });
        }



        public void onBind(OnlineAttendanceClassVO value) {
            className.setText(value.getClass_nickname());

            if (RhyaApplication.getOnlineAttendanceTeacherVOHashMap().containsKey(value.getClass_teacher_uuid()))
                teacherNameTextView.setText(Objects.requireNonNull(RhyaApplication.getOnlineAttendanceTeacherVOHashMap().get(value.getClass_teacher_uuid())).getName());

            if (value.getChecked()) {
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.white_5));
            }else {
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }
        }
    }
}
