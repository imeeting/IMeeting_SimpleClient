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

	// my talking groups info array
	private JSONArray _mMyTalkingGroupsInfoArray = new JSONArray();

	// my talking group list view
	private ListView _mMyTalkingGroupListView;

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
							"{\"startedtime\":\"136557880\", \"id\":\"123456\", \"status\":\"opened\"}"));
			_mMyTalkingGroupsInfoArray
					.put(new JSONObject(
							"{\"startedtime\":\"1365579420\", \"id\":\"452316\", \"status\":\"unopened\"}"));
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
									R.id.my_talkinggroup_startedDate,
									R.id.my_talkinggroup_startedTime,
									R.id.my_talkinggroup_groupId,
									R.id.my_talkinggroup_groupStatus }));

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
					.getLongFromJSONObject(_groupInfoJsonObject, "startedtime");
			Object _startedDate = getContext().getResources().getString(
					R.string.myTalkingGroup_startedDate_hint)
					+ _myTalkingGroupStartedDateDateFormat
							.format(_groupStartedTimestamp);
			Object _startedTime = getContext().getResources().getString(
					R.string.myTalkingGroup_startedTime_hint)
					+ _myTalkingGroupStartedTimeDateFormat
							.format(_groupStartedTimestamp);
			Object _groupId = getContext().getResources().getString(
					R.string.myTalkingGroup_groupId_hint)
					+ JSONUtils.getStringFromJSONObject(_groupInfoJsonObject,
							"id");
			Object _groupStatus = JSONUtils.getStringFromJSONObject(
					_groupInfoJsonObject, "status");

			// check my talking group status and reset my talking group started
			// date, time, group id and status
			if ("opened".equalsIgnoreCase((String) _groupStatus)) {
				ForegroundColorSpan _darkSeaGreenForegroundColorSpan = new ForegroundColorSpan(
						getContext().getResources().getColor(
								R.color.dark_seagreen));

				_startedDate = new SpannableString((String) _startedDate);
				((SpannableString) _startedDate).setSpan(
						_darkSeaGreenForegroundColorSpan, 0,
						((SpannableString) _startedDate).length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				_startedTime = new SpannableString((String) _startedTime);
				((SpannableString) _startedTime).setSpan(
						_darkSeaGreenForegroundColorSpan, 0,
						((SpannableString) _startedTime).length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				_groupId = new SpannableString((String) _groupId);
				((SpannableString) _groupId).setSpan(
						_darkSeaGreenForegroundColorSpan, 0,
						((SpannableString) _groupId).length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

				_groupStatus = new SpannableString(getContext().getResources()
						.getString(R.string.myTalkingGroup_groupStatus_hint)
						+ (String) _groupStatus);
				((SpannableString) _groupStatus).setSpan(
						_darkSeaGreenForegroundColorSpan, 0,
						((SpannableString) _groupStatus).length(),
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			} else {
				// update my talking group status
				_groupStatus = getContext().getResources().getString(
						R.string.myTalkingGroup_groupStatus_hint)
						+ _groupStatus;
			}

			// define each my talking group data map
			Map<String, Object> _dataMap = new HashMap<String, Object>();

			// set data
			_dataMap.put(GROUP_STARTEDDATE, _startedDate);
			_dataMap.put(GROUP_STARTEDTIME, _startedTime);
			_dataMap.put(GROUP_ID, _groupId);
			_dataMap.put(GROUP_STATUS, _groupStatus);

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

	// my talking group listView on item click listener
	class MyTalkingGroupListViewOnItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// update selected talking group attendee listView adapter and show
			_mMyTalkingGroupAttendeeListView.setAdapter(null);

			((RelativeLayout) findViewById(R.id.mtg_talkingGroupAttendeeList_relativeLayout))
					.setVisibility(View.VISIBLE);
		}

	}

}
