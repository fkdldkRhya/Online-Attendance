package kro.kr.rhya_network.online_attendance.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Size;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

import kro.kr.rhya_network.online_attendance.R;
import kro.kr.rhya_network.online_attendance.adapter.RhyaClassNameSelectRecyclerViewAdapter;

public class RhyaDialogManager {
    // Activity
    private Activity activity;
    // Snack bar
    private Snackbar snackbar;
    // Task dialog
    private final Dialog dialog;
    // Background
    public final Drawable background = new ColorDrawable(Color.TRANSPARENT);
    // Listener
    // ============================================ //
    public interface DialogListener_Type1 {
        void onClickListenerButtonNoKey();
        void onClickListenerButtonSyncAccount(String key);
    }
    public interface DialogListener_Type2 {
        void onClickListenerButton1();
        void onClickListenerButton2();
    }
    public interface DialogListener_Type3 {
        void onClickListenerButton();
    }
    public interface DialogListener_Type4 {
        void selectClassName(OnlineAttendanceClassVO tempVO);
    }
    public interface DialogListener_Type5 {
        void onClickListenerButton(String uuid, String editText);
    }
    // ============================================ //


    /**
     * Dialog 관리자 초기화
     *
     * @param activity activity
     * @param isCancelable Cancelable 가능 불가능 여부
     * @param type Dialog 타입
     */
    public RhyaDialogManager(Activity activity, boolean isCancelable, double dSize, int type) {
        // Set activity
        this.activity = activity;

        Point size = new Point();
        // Dialog 기본 설정
        dialog = new Dialog(activity);
        dialog.getWindow().setBackgroundDrawable(background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(isCancelable);

        switch (type) {
            default:
            case 0: {
                dialog.setContentView(R.layout.dialog_task);
                break;
            }

            case 1: {
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.setContentView(R.layout.dialog_account_sync_key);
                break;
            }

            case 2: {
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.setContentView(R.layout.dialog_yes_or_no);
                break;
            }

            case 3: {
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.setContentView(R.layout.dialog_ok);
                break;
            }

            case 4: {
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.setContentView(R.layout.dialog_class_name_select);
                break;
            }

            case 5: {
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.setContentView(R.layout.dialog_student_attendance_info);
                break;
            }

            case 6: {
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.setContentView(R.layout.dialog_studnet_info);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(params);

                return;
            }
        }


        // Dialog 사이즈 설정
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            final WindowMetrics metrics = activity.getWindowManager().getCurrentWindowMetrics();
            // Gets all excluding insets
            final WindowInsets windowInsets = metrics.getWindowInsets();
            Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars()
                    | WindowInsets.Type.displayCutout());
            int insetsWidth = insets.right + insets.left;
            int insetsHeight = insets.top + insets.bottom;

            // Legacy size that Display#getSize reports
            final Rect bounds = metrics.getBounds();
            final Size legacySize = new Size(bounds.width() - insetsWidth,
                    bounds.height() - insetsHeight);
            params.width = (int) Math.round((legacySize.getWidth() * dSize));
        }else {
            activity.getWindowManager().getDefaultDisplay().getRealSize(size);
            params.width = (int) Math.round((size.x * dSize));
        }

        dialog.getWindow().setAttributes(params);
    }



    // =========================================================================
    // =========================================================================
    public void setDialogSettingType_0(String message) {
        ((TextView) dialog.findViewById(R.id.message)).setText(message);
    }
    // =========================================================================
    // =========================================================================



    // =========================================================================
    // =========================================================================
    public void setDialogSettingType_1(DialogListener_Type1 listenerType_1) {
        EditText accountSyncInputKeyEditText = dialog.findViewById(R.id.accountSyncInputKeyEditText);
        TextView accountSyncDontHaveKeyMessageTextView = dialog.findViewById(R.id.accountSyncDontHaveKeyMessageTextView);
        Button accountRequestSyncButton = dialog.findViewById(R.id.accountRequestSyncButton);

        RhyaSnackBarStyle rhyaSnackBarStyle = new RhyaSnackBarStyle();
        snackbar = rhyaSnackBarStyle.getSnackBar(dialog.findViewById(R.id.mainLayout), "'-' (하이폰), 영문, 숫자만 입력할 수 있습니다.", dialog.getContext());
        snackbar.setAction("닫기", v -> snackbar.dismiss());

        accountRequestSyncButton.setEnabled(accountSyncInputKeyEditText.getText().toString().length() == 36);

        accountSyncInputKeyEditText.setFilters(new InputFilter[] {(source, start, end, dest, dstart, dend) -> {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9- |]+$");
            if (source.equals("") || ps.matcher(source).matches()) {
                return source.toString().replace(" ", "");
            }

            snackbar.show();

            return "";
        },new InputFilter.LengthFilter(36)});
        accountSyncInputKeyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No work
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No work
            }

            @Override
            public void afterTextChanged(Editable editable) {
                accountRequestSyncButton.setEnabled(accountSyncInputKeyEditText.getText().toString().length() == 36);
            }
        });


        accountSyncDontHaveKeyMessageTextView.setOnClickListener(v ->
                listenerType_1.onClickListenerButtonNoKey());
        accountRequestSyncButton.setOnClickListener(v ->
                listenerType_1.onClickListenerButtonSyncAccount(accountSyncInputKeyEditText.getText().toString()));
    }
    // =========================================================================
    // =========================================================================



    // =========================================================================
    // =========================================================================
    public void setDialogSettingType_2(String title, String message, String btn1Text, String btn2Text, DialogListener_Type2 dialogListener_type2) {
        ((TextView) dialog.findViewById(R.id.titleTextView)).setText(title);
        ((TextView) dialog.findViewById(R.id.messageTextView)).setText(message);
        Button btn1 = dialog.findViewById(R.id.button1);
        btn1.setText(btn1Text);
        btn1.setOnClickListener(v -> dialogListener_type2.onClickListenerButton1());
        Button btn2 = dialog.findViewById(R.id.button2);
        btn2.setText(btn2Text);
        btn2.setOnClickListener(v -> dialogListener_type2.onClickListenerButton2());
    }
    // =========================================================================
    // =========================================================================



    // =========================================================================
    // =========================================================================
    public void setDialogSettingType_3(String title, String message, String btnText, DialogListener_Type3 dialogListener_type3) {
        ((TextView) dialog.findViewById(R.id.titleTextView)).setText(title);
        ((TextView) dialog.findViewById(R.id.messageTextView)).setText(message);
        Button btn = dialog.findViewById(R.id.button);
        btn.setText(btnText);
        btn.setOnClickListener(v -> dialogListener_type3.onClickListenerButton());
    }
    // =========================================================================
    // =========================================================================



    // =========================================================================
    // =========================================================================
    public void setDialogSettingType_4(ArrayList<OnlineAttendanceClassVO> inputValue, DialogListener_Type4 dialogListener_type4) {
        Button btn = dialog.findViewById(R.id.loadButton);
        RecyclerView recyclerView = dialog.findViewById(R.id.classSelectRecyclerView);
        RhyaClassNameSelectRecyclerViewAdapter rhyaClassNameSelectRecyclerViewAdapter = new RhyaClassNameSelectRecyclerViewAdapter(inputValue);
        LinearLayoutManager linearLayout = new LinearLayoutManager(dialog.getContext());
        linearLayout.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayout);
        recyclerView.setAdapter(rhyaClassNameSelectRecyclerViewAdapter);

        btn.setOnClickListener(view -> {
            if (rhyaClassNameSelectRecyclerViewAdapter.selectedPos != -1) {
                dialogListener_type4.selectClassName(inputValue.get(rhyaClassNameSelectRecyclerViewAdapter.selectedPos));
            }else {
                dialog.dismiss();
            }
        });
    }
    // =========================================================================
    // =========================================================================



    // =========================================================================
    // =========================================================================
    public void setDialogSettingType_5(OnlineAttendanceStudentVO onlineAttendanceStudentVO, OnlineAttendanceInfoVO onlineAttendanceInfoVO, DialogListener_Type5 dialogListener_type5) {
        Button btn = dialog.findViewById(R.id.saveButton);
        TextView studentName = dialog.findViewById(R.id.studentName);
        EditText studentDescriptionEditText = dialog.findViewById(R.id.studentDescriptionEditText);

        studentName.setText(onlineAttendanceStudentVO.getName());
        if (!onlineAttendanceInfoVO.getNote().equals(onlineAttendanceInfoVO.NO_VALUE_TEXT)) {
            studentDescriptionEditText.setText(onlineAttendanceInfoVO.getNote());
        }else {
            studentDescriptionEditText.setText("");
        }

        btn.setOnClickListener(view -> {
            try {
                dialogListener_type5.onClickListenerButton(onlineAttendanceStudentVO.getUuid(), URLEncoder.encode(studentDescriptionEditText.getText().toString().replaceAll(System.lineSeparator(), "</br>"), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            dialog.dismiss();
        });
    }
    // =========================================================================
    // =========================================================================



    // =========================================================================
    // =========================================================================
    public void setDialogSettingType_6() {
    }
    // =========================================================================
    // =========================================================================



    // =========================================================================
    // =========================================================================
    public void showDialog() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
    public void dismissDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    // =========================================================================
    // =========================================================================
}
