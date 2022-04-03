package kro.kr.rhya_network.online_attendance.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;

import kro.kr.rhya_network.online_attendance.R;
import kro.kr.rhya_network.online_attendance.core.RhyaApplication;
import kro.kr.rhya_network.online_attendance.fragment.AttendanceFragment;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceInfoVO;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceStudentVO;
import kro.kr.rhya_network.online_attendance.utils.RhyaAsyncTask;
import kro.kr.rhya_network.online_attendance.utils.RhyaDialogManager;
import kro.kr.rhya_network.online_attendance.utils.RhyaErrorCodeManager;
import kro.kr.rhya_network.online_attendance.utils.RhyaHttpsConnection;

public class RhyaAttendanceInfoRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // Auth Token
    private final String authToken;
    // Fragment
    private final Fragment fragment;
    // URL
    private final String url;
    // Dialog 관리자
    private final RhyaDialogManager rhyaDialogManager_Type0;
    private final RhyaDialogManager rhyaDialogManager_Type3;
    private final RhyaDialogManager rhyaDialogManager_Type5;
    // 오류 메시지 관리자
    private final RhyaErrorCodeManager rhyaErrorCodeManager;
    // Class name data
    private ArrayList<OnlineAttendanceInfoVO> inputValue;
    // Time
    private int time = 0;
    // Context
    private Context context;
    // Select list
    public ArrayList<String> stringOnlineAttendanceInfoStudentUUIDS;




    public RhyaAttendanceInfoRecyclerViewAdapter(String authToken, Activity activity, String url, Fragment fragment) {
        this.authToken = authToken;
        stringOnlineAttendanceInfoStudentUUIDS = new ArrayList<>();
        rhyaErrorCodeManager = new RhyaErrorCodeManager();
        rhyaDialogManager_Type0 = new RhyaDialogManager(
                activity,
                false,
                0.85,
                0);
        rhyaDialogManager_Type0.setDialogSettingType_0("작업 처리 중...");
        rhyaDialogManager_Type3 = new RhyaDialogManager(
                activity,
                true,
                0.95,
                3);
        rhyaDialogManager_Type5 = new RhyaDialogManager(
                activity,
                true,
                0.95,
                5);
        this.url = url;
        this.fragment = fragment;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_edit_1, parent, false);
        context = view.getContext();
        return new RhyaAttendanceInfoRecyclerViewAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RhyaAttendanceInfoRecyclerViewAdapter.ViewHolder) holder).onBind(inputValue.get(position));
    }



    @Override
    public int getItemCount() {
        return inputValue == null ? 0 : inputValue.size();
    }



    @SuppressLint("NotifyDataSetChanged")
    public void setInputValue(ArrayList<OnlineAttendanceInfoVO> inputValue, int time) {
        stringOnlineAttendanceInfoStudentUUIDS.clear();
        this.inputValue = inputValue;
        this.time = time;

        notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void selectAll() {
        stringOnlineAttendanceInfoStudentUUIDS.clear();
        for (int index = 0; index < inputValue.size(); index++) {
            OnlineAttendanceInfoVO onlineAttendanceInfoVO = inputValue.get(index);
            onlineAttendanceInfoVO.setChecked(true);

            stringOnlineAttendanceInfoStudentUUIDS.add(onlineAttendanceInfoVO.getUuid());
        }

        notifyDataSetChanged();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void selectCancel() {
        for (int index = 0; index < inputValue.size(); index++) {
            OnlineAttendanceInfoVO onlineAttendanceInfoVO = inputValue.get(index);
            onlineAttendanceInfoVO.setChecked(false);
        }

        stringOnlineAttendanceInfoStudentUUIDS.clear();

        notifyDataSetChanged();
    }



    private class ViewHolder extends RecyclerView.ViewHolder {
        // UI Object
        private final ImageView studentImage;
        private final TextView studentName;
        private final TextView studentClassAndNumberAndSex;
        private final TextView teacherName;
        private final ImageView attendanceInfoImage;
        private final TextView attendanceInfoTextView;
        private final ImageView selectButton;
        private final TextView studentDescription;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // UI Object
            studentImage = itemView.findViewById(R.id.studentImage);
            studentName = itemView.findViewById(R.id.studentName);
            studentClassAndNumberAndSex = itemView.findViewById(R.id.studentClassAndNumberAndSex);
            teacherName = itemView.findViewById(R.id.teacherName);
            attendanceInfoImage = itemView.findViewById(R.id.attendanceInfoImage);
            attendanceInfoTextView = itemView.findViewById(R.id.attendanceInfoTextView);
            selectButton = itemView.findViewById(R.id.selectButton);
            studentDescription = itemView.findViewById(R.id.studentDescription);

            itemView.setOnClickListener(view -> {
                int pos = getAbsoluteAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    OnlineAttendanceInfoVO onlineAttendanceInfoVO = inputValue.get(pos);
                    onlineAttendanceInfoVO.setChecked(!onlineAttendanceInfoVO.getChecked());

                    if (onlineAttendanceInfoVO.getChecked()) {
                        stringOnlineAttendanceInfoStudentUUIDS.add(onlineAttendanceInfoVO.getUuid());
                    }else {
                        stringOnlineAttendanceInfoStudentUUIDS.remove(onlineAttendanceInfoVO.getUuid());
                    }

                    notifyItemChanged(pos);
                }
            });

            itemView.setOnLongClickListener(view -> {
                int pos = getAbsoluteAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    final OnlineAttendanceInfoVO onlineAttendanceInfoVO = inputValue.get(pos);
                    final OnlineAttendanceStudentVO onlineAttendanceStudentVO = RhyaApplication.getOnlineAttendanceStudentVOHashMap().get(onlineAttendanceInfoVO.getUuid());

                    assert onlineAttendanceStudentVO != null;
                    rhyaDialogManager_Type5.setDialogSettingType_5(onlineAttendanceStudentVO, onlineAttendanceInfoVO, (uuid, editText) -> {
                        rhyaDialogManager_Type0.showDialog();

                        // 비동기 작업
                        new RhyaAsyncTask<String, String>() {
                            @Override
                            protected void onPreExecute() {
                            }

                            @Override
                            protected String doInBackground(String arg) {
                                try {
                                    RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
                                    ContentValues urlParameter = new ContentValues();
                                    // 파라미터 설정
                                    urlParameter.put("authToken", authToken);
                                    urlParameter.put("mode", 11);
                                    urlParameter.put("studentUUID", onlineAttendanceStudentVO.getUuid());
                                    urlParameter.put("date", onlineAttendanceInfoVO.getAttendance_date());
                                    if (editText.length() == 0) {
                                        urlParameter.put("description", URLEncoder.encode(Base64.encodeBase64String("[NoValue]".getBytes()), "UTF-8"));
                                    }else {
                                        urlParameter.put("description", URLEncoder.encode(Base64.encodeBase64String(editText.getBytes()), "UTF-8"));
                                    }

                                    // JSON 데이터 파싱
                                    JSONObject serverResponseJSONObject = new JSONObject(rhyaHttpsConnection.request(url, urlParameter));
                                    String serverResponse = serverResponseJSONObject.getString("result");
                                    if (serverResponse.equals("success")) {
                                        return "success";
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                                return null;
                            }

                            @Override
                            protected void onPostExecute(String result) {
                                try {
                                    if (result == null) {
                                        // 오류 메시지 출력
                                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                                "경고",
                                                rhyaErrorCodeManager.getErrorMessage(101),
                                                "확인",
                                                rhyaDialogManager_Type3::dismissDialog);
                                        rhyaDialogManager_Type0.dismissDialog();
                                        rhyaDialogManager_Type3.showDialog();
                                    }else {
                                        ((AttendanceFragment) fragment).setClassData(((AttendanceFragment) fragment).selectedClassData);

                                        rhyaDialogManager_Type0.dismissDialog();
                                    }
                                }catch (Exception ex) {
                                    ex.printStackTrace();

                                    // 오류 메시지 출력
                                    StringBuilder stringBuilder;
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(rhyaErrorCodeManager.getErrorMessage(999));
                                    stringBuilder.append(System.lineSeparator());
                                    stringBuilder.append(System.lineSeparator());
                                    stringBuilder.append(ex.getMessage());
                                    rhyaDialogManager_Type3.setDialogSettingType_3(
                                            "알 수 없는 오류",
                                            stringBuilder.toString(),
                                            "확인",
                                            rhyaDialogManager_Type3::dismissDialog);
                                    rhyaDialogManager_Type0.dismissDialog();
                                    rhyaDialogManager_Type3.showDialog();
                                }
                            }
                        }.execute(null);
                    });
                    rhyaDialogManager_Type5.showDialog();
                }

                return true;
            });
        }




        public void onBind(OnlineAttendanceInfoVO onlineAttendanceInfoVO) {
            try {
                if (onlineAttendanceInfoVO.getChecked()) {
                    selectButton.setVisibility(View.VISIBLE);
                }else {
                    selectButton.setVisibility(View.GONE);
                }

                OnlineAttendanceStudentVO onlineAttendanceStudentVO = RhyaApplication.getOnlineAttendanceStudentVOHashMap().get(onlineAttendanceInfoVO.getUuid());
                assert onlineAttendanceStudentVO != null;
                Glide.with(context)
                        .load(onlineAttendanceStudentVO.getImageURL(authToken))
                        .placeholder(R.drawable.attendance_black)
                        .error(R.drawable.attendance_black)
                        .fallback(R.drawable.attendance_black)
                        .signature(new ObjectKey(onlineAttendanceStudentVO.getVersion()))
                        .into(studentImage);

                studentName.setText(onlineAttendanceStudentVO.getName());

                StringBuilder stringBuilder;
                stringBuilder = new StringBuilder();
                stringBuilder.append(Objects.requireNonNull(RhyaApplication.getOnlineAttendanceClassVOHashMap().get(onlineAttendanceStudentVO.getClass_uuid())).getClass_nickname());
                stringBuilder.append(" / ");
                stringBuilder.append(onlineAttendanceStudentVO.getNumber());
                stringBuilder.append(" / ");
                if (onlineAttendanceStudentVO.getGender() == 1) {
                    stringBuilder.append("여자");
                }else {
                    stringBuilder.append("남자");
                }
                studentClassAndNumberAndSex.setText(stringBuilder.toString());

                if (!onlineAttendanceInfoVO.getNote().equals(onlineAttendanceInfoVO.NO_VALUE_TEXT)) {
                    studentDescription.setText(onlineAttendanceInfoVO.getNote());
                }else {
                    studentDescription.setText("");
                }

                attendanceInfoImage.setVisibility(View.VISIBLE);

                String valueTeacherName = null;
                int valueAttendance = -1;

                switch (time) {
                    case 1: {
                        valueTeacherName = onlineAttendanceInfoVO.getValue_1_teacher();
                        valueAttendance = onlineAttendanceInfoVO.getValue_1();

                        break;
                    }

                    case 2: {
                        valueTeacherName = onlineAttendanceInfoVO.getValue_2_teacher();
                        valueAttendance = onlineAttendanceInfoVO.getValue_2();

                        break;
                    }

                    case 3: {
                        valueTeacherName = onlineAttendanceInfoVO.getValue_3_teacher();
                        valueAttendance = onlineAttendanceInfoVO.getValue_3();

                        break;
                    }

                    case 4: {
                        valueTeacherName = onlineAttendanceInfoVO.getValue_4_teacher();
                        valueAttendance = onlineAttendanceInfoVO.getValue_4();

                        break;
                    }

                    case 5: {
                        valueTeacherName = onlineAttendanceInfoVO.getValue_5_teacher();
                        valueAttendance = onlineAttendanceInfoVO.getValue_5();

                        break;
                    }

                    case 6: {
                        valueTeacherName = onlineAttendanceInfoVO.getValue_6_teacher();
                        valueAttendance = onlineAttendanceInfoVO.getValue_6();

                        break;
                    }

                    case 7: {
                        valueTeacherName = onlineAttendanceInfoVO.getValue_7_teacher();
                        valueAttendance = onlineAttendanceInfoVO.getValue_7();

                        break;
                    }

                    case 8: {
                        valueTeacherName = onlineAttendanceInfoVO.getValue_8_teacher();
                        valueAttendance = onlineAttendanceInfoVO.getValue_8();

                        break;
                    }

                    case 9: {
                        valueTeacherName = onlineAttendanceInfoVO.getValue_9_teacher();
                        valueAttendance = onlineAttendanceInfoVO.getValue_9();

                        break;
                    }

                    case 10: {
                        valueTeacherName = onlineAttendanceInfoVO.getValue_10_teacher();
                        valueAttendance = onlineAttendanceInfoVO.getValue_10();

                        break;
                    }
                }

                if (valueTeacherName == null || valueAttendance == -1) {
                    attendanceInfoImage.setVisibility(View.GONE);
                    teacherName.setText("등록되지 않음");
                    attendanceInfoTextView.setText("등록되지 않음");
                }else {
                    int id = -1;
                    String result = null;

                    switch (valueAttendance) {
                        case 0:
                            id = R.drawable.ic_baseline_check_black_24;
                            result = "출석";

                            break;

                        case 13:
                            id = R.drawable.ic_attandance_1_1;
                            result = "결과(질병)";

                            break;


                        case 1:
                            id = R.drawable.ic_attandance_1_1;
                            result = "결석(질병)";

                            break;

                        case 14:
                            id = R.drawable.ic_attandance_1_2;
                            result = "결과(무단)";

                            break;

                        case 2:
                            id = R.drawable.ic_attandance_1_2;
                            result = "결석(무단)";

                            break;

                        case 15:
                            id = R.drawable.ic_attandance_1_3;
                            result = "결과(기타)";

                            break;

                        case 3:
                            id = R.drawable.ic_attandance_1_3;
                            result = "결석(기타)";

                            break;

                        case 16:
                            id = R.drawable.ic_attandance_1_4;
                            result = "결과(인정)";

                            break;

                        case 4:
                            id = R.drawable.ic_attandance_1_4;
                            result = "결석(인정)";

                            break;

                        case 5:
                            id = R.drawable.ic_attandance_2_1;
                            result = "지각(질병)";

                            break;

                        case 6:
                            id = R.drawable.ic_attandance_2_2;
                            result = "지각(무단)";

                            break;

                        case 7:
                            id = R.drawable.ic_attandance_2_3;
                            result = "지각(기타)";

                            break;

                        case 8:
                            id = R.drawable.ic_attandance_2_4;
                            result = "지각(인정)";

                            break;

                        case 9:
                            id = R.drawable.ic_attandance_3_1;
                            result = "조퇴(질병)";

                            break;

                        case 10:
                            id = R.drawable.ic_attandance_3_2;
                            result = "조퇴(무단)";

                            break;

                        case 11:
                            id = R.drawable.ic_attandance_3_3;
                            result = "조퇴(기타)";

                            break;

                        case 12:
                            id = R.drawable.ic_attandance_3_4;
                            result = "조퇴(인정)";

                            break;
                    }

                    Glide.with(context)
                            .load(id)
                            .placeholder(R.drawable.attendance_black)
                            .error(R.drawable.attendance_black)
                            .fallback(R.drawable.attendance_black)
                            .signature(new ObjectKey(onlineAttendanceStudentVO.getVersion()))
                            .into(attendanceInfoImage);
                    attendanceInfoTextView.setText(result);

                    valueTeacherName = Objects.requireNonNull(RhyaApplication.getOnlineAttendanceTeacherVOHashMap().get(valueTeacherName)).getName();
                    teacherName.setText(valueTeacherName);
                }
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
