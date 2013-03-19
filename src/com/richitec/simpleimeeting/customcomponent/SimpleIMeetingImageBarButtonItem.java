package com.richitec.simpleimeeting.customcomponent;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.commontoolkit.customcomponent.ImageBarButtonItem;
import com.richitec.simpleimeeting.R;

public class SimpleIMeetingImageBarButtonItem extends ImageBarButtonItem {

	public SimpleIMeetingImageBarButtonItem(Context context,
			Drawable srcDrawable, BarButtonItemStyle barBtnItemStyle,
			OnClickListener btnClickListener) {
		super(
				context,
				srcDrawable,
				barBtnItemStyle,
				BarButtonItemStyle.LEFT_BACK == barBtnItemStyle ? context
						.getResources()
						.getDrawable(
								R.drawable.img_imeeting_leftbarbtnitem_normal_bg)
						: (BarButtonItemStyle.RIGHT_GO == barBtnItemStyle ? context
								.getResources()
								.getDrawable(
										R.drawable.img_imeeting_rightbarbtnitem_normal_bg)
								: null),
				BarButtonItemStyle.LEFT_BACK == barBtnItemStyle ? context
						.getResources()
						.getDrawable(
								R.drawable.img_imeeting_leftbarbtnitem_touchdown_bg)
						: (BarButtonItemStyle.RIGHT_GO == barBtnItemStyle ? context
								.getResources()
								.getDrawable(
										R.drawable.img_imeeting_rightbarbtnitem_touchdown_bg)
								: null), btnClickListener);
	}

	public SimpleIMeetingImageBarButtonItem(Context context, int srcId,
			BarButtonItemStyle barBtnItemStyle, OnClickListener btnClickListener) {
		this(context, context.getResources().getDrawable(srcId),
				barBtnItemStyle, btnClickListener);
	}

	public SimpleIMeetingImageBarButtonItem(Context context,
			Drawable srcDrawable, BarButtonItemStyle barBtnItemStyle,
			Drawable normalBackgroundDrawable,
			Drawable pressedBackgroundDrawable, OnClickListener btnClickListener) {
		super(context, srcDrawable, barBtnItemStyle, normalBackgroundDrawable,
				pressedBackgroundDrawable, btnClickListener);
	}

	public SimpleIMeetingImageBarButtonItem(Context context,
			Drawable srcDrawable, OnClickListener btnClickListener) {
		super(context, srcDrawable, btnClickListener);
	}

	public SimpleIMeetingImageBarButtonItem(Context context, int srcId,
			int normalBackgroundResId, int pressedBackgroundResId,
			OnClickListener btnClickListener) {
		super(context, srcId, normalBackgroundResId, pressedBackgroundResId,
				btnClickListener);
	}

	public SimpleIMeetingImageBarButtonItem(Context context, int srcId,
			OnClickListener btnClickListener) {
		super(context, srcId, btnClickListener);
	}

	public SimpleIMeetingImageBarButtonItem(Context context, int resId) {
		super(context, resId);
	}

	public SimpleIMeetingImageBarButtonItem(Context context) {
		super(context);
	}

	@Override
	protected Drawable leftBarBtnItemNormalDrawable() {
		return this.getResources().getDrawable(
				R.drawable.img_imeeting_leftbarbtnitem_normal_bg);
	}

	@Override
	protected Drawable rightBarBtnItemNormalDrawable() {
		return this.getResources().getDrawable(
				R.drawable.img_imeeting_rightbarbtnitem_normal_bg);
	}

}
