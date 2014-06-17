package com.vnow.sdk.openapi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.view.Surface;

import com.nyist.vnow.R;
import com.nyist.vnow.core.VNowApplication;
import com.nyist.vnow.event.Notify;
import com.nyist.vnow.event.Request;
import com.nyist.vnow.event.Response;
import com.nyist.vnow.struct.Room;
import com.nyist.vnow.struct.User;
import com.nyist.vnow.utils.CommonUtil;
import com.vnow.sdk.framework.IVNowFramework;

public class IVNowAPI {
	private final String TAG = "IVNowAPI";
	private static String mSessionID;
	static private IVNowFramework mIVNowFramework = null;
	static private ArrayList<EventListener> mListenerList = new ArrayList<EventListener>();
	static private VNFrameworkEventListener mVNFrameworkEventListener = new VNFrameworkEventListener();
	private Context mContext; 
	private IVNowAPI() {
		
	}

	static public IVNowAPI createIVNowAPI() {
		if (mIVNowFramework == null) {
			mIVNowFramework = new IVNowFramework();
			mIVNowFramework.setEventListener(mVNFrameworkEventListener);
		}
		return new IVNowAPI();
	}
	

	public void init(Context context) {
		Log.i(TAG, "init");
		mIVNowFramework.init(context);
		mContext = context;
	}
	public void setServerIP(String server){
		
	}
	public void deInit(Context context) {
		Log.i(TAG, "deInit");
		mIVNowFramework.deInit(context);
	}

	public void coreExit(Context context) {
		Log.i(TAG, "coreExit");
		mIVNowFramework.coreExit(context);
	}

	public void setEventListener(EventListener listener) {
		Log.i(TAG, "setEventListener");
		if(null!=listener)
			mListenerList.add(listener);
	}

	public void removeEventListener(EventListener listener) {
		Log.i(TAG, "setEventListener");
		mListenerList.remove(listener);
	}

	// interface for media server
	public int dispatchApi(String strPhone,String pwd) {
		String strCmd = "<root><Info DispType=\"userlogin\" OpSeq=1 SvrAddr="+CommonUtil._svrAddr+" UserID="+strPhone +" Psw="+pwd+"/></root>";
		return mIVNowFramework.dispatchApi(strCmd);
	}
	
	public int p2pCall(String callID) {
		String strCmd = "<root><Info DispType=\"p2pcallout\" OpSeq=2 CallSeq=100 CalledID="+callID+"/></root>";
		return mIVNowFramework.dispatchApi(strCmd);
	}
	
	public int p2pAnswer(){
		String strCmd = "<root><Info DispType=\"answercall\" OpSeq=3 CallSeq=100 /></root>";
		return mIVNowFramework.dispatchApi(strCmd);
	}
	
	public int p2pHangup(){
		String strCmd = "<root><Info DispType=\"hungupcall\" OpSeq=4 CallSeq=100 /></root>";
		return mIVNowFramework.dispatchApi(strCmd);
	}
	
	public int uploadCapture(){
		String strCmd = "<root><Info DispType=\"takepicture\" /></root>";
		return mIVNowFramework.dispatchApi(strCmd);
	}

	public int vdoStartRecode(){
		String strCmd = "<root><Info DispType=\"startrecordlocalav\" ChnIdx=\"0\" /></root>";
		return mIVNowFramework.dispatchApi(strCmd);
	}

	public int vdoStopRecode(){
		String strCmd = "<root><Info DispType=\"stoprecordlocalav\" ChnIdx=\"0\" /> </root>";
		return mIVNowFramework.dispatchApi(strCmd);
	}
	
	public int apiLogout(String strPhone){
		String strCmd = "<root><Info DispType=\"record\" /></root>";
		return mIVNowFramework.dispatchApi(strCmd);
	}
	public int setRemoteVidSurface(Surface remoteVideoSurface) {
		return mIVNowFramework.setRemoteVidSurface(remoteVideoSurface);
	}
	
	public int openLocalVideo(Surface previewSurface, String strParam) {
		return mIVNowFramework.openLocalVideo(previewSurface, strParam);
	}
	
	public int closeLocalVideo() {
		return mIVNowFramework.closeLocalVideo();
	}

	public int sendTransChannel(String DstID,String resUrl){
//		String url = CommonUtil._channelPic+resUrl+".jpg";
		String strCmd = "<root><Info DispType=\"transparent\" DstID=\"" + DstID + "\" Content=\"" + resUrl + "\"/></root>";
		return mIVNowFramework.dispatchApi(strCmd);
	}

	public int receiveTransChannel(String SrcID,String resUrl){
		String url = CommonUtil._channelPic+resUrl;
		String strCmd = "<root><Info EvtType=\"transparent\" SrcID="+SrcID+" Content="+url+" /></root>";
		return mIVNowFramework.dispatchApi(strCmd);
	}
	/**
	 * 注册用户
	 * /login/req_register/loginRegister/SafetyExit
	 * @param user
	 * @return onResponseRegister()
	 */
	public int register(User user) {
		if (mIVNowFramework != null) {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("ad_phone", user.phone));
			params.add(new BasicNameValuePair("ad_pass", user.password));
			params.add(new BasicNameValuePair("ad_name", user.name));
			params.add(new BasicNameValuePair("code", user.company_code));
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("login/req_register/loginRegister/SafetyExit.html");
			System.out.println(sBuffer.toString()+"--------------------");
			return mIVNowFramework.httpPost(sBuffer.toString(), params,
					Request.REQ_REGISTER);
		}
		return -1;
	}

	/**
	 * 登录
	 * 
	 * @param strPhone
	 *            注册手机号
	 * @param strPsw
	 *            注册密码
	 * @return onResponseLogin()
	 */
	public int login(String strPhone, String strPsw) {
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("login/").append(strPsw).append("/req_login/login/")
					.append(strPhone)
					.append("/")
					.append(CommonUtil._svrIP).append("/SafetyExit.html");
			System.out.println(sBuffer.toString());
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_LOGIN);

		}
		return -1;
	}

	/**
	 * 注销
	 * 
	 * @param user
	 * @return onResponseLogout()
	 */
	public int logout(User user) {
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("login/").append(user.password)
					.append("/req_logout/login/").append(user.phone)
					.append("/").append(user.uuid).append("/")
					.append(user.password).append(".html");
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_LOGOUT);
		}
		return -1;
	}
	
	/**
	 * 注销
	 * 
	 * @param uuid
	 * @return onResponseLogout()
	 * 
	 * userInfo/req_query_info/{uuid}
	 */
	public int getMyselfInfo(String uuid) {
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("userInfo/req_query_info/").append(uuid).append(".html;jsessionid=")
					.append(mSessionID);
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_LOGOUT);
		}
		return -1;
	}

	public int call(String strUserID) {

		return -1;
	}

	public int hangup(String strUserID) {

		return -1;
	}
	
	public int echoDelaySet(String valueOf) {
		// TODO Auto-generated method stub
		if (mIVNowFramework != null) {
			return mIVNowFramework.echoDelaySet(valueOf);
		}
		return -1;
	}

	public int startVideo(String strRoomID, Surface previewSurface,
			Surface remoteSurface) {
		if (mIVNowFramework != null) {
		}
		return -1;
	}

	public int stopVideo(String strRoomID) {

		return -1;
	}

	public int createRoom(Room room) {

		return -1;
	}

	public int modifyRoom(Room room) {

		return -1;
	}

	public int destroyRoom(Room room) {

		return -1;
	}

	public int enterRoom(Room room) {

		return -1;
	}

	public int exitRoom(Room room) {

		return -1;
	}

	public int queryMyRoom() {

		return -1;
	}

	public int queryRoomUserList(Room room) {

		return -1;
	}

	public int queryCallHistory() {
		if (mIVNowFramework != null) {
		}
		return -1;
	}

	public int muteAll(Room room) {
		return -1;
	}

	public int muteSelf(boolean bMute) {
		return -1;
	}

	public int muteOther(Room room, User user) {
		return -1;
	}

	public int inviteToRoom(User user) {

		return -1;
	}

	public int kickFromRoom(User user) {

		return -1;
	}
	
//<root><Info DispType="uploadfile" LocalFile="/mnt/sdcard/w.jpg" /></root>
	public int uploadFile(String strFilePath) {
		if (mIVNowFramework != null) {
			String strCmd = "<root><Info DispType=\"uploadfile\" LocalFile=\""+strFilePath+"\" /></root>";
			return mIVNowFramework.dispatchApi(strCmd);
		}
		return -1;
	}

	public int downloadFile(String strFileID) {

		return -1;
	}

	public int openScreenShare() {

		return -1;
	}

	public int closeScreenShare() {

		return -1;
	}

	/**
	 * 查看同事列表
	 * 
	 * @param uuid
	 *            用户32位uuid
	 * @param version
	 *            数据库版本 默认为0
	 * @param code
	 *            企业编码
	 * @return onResponseQueryColleageList()
	 */
	public int queryColleagueList(String uuid, int version, String code) {
		if (mIVNowFramework != null) {
			if(null == CommonUtil._httpUrl)
				CommonUtil.set_httpUrl(VNowApplication.the().getSetting("media_server_ip",mContext.getString(R.string.setting_defult_server_ip)));
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("coll/").append(uuid)
					.append("/req_query_colleage_list/").append(version)
					.append("/").append(code).append("/list.html;jsessionid=")
					.append(mSessionID);
			System.out.println(sBuffer.toString());
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_QUERY_COLLEAGE_LIST);
		}
		return -1;
	}

//	 /remote/file/req_upload_file
	public int httpWebUpLoad(List<NameValuePair> value){
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("file/req_upload_file").append(".html;jsessionid=")
					.append(mSessionID);
			return mIVNowFramework.httpPost(sBuffer.toString(), value,
					Request.REQ_UPLOAD_FILE_HTTP);
		}
		return -1;
	}
	/**
	 * 查看好友列表
	 * 
	 * @param uuid
	 *            用户32位uuid
	 * @param version
	 *            数据库版本 默认为0
	 * @return onResponseQueryFriendList()
	 */
	public int queryFriendList(String uuid, int version) {
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("fid/").append(uuid)
					.append("/req_query_group_list/").append(version)
					.append(".html;jsessionid=").append(mSessionID);
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_QUERY_FRIEND_LIST);
		}
		return -1;
	}

	public int queryUserInfo(String strUserID) {

		return -1;
	}

	/**
	 * 添加好友
	 * 
	 * @param phone
	 *            好友手机
	 * @param uuid
	 *            用户32位uuid
	 * @param name
	 *            好友名字
	 * @return onResponseAddFriend()
	 * fid/{phone}/req_add_friend/{uuid}/{name}
	 */
	public int addFriend(String phone, String uuid, String name) {
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("fid/").append(phone)
					.append("/req_add_friend/").append(uuid).append("/")
					.append(name).append(".html;jsessionid=")
					.append(mSessionID);
			System.out.println("addFriend-->" + sBuffer.toString());
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_ADD_FRIEND);
		}
		return -1;
	}

	/**
	 * 修改好友信息
	 * 
	 * @param phone
	 *            好友手机
	 * @param uuid
	 *            用户32位uuid
	 * @param name
	 *            好友新名字
	 * @param f_uuid
	 *            好友32位uuid
	 *            
	 *            /fid/{phone}/{uuid}/{name}/req_update_friend/{f_uuid}/setFird
	 * @return onResponseModifyFriend()
	 */
	public int modifyFriend(String phone, String uuid, String name,
			String f_uuid) {
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("fid/").append(phone).append("/").append(uuid)
					.append("/").append(name).append("/req_update_friend/")
					.append(f_uuid).append("/setFird.html;jsessionid=")
					.append(mSessionID);
			System.out.println("modifyFriend-->" + sBuffer.toString());
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_MODIFY_FRIEND);
		}

		return -1;
	}

	
	/**
	 * 修改好友信息
	 * 
	 * @param phone
	 *            好友手机
	 * @param uuid
	 *            用户32位uuid
	 * @param name
	 *            好友新名字
	 * @param f_uuid
	 *            好友32位uuid
	 *            
	 *            /fid/{phone}/{uuid}/{name}/req_update_friend/{f_uuid}/setFird
	 * @return onResponseModifyFriend()
	 */
	public int getFriendInfo(String phone, String uuid, String name,
			String f_uuid) {
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("fid/").append(phone).append("/").append(uuid)
					.append("/").append(name).append("/req_update_friend/")
					.append(f_uuid).append("/setFird.html;jsessionid=")
					.append(mSessionID);
			System.out.println("modifyFriend-->" + sBuffer.toString());
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_MODIFY_FRIEND);
		}

		return -1;
	}

	/**
	 * 删除好友
	 * 
	 * @param uuid
	 *            用户32位uuid
	 * @param phone
	 *            好友手机
	 * @param f_uuid
	 *            好友32位uuid
	 *            
	 *            /fid/{uuid}/{phone}/{f_uuid}/req_del_friend/remove
	 * @return onResponseDelFriend()
	 */
	public int delFriend(String uuid, String phone, String f_uuid) {
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("fid/").append(uuid).append("/").append(phone)
					.append("/").append(f_uuid)
					.append("/req_del_friend/remove.html;jsessionid=")
					.append(mSessionID);
			System.out.println("delFriend-->" + sBuffer.toString());
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_DEL_FRIEND);
		}

		return -1;
	}

	/**
	 * 查看群组
	 * 
	 * @param uuid
	 *            用户32位uuid
	 * @param version
	 *            数据库版本 默认为0
	 * @return onResponseQueryGroupList()
	 */
	public int queryGroupList(String uuid, int version) {
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("guser/").append(uuid)
					.append("/req_query_group_list/").append(version)
					.append("/list.html;jsessionid=").append(mSessionID);
			System.out.println("queryGroupList--->" + sBuffer.toString());
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_QUERY_GROUP_LIST);
		}
		return -1;
	}

	/**
	 * 创建群组
	 * 
	 * @param uuid
	 *            用户32位uuid
	 * @param strGroupName
	 *            群组名称
	 * @return onResponseCreateGroup()
	 */
	public int createGroup(String uuid, String strGroupName) {
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("guser/").append(uuid).append("/req_create_group/")
					.append(strGroupName).append(".html;jsessionid=")
					.append(mSessionID);
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_CREATE_GROUP);
		}
		return -1;
	}

	/**
	 * 修改群组
	 * 
	 * @param uuid
	 *            用户32位uuid
	 * @param gName
	 *            新群组名称
	 * @param g_uuid
	 *            群组32位ID
	 *            
	 *            /guser/{uuid}/req_edit_group/{name}/{g_uuid}
	 * @return onResponseModifyGroup()
	 */
	public int modifyGroup(String uuid, String gName, String g_uuid) {
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("guser/").append(uuid).append("/req_edit_group/")
					.append(gName).append("/").append(g_uuid)
					.append(".html;jsessionid=").append(mSessionID);
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_MODIFY_GROUP);
		}
		return -1;
	}

	/**
	 * 删除群组
	 * 
	 * @param uuid
	 *            用户32位uuid
	 * @param g_uuid
	 *            群组32位ID
	 *            
	 *            /guser/req_del_group/{uuid}/{g_uuid}
	 * @return onResponseDelGroup()
	 */
	public int delGroup(String uuid, String g_uuid) {
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("guser/req_del_group/").append(uuid).append("/")
					.append(g_uuid).append(".html;jsessionid=")
					.append(mSessionID);
			System.out.println("delGroup-->" + sBuffer.toString());
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_DEL_GROUP);
		}
		return -1;
	}

	public int getGroupUser(String strGroupName) {

		return -1;
	}

	/**
	 * 添加群成员
	 * 
	 * @param uuid
	 *            用户32位uuid
	 * @param g_uuid
	 *            群组32位ID
	 * @param name
	 *            成员名字
	 * @param phone
	 *            成员电话
	 *            
	 *            /guser/req_add_group_user/{uuid}/{g_uuid}/{name}/{phone}/addFguser
	 * @return onResponseAddGroupUser()
	 */
	public int addGroupUser(String uuid, String g_uuid, String name,
			String phone) {
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("guser/req_add_group_user/").append(uuid)
					.append("/").append(g_uuid).append("/").append(name)
					.append("/").append(phone)
					.append("/addFguser.html;jsessionid=").append(mSessionID);
			System.out.println("addGroupUser-->" + sBuffer.toString());
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_ADD_GROUP_USER);
		}
		return -1;
	}

	/**
	 * 删除群成员
	 * 
	 * @param phone
	 *            成员电话
	 * @param uuid
	 *            用户32位uuid
	 * @param g_uuid
	 *            群组32位ID
	 * @return onResponseDelGroupUser()
	 */
	public int delGroupUser(String phone, String uuid, String g_uuid) {
		if (mIVNowFramework != null) {
			StringBuffer sBuffer = new StringBuffer(CommonUtil._httpUrl);
			sBuffer.append("guser/").append(phone)
					.append("/req_del_group_user/").append(uuid).append("/")
					.append(g_uuid).append(".html;jsessionid=")
					.append(mSessionID);
			System.out.println("delGroupUser-->" + sBuffer.toString());
			return mIVNowFramework.httpGet(sBuffer.toString(),
					Request.REQ_DEL_GROUP_USER);
		}
		return -1;
	}

	
    private static boolean handleXMLMsg(String strMsg) {
	    		ByteArrayInputStream tInputStringStream = null;
	    		try {
	    			if (strMsg != null && !strMsg.trim().equals("")) {
	    				tInputStringStream = new ByteArrayInputStream(strMsg.getBytes());
	    			}
	    		} catch (Exception e) {
	    		   // TODO: handle exception
	    			e.printStackTrace();
	    		   return false;
	    		}
	    		
	    		XmlPullParser parser = Xml.newPullParser();
	    		try {
	    			parser.setInput(tInputStringStream, "UTF-8");
	    			int eventType = parser.getEventType();
	    			while (eventType != XmlPullParser.END_DOCUMENT) {
	    				switch (eventType) {
	    				case XmlPullParser.START_DOCUMENT:
	    					break;
	    				case XmlPullParser.START_TAG:
	    					String name = parser.getName();
	    					if (name.equalsIgnoreCase("Info")) {
	    						name = parser.getAttributeValue(null, "EvtType");
	    						if (name != null && "apistatus".equals(name)) {
	    							String status = parser.getAttributeValue(null, "Status");
	    							onResponseApiStatus(status);
	    						}else if (name != null && "loginsta".equals(name)) {
	    							
	    						}else if(name != null && "callhungup".equals(name)){
	    							onResponseHangup(name);
//	    							VNowAPI.closeLocalVideo();
	    							return true;
	    						}else if(name != null && "p2pcallrsp".equals(name)){
	    							String result = parser.getAttributeValue(null, "Result");
	    							if(!result.equals("0")){
	    								onResponseCallFailed(result);
	    							}
	    						}else if(name != null && "callanswer".equals(name)){
	    							onResponseAnswerCall(name);
	    						}else if(name != null && "p2pcallin".equals(name)){
	    							String callFrom = parser.getAttributeValue(null, "CallerID");
	    							onResponseCallIn(callFrom);
	    						}else if(name != null && "callanswer".equals(name)){
	    							
	    						}else if(name != null && "stopvidenc".equals(name)){
	    							//05-06 20:51:58.741: I/System.out(3491): processCoreEvent--><root><Info EvtType="takepicture" path="/mnt/sdcard/20140506085158.jpg"Result="0" /></root>

	    						}else if(name != null && "takepicture".equals(name)){
	    							String result = parser.getAttributeValue(null, "Result");
	    							if(result.equals("0")){
		    							String picPath = parser.getAttributeValue(null, "path");
		    							onResponseTakePicture(picPath,true);
	    							}else{
	    								onResponseTakePicture(null,false);
	    							}
	    						}else if(name != null && "recfilepath".equals(name)){
		    							String vdoPath = parser.getAttributeValue(null, "path");
		    							if(null!=vdoPath){
		    								onResponseVdoRecode(vdoPath,true);
		    							}else{
		    								onResponseVdoRecode(null,false);
		    							}
	    						}else if(name != null && "transparent".equals(name)){
	    							String contentUrl = parser.getAttributeValue(null, "Content");
	    							String srcID = parser.getAttributeValue(null, "SrcID");
	    							if(null != contentUrl){
	    								onResponseSynTransparent(contentUrl,srcID,true);
	    							}else{
	    								onResponseSynTransparent(null,null,false);
	    							}
	    							
	    						}else if(name != null && "ulfileprogress".equals(name)){
//	    							<root> <Info EvtType="ulfileprogress" Result="0" 
//	    							Handle="12884901888" Progress="100" 
//	    									FilePath="/ vnowfileul/2014/5/29/11/20592167040-3.h264" 
//	    									/> </root>
	    							String result = parser.getAttributeValue(null, "Result");
	    							if(result.equals("0")){
	    								String picHandle = parser.getAttributeValue(null, "Handle");
		    							String progress = parser.getAttributeValue(null, "Progress");
		    							String filePath = parser.getAttributeValue(null, "FilePath");
		    							onResponseConfUpLoadFile(picHandle,progress,filePath,true);
	    							}else{
	    								onResponseConfUpLoadFile(null,null,null,false);
	    							}
	    							

	    						}
	    					}
	    					if (name.equalsIgnoreCase("loginlink")) {
	    					}
	    					break;
	    				case XmlPullParser.END_TAG:
	    					break;
	    				}
	    				eventType = parser.next();
	    			}
	    			tInputStringStream.close();

	    		} catch (XmlPullParserException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    		
	    		return false;
	        }
	// ////////////////////////////////////////////////////////////////
	// event process
	
	private static void processCoreEvent(String strResult){
		System.out.println("processCoreEvent-->"+strResult);
		handleXMLMsg(strResult);
	}

	private static void processEvent(String strEvent, String strResult) {
		System.out.println(strEvent + "------>" + strResult);
		if(null == strResult)
			return;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(strResult);
			if (jsonObject == null)
				return;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (Request.REQ_REGISTER.equals(strEvent)) {
			onResponseRegister(jsonObject);
		} else if (Request.REQ_LOGIN.equals(strEvent)) {
			onResponseLogin(jsonObject);
		} else if (Response.RESPONSE_LOGOUT.equals(strEvent)) {
			onResponseLogout(jsonObject);
		} else if (Request.REQ_QUERY_COLLEAGE_LIST.equals(strEvent)) {
			onResponseQueryColleageList(strResult);
		} else if (Request.REQ_QUERY_FRIEND_LIST.equals(strEvent)) {
			onResponseQueryFriendList(strResult);
		} else if (Request.REQ_QUERY_GROUP_LIST.equals(strEvent)) {
			onResponseQueryGroupList(strResult);
		} else if (Request.REQ_CREATE_GROUP.equals(strEvent)) {
			onResponseCreateGroup(jsonObject);
		} else if (Response.RESPONSE_CREATE_ROOM.equals(strEvent)) {
			onResponseCreateRoom(jsonObject);
		} else if (Response.RESPONSE_MODIFY_ROOM.equals(strEvent)) {
			onResponseModifyRoom(jsonObject);
		} else if (Response.RESPONSE_DESTROY_ROOM.equals(strEvent)) {
			onResponseDestroyRoom(jsonObject);
		} else if (Response.RESPONSE_ENTER_ROOM.equals(strEvent)) {
			onResponseEnterRoom(jsonObject);
		} else if (Response.RESPONSE_EXIT_ROOM.equals(strEvent)) {
			onResponseExitRoom(jsonObject);
		} else if (Response.RESPONSE_MUTE_ALL.equals(strEvent)) {
			onResponseMuteAll(jsonObject);
		} else if (Response.RESPONSE_MUTE_OTHER.equals(strEvent)) {
			onResponseMuteOther(jsonObject);
		} else if (Response.RESPONSE_INVITE_TO_ROOM.equals(strEvent)) {
			onResponseInviteToRoom(jsonObject);
		} else if (Response.RESPONSE_KICK_FROM_ROOM.equals(strEvent)) {
			onResponseKickFromRoom(jsonObject);
		} else if (Response.RESPONSE_UP_LOAD_FILE.equals(strEvent)) {
			onResponseUploadFile(jsonObject);
		} else if (Notify.NOTIFY_UL_FILE_PROGRESS.equals(strEvent)) {
			onResponseUploadFileProgress(jsonObject);
		} else if (Response.RESPONSE_DOWN_LOAD_FILE.equals(strEvent)) {
			onResponseDownloadFile(jsonObject);
		} else if (Notify.NOTIFY_DL_FILE_PROGRESS.equals(strEvent)) {
			onResponseDownloadFileProgress(jsonObject);
		} else if (Request.REQ_ADD_FRIEND.equals(strEvent)) {
			onResponseAddFriend(jsonObject);
		} else if (Request.REQ_MODIFY_FRIEND.equals(strEvent)) {
			onResponseModifyFriend(jsonObject);
		} else if (Request.REQ_DEL_FRIEND.equals(strEvent)) {
			onResponseDelFriend(jsonObject);
		} else if (Response.RESPONSE_CREATE_GROUP.equals(strEvent)) {
			onResponseCreateGroup(jsonObject);
		} else if (Request.REQ_MODIFY_GROUP.equals(strEvent)) {
			onResponseModifyGroup(jsonObject);
		} else if (Request.REQ_DEL_GROUP.equals(strEvent)) {
			onResponseDelGroup(jsonObject);
		} else if (Request.REQ_ADD_GROUP_USER.equals(strEvent)) {
			onResponseAddGroupUser(jsonObject);
		} else if (Request.REQ_DEL_GROUP_USER.equals(strEvent)) {
			onResponseDelGroupUser(jsonObject);
		}else if(Request.REQ_UPLOAD_FILE_HTTP.equals(strEvent)){
			onResponseUploadFileHttp(jsonObject);
		}
	}


	private static void onResponseRegister(JSONObject jsonObject) {
		if(null == jsonObject)
			return;
		System.out.println("jsonObject--->" + jsonObject.toString());
		String strResult = jsonObject.optString("result", "");
		String uuid = null;
		boolean bResult = false;
		if ("1".equals(strResult)) {
			bResult = true;
			uuid = jsonObject.optString("uuid", "");
		} else {
			bResult = false;
		}

		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseRegister(bResult, uuid);
		}
	}

	private static void onResponseLogin(JSONObject jsonObject) {
//		System.out.println("onResponseLogin" + jsonObject.toString());
		if(null == jsonObject)
			return;
		String strResult = jsonObject.optString("result", "");
		boolean bResult = false;
		User user = null;
		if ("1".equals(strResult)) {
			bResult = true;
			mSessionID = jsonObject.optString("ssid", "");
			user = User.parse(jsonObject);
		} else {
			bResult = false;
		}

		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseLogin(bResult, user);
		}
	}

	private static void onResponseLogout(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseLogout(true);
		}
	}

	private static void onResponseCall(String name) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseCall(true);
		}
	}
	
	private static void onResponseApiStatus(String status) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			System.out.println("EventListeneronseApiStatus");
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseApiStatus(status);
		}
	}
	
	private static void onResponseCallIn(String fromName) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseCallIn(fromName);
		}
	}
	
	private static void onResponseTakePicture(String picPath,boolean isSuccsee) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseCapture(picPath,isSuccsee);
		}
	}
	
	private static void onResponseVdoRecode(String vdoPath,boolean isSuccsee) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseVdoRecode(vdoPath,isSuccsee);
		}
	}
	
	private static void onResponseSynTransparent(String contentUrl,String srcID,boolean isSuccess){
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseSynTransport(contentUrl,srcID,isSuccess);
		}
	}
	
	private static void onResponseConfUpLoadFile(String handID,String Progress,String filePath,boolean isSuccess){
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseConfUpLoadFile(handID,Progress,filePath,isSuccess);
		}
	}
	
	private static void onResponseCallFailed(String resion){
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseCallFailed(resion);
		}
	}

	private static void onResponseAnswerCall(String name) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			System.out.println("onResponseAnswerCall-->"+listener);
			listener.onAnswerCall(true);
		}
	}
	private static void onResponseHangup(String name) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseHangup(true);
		}
	}

	private static void onResponseCreateRoom(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseCreateRoom(true);
		}
	}

	private static void onResponseModifyRoom(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseModifyRoom(true);
		}
	}

	private static void onResponseDestroyRoom(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseDestroyRoom(true);
		}
	}

	private static void onResponseEnterRoom(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseEnterRoom(true);
		}
	}

	private static void onResponseExitRoom(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseExitRoom(true);
		}
	}

	private static void onResponseQueryMyRoom(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseQueryMyRoom(true);
		}
	}

	private static void onResponseQueryRoomUsrList(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseQueryRoomUsrList(true);
		}
	}

	private static void onResponseQueryCallHistory(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseQueryCallHistory(true);
		}
	}

	private static void onResponseMuteAll(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseMuteAll(true);
		}
	}

	private static void onResponseMuteOther(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseMuteOther(true);
		}
	}

	private static void onResponseInviteToRoom(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseInviteToRoom(true);
		}
	}

	private static void onResponseKickFromRoom(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseKickFromRoom(true);
		}
	}

	private static void onResponseUploadFile(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseUploadFile(true);
		}
	}

	private static void onResponseUploadFileProgress(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseUploadFileProgress(true);
		}
	}

	private static void onResponseDownloadFile(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseDownloadFile(true);
		}
	}

	private static void onResponseDownloadFileProgress(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseDownloadFileProgress(true);
		}
	}

	private static void onResponseQueryColleageList(String jsonResult) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseQueryColleageList(true,jsonResult);
		}
	}

	private static void onResponseQueryFriendList(String jsonResult) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseQueryFriendList(true,jsonResult);
		}
	}

	private static void onResponseQueryUserInfo(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseQueryUserInfo(true);
		}
	}

	private static void onResponseAddFriend(JSONObject jsonObject) {
		if (null != jsonObject) {
			for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
				EventListener listener = (EventListener) mListenerList
						.get(nIndex);
				int result = 0;
				boolean isSuccess = false;
				try {
					result = jsonObject.getInt("result");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (result == 1) {
					isSuccess = true;
				}
				listener.onResponseAddFriend(isSuccess, result);
			}
		}
	}

	private static void onResponseModifyFriend(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseModifyFriend(true);
		}
	}

	private static void onResponseDelFriend(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			int result = 0;
			try {
				result = jsonObject.getInt("result");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result == 1)
			{
				listener.onResponseDelFriend(true);
			} else {
				listener.onResponseDelFriend(false);
			}
		}
	}

	private static void onResponseQueryGroupList(String jsonResult) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseQueryGroupList(true,jsonResult);
		}
	}

	private static void onResponseCreateGroup(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseCreateGroup(true);
		}
	}

	public static void onResponseModifyGroup(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			int result = 0;
			try {
				result = jsonObject.getInt("result");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result == 1) {
				listener.onResponseModifyGroup(true);
			} else {
				listener.onResponseModifyGroup(false);
			}
			
		}
	}

	private static void onResponseDelGroup(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			int result = 0;
			try {
				result = jsonObject.getInt("result");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result == 1) {
				listener.onResponseDelGroup(true);
			} else {
				listener.onResponseDelGroup(false);
			}

		}
	}

	private static void onResponseGetGroupUser(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseGetGroupUser(true);
		}
	}

	private static void onResponseAddGroupUser(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			boolean isSuccess = false;
			int result = 0;
			try {
				if(null != jsonObject){
					result = jsonObject.getInt("result");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(result == 1){
				isSuccess = true;
			}
			listener.onResponseAddGroupUser(isSuccess,result);
		}
	}

	private static void onResponseDelGroupUser(JSONObject jsonObject) {
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			listener.onResponseDelGroupUser(true);
		}
	}
	
	private static void onResponseUploadFileHttp(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		if(null == jsonObject)
			return;
		for (int nIndex = 0; nIndex < mListenerList.size(); nIndex++) {
			EventListener listener = (EventListener) mListenerList.get(nIndex);
			int result = 0;
			String resUuid = null;
			String type = null;
			try {
				result = jsonObject.getInt("result");
				resUuid = jsonObject.getString("uuid");
				type = jsonObject.getString("type");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result == 1) {
				listener.onResponseUploadFileHttp(true,resUuid,type);
			} else {
				listener.onResponseUploadFileHttp(false,"","");
			}
		}
	}

	// ////////////////////////////////////////////////////////////////
	// VNow Framework callback
	private static class VNFrameworkEventListener implements
			IVNowFramework.IFrameworkEventListener {
		public void onEventNotify(String strEvent, String strResult) {
			if(strEvent.equals("coremsg")){
				processCoreEvent(strResult);
			}else{
				processEvent(strEvent, strResult);
			}
		}
	}
}