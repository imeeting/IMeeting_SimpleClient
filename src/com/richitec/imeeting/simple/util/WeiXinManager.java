package com.richitec.imeeting.simple.util;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.richitec.imeeting.simple.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class WeiXinManager {
	private static IWXAPI api;
	public final static String APP_ID= "wx1d17bdecb3bc2b13";//"wx6d0e1dd1ed644f48";"wxa0a0756f13fea27e"
	private static Context context;
	
	public static void createWeiXinAPI(Context c){
		api = WXAPIFactory.createWXAPI(c, APP_ID, false);
		context = c;
	}
	
	public static boolean registWeiXin(Context contex){
		if(api!=null&&isInstallWeiXin())
			return api.registerApp(APP_ID); 
		else
			return false;
	}
	
	public static boolean isInstallWeiXin(){
		if(api!=null){
			Log.d("is install",api.isWXAppInstalled()+"");
			return api.isWXAppInstalled();
			
		}
		return false;
	}
	
	public static boolean sendInviteMessage(String groupId){
		if(api!=null&&isInstallWeiXin()){
			String text = String.format(context.getString(R.string.email_body),groupId);
			
			// ��ʼ��һ��WXTextObject����
			WXTextObject textObj = new WXTextObject();
			textObj.text = text;
	
			// ��WXTextObject�����ʼ��һ��WXMediaMessage����
			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = textObj;
			// �����ı����͵���Ϣʱ��title�ֶβ�������
			// msg.title = "Will be ignored";
			msg.description = text;
			
			// ����һ��Req
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("text"); // transaction�ֶ�����Ψһ��ʶһ������
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneSession;
			
			// ����api�ӿڷ������ݵ�΢��
			api.sendReq(req);
			return true;
		}
		else{
			return false;
		}
	}
	
	public static void unregistWeiXin(){
		if(api!=null&&isInstallWeiXin())
			api.unregisterApp();
	}
	
	 private static String buildTransaction(final String type) {
			return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
		}
	 
	 public static void bindAppAndWeiXin(Intent t1,IWXAPIEventHandler t2){
		 if(api!=null&&isInstallWeiXin())
			 api.handleIntent(t1, t2);
	 }
}
