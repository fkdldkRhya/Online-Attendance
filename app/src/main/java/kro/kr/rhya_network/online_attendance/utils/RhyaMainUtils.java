package kro.kr.rhya_network.online_attendance.utils;

import java.util.Random;

import kro.kr.rhya_network.online_attendance.BuildConfig;

public class RhyaMainUtils {
    // 앱 버전 확인
    public boolean updateChecker(String version) {
        return Integer.parseInt(BuildConfig.VERSION_NAME.replace(".", "")) >= Integer.parseInt(version.replace(".", ""));
    }



    // 앱 버전
    public String getAppVersion() {
        return BuildConfig.VERSION_NAME;
    }



    // 랜덤 시간 대기
    public void randomThreadSleep(int input) throws InterruptedException {
        Random random = new Random();
        Thread.sleep(random.nextInt(input));
    }
}
