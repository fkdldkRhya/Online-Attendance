package kro.kr.rhya_network.online_attendance.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

import kro.kr.rhya_network.online_attendance.R;
import kro.kr.rhya_network.online_attendance.core.RhyaAuthManager;
import kro.kr.rhya_network.online_attendance.utils.RhyaAsyncTask;
import kro.kr.rhya_network.online_attendance.utils.RhyaErrorCodeManager;
import kro.kr.rhya_network.online_attendance.utils.RhyaSnackBarStyle;

public class ActivitySplash extends AppCompatActivity {
    // Activity info
    private Context context;
    // UI Object
    private ConstraintLayout mainLayout;
    private WebView webView;
    private ProgressWheel progressWheel;
    // 오류 메시지 관리자
    private RhyaErrorCodeManager rhyaErrorCodeManager;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash);
        super.onCreate(savedInstanceState);

        // 변수 초기화
        context = this;
        rhyaErrorCodeManager = new RhyaErrorCodeManager();

        // UI 초기화
        mainLayout = findViewById(R.id.mainLayout);
        webView = findViewById(R.id.webView);
        progressWheel = findViewById(R.id.progressWheel);

        permissionChecker();
    }



    private void permissionChecker() {
        TedPermission.create()
                .setGotoSettingButton(false)
                .setPermissionListener(permissionlistener)
                .setPermissions(Manifest.permission.CALL_PHONE)
                .check();
    }
    private final PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            ImageView appLogoImageView = findViewById(R.id.appLogoImageView);

            // 애니메이션 실행
            appLogoImageView.animate()
                    .setStartDelay(500)
                    .alpha(1f)
                    .translationY(-50f)
                    .setDuration(1000)
                    .withEndAction(() -> {
                        // 로그인 확인
                        new RhyaAsyncTask<String, String>() {
                            // RHYA Auth Token 관리자 선언
                            private RhyaAuthManager rhyaAuthManager;
                            // 오류 메시지 임시 저장
                            private int exceptionMessage = 0;


                            @Override
                            protected void onPreExecute() {
                                // 변수 초기화
                                rhyaAuthManager = new RhyaAuthManager(context);
                            }

                            @Override
                            protected String doInBackground(String arg) {
                                try {
                                    // 1초 대기
                                    Thread.sleep(1000);

                                    // Auth Token 확인
                                    if (rhyaAuthManager.checkAuthToken(null)) {
                                        return "success";
                                    }
                                } catch (Exception e) {
                                    // 오류 메시지 출력
                                    e.printStackTrace();

                                    exceptionMessage = 1;
                                }

                                return null;
                            }

                            @Override
                            protected void onPostExecute(String result) {
                                if (result != null) {
                                    // 로그인 성공 - 화면 전환
                                    loginSuccessTask();
                                }else {
                                    if (exceptionMessage == 1) {
                                        showSnackBar(rhyaErrorCodeManager.getErrorMessage(999));
                                    }

                                    // 로그인 WebView 설정
                                    webView = rhyaAuthManager.setWebViewSetting(
                                            webView,
                                            progressWheel,
                                            new RhyaAuthManager.LoginListener() {
                                                @Override
                                                public void isLoginSuccess(String user, String token) {
                                                    new RhyaAsyncTask<String, String>() {
                                                        @Override
                                                        protected void onPreExecute() {
                                                        }

                                                        @Override
                                                        protected String doInBackground(String arg) {
                                                            try {
                                                                // 로그인 성공
                                                                String authToken = rhyaAuthManager.getNewAuthToken(user, token);
                                                                if (authToken != null) {
                                                                    // Auth token 등록
                                                                    rhyaAuthManager.setSharedPreferencesAuthToken(authToken);

                                                                    return "success";
                                                                }
                                                            } catch (Exception ex) {
                                                                // 오류 메시지 출력
                                                                ex.printStackTrace();
                                                            }

                                                            return null;
                                                        }

                                                        @Override
                                                        protected void onPostExecute(String result) {
                                                            if (result != null) {
                                                                // 로그인 성공 - 화면 전환
                                                                loginSuccessTask();
                                                            }else {
                                                                // 오류 메시지 출력
                                                                showSnackBar(rhyaErrorCodeManager.getErrorMessage(201));
                                                                // WebView 로딩
                                                                rhyaAuthManager.reloadLoginPage(webView);
                                                            }
                                                        }
                                                    }.execute(null);
                                                }

                                                @Override
                                                public void isLoginFail() {
                                                    // 오류 메시지 출력
                                                    showSnackBar(rhyaErrorCodeManager.getErrorMessage(201));
                                                    // WebView 로딩
                                                    rhyaAuthManager.reloadLoginPage(webView);
                                                }
                                            });
                                    // 로고 아이콘 숨기기
                                    appLogoImageView.setVisibility(View.GONE);
                                }
                            }
                        }.execute(null);
                    })
                    .start();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(context, "권한 허용을 하지 않으면 서비스를 이용할 수 없습니다.", Toast.LENGTH_SHORT).show();

            finish();
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
    }



    @Override
    public void onBackPressed() {
        finish();
    }



    private void loginSuccessTask() {
        if (!this.isFinishing()) {
            webView.setVisibility(View.INVISIBLE);
            progressWheel.setVisibility(View.INVISIBLE);

            // 화면 전환
            Intent intent = new Intent(this, ActivityAccountSync.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
    }



    private void showSnackBar(String message) {
        RhyaSnackBarStyle rhyaSnackBarStyle = new RhyaSnackBarStyle();
        Snackbar snackbar = rhyaSnackBarStyle.getSnackBar(mainLayout, message, context);
        snackbar.setAction("닫기", v -> snackbar.dismiss());
        snackbar.show();
    }
}
