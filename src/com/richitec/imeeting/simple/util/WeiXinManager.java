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
	public final static String APP_ID= "wx3e0b8398d893e9d1";
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
			
			// 初始化一个WXTextObject对象
			WXTextObject textObj = new WXTextObject();
			textObj.text = text;
	
			// 用WXTextObject对象初始化一个WXMediaMessage对象
			WXMediaMessage msg = new WXMediaMessage();
			msg.mediaObject = textObj;

			msg.description = text;
			
			// 构造一个Req
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
			req.message = msg;
			req.scene = SendMessageToWX.Req.WXSceneSession;
			
			// 调用api接口发送数据到微信
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
