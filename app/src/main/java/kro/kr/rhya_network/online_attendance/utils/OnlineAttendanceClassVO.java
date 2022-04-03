package kro.kr.rhya_network.online_attendance.utils;

public class OnlineAttendanceClassVO {
    // 반 구분 UUID
    private String class_uuid;
    // 반 별칭
    private String class_nickname;
    // 담임 선생님 UUID
    private String class_teacher_uuid;
    // 반 데이터 변경 감지
    private int version;
    // 기타 데이터
    private boolean isChecked = false;
    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
    public boolean getChecked() {
        return isChecked;
    }



    public OnlineAttendanceClassVO(String class_uuid, String class_nickname, String class_teacher_uuid, int version) {
        this.class_uuid = class_uuid;
        this.class_nickname = class_nickname;
        this.class_teacher_uuid = class_teacher_uuid;
        this.version = version;
    }


    public String getClass_uuid() {
        return class_uuid;
    }

    public void setClass_uuid(String class_uuid) {
        this.class_uuid = class_uuid;
    }

    public String getClass_nickname() {
        return class_nickname;
    }

    public void setClass_nickname(String class_nickname) {
        this.class_nickname = class_nickname;
    }

    public String getClass_teacher_uuid() {
        return class_teacher_uuid;
    }

    public void setClass_teacher_uuid(String class_teacher_uuid) {
        this.class_teacher_uuid = class_teacher_uuid;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
