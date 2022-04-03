package kro.kr.rhya_network.online_attendance.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import java.util.ArrayList;
import java.util.Objects;

import kro.kr.rhya_network.online_attendance.R;
import kro.kr.rhya_network.online_attendance.core.RhyaApplication;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceStudentVO;

public class RhyaClassInfoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Fragment
    private final Fragment fragment;
    // Auth Token
    private final String authToken;
    // Class name data
    private ArrayList<OnlineAttendanceStudentVO> inputValue;
    // Context
    private Context context;




    public RhyaClassInfoRecyclerViewAdapter(Fragment fragment, String authToken) {
        this.fragment = fragment;
        this.authToken = authToken;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_info, parent, false);
        context = view.getContext();

        return new RhyaClassInfoRecyclerViewAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OnlineAttendanceStudentVO value = inputValue.get(position);

        ((RhyaClassInfoRecyclerViewAdapter.ViewHolder) holder).onBind(value);
    }



    @Override
    public int getItemCount() {
        return inputValue == null ? 0 : inputValue.size();
    }



    @SuppressLint("NotifyDataSetChanged")
    public void setInputValue(ArrayList<OnlineAttendanceStudentVO> inputValue) {
        this.inputValue = inputValue;

        notifyDataSetChanged();
    }



    private class ViewHolder extends RecyclerView.ViewHolder {
        // UI Object
        private final ImageView imageView;
        private final TextView studentName;
        private final TextView studentInfo;
        private final TextView studentSex;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            imageView = itemView.findViewById(R.id.studentImage);
            studentName = itemView.findViewById(R.id.studentName);
            studentInfo = itemView.findViewById(R.id.studentInfo);
            studentSex = itemView.findViewById(R.id.studentSex);
        }



        public void onBind(OnlineAttendanceStudentVO value) {
            if (RhyaApplication.getOnlineAttendanceClassVOHashMap().containsKey(value.getClass_uuid())) {
                Glide.with(context)
                        .load(value.getImageURL(authToken))
                        .placeholder(R.drawable.attendance_black)
                        .error(R.drawable.attendance_black)
                        .fallback(R.drawable.attendance_black)
                        .signature(new ObjectKey(value.getVersion()))
                        .into(imageView);
                studentName.setText(value.getName());

                String classTxt = Objects.requireNonNull(RhyaApplication.getOnlineAttendanceClassVOHashMap().get(value.getClass_uuid())).getClass_nickname();

                StringBuilder stringBuilder;
                stringBuilder = new StringBuilder();
                stringBuilder.append(value.getNumber());
                stringBuilder.append(" 번 / ");
                stringBuilder.append(classTxt);

                // 색상
                final int colorBlue2 = ContextCompat.getColor(context, R.color.blue_2);

                SpannableString spannableString = new SpannableString(stringBuilder.toString());
                spannableString.setSpan(new ForegroundColorSpan(colorBlue2), 0, String.valueOf(value.getNumber()).length() + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                studentInfo.setText(spannableString);

                if (value.getGender() == 1)
                    studentSex.setText("여");
                else
                    studentSex.setText("남");
            }
        }
    }
}
