package kro.kr.rhya_network.online_attendance.utils;

import android.content.ContentValues;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class RhyaHttpsConnection {
    public String request(String _url, ContentValues _params){

        // HttpURLConnection 참조 변수.
        HttpsURLConnection urlConn = null;
        // URL 뒤에 붙여서 보낼 파라미터.
        StringBuilder sbParams = new StringBuilder();

        // 보낼 데이터가 없으면 파라미터를 비운다.
        if (_params == null)
            sbParams.append("");
            // 보낼 데이터가 있으면 파라미터를 채운다.
        else {
            // 파라미터가 2개 이상이면 파라미터 연결에 &가 필요하므로 스위칭할 변수 생성.
            boolean isAnd = false;
            // 파라미터 키와 값.
            String key;
            String value;

            for(Map.Entry<String, Object> parameter : _params.valueSet()){
                key = parameter.getKey();
                value = parameter.getValue().toString();

                // 파라미터가 두개 이상일때, 파라미터 사이에 &를 붙인다.
                if (isAnd)
                    sbParams.append("&");

                sbParams.append(key).append("=").append(value);

                // 파라미터가 2개 이상이면 isAnd를 true로 바꾸고 다음 루프부터 &를 붙인다.
                if (!isAnd)
                    if (_params.size() >= 2)
                        isAnd = true;
            }
        }

        try{
            URL url = new URL(_url);

            urlConn = (HttpsURLConnection) url.openConnection();

            urlConn.setConnectTimeout(15000);

            // [2-1]. urlConn 설정.
            urlConn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
            urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            // TLS 설정
            SSLContext sslcontext = SSLContext.getInstance("TLSv1.1");
            sslcontext.init(null, null, null);
            SSLSocketFactory SSLFactory = new RhyaTLSOnlySocketFactory(sslcontext.getSocketFactory());
            HttpsURLConnection.setDefaultSSLSocketFactory(SSLFactory);
            urlConn.setSSLSocketFactory(SSLFactory);

            // [2-2]. parameter 전달 및 데이터 읽어오기.
            String strParams = sbParams.toString(); //sbParams에 정리한 파라미터들을 스트링으로 저장. 예)id=id1&pw=123;
            OutputStream os = urlConn.getOutputStream();
            os.write(strParams.getBytes(StandardCharsets.UTF_8)); // 출력 스트림에 출력.
            os.flush(); // 출력 스트림을 플러시(비운다)하고 버퍼링 된 모든 출력 바이트를 강제 실행.
            os.close(); // 출력 스트림을 닫고 모든 시스템 자원을 해제.

            // [2-3]. 연결 요청 확인.
            // 실패 시 null을 리턴하고 메서드를 종료.
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            // [2-4]. 읽어온 결과물 리턴.
            // 요청한 URL의 출력물을 BufferedReader로 받는다.
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), StandardCharsets.UTF_8));

            // 출력물의 라인과 그 합에 대한 변수.
            String line;
            StringBuilder page = new StringBuilder();

            // 라인을 받아와 합친다.
            while ((line = reader.readLine()) != null){
                page.append(line);
            }

            return page.toString();

        } catch (IOException e) { // for URL.
            e.printStackTrace();
            return "IOException";
        } catch (KeyManagementException | NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        finally {
            if (urlConn != null)
                urlConn.disconnect();
        }

        return null;
    }
}
