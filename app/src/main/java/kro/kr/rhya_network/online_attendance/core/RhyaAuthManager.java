package kro.kr.rhya_network.online_attendance.core;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import kro.kr.rhya_network.online_attendance.utils.RhyaHttpsConnection;


/**
 * RHYA.Network Login Auth.V1 API
 *
 * RHYA.Network License :
 * MIT License
 * Copyright (c) 2022 CHOI SI-HUN
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 * Copyright (c) 2022 RHYA.Network. All rights reserved.
 * @author RHYA.Network Service
 */
public class RhyaAuthManager {
    // Context
    private final Context context;
    /**
     * Shared preferences 관련 변수
     */
    private final String SHARED_PREFERENCES_APP_NAME = "RHYA.Network"; // 저장 이름
    private final String SHARED_PREFERENCES_AUTH_TOKEN_KEY = "AUTH_V1__AUTH_TOKEN"; // Auth Token 을 저장할 키 이름
    private final String SHARED_PREFERENCES_DEFAULT_RETURN = "#NULL#"; // Shared preferences 기본 반환 값
    /**
     * RHYA.Network Web Address
     */
    public final String RHYA_NETWORK_CALL_BACK_URL = "https://rhya-network.kro.kr/RhyaNetwork/webpage/jsp/auth.v1/callback.jsp";
    public final String RHYA_NETWORK_LOGIN_URL = "https://rhya-network.kro.kr/RhyaNetwork/webpage/jsp/auth.v1/sign_in.jsp?rpid=10&ctoken=1";
    public final String AUTH_TOKEN_GET_URL = "https://rhya-network.kro.kr/RhyaNetwork/webpage/jsp/auth.v1/auth_token.jsp";
    public final String AUTH_TOKEN_CHECK_URL = "https://rhya-network.kro.kr/RhyaNetwork/webpage/jsp/auth.v1/auth_token_checker.jsp";
    public final String ONLINE_ATTENDANCE_ACCOUNT_SYNC = "https://rhya-network.kro.kr/RhyaNetwork/webpage/jsp/service/online_attendance_account_sync.jsp";
    public final String ONLINE_ATTENDANCE_MANAGER = "https://rhya-network.kro.kr/RhyaNetwork/online_attendance_manager";
    /**
     * RHYA.Network Web Parameter Data
     */
    public final String AUTH_TOKEN_CHECK_PARM_NAME = "kro_kr_rhya__network_online__attendance";

    /**
     * RHYA.Network Web 사용자 로그인 성공, 실패 작업 리스너
     */
    public interface LoginListener {
        void isLoginSuccess(String user, String token); // 로그인 성공
        void isLoginFail(); // 로그인 실패
    }





    /**
     * RhyaAuthManager 생성자
     *
     * - 변수 초기화
     */
    public RhyaAuthManager(Context context) {
        // 변수 초기화
        this.context = context;
    }



    /**
     * checkAuthToken : Auth Token 확인 함수
     *
     * @param authToken 사용자의 Auth Token
     * @return Auth Token 사용 가능 여부
     */
    public boolean checkAuthToken(String authToken) throws JSONException {
        String returnValue;

        if (authToken == null) {
            returnValue = getSharedPreferencesAuthToken();

            // 기본 반환 값과 비교
            if (returnValue.equals(SHARED_PREFERENCES_DEFAULT_RETURN)) {
                // Auth Token 이 생성돼있지 않음
                return false;
            }
        }else {
            returnValue = authToken;
        }

        // RHYA.Network 에 접속하여 해당 Auth Token 이 사용 가능한지 확인
        RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
        ContentValues urlParameter = new ContentValues();
        // 파라미터 설정
        urlParameter.put("token", returnValue);
        urlParameter.put("name", AUTH_TOKEN_CHECK_PARM_NAME);
        // JSON 데이터 파싱
        JSONObject serverResponseJSONObject = new JSONObject(rhyaHttpsConnection.request(AUTH_TOKEN_CHECK_URL, urlParameter));
        String serverResponse = serverResponseJSONObject.getString("result");


        return serverResponse.equals("success");
    }



    /**
     * getAuthToken : Shared preferences 에 저장된 Auth Token 구하는 함수
     *
     * @return Shared preferences 에 저장된 Auth Token
     */
    public String getSharedPreferencesAuthToken() {
        SharedPreferences prefs = context.getSharedPreferences(
                SHARED_PREFERENCES_APP_NAME,
                Context.MODE_PRIVATE);

        return prefs.getString(
                SHARED_PREFERENCES_AUTH_TOKEN_KEY,
                SHARED_PREFERENCES_DEFAULT_RETURN);
    }



    /**
     * getAuthToken : Shared preferences 에 저장된 Auth Token 구하는 함수
     *
     * @param authToken 사용자의 Auth Token
     */
    public void setSharedPreferencesAuthToken(String authToken) {
        SharedPreferences prefs = context.getSharedPreferences(
                SHARED_PREFERENCES_APP_NAME,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SHARED_PREFERENCES_AUTH_TOKEN_KEY, authToken);
        editor.apply();
    }


    /**
     * setWebViewSetting : WebView 기본 설정
     *
     * @param webView WebView [Layout]
     * @param progressWheel ProgressWheel [Layout]
     * @param loginListener 로그인 성공 및 실패 처리 리스너
     * @return WebView
     */
    @SuppressLint("SetJavaScriptEnabled")
    public WebView setWebViewSetting(WebView webView,
                                     ProgressWheel progressWheel,
                                     LoginListener loginListener) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookies(null);
        cookieManager.removeAllCookies(null);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setDomStorageEnabled(true);
        webSettings.setTextZoom(100);
        webView.setWebViewClient(
                new RhyaLoginWebViewClient(
                        progressWheel,
                        loginListener));

        webView.loadUrl(RHYA_NETWORK_LOGIN_URL);

        return webView;
    }



    /**
     * reloadLoginPage :RHYA.Network 로그인 페이지 다시 로딩
     *
     * @param webView WebView
     */
    public void reloadLoginPage(WebView webView) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookies(null);
        cookieManager.removeAllCookies(null);
        webView.stopLoading();
        webView.loadUrl(RHYA_NETWORK_LOGIN_URL);
    }



    /**
     * getNewAuthToken : Auth Token 생성 함수
     *
     * @param user 시용지 UUID
     * @param token 사용자 자동 로그인 Token
     * @return Auth Token
     * @throws JSONException JSON 파싱 오류
     */
    public String getNewAuthToken(String user, String token) throws JSONException {
        // RHYA.Network 에 접속하여 Auth Token 발급
        RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
        ContentValues urlParameter = new ContentValues();
        // 파라미터 설정
        urlParameter.put("user", user);
        urlParameter.put("token", token);
        urlParameter.put("name", AUTH_TOKEN_CHECK_PARM_NAME);
        // JSON 데이터 파싱
        JSONObject serverResponseJSONObject = new JSONObject(rhyaHttpsConnection.request(AUTH_TOKEN_GET_URL, urlParameter));
        String serverResponse = serverResponseJSONObject.getString("result");
        // 데이터 확인
        if (serverResponse.equals("success"))
            return serverResponseJSONObject.getString("message");

        return null;
    }



    /**
     * isAccessURL : RHYA.Network의 도메인 인지 확인하는 함수
     *
     * @param url 검사할 URL
     * @return 검사 결과
     */
    public boolean isAccessURL(String url) throws URISyntaxException {
        // RHYA.Network 도메인
        final String domain = "rhya-network.kro.kr";

        URI uri = new URI(url);
        String hostname = uri.getHost();
        // Null 확인
        if (hostname != null)
            return (hostname.startsWith("www.") ? hostname.substring(4) : hostname).equals(domain);

        return false;
    }




    /**
     * RhyaLoginWebViewClient : RHYA.Network 로그인 WebView Client
     */
    private class RhyaLoginWebViewClient extends WebViewClient {
        // 로그인 UI Object
        private final ProgressWheel progressWheel;
        // 로그인 성공, 실패 작업 리스너
        public LoginListener loginListener;



        /**
         * 생성자
         */
        public RhyaLoginWebViewClient(ProgressWheel progressWheel,
                                      LoginListener loginListener) {
            // 변수 초기화
            this.progressWheel = progressWheel;
            this.loginListener = loginListener;
        }



        /**
         * onPageStarted : 페이지 로딩 감지 함수
         *
         * @param view WebView [Override]
         * @param url URL [Override]
         * @param favicon Favicon [Override]
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            // UI 설정
            view.setVisibility(View.INVISIBLE);
            progressWheel.setVisibility(View.VISIBLE);


            try {
                // URL 확인
                if (!isAccessURL(url)) {
                    view.stopLoading();
                    view.loadUrl(RHYA_NETWORK_LOGIN_URL);

                    return;
                }

                // Call back URL 확인
                if (url.equals(RHYA_NETWORK_CALL_BACK_URL)) {
                    // 로그인 성공 확인
                    CookieManager cookieManager = CookieManager.getInstance();

                    final String cookieResult = cookieManager.getCookie(url);
                    final String userUUID = findCookie(cookieResult, "AutoLogin_UserUUID");
                    final String tokenUUID = findCookie(cookieResult, "AutoLogin_TokenUUID");

                    // Cookie 데이터 확인
                    if (userUUID == null || tokenUUID == null) {
                        // 자동 로그인 등록 실패
                        loginListener.isLoginFail();
                    }else {
                        loginListener.isLoginSuccess(userUUID, tokenUUID);
                    }
                }
            } catch (Exception e) {
                // 오류 메시지 출력
                e.printStackTrace();

                reloadLoginPage(view);
            }
        }



        /**
         * findCookie : 특정 쿠키 데이터 출력
         *
         * @param result All cookie
         * @param key Cookie name
         * @return Cookie value
         */
        public String findCookie(String result, String key) {
            if (result == null) {
                return  null;
            }

            if (!result.contains(";")) {
                if (result.split("=")[0].trim().equals(key.trim())) {
                    return result.split("=")[1].trim();
                }else {
                    return null;
                }
            }

            String[] root_split = result.split(";");
            for (String root_node : root_split) {
                root_node = root_node.trim();
                if (root_node.split("=")[0].trim().equals(key.trim())) {
                    return root_node.split("=")[1].trim();
                }
            }

            return null;
        }



        // -----------------------------------------------------------------------------------------------------
        // ------------------============================ WebView ============================------------------
        // -----------------------------------------------------------------------------------------------------
        // 리소스를 로드하는 중 여러번 호출
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }
        // 방문 내역을 히스토리에 업데이트 할 때
        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
        }
        // 로딩이 완료됬을 때 한번 호출
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            // UI 설정
            view.setVisibility(View.VISIBLE);
            progressWheel.setVisibility(View.INVISIBLE);
        }
        // 오류가 났을 경우, 오류는 복수할 수 없음
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            switch (errorCode) {
                case ERROR_AUTHENTICATION:
                    break;               // 서버에서 사용자 인증 실패
                case ERROR_BAD_URL:
                    break;                           // 잘못된 URL
                case ERROR_CONNECT:
                    break;                          // 서버로 연결 실패
                case ERROR_FAILED_SSL_HANDSHAKE:
                    break;    // SSL handshake 수행 실패
                case ERROR_FILE:
                    break;                                  // 일반 파일 오류
                case ERROR_FILE_NOT_FOUND:
                    break;               // 파일을 찾을 수 없습니다
                case ERROR_HOST_LOOKUP:
                    break;           // 서버 또는 프록시 호스트 이름 조회 실패
                case ERROR_IO:
                    break;                              // 서버에서 읽거나 서버로 쓰기 실패
                case ERROR_PROXY_AUTHENTICATION:
                    break;   // 프록시에서 사용자 인증 실패
                case ERROR_REDIRECT_LOOP:
                    break;               // 너무 많은 리디렉션
                case ERROR_TIMEOUT:
                    break;                          // 연결 시간 초과
                case ERROR_TOO_MANY_REQUESTS:
                    break;     // 페이지 로드중 너무 많은 요청 발생
                case ERROR_UNKNOWN:
                    break;                        // 일반 오류
                case ERROR_UNSUPPORTED_AUTH_SCHEME:
                    break; // 지원되지 않는 인증 체계
                case ERROR_UNSUPPORTED_SCHEME:
                    break;          // URI가 지원되지 않는 방식
            }

        }
        // http 인증 요청이 있는 경우, 기본 동작은 요청 취소
        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
        // 확대나 크기 등의 변화가 있는 경우
        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
        }
        // 잘못된 키 입력이 있는 경우
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }
        // 새로운 URL이 webview에 로드되려 할 경우 컨트롤을 대신할 기회를 줌
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        // -----------------------------------------------------------------------------------------------------
    }
}
