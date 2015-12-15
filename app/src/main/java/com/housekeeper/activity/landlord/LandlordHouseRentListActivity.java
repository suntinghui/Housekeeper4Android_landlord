package com.housekeeper.activity.landlord;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.ares.house.dto.app.AppMessageDto;
import com.ares.house.dto.app.AppResponseStatus;
import com.ares.house.dto.app.LandLordRentListAppDto;
import com.ares.house.dto.app.LandlordAppDto;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.activity.view.HouseRentLayout;
import com.housekeeper.client.RequestEnum;
import com.housekeeper.client.net.JSONRequest;
import com.housekeeper.utils.AdapterUtil;
import com.wufriends.housekeeper.landlord.R;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;

import java.util.List;

/**
 * Created by sth on 12/15/15.
 */
public class LandlordHouseRentListActivity extends BaseActivity implements View.OnClickListener {

    private TextView totalMoneyTextView = null;
    private LinearLayout contentLayout = null;

    private List<LandLordRentListAppDto> list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_landlord_house_rent_list);

        this.initView();

        requestHouseRentList();
    }

    private void initView() {
        this.findViewById(R.id.backBtn).setOnClickListener(this);
        ((TextView) this.findViewById(R.id.titleTextView)).setText("租金列表");

        this.totalMoneyTextView = (TextView) this.findViewById(R.id.totalMoneyTextView);
        this.contentLayout = (LinearLayout) this.findViewById(R.id.contentLayout);

        this.totalMoneyTextView.setText(((LandlordAppDto) getIntent().getSerializableExtra("DTO")).getTotalRent());
    }

    private void requestHouseRentList() {
        JSONRequest request = new JSONRequest(this, RequestEnum.HOUSE_LANDLORD_RENT_LIST, null, new Response.Listener<String>() {

            @Override
            public void onResponse(String jsonObject) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, LandLordRentListAppDto.class);
                    JavaType type = objectMapper.getTypeFactory().constructParametricType(AppMessageDto.class, javaType);
                    AppMessageDto<List<LandLordRentListAppDto>> dto = objectMapper.readValue(jsonObject, type);
                    if (dto.getStatus() == AppResponseStatus.SUCCESS) {

                        list = dto.getData();

                        responseHouseRentList();

                    } else {
                        Toast.makeText(LandlordHouseRentListActivity.this, dto.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        this.addToRequestQueue(request, "正在请求数据...");
    }

    private void responseHouseRentList() {
        this.contentLayout.removeAllViews();

        for (LandLordRentListAppDto dto : list) {
            HouseRentLayout layout = new HouseRentLayout(this);
            layout.setData(dto);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, AdapterUtil.dip2px(this, 20), 0, 0);

            this.contentLayout.addView(layout, params);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn:
                this.finish();
                break;
        }
    }
}
