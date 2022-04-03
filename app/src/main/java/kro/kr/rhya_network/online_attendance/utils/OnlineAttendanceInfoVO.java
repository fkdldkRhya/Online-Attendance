package kro.kr.rhya_network.online_attendance.utils;

import java.util.Objects;

import kro.kr.rhya_network.online_attendance.core.RhyaApplication;

public class OnlineAttendanceInfoVO implements Comparable<OnlineAttendanceInfoVO> {
    // No value text
    public final String NO_VALUE_TEXT = "[NoValue]";

    // 학생 UUID
    private String uuid;
    // 출석부 날자
    private String attendance_date;
    // 출석부 데이터 - 1교시
    private int value_1;
    // 출석부 데이터 - 2교시
    private int value_2;
    // 출석부 데이터 - 3교시
    private int value_3;
    // 출석부 데이터 - 4교시
    private int value_4;
    // 출석부 데이터 - 5교시
    private int value_5;
    // 출석부 데이터 - 6교시
    private int value_6;
    // 출석부 데이터 - 7교시
    private int value_7;
    // 출석부 데이터 - 8교시
    private int value_8;
    // 출석부 데이터 - 9교시
    private int value_9;
    // 출석부 데이터 - 10교시
    private int value_10;
    // 출석부 데이터 선생님
    private String value_1_teacher;
    private String value_2_teacher;
    private String value_3_teacher;
    private String value_4_teacher;
    private String value_5_teacher;
    private String value_6_teacher;
    private String value_7_teacher;
    private String value_8_teacher;
    private String value_9_teacher;
    private String value_10_teacher;
    // 학생 기타 정보
    private String note;
    // 기타 데이터
    private boolean isChecked = false;
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
    public boolean getChecked() {
        return isChecked;
    }




    public OnlineAttendanceInfoVO(String uuid, String attendance_date, int value_1, int value_2, int value_3, int value_4, int value_5, int value_6, int value_7, int value_8, int value_9, int value_10, String value_1_teacher, String value_2_teacher, String value_3_teacher, String value_4_teacher, String value_5_teacher, String value_6_teacher, String value_7_teacher, String value_8_teacher, String value_9_teacher, String value_10_teacher, String note) {
        this.uuid = uuid;
        this.attendance_date = attendance_date;
        this.value_1 = value_1;
        this.value_2 = value_2;
        this.value_3 = value_3;
        this.value_4 = value_4;
        this.value_5 = value_5;
        this.value_6 = value_6;
        this.value_7 = value_7;
        this.value_8 = value_8;
        this.value_9 = value_9;
        this.value_10 = value_10;
        this.value_1_teacher = value_1_teacher;
        this.value_2_teacher = value_2_teacher;
        this.value_3_teacher = value_3_teacher;
        this.value_4_teacher = value_4_teacher;
        this.value_5_teacher = value_5_teacher;
        this.value_6_teacher = value_6_teacher;
        this.value_7_teacher = value_7_teacher;
        this.value_8_teacher = value_8_teacher;
        this.value_9_teacher = value_9_teacher;
        this.value_10_teacher = value_10_teacher;
        this.note = note;
    }



    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAttendance_date() {
        return attendance_date;
    }

    public void setAttendance_date(String attendance_date) {
        this.attendance_date = attendance_date;
    }

    public int getValue_1() {
        return value_1;
    }

    public void setValue_1(int value_1) {
        this.value_1 = value_1;
    }

    public int getValue_2() {
        return value_2;
    }

    public void setValue_2(int value_2) {
        this.value_2 = value_2;
    }

    public int getValue_3() {
        return value_3;
    }

    public void setValue_3(int value_3) {
        this.value_3 = value_3;
    }

    public int getValue_4() {
        return value_4;
    }

    public void setValue_4(int value_4) {
        this.value_4 = value_4;
    }

    public int getValue_5() {
        return value_5;
    }

    public void setValue_5(int value_5) {
        this.value_5 = value_5;
    }

    public int getValue_6() {
        return value_6;
    }

    public void setValue_6(int value_6) {
        this.value_6 = value_6;
    }

    public int getValue_7() {
        return value_7;
    }

    public void setValue_7(int value_7) {
        this.value_7 = value_7;
    }

    public int getValue_8() {
        return value_8;
    }

    public void setValue_8(int value_8) {
        this.value_8 = value_8;
    }

    public int getValue_9() {
        return value_9;
    }

    public void setValue_9(int value_9) {
        this.value_9 = value_9;
    }

    public int getValue_10() {
        return value_10;
    }

    public void setValue_10(int value_10) {
        this.value_10 = value_10;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getValue_1_teacher() {
        return value_1_teacher;
    }

    public void setValue_1_teacher(String value_1_teacher) {
        this.value_1_teacher = value_1_teacher;
    }

    public String getValue_2_teacher() {
        return value_2_teacher;
    }

    public void setValue_2_teacher(String value_2_teacher) {
        this.value_2_teacher = value_2_teacher;
    }

    public String getValue_3_teacher() {
        return value_3_teacher;
    }

    public void setValue_3_teacher(String value_3_teacher) {
        this.value_3_teacher = value_3_teacher;
    }

    public String getValue_4_teacher() {
        return value_4_teacher;
    }

    public void setValue_4_teacher(String value_4_teacher) {
        this.value_4_teacher = value_4_teacher;
    }

    public String getValue_5_teacher() {
        return value_5_teacher;
    }

    public void setValue_5_teacher(String value_5_teacher) {
        this.value_5_teacher = value_5_teacher;
    }

    public String getValue_6_teacher() {
        return value_6_teacher;
    }

    public void setValue_6_teacher(String value_6_teacher) {
        this.value_6_teacher = value_6_teacher;
    }

    public String getValue_7_teacher() {
        return value_7_teacher;
    }

    public void setValue_7_teacher(String value_7_teacher) {
        this.value_7_teacher = value_7_teacher;
    }

    public String getValue_8_teacher() {
        return value_8_teacher;
    }

    public void setValue_8_teacher(String value_8_teacher) {
        this.value_8_teacher = value_8_teacher;
    }

    public String getValue_9_teacher() {
        return value_9_teacher;
    }

    public void setValue_9_teacher(String value_9_teacher) {
        this.value_9_teacher = value_9_teacher;
    }

    public String getValue_10_teacher() {
        return value_10_teacher;
    }

    public void setValue_10_teacher(String value_10_teacher) {
        this.value_10_teacher = value_10_teacher;
    }





    @Override
    public int compareTo(OnlineAttendanceInfoVO onlineAttendanceInfoVO) {
        if (onlineAttendanceInfoVO == null) return 0;

        return Integer.compare(
                Objects.requireNonNull(RhyaApplication.getOnlineAttendanceStudentVOHashMap().get(uuid)).getNumber(),
                Objects.requireNonNull(RhyaApplication.getOnlineAttendanceStudentVOHashMap().get(onlineAttendanceInfoVO.getUuid())).getNumber()
        );
    }
}
