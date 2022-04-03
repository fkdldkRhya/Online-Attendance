package kro.kr.rhya_network.online_attendance.utils;

public class OnlineAttendanceDepartmentVO {
    // 구분 UUID
    private String uuid;
    // 별칭
    private String name;
    // 데이터 변경 감지
    private int version;


    public OnlineAttendanceDepartmentVO(String uuid, String name, int version) {
        this.uuid = uuid;
        this.name = name;
        this.version = version;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
