package com.richitec.imeeting.simple.talkinggroup;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.richitec.commontoolkit.addressbook.AddressBookManager;
import com.richitec.commontoolkit.user.UserManager;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.MyToast;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.imeeting.simple.R;
import com.richitec.imeeting.simple.constants.Attendee;
import com.richitec.imeeting.simple.constants.Notify;
import com.richitec.imeeting.simple.constants.SystemConstants;
import com.richitec.imeeting.simple.constants.TalkGroup;
import com.richitec.imeeting.simple.talkinggroup.adapter.MemberListAdapter;
import com.richitec.imeeting.simple.talkinggroup.statusfilter.AttendeeModeStatusFilter;
import com.richitec.imeeting.simple.talkinggroup.statusfilter.OwnerModeStatusFilter;
import com.richitec.imeeting.simple.util.WeiXinManager;
import com.richitec.websocket.notifier.NotifierCallbackListener;
import com.richitec.websocket.notifier.WebSocketNotifier;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public class TalkingGroupActivity extends Activity{
	private PullToRefreshListView memberListView;
	private MemberListAdapter memberListAdapter;
	private WebSocketNotifier notifier;
	private ProgressDialog progressDialog;
	private String groupId; 
	private String owner;
	private Handler handler;
	private Timer timer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.activity_talking_group);
		handler = new Handler(Looper.myLooper());
		
		//����΢��API
		WeiXinManager.createWeiXinAPI(this);
		
		Bundle data = getIntent().getExtras();
		groupId = data.getString(TalkGroup.conferenceId.name());
		owner = data.getString(TalkGroup.owner.name());
		
		initUI();

		notifier = new WebSocketNotifier();
		notifier.setServerAddress(getString(R.string.notify_url));
		notifier.setSubscriberID(UserManager.getInstance().getUser().getName());
		notifier.setTopic(groupId);
		notifier.setNotifierActionListener(notifyCallbackListener);
		notifier.connect();
		
		timer = new Timer();
		timer.schedule(new HeartBeatTimerTask(), 10000, 10000);
	}
	
	private void initUI(){
		// set title text
		String title = getString(R.string.talking_group_title) + groupId;
		((TextView)findViewById(R.id.conf_text_title)).setText(title);
		//setTitle(title);
		
		memberListView = (PullToRefreshListView) findViewById(R.id.gt_memberlist);
		memberListAdapter = new MemberListAdapter(this);
		memberListView.getRefreshableView().setAdapter(memberListAdapter);
		memberListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				refreshMemberList();
			}
		});
		
		if (isOwner()) {
			memberListAdapter.setStatusFilter(new OwnerModeStatusFilter());
		} else {
			memberListAdapter.setStatusFilter(new AttendeeModeStatusFilter());
		}
		
		refreshMemberList();
	}
	
	class HeartBeatTimerTask extends TimerTask {
		private int timeoutCount = 0;

		@Override
		public void run() {
			// send heart beat to server
			HashMap<String, String> params = new HashMap<String, String>();
			params.put(TalkGroup.conferenceId.name(), groupId);
			HttpUtils.postSignatureRequest(getString(R.string.server_url)
					+ getString(R.string.heart_beat_url),
					PostRequestFormat.URLENCODED, params, null,
					HttpRequestType.ASYNCHRONOUS, onHearBeatReturn);
		}

		private OnHttpRequestListener onHearBeatReturn = new OnHttpRequestListener() {

			@Override
			public void onFinished(HttpResponseResult responseResult) {
				Log.d(SystemConstants.TAG, "onHeartBeatReturn - code: " + responseResult.getStatusCode());
				timeoutCount--;
				if (timeoutCount > 0) {
					refreshMemberList();
				} else {
					timeoutCount = 0;
				}
				
			}

			@Override
			public void onFailed(HttpResponseResult responseResult) {
				Log.d(SystemConstants.TAG, "onHearBeatReturn: code: "
						+ responseResult.getStatusCode());
				if (responseResult.getStatusCode() == -1) {
					timeoutCount++;
					if (timeoutCount == 2) {
						// set all members as offline
						memberListAdapter.setAllMemberOffline();
					}
					if (timeoutCount <= 4 && timeoutCount >= 2) {
						MyToast.show(TalkingGroupActivity.this,
								R.string.network_error, Toast.LENGTH_SHORT);
					}
					if (timeoutCount > 4) {
						timeoutCount = 4;
					}
				}
			}
		};
	}
	
	private NotifierCallbackListener notifyCallbackListener = new NotifierCallbackListener() {

		@Override
		public void doAction(String event, JSONObject data) {
			Log.d(SystemConstants.TAG, "NotifierCallbackListener - doAction: "
					+ data.toString());
			if (event.equals(Notify.notice.name())) {
				// process notice message
				try {
					String cmd = data.getString(Notify.cmd.name());
					final JSONArray noticeList = data
							.getJSONArray(Notify.notice_list.name());
					if (cmd.equals(Notify.notify.name())
							|| cmd.equals(Notify.cache.name())) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								for (int i = 0; i < noticeList.length(); i++) {
									// process one notice message
									try {
										processOneNotice(noticeList
												.getJSONObject(i));
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

							}
						});
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}
	};
	
	private void processOneNotice(JSONObject notice) throws JSONException {
		String action = notice.getString(Notify.action.name());
		String groupId = notice.getString(TalkGroup.conferenceId.name());
		if (this.groupId.equals(groupId)) {
			if (Notify.Action.update_status.name().equals(action)) {
				JSONObject attendee = notice.getJSONObject(Attendee.attendee
						.name());
				memberListAdapter.updateMember(attendee);
			} else if (Notify.Action.update_attendee_list.name().equals(action)) {
				refreshMemberList();
			}
		}
	}
	
	public void leaveGroupTalk(){
		timer.cancel();
		notifier.disconnect();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TalkGroup.conferenceId.name(), groupId);
		HttpUtils.postSignatureRequest(getString(R.string.server_url)
				+ getString(R.string.unjoin_conf_url),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, null);
		TalkingGroupActivity.this.finish();
	}
	
	private void closeGroupTalk() {
		progressDialog = ProgressDialog.show(TalkingGroupActivity.this, null,
				getString(R.string.destroying_talkgroup));
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TalkGroup.conferenceId.name(), groupId);
		HttpUtils.postSignatureRequest(getString(R.string.server_url)
				+ getString(R.string.destroy_conf_url),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishedCloseGroupTalk);
	}

	private OnHttpRequestListener onFinishedCloseGroupTalk = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			onCloseGroupTalkRequestReturn();
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			onCloseGroupTalkRequestReturn();
		}
	};
	
	private void onCloseGroupTalkRequestReturn() {
		dismissProgressDlg();
		timer.cancel();
		notifier.disconnect();
		TalkingGroupActivity.this.finish();
	}
	
	private void dismissProgressDlg(){
		if(progressDialog!=null)
			progressDialog.dismiss();
	}
	
	private boolean isOwner(){
		String username = UserManager.getInstance().getUser().getName();
		if(username.equals(owner))
			return true;
		return false;
	}
	
	private void refreshMemberList(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TalkGroup.conferenceId.name(), groupId);
		HttpUtils.postSignatureRequest(getString(R.string.server_url)
				+ getString(R.string.get_attendee_list_url),
				PostRequestFormat.URLENCODED, params, null,
				HttpRequestType.ASYNCHRONOUS, onFinishedGetMemberList);
	}
	private OnHttpRequestListener onFinishedGetMemberList = new OnHttpRequestListener() {

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			memberListView.onRefreshComplete();

			try {
				JSONArray attendees = new JSONArray(
						responseResult.getResponseText());
				memberListAdapter.setData(attendees);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			memberListView.onRefreshComplete();

		}
	};
	
	public void onSmsInviteAction(View v) {	
		//check whether surpport telephone service
		if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)){
			Uri uri = Uri.parse("smsto:" + "");
			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			String smsBody = String.format(getString(R.string.invite_sms_body),
					groupId);
			intent.putExtra("sms_body", smsBody);
			startActivity(intent);
		}
		else
			Toast.makeText(this, getString(R.string.sms_not_surpport), Toast.LENGTH_SHORT).show();
	}
	
	public void onLeaveAction(View v){
		new AlertDialog.Builder(this)
		.setTitle(R.string.select_operation)
		.setItems(R.array.leave_talkgroup_menu,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						switch (which) {
						case 0:
							leaveGroupTalk();
							break;
						case 1:
							closeGroupTalk();
							break;
						default:
							break;
						}

					}
				}).show();
	}
	
	public void onEmailInviteAction(View v){
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, new String[]{});
		String userName = UserManager.getInstance().getUser().getName();
		String inviteName = AddressBookManager.getInstance().
						getContactsDisplayNamesByPhone(userName).get(0);
		String emailbody = String.format(getString(R.string.email_body),groupId);

		intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.email_subject) );
		intent.putExtra(Intent.EXTRA_TEXT, inviteName + emailbody);
		intent.setType("plain/text");
		startActivity(intent);
		
	}
	
	public void onWeixinAction(View v){
		//����΢����Ϣ
		if(!WeiXinManager.sendInviteMessage(groupId)){
			Toast.makeText(this, getString(R.string.weixin_not_install), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onBackPressed(){
		onLeaveAction(null);
	}
	
	public void onDialAction(View v){
		//check whether surpport telephone service
		if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)){
			String phone = getString(R.string.call_number);
			Uri uri = Uri.parse("tel:" + phone);
			Intent intent = new Intent(Intent.ACTION_CALL,uri);
			startActivity(intent);
		}
		else
			Toast.makeText(this, getString(R.string.phone_not_surpport), Toast.LENGTH_SHORT).show();
	}
}
