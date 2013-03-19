package com.richitec.simpleimeeting.customcomponent;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.richitec.commontoolkit.customcomponent.BarButtonItem;
import com.richitec.simpleimeeting.R;

public class SimpleIMeetingBarButtonItem extends BarButtonItem {

	public SimpleIMeetingBarButtonItem(Context context,
			BarButtonItemStyle barBtnItemStyle, CharSequence title,
			OnClickListener btnClickListener) {
		super(
				context,
				title,
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

	public SimpleIMeetingBarButtonItem(Context context,
			BarButtonItemStyle barBtnItemStyle, int titleId,
			OnClickListener btnClickListener) {
		this(context, barBtnItemStyle, context.getResources()
				.getString(titleId), btnClickListener);
	}

	public SimpleIMeetingBarButtonItem(Context context, CharSequence title,
			BarButtonItemStyle barBtnItemStyle,
			Drawable normalBackgroundDrawable,
			Drawable pressedBackgroundDrawable, OnClickListener btnClickListener) {
		super(context, title, barBtnItemStyle, normalBackgroundDrawable,
				pressedBackgroundDrawable, btnClickListener);
	}

	public SimpleIMeetingBarButtonItem(Context context, CharSequence title,
			OnClickListener btnClickListener) {
		super(context, title, btnClickListener);
	}

	public SimpleIMeetingBarButtonItem(Context context, int titleId,
			int normalBackgroundResId, int pressedBackgroundResId,
			OnClickListener btnClickListener) {
		super(context, titleId, normalBackgroundResId, pressedBackgroundResId,
				btnClickListener);
	}

	public SimpleIMeetingBarButtonItem(Context context, int titleId,
			OnClickListener btnClickListener) {
		super(context, titleId, btnClickListener);
	}

	public SimpleIMeetingBarButtonItem(Context context, int resId) {
		super(context, resId);
	}

	public SimpleIMeetingBarButtonItem(Context context) {
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
