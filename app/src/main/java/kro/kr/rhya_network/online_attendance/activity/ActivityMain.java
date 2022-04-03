package kro.kr.rhya_network.online_attendance.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import kro.kr.rhya_network.online_attendance.R;
import kro.kr.rhya_network.online_attendance.adapter.RhyaMainViewPagerAdapter;
import kro.kr.rhya_network.online_attendance.core.RhyaAuthManager;
import kro.kr.rhya_network.online_attendance.fragment.AttendanceFragment;
import kro.kr.rhya_network.online_attendance.fragment.SearchFragment;
import kro.kr.rhya_network.online_attendance.utils.OnlineAttendanceTeacherVO;

public class ActivityMain extends AppCompatActivity {
    // Activity info
    private Context context;
    // Toast message
    private Toast toast;
    // 뒤로가기 버튼 시간
    private long backBtnTime = 0;
    // 인자 데이터
    private String argsAuthToken;
    private OnlineAttendanceTeacherVO argsMyInfo;
    // RHYA Auth Token 관리자 선언
    private RhyaAuthManager rhyaAuthManager;
    // Fragment
    private AttendanceFragment attendanceFragment;
    private SearchFragment searchFragment;
    // UI Object
    private ViewPager2 viewPager2;




    // ===========================================================
    /**
     * Activity 생성 시 이벤트
     */
    // ===========================================================
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);


        // UI 초기화
        viewPager2 = findViewById(R.id.mainViewPager);


        // 인자 설정
        Intent intent = getIntent();
        argsAuthToken = intent.getStringExtra("authToken");
        argsMyInfo = intent.getParcelableExtra("myInfo");


        // 변수 초기화
        context = this;
        rhyaAuthManager = new RhyaAuthManager(this);
        toast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);

        // TabLayout 초기화
        tabLayoutInit();

        // Fragment 초기화
        fragmentInit();

        // ViewPager2 초기화
        viewPager2Init();
    }



    // ===========================================================
    /**
     * 종료 버튼 이벤트 가로채기
     */
    // ===========================================================
    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            // 종료
            super.onBackPressed();
        } else {
            backBtnTime = curTime;

            // Toast 메시지 출력
            toast.cancel();
            toast.makeText(context, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }



    // ===========================================================
    /**
     * TabLayout 초기화 함수
     */
    // ===========================================================
    private void tabLayoutInit() {
        final int tabItemSelectColor = ContextCompat.getColor(this, R.color.white_3);
        final int tabItemUnSelectColor = ContextCompat.getColor(this, R.color.black);

        TabLayout mainTabLayout = findViewById(R.id.mainTabLayout);


        Objects.requireNonNull(
                Objects.requireNonNull(
                        mainTabLayout.getTabAt(0)
                ).getIcon()
        ).setColorFilter(tabItemSelectColor, PorterDuff.Mode.SRC_IN);

        for (int i = 1; i < mainTabLayout.getTabCount(); i++) {
            Objects.requireNonNull(
                    Objects.requireNonNull(
                            mainTabLayout.getTabAt(i)
                    ).getIcon()
            ).setColorFilter(tabItemUnSelectColor, PorterDuff.Mode.SRC_IN);
        }

        mainTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // 색상 변경
                colorChange(tab);
                // Fragment 변경
                changeFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // 색상 변경
                Objects.requireNonNull(
                        tab.getIcon()
                ).setColorFilter(tabItemUnSelectColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // 색상 변경
                colorChange(tab);
                // Fragment 변경
                changeFragment(tab.getPosition());
            }

            private void colorChange(TabLayout.Tab tab) {
                // 색상 변경
                Objects.requireNonNull(
                        tab.getIcon()
                ).setColorFilter(tabItemSelectColor, PorterDuff.Mode.SRC_IN);
            }

            private void changeFragment(int pos) {
                // Fragment 변경
                switch (pos)
                {
                    case 0 : // 출석부

                        break;
                    case 1 : // 검색

                        break;
                    case 2 : // 반 관리

                        break;
                    case 3 : // 설정

                        break;
                }

                viewPager2.setCurrentItem(pos, false);
            }
        });
    }



    // ===========================================================
    /**
     * Fragment 초기화 함수
     */
    // ===========================================================
    private void fragmentInit() {
        // 출석부 Fragment
        if (attendanceFragment == null) attendanceFragment = AttendanceFragment.newInstance(argsAuthToken, argsMyInfo, rhyaAuthManager.ONLINE_ATTENDANCE_MANAGER);
        if (searchFragment == null) searchFragment = SearchFragment.newInstance(argsAuthToken, argsMyInfo);
    }



    // ===========================================================
    /**
     * ViewPager2 초기화 함수
     */
    // ===========================================================
    private void viewPager2Init() {
        RhyaMainViewPagerAdapter rhyaMainViewPagerAdapter = new RhyaMainViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        rhyaMainViewPagerAdapter.addFragment(attendanceFragment);
        rhyaMainViewPagerAdapter.addFragment(searchFragment);

        viewPager2.setUserInputEnabled(false);
        viewPager2.setAdapter(rhyaMainViewPagerAdapter);
    }
}
