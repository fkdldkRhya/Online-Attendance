package kro.kr.rhya_network.online_attendance.utils;

public class OnlineAttendanceStudentVO {
    // No value text
    public final String NO_VALUE_TEXT = "[NoValue]";

    // 학생 구분 UUID
    private String uuid;
    // 학생 반 구분 UUID
    private String class_uuid;
    // 학생 번호
    private int number;
    // 학생 이름
    private String name;
    // 학생 이미지 URL [ Auth Token 필요 ]
    private String image;
    // 학생 성별
    private int gender;
    // 학생 전학 여부
    private int move_out;
    // 년도
    private int year;
    // 학생 기타 사항
    private String note;
    // 학생 데이터 변경 감지
    private int version;
    // 학교 아이디
    private int school_id;




    /**
     * 생성자
     *
     * @param uuid 학생 UUID
     * @param class_uuid 학생 반 UUID
     * @param number 학생 번호
     * @param name 학생 이름
     * @param image 학생 이미지
     * @param gender 학생 성별
     * @param move_out 전학 여부
     * @param year 년도
     * @param note 기타 사항
     * @param version 데이터 변경 감지 버전
     * @param school_id 학생 소속 학교
     */
    public OnlineAttendanceStudentVO(String uuid, String class_uuid, int number, String name, String image, int gender,
                                     int move_out, int year, String note, int version, int school_id) {
        this.uuid = uuid;
        this.class_uuid = class_uuid;
        this.number = number;
        this.name = name;
        this.image = image;
        this.gender = gender;
        this.move_out = move_out;
        this.year = year;
        this.note = note;
        this.version = version;
        this.school_id = school_id;
    }



    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }



    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }



    /**
     * @return the class_uuid
     */
    public String getClass_uuid() {
        return class_uuid;
    }



    /**
     * @param class_uuid the class_uuid to set
     */
    public void setClass_uuid(String class_uuid) {
        this.class_uuid = class_uuid;
    }



    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }



    /**
     * @param number the number to set
     */
    public void setNumber(int number) {
        this.number = number;
    }



    /**
     * @return the name
     */
    public String getName() {
        return name;
    }



    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }



    /**
     * @return the image
     */
    public String getImage() {
        return image;
    }



    /**
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }



    /**
     * @return the gender
     */
    public int getGender() {
        return gender;
    }



    /**
     * @param gender the gender to set
     */
    public void setGender(int gender) {
        this.gender = gender;
    }



    /**
     * @return the move_out
     */
    public int getMove_out() {
        return move_out;
    }



    /**
     * @param move_out the move_out to set
     */
    public void setMove_out(int move_out) {
        this.move_out = move_out;
    }



    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }



    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }



    /**
     * @return the note
     */
    public String getNote() {
        return note;
    }



    /**
     * @param note the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }



    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }



    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
    }



    public String getImageURL(String authToken) {
        return image.concat("&auth=").concat(authToken);
    }



    /**
     * @return the school_id
     */
    public int getSchool_id() {
        return school_id;
    }
}
