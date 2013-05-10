package com.richitec.imeeting.simple.customcomponent;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.richitec.commontoolkit.activityextension.NavigationActivity;
import com.richitec.imeeting.simple.R;

public class SimpleIMeetingNavigationActivity extends NavigationActivity {

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);

		// set navigation bar background drawable
		setNavBarBackgroundResource(R.drawable.img_imeeting_navbar_bg);
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);

		// set navigation bar background drawable
		setNavBarBackgroundResource(R.drawable.img_imeeting_navbar_bg);
	}

	@Override
	protected Drawable backBarBtnItemNormalDrawable() {
		return getResources().getDrawable(
				R.drawable.img_imeeting_leftbarbtnitem_normal_bg);
	}

	@Override
	protected Drawable backBarBtnItemPressedDrawable() {
		return getResources().getDrawable(
				R.drawable.img_imeeting_leftbarbtnitem_touchdown_bg);
	}

}
