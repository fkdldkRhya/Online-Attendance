package kro.kr.rhya_network.online_attendance.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

public class RhyaDataManager {
    // 데이터 저장 경로
    public final String SAVE_ROOT_PATH;
    // 선생님 데이터 저장 파일 이름
    public final String TEACHER_INFO_FILENAME = "teacher.json";
    public final String STUDENT_INFO_FILENAME = "student.json";




    // 생성자
    public RhyaDataManager(String root) {
        // 경로 생성
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();
        stringBuilder.append(root);
        stringBuilder.append(File.separator);
        stringBuilder.append("online_attendance");

        SAVE_ROOT_PATH = stringBuilder.toString();

        if (!new File(SAVE_ROOT_PATH).exists()) {
            //noinspection ResultOfMethodCallIgnored
            new File(SAVE_ROOT_PATH).mkdir();
        }
    }



    // 선생님 데이터 읽기
    public HashMap<String, OnlineAttendanceTeacherVO> readJSONToTeacherVO(String json, boolean isDecode) throws JSONException, UnsupportedEncodingException {
        // 반환 데이터
        HashMap<String, OnlineAttendanceTeacherVO> onlineAttendanceTeacherVOHashMap = new HashMap<>();
        // JSON Object, Array
        JSONObject teacherJSONRootObject = new JSONObject(json);
        JSONArray teacherJSONRootArray = teacherJSONRootObject.getJSONArray("teacher");
        // JSON Array for
        for (int jsonArrayIndex = 0; jsonArrayIndex < teacherJSONRootArray.length(); jsonArrayIndex++) {
            // 선생님 정보 추출
            JSONObject teacherInfo = teacherJSONRootArray.getJSONObject(jsonArrayIndex);
            String uuid = teacherInfo.getString("uuid");
            String name = teacherInfo.getString("name");
            String name2 = teacherInfo.getString("name2");
            String image = teacherInfo.getString("image");
            String description = teacherInfo.getString("description");
            String department1 = teacherInfo.getString("department1");
            String department2 = teacherInfo.getString("department2");
            String office_phone = teacherInfo.getString("office_phone");
            String position = teacherInfo.getString("position");
            String subject = teacherInfo.getString("subject");
            int school_id = teacherInfo.getInt("school_id");
            int version = teacherInfo.getInt("version");

            if (isDecode) {
                final String UTF_8 = "UTF-8";

                uuid = URLDecoder.decode(uuid, UTF_8);
                name = URLDecoder.decode(name, UTF_8);
                name2 = URLDecoder.decode(name2, UTF_8);
                image = URLDecoder.decode(image, UTF_8);
                description = URLDecoder.decode(description, UTF_8);
                department1 = URLDecoder.decode(department1, UTF_8);
                department2 = URLDecoder.decode(department2, UTF_8);
                office_phone = URLDecoder.decode(office_phone, UTF_8);
                position = URLDecoder.decode(position, UTF_8);
                subject = URLDecoder.decode(subject, UTF_8);
            }


            // Teacher info VO
            OnlineAttendanceTeacherVO onlineAttendanceTeacherVO = new OnlineAttendanceTeacherVO(
                    uuid,
                    name,
                    name2,
                    image,
                    description,
                    department1,
                    department2,
                    null, // 민감 정보는 제외
                    null, // 민감 정보는 제외
                    office_phone,
                    position,
                    subject,
                    school_id,
                    version
            );
            onlineAttendanceTeacherVOHashMap.put(uuid, onlineAttendanceTeacherVO);
        }

        return onlineAttendanceTeacherVOHashMap;
    }
    // 학생 데이터 읽기
    public HashMap<String, OnlineAttendanceStudentVO> readJSONToStudentVO(String json, int school_id, boolean isDecode) throws JSONException, UnsupportedEncodingException {
        // 반환 데이터
        HashMap<String, OnlineAttendanceStudentVO> onlineAttendanceStudentVOHashMap = new HashMap<>();
        // JSON Object, Array
        JSONObject studentJSONRootObject = new JSONObject(json);
        JSONArray studentJSONRootArray = studentJSONRootObject.getJSONArray("student");
        // JSON Array for
        for (int jsonArrayIndex = 0; jsonArrayIndex < studentJSONRootArray.length(); jsonArrayIndex++) {
            // 선생님 정보 추출
            JSONObject studentInfo = studentJSONRootArray.getJSONObject(jsonArrayIndex);

            String student_uuid = studentInfo.getString("student_uuid");
            String student_class_uuid = studentInfo.getString("student_class_uuid");
            int student_number = studentInfo.getInt("student_number");
            String student_name = studentInfo.getString("student_name");
            String student_image = studentInfo.getString("student_image");
            int gender = studentInfo.getInt("gender");
            int move_out = studentInfo.getInt("move_out");
            int year = studentInfo.getInt("year");
            String note = studentInfo.getString("note");
            int version = studentInfo.getInt("version");

            if (isDecode) {
                final String UTF_8 = "UTF-8";

                student_uuid = URLDecoder.decode(student_uuid, UTF_8);
                student_class_uuid = URLDecoder.decode(student_class_uuid, UTF_8);
                student_name = URLDecoder.decode(student_name, UTF_8);
                student_image = URLDecoder.decode(student_image, UTF_8);
                note = URLDecoder.decode(note, UTF_8);
            }

            OnlineAttendanceStudentVO onlineAttendanceStudentVO = new OnlineAttendanceStudentVO(
                    student_uuid,
                    student_class_uuid,
                    student_number,
                    student_name,
                    student_image,
                    gender,
                    move_out,
                    year,
                    note,
                    version,
                    school_id
            );
            onlineAttendanceStudentVOHashMap.put(student_uuid, onlineAttendanceStudentVO);
        }

        return onlineAttendanceStudentVOHashMap;
    }



    // 파일 읽기
    // =========================================================== //
    // =========================================================== //
    public String readFile(File target) throws IOException {
        // 파일 읽기 변수 초기화
        BufferedReader buf;
        FileReader fr;
        String readLine;

        // 파일 읽기
        fr = new FileReader(target);
        buf = new BufferedReader(fr);

        StringBuilder readText = new StringBuilder();
        while ((readLine = buf.readLine()) != null) {
            readText.append(readLine);
            readText.append(System.lineSeparator());
        }

        buf.close();
        fr.close();

        return readText.toString();
    }
    // =========================================================== //
    // 파일 쓰기
    // =========================================================== //
    public void writeFile(File target, String data) throws IOException {
        // 파일 쓰기 변수 초기화
        BufferedWriter buf;
        FileWriter fw;
        // 파일 쓰기
        fw = new FileWriter(target, false);
        buf = new BufferedWriter(fw);
        buf.append(data);
        buf.close();
        fw.close();
    }
    // =========================================================== //
    // =========================================================== //
}
