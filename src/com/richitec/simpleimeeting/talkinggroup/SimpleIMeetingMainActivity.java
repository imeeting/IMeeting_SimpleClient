package com.richitec.simpleimeeting.talkinggroup;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.commontoolkit.utils.DisplayScreenUtils;
import com.richitec.simpleimeeting.R;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingBarButtonItem;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingImageBarButtonItem;
import com.richitec.simpleimeeting.customcomponent.SimpleIMeetingNavigationActivity;
import com.richitec.simpleimeeting.view.SIMViewFactory;

public class SimpleIMeetingMainActivity extends
		SimpleIMeetingNavigationActivity {

	private static final String LOG_TAG = SimpleIMeetingMainActivity.class
			.getCanonicalName();

	// simple imeeting main view type
	private SimpleIMeetingMainViewType _mMainViewType = SimpleIMeetingMainViewType.ADDRESSBOOK_CONTACTS;

	// contacts select and my talking group list subViews
	private View _mContactsSelectView;
	private View _mMyTalkingGroupsView;

	// tap to generate new talking group title textView
	private TextView _mTap2GenNewTalkingGroupTitleTextView;

	// left bar button item, switch to my talking group list and switch to
	// contacts select bar button item
	private SimpleIMeetingBarButtonItem _mSwitch2MyTalkingGroupsLeftBarButtonItem;
	private SimpleIMeetingBarButtonItem _mSwitch2ContactsSelectLeftBarButtonItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setMainActivityContentView();

		// set navigation title and left bar button item
		setMainActivityNavigationTitle7LeftBarButtonItem();

		// set right image bar button item, about info image bar button item
		setRightBarButtonItem(new SimpleIMeetingImageBarButtonItem(this,
				android.R.drawable.ic_dialog_info, BarButtonItemStyle.RIGHT_GO,
				new MoreMenuImageBarButtonItemOnClickListener()));
	}

	// set main activity content view
	private void setMainActivityContentView(
			SimpleIMeetingMainViewType mainViewType) {
		// main activity content view
		View _contentView;

		// save main view type
		_mMainViewType = mainViewType;

		// check main view type and content view
		switch (mainViewType) {
		case MY_TALKINGGROUP_LIST: {
			// check my talking group list content view
			if (null == _mMyTalkingGroupsView) {
				// init my talking group list content view and save
				_mMyTalkingGroupsView = SIMViewFactory.createSIMView4Present(
						this, MyTalkingGroupsView.class);
			}

			_contentView = _mMyTalkingGroupsView;
		}
			break;

		default:
		case ADDRESSBOOK_CONTACTS: {
			// check contacts select content view
			if (null == _mContactsSelectView) {
				// init contacts select content view and save
				_mContactsSelectView = SIMViewFactory.createSIMView4Present(
						this, ContactsSelectView.class);
			}

			_contentView = _mContactsSelectView;
		}
			break;
		}

		// check content view and set it
		if (null != _contentView) {
			setContentView(_contentView);
		} else {
			Log.e(LOG_TAG,
					"No, no no ...! There is no main activity content view.");
		}
	}

	private void setMainActivityContentView() {
		setMainActivityContentView(_mMainViewType);
	}

	// set navigation title and left bar button item
	private void setMainActivityNavigationTitle7LeftBarButtonItem() {
		// set title
		String _titleTag;

		// check tap to generate new talking group title textView
		if (null == _mTap2GenNewTalkingGroupTitleTextView) {
			// init tap to generate new talking group title textView
			_mTap2GenNewTalkingGroupTitleTextView = new TextView(this);

			// set title textView text, font size and color
			_mTap2GenNewTalkingGroupTitleTextView
					.setText(R.string.tap2generateNewTalkingGroup_title);
			_mTap2GenNewTalkingGroupTitleTextView
					.setTextSize(DisplayScreenUtils.sp2pix(14));
			_mTap2GenNewTalkingGroupTitleTextView.setTextColor(Color.WHITE);

			// set title on touch listener
			_mTap2GenNewTalkingGroupTitleTextView
					.setOnTouchListener(new TitleOnTouchListener());
		}

		// main activity left bar button item
		SimpleIMeetingBarButtonItem _leftBarButtonItem;

		// check main view type and set left bar button item
		switch (_mMainViewType) {
		case MY_TALKINGGROUP_LIST: {
			// check switch to contacts select left bar button item
			if (null == _mSwitch2ContactsSelectLeftBarButtonItem) {
				// init switch to contacts select left bar button item and save
				_mSwitch2ContactsSelectLeftBarButtonItem = new SimpleIMeetingBarButtonItem(
						this, BarButtonItemStyle.RIGHT_GO,
						R.string.myTalkingGroup_leftBarButtonItem_navTitle,
						new AddressBookContactsBarButtonItemOnClickListener());
			}

			_titleTag = getResources().getString(
					R.string.contactsSelect_leftBarButtonItem_navTitle);

			_leftBarButtonItem = _mSwitch2ContactsSelectLeftBarButtonItem;
		}
			break;

		default:
		case ADDRESSBOOK_CONTACTS: {
			// check switch to my talking group list left bar button item
			if (null == _mSwitch2MyTalkingGroupsLeftBarButtonItem) {
				// init switch to my talking group list left bar button item and
				// save
				_mSwitch2MyTalkingGroupsLeftBarButtonItem = new SimpleIMeetingBarButtonItem(
						this, BarButtonItemStyle.RIGHT_GO,
						R.string.contactsSelect_leftBarButtonItem_navTitle,
						new MyTalkingGroupBarButtonItemOnClickListener());
			}

			_titleTag = getResources().getString(
					R.string.myTalkingGroup_leftBarButtonItem_navTitle);

			_leftBarButtonItem = _mSwitch2MyTalkingGroupsLeftBarButtonItem;
		}
			break;
		}

		// set title with tag
		setTitle(_mTap2GenNewTalkingGroupTitleTextView, _titleTag);

		// set left bar button item
		// check left bar button item and set it
		if (null != _leftBarButtonItem) {
			setLeftBarButtonItem(_leftBarButtonItem);
		} else {
			Log.e(LOG_TAG,
					"No, no no ...! There is no main activity left bar button item.");
		}
	}

	// inner class
	// simple imeeting main view type
	enum SimpleIMeetingMainViewType {
		ADDRESSBOOK_CONTACTS, MY_TALKINGGROUP_LIST
	}

	// title on touch listener
	class TitleOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// check motion event
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				Log.d(LOG_TAG, "Generate new talking group");
			}

			return false;
		}

	}

	// my talking group bar button item on click listener
	class MyTalkingGroupBarButtonItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// switch to my talking group view
			Log.d(LOG_TAG, "Switch to my talking group view");

			//
			setMainActivityContentView(SimpleIMeetingMainViewType.MY_TALKINGGROUP_LIST);
			setMainActivityNavigationTitle7LeftBarButtonItem();
		}

	}

	// addressbook contacts bar button item on click listener
	class AddressBookContactsBarButtonItemOnClickListener implements
			OnClickListener {

		@Override
		public void onClick(View v) {
			// switch to addressbook contacts view
			Log.d(LOG_TAG, "Switch to addressbook contacts view");

			//
			setMainActivityContentView(SimpleIMeetingMainViewType.ADDRESSBOOK_CONTACTS);
			setMainActivityNavigationTitle7LeftBarButtonItem();
		}

	}

	// more menu image bar button item on click listener
	class MoreMenuImageBarButtonItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// show more menu
			// ??

			// go to about activity
			// pushActivity(AboutActivity.class);
		}

	}

}
