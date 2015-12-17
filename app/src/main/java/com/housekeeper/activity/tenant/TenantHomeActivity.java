package com.housekeeper.activity.tenant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.ares.house.dto.app.AppMessageDto;
import com.ares.house.dto.app.AppResponseStatus;
import com.ares.house.dto.app.HouseReleaseListAppDto;
import com.ares.house.dto.app.LinkArticle;
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.activity.HousePushIntentService;
import com.housekeeper.activity.view.HouseRecommendLayout;
import com.housekeeper.activity.view.MediaImagePagerAdapter;
import com.housekeeper.client.Constants;
import com.housekeeper.client.RequestEnum;
import com.housekeeper.client.net.JSONRequest;
import com.housekeeper.utils.ActivityUtil;
import com.housekeeper.utils.AdapterUtil;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.update.UmengUpdateAgent;
import com.wufriends.housekeeper.landlord.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * Created by sth on 10/23/15.
 */
public class TenantHomeActivity extends BaseActivity implements View.OnClickListener {

    private PullToZoomScrollViewEx scrollView;

    private LinearLayout wholeRentLayout;
    private LinearLayout shareRentLaout;
    private LinearLayout singleRoomLayout;
    private LinearLayout bedLayout;
    private LinearLayout landlordLayout;
    private LinearLayout keeperLayout;

    private AutoScrollViewPager mediaViewPager = null;
    private MediaImagePagerAdapter mediaViewPagerAdapter = null;
    private List<LinkArticle> mediaImageURLList = new ArrayList<LinkArticle>();

    private LinearLayout recommendLayout = null;
    private List<HouseReleaseListAppDto> mList = new ArrayList<HouseReleaseListAppDto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_tenant_home);

        loadViewForCode();

        //requestMediaImage();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (null != mediaViewPager) {
            mediaViewPager.startAutoScroll();
        }

        requestRecommendList();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (null != mediaViewPager) {
            mediaViewPager.stopAutoScroll();
        }
    }

    private void loadViewForCode() {
        scrollView = (PullToZoomScrollViewEx) findViewById(R.id.scroll_view);

        // 背景图片
        View zoomView = LayoutInflater.from(this).inflate(R.layout.tenant_home_profile, null, false);
        scrollView.setZoomView(zoomView);

        // 下面的内容
        View contentView = LayoutInflater.from(this).inflate(R.layout.tenant_home_content, null, false);
        scrollView.setScrollContentView(contentView);

        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);

        initView();
    }

    private void initView() {
        this.wholeRentLayout = (LinearLayout) scrollView.getRootView().findViewById(R.id.wholeRentLayout);
        this.wholeRentLayout.setOnClickListener(this);

        this.shareRentLaout = (LinearLayout) scrollView.getRootView().findViewById(R.id.shareRentLayout);
        this.shareRentLaout.setOnClickListener(this);

        this.singleRoomLayout = (LinearLayout) scrollView.getRootView().findViewById(R.id.singleRoomLayout);
        this.singleRoomLayout.setOnClickListener(this);

        this.bedLayout = (LinearLayout) scrollView.getRootView().findViewById(R.id.bedLayout);
        this.bedLayout.setOnClickListener(this);

        this.keeperLayout = (LinearLayout) scrollView.getRootView().findViewById(R.id.keeperLayout);
        this.keeperLayout.setOnClickListener(this);

        this.landlordLayout = (LinearLayout) scrollView.getRootView().findViewById(R.id.landlordLayout);
        this.landlordLayout.setOnClickListener(this);

        this.recommendLayout = (LinearLayout) scrollView.getRootView().findViewById(R.id.recommendLayout);
    }

    private void initMediaViewPager() {
        // ViewPager
        mediaViewPager = (AutoScrollViewPager) this.findViewById(R.id.mediaViewPager);
        mediaViewPager.setInterval(3000);
        mediaViewPager.setCycle(true);
        mediaViewPager.setAutoScrollDurationFactor(7.0);
        mediaViewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mediaViewPager.setStopScrollWhenTouch(true);
        mediaViewPagerAdapter = new MediaImagePagerAdapter(this, mediaImageURLList);

        // viewPagerAdapter.setInfiniteLoop(true);
        mediaViewPager.setAdapter(mediaViewPagerAdapter);
        mediaViewPager.startAutoScroll();
    }

    // 取得媒体报道图片
    private void requestMediaImage() {
        JSONRequest request = new JSONRequest(this, RequestEnum.LINK_ARTICLE, null, false, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, LinkArticle.class);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);
                    AppMessageDto<List<LinkArticle>> dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {
                        mediaImageURLList = dto.getData();

                        initMediaViewPager();

                        mediaViewPagerAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, null);
    }

    // 取得媒体报道图片
    private void requestRecommendList() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("cityId", "5");

        JSONRequest request = new JSONRequest(this, RequestEnum.HOUSE_RECOMMEND, map, false, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(List.class, HouseReleaseListAppDto.class);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, type);
                    AppMessageDto<List<HouseReleaseListAppDto>> dto = objectMapper.readValue(jsonObject, javaType);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        mList = dto.getData();

                        responseRecommendList();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, null);
    }

    private void responseRecommendList() {
        recommendLayout.removeAllViews();

        for (HouseReleaseListAppDto infoDto : mList) {
            HouseRecommendLayout layout = new HouseRecommendLayout(this);
            layout.setData(infoDto);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, AdapterUtil.dip2px(this, 20));

            recommendLayout.addView(layout, params);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wholeRentLayout: { // 整租
                Intent intent = new Intent(this, TenantPublishListActivity.class);
                intent.putExtra("leaseType", "OVERALL");
                this.startActivity(intent);
            }
            break;

            case R.id.shareRentLayout: { // 合租
                Intent intent = new Intent(this, TenantPublishListActivity.class);
                intent.putExtra("leaseType", "COMBINATION");
                this.startActivity(intent);
            }
            break;

            case R.id.singleRoomLayout: { // 单间
                Intent intent = new Intent(this, TenantPublishListActivity.class);
                intent.putExtra("leaseType", "SEPARATE");
                this.startActivity(intent);
            }
            break;

            case R.id.bedLayout: { // 床位
                Intent intent = new Intent(this, TenantPublishListActivity.class);
                intent.putExtra("leaseType", "BUNK");
                this.startActivity(intent);
            }
            break;

            case R.id.keeperLayout: {
                Intent intent = new Intent(this, TenantDownloadLandlordActivity.class);
                this.startActivity(intent);
            }
            break;

            case R.id.landlordLayout: {
                Intent intent = new Intent(this, TenantDownloadLandlordActivity.class);
                this.startActivity(intent);
            }
            break;
        }
    }

}
