package com.housekeeper.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ares.house.dto.app.LandLordRentListAppDto;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.activity.InvestmentActivity;
import com.wufriends.housekeeper.landlord.R;

/**
 * Created by sth on 12/15/15.
 */
public class HouseRentLayout extends LinearLayout {

    private BaseActivity context;
    private TextView moneyTextView;
    private TextView timeTextView;

    public HouseRentLayout(BaseActivity context) {
        super(context);

        this.initView(context);
    }

    public HouseRentLayout(InvestmentActivity context, AttributeSet attrs) {
        super(context, attrs);

        this.initView(context);
    }


    private void initView(BaseActivity context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_house_rent, this);

        moneyTextView = (TextView) this.findViewById(R.id.moneyTextView);
        timeTextView = (TextView) this.findViewById(R.id.timeTextView);
    }

    public void setData(LandLordRentListAppDto dto) {
        this.moneyTextView.setText(dto.getMoney());
        this.timeTextView.setText(dto.getTimeStr());
    }

}
