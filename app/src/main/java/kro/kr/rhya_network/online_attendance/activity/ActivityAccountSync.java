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
    // RHYA Auth Token 관리자 선언
    private RhyaAuthManager rhyaAuthManager;
    // 오류 메시지 관리자
    private RhyaErrorCodeManager rhyaErrorCodeManager;
    // Utils 함수 관리자
    private RhyaMainUtils rhyaMainUtils;
    // 데이터 관리자
    private RhyaDataManager rhyaDataManager;
    // 암호화 관리자
    private RhyaAESManager rhyaAESManager;
    // Dialog 관리자
    private RhyaDialogManager rhyaDialogManager_Type0;
    private RhyaDialogManager rhyaDialogManager_Type1;
    private RhyaDialogManager rhyaDialogManager_Type3;
    // 선생님 데이터
    private OnlineAttendanceTeacherVO onlineAttendanceTeacherVO;
    // Toast message
    private Toast toast;
    // 뒤로가기 버튼 시간
    private long backBtnTime = 0;
    // 뒤로가기 버튼 감지
    private boolean isEventExit = false;
    // 업데이트 데이터
    private long enqueue;
    private DownloadManager downloadManager;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_account_sync);
        super.onCreate(savedInstanceState);


        // UI 초기화
        accountSyncLayout = findViewById(R.id.accountSyncLayout);
        accountSyncFailLayout = findViewById(R.id.accountSyncFailLayout);
        accountSyncSuccessLayout = findViewById(R.id.accountSyncSuccessLayout);
        mainLayout = findViewById(R.id.mainLayout);
        Button accountRestartSyncButton = findViewById(R.id.accountRestartSyncButton);
        Button accountRequestSyncButton = findViewById(R.id.accountRequestSyncButton);
        teacherImageView = findViewById(R.id.teacherImageView);
        teacherNameTextView = findViewById(R.id.teacherNameTextView);
        schoolNameTextView = findViewById(R.id.schoolNameTextView);


        // 변수 초기화
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

        // 예외 처리
        try {
            // Auth Token 구하기
            authToken = rhyaAuthManager.getSharedPreferencesAuthToken();

            checkAccountSync(authToken);
        } catch (Exception ex) {
            // 오류 메시지 출력
            ex.printStackTrace();

            goBackActivity();
        }
        String finalAuthToken = authToken;


        // Dialog 관리자 초기화
        rhyaDialogManager_Type0 = new RhyaDialogManager(
                this,
                false,
                0.85,
                0);
        rhyaDialogManager_Type0.setDialogSettingType_0("작업 처리 중...");
        rhyaDialogManager_Type1 = new RhyaDialogManager(
                this,
                true,
                0.95,
                1);
        rhyaDialogManager_Type1.setDialogSettingType_1(new RhyaDialogManager.DialogListener_Type1() {
            @Override
            public void onClickListenerButtonNoKey() {
                // Dialog 닫기
                rhyaDialogManager_Type1.dismissDialog();
                // Dialog 생성
                rhyaDialogManager_Type0.showDialog();

                accountSyncRequestClient(finalAuthToken, 0, null);
            }

            @Override
            public void onClickListenerButtonSyncAccount(String key) {
                // Dialog 닫기
                rhyaDialogManager_Type1.dismissDialog();
                // Dialog 생성
                rhyaDialogManager_Type0.showDialog();

                accountSyncRequestClient(finalAuthToken, 1, key);
            }
        });
        rhyaDialogManager_Type3 = new RhyaDialogManager(
                this,
                false,
                0.95,
                3);


        // 리스너 등록
        accountRestartSyncButton.setOnClickListener(v -> checkAccountSync(finalAuthToken));
        accountRequestSyncButton.setOnClickListener(v -> requestAccountSync());
    }



    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            // 종료
            isEventExit = true;

            finish();
        } else {
            isEventExit = false;
            backBtnTime = curTime;

            // Toast 메시지 출력
            toast.cancel();
            toast.makeText(context, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
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
            // ActivitySplash 전환
            Intent intent = new Intent(context, ActivitySplash.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
    }



    private void goNextActivity(String authToken) {
        if (!isEventExit && !this.isFinishing()) {
            // ActivitySplash 전환
            Intent intent = new Intent(context, ActivityMain.class);
            // 인자 설정
            intent.putExtra("authToken", authToken);
            intent.putExtra("myInfo", onlineAttendanceTeacherVO);
            // 화면 전환
            startActivity(intent);
            overridePendingTransition(R.anim.slide_right_enter, R.anim.scale_small);
            finish();
        }
    }



    private void checkAccountSync(String authToken) {
        // 계정 동기화 확인
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
                    // RHYA.Network 에 접속하여 Auth Token 확인
                    RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
                    ContentValues urlParameter = new ContentValues();
                    // 파라미터 설정
                    urlParameter.put("authToken", authToken);
                    urlParameter.put("mode", 0);
                    // JSON 데이터 파싱
                    JSONObject serverResponseJSONObject = new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_ACCOUNT_SYNC, urlParameter));
                    String serverResponse = serverResponseJSONObject.getString("result");
                    // 데이터 확인
                    if (serverResponse.equals("success")) {
                        isSuccess = 1;
                    }else {
                        isSuccess = 2;
                    }
                } catch (Exception ex) {
                    // 오류 메시지 출력
                    ex.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                if (isSuccess == 1) {
                    // 계정 데이터 가져오기
                    getAccountInfo(authToken);

                    return;
                }else if (isSuccess == 0) {
                    showSnackBar(rhyaErrorCodeManager.getErrorMessage(300));
                }

                // 계정 동기화 실패
                accountSyncLayout.setVisibility(View.GONE);
                accountSyncSuccessLayout.setVisibility(View.GONE);
                accountSyncFailLayout.setVisibility(View.VISIBLE);
            }
        }.execute(null);
    }



    public void accountSyncRequestClient(String authToken, int type, String value) {
        // 서버 접속
        new RhyaAsyncTask<String, String>() {
            private int isSuccess = -1;

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(String arg) {
                try {
                    try {
                        // RHYA.Network 에 접속하여 Auth Token 확인
                        RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
                        ContentValues urlParameter = new ContentValues();
                        // 파라미터 설정
                        urlParameter.put("authToken", authToken);
                        urlParameter.put("mode", 1);
                        if (type == 1) urlParameter.put("authorizationKey", value);
                        // JSON 데이터 파싱
                        JSONObject serverResponseJSONObject = new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_ACCOUNT_SYNC, urlParameter));
                        String serverResponse = serverResponseJSONObject.getString("result");

                        // 데이터 확인
                        isSuccess = 1;
                        return serverResponse;
                    }catch (JSONException ex) {
                        ex.printStackTrace();
                        isSuccess = 2;
                    }
                } catch (Exception ex) {
                    // 오류 메시지 출력
                    ex.printStackTrace();
                    isSuccess = 3;
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                rhyaDialogManager_Type0.dismissDialog();

                if (isSuccess == 1) {
                    // 결과 확인
                    if (result != null) {
                        switch (result) {
                            default:
                                showSnackBar(rhyaErrorCodeManager.getErrorMessage(999));
                                break;

                            case "NO_AUTH_TOKEN":
                                showSnackBar("Auth Token 값이 입력되지 않았습니다.");
                                break;

                            case "NO_SYNC_KEY_FOR_DB":
                                showSnackBar("계정 연동 코드가 데이터베이스에서 설정되지 않았습니다.");
                                break;

                            case "MATCH_SYNC_KEY":
                                // 계정 데이터 가져오기
                                getAccountInfo(authToken);

                                break;

                            case "NOT_MATCH_SYNC_KEY":
                                showSnackBar("계정 연동 코드가 일치하지 않습니다.");
                                break;

                            case "EMAIL_ALREADY_SEND":
                                showSnackBar("이미 1번 이상 계정 연동 요청을 보냈습니다.");
                                break;

                            case "UPDATE_AND_SEND_EMAIL_SUCCESS":
                            case "INSERT_AND_SEND_EMAIL_SUCCESS":
                                showSnackBar("계정 연동 요청을 성공적으로 보냈습니다.");
                                break;

                            case "INSERT_AND_SEND_EMAIL_FAIL":
                                showSnackBar("계정 연동 요청을 보내는 도중 오류가 발생하였습니다.");
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
        // 데이터 가져오기
        rhyaDialogManager_Type0.showDialog();
        // 서버 접속
        new RhyaAsyncTask<String, String>() {
            // 학교 이름
            private String schoolName;

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected String doInBackground(String arg) {
                try {
                    try {
                        // RHYA.Network 에 접속하여 Auth Token 확인
                        RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
                        ContentValues urlParameter = new ContentValues();
                        // 파라미터 설정
                        urlParameter.put("authToken", authToken);
                        urlParameter.put("mode", 2);
                        // JSON 데이터 파싱
                        JSONObject serverResponseJSONObject = new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_ACCOUNT_SYNC, urlParameter));
                        String serverResponse = serverResponseJSONObject.getString("result");

                        // 데이터 확인
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

                            // 파라미터 설정
                            urlParameter.remove("authToken");
                            urlParameter.put("mode", 3);
                            urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                            // JSON 데이터 파싱
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

                    // 오류 메시지 출력
                    ex.printStackTrace();

                    return "Exception";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                rhyaDialogManager_Type0.dismissDialog();

                // 결과 비교
                switch (result) {
                    case "Exception": {
                        // 계정 동기화 실패
                        accountSyncLayout.setVisibility(View.GONE);
                        accountSyncSuccessLayout.setVisibility(View.GONE);
                        accountSyncFailLayout.setVisibility(View.VISIBLE);

                        showSnackBar(rhyaErrorCodeManager.getErrorMessage(999));
                        break;
                    }

                    case "JSONException": {
                        // 계정 동기화 실패
                        accountSyncLayout.setVisibility(View.GONE);
                        accountSyncSuccessLayout.setVisibility(View.GONE);
                        accountSyncFailLayout.setVisibility(View.VISIBLE);

                        showSnackBar(rhyaErrorCodeManager.getErrorMessage(101));
                        break;
                    }

                    case "Fail": {
                        // 계정 동기화 실패
                        accountSyncLayout.setVisibility(View.GONE);
                        accountSyncSuccessLayout.setVisibility(View.GONE);
                        accountSyncFailLayout.setVisibility(View.VISIBLE);

                        showSnackBar("연동된 계정을 가져오는 도중 오류가 발생하였습니다.");
                        break;
                    }

                    case "Success": {
                        // UI 데이터 설정
                        // =============================================================================== //
                        // =============================================================================== //
                        // 선생님 이미지
                        Glide.with(context)
                                .load(onlineAttendanceTeacherVO.getImageURL(authToken))
                                .placeholder(R.drawable.teacher_icon)
                                .error(R.drawable.teacher_icon)
                                .fallback(R.drawable.teacher_icon)
                                .signature(new ObjectKey(onlineAttendanceTeacherVO.getVersion()))
                                .into(teacherImageView);
                        // 선생님 이름
                        String teacherName = onlineAttendanceTeacherVO.getName();
                        String content = teacherName.concat("선생님");
                        SpannableString spannableString = new SpannableString(content);
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0074FF")), 0, teacherName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        teacherNameTextView.setText(spannableString);
                        // 학교 이름
                        if (schoolName == null) schoolName = "소속 학교 없음";
                        schoolNameTextView.setText(schoolName);
                        // =============================================================================== //
                        // =============================================================================== //

                        // 화면 변경
                        accountSyncLayout.setVisibility(View.GONE);
                        accountSyncSuccessLayout.setVisibility(View.VISIBLE);
                        accountSyncFailLayout.setVisibility(View.GONE);

                        // 출석부 데이터 로딩
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
        // 계정 동기화 확인
        new RhyaAsyncTask<String, String>() {
            // 데이터
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
                    // RHYA.Network 에 접속하여 Auth Token 확인
                    RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
                    ContentValues urlParameter = new ContentValues();
                    // 파라미터 설정
                    urlParameter.put("authToken", authToken);
                    urlParameter.put("mode", 0);
                    // JSON 데이터 파싱
                    JSONObject serverResponseJSONObjectForMode0 = new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                    String serverResponseForMode0 = serverResponseJSONObjectForMode0.getString("result");

                    // 데이터 확인
                    if (serverResponseForMode0.equals("success")) {
                        // Mode 0 실행 결과 설정
                        version = serverResponseJSONObjectForMode0.getString("version");
                        signKey = serverResponseJSONObjectForMode0.getString("app_sign_key");

                        // 업데이트 확인
                        if (!rhyaMainUtils.updateChecker(version)) {
                            // 업데이트 필요
                            return "plz_update";
                        }else {
                            // 루팅 확인
                            CellphoneRoutingCheck cellphoneRoutingCheck = new CellphoneRoutingCheck();
                            if (cellphoneRoutingCheck.checkSuperUser()) return "root_user";

                            // 무결성 검사
                            if (integrityCheck(signKey)) {
                                // 데이터 파일 검사
                                File teacherInfoFile = new File(rhyaDataManager.SAVE_ROOT_PATH, rhyaDataManager.TEACHER_INFO_FILENAME);
                                File studentInfoFile = new File(rhyaDataManager.SAVE_ROOT_PATH, rhyaDataManager.STUDENT_INFO_FILENAME);
                                HashMap<String, OnlineAttendanceTeacherVO> onlineAttendanceTeacherVOHashMap;
                                HashMap<String, OnlineAttendanceStudentVO> onlineAttendanceStudentVOHashMap;
                                String teacherInfoFileValue;
                                String studentInfoFileValue;


                                // =====================================================================================================================
                                // =====================================================================================================================
                                if (!teacherInfoFile.exists()) {
                                    // 선생님 정보 파일 다운로드
                                    urlParameter.put("mode", 1);
                                    urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                    JSONObject serverResponseJSONObject =
                                            new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                    String serverResponse = serverResponseJSONObject.getString("result");
                                    // 결과 확인
                                    if (serverResponse.equals("success")) {
                                        teacherInfoFileValue = serverResponseJSONObject.toString();
                                        // 파일 생성
                                        rhyaDataManager.writeFile(
                                                teacherInfoFile,
                                                rhyaAESManager.aesEncode(teacherInfoFileValue)
                                        );
                                        // JSON 파싱
                                        onlineAttendanceTeacherVOHashMap = rhyaDataManager.readJSONToTeacherVO(teacherInfoFileValue, true);
                                    }else {
                                        return "mode_1_fail";
                                    }
                                }else {
                                    // 파일 읽기 전용 예외 처리
                                    try {
                                        // 파일 읽기
                                        teacherInfoFileValue = rhyaDataManager.readFile(teacherInfoFile);
                                        teacherInfoFileValue = rhyaAESManager.aesDecode(teacherInfoFileValue);
                                        // JSON 파싱
                                        onlineAttendanceTeacherVOHashMap = rhyaDataManager.readJSONToTeacherVO(teacherInfoFileValue, true);
                                        // School 아이디 확인
                                        for (String uuid : onlineAttendanceTeacherVOHashMap.keySet()) {
                                            if (Objects.requireNonNull(onlineAttendanceTeacherVOHashMap.get(uuid)).getSchool_id() != onlineAttendanceTeacherVO.getSchool_id()) {
                                                // 파일 제거
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
                                        // 데이터 동기화
                                        urlParameter.put("mode", 5);
                                        urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                        JSONObject serverResponseJSONObject =
                                                new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                        String serverResponse = serverResponseJSONObject.getString("result");
                                        // 결과 확인
                                        if (serverResponse.equals("success")) {
                                            // JSON Object, Array
                                            JSONArray teacherJSONRootArray = serverResponseJSONObject.getJSONArray("teacher");
                                            // JSON Array for
                                            for (int jsonArrayIndex = 0; jsonArrayIndex < teacherJSONRootArray.length(); jsonArrayIndex++) {
                                                // 선생님 정보 추출
                                                JSONObject teacherInfo = teacherJSONRootArray.getJSONObject(jsonArrayIndex);
                                                String uuid = teacherInfo.getString("uuid");
                                                int version = teacherInfo.getInt("version");
                                                // 버전 확인
                                                OnlineAttendanceTeacherVO tempOnlineAttendanceTeacherVO = onlineAttendanceTeacherVOHashMap.get(uuid);
                                                if (tempOnlineAttendanceTeacherVO == null) {
                                                    // 선생님 정보 다시 로딩
                                                    urlParameter.put("mode", 6);
                                                    urlParameter.put("uuid", uuid);
                                                    JSONObject teacherInfoServerResponseJSONObject =
                                                            new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                                    String teacherInfoServerResponse = teacherInfoServerResponseJSONObject.getString("result");
                                                    // 결과 확인
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
                                                                null, // 민감 정보는 제외
                                                                null, // 민감 정보는 제외
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
                                                        // 선생님 정보 다시 로딩
                                                        urlParameter.put("mode", 6);
                                                        urlParameter.put("uuid", uuid);
                                                        JSONObject teacherInfoServerResponseJSONObject =
                                                                new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                                        String teacherInfoServerResponse = teacherInfoServerResponseJSONObject.getString("result");
                                                        // 결과 확인
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

                                            // 수정된 데이터를 파일에 덮어씌우기
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
                                        // 파일을 읽는 도중 오류 발생
                                        // 파일 다시 다운로드

                                        // 파일 제거
                                        //noinspection ResultOfMethodCallIgnored
                                        teacherInfoFile.delete();
                                        // 선생님 정보 파일 다운로드
                                        urlParameter.put("mode", 1);
                                        urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                        JSONObject serverResponseJSONObject =
                                                new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                        String serverResponse = serverResponseJSONObject.getString("result");
                                        // 결과 확인
                                        if (serverResponse.equals("success")) {
                                            teacherInfoFileValue = serverResponseJSONObject.toString();
                                            // 파일 생성
                                            rhyaDataManager.writeFile(
                                                    teacherInfoFile,
                                                    rhyaAESManager.aesEncode(teacherInfoFileValue)
                                            );
                                            // JSON 파싱
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
                                    // 학생 정보 파일 다운로드
                                    urlParameter.put("mode", 3);
                                    urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                    JSONObject serverResponseJSONObject =
                                            new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                    String serverResponseForMode = serverResponseJSONObject.getString("result");
                                    // 결과 확인
                                    if (serverResponseForMode.equals("success")) {
                                        studentInfoFileValue = serverResponseJSONObject.toString();
                                        // 파일 생성
                                        rhyaDataManager.writeFile(
                                                studentInfoFile,
                                                rhyaAESManager.aesEncode(studentInfoFileValue)
                                        );
                                        // JSON 파싱
                                        onlineAttendanceStudentVOHashMap = rhyaDataManager.readJSONToStudentVO(studentInfoFileValue, onlineAttendanceTeacherVO.getSchool_id(),true);
                                    }else {
                                        return "mode_3_fail";
                                    }
                                }else {
                                    // 파일 읽기 전용 예외 처리
                                    try {
                                        // 파일 읽기
                                        studentInfoFileValue = rhyaDataManager.readFile(studentInfoFile);
                                        studentInfoFileValue = rhyaAESManager.aesDecode(studentInfoFileValue);
                                        // JSON 파싱
                                        onlineAttendanceStudentVOHashMap = rhyaDataManager.readJSONToStudentVO(studentInfoFileValue, onlineAttendanceTeacherVO.getSchool_id(), true);
                                        // School 아이디 확인
                                        for (String uuid : onlineAttendanceStudentVOHashMap.keySet()) {
                                            if (Objects.requireNonNull(onlineAttendanceStudentVOHashMap.get(uuid)).getSchool_id() != onlineAttendanceTeacherVO.getSchool_id()) {
                                                // 파일 제거
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
                                        // 데이터 동기화
                                        urlParameter.put("mode", 4);
                                        urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                        JSONObject serverResponseJSONObject =
                                                new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                        String serverResponse = serverResponseJSONObject.getString("result");
                                        // 결과 확인
                                        if (serverResponse.equals("success")) {
                                            // JSON Object, Array
                                            JSONArray studentJSONRootArray = serverResponseJSONObject.getJSONArray("student");
                                            // JSON Array for
                                            for (int jsonArrayIndex = 0; jsonArrayIndex < studentJSONRootArray.length(); jsonArrayIndex++) {
                                                // 학생 정보 추출
                                                JSONObject studentInfo = studentJSONRootArray.getJSONObject(jsonArrayIndex);
                                                String uuid = studentInfo.getString("student_uuid");
                                                int version = studentInfo.getInt("version");
                                                // 버전 확인
                                                OnlineAttendanceStudentVO tempOnlineAttendanceStudentVO = onlineAttendanceStudentVOHashMap.get(uuid);
                                                if (tempOnlineAttendanceStudentVO == null) {
                                                    // 학생 정보 다시 로딩
                                                    urlParameter.put("mode", 7);
                                                    urlParameter.put("uuid", uuid);
                                                    JSONObject studentInfoServerResponseJSONObject =
                                                            new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                                    String studentInfoServerResponse = studentInfoServerResponseJSONObject.getString("result");
                                                    // 결과 확인
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
                                                        // 선생님 정보 다시 로딩
                                                        urlParameter.put("mode", 7);
                                                        urlParameter.put("uuid", uuid);
                                                        JSONObject studentInfoServerResponseJSONObject =
                                                                new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                                        String studentInfoServerResponse = studentInfoServerResponseJSONObject.getString("result");
                                                        // 결과 확인
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

                                            // 수정된 데이터를 파일에 덮어씌우기
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
                                        // 파일을 읽는 도중 오류 발생
                                        // 파일 다시 다운로드

                                        // 파일 제거
                                        //noinspection ResultOfMethodCallIgnored
                                        studentInfoFile.delete();
                                        // 학생 정보 파일 다운로드
                                        urlParameter.put("mode", 3);
                                        urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                        JSONObject serverResponseJSONObject =
                                                new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                        String serverResponseForMode = serverResponseJSONObject.getString("result");
                                        // 결과 확인
                                        if (serverResponseForMode.equals("success")) {
                                            studentInfoFileValue = serverResponseJSONObject.toString();
                                            // 파일 생성
                                            rhyaDataManager.writeFile(
                                                    studentInfoFile,
                                                    rhyaAESManager.aesEncode(studentInfoFileValue)
                                            );
                                            // JSON 파싱
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
                                    // 데이터 동기화
                                    urlParameter.put("mode", 8);
                                    urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                    JSONObject serverResponseJSONObject =
                                            new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                    String serverResponse = serverResponseJSONObject.getString("result");
                                    // 결과 확인
                                    if (serverResponse.equals("success")) {
                                        // JSON Object, Array
                                        HashMap<String, OnlineAttendanceClassVO> onlineAttendanceClassVOHashMap = new HashMap<>();
                                        JSONArray studentJSONRootArray = serverResponseJSONObject.getJSONArray("class");
                                        // JSON Array for
                                        for (int jsonArrayIndex = 0; jsonArrayIndex < studentJSONRootArray.length(); jsonArrayIndex++) {
                                            // 선생님 정보 추출
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
                                    // 데이터 동기화
                                    urlParameter.put("mode", 9);
                                    urlParameter.put("schoolid", onlineAttendanceTeacherVO.getSchool_id());
                                    JSONObject serverResponseJSONObject =
                                            new JSONObject(rhyaHttpsConnection.request(rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER, urlParameter));
                                    String serverResponse = serverResponseJSONObject.getString("result");
                                    // 결과 확인
                                    if (serverResponse.equals("success")) {
                                        // JSON Object, Array
                                        HashMap<String, OnlineAttendanceDepartmentVO> onlineAttendanceDepartmentVOHashMap = new HashMap<>();
                                        JSONArray studentJSONRootArray = serverResponseJSONObject.getJSONArray("department");
                                        // JSON Array for
                                        for (int jsonArrayIndex = 0; jsonArrayIndex < studentJSONRootArray.length(); jsonArrayIndex++) {
                                            // 선생님 정보 추출
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


                                // 1.5초 대기
                                Thread.sleep(1500);

                                // 데이터 설정
                                RhyaApplication.setOnlineAttendanceTeacherVOHashMap(onlineAttendanceTeacherVOHashMap);
                                RhyaApplication.setOnlineAttendanceStudentVOHashMap(onlineAttendanceStudentVOHashMap);
                            }else {
                                // 앱 변조 감지
                                return "integrity_fail";
                            }
                        }
                    }else {
                        return "mode_0_fail";
                    }
                } catch (Exception ex) {
                    // 오류 메시지 출력
                    ex.printStackTrace();
                    exceptionStr = ex.getMessage();

                    return "exception";
                }

                return "no_result";
            }

            @Override
            protected void onPostExecute(String result) {
                // 리스너 설정
                final RhyaDialogManager.DialogListener_Type3 dialogListener_type3_for_exit = () -> finishAndRemoveTask();
                final RhyaDialogManager.DialogListener_Type3 dialogListener_type3_for_update = () -> {
                    // 앱 업데이트
                    rhyaDialogManager_Type3.dismissDialog();
                    updateApp();
                };


                // 결과 비교
                switch (result) {
                    case "no_result": { // 성공
                        // 화면 전환
                        goNextActivity(authToken);

                        break;
                    }


                    case "plz_update": { // 업데이트 필요
                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                "업데이트 필요",
                                "현재 이용하고 계신 클라이언트의 버전이 오래되었습니다. 최신 클라이언트로의 업데이트가 필요합니다.",
                                "업데이트",
                                dialogListener_type3_for_update
                        );
                        rhyaDialogManager_Type3.showDialog();

                        break;
                    }


                    case "root_user": { // 루팅 기기 감지
                        StringBuilder stringBuilder;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("현재 이용하고 계신 기기는 루팅 된 기기입니다. 해당 기기로는 온라인 출석부 서비스를 이용하실 수 없습니다. 서비스를 이용하기 위해서는 루팅을 해제해 주세요.");
                        stringBuilder.append(System.lineSeparator());
                        stringBuilder.append(System.lineSeparator());
                        stringBuilder.append("만약 루팅이 되어있지 않음에도 이러한 오류가 지속해서 발생하는 경우 관리자에게 문의하여주십시오.");
                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                "비정상적인 접근",
                                stringBuilder.toString(),
                                "종료",
                                dialogListener_type3_for_exit
                        );
                        rhyaDialogManager_Type3.showDialog();

                        break;
                    }


                    // ========================================================================================================
                    case "mode_0_fail": // 앱 버전, 앱 서명 SHA 지문 출력 실패
                    case "mode_1_fail": // 입력된 학교에 소속되어있는 모든 선생님 정보 출력 실패
                    case "mode_3_fail": // 입력된 학교에 소속되어있는 모든 학생 정보 출력 실패
                    case "mode_4_fail": // 입력된 학교에 소속되어있는 모든 학생 정보 출력 [ Version 정보와 UUID만 출력 ] 실패
                    case "mode_5_fail": // 입력된 학교에 소속되어있는 모든 선생님 정보 출력 [ Version 정보와 UUID만 출력 ] 실패
                    case "mode_8_fail": // 입력된 학교에 존재하는 모든 반 정보 출력 실패
                        {
                            StringBuilder stringBuilder;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(rhyaErrorCodeManager.getErrorMessage(101));
                            stringBuilder.append("만약 이러한 오류가 지속해서 발생하는 경우 관리자에게 문의하여주십시오.");
                            stringBuilder.append(System.lineSeparator());
                            stringBuilder.append(System.lineSeparator());
                            stringBuilder.append("ERROR MESSAGE: ");
                            stringBuilder.append(result);

                            rhyaDialogManager_Type3.setDialogSettingType_3(
                                    "서버 접근 오류",
                                    stringBuilder.toString(),
                                    "종료",
                                    dialogListener_type3_for_exit
                            );
                            rhyaDialogManager_Type3.showDialog();

                            break;
                        }
                    // ========================================================================================================


                    case "school_id_error": {   // 학교 아이디가 일치하지 않음
                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                "접근 불가 데이터 감지",
                                "현재 접근 하시려는 데이터에 해당 계정으로 접근할 수 없는 데이터가 감지되었습니다. 해당 계정으로는 같은 학교의 데이터만 접근할 수 있습니다. 앱을 종료후 다시 실행시켜주세요.",
                                "종료",
                                dialogListener_type3_for_exit
                        );
                        rhyaDialogManager_Type3.showDialog();

                        break;
                    }


                    case "integrity_fail": {   // 무결성 검사 오류
                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                "앱 변조 감지",
                                "앱의 위변조가 감지되었습니다. 온라인 출석부 앱을 정상적인 경로를 통해 받은 것인지 확인해주십시오. 다른 곳에서 해당 기기를 감염시키기 위해 온 바이러스일 수도 있습니다. 만약 앱을 정상적인 경로에서 내려받았고 아무런 문제가 발생하지 않는 경우 관리자에게 문의하여주십시오.",
                                "종료",
                                dialogListener_type3_for_exit
                        );
                        rhyaDialogManager_Type3.showDialog();

                        break;
                    }


                    case "exception": {   // 알 수 없는 오류
                        StringBuilder stringBuilder;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("알 수 없는 오류가 발생하였습니다. 만약 이러한 오류가 지속해서 발생하는 경우 관리자에게 문의하여주십시오.");
                        stringBuilder.append(System.lineSeparator());
                        stringBuilder.append(System.lineSeparator());
                        stringBuilder.append(exceptionStr);
                        rhyaDialogManager_Type3.setDialogSettingType_3(
                                "알 수 없는 오류",
                                stringBuilder.toString(),
                                "종료",
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
                            Toast.makeText(context, "다운로드 중 알 수 없는 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                            if (!activity.isFinishing())
                                runOnUiThread(() -> finishAndRemoveTask());

                            break;

                        case DownloadManager.STATUS_SUCCESSFUL:
                            Toast.makeText(context, "다운로드를 성공했습니다.", Toast.LENGTH_SHORT).show();

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

                                Toast.makeText(context, "앱 설치 중 오류 발생", Toast.LENGTH_SHORT).show();

                                if (!activity.isFinishing())
                                    runOnUiThread(() -> finishAndRemoveTask());
                            }

                            break;

                        case DownloadManager.STATUS_PAUSED:
                            Toast.makeText(getApplicationContext(), "다운로드가 취소되었습니다. ERROR:" + reason, Toast.LENGTH_SHORT).show();
                            if (!activity.isFinishing())
                                runOnUiThread(() -> finishAndRemoveTask());

                            break;

                        case DownloadManager.STATUS_FAILED:
                            Toast.makeText(getApplicationContext(), "다운로드 중 오류가 발생하였습니다. ERROR:" + reason, Toast.LENGTH_SHORT).show();
                            if (!activity.isFinishing())
                                runOnUiThread(() -> finishAndRemoveTask());

                            break;
                    }
                }
            }catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(context, "앱 업데이트 중 오류 발생", Toast.LENGTH_SHORT).show();

                if (!activity.isFinishing())
                    runOnUiThread(() -> finishAndRemoveTask());
            }
        }
    };
    // =============================================================================
    // 업데이트 작업 처리
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
            request.setTitle("Online Attendance 업데이트");
            request.setDescription("다운로드 중...");
            request.setNotificationVisibility(0);
            request.setVisibleInDownloadsUi(true);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            request.setMimeType("application/vnd.android.package-archive");
            request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "oa_update_apk.apk");
            enqueue = downloadManager.enqueue(request);
        }catch (Exception ex) {
            ex.printStackTrace();

            Toast.makeText(context, "다운로드 중 알 수 없는 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();

            if (!activity.isFinishing())
                runOnUiThread(this::finishAndRemoveTask);
        }
    }
    // =============================================================================
    // =============================================================================



    private void showSnackBar(String message) {
        RhyaSnackBarStyle rhyaSnackBarStyle = new RhyaSnackBarStyle();
        Snackbar snackbar = rhyaSnackBarStyle.getSnackBar(mainLayout, message, context);
        snackbar.setAction("닫기", v -> snackbar.dismiss());
        snackbar.show();
    }
}
