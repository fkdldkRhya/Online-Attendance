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
     * Shared preferences ?????? ??????
     */
    private final String SHARED_PREFERENCES_APP_NAME = "RHYA.Network"; // ?????? ??????
    private final String SHARED_PREFERENCES_AUTH_TOKEN_KEY = "AUTH_V1__AUTH_TOKEN"; // Auth Token ??? ????????? ??? ??????
    private final String SHARED_PREFERENCES_DEFAULT_RETURN = "#NULL#"; // Shared preferences ?????? ?????? ???
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
     * RHYA.Network Web ????????? ????????? ??????, ?????? ?????? ?????????
     */
    public interface LoginListener {
        void isLoginSuccess(String user, String token); // ????????? ??????
        void isLoginFail(); // ????????? ??????
    }





    /**
     * RhyaAuthManager ?????????
     *
     * - ?????? ?????????
     */
    public RhyaAuthManager(Context context) {
        // ?????? ?????????
        this.context = context;
    }



    /**
     * checkAuthToken : Auth Token ?????? ??????
     *
     * @param authToken ???????????? Auth Token
     * @return Auth Token ?????? ?????? ??????
     */
    public boolean checkAuthToken(String authToken) throws JSONException {
        String returnValue;

        if (authToken == null) {
            returnValue = getSharedPreferencesAuthToken();

            // ?????? ?????? ?????? ??????
            if (returnValue.equals(SHARED_PREFERENCES_DEFAULT_RETURN)) {
                // Auth Token ??? ??????????????? ??????
                return false;
            }
        }else {
            returnValue = authToken;
        }

        // RHYA.Network ??? ???????????? ?????? Auth Token ??? ?????? ???????????? ??????
        RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
        ContentValues urlParameter = new ContentValues();
        // ???????????? ??????
        urlParameter.put("token", returnValue);
        urlParameter.put("name", AUTH_TOKEN_CHECK_PARM_NAME);
        // JSON ????????? ??????
        JSONObject serverResponseJSONObject = new JSONObject(rhyaHttpsConnection.request(AUTH_TOKEN_CHECK_URL, urlParameter));
        String serverResponse = serverResponseJSONObject.getString("result");


        return serverResponse.equals("success");
    }



    /**
     * getAuthToken : Shared preferences ??? ????????? Auth Token ????????? ??????
     *
     * @return Shared preferences ??? ????????? Auth Token
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
     * getAuthToken : Shared preferences ??? ????????? Auth Token ????????? ??????
     *
     * @param authToken ???????????? Auth Token
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
     * setWebViewSetting : WebView ?????? ??????
     *
     * @param webView WebView [Layout]
     * @param progressWheel ProgressWheel [Layout]
     * @param loginListener ????????? ?????? ??? ?????? ?????? ?????????
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
     * reloadLoginPage :RHYA.Network ????????? ????????? ?????? ??????
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
     * getNewAuthToken : Auth Token ?????? ??????
     *
     * @param user ????????? UUID
     * @param token ????????? ?????? ????????? Token
     * @return Auth Token
     * @throws JSONException JSON ?????? ??????
     */
    public String getNewAuthToken(String user, String token) throws JSONException {
        // RHYA.Network ??? ???????????? Auth Token ??????
        RhyaHttpsConnection rhyaHttpsConnection = new RhyaHttpsConnection();
        ContentValues urlParameter = new ContentValues();
        // ???????????? ??????
        urlParameter.put("user", user);
        urlParameter.put("token", token);
        urlParameter.put("name", AUTH_TOKEN_CHECK_PARM_NAME);
        // JSON ????????? ??????
        JSONObject serverResponseJSONObject = new JSONObject(rhyaHttpsConnection.request(AUTH_TOKEN_GET_URL, urlParameter));
        String serverResponse = serverResponseJSONObject.getString("result");
        // ????????? ??????
        if (serverResponse.equals("success"))
            return serverResponseJSONObject.getString("message");

        return null;
    }



    /**
     * isAccessURL : RHYA.Network??? ????????? ?????? ???????????? ??????
     *
     * @param url ????????? URL
     * @return ?????? ??????
     */
    public boolean isAccessURL(String url) throws URISyntaxException {
        // RHYA.Network ?????????
        final String domain = "rhya-network.kro.kr";

        URI uri = new URI(url);
        String hostname = uri.getHost();
        // Null ??????
        if (hostname != null)
            return (hostname.startsWith("www.") ? hostname.substring(4) : hostname).equals(domain);

        return false;
    }




    /**
     * RhyaLoginWebViewClient : RHYA.Network ????????? WebView Client
     */
    private class RhyaLoginWebViewClient extends WebViewClient {
        // ????????? UI Object
        private final ProgressWheel progressWheel;
        // ????????? ??????, ?????? ?????? ?????????
        public LoginListener loginListener;



        /**
         * ?????????
         */
        public RhyaLoginWebViewClient(ProgressWheel progressWheel,
                                      LoginListener loginListener) {
            // ?????? ?????????
            this.progressWheel = progressWheel;
            this.loginListener = loginListener;
        }



        /**
         * onPageStarted : ????????? ?????? ?????? ??????
         *
         * @param view WebView [Override]
         * @param url URL [Override]
         * @param favicon Favicon [Override]
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            // UI ??????
            view.setVisibility(View.INVISIBLE);
            progressWheel.setVisibility(View.VISIBLE);


            try {
                // URL ??????
                if (!isAccessURL(url)) {
                    view.stopLoading();
                    view.loadUrl(RHYA_NETWORK_LOGIN_URL);

                    return;
                }

                // Call back URL ??????
                if (url.equals(RHYA_NETWORK_CALL_BACK_URL)) {
                    // ????????? ?????? ??????
                    CookieManager cookieManager = CookieManager.getInstance();

                    final String cookieResult = cookieManager.getCookie(url);
                    final String userUUID = findCookie(cookieResult, "AutoLogin_UserUUID");
                    final String tokenUUID = findCookie(cookieResult, "AutoLogin_TokenUUID");

                    // Cookie ????????? ??????
                    if (userUUID == null || tokenUUID == null) {
                        // ?????? ????????? ?????? ??????
                        loginListener.isLoginFail();
                    }else {
                        loginListener.isLoginSuccess(userUUID, tokenUUID);
                    }
                }
            } catch (Exception e) {
                // ?????? ????????? ??????
                e.printStackTrace();

                reloadLoginPage(view);
            }
        }



        /**
         * findCookie : ?????? ?????? ????????? ??????
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
        // ???????????? ???????????? ??? ????????? ??????
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }
        // ?????? ????????? ??????????????? ???????????? ??? ???
        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
        }
        // ????????? ???????????? ??? ?????? ??????
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            // UI ??????
            view.setVisibility(View.VISIBLE);
            progressWheel.setVisibility(View.INVISIBLE);
        }
        // ????????? ?????? ??????, ????????? ????????? ??? ??????
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            switch (errorCode) {
                case ERROR_AUTHENTICATION:
                    break;               // ???????????? ????????? ?????? ??????
                case ERROR_BAD_URL:
                    break;                           // ????????? URL
                case ERROR_CONNECT:
                    break;                          // ????????? ?????? ??????
                case ERROR_FAILED_SSL_HANDSHAKE:
                    break;    // SSL handshake ?????? ??????
                case ERROR_FILE:
                    break;                                  // ?????? ?????? ??????
                case ERROR_FILE_NOT_FOUND:
                    break;               // ????????? ?????? ??? ????????????
                case ERROR_HOST_LOOKUP:
                    break;           // ?????? ?????? ????????? ????????? ?????? ?????? ??????
                case ERROR_IO:
                    break;                              // ???????????? ????????? ????????? ?????? ??????
                case ERROR_PROXY_AUTHENTICATION:
                    break;   // ??????????????? ????????? ?????? ??????
                case ERROR_REDIRECT_LOOP:
                    break;               // ?????? ?????? ????????????
                case ERROR_TIMEOUT:
                    break;                          // ?????? ?????? ??????
                case ERROR_TOO_MANY_REQUESTS:
                    break;     // ????????? ????????? ?????? ?????? ?????? ??????
                case ERROR_UNKNOWN:
                    break;                        // ?????? ??????
                case ERROR_UNSUPPORTED_AUTH_SCHEME:
                    break; // ???????????? ?????? ?????? ??????
                case ERROR_UNSUPPORTED_SCHEME:
                    break;          // URI??? ???????????? ?????? ??????
            }

        }
        // http ?????? ????????? ?????? ??????, ?????? ????????? ?????? ??????
        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
        // ????????? ?????? ?????? ????????? ?????? ??????
        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
        }
        // ????????? ??? ????????? ?????? ??????
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }
        // ????????? URL??? webview??? ???????????? ??? ?????? ???????????? ????????? ????????? ???
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        // -----------------------------------------------------------------------------------------------------
    }
}
