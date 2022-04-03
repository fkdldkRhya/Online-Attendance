package kro.kr.rhya_network.online_attendance.utils;

public class RhyaErrorCodeManager {
    /**
     * getErrorMessage : 오류 메시지 관리 함수
     *
     * < 오류 메시지 종류 >
     * 1xx : 서버에서 데이터를 처리 중 오류 발생
     * 2xx : 계정 관련 인증 오류 발생
     * 3xx : 클라이언트에서 데이터 처리 중 오류 발생
     * 4xx : 네트워크 오류 발생
     * 5xx : 기타 작업 오류
     * 999 : 알 수 없는 오류
     *
     * @param errorCode 오류 코드
     *
     * @return 오류 메시지
     */
    public String getErrorMessage(int errorCode) {
        // 오류 메시지 리스트
        final String ERROR_CODE_100 = "서버에서 로그인 데이터를 처리 중 오류가 발생하였습니다. 다시 로그인해주세요.";
        final String ERROR_CODE_101 = "서버에서 JSON 데이터를 처리 중 오류가 발생하였습니다.";
        final String ERROR_CODE_200 = "로그인 확인이 실패하였습니다. 다시 로그인해주세요.";
        final String ERROR_CODE_201 = "사용자 인증 작업 처리 중 오류가 발생하였습니다. 다시 로그인해주세요.";
        final String ERROR_CODE_300 = "계정 동기화를 확인하는 중 예기치 못한 오류가 발생하였습니다.";
        final String ERROR_CODE_301 = "클라이언트와 서버의 데이터가 동기화되어있지 않습니다.";

        String result = "알 수 없는 오류가 발생하였습니다. 다시시도해 주세요.";

        switch (errorCode) {
            case 999:
                break;

            case 100:
                result = ERROR_CODE_100;
                break;

            case 101:
                result = ERROR_CODE_101;
                break;

            case 200:
                result = ERROR_CODE_200;
                break;

            case 201:
                result = ERROR_CODE_201;
                break;

            case 300:
                result = ERROR_CODE_300;
                break;

            case 301:
                result = ERROR_CODE_301;
                break;
        }


        // 오류 메시지 반환
        return result;
    }
}
