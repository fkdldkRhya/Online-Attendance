package kro.kr.rhya_network.online_attendance.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.applikeysolutions.cosmocalendar.selection.SingleSelectionManager;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.tabs.TabLayout;
import com.ornach.nobobutton.NoboButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import kro.kr.rhya_network.online_attendance.R;
import kro.kr.rhya_network.online_attendance.adapter.RhyaAttendanceInfoRecyclerViewAdapter;
import kro.kr.rhya_network.online_attendance.adapter.RhyaAttendanceTableRecyclerViewAdapter;
import kro.kr.rhya_network.online_attendance.adapter.RhyaClassInfoRecyclerViewAdapter;
import kro.kr.rhya_network.online_attendance.adapter.RhyaTimeSelectSpinerAdapter;
import kro.kr.rhya_network.online_attendance.adapter.RhyaTimeSelectSpinerAdapterForAttendance;
import kro.kr.rhya_network.online_attendance.core.RhyaApplication;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceClassVO;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceInfoVO;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceStudentVO;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceTeacherVO;
import kro.kr.rhya_network.online_attendance.utils.RhyaAsyncTask;
import kro.kr.rhya_network.online_attendance.utils.RhyaDialogManager;
import kro.kr.rhya_network.online_attendance.utils.RhyaErrorCodeManager;
import kro.kr.rhya_network.online_attendance.utils.RhyaHttpsConnection;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AttendanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AttendanceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "authToken";
    private static final String ARG_PARAM2 = "myInfo";
    private static final String ARG_PARAM3 = "url";
    // TODO: Rename and change types of parameters
    private String authToken;
    private String url;
    // Context
    private Context context;
    // Dialog ?????????
    private RhyaDialogManager rhyaDialogManager_Type0;
    private RhyaDialogManager rhyaDialogManager_Type3;
    private RhyaDialogManager rhyaDialogManager_Type4;
    // ?????? ????????? ?????????
    private RhyaErrorCodeManager rhyaErrorCodeManager;
    // UI Object
    private CalendarView calendarView;
    private TextView showDateTextView;
    private TextView selectedClassName;
    private ImageView teacherImage;
    private TextView teacherName;
    private TextView teacherDepartment;
    private TextView teacherPhoneNumber;
    private TextView teacherEmailAddress;
    private TabLayout subTabLayout;
    private Spinner spinnerTimeSelect;
    private Spinner spinnerAttendanceCheck;
    private NoboButton attendanceCheckButton;
    private TextView recyclerViewTitleTextView;
    private RecyclerView studentInfoRecyclerView;
    private RecyclerView attendanceInfoRecyclerView;
    private RecyclerView attendanceTableRecyclerView;
    private LinearLayoutCompat attendanceLayout;
    // Adapter
    private RhyaClassInfoRecyclerViewAdapter rhyaClassInfoRecyclerViewAdapter;
    private RhyaAttendanceTableRecyclerViewAdapter rhyaAttendanceTableRecyclerViewAdapter;
    private RhyaAttendanceInfoRecyclerViewAdapter rhyaAttendanceInfoRecyclerViewAdapter;
    // Fragment ??????
    private ArrayList<OnlineAttendanceStudentVO> selectedClassDataStudentList = null;
    public OnlineAttendanceClassVO selectedClassData = null;
    private String selectedValue = null;
    private int timeSelectPos = -1;
    private int loadDataType = 0;




    public AttendanceFragment() {
        // Required empty public constructor
    }



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AttendanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendanceFragment newInstance(String param1, OnlineAttendanceTeacherVO param2, String param3) {
        AttendanceFragment fragment = new AttendanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putParcelable(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            authToken = getArguments().getString(ARG_PARAM1);
            url = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attendance, container, false);
    }



    // =======================================================================
    // =======================================================================
    // =======================================================================
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // ?????? ?????????
        context = view.getContext();
        rhyaErrorCodeManager = new RhyaErrorCodeManager();
        rhyaClassInfoRecyclerViewAdapter = new RhyaClassInfoRecyclerViewAdapter(this, authToken);
        selectedClassDataStudentList = new ArrayList<>();


        // Dialog ????????? ?????????
        rhyaDialogManager_Type0 = new RhyaDialogManager(
                 getActivity(),
                false,
                0.85,
                0);
        rhyaDialogManager_Type0.setDialogSettingType_0("?????? ?????? ???...");
        rhyaDialogManager_Type3 = new RhyaDialogManager(
                getActivity(),
                false,
                0.95,
                3);
        rhyaDialogManager_Type4 = new RhyaDialogManager(
                getActivity(),
                true,
                0.95,
                4);


        // UI ?????????
        UIObjectInit(view);
    }
    // =======================================================================
    // =======================================================================
    // =======================================================================



    // ===========================================================
    /**
     * UI ????????? ??????
     */
    // ===========================================================
    private void UIObjectInit(View view) {
        // ?????? ??????
        try {
            // UI Object
            calendarView = view.findViewById(R.id.calendarView);
            showDateTextView = view.findViewById(R.id.showDateTextView);
            selectedClassName = view.findViewById(R.id.selectedClassName);
            final Button classNameSelectButton = view.findViewById(R.id.classNameSelectButton);
            studentInfoRecyclerView = view.findViewById(R.id.studentInfoRecyclerView);
            subTabLayout = view.findViewById(R.id.subTapLayout);
            teacherImage = view.findViewById(R.id.teacherImage);
            teacherName = view.findViewById(R.id.teacherName);
            teacherDepartment = view.findViewById(R.id.teacherDepartment);
            teacherPhoneNumber = view.findViewById(R.id.teacherPhoneNumber);
            teacherEmailAddress = view.findViewById(R.id.teacherEmailAddress);
            spinnerTimeSelect = view.findViewById(R.id.spinnerTimeSelect);
            recyclerViewTitleTextView = view.findViewById(R.id.recyclerViewTitleTextView);
            attendanceLayout = view.findViewById(R.id.attendanceLayout);
            spinnerAttendanceCheck = view.findViewById(R.id.spinnerAttendanceCheck);
            attendanceInfoRecyclerView = view.findViewById(R.id.attendanceInfoRecyclerView);
            attendanceTableRecyclerView = view.findViewById(R.id.attendanceTableRecyclerView);
            attendanceCheckButton = view.findViewById(R.id.attendanceCheckButton);
            final ConstraintLayout attendanceLayoutForC = view.findViewById(R.id.attendanceLayoutForC);
            final NoboButton callButton = view.findViewById(R.id.callButton);
            final NoboButton allSelectButton = view.findViewById(R.id.allSelectButton);
            final NoboButton selectCancelButton = view.findViewById(R.id.selectCancelButton);




            // ??????
            // ==========================================
            final int colorWhite = ContextCompat.getColor(context, R.color.white);
            final int colorWhite2 = ContextCompat.getColor(context, R.color.white_2);
            final int colorBlue1 = ContextCompat.getColor(context, R.color.blue_1);
            final int colorBlue2 = ContextCompat.getColor(context, R.color.blue_2);
            final int colorBlue3 = ContextCompat.getColor(context, R.color.blue_3);
            final int colorBlack2 = ContextCompat.getColor(context, R.color.black_2);
            // ==========================================


            // ?????? ?????????
            calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);
            calendarView.setCalendarBackgroundColor(colorWhite);
            calendarView.setOtherDayTextColor(colorWhite2);
            calendarView.setWeekendDayTextColor(colorBlue1);
            calendarView.setCurrentDayIconRes(R.drawable.ic_triangle_black);
            calendarView.setCurrentDayTextColor(colorBlue2);
            calendarView.setSelectedDayBackgroundColor(colorBlue3);
            calendarView.setDayTextColor(colorBlack2);
            calendarView.setMonthTextColor(colorBlack2);
            calendarView.setFirstDayOfWeek(1);
            calendarView.setWeekendDays(new HashSet(){{
                add(Calendar.SUNDAY);
                add(Calendar.SATURDAY);
            }});
            // ?????? ????????? ??????
            calendarView.setSelectionManager(new SingleSelectionManager(() -> {
                // ?????? ?????????
                List<Calendar> days = calendarView.getSelectedDates();

                // ?????? ??????
                if (days.size() > 0) {
                    Calendar calendar = days.get(0);
                    final int day = calendar.get(Calendar.DAY_OF_MONTH);
                    final int month = calendar.get(Calendar.MONTH);
                    final int year = calendar.get(Calendar.YEAR);
                    String week = new SimpleDateFormat("EE", Locale.KOREA).format(calendar.getTime());

                    StringBuilder stringBuilder;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(year);
                    stringBuilder.append("??? ");
                    stringBuilder.append((month + 1));
                    stringBuilder.append("??? ");
                    stringBuilder.append(day);
                    stringBuilder.append("??? ");
                    stringBuilder.append(week);
                    stringBuilder.append("??????");

                    showDateTextView.setText(stringBuilder.toString());

                    stringBuilder.setLength(0);

                    stringBuilder.append(year);
                    stringBuilder.append("-");
                    stringBuilder.append(String.format(Locale.KOREA, "%02d", month + 1));
                    stringBuilder.append("-");
                    stringBuilder.append(String.format(Locale.KOREA, "%02d", day));

                    selectedValue = stringBuilder.toString();

                    if (loadDataType == 1 && selectedClassData != null)
                        setClassData(selectedClassData);
                }
            }));

            // ?????? TextView ?????????
            showDateTextView.setText("????????? ????????? ?????????.");

            // Adapter ?????????
            GridLayoutManager studentInfoRecyclerViewGridLayoutManager = new GridLayoutManager(getActivity(), 3);
            studentInfoRecyclerView.setLayoutManager(studentInfoRecyclerViewGridLayoutManager);
            studentInfoRecyclerView.setAdapter(rhyaClassInfoRecyclerViewAdapter);

            // Class name select ?????????
            ArrayList<OnlineAttendanceClassVO> onlineAttendanceClassVOS = new ArrayList<>();
            List<Map.Entry<String, OnlineAttendanceClassVO>> list_entries = new ArrayList<>(RhyaApplication.getOnlineAttendanceClassVOHashMap().entrySet());
            Collections.sort(list_entries, (obj1, obj2) -> {
                final String splitTextValue = "-";

                String[] splitForObj1 = obj1.getValue().getClass_nickname().split(splitTextValue);
                String[] splitForObj2 = obj2.getValue().getClass_nickname().split(splitTextValue);
                if (splitForObj1[0].equals(splitForObj2[0])) {
                    return Integer.compare(
                            Integer.parseInt(splitForObj1[1]),
                            Integer.parseInt(splitForObj2[1]));
                }else {
                    return Integer.compare(
                            Integer.parseInt(splitForObj1[0]),
                            Integer.parseInt(splitForObj2[0]));
                }
            });
            for (Map.Entry<String, OnlineAttendanceClassVO> map : list_entries)
                onlineAttendanceClassVOS.add(map.getValue());

            // Class name select button ?????????
            rhyaDialogManager_Type4.setDialogSettingType_4(onlineAttendanceClassVOS, tempVO -> {
                setClassData(tempVO); // ????????? ??????

                rhyaDialogManager_Type4.dismissDialog();
            });
            classNameSelectButton.setOnClickListener(v -> rhyaDialogManager_Type4.showDialog());

            // Call button ?????????
            callButton.setOnClickListener(v -> {
                if (!teacherPhoneNumber.getText().toString().equals("") &&
                        !teacherPhoneNumber.getText().toString().equals("????????? ?????? ??????") &&
                        !teacherPhoneNumber.getText().toString().equals("????????? ?????? ?????????."))
                    startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:".concat(teacherPhoneNumber.getText().toString()))));
                else {
                    rhyaDialogManager_Type3.setDialogSettingType_3(
                            "??????",
                            "?????? ????????? ????????? ??? ????????????.",
                            "??????",
                            () -> rhyaDialogManager_Type3.dismissDialog());
                    rhyaDialogManager_Type3.showDialog();
                }
            });

            // Spinner ?????????
            RhyaTimeSelectSpinerAdapter rhyaTimeSelectSpinerAdapter1 = new RhyaTimeSelectSpinerAdapter(context,
                    android.R.layout.simple_spinner_item, new String[] { "?????? ??????", "1??????", "2??????", "3??????", "4??????", "5??????", "6??????", "7??????", "8??????", "9??????", "10??????" });
            RhyaTimeSelectSpinerAdapterForAttendance rhyaTimeSelectSpinerAdapterForAttendance = new RhyaTimeSelectSpinerAdapterForAttendance(context,
                    android.R.layout.simple_spinner_item, new String[] {
                    "??????",
                    "?????? - ??????", "?????? - ??????", "?????? - ??????", "?????? - ??????",
                    "?????? - ??????", "?????? - ??????", "?????? - ??????", "?????? - ??????",
                    "?????? - ??????", "?????? - ??????", "?????? - ??????", "?????? - ??????",
                    "?????? - ??????", "?????? - ??????", "?????? - ??????", "?????? - ??????" });
            spinnerTimeSelect.setAdapter(rhyaTimeSelectSpinerAdapter1);
            spinnerAttendanceCheck.setAdapter(rhyaTimeSelectSpinerAdapterForAttendance);
            spinnerTimeSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        if (spinnerTimeSelect.getItemAtPosition(position).equals("?????? ??????")) {
                            attendanceLayoutForC.setVisibility(View.GONE);
                            attendanceTableRecyclerView.setVisibility(View.VISIBLE);
                            attendanceInfoRecyclerView.setVisibility(View.GONE);

                            final ArrayList<OnlineAttendanceInfoVO> nullTempAttendanceVOValue = new ArrayList<>();
                            rhyaAttendanceInfoRecyclerViewAdapter.setInputValue(nullTempAttendanceVOValue, 0);
                        }else {
                            attendanceLayoutForC.setVisibility(View.VISIBLE);
                            attendanceTableRecyclerView.setVisibility(View.GONE);
                            attendanceInfoRecyclerView.setVisibility(View.VISIBLE);

                            final ArrayList<OnlineAttendanceInfoVO> nullTempAttendanceVOValue = new ArrayList<>();
                            rhyaAttendanceTableRecyclerViewAdapter.setInputValue(nullTempAttendanceVOValue);
                        }

                        timeSelectPos = position;

                        setClassData(selectedClassData);
                    }catch (Exception ex) {
                        ex.printStackTrace();

                        // ?????? ????????? ?????? ??? ??????
                        StringBuilder stringBuilder;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("????????? ???????????? ?????? ?????? ????????? ?????????????????????. ?????? ?????? ??? ?????? ????????????????????????.");
                        stringBuilder.append(System.lineSeparator());
                        stringBuilder.append(System.lineSeparator());
                        stringBuilder.append(ex.getMessage());
                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                "????????? ?????? ??? ?????? ??????!",
                                stringBuilder.toString(),
                                "??????",
                                () -> requireActivity().finishAndRemoveTask());
                        rhyaDialogManager_Type3.showDialog();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            // Tab Layout ?????????
            subTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (selectedClassData != null)  {
                        int pos = tab.getPosition();

                        loadDataType = pos;

                        if (pos == 0) {
                            recyclerViewTitleTextView.setText("?????? ??????");
                        }else {
                            recyclerViewTitleTextView.setText("?????????");
                        }

                        setClassData(selectedClassData);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            // attendanceTableRecyclerView ?????????
            rhyaAttendanceTableRecyclerViewAdapter = new RhyaAttendanceTableRecyclerViewAdapter();
            LinearLayoutManager attendanceTableRecyclerViewLinearLayoutManager = new LinearLayoutManager(context);
            attendanceTableRecyclerViewLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            attendanceTableRecyclerView.setLayoutManager(attendanceTableRecyclerViewLinearLayoutManager);
            attendanceTableRecyclerView.setAdapter(rhyaAttendanceTableRecyclerViewAdapter);

            // attendanceInfoRecyclerView ?????????
            rhyaAttendanceInfoRecyclerViewAdapter = new RhyaAttendanceInfoRecyclerViewAdapter(authToken, getActivity(), url, this);
            LinearLayoutManager attendanceInfoRecyclerViewLinearLayoutManager = new LinearLayoutManager(context);
            attendanceInfoRecyclerViewLinearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            attendanceInfoRecyclerView.setLayoutManager(attendanceInfoRecyclerViewLinearLayoutManager);
            attendanceInfoRecyclerView.setAdapter(rhyaAttendanceInfoRecyclerViewAdapter);

            // allSelectButton ?????????
            allSelectButton.setOnClickListener(v -> rhyaAttendanceInfoRecyclerViewAdapter.selectAll());
            // selectCancelButton ?????????
            selectCancelButton.setOnClickListener(v -> rhyaAttendanceInfoRecyclerViewAdapter.selectCancel());

            // attendanceCheckButton ?????????
            attendanceCheckButton.setOnClickListener(v -> {
                String selectedItem = (String) spinnerAttendanceCheck.getSelectedItem();

                // ????????? ?????? ??????
                new RhyaAsyncTask<String, String>() {
                    @Override
                    protected void onPreExecute() {
                        rhyaDialogManager_Type0.showDialog();
                    }

                    @Override
                    protected String doInBackground(String arg) {
                        try {
                            ContentValues urlParameter = new ContentValues();

                            int errorCount = 0;

                            for (String studentUUID : rhyaAttendanceInfoRecyclerViewAdapter.stringOnlineAttendanceInfoStudentUUIDS) {
                                // ???????????? ??????
                                urlParameter.put("authToken", authToken);
                                urlParameter.put("studentUUID", studentUUID);
                                urlParameter.put("mode", 12);
                                urlParameter.put("date", selectedValue);
                                urlParameter.put("time", timeSelectPos);

                                switch (selectedItem) {
                                    case "??????": {
                                        urlParameter.put("value", 0);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 1);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 2);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 3);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 4);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 5);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 6);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 7);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 8);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 9);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 10);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 11);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 12);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 13);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 14);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 15);
                                        break;
                                    }

                                    case "?????? - ??????": {
                                        urlParameter.put("value", 16);
                                        break;
                                    }
                                }

                                // JSON ????????? ??????
                                RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
                                JSONObject serverResponseJSONObject = new JSONObject(rhyaHttpsConnection.request(url, urlParameter));
                                String serverResponse = serverResponseJSONObject.getString("result");

                                if (!serverResponse.equals("success"))
                                    errorCount++;
                            }

                            return String.valueOf(errorCount);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        rhyaDialogManager_Type0.dismissDialog();

                        try {
                            if (result == null) {
                                // ?????? ??????
                                rhyaDialogManager_Type3.setDialogSettingType_3(
                                        "??? ??? ?????? ??????",
                                        "????????? ???????????? ??? ????????? ?????????????????????.",
                                        "??????",
                                        () -> rhyaDialogManager_Type3.dismissDialog());
                                rhyaDialogManager_Type3.showDialog();
                            }else {
                                StringBuilder stringBuilder;
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("?????? ???????????? ????????? ?????? ????????? ?????????????????????. ?????? ???????????? ????????????.");
                                stringBuilder.append(System.lineSeparator());
                                stringBuilder.append(System.lineSeparator());
                                stringBuilder.append("?????? ????????? ?????? ???: ");
                                stringBuilder.append(result);
                                rhyaDialogManager_Type3.setDialogSettingType_3(
                                        "??????",
                                        stringBuilder.toString(),
                                        "??????",
                                        () -> rhyaDialogManager_Type3.dismissDialog());

                                setClassData(selectedClassData);
                            }
                        }catch (Exception ex) {
                            ex.printStackTrace();

                            // ?????? ????????? ?????? ??? ??????
                            StringBuilder stringBuilder;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(rhyaErrorCodeManager.getErrorMessage(999));
                            stringBuilder.append(System.lineSeparator());
                            stringBuilder.append(System.lineSeparator());
                            stringBuilder.append(ex.getMessage());
                            rhyaDialogManager_Type3.setDialogSettingType_3(
                                    "??? ??? ?????? ??????",
                                    stringBuilder.toString(),
                                    "??????",
                                    () -> requireActivity().finishAndRemoveTask());
                            rhyaDialogManager_Type3.showDialog();
                        }
                    }
                }.execute(null);
            });

            // ?????? ????????? ??????
            if (list_entries.size() > 0) {
                OnlineAttendanceClassVO onlineAttendanceClassVO = list_entries.get(0).getValue();
                // ????????? Null ??????
                if (RhyaApplication.getOnlineAttendanceTeacherVOHashMap().containsKey(onlineAttendanceClassVO.getClass_teacher_uuid()))
                    setClassData(onlineAttendanceClassVO); // ????????? ??????
            }
        }catch (Exception ex) {
            ex.printStackTrace();

            // ?????? ????????? ?????? ??? ??????
            StringBuilder stringBuilder;
            stringBuilder = new StringBuilder();
            stringBuilder.append("AttendanceFragment??? UI??? ??????????????? ????????? ????????? ?????????????????????. ?????? ?????? ??? ?????? ????????????????????????.");
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(ex.getMessage());
            rhyaDialogManager_Type3.setDialogSettingType_3(
                    "UI ????????? ??? ?????? ??????!",
                    stringBuilder.toString(),
                    "??????",
                    () -> requireActivity().finishAndRemoveTask());
            rhyaDialogManager_Type3.showDialog();
        }
    }



    // ===========================================================
    /**
     * ??? ????????? ??????
     */
    // ===========================================================
    public void setClassData(OnlineAttendanceClassVO onlineAttendanceClassVO) {
        try {
            // Dialog ??????
            rhyaDialogManager_Type0.showDialog();

            // ??? ?????? ??????
            selectedClassName.setText(onlineAttendanceClassVO.getClass_nickname());

            selectedClassData = onlineAttendanceClassVO;

            // ?????? ????????? ?????? ??????
            OnlineAttendanceTeacherVO onlineAttendanceTeacherVO = RhyaApplication.getOnlineAttendanceTeacherVOHashMap().get(onlineAttendanceClassVO.getClass_teacher_uuid());
            assert onlineAttendanceTeacherVO != null;
            Glide.with(context)
                    .load(onlineAttendanceTeacherVO.getImageURL(authToken))
                    .placeholder(R.drawable.teacher_icon)
                    .error(R.drawable.teacher_icon)
                    .fallback(R.drawable.teacher_icon)
                    .signature(new ObjectKey(onlineAttendanceTeacherVO.getVersion()))
                    .into(teacherImage);

            teacherName.setText(onlineAttendanceTeacherVO.getName());

            StringBuilder stringBuilder;
            stringBuilder = new StringBuilder();

            if (!onlineAttendanceTeacherVO.getDepartment1().equals(onlineAttendanceTeacherVO.NO_VALUE_TEXT)) {
                stringBuilder.append(Objects.requireNonNull(RhyaApplication.getOnlineAttendanceDepartmentVOHashMap().get(onlineAttendanceTeacherVO.getDepartment1())).getName());
            }

            stringBuilder.append(" (");
            stringBuilder.append(onlineAttendanceTeacherVO.getPosition());

            if (!onlineAttendanceTeacherVO.getSubject().equals(onlineAttendanceTeacherVO.NO_VALUE_TEXT)) {
                stringBuilder.append("-");
                stringBuilder.append(onlineAttendanceTeacherVO.getSubject());
            }

            stringBuilder.append(")");

            teacherDepartment.setText(stringBuilder.toString().replace(onlineAttendanceTeacherVO.NO_VALUE_TEXT, ""));

            final String showNoValueTxt = "????????? ?????? ??????";
            final String showPrivateTxt = "????????? ?????? ?????????.";

            teacherPhoneNumber.setText(showNoValueTxt);
            teacherEmailAddress.setText(showNoValueTxt);


            // ????????? ?????? ??????
            new RhyaAsyncTask<String, String>() {
                private String value1 = "";
                private String value2 = "";
                private ArrayList<OnlineAttendanceStudentVO> inputValueForClassInfo = null;



                @Override
                protected void onPreExecute() {
                }

                @Override
                protected String doInBackground(String arg) {
                    try {
                        inputValueForClassInfo = getClassData(onlineAttendanceClassVO.getClass_uuid());

                        RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
                        ContentValues urlParameter = new ContentValues();
                        // ???????????? ??????
                        urlParameter.put("authToken", authToken);
                        urlParameter.put("uuid", onlineAttendanceTeacherVO.getUuid());
                        urlParameter.put("mode", 2);
                        // JSON ????????? ??????
                        JSONObject serverResponseJSONObject = new JSONObject(rhyaHttpsConnection.request(url, urlParameter));
                        String serverResponse = serverResponseJSONObject.getString("result");

                        if (serverResponse.equals("success")) {
                            value1 = URLDecoder.decode(serverResponseJSONObject.getString("phone"), "UTF-8");
                            value2 = URLDecoder.decode(serverResponseJSONObject.getString("email"), "UTF-8");

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
                        if (inputValueForClassInfo == null) {
                            loadDataException();

                            // ?????? ??????
                            rhyaDialogManager_Type3.setDialogSettingType_3(
                                    "??? ??? ?????? ??????",
                                    "????????? ???????????? ??? ????????? ?????????????????????.",
                                    "??????",
                                    () -> rhyaDialogManager_Type3.dismissDialog());
                            rhyaDialogManager_Type3.showDialog();
                        }else {
                            // ????????? ??????
                            if (result != null) {
                                final String noValueText = "[NoValue]";
                                final String privateValue = "[private]";


                                if (value1.equals(noValueText))
                                    teacherPhoneNumber.setText(showNoValueTxt);
                                else if (value1.equals(privateValue))
                                    teacherPhoneNumber.setText(showPrivateTxt);
                                else
                                    teacherPhoneNumber.setText(value1);


                                if (value2.equals(noValueText))
                                    teacherEmailAddress.setText(showNoValueTxt);
                                else if (value2.equals(privateValue))
                                    teacherEmailAddress.setText(showPrivateTxt);
                                else
                                    teacherEmailAddress.setText(value2);
                            }else {
                                teacherPhoneNumber.setText(showNoValueTxt);
                                teacherEmailAddress.setText(showPrivateTxt);
                            }

                            // ????????? ????????????
                            switch (loadDataType) {
                                // ??? ?????? ????????????
                                case 0: {
                                    spinnerTimeSelect.setEnabled(false);

                                    studentInfoRecyclerView.setVisibility(View.VISIBLE);
                                    attendanceLayout.setVisibility(View.GONE);

                                    rhyaClassInfoRecyclerViewAdapter.setInputValue(inputValueForClassInfo);

                                    // Dialog ??????
                                    rhyaDialogManager_Type0.dismissDialog();

                                    break;
                                }

                                // ????????? ????????? ????????????
                                case 1: {
                                    spinnerTimeSelect.setEnabled(true);
                                    studentInfoRecyclerView.setVisibility(View.GONE);
                                    attendanceLayout.setVisibility(View.VISIBLE);

                                    // ????????? ????????? ?????? ??????
                                    if (selectedValue != null) {
                                        setAttendanceData();
                                    }else {
                                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                                "?????? ?????? ??????",
                                                "????????? ???????????? ???????????? ????????? ????????? ???????????? ?????????. ???????????? ????????? ????????? ?????????.",
                                                "??????",
                                                () -> rhyaDialogManager_Type3.dismissDialog());
                                        subTabLayout.selectTab(subTabLayout.getTabAt(0));
                                        rhyaDialogManager_Type3.showDialog();

                                        // Dialog ??????
                                        rhyaDialogManager_Type0.dismissDialog();
                                    }

                                    break;
                                }
                            }
                        }
                    }catch (Exception ex) {
                        ex.printStackTrace();

                        loadDataException();
                        // ?????? ????????? ?????? ??? ??????
                        StringBuilder stringBuilder;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(rhyaErrorCodeManager.getErrorMessage(999));
                        stringBuilder.append(System.lineSeparator());
                        stringBuilder.append(System.lineSeparator());
                        stringBuilder.append(ex.getMessage());
                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                "??? ??? ?????? ??????",
                                stringBuilder.toString(),
                                "??????",
                                () -> requireActivity().finishAndRemoveTask());
                        rhyaDialogManager_Type3.showDialog();
                    }
                }
            }.execute(null);
        }catch (Exception ex) {
            ex.printStackTrace();

            loadDataException();
            // ?????? ????????? ?????? ??? ??????
            StringBuilder stringBuilder;
            stringBuilder = new StringBuilder();
            stringBuilder.append(rhyaErrorCodeManager.getErrorMessage(999));
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append(ex.getMessage());
            rhyaDialogManager_Type3.setDialogSettingType_3(
                    "??? ??? ?????? ??????",
                    stringBuilder.toString(),
                    "??????",
                    () -> requireActivity().finishAndRemoveTask());
            rhyaDialogManager_Type3.showDialog();
        }
    }
    private void loadDataException() {
        final String showNoValueTxt = "????????? ?????? ??????";
        final ArrayList<OnlineAttendanceStudentVO> nullTempStudentVOValue = new ArrayList<>();
        final ArrayList<OnlineAttendanceInfoVO> nullTempAttendanceVOValue = new ArrayList<>();

        // ?????? ??????
        teacherPhoneNumber.setText(showNoValueTxt);
        teacherEmailAddress.setText(showNoValueTxt);
        teacherDepartment.setText(showNoValueTxt);
        teacherName.setText(showNoValueTxt);
        rhyaClassInfoRecyclerViewAdapter.setInputValue(nullTempStudentVOValue);
        attendanceLayout.setVisibility(View.GONE);
        studentInfoRecyclerView.setVisibility(View.GONE);
        rhyaAttendanceTableRecyclerViewAdapter.setInputValue(nullTempAttendanceVOValue);
        rhyaAttendanceInfoRecyclerViewAdapter.setInputValue(nullTempAttendanceVOValue, 0);
    }





    // ===========================================================
    /**
     * ??? ????????? ????????????
     */
    // ===========================================================
    private ArrayList<OnlineAttendanceStudentVO> getClassData(String classUUID) {
        // ?????? ?????????
        ArrayList<OnlineAttendanceStudentVO> onlineAttendanceStudentVOS = null;

        int index = 0;

        selectedClassDataStudentList.clear();

        // ????????? ??????
        for (String tempOnlineAttendanceStudentVOKey : RhyaApplication.getOnlineAttendanceStudentVOHashMap().keySet()) {
            OnlineAttendanceStudentVO tempOnlineAttendanceStudentVO = RhyaApplication.getOnlineAttendanceStudentVOHashMap().get(tempOnlineAttendanceStudentVOKey);
            assert tempOnlineAttendanceStudentVO != null;
            if (tempOnlineAttendanceStudentVO.getClass_uuid().equals(classUUID)) {
                if (onlineAttendanceStudentVOS == null)
                    onlineAttendanceStudentVOS = new ArrayList<>();

                selectedClassDataStudentList.add(tempOnlineAttendanceStudentVO);

                onlineAttendanceStudentVOS.add(index, tempOnlineAttendanceStudentVO);
                index++;
            }
        }

        if (onlineAttendanceStudentVOS == null) return null;

        // ?????? ???????????? ??????
        Collections.sort(onlineAttendanceStudentVOS, (obj1, obj2) -> Integer.compare(obj1.getNumber(), obj2.getNumber()));

        return onlineAttendanceStudentVOS;
    }



    // ===========================================================
    /**
     * ????????? ????????? ????????????
     */
    // ===========================================================
    private ArrayList<OnlineAttendanceInfoVO> getAttendanceData() throws JSONException {
        final String selectDate = selectedValue;

        if (selectDate != null) {
            ArrayList<OnlineAttendanceInfoVO> onlineAttendanceInfoVOS = new ArrayList<>();

            onlineAttendanceInfoVOS.add(null);

            RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
            ContentValues urlParameter = new ContentValues();
            // ???????????? ??????
            urlParameter.put("authToken", authToken);
            urlParameter.put("classUUID", selectedClassData.getClass_uuid());
            urlParameter.put("date", selectDate);
            urlParameter.put("mode", 10);
            // JSON ????????? ??????
            JSONObject serverResponseJSONObject = new JSONObject(rhyaHttpsConnection.request(url, urlParameter));

            String serverResponse = serverResponseJSONObject.getString("result");
            if (serverResponse.equals("success")) {
                try {
                    // JSON Object, Array
                    JSONArray studentJSONRootArray = serverResponseJSONObject.getJSONArray("student");
                    // JSON Array for
                    for (int jsonArrayIndex = 0; jsonArrayIndex < studentJSONRootArray.length(); jsonArrayIndex++) {
                        // ?????? ?????? ??????
                        JSONObject studentInfo = studentJSONRootArray.getJSONObject(jsonArrayIndex);
                        OnlineAttendanceInfoVO onlineAttendanceInfoVO = new OnlineAttendanceInfoVO(
                                URLDecoder.decode(studentInfo.getString("student_uuid"), "UTF-8"),
                                studentInfo.getString("attendance_date"),
                                studentInfo.getInt("value_1"),
                                studentInfo.getInt("value_2"),
                                studentInfo.getInt("value_3"),
                                studentInfo.getInt("value_4"),
                                studentInfo.getInt("value_5"),
                                studentInfo.getInt("value_6"),
                                studentInfo.getInt("value_7"),
                                studentInfo.getInt("value_8"),
                                studentInfo.getInt("value_9"),
                                studentInfo.getInt("value_10"),
                                studentInfo.getString("value_1_teacher"),
                                studentInfo.getString("value_2_teacher"),
                                studentInfo.getString("value_3_teacher"),
                                studentInfo.getString("value_4_teacher"),
                                studentInfo.getString("value_5_teacher"),
                                studentInfo.getString("value_6_teacher"),
                                studentInfo.getString("value_7_teacher"),
                                studentInfo.getString("value_8_teacher"),
                                studentInfo.getString("value_9_teacher"),
                                studentInfo.getString("value_10_teacher"),
                                URLDecoder.decode(studentInfo.getString("note"), "UTF-8")
                        );

                        onlineAttendanceInfoVOS.add(onlineAttendanceInfoVO);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            return onlineAttendanceInfoVOS;
        }

        return null;
    }



    // ===========================================================
    /**
     * ????????? ????????? ??????
     */
    // ===========================================================
    private void setAttendanceData() {
        // ????????? ?????? ??????
        new RhyaAsyncTask<String, String>() {
            private ArrayList<OnlineAttendanceInfoVO> onlineAttendanceInfoVOS;

            @Override
            protected void onPreExecute() {
                // Dialog ??????
                rhyaDialogManager_Type0.showDialog();
            }

            @Override
            protected String doInBackground(String arg) {
                try {
                    onlineAttendanceInfoVOS = getAttendanceData();

                    ArrayList<OnlineAttendanceInfoVO> inputValue = new ArrayList<>();

                    if (onlineAttendanceInfoVOS != null) {
                        if (onlineAttendanceInfoVOS.size() != selectedClassDataStudentList.size() + 1) {
                            for (OnlineAttendanceStudentVO onlineAttendanceStudentVO : selectedClassDataStudentList) {
                                boolean isCheck = false;

                                for (OnlineAttendanceInfoVO onlineAttendanceInfoVO : onlineAttendanceInfoVOS) {
                                    if (onlineAttendanceInfoVO != null) {
                                        if (onlineAttendanceInfoVO.getUuid().equals(onlineAttendanceStudentVO.getUuid())) {
                                            isCheck = true;

                                            break;
                                        }
                                    }
                                }

                                if (!isCheck) {
                                    final String noValueText = "[NoValue]";

                                    OnlineAttendanceInfoVO onlineAttendanceInfoVOForPut = new OnlineAttendanceInfoVO(
                                            onlineAttendanceStudentVO.getUuid(),
                                            selectedValue,
                                            -1,
                                            -1,
                                            -1,
                                            -1,
                                            -1,
                                            -1,
                                            -1,
                                            -1,
                                            -1,
                                            -1,
                                            noValueText,
                                            noValueText,
                                            noValueText,
                                            noValueText,
                                            noValueText,
                                            noValueText,
                                            noValueText,
                                            noValueText,
                                            noValueText,
                                            noValueText,
                                            noValueText
                                    );

                                    inputValue.add(onlineAttendanceInfoVOForPut);
                                }
                            }

                            onlineAttendanceInfoVOS.addAll(inputValue);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();

                    onlineAttendanceInfoVOS = null;
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    if (onlineAttendanceInfoVOS != null) {
                        if (timeSelectPos != -1) {
                            // ????????? ??????
                            Collections.sort(onlineAttendanceInfoVOS);

                            if (timeSelectPos == 0) {
                                rhyaAttendanceTableRecyclerViewAdapter.setInputValue(onlineAttendanceInfoVOS);
                            }else {
                                onlineAttendanceInfoVOS.remove(null);
                                rhyaAttendanceInfoRecyclerViewAdapter.setInputValue(onlineAttendanceInfoVOS, timeSelectPos);
                            }
                        }
                    }else {
                        // ?????? ??????
                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                "??? ??? ?????? ??????",
                                "????????? ???????????? ??? ????????? ?????????????????????.",
                                "??????",
                                () -> rhyaDialogManager_Type3.dismissDialog());
                        rhyaDialogManager_Type3.showDialog();
                    }

                    // Dialog ??????
                    rhyaDialogManager_Type0.dismissDialog();
                }catch (Exception ex) {
                    // Dialog ??????
                    rhyaDialogManager_Type0.dismissDialog();

                    ex.printStackTrace();

                    loadDataException();

                    // ?????? ????????? ?????? ??? ??????
                    StringBuilder stringBuilder;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(rhyaErrorCodeManager.getErrorMessage(999));
                    stringBuilder.append(System.lineSeparator());
                    stringBuilder.append(System.lineSeparator());
                    stringBuilder.append(ex.getMessage());
                    rhyaDialogManager_Type3.setDialogSettingType_3(
                            "??? ??? ?????? ??????",
                            stringBuilder.toString(),
                            "??????",
                            () -> requireActivity().finishAndRemoveTask());
                    rhyaDialogManager_Type3.showDialog();
                }
            }
        }.execute(null);
    }
}