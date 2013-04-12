package com.richitec.simpleimeeting.talkinggroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.richitec.commontoolkit.customadapter.CTListAdapter;
import com.richitec.commontoolkit.utils.JSONUtils;
import com.richitec.simpleimeeting.R;
import com.richitec.simpleimeeting.view.SIMBaseView;

public class MyTalkingGroupsView extends SIMBaseView {

	private static final String LOG_TAG = MyTalkingGroupsView.class
			.getCanonicalName();

	// my talking group listView item adapter data keys
	private final String GROUP_STARTEDDATE = "group_startedDate";
	private final String GROUP_STARTEDTIME = "group_startedTime";
	private final String GROUP_ID = "group_id";
	private final String GROUP_STATUS = "group_status";

	// my talking group attendee listView item adapter data keys
	private final String ATTENDEE_NAME = "attendee_name";
	private final String ATTENDEE_STATUS = "attendee_status";

	// my talking groups info array
	private JSONArray _mMyTalkingGroupsInfoArray = new JSONArray();

	// my talking group list view
	private ListView _mMyTalkingGroupListView;

	// selected talking group index
	private Integer _mSelectedTalkingGroupIndex = 0;

	// my talking group attendees info array
	private JSONArray _mMyTalkingGroupAttendeesInfoArray = new JSONArray();

	// my talking group attendee list view
	private ListView _mMyTalkingGroupAttendeeListView;

	@Override
	public int presentViewLayout() {
		// return my talking group list view layout
		return R.layout.my_talkinggroups_view_layout;
	}

	@Override
	public void initSubComponents() {
		// test by ares
		try {
			// set my talking groups info array
			_mMyTalkingGroupsInfoArray
					.put(new JSONObject(
							"{\"startedtimestamp\":\"1365739200\", \"id\":\"123456\", \"status\":\"opened\"}"));
			_mMyTalkingGroupsInfoArray
					.put(new JSONObject(
							"{\"startedtimestamp\":\"1366430400\", \"id\":\"452316\", \"status\":\"unopened\"}"));

			// set my talking group attendees info array
			_mMyTalkingGroupAttendeesInfoArray.put(new JSONObject(
					"{\"phone\":\"18001582338\", \"status\":\"in\"}"));
			_mMyTalkingGroupAttendeesInfoArray.put(new JSONObject(
					"{\"phone\":\"13813005146\", \"status\":\"out\"}"));
			_mMyTalkingGroupAttendeesInfoArray.put(new JSONObject(
					"{\"phone\":\"18652970325\", \"status\":\"in\"}"));
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Set my talking groups info array for test error");

			e.printStackTrace();
		}

		// check my talking groups info array
		if (0 != _mMyTalkingGroupsInfoArray.length()) {
			// hide no talking group tip textView and show my talking groups
			((TextView) findViewById(R.id.mtg_noTalkingGroup_tip_textView))
					.setVisibility(View.GONE);
			((LinearLayout) findViewById(R.id.mtg_myTalkingGroup_linearLayout))
					.setVisibility(View.VISIBLE);

			// get my talking group list view
			_mMyTalkingGroupListView = (ListView) findViewById(R.id.mtg_talkingGroupListView);

			// set my talking group listView adapter
			_mMyTalkingGroupListView
					.setAdapter(new MyTalkingGroup7MyTalkingGroupAttendeeAdapter(
							getContext(),
							generateMyTalkingGroupListDataList(_mMyTalkingGroupsInfoArray),
							R.layout.my_talkinggroup_layout, new String[] {
									GROUP_STARTEDDATE, GROUP_STARTEDTIME,
									GROUP_ID, GROUP_STATUS }, new int[] {
									R.id.my_talkingGroup_startedDate,
									R.id.my_talkingGroup_startedTime,
									R.id.my_talkingGroup_groupId,
									R.id.my_talkingGroup_groupStatus }));

			// set my talking group listView on item click listener
			_mMyTalkingGroupListView
					.setOnItemClickListener(new MyTalkingGroupListViewOnItemClickListener());

			// get my talking group attendee list view
			_mMyTalkingGroupAttendeeListView = (ListView) findViewById(R.id.mtg_talkingGroupAttendeeListView);
		} else {
			Log.i(LOG_TAG, "There is no talking group with me now");
		}
	}

	// generate my talking group listView adapter data list
	private List<Map<String, ?>> generateMyTalkingGroupListDataList(
			JSONArray myTalkingGroupsInfoArray) {
		// my talking group started date and time date format, format unix
		// timeStamp
		final DateFormat _myTalkingGroupStartedDateDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd", Locale.getDefault());
		final DateFormat _myTalkingGroupStartedTimeDateFormat = new SimpleDateFormat(
				"HH:mm", Locale.getDefault());

		// my talking group list data list
		List<Map<String, ?>> _dataList = new ArrayList<Map<String, ?>>();

		// generate data
		for (int i = 0; i < myTalkingGroupsInfoArray.length(); i++) {
			// get group info json object
			JSONObject _groupInfoJsonObject = JSONUtils
					.getJSONObjectFromJSONArray(myTalkingGroupsInfoArray, i);

			// get group started timestamp, date, time, id and status
			Long _groupStartedTimestamp = 1000 * JSONUtils
					.getLongFromJSONObject(
							_groupInfoJsonObject,
							getContext()
									.getResources()
									.getString(
											R.string.bg_server_myTalkingGroup_startedTimestamp));
			Object _groupStartedDate = getContext().getResources().getString(
					R.string.myTalkingGroup_startedDate_hint)
					+ _myTalkingGroupStartedDateDateFormat
							.format(_groupStartedTimestamp);
			Object _groupStartedTime = getContext().getResources().getString(
					R.string.myTalkingGroup_startedTime_hint)
					+ _myTalkingGroupStartedTimeDateFormat
							.format(_groupStartedTimestamp);
			Object _groupId = getContext().getResources().getString(
					R.string.myTalkingGroup_groupId_hint)
					+ JSONUtils.getStringFromJSONObject(
							_groupInfoJsonObject,
							getContext().getResources().getString(
									R.string.bg_server_myTalkingGroup_id));
			Object _groupStatus = JSONUtils.getStringFromJSONObject(
					_groupInfoJsonObject,
					getContext().getResources().getString(
							R.string.bg_server_myTalkingGroup_status));

			// check my talking group status and reset my talking group started
			// date, time, group id and status
			if (getContext()
					.getResources()
					.getString(
							R.string.bg_server_myTalkingGroup_talkingGroupOpened)
					.equalsIgnoreCase((String) _groupStatus)) {
				// dark sea green foreground color span
				ForegroundColorSpan _darkSeaGreenForegroundColorSpan = new ForegroundColorSpan(
						getContext().getResources().getColor(
								R.color.dark_seagreen));

				_groupStartedDate = new SpannableString(
						(String) _groupStartedDate);
				((SpannableString) _groupStartedDate).setSpan(
						_darkSeaGreenForegroundColorSpan, 0,
						((SpannableString) _groupStartedDate).length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				_groupStartedTime = new SpannableString(
						(String) _groupStartedTime);
				((SpannableString) _groupStartedTime).setSpan(
						_darkSeaGreenForegroundColorSpan, 0,
						((SpannableString) _groupStartedTime).length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				_groupId = new SpannableString((String) _groupId);
				((SpannableString) _groupId).setSpan(
						_darkSeaGreenForegroundColorSpan, 0,
						((SpannableString) _groupId).length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				_groupStatus = new SpannableString(getContext().getResources()
						.getString(R.string.myTalkingGroup_groupStatus_hint)
						+ getContext().getResources().getString(
								R.string.myTalkingGroup_groupStatus_opened));
				((SpannableString) _groupStatus).setSpan(
						_darkSeaGreenForegroundColorSpan, 0,
						((SpannableString) _groupStatus).length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				// update my talking group status
				_groupStatus = getContext().getResources().getString(
						R.string.myTalkingGroup_groupStatus_hint)
						+ getContext().getResources().getString(
								R.string.myTalkingGroup_groupStatus_unopened);
			}

			// define each my talking group data map
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// set data
			_dataMap.put(GROUP_STARTEDDATE, _groupStartedDate);
			_dataMap.put(GROUP_STARTEDTIME, _groupStartedTime);
			_dataMap.put(GROUP_ID, _groupId);
			_dataMap.put(GROUP_STATUS, _groupStatus);

			// add to data list
			_dataList.add(_dataMap);
		}

		return _dataList;
	}

	// generate my talking group attendee listView adapter data list
	private List<Map<String, ?>> generateMyTalkingGroupAttendeeListDataList(
			JSONArray myTalkingGroupAttendeesInfoArray) {
		// my talking group attendee list data list
		List<Map<String, ?>> _dataList = new ArrayList<Map<String, ?>>();

		// generate data
		for (int i = 0; i < myTalkingGroupAttendeesInfoArray.length(); i++) {
			// get attendee info json object
			JSONObject _attendeeInfoJsonObject = JSONUtils
					.getJSONObjectFromJSONArray(
							myTalkingGroupAttendeesInfoArray, i);

			// get attendee status and phone
			Object _attendeeStatus = JSONUtils.getStringFromJSONObject(
					_attendeeInfoJsonObject,
					getContext().getResources().getString(
							R.string.bg_server_talkingGroupAttendee_status));
			Object _attendeePhone = JSONUtils.getStringFromJSONObject(
					_attendeeInfoJsonObject,
					getContext().getResources().getString(
							R.string.bg_server_talkingGroupAttendee_phone));

			// check talking group attendee status and reset talking group
			// attendee status and name
			if (getContext()
					.getResources()
					.getString(
							R.string.bg_server_talkingGroupAttendee_attendeeIn)
					.equalsIgnoreCase((String) _attendeeStatus)) {
				_attendeeStatus = getContext().getResources().getDrawable(
						android.R.drawable.presence_online);

				// dark sea green foreground color span
				ForegroundColorSpan _darkSeaGreenForegroundColorSpan = new ForegroundColorSpan(
						getContext().getResources().getColor(
								R.color.dark_seagreen));

				_attendeePhone = new SpannableString((String) _attendeePhone);
				((SpannableString) _attendeePhone).setSpan(
						_darkSeaGreenForegroundColorSpan, 0,
						((SpannableString) _attendeePhone).length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else if (getContext()
					.getResources()
					.getString(
							R.string.bg_server_talkingGroupAttendee_attendeeOut)
					.equalsIgnoreCase((String) _attendeeStatus)) {
				_attendeeStatus = Boolean.valueOf(true);

				//
			}

			// define each my talking group data map
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// set data
			_dataMap.put(ATTENDEE_STATUS, _attendeeStatus);
			_dataMap.put(ATTENDEE_NAME, _attendeePhone);

			// add to data list
			_dataList.add(_dataMap);
		}

		return _dataList;
	}

	// inner class
	// my talking group and my talking group attendee adapter
	class MyTalkingGroup7MyTalkingGroupAttendeeAdapter extends CTListAdapter {

		public MyTalkingGroup7MyTalkingGroupAttendeeAdapter(Context context,
				List<Map<String, ?>> data, int itemsLayoutResId,
				String[] dataKeys, int[] itemsComponentResIds) {
			super(context, data, itemsLayoutResId, dataKeys,
					itemsComponentResIds);
		}

		@Override
		protected void bindView(View view, Map<String, ?> dataMap,
				String dataKey) {
			// get item data object
			Object _itemData = dataMap.get(dataKey);

			// check view type
			// textView
			if (view instanceof TextView) {
				// set view text
				((TextView) view)
						.setText(null == _itemData ? ""
								: _itemData instanceof SpannableString ? (SpannableString) _itemData
										: _itemData.toString());
			}
			// my talking group detail info or my talking group attendee status
			// imageView
			else if (view instanceof ImageView) {
				// check item data type
				if (_itemData instanceof Boolean) {
					// define item data boolean and convert item data to
					// boolean
					Boolean _itemDataBoolean = (Boolean) _itemData;

					// set imageView visibility
					if (false == _itemDataBoolean) {
						((ImageView) view).setVisibility(View.GONE);
					}
				} else {
					try {
						// define item data drawable and convert item data to
						// drawable
						Drawable _itemDataDrawable = (Drawable) _itemData;

						// set imageView image
						((ImageView) view).setImageDrawable(_itemDataDrawable);
					} catch (Exception e) {
						e.printStackTrace();

						Log.e(LOG_TAG,
								"Convert item data to drawable error, item data = "
										+ _itemData);
					}
				}
			}
		}

	}

	// my talking group listView on item click listener
	class MyTalkingGroupListViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// update selected talking group attendee listView adapter
			_mMyTalkingGroupAttendeeListView
					.setAdapter(new MyTalkingGroup7MyTalkingGroupAttendeeAdapter(
							getContext(),
							generateMyTalkingGroupAttendeeListDataList(_mMyTalkingGroupAttendeesInfoArray),
							R.layout.talkinggroup_attendee_layout,
							new String[] { ATTENDEE_STATUS, ATTENDEE_NAME },
							new int[] {
									R.id.talkingGroup_attendee_status_imageView,
									R.id.talkingGroup_attendee_displayName_textView }));

			// get my talking group attendee listView relativeLayout
			RelativeLayout _myTalkingGroupAttendeeListViewRelativeLayout = (RelativeLayout) findViewById(R.id.mtg_talkingGroupAttendeeList_relativeLayout);

			// check my talking group attendee listView relativeLayout
			// visibility and show it
			if (View.VISIBLE != _myTalkingGroupAttendeeListViewRelativeLayout
					.getVisibility()) {
				_myTalkingGroupAttendeeListViewRelativeLayout
						.setVisibility(View.VISIBLE);
			}
		}

	}

}
