package com.richitec.simpleimeeting.talkinggroup;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.simpleimeeting.R;
import com.richitec.simpleimeeting.assistant.AboutActivity;
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

	// tap to generate new talking group title textView
	private TextView _mTap2GenNewTalkingGroupTitleTextView;

	// left bar button item, switch to my talking group list and switch to
	// contacts select bar button item
	private SimpleIMeetingBarButtonItem _fmSwitch2MyTalkingGroupsLeftBarButtonItem;
	private SimpleIMeetingBarButtonItem _fmSwitch2ContactsSelectLeftBarButtonItem;

	// right bar button item, about info image bar button item
	private SimpleIMeetingImageBarButtonItem _fmAboutInfoImageBarButtonItem;

	// contacts select and my talking groups subViews
	private View _fmContactsSelectView;
	private View _fmMyTalkingGroupsView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//
		_fmContactsSelectView = SIMViewFactory.createSIMView4Present(this,
				ContactsSelectView.class);
		_fmMyTalkingGroupsView = SIMViewFactory.createSIMView4Present(this,
				MyTalkingGroupsView.class);

		// set content view
		setMainActivityContentView();

		//
		_fmSwitch2MyTalkingGroupsLeftBarButtonItem = new SimpleIMeetingBarButtonItem(
				this, BarButtonItemStyle.RIGHT_GO,
				R.string.contactsSelect_leftBarButtonItem_navTitle,
				new MyTalkingGroupBarButtonItemOnClickListener());
		_fmSwitch2ContactsSelectLeftBarButtonItem = new SimpleIMeetingBarButtonItem(
				this, BarButtonItemStyle.RIGHT_GO,
				R.string.myTalkingGroup_leftBarButtonItem_navTitle,
				new AddressBookContactsBarButtonItemOnClickListener());

		//
		_fmAboutInfoImageBarButtonItem = new SimpleIMeetingImageBarButtonItem(
				this, android.R.drawable.ic_dialog_info,
				BarButtonItemStyle.RIGHT_GO,
				new MoreMenuImageBarButtonItemOnClickListener());

		// set navigation bar
		setMainActivityNavigationBar();
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
			// get my talking groups' parent view
			ViewParent _myTalkingGroupsParentView = _fmMyTalkingGroupsView
					.getParent();

			// check my talking groups' parent view
			if (null != _myTalkingGroupsParentView) {
				// remove my talking group list view from its parent view
				((ViewGroup) _myTalkingGroupsParentView)
						.removeView(_fmMyTalkingGroupsView);
			}

			_contentView = _fmMyTalkingGroupsView;
		}
			break;

		default:
		case ADDRESSBOOK_CONTACTS: {
			// get contacts select's parent view
			ViewParent _contactsSelectParentView = _fmContactsSelectView
					.getParent();

			// check contacts select's parent view
			if (null != _contactsSelectParentView) {
				// remove contacts select view from its parent view
				((ViewGroup) _contactsSelectParentView)
						.removeView(_fmContactsSelectView);
			}

			_contentView = _fmContactsSelectView;
		}
			break;
		}

		// check content view and set content view
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

	// set main activity navigation bar
	private void setMainActivityNavigationBar() {
		// set title
		// check tap to generate new talking group title textView
		if (null == _mTap2GenNewTalkingGroupTitleTextView) {
			// init title textView
			_mTap2GenNewTalkingGroupTitleTextView = new TextView(this);

			// set title textView text, appearance and color
			_mTap2GenNewTalkingGroupTitleTextView
					.setText(R.string.tap2generateNewTalkingGroup_title);
			_mTap2GenNewTalkingGroupTitleTextView.setTextAppearance(this,
					android.R.attr.textAppearanceLarge);
			_mTap2GenNewTalkingGroupTitleTextView.setTextColor(Color.WHITE);

			// set title on touch listener
			_mTap2GenNewTalkingGroupTitleTextView
					.setOnTouchListener(new TitleOnTouchListener());
		}

		setTitle(R.string.tap2generateNewTalkingGroup_title);

		// check main view type and set left bar button item
		switch (_mMainViewType) {
		case MY_TALKINGGROUP_LIST:
			setLeftBarButtonItem(_fmSwitch2ContactsSelectLeftBarButtonItem);
			break;

		default:
		case ADDRESSBOOK_CONTACTS:
			setLeftBarButtonItem(_fmSwitch2MyTalkingGroupsLeftBarButtonItem);
			break;
		}

		// set right image bar button item
		setRightBarButtonItem(_fmAboutInfoImageBarButtonItem);
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
			setMainActivityNavigationBar();
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
			setMainActivityNavigationBar();
		}

	}

	// more menu image bar button item on click listener
	class MoreMenuImageBarButtonItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// go to about activity
			pushActivity(AboutActivity.class);
		}

	}

}
