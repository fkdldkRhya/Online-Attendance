package kro.kr.rhya_network.online_attendance.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/*
 * SharedPreferences 관리자
 */
public class RhyaSharedPreferences {
    /*
     * SharedPreferences 저장 정보
     */
    public final String SHARED_PREFERENCES_APP_NAME = "RHYA.Network"; // 저장 이름
    public final String SHARED_PREFERENCES_AES_KEY = "AES_SECRET_KEY"; // AES 암호화-복호화 값
    public final String SHARED_PREFERENCES_DEFAULT_RETURN = "#NULL#"; // Shared preferences 기본 반환 값



    // SharedPreferences 함수
    // **********************************************************************
    /**
     * SharedPreferences (No Encrypt) [ String ] 설정
     * @param key 설정 이름
     * @param value 설정 데이터
     */
    public void setStringNoAES(String key, String value, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_APP_NAME,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
    /**
     * SharedPreferences (No Decrypt) [ String ] 가져오기
     * @param key 설정 이름
     * @return 설정 데이터
     */
    public String getStringNoAES(String key, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_APP_NAME,
                Context.MODE_PRIVATE);
        return prefs.getString(key, SHARED_PREFERENCES_DEFAULT_RETURN);
    }
    /**
     * SharedPreferences [ String ] 제거
     * @param key 설정 이름
     */
    public void removeString(String key, Context mContext) {

        SharedPreferences prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_APP_NAME,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }
    // **********************************************************************
}
