package kro.kr.rhya_network.online_attendance.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;

import kro.kr.rhya_network.online_attendance.BuildConfig;
import kro.kr.rhya_network.online_attendance.R;
import kro.kr.rhya_network.online_attendance.core.RhyaApplication;
import kro.kr.rhya_network.online_attendance.core.RhyaAuthManager;
import kro.kr.rhya_network.online_attendance.utils.CellphoneRoutingCheck;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceClassVO;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceDepartmentVO;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceStudentVO;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceTeacherVO;
import kro.kr.rhya_network.online_attendance.utils.RhyaAESManager;
import kro.kr.rhya_network.online_attendance.utils.RhyaAsyncTask;
import kro.kr.rhya_network.online_attendance.utils.RhyaDataManager;
import kro.kr.rhya_network.online_attendance.utils.RhyaDialogManager;
import kro.kr.rhya_network.online_attendance.utils.RhyaErrorCodeManager;
import kro.kr.rhya_network.online_attendance.utils.RhyaHttpsConnection;
import kro.kr.rhya_network.online_attendance.utils.RhyaMainUtils;
import kro.kr.rhya_network.online_attendance.utils.RhyaSnackBarStyle;

public class ActivityAccountSync extends AppCompatActivity {
    // Activity info
    private Activity activity;
    private Context context;
    // UI Object
    private ConstraintLayout mainLayout;
    private ConstraintLayout accountSyncLayout;
    private ConstraintLayout accountSyncFailLayout;
    private ConstraintLayout accountSyncSuccessLayout;
    private RoundedImageView teacherImageView;
    private TextView teacherNameTextView;
    private TextView schoolNameTextView;
    // RHYA Auth Token ????????? ??????
    private RhyaAuthManager rhyaAuthManager;
    // ?????? ????????? ?????????
    private RhyaErrorCodeManager rhyaErrorCodeManager;
    // Utils ?????? ?????????
    private RhyaMainUtils rhyaMainUtils;
    // ????????? ?????????
    private RhyaDataManager rhyaDataManager;
    // ????????? ?????????
    private RhyaAESManager rhyaAESManager;
    // Dialog ?????????
    private RhyaDialogManager rhyaDialogManager_Type0;
    private RhyaDialogManager rhyaDialogManager_Type1;
    private RhyaDialogManager rhyaDialogManager_Type3;
    // ????????? ?????????
    private OnlineAttendanceTeacherVO onlineAttendanceTeacherVO;
    // Toast message
    private Toast toast;
    // ???????????? ?????? ??????
    private long backBtnTime = 0;
    // ???????????? ?????? ??????
    private boolean isEventExit = false;
    // ???????????? ?????????
    private long enqueue;
    private DownloadManager downloadManager;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_account_sync);
        super.onCreate(savedInstanceState);


        // UI ?????????
        accountSyncLayout = findViewById(R.id.accountSyncLayout);
        accountSyncFailLayout = findViewById(R.id.accountSyncFailLayout);
        accountSyncSuccessLayout = findViewById(R.id.accountSyncSuccessLayout);
        mainLayout = findViewById(R.id.mainLayout);
        Button accountRestartSyncButton = findViewById(R.id.accountRestartSyncButton);
        Button accountRequestSyncButton = findViewById(R.id.accountRequestSyncButton);
        teacherImageView = findViewById(R.id.teacherImageView);
        teacherNameTextView = findViewById(R.id.teacherNameTextView);
        schoolNameTextView = findViewById(R.id.schoolNameTextView);


        // ?????? ?????????
        activity = this;
        context = this;
        toast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
        rhyaAuthManager = new RhyaAuthManager(this);
        rhyaErrorCodeManager = new RhyaErrorCodeManager();
        rhyaMainUtils = new RhyaMainUtils();
        rhyaDataManager = new RhyaDataManager(getFilesDir().getAbsolutePath());
        rhyaAESManager = new RhyaAESManager(context);


        // Auth token
        String authToken = null;

        // ?????? ??????
        try {
            // Auth Token ?????????
            authToken = rhyaAuthManager.getSharedPreferencesAuthToken();

            checkAccountSync(authToken);
        } catch (Exception ex) {
            // ?????? ????????? ??????
            ex.printStackTrace();

            goBackActivity();
        }
        String finalAuthToken = authToken;


        // Dialog ????????? ?????????
        rhyaDialogManager_Type0 = new RhyaDialogManager(
                this,
                false,
                0.85,
                0);
        rhyaDialogManager_Type0.setDialogSettingType_0("?????? ?????? ???...");
        rhyaDialogManager_Type1 = new RhyaDialogManager(
                this,
                true,
                0.95,
                1);
        rhyaDialogManager_Type1.setDialogSettingType_1(new RhyaDialogManager.DialogListener_Type1() {
            @Override
            public void onClickListenerButtonNoKey() {
                // Dialog ??????
                rhyaDialogManager_Type1.dismissDialog();
                // Dialog ??????
                rhyaDialogManager_Type0.showDialog();

                accountSyncRequestClient(finalAuthToken, 0, null);
            }

            @Override
            public void onClickListenerButtonSyncAccount(String key) {
                // Dialog ??????
                rhyaDialogManager_Type1.dismissDialog();
                // Dialog ??????
                rhyaDialogManager_Type0.showDialog();

                accountSyncRequestClient(finalAuthToken, 1, key);
            }
        });
        rhyaDialogManager_Type3 = new RhyaDialogManager(
                this,
                false,
                0.95,
                3);


        // ????????? ??????
        accountRestartSyncButton.setOnClickListener(v -> checkAccountSync(finalAuthToken));
        accountRequestSyncButton.setOnClickListener(v -> requestAccountSync());
    }



    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            // ??????
            isEventExit = true;

            finish();
        } else {
            isEventExit = false;
            backBtnTime = curTime;

            // Toast ????????? ??????
            toast.cancel();
            toast.makeText(context, "'??????' ????????? ?????? ??? ???????????? ???????????????.", Toast.LENGTH_SHORT).show();
        }
    }



    private boolean integrityCheck(String signKey) throws NoSuchAlgorithmException, PackageManager.NameNotFoundException {
        Context context = getApplicationContext();
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        String cert;

        @SuppressLint("PackageManagerGetSignatures")
        PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

        Signature certSignature = packageInfo.signatures[0];
        MessageDigest msgDigest = MessageDigest.getInstance("SHA1");
        msgDigest.update(certSignature.toByteArray());

        byte[] byteData = msgDigest.digest();

        StringBuffer sb;
        sb = new StringBuffer();

        for (byte byteDatum : byteData) {
            sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
            sb.append(":");
        }

        cert = sb.toString();

        return signKey.equals(cert);
    }



    private void goBackActivity() {
        if (!isEventExit && !this.isFinishing()) {
            // ActivitySplash ??????
            Intent intent = new Intent(context, ActivitySplash.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
    }



    private void goNextActivity(String authToken) {
        if (!isEventExit && !this.isFinishing()) {
            // ActivitySplash ??????
            Intent intent = new Intent(context, ActivityMain.class);
            // ?????? ??????
            intent.putExtra("authToken", authToken);
            intent.putExtra("myInfo", onlineAttendanceTeacherVO);
            // ?????? ??????
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_enter, R.anim.scale_small);
            finish();
        }
    }



    private void checkAccountSync(String authToken) {
        // ?????? ????????? ??????
        new RhyaAsyncTask<String, String>() {
            private int isSuccess = 0;

            @Override
            protected void onPreExecute() {
                accountSyncLayout.setVisibility(View.VISIBLE);
                accountSyncSuccessLayout.setVisibility(View.GONE);
                accountSyncFailLayout.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(String arg) {
                try {
                    // RHYA.Network ??? ???????????? Auth Token ??????
                    RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
                    ContentValues urlParameter = new ContentValues();
                    // ???????????? ??????
                    urlParameter.put("authToken", authToken);
                    urlParameter.put("mode", 0);
                    // JSON ????????? ??????
                    JSONObject serverResponseJSONObject = new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_ACCOUNT_SYNC, urlParameter));
                    String serverResponse = serverResponseJSONObject.getString("result");
                    // ????????? ??????
                    if (serverResponse.equals("success")) {
                        isSuccess = 1;
                    }else {
                        isSuccess = 2;
                    }
                } catch (Exception ex) {
                    // ?????? ????????? ??????
                    ex.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (isSuccess == 1) {
                    // ?????? ????????? ????????????
                    getAccountInfo(authToken);

                    return;
                }else if (isSuccess == 0) {
                    showSnackBar(rhyaErrorCodeManager.getErrorMessage(300));
                }

                // ?????? ????????? ??????
                accountSyncLayout.setVisibility(View.GONE);
                accountSyncSuccessLayout.setVisibility(View.GONE);
                accountSyncFailLayout.setVisibility(View.VISIBLE);
            }
        }.execute(null);
    }



    public void accountSyncRequestClient(String authToken, int type, String value) {
        // ?????? ??????
        new RhyaAsyncTask<String, String>() {
            private int isSuccess = -1;

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(String arg) {
                try {
                    try {
                        // RHYA.Network ??? ???????????? Auth Token ??????
                        RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
                        ContentValues urlParameter = new ContentValues();
                        // ???????????? ??????
                        urlParameter.put("authToken", authToken);
                        urlParameter.put("mode", 1);
                        if (type == 1) urlParameter.put("authorizationKey", value);
                        // JSON ????????? ??????
                        JSONObject serverResponseJSONObject = new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_ACCOUNT_SYNC, urlParameter));
                        String serverResponse = serverResponseJSONObject.getString("result");

                        // ????????? ??????
                        isSuccess = 1;
                        return serverResponse;
                    }catch (JSONException ex) {
                        ex.printStackTrace();
                        isSuccess = 2;
                    }
                } catch (Exception ex) {
                    // ?????? ????????? ??????
                    ex.printStackTrace();
                    isSuccess = 3;
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                rhyaDialogManager_Type0.dismissDialog();

                if (isSuccess == 1) {
                    // ?????? ??????
                    if (result != null) {
                        switch (result) {
                            default:
                                showSnackBar(rhyaErrorCodeManager.getErrorMessage(999));
                                break;

                            case "NO_AUTH_TOKEN":
                                showSnackBar("Auth Token ?????? ???????????? ???????????????.");
                                break;

                            case "NO_SYNC_KEY_FOR_DB":
                                showSnackBar("?????? ?????? ????????? ???????????????????????? ???????????? ???????????????.");
                                break;

                            case "MATCH_SYNC_KEY":
                                // ?????? ????????? ????????????
                                getAccountInfo(authToken);

                                break;

                            case "NOT_MATCH_SYNC_KEY":
                                showSnackBar("?????? ?????? ????????? ???????????? ????????????.");
                                break;

                            case "EMAIL_ALREADY_SEND":
                                showSnackBar("?????? 1??? ?????? ?????? ?????? ????????? ???????????????.");
                                break;

                            case "UPDATE_AND_SEND_EMAIL_SUCCESS":
                            case "INSERT_AND_SEND_EMAIL_SUCCESS":
                                showSnackBar("?????? ?????? ????????? ??????????????? ???????????????.");
                                break;

                            case "INSERT_AND_SEND_EMAIL_FAIL":
                                showSnackBar("?????? ?????? ????????? ????????? ?????? ????????? ?????????????????????.");
                                break;
                        }
                    }else {
                        showSnackBar(rhyaErrorCodeManager.getErrorMessage(999));
                    }
                }else if (isSuccess == 2) {
                    showSnackBar(rhyaErrorCodeManager.getErrorMessage(101));
                }else if (isSuccess == 3) {
                    showSnackBar(rhyaErrorCodeManager.getErrorMessage(300));
                }else {
                    showSnackBar(rhyaErrorCodeManager.getErrorMessage(999));
                }
            }
        }.execute(null);
    }



    private void getAccountInfo(String authToken) {
        // ????????? ????????????
        rhyaDialogManager_Type0.showDialog();
        // ?????? ??????
        new RhyaAsyncTask<String, String>() {
            // ?????? ??????
            private String schoolName;

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(String arg) {
                try {
                    try {
                        // RHYA.Network ??? ???????????? Auth Token ??????
                        RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
                        ContentValues urlParameter = new ContentValues();
                        // ???????????? ??????
                        urlParameter.put("authToken", authToken);
                        urlParameter.put("mode", 2);
                        // JSON ????????? ??????
                        JSONObject serverResponseJSONObject = new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_ACCOUNT_SYNC, urlParameter));
                        String serverResponse = serverResponseJSONObject.getString("result");

                        // ????????? ??????
                        if (serverResponse.equals("success")) {
                            final String UTF_8 = "UTF-8";

                            onlineAttendanceTeacherVO = new OnlineAttendanceTeacherVO(
                                    URLDecoder.decode(serverResponseJSONObject.getString("uuid"), UTF_8),
                                    URLDecoder.decode(serverResponseJSONObject.getString("name"), UTF_8),
                                    URLDecoder.decode(serverResponseJSONObject.getString("name2"), UTF_8),
                                    URLDecoder.decode(serverResponseJSONObject.getString("image"), UTF_8),
                                    URLDecoder.decode(serverResponseJSONObject.getString("description"), UTF_8),
                                    URLDecoder.decode(serverResponseJSONObject.getString("department1"), UTF_8),
                                    URLDecoder.decode(serverResponseJSONObject.getString("department2"), UTF_8),
                                    URLDecoder.decode(serverResponseJSONObject.getString("email_address"), UTF_8),
                                    URLDecoder.decode(serverResponseJSONObject.getString("mobile_phone"), UTF_8),
                                    URLDecoder.decode(serverResponseJSONObject.getString("office_phone"), UTF_8),
                                    URLDecoder.decode(serverResponseJSONObject.getString("position"), UTF_8),
                                    URLDecoder.decode(serverResponseJSONObject.getString("subject"), UTF_8),
                                    serverResponseJSONObject.getInt("school_id"),
                                    serverResponseJSONObject.getInt("version"));

                            // ???????????? ??????
                            urlParameter.remove("authToken");
                            urlParameter.put("mode", 3);
                            urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                            // JSON ????????? ??????
                            JSONObject JSONObject = new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_ACCOUNT_SYNC, urlParameter));
                            String schoolNameResult = JSONObject.getString("result");

                            if (schoolNameResult.equals("success"))
                                this.schoolName = URLDecoder.decode(JSONObject.getString("message"), UTF_8);
                            else
                                this.schoolName = null;

                            return "Success";
                        }else {
                            onlineAttendanceTeacherVO = null;

                            return "Fail";
                        }
                    }catch (JSONException ex) {
                        onlineAttendanceTeacherVO = null;

                        ex.printStackTrace();

                        return "JSONException";
                    }
                } catch (Exception ex) {
                    onlineAttendanceTeacherVO = null;

                    // ?????? ????????? ??????
                    ex.printStackTrace();

                    return "Exception";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                rhyaDialogManager_Type0.dismissDialog();

                // ?????? ??????
                switch (result) {
                    case "Exception": {
                        // ?????? ????????? ??????
                        accountSyncLayout.setVisibility(View.GONE);
                        accountSyncSuccessLayout.setVisibility(View.GONE);
                        accountSyncFailLayout.setVisibility(View.VISIBLE);

                        showSnackBar(rhyaErrorCodeManager.getErrorMessage(999));
                        break;
                    }

                    case "JSONException": {
                        // ?????? ????????? ??????
                        accountSyncLayout.setVisibility(View.GONE);
                        accountSyncSuccessLayout.setVisibility(View.GONE);
                        accountSyncFailLayout.setVisibility(View.VISIBLE);

                        showSnackBar(rhyaErrorCodeManager.getErrorMessage(101));
                        break;
                    }

                    case "Fail": {
                        // ?????? ????????? ??????
                        accountSyncLayout.setVisibility(View.GONE);
                        accountSyncSuccessLayout.setVisibility(View.GONE);
                        accountSyncFailLayout.setVisibility(View.VISIBLE);

                        showSnackBar("????????? ????????? ???????????? ?????? ????????? ?????????????????????.");
                        break;
                    }

                    case "Success": {
                        // UI ????????? ??????
                        // =============================================================================== //
                        // =============================================================================== //
                        // ????????? ?????????
                        Glide.with(context)
                                .load(onlineAttendanceTeacherVO.getImageURL(authToken))
                                .placeholder(R.drawable.teacher_icon)
                                .error(R.drawable.teacher_icon)
                                .fallback(R.drawable.teacher_icon)
                                .signature(new ObjectKey(onlineAttendanceTeacherVO.getVersion()))
                                .into(teacherImageView);
                        // ????????? ??????
                        String teacherName = onlineAttendanceTeacherVO.getName();
                        String content = teacherName.concat("?????????");
                        SpannableString spannableString = new SpannableString(content);
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0074FF")), 0, teacherName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        teacherNameTextView.setText(spannableString);
                        // ?????? ??????
                        if (schoolName == null) schoolName = "?????? ?????? ??????";
                        schoolNameTextView.setText(schoolName);
                        // =============================================================================== //
                        // =============================================================================== //

                        // ?????? ??????
                        accountSyncLayout.setVisibility(View.GONE);
                        accountSyncSuccessLayout.setVisibility(View.VISIBLE);
                        accountSyncFailLayout.setVisibility(View.GONE);

                        // ????????? ????????? ??????
                        loadAttendanceInfo(authToken);
                    }
                }
            }
        }.execute(null);
    }



    private void requestAccountSync() {
        rhyaDialogManager_Type1.showDialog();
    }



    private void loadAttendanceInfo(String authToken) {
        // ?????? ????????? ??????
        new RhyaAsyncTask<String, String>() {
            // ?????????
            private String version;
            private String signKey;
            private String exceptionStr;


            @Override
            protected void onPreExecute() {
                version = null;
                signKey = null;
            }

            @Override
            protected String doInBackground(String arg) {
                try {
                    // RHYA.Network ??? ???????????? Auth Token ??????
                    RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
                    ContentValues urlParameter = new ContentValues();
                    // ???????????? ??????
                    urlParameter.put("authToken", authToken);
                    urlParameter.put("mode", 0);
                    // JSON ????????? ??????
                    JSONObject serverResponseJSONObjectForMode0 = new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                    String serverResponseForMode0 = serverResponseJSONObjectForMode0.getString("result");

                    // ????????? ??????
                    if (serverResponseForMode0.equals("success")) {
                        // Mode 0 ?????? ?????? ??????
                        version = serverResponseJSONObjectForMode0.getString("version");
                        signKey = serverResponseJSONObjectForMode0.getString("app_sign_key");

                        // ???????????? ??????
                        if (!rhyaMainUtils.updateChecker(version)) {
                            // ???????????? ??????
                            return "plz_update";
                        }else {
                            // ?????? ??????
                            CellphoneRoutingCheck cellphoneRoutingCheck = new CellphoneRoutingCheck();
                            if (cellphoneRoutingCheck.checkSuperUser()) return "root_user";

                            // ????????? ??????
                            if (integrityCheck(signKey)) {
                                // ????????? ?????? ??????
                                File teacherInfoFile = new File(rhyaDataManager.SAVE_ROOT_PATH, rhyaDataManager.TEACHER_INFO_FILENAME);
                                File studentInfoFile = new File(rhyaDataManager.SAVE_ROOT_PATH, rhyaDataManager.STUDENT_INFO_FILENAME);
                                HashMap<String, OnlineAttendanceTeacherVO> onlineAttendanceTeacherVOHashMap;
                                HashMap<String, OnlineAttendanceStudentVO> onlineAttendanceStudentVOHashMap;
                                String teacherInfoFileValue;
                                String studentInfoFileValue;


                                // =====================================================================================================================
                                // =====================================================================================================================
                                if (!teacherInfoFile.exists()) {
                                    // ????????? ?????? ?????? ????????????
                                    urlParameter.put("mode", 1);
                                    urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                    JSONObject serverResponseJSONObject =
                                            new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                    String serverResponse = serverResponseJSONObject.getString("result");
                                    // ?????? ??????
                                    if (serverResponse.equals("success")) {
                                        teacherInfoFileValue = serverResponseJSONObject.toString();
                                        // ?????? ??????
                                        rhyaDataManager.writeFile(
                                                teacherInfoFile,
                                                rhyaAESManager.aesEncode(teacherInfoFileValue)
                                        );
                                        // JSON ??????
                                        onlineAttendanceTeacherVOHashMap = rhyaDataManager.readJSONToTeacherVO(teacherInfoFileValue, true);
                                    }else {
                                        return "mode_1_fail";
                                    }
                                }else {
                                    // ?????? ?????? ?????? ?????? ??????
                                    try {
                                        // ?????? ??????
                                        teacherInfoFileValue = rhyaDataManager.readFile(teacherInfoFile);
                                        teacherInfoFileValue = rhyaAESManager.aesDecode(teacherInfoFileValue);
                                        // JSON ??????
                                        onlineAttendanceTeacherVOHashMap = rhyaDataManager.readJSONToTeacherVO(teacherInfoFileValue, true);
                                        // School ????????? ??????
                                        for (String uuid : onlineAttendanceTeacherVOHashMap.keySet()) {
                                            if (Objects.requireNonNull(onlineAttendanceTeacherVOHashMap.get(uuid)).getSchool_id() != onlineAttendanceTeacherVO.getSchool_id()) {
                                                // ?????? ??????
                                                if (teacherInfoFile.exists()) {
                                                    //noinspection ResultOfMethodCallIgnored
                                                    teacherInfoFile.delete();
                                                }
                                                if (studentInfoFile.exists()) {
                                                    //noinspection ResultOfMethodCallIgnored
                                                    studentInfoFile.delete();
                                                }

                                                return "school_id_error";
                                            }
                                        }
                                        // ????????? ?????????
                                        urlParameter.put("mode", 5);
                                        urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                        JSONObject serverResponseJSONObject =
                                                new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                        String serverResponse = serverResponseJSONObject.getString("result");
                                        // ?????? ??????
                                        if (serverResponse.equals("success")) {
                                            // JSON Object, Array
                                            JSONArray teacherJSONRootArray = serverResponseJSONObject.getJSONArray("teacher");
                                            // JSON Array for
                                            for (int jsonArrayIndex = 0; jsonArrayIndex < teacherJSONRootArray.length(); jsonArrayIndex++) {
                                                // ????????? ?????? ??????
                                                JSONObject teacherInfo = teacherJSONRootArray.getJSONObject(jsonArrayIndex);
                                                String uuid = teacherInfo.getString("uuid");
                                                int version = teacherInfo.getInt("version");
                                                // ?????? ??????
                                                OnlineAttendanceTeacherVO tempOnlineAttendanceTeacherVO = onlineAttendanceTeacherVOHashMap.get(uuid);
                                                if (tempOnlineAttendanceTeacherVO == null) {
                                                    // ????????? ?????? ?????? ??????
                                                    urlParameter.put("mode", 6);
                                                    urlParameter.put("uuid", uuid);
                                                    JSONObject teacherInfoServerResponseJSONObject =
                                                            new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                                    String teacherInfoServerResponse = teacherInfoServerResponseJSONObject.getString("result");
                                                    // ?????? ??????
                                                    if (teacherInfoServerResponse.equals("success")) {
                                                        uuid = teacherInfoServerResponseJSONObject.getString("uuid");
                                                        String name = teacherInfoServerResponseJSONObject.getString("name");
                                                        String name2 = teacherInfoServerResponseJSONObject.getString("name2");
                                                        String image = teacherInfoServerResponseJSONObject.getString("image");
                                                        String description = teacherInfoServerResponseJSONObject.getString("description");
                                                        String department1 = teacherInfoServerResponseJSONObject.getString("department1");
                                                        String department2 = teacherInfoServerResponseJSONObject.getString("department2");
                                                        String office_phone = teacherInfoServerResponseJSONObject.getString("office_phone");
                                                        String position = teacherInfoServerResponseJSONObject.getString("position");
                                                        String subject = teacherInfoServerResponseJSONObject.getString("subject");
                                                        int school_id = teacherInfoServerResponseJSONObject.getInt("school_id");
                                                        version = teacherInfoServerResponseJSONObject.getInt("version");

                                                        final String UTF_8 = "UTF-8";
                                                        uuid = URLDecoder.decode(uuid, UTF_8);
                                                        name = URLDecoder.decode(name, UTF_8);
                                                        name2 = URLDecoder.decode(name2, UTF_8);
                                                        image = URLDecoder.decode(image, UTF_8);
                                                        description = URLDecoder.decode(description, UTF_8);
                                                        department1 = URLDecoder.decode(department1, UTF_8);
                                                        department2 = URLDecoder.decode(department2, UTF_8);
                                                        office_phone = URLDecoder.decode(office_phone, UTF_8);
                                                        position = URLDecoder.decode(position, UTF_8);
                                                        subject = URLDecoder.decode(subject, UTF_8);

                                                        tempOnlineAttendanceTeacherVO = new OnlineAttendanceTeacherVO(
                                                                uuid,
                                                                name,
                                                                name2,
                                                                image,
                                                                description,
                                                                department1,
                                                                department2,
                                                                null, // ?????? ????????? ??????
                                                                null, // ?????? ????????? ??????
                                                                office_phone,
                                                                position,
                                                                subject,
                                                                school_id,
                                                                version
                                                        );
                                                        onlineAttendanceTeacherVOHashMap.put(uuid, tempOnlineAttendanceTeacherVO);
                                                    }else {
                                                        onlineAttendanceTeacherVOHashMap.remove(uuid);
                                                    }
                                                }else {
                                                    if (tempOnlineAttendanceTeacherVO.getVersion() != version) {
                                                        // ????????? ?????? ?????? ??????
                                                        urlParameter.put("mode", 6);
                                                        urlParameter.put("uuid", uuid);
                                                        JSONObject teacherInfoServerResponseJSONObject =
                                                                new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                                        String teacherInfoServerResponse = teacherInfoServerResponseJSONObject.getString("result");
                                                        // ?????? ??????
                                                        if (teacherInfoServerResponse.equals("success")) {
                                                            uuid = teacherInfoServerResponseJSONObject.getString("uuid");
                                                            String name = teacherInfoServerResponseJSONObject.getString("name");
                                                            String name2 = teacherInfoServerResponseJSONObject.getString("name2");
                                                            String image = teacherInfoServerResponseJSONObject.getString("image");
                                                            String description = teacherInfoServerResponseJSONObject.getString("description");
                                                            String department1 = teacherInfoServerResponseJSONObject.getString("department1");
                                                            String department2 = teacherInfoServerResponseJSONObject.getString("department2");
                                                            String office_phone = teacherInfoServerResponseJSONObject.getString("office_phone");
                                                            String position = teacherInfoServerResponseJSONObject.getString("position");
                                                            String subject = teacherInfoServerResponseJSONObject.getString("subject");
                                                            int school_id = teacherInfoServerResponseJSONObject.getInt("school_id");
                                                            version = teacherInfoServerResponseJSONObject.getInt("version");

                                                            final String UTF_8 = "UTF-8";
                                                            uuid = URLDecoder.decode(uuid, UTF_8);
                                                            name = URLDecoder.decode(name, UTF_8);
                                                            name2 = URLDecoder.decode(name2, UTF_8);
                                                            image = URLDecoder.decode(image, UTF_8);
                                                            description = URLDecoder.decode(description, UTF_8);
                                                            department1 = URLDecoder.decode(department1, UTF_8);
                                                            department2 = URLDecoder.decode(department2, UTF_8);
                                                            office_phone = URLDecoder.decode(office_phone, UTF_8);
                                                            position = URLDecoder.decode(position, UTF_8);
                                                            subject = URLDecoder.decode(subject, UTF_8);

                                                            tempOnlineAttendanceTeacherVO.setUuid(uuid);
                                                            tempOnlineAttendanceTeacherVO.setName(name);
                                                            tempOnlineAttendanceTeacherVO.setName2(name2);
                                                            tempOnlineAttendanceTeacherVO.setImage(image);
                                                            tempOnlineAttendanceTeacherVO.setDescription(description);
                                                            tempOnlineAttendanceTeacherVO.setDepartment1(department1);
                                                            tempOnlineAttendanceTeacherVO.setDepartment2(department2);
                                                            tempOnlineAttendanceTeacherVO.setOffice_phone(office_phone);
                                                            tempOnlineAttendanceTeacherVO.setPosition(position);
                                                            tempOnlineAttendanceTeacherVO.setSubject(subject);
                                                            tempOnlineAttendanceTeacherVO.setSchool_id(school_id);
                                                            tempOnlineAttendanceTeacherVO.setVersion(version);
                                                            onlineAttendanceTeacherVOHashMap.put(uuid, tempOnlineAttendanceTeacherVO);
                                                        }else {
                                                            onlineAttendanceTeacherVOHashMap.remove(uuid);
                                                        }
                                                    }
                                                }
                                            }

                                            // ????????? ???????????? ????????? ???????????????
                                            JSONObject newWriteTeacherJSONRootObject = new JSONObject();
                                            JSONArray newWriteTeacherJSONRootArray = new JSONArray();
                                            for (String teacherUUID : onlineAttendanceTeacherVOHashMap.keySet()) {
                                                OnlineAttendanceTeacherVO onlineAttendanceTeacherVO = onlineAttendanceTeacherVOHashMap.get(teacherUUID);

                                                if (onlineAttendanceTeacherVO != null) {
                                                    JSONObject tempObj = new JSONObject();

                                                    tempObj.put("uuid", onlineAttendanceTeacherVO.getUuid());
                                                    tempObj.put("name", onlineAttendanceTeacherVO.getName());
                                                    tempObj.put("name2", onlineAttendanceTeacherVO.getName2());
                                                    tempObj.put("image", onlineAttendanceTeacherVO.getImage());
                                                    tempObj.put("description", onlineAttendanceTeacherVO.getDescription());
                                                    tempObj.put("department1", onlineAttendanceTeacherVO.getDepartment1());
                                                    tempObj.put("department2", onlineAttendanceTeacherVO.getDepartment2());
                                                    tempObj.put("office_phone", onlineAttendanceTeacherVO.getOffice_phone());
                                                    tempObj.put("position", onlineAttendanceTeacherVO.getPosition());
                                                    tempObj.put("subject", onlineAttendanceTeacherVO.getSubject());
                                                    tempObj.put("school_id", onlineAttendanceTeacherVO.getSchool_id());
                                                    tempObj.put("version", onlineAttendanceTeacherVO.getVersion());
                                                    newWriteTeacherJSONRootArray.put(tempObj);
                                                }
                                            }
                                            newWriteTeacherJSONRootObject.put("teacher", newWriteTeacherJSONRootArray);
                                            rhyaDataManager.writeFile(
                                                    teacherInfoFile,
                                                    rhyaAESManager.aesEncode(newWriteTeacherJSONRootObject.toString())
                                            );
                                        }else {
                                            return "mode_5_fail";
                                        }
                                    }catch (Exception ex) {
                                        ex.printStackTrace();
                                        // ????????? ?????? ?????? ?????? ??????
                                        // ?????? ?????? ????????????

                                        // ?????? ??????
                                        //noinspection ResultOfMethodCallIgnored
                                        teacherInfoFile.delete();
                                        // ????????? ?????? ?????? ????????????
                                        urlParameter.put("mode", 1);
                                        urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                        JSONObject serverResponseJSONObject =
                                                new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                        String serverResponse = serverResponseJSONObject.getString("result");
                                        // ?????? ??????
                                        if (serverResponse.equals("success")) {
                                            teacherInfoFileValue = serverResponseJSONObject.toString();
                                            // ?????? ??????
                                            rhyaDataManager.writeFile(
                                                    teacherInfoFile,
                                                    rhyaAESManager.aesEncode(teacherInfoFileValue)
                                            );
                                            // JSON ??????
                                            onlineAttendanceTeacherVOHashMap = rhyaDataManager.readJSONToTeacherVO(teacherInfoFileValue, true);
                                        }else {
                                            return "mode_1_fail";
                                        }
                                    }
                                }
                                // =====================================================================================================================
                                // =====================================================================================================================


                                // =====================================================================================================================
                                // =====================================================================================================================
                                if (!studentInfoFile.exists()) {
                                    // ?????? ?????? ?????? ????????????
                                    urlParameter.put("mode", 3);
                                    urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                    JSONObject serverResponseJSONObject =
                                            new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                    String serverResponseForMode = serverResponseJSONObject.getString("result");
                                    // ?????? ??????
                                    if (serverResponseForMode.equals("success")) {
                                        studentInfoFileValue = serverResponseJSONObject.toString();
                                        // ?????? ??????
                                        rhyaDataManager.writeFile(
                                                studentInfoFile,
                                                rhyaAESManager.aesEncode(studentInfoFileValue)
                                        );
                                        // JSON ??????
                                        onlineAttendanceStudentVOHashMap = rhyaDataManager.readJSONToStudentVO(studentInfoFileValue, onlineAttendanceTeacherVO.getSchool_id(),true);
                                    }else {
                                        return "mode_3_fail";
                                    }
                                }else {
                                    // ?????? ?????? ?????? ?????? ??????
                                    try {
                                        // ?????? ??????
                                        studentInfoFileValue = rhyaDataManager.readFile(studentInfoFile);
                                        studentInfoFileValue = rhyaAESManager.aesDecode(studentInfoFileValue);
                                        // JSON ??????
                                        onlineAttendanceStudentVOHashMap = rhyaDataManager.readJSONToStudentVO(studentInfoFileValue, onlineAttendanceTeacherVO.getSchool_id(), true);
                                        // School ????????? ??????
                                        for (String uuid : onlineAttendanceStudentVOHashMap.keySet()) {
                                            if (Objects.requireNonNull(onlineAttendanceStudentVOHashMap.get(uuid)).getSchool_id() != onlineAttendanceTeacherVO.getSchool_id()) {
                                                // ?????? ??????
                                                if (teacherInfoFile.exists()) {
                                                    //noinspection ResultOfMethodCallIgnored
                                                    teacherInfoFile.delete();
                                                }
                                                if (studentInfoFile.exists()) {
                                                    //noinspection ResultOfMethodCallIgnored
                                                    studentInfoFile.delete();
                                                }

                                                return "school_id_error";
                                            }
                                        }
                                        // ????????? ?????????
                                        urlParameter.put("mode", 4);
                                        urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                        JSONObject serverResponseJSONObject =
                                                new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                        String serverResponse = serverResponseJSONObject.getString("result");
                                        // ?????? ??????
                                        if (serverResponse.equals("success")) {
                                            // JSON Object, Array
                                            JSONArray studentJSONRootArray = serverResponseJSONObject.getJSONArray("student");
                                            // JSON Array for
                                            for (int jsonArrayIndex = 0; jsonArrayIndex < studentJSONRootArray.length(); jsonArrayIndex++) {
                                                // ?????? ?????? ??????
                                                JSONObject studentInfo = studentJSONRootArray.getJSONObject(jsonArrayIndex);
                                                String uuid = studentInfo.getString("student_uuid");
                                                int version = studentInfo.getInt("version");
                                                // ?????? ??????
                                                OnlineAttendanceStudentVO tempOnlineAttendanceStudentVO = onlineAttendanceStudentVOHashMap.get(uuid);
                                                if (tempOnlineAttendanceStudentVO == null) {
                                                    // ?????? ?????? ?????? ??????
                                                    urlParameter.put("mode", 7);
                                                    urlParameter.put("uuid", uuid);
                                                    JSONObject studentInfoServerResponseJSONObject =
                                                            new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                                    String studentInfoServerResponse = studentInfoServerResponseJSONObject.getString("result");
                                                    // ?????? ??????
                                                    if (studentInfoServerResponse.equals("success")) {
                                                        String student_uuid = studentInfoServerResponseJSONObject.getString("student_uuid");
                                                        String student_class_uuid = studentInfoServerResponseJSONObject.getString("student_class_uuid");
                                                        int student_number = studentInfoServerResponseJSONObject.getInt("student_number");
                                                        String student_name = studentInfoServerResponseJSONObject.getString("student_name");
                                                        String student_image = studentInfoServerResponseJSONObject.getString("student_image");
                                                        int gender = studentInfoServerResponseJSONObject.getInt("gender");
                                                        int move_out = studentInfoServerResponseJSONObject.getInt("move_out");
                                                        int year = studentInfoServerResponseJSONObject.getInt("year");
                                                        String note = studentInfoServerResponseJSONObject.getString("note");
                                                        version = studentInfoServerResponseJSONObject.getInt("version");

                                                        final String UTF_8 = "UTF-8";
                                                        student_uuid = URLDecoder.decode(student_uuid, UTF_8);
                                                        student_class_uuid = URLDecoder.decode(student_class_uuid, UTF_8);
                                                        student_name = URLDecoder.decode(student_name, UTF_8);
                                                        student_image = URLDecoder.decode(student_image, UTF_8);
                                                        note = URLDecoder.decode(note, UTF_8);

                                                        tempOnlineAttendanceStudentVO = new OnlineAttendanceStudentVO(
                                                                student_uuid,
                                                                student_class_uuid,
                                                                student_number,
                                                                student_name,
                                                                student_image,
                                                                gender,
                                                                move_out,
                                                                year,
                                                                note,
                                                                version,
                                                                onlineAttendanceTeacherVO.getSchool_id()
                                                        );
                                                        onlineAttendanceStudentVOHashMap.put(uuid, tempOnlineAttendanceStudentVO);
                                                    }else {
                                                        onlineAttendanceStudentVOHashMap.remove(uuid);
                                                    }
                                                }else {
                                                    if (tempOnlineAttendanceStudentVO.getVersion() != version) {
                                                        // ????????? ?????? ?????? ??????
                                                        urlParameter.put("mode", 7);
                                                        urlParameter.put("uuid", uuid);
                                                        JSONObject studentInfoServerResponseJSONObject =
                                                                new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                                        String studentInfoServerResponse = studentInfoServerResponseJSONObject.getString("result");
                                                        // ?????? ??????
                                                        if (studentInfoServerResponse.equals("success")) {
                                                            String student_uuid = studentInfoServerResponseJSONObject.getString("student_uuid");
                                                            String student_class_uuid = studentInfoServerResponseJSONObject.getString("student_class_uuid");
                                                            int student_number = studentInfoServerResponseJSONObject.getInt("student_number");
                                                            String student_name = studentInfoServerResponseJSONObject.getString("student_name");
                                                            String student_image = studentInfoServerResponseJSONObject.getString("student_image");
                                                            int gender = studentInfoServerResponseJSONObject.getInt("gender");
                                                            int move_out = studentInfoServerResponseJSONObject.getInt("move_out");
                                                            int year = studentInfoServerResponseJSONObject.getInt("year");
                                                            String note = studentInfoServerResponseJSONObject.getString("note");
                                                            version = studentInfoServerResponseJSONObject.getInt("version");

                                                            final String UTF_8 = "UTF-8";
                                                            student_uuid = URLDecoder.decode(student_uuid, UTF_8);
                                                            student_class_uuid = URLDecoder.decode(student_class_uuid, UTF_8);
                                                            student_name = URLDecoder.decode(student_name, UTF_8);
                                                            student_image = URLDecoder.decode(student_image, UTF_8);
                                                            note = URLDecoder.decode(note, UTF_8);

                                                            tempOnlineAttendanceStudentVO.setUuid(student_uuid);
                                                            tempOnlineAttendanceStudentVO.setClass_uuid(student_class_uuid);
                                                            tempOnlineAttendanceStudentVO.setNumber(student_number);
                                                            tempOnlineAttendanceStudentVO.setName(student_name);
                                                            tempOnlineAttendanceStudentVO.setImage(student_image);
                                                            tempOnlineAttendanceStudentVO.setGender(gender);
                                                            tempOnlineAttendanceStudentVO.setMove_out(move_out);
                                                            tempOnlineAttendanceStudentVO.setYear(year);
                                                            tempOnlineAttendanceStudentVO.setNote(note);
                                                            tempOnlineAttendanceStudentVO.setVersion(version);
                                                            onlineAttendanceStudentVOHashMap.put(uuid, tempOnlineAttendanceStudentVO);
                                                        }else {
                                                            onlineAttendanceStudentVOHashMap.remove(uuid);
                                                        }
                                                    }
                                                }
                                            }

                                            // ????????? ???????????? ????????? ???????????????
                                            JSONObject newWriteStudentJSONRootObject = new JSONObject();
                                            JSONArray newWriteStudentJSONRootArray = new JSONArray();
                                            for (String studentUUID : onlineAttendanceStudentVOHashMap.keySet()) {
                                                OnlineAttendanceStudentVO onlineAttendanceStudentVO = onlineAttendanceStudentVOHashMap.get(studentUUID);

                                                if (onlineAttendanceStudentVO != null) {
                                                    JSONObject tempObj = new JSONObject();

                                                    tempObj.put("student_uuid", onlineAttendanceStudentVO.getUuid());
                                                    tempObj.put("student_class_uuid", onlineAttendanceStudentVO.getClass_uuid());
                                                    tempObj.put("student_number", onlineAttendanceStudentVO.getNumber());
                                                    tempObj.put("student_name", onlineAttendanceStudentVO.getName());
                                                    tempObj.put("student_image", onlineAttendanceStudentVO.getImage());
                                                    tempObj.put("gender", onlineAttendanceStudentVO.getGender());
                                                    tempObj.put("move_out", onlineAttendanceStudentVO.getMove_out());
                                                    tempObj.put("year", onlineAttendanceStudentVO.getYear());
                                                    tempObj.put("note", onlineAttendanceStudentVO.getNote());
                                                    tempObj.put("school_id", onlineAttendanceStudentVO.getSchool_id());
                                                    tempObj.put("version", onlineAttendanceStudentVO.getVersion());
                                                    newWriteStudentJSONRootArray.put(tempObj);
                                                }
                                            }
                                            newWriteStudentJSONRootObject.put("student", newWriteStudentJSONRootArray);
                                            rhyaDataManager.writeFile(
                                                    studentInfoFile,
                                                    rhyaAESManager.aesEncode(newWriteStudentJSONRootObject.toString())
                                            );
                                        }else {
                                            return "mode_4_fail";
                                        }
                                    }catch (Exception ex) {
                                        ex.printStackTrace();
                                        // ????????? ?????? ?????? ?????? ??????
                                        // ?????? ?????? ????????????

                                        // ?????? ??????
                                        //noinspection ResultOfMethodCallIgnored
                                        studentInfoFile.delete();
                                        // ?????? ?????? ?????? ????????????
                                        urlParameter.put("mode", 3);
                                        urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                        JSONObject serverResponseJSONObject =
                                                new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                        String serverResponseForMode = serverResponseJSONObject.getString("result");
                                        // ?????? ??????
                                        if (serverResponseForMode.equals("success")) {
                                            studentInfoFileValue = serverResponseJSONObject.toString();
                                            // ?????? ??????
                                            rhyaDataManager.writeFile(
                                                    studentInfoFile,
                                                    rhyaAESManager.aesEncode(studentInfoFileValue)
                                            );
                                            // JSON ??????
                                            onlineAttendanceStudentVOHashMap = rhyaDataManager.readJSONToStudentVO(studentInfoFileValue, onlineAttendanceTeacherVO.getSchool_id(),true);
                                        }else {
                                            return "mode_3_fail";
                                        }
                                    }
                                }
                                // =====================================================================================================================
                                // =====================================================================================================================


                                // =====================================================================================================================
                                // =====================================================================================================================
                                if (RhyaApplication.getOnlineAttendanceClassVOHashMap() == null) {
                                    // ????????? ?????????
                                    urlParameter.put("mode", 8);
                                    urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                    JSONObject serverResponseJSONObject =
                                            new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                    String serverResponse = serverResponseJSONObject.getString("result");
                                    // ?????? ??????
                                    if (serverResponse.equals("success")) {
                                        // JSON Object, Array
                                        HashMap<String, OnlineAttendanceClassVO> onlineAttendanceClassVOHashMap = new HashMap<>();
                                        JSONArray studentJSONRootArray = serverResponseJSONObject.getJSONArray("class");
                                        // JSON Array for
                                        for (int jsonArrayIndex = 0; jsonArrayIndex < studentJSONRootArray.length(); jsonArrayIndex++) {
                                            // ????????? ?????? ??????
                                            JSONObject studentInfo = studentJSONRootArray.getJSONObject(jsonArrayIndex);
                                            String uuid = studentInfo.getString("class_uuid");
                                            final String UTF_8 = "UTF-8";

                                            OnlineAttendanceClassVO onlineAttendanceClassVO = new OnlineAttendanceClassVO(
                                                    uuid,
                                                    URLDecoder.decode(studentInfo.getString("class_nickname"), UTF_8),
                                                    URLDecoder.decode(studentInfo.getString("class_teacher_uuid"), UTF_8),
                                                    studentInfo.getInt("version")
                                            );

                                            onlineAttendanceClassVOHashMap.put(uuid, onlineAttendanceClassVO);
                                        }

                                        RhyaApplication.setOnlineAttendanceClassVOHashMap(onlineAttendanceClassVOHashMap);
                                    }else {
                                        return "mode_8_fail";
                                    }
                                }
                                // =====================================================================================================================
                                // =====================================================================================================================


                                // =====================================================================================================================
                                // =====================================================================================================================
                                if (RhyaApplication.getOnlineAttendanceDepartmentVOHashMap() == null) {
                                    // ????????? ?????????
                                    urlParameter.put("mode", 9);
                                    urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                    JSONObject serverResponseJSONObject =
                                            new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                    String serverResponse = serverResponseJSONObject.getString("result");
                                    // ?????? ??????
                                    if (serverResponse.equals("success")) {
                                        // JSON Object, Array
                                        HashMap<String, OnlineAttendanceDepartmentVO> onlineAttendanceDepartmentVOHashMap = new HashMap<>();
                                        JSONArray studentJSONRootArray = serverResponseJSONObject.getJSONArray("department");
                                        // JSON Array for
                                        for (int jsonArrayIndex = 0; jsonArrayIndex < studentJSONRootArray.length(); jsonArrayIndex++) {
                                            // ????????? ?????? ??????
                                            JSONObject studentInfo = studentJSONRootArray.getJSONObject(jsonArrayIndex);
                                            String uuid = studentInfo.getString("uuid");
                                            final String UTF_8 = "UTF-8";

                                            OnlineAttendanceDepartmentVO onlineAttendanceDepartmentVO = new OnlineAttendanceDepartmentVO(
                                                    uuid,
                                                    URLDecoder.decode(studentInfo.getString("name"), UTF_8),
                                                    studentInfo.getInt("version")
                                            );

                                            onlineAttendanceDepartmentVOHashMap.put(uuid, onlineAttendanceDepartmentVO);
                                        }

                                        RhyaApplication.setOnlineAttendanceDepartmentVOHashMap(onlineAttendanceDepartmentVOHashMap);
                                    }else {
                                        return "mode_8_fail";
                                    }
                                }
                                // =====================================================================================================================
                                // =====================================================================================================================


                                // 1.5??? ??????
                                Thread.sleep(1500);

                                // ????????? ??????
                                RhyaApplication.setOnlineAttendanceTeacherVOHashMap(onlineAttendanceTeacherVOHashMap);
                                RhyaApplication.setOnlineAttendanceStudentVOHashMap(onlineAttendanceStudentVOHashMap);
                            }else {
                                // ??? ?????? ??????
                                return "integrity_fail";
                            }
                        }
                    }else {
                        return "mode_0_fail";
                    }
                } catch (Exception ex) {
                    // ?????? ????????? ??????
                    ex.printStackTrace();
                    exceptionStr = ex.getMessage();

                    return "exception";
                }

                return "no_result";
            }

            @Override
            protected void onPostExecute(String result) {
                // ????????? ??????
                final RhyaDialogManager.DialogListener_Type3 dialogListener_type3_for_exit = () -> finishAndRemoveTask();
                final RhyaDialogManager.DialogListener_Type3 dialogListener_type3_for_update = () -> {
                    // ??? ????????????
                    rhyaDialogManager_Type3.dismissDialog();
                    updateApp();
                };


                // ?????? ??????
                switch (result) {
                    case "no_result": { // ??????
                        // ?????? ??????
                        goNextActivity(authToken);

                        break;
                    }


                    case "plz_update": { // ???????????? ??????
                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                "???????????? ??????",
                                "?????? ???????????? ?????? ?????????????????? ????????? ?????????????????????. ?????? ????????????????????? ??????????????? ???????????????.",
                                "????????????",
                                dialogListener_type3_for_update
                        );
                        rhyaDialogManager_Type3.showDialog();

                        break;
                    }


                    case "root_user": { // ?????? ?????? ??????
                        StringBuilder stringBuilder;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("?????? ???????????? ?????? ????????? ?????? ??? ???????????????. ?????? ???????????? ????????? ????????? ???????????? ???????????? ??? ????????????. ???????????? ???????????? ???????????? ????????? ????????? ?????????.");
                        stringBuilder.append(System.lineSeparator());
                        stringBuilder.append(System.lineSeparator());
                        stringBuilder.append("?????? ????????? ???????????? ???????????? ????????? ????????? ???????????? ???????????? ?????? ??????????????? ????????????????????????.");
                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                "??????????????? ??????",
                                stringBuilder.toString(),
                                "??????",
                                dialogListener_type3_for_exit
                        );
                        rhyaDialogManager_Type3.showDialog();

                        break;
                    }


                    // ========================================================================================================
                    case "mode_0_fail": // ??? ??????, ??? ?????? SHA ?????? ?????? ??????
                    case "mode_1_fail": // ????????? ????????? ?????????????????? ?????? ????????? ?????? ?????? ??????
                    case "mode_3_fail": // ????????? ????????? ?????????????????? ?????? ?????? ?????? ?????? ??????
                    case "mode_4_fail": // ????????? ????????? ?????????????????? ?????? ?????? ?????? ?????? [ Version ????????? UUID??? ?????? ] ??????
                    case "mode_5_fail": // ????????? ????????? ?????????????????? ?????? ????????? ?????? ?????? [ Version ????????? UUID??? ?????? ] ??????
                    case "mode_8_fail": // ????????? ????????? ???????????? ?????? ??? ?????? ?????? ??????
                        {
                            StringBuilder stringBuilder;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(rhyaErrorCodeManager.getErrorMessage(101));
                            stringBuilder.append("?????? ????????? ????????? ???????????? ???????????? ?????? ??????????????? ????????????????????????.");
                            stringBuilder.append(System.lineSeparator());
                            stringBuilder.append(System.lineSeparator());
                            stringBuilder.append("ERROR MESSAGE: ");
                            stringBuilder.append(result);

                            rhyaDialogManager_Type3.setDialogSettingType_3(
                                    "?????? ?????? ??????",
                                    stringBuilder.toString(),
                                    "??????",
                                    dialogListener_type3_for_exit
                            );
                            rhyaDialogManager_Type3.showDialog();

                            break;
                        }
                    // ========================================================================================================


                    case "school_id_error": {   // ?????? ???????????? ???????????? ??????
                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                "?????? ?????? ????????? ??????",
                                "?????? ?????? ???????????? ???????????? ?????? ???????????? ????????? ??? ?????? ???????????? ?????????????????????. ?????? ??????????????? ?????? ????????? ???????????? ????????? ??? ????????????. ?????? ????????? ?????? ?????????????????????.",
                                "??????",
                                dialogListener_type3_for_exit
                        );
                        rhyaDialogManager_Type3.showDialog();

                        break;
                    }


                    case "integrity_fail": {   // ????????? ?????? ??????
                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                "??? ?????? ??????",
                                "?????? ???????????? ?????????????????????. ????????? ????????? ?????? ???????????? ????????? ?????? ?????? ????????? ?????????????????????. ?????? ????????? ?????? ????????? ??????????????? ?????? ??? ??????????????? ?????? ????????????. ?????? ?????? ???????????? ???????????? ??????????????? ????????? ????????? ???????????? ?????? ?????? ??????????????? ????????????????????????.",
                                "??????",
                                dialogListener_type3_for_exit
                        );
                        rhyaDialogManager_Type3.showDialog();

                        break;
                    }


                    case "exception": {   // ??? ??? ?????? ??????
                        StringBuilder stringBuilder;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("??? ??? ?????? ????????? ?????????????????????. ?????? ????????? ????????? ???????????? ???????????? ?????? ??????????????? ????????????????????????.");
                        stringBuilder.append(System.lineSeparator());
                        stringBuilder.append(System.lineSeparator());
                        stringBuilder.append(exceptionStr);
                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                "??? ??? ?????? ??????",
                                stringBuilder.toString(),
                                "??????",
                                dialogListener_type3_for_exit
                        );
                        rhyaDialogManager_Type3.showDialog();

                        break;
                    }
                }
            }
        }.execute(null);
    }



    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(completeReceiver, completeFilter);
    }
    @Override
    protected void onPause() {
        super.onPause();

        isEventExit = true;
        unregisterReceiver(completeReceiver);

        finish();
    }



    // =============================================================================
    // =============================================================================
    private final BroadcastReceiver completeReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                if(enqueue == reference) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);
                    Cursor cursor = downloadManager.query(query);

                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);

                    int status = cursor.getInt(columnIndex);
                    int reason = cursor.getInt(columnReason);

                    cursor.close();

                    switch (status){
                        default:
                            Toast.makeText(context, "???????????? ??? ??? ??? ?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                            if (!activity.isFinishing())
                                runOnUiThread(() -> finishAndRemoveTask());

                            break;

                        case DownloadManager.STATUS_SUCCESSFUL:
                            Toast.makeText(context, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show();

                            try {
                                File file = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                                String get = file.getPath();
                                File fileFinal = new File(get, "oa_update_apk.apk");

                                if (Build.VERSION.SDK_INT >= 24) {
                                    // Android Nougat ( 7.0 ) and later
                                    Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", fileFinal);
                                    Intent intentSub = new Intent(Intent.ACTION_VIEW);
                                    intentSub.setDataAndType(uri, "application/vnd.android.package-archive");
                                    intentSub.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    context.startActivity(intentSub);
                                }else {
                                    Intent intentSub = new Intent(Intent.ACTION_VIEW);
                                    Uri apkUri = Uri.fromFile(fileFinal);
                                    intentSub.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                    intentSub.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(intentSub);
                                }
                            }catch (Exception ex) {
                                ex.printStackTrace();

                                Toast.makeText(context, "??? ?????? ??? ?????? ??????", Toast.LENGTH_SHORT).show();

                                if (!activity.isFinishing())
                                    runOnUiThread(() -> finishAndRemoveTask());
                            }

                            break;

                        case DownloadManager.STATUS_PAUSED:
                            Toast.makeText(getApplicationContext(), "??????????????? ?????????????????????. ERROR:" + reason, Toast.LENGTH_SHORT).show();
                            if (!activity.isFinishing())
                                runOnUiThread(() -> finishAndRemoveTask());

                            break;

                        case DownloadManager.STATUS_FAILED:
                            Toast.makeText(getApplicationContext(), "???????????? ??? ????????? ?????????????????????. ERROR:" + reason, Toast.LENGTH_SHORT).show();
                            if (!activity.isFinishing())
                                runOnUiThread(() -> finishAndRemoveTask());

                            break;
                    }
                }
            }catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(context, "??? ???????????? ??? ?????? ??????", Toast.LENGTH_SHORT).show();

                if (!activity.isFinishing())
                    runOnUiThread(() -> finishAndRemoveTask());
            }
        }
    };
    // =============================================================================
    // ???????????? ?????? ??????
    // =============================================================================
    public void updateApp() {
        try {
            File file = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            String get = file.getPath();
            File fileFinal = new File(get, "oa_update_apk.apk");
            if (fileFinal.exists()) //noinspection ResultOfMethodCallIgnored
                fileFinal.delete();

            downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://rhya-network.kro.kr/RhyaNetwork/online_attendance_manager?mode=13"));
            request.setTitle("Online Attendance ????????????");
            request.setDescription("???????????? ???...");
            request.setNotificationVisibility(0);
            request.setVisibleInDownloadsUi(true);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setMimeType("application/vnd.android.package-archive");
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "oa_update_apk.apk");
            enqueue = downloadManager.enqueue(request);
        }catch (Exception ex) {
            ex.printStackTrace();

            Toast.makeText(context, "???????????? ??? ??? ??? ?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();

            if (!activity.isFinishing())
                runOnUiThread(this::finishAndRemoveTask);
        }
    }
    // =============================================================================
    // =============================================================================



    private void showSnackBar(String message) {
        RhyaSnackBarStyle rhyaSnackBarStyle = new RhyaSnackBarStyle();
        Snackbar snackbar = rhyaSnackBarStyle.getSnackBar(mainLayout, message, context);
        snackbar.setAction("??????", v -> snackbar.dismiss());
        snackbar.show();
    }
}
