package com.housekeeper.activity.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ares.house.dto.app.LandlordHouseListAppDto;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.client.Constants;
import com.housekeeper.client.net.ImageCacheManager;
import com.wufriends.housekeeper.landlord.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sth on 12/15/15.
 */
public class LandlordHouseInfoLayout extends LinearLayout {

    private BaseActivity context;

    private CustomNetworkImageView houseLogoImageView = null;
    private TextView houseCommunityTextView = null;
    private TextView houseAreaTextView = null;
    private TextView yearMoneyTextView = null;
    private TextView timeTextView = null;

    private LinearLayout agentLayout = null;
    private CircleImageView agentLogoImageView = null;
    private TextView agentNameTextView = null;

    public LandlordHouseInfoLayout(Context context) {
        super(context);

        this.initView(context);
    }

    public LandlordHouseInfoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }

    private void initView(Context context) {
        this.context = (BaseActivity) context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_landlord_house_info, this);

        houseLogoImageView = (CustomNetworkImageView) this.findViewById(R.id.houseLogoImageView);
        houseCommunityTextView = (TextView) this.findViewById(R.id.houseCommunityTextView);
        houseAreaTextView = (TextView) this.findViewById(R.id.houseAreaTextView);
        yearMoneyTextView = (TextView) this.findViewById(R.id.yearMoneyTextView);
        timeTextView = (TextView) this.findViewById(R.id.timeTextView);

        agentLayout = (LinearLayout) this.findViewById(R.id.agentLayout);
        agentLogoImageView = (CircleImageView) this.findViewById(R.id.agentLogoImageView);
        agentNameTextView = (TextView) this.findViewById(R.id.agentNameTextView);
    }

    public void setData(final LandlordHouseListAppDto dto) {
        houseLogoImageView.setImageUrl(Constants.HOST_IP + dto.getIndexImgUrl(), ImageCacheManager.getInstance().getImageLoader());
        houseCommunityTextView.setText(dto.getCommunity() + " " + dto.getHouseNum());
        houseAreaTextView.setText(dto.getCityStr() + " • " + dto.getAreaStr() + " • " + dto.getAddress());
        yearMoneyTextView.setText(dto.getYearMoney());
        timeTextView.setText(Html.fromHtml(dto.getBeginTimeStr() + "<font color=#666666> 至 </font>" + dto.getEndTimeStr()));

        agentLogoImageView.setImageURL(Constants.HOST_IP + dto.getAgentLogo());
        agentNameTextView.setText(dto.getAgentName());
        agentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + dto.getAgentTelphone()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }
}
