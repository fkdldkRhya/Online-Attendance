package kro.kr.rhya_network.online_attendance.core;

import android.app.Application;

import java.util.HashMap;

import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceClassVO;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceDepartmentVO;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceStudentVO;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceTeacherVO;

public class RhyaApplication extends Application {
    // 온라인 츨석부 정보
    // ============================================================================================== //
    private static HashMap<String, OnlineAttendanceTeacherVO> onlineAttendanceTeacherVOHashMap = null;
    private static HashMap<String, OnlineAttendanceStudentVO> onlineAttendanceStudentVOHashMap = null;
    private static HashMap<String, OnlineAttendanceClassVO> onlineAttendanceClassVOHashMap = null;
    private static HashMap<String, OnlineAttendanceDepartmentVO> onlineAttendanceDepartmentVOHashMap = null;
    // ============================================================================================== //



    public static HashMap<String, OnlineAttendanceTeacherVO> getOnlineAttendanceTeacherVOHashMap() {
        return onlineAttendanceTeacherVOHashMap;
    }

    public static void setOnlineAttendanceTeacherVOHashMap(HashMap<String, OnlineAttendanceTeacherVO> onlineAttendanceTeacherVOHashMap) {
        if (RhyaApplication.onlineAttendanceTeacherVOHashMap == null)
            RhyaApplication.onlineAttendanceTeacherVOHashMap = onlineAttendanceTeacherVOHashMap;
    }

    public static HashMap<String, OnlineAttendanceStudentVO> getOnlineAttendanceStudentVOHashMap() {
        return onlineAttendanceStudentVOHashMap;
    }

    public static void setOnlineAttendanceStudentVOHashMap(HashMap<String, OnlineAttendanceStudentVO> onlineAttendanceStudentVOHashMap) {
        if (RhyaApplication.onlineAttendanceStudentVOHashMap == null)
            RhyaApplication.onlineAttendanceStudentVOHashMap = onlineAttendanceStudentVOHashMap;
    }



    public static HashMap<String, OnlineAttendanceClassVO> getOnlineAttendanceClassVOHashMap() {
        return onlineAttendanceClassVOHashMap;
    }

    public static void setOnlineAttendanceClassVOHashMap(HashMap<String, OnlineAttendanceClassVO> onlineAttendanceClassVOHashMap) {
        if (RhyaApplication.onlineAttendanceClassVOHashMap == null)
            RhyaApplication.onlineAttendanceClassVOHashMap = onlineAttendanceClassVOHashMap;
    }



    public static HashMap<String, OnlineAttendanceDepartmentVO> getOnlineAttendanceDepartmentVOHashMap() {
        return onlineAttendanceDepartmentVOHashMap;
    }

    public static void setOnlineAttendanceDepartmentVOHashMap(HashMap<String, OnlineAttendanceDepartmentVO> onlineAttendanceDepartmentVOHashMap) {
        if (RhyaApplication.onlineAttendanceDepartmentVOHashMap == null)
            RhyaApplication.onlineAttendanceDepartmentVOHashMap = onlineAttendanceDepartmentVOHashMap;
    }
}
