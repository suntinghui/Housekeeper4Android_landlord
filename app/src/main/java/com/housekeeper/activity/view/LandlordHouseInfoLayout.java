package com.housekeeper.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.ares.house.dto.app.LandlordHouseListAppDto;
import com.housekeeper.activity.BaseActivity;
import com.housekeeper.activity.keeper.KeeperReserveListActivity;
import com.wufriends.housekeeper.landlord.R;

/**
 * Created by sth on 12/15/15.
 */
public class LandlordHouseInfoLayout extends LinearLayout {

    private BaseActivity context;

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



    }

    public void setData(LandlordHouseListAppDto dto){

    }
}
