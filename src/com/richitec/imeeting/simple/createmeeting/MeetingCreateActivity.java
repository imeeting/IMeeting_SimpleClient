package com.richitec.imeeting.simple.createmeeting;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.richitec.commontoolkit.customcomponent.BarButtonItem.BarButtonItemStyle;
import com.richitec.commontoolkit.utils.HttpUtils;
import com.richitec.commontoolkit.utils.MyToast;
import com.richitec.commontoolkit.utils.HttpUtils.HttpResponseResult;
import com.richitec.commontoolkit.utils.HttpUtils.OnHttpRequestListener;
import com.richitec.commontoolkit.utils.VersionUtils;
import com.richitec.commontoolkit.utils.HttpUtils.HttpRequestType;
import com.richitec.commontoolkit.utils.HttpUtils.PostRequestFormat;
import com.richitec.imeeting.simple.R;
import com.richitec.imeeting.simple.assistant.SettingActivity;
import com.richitec.imeeting.simple.constants.TalkGroup;
import com.richitec.imeeting.simple.customcomponent.IMeetingBarButtonItem;
import com.richitec.imeeting.simple.customcomponent.IMeetingNavigationActivity;
import com.richitec.imeeting.simple.talkinggroup.TalkingGroupActivity;

public class MeetingCreateActivity extends IMeetingNavigationActivity {
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set content view
		setContentView(R.layout.create_meeting_activity_layout);
		
		this.setLeftBarButtonItem(new IMeetingBarButtonItem(this,
				BarButtonItemStyle.RIGHT_GO, R.string.setting_nav_btn_title,
				new SettingBtnOnClickListener()));

		// set title text
		setTitle(R.string.create_meeting_title);
		
		Button createButton = (Button) findViewById(R.id.create_meeting_button);
		createButton.setOnClickListener(new CreateBtnOnClickListener());
		
	}
	
	class CreateBtnOnClickListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			progressDialog = ProgressDialog.show(MeetingCreateActivity.this, null, getString(R.string.sending_request));
			HttpUtils.postSignatureRequest(getString(R.string.server_url)
					+ getString(R.string.create_conf_url),
					PostRequestFormat.URLENCODED, null, null,
					HttpRequestType.ASYNCHRONOUS, onFinishedCreateGroupTalk);
			
		}
		
	} 
	
	private void dismissProgressDlg(){
		if(progressDialog!=null)
			progressDialog.dismiss();
	}
	
	private OnHttpRequestListener onFinishedCreateGroupTalk = new OnHttpRequestListener(){

		@Override
		public void onFinished(HttpResponseResult responseResult) {
			// TODO Auto-generated method stub
			dismissProgressDlg();
			try {
				JSONObject data = new JSONObject(responseResult.getResponseText());
				String groupId = data.getString(TalkGroup.conferenceId.name());
				String owner = data.getString(TalkGroup.owner.name());

				Intent intent = new Intent(MeetingCreateActivity.this,TalkingGroupActivity.class);
				intent.putExtra(TalkGroup.conferenceId.name(), groupId);
				intent.putExtra(TalkGroup.owner.name(), owner);
				
				startActivity(intent);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void onFailed(HttpResponseResult responseResult) {
			// TODO Auto-generated method stub
			dismissProgressDlg();
			switch (responseResult.getStatusCode()) {
			case 402:
				MyToast.show(MeetingCreateActivity.this,
						R.string.payment_required, Toast.LENGTH_SHORT);
				break;

			default:
				MyToast.show(MeetingCreateActivity.this,
						R.string.error_in_create_group, Toast.LENGTH_SHORT);
				break;
			}
		}
		
	};
	
	class SettingBtnOnClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			pushActivity(SettingActivity.class);
		}
		
	}
	
	public void onBackPressed(){
		new AlertDialog.Builder(this)
			.setTitle(R.string.alert_title)
			.setMessage(R.string.exit_app)
			.setNegativeButton(R.string.cancel, null)
			.setPositiveButton(R.string.exit,
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							System.exit(0);
						}
					}
					).show();
	}
}
