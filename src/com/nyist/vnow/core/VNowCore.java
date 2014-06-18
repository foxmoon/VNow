package com.nyist.vnow.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Surface;

import com.amap.api.location.AMapLocation;
import com.nyist.vnow.R;
import com.nyist.vnow.event.CoreEvent;
import com.nyist.vnow.listener.CoreCallBack;
import com.nyist.vnow.struct.Colleage;
import com.nyist.vnow.struct.FileUpdate;
import com.nyist.vnow.struct.Friend;
import com.nyist.vnow.struct.Group;
import com.nyist.vnow.struct.UploadCaptrue;
import com.nyist.vnow.struct.User;
import com.nyist.vnow.struct.VNConfItem;
import com.nyist.vnow.struct.VNowFriend;
import com.nyist.vnow.struct.VNowRctContact;
import com.nyist.vnow.utils.CommonUtil;
import com.nyist.vnow.utils.DES;
import com.nyist.vnow.utils.MD5;
import com.nyist.vnow.utils.Session;
import com.nyist.vnow.utils.UpdateSoftManager;
import com.nyist.vnow.utils.VNJsonUtil;
import com.nyist.vnow.utils.VNTaskDao;
import com.vnow.sdk.framework.IVNowFramework;
import com.vnow.sdk.openapi.EventListener;
import com.vnow.sdk.openapi.IVNowAPI;

/**
 * 常用接口访问方法的封装
 * 
 * @author harry
 * @version Creat on 2014-6-17上午9:47:03
 */
public class VNowCore {
    private final String[] PHONES_PROJECTION = new String[] {
            Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };
    private final int PHONES_DISPLAY_NAME_INDEX = 0;
    private final int PHONES_NUMBER_INDEX = 1;
    private final int PHONES_PHOTO_ID_INDEX = 2;
    private final int PHONES_CONTACT_ID_INDEX = 3;
    private List<VNowFriend> mListPhoneContacts;
    private List<VNowFriend> mListOtnereContacts;
    private List<Friend> mListFriends;
    private List<Group> mListGroups;
    private List<Colleage> mListColleages;
    private List<VNowRctContact> mListRctContacts;
    private IVNowAPI mIVNowAPI;
    private MyEventListener mEventListener;
    private Context mContext;
    private VNConfItem mCurrentConfInfo;
    private List<VNConfItem> mConfItemList;
    private VNTaskDao mTaskDao;
    private User mMySelf;
    private DES mDES;
    private VNJsonUtil mJsonUtil;
    private String mApiStatus = null;
    private List<FileUpdate> mListFileUpdate;
    private List<UploadCaptrue> mListPhotoUpdate;
    private List<String> mListPicUrl;
    private MediaPlayer mMediaPlayer;
    private CoreCallBack mCallBackListener;
    private UpdateSoftManager mUpdateSoftManager;
    private boolean _isCheckVersion = false;
    private double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

    public VNowCore(Context context) {
        this.mContext = context;
        mConfItemList = new ArrayList<VNConfItem>();
        mListPhoneContacts = new ArrayList<VNowFriend>();
        mListOtnereContacts = new ArrayList<VNowFriend>();
        mListFriends = new ArrayList<Friend>();
        mListGroups = new ArrayList<Group>();
        mListColleages = new ArrayList<Colleage>();
        mListRctContacts = new ArrayList<VNowRctContact>();
        mListFileUpdate = new ArrayList<FileUpdate>();
        mListPhotoUpdate = new ArrayList<UploadCaptrue>();
        mListPicUrl = new ArrayList<String>();
        mTaskDao = new VNTaskDao(mContext);
        mIVNowAPI = IVNowAPI.newInstance();
        bindVNowService();
        mEventListener = new MyEventListener();
        mIVNowAPI.setEventListener(mEventListener);
        mDES = new DES();
        mJsonUtil = new VNJsonUtil();
        mMySelf = new User();
    }

    public void setCoreListener(CoreCallBack listener) {
        // TODO Auto-generated method stub
        mCallBackListener = listener;
    }

    public void unSetCoreListener() {
        // TODO Auto-generated method stub
        mCallBackListener = null;
    }

    public List<FileUpdate> getmListFileUpdate() {
        return mListFileUpdate;
    }

    public List<UploadCaptrue> getmListPhotoUpdate() {
        return mListPhotoUpdate;
    }

    public List<VNowRctContact> getmListRctContacts() {
        return mListRctContacts;
    }

    public boolean addUploadFile(FileUpdate file) {
        boolean isExist = false;
        for (FileUpdate fileupdate : mListFileUpdate) {
            if (file.getmFile().getAbsolutePath()
                    .equals(fileupdate.getmFile().getAbsolutePath())
                    && file.getmFileName().equals(fileupdate.getmFileName())) {
                isExist = true;
                break;
            }
        }
        if (isExist) {
            return false;
        }
        else {
            mListFileUpdate.add(file);
            return true;
        }
    }

    public boolean addUpLoadPhoto(String path) {
        UploadCaptrue photo = new UploadCaptrue();
        photo.setPhotoPath(path);
        File file = new File(path);
        photo.setPhotoName(file.getName());
        Bitmap bmp = getBitmapFromFile(file, 360, 480);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        photo.setBmpPhoto(bmp);
        boolean isExist = false;
        for (UploadCaptrue captrue : mListPhotoUpdate) {
            if (photo.getPhotoPath()
                    .equals(captrue.getPhotoPath())
                    && photo.getPhotoName().equals(captrue.getPhotoName())) {
                isExist = true;
                break;
            }
        }
        if (isExist) {
            return false;
        }
        else {
            mListPhotoUpdate.add(photo);
            return true;
        }
    }

    public void clearCore() {
        mConfItemList.clear();
        mListFriends.clear();
        mListFileUpdate.clear();
        mListRctContacts.clear();
        mConfItemList = null;
        mListFriends = null;
        mIVNowAPI.removeEventListener(mEventListener);
        mIVNowAPI.coreExit(mContext);
        mIVNowAPI.deInit(mContext);
        mTaskDao.closeDB();
    }

    public void doSetEventListener(EventListener eventListener) {
        mIVNowAPI.setEventListener(eventListener);
    }

    public void doRemoveEventListener(EventListener eventListener) {
        mIVNowAPI.removeEventListener(eventListener);
    }

    // --------------------------web 接口--------------------------------------
    /**
     * 向web发送服务器中图片的位置信息
     * 
     * @param newName
     * @param oldName
     * @param type
     * @param remark
     * @param shareUUID
     * @param fileType
     */
    public synchronized void doHttpWebUpLoad(String newName, String oldName, String type, String remark, String shareUUID, String fileType) {
        AMapLocation location = VNowApplication.newInstance().getBDLocation();
        if (null == location)
            return;
        double[] ltResult = bd_encrypt(location.getLatitude(), location.getLongitude());
        String serverName = "http://" + CommonUtil._svrIP + ":8080" + newName;
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("uuid", mMySelf.uuid));
        params.add(new BasicNameValuePair("latitude", String.valueOf(String.valueOf(ltResult[0]))));
        params.add(new BasicNameValuePair("longitude", String.valueOf(String.valueOf(ltResult[1]))));
        params.add(new BasicNameValuePair("remarks", remark));
        params.add(new BasicNameValuePair("old_name", oldName));
        params.add(new BasicNameValuePair("new_name", serverName));
        params.add(new BasicNameValuePair("type", type));
        params.add(new BasicNameValuePair("share", shareUUID));
        params.add(new BasicNameValuePair("file_type", fileType));
        mIVNowAPI.httpWebUpLoad(params);
    }

    /**
     * web 注册接口调用
     * 
     * @param userName
     * @param password
     * @param phone
     * @param emial
     * @param code
     */
    public synchronized void doRegidt(String userName, String password, String phone, String emial, String code) {
        String passMD5 = MD5.getMD5(password);
        String phoneDes = mDES.encrypt(phone);
        if (null == mMySelf)
            mMySelf = new User();
        mMySelf.name = userName;
        mMySelf.password = passMD5;
        mMySelf.phone = phoneDes;
        mMySelf.company_code = code;
        mIVNowAPI.register(mMySelf);
    }

    /**
     * web 登录接口调用
     * 
     * @param strPhone
     * @param strPsw
     */
    public synchronized void doLogin(String strPhone, String strPsw, boolean isAuto) {
        String phoneDes;
        String passMD5;
        if (!isAuto) {
            phoneDes = mDES.encrypt(strPhone);
            passMD5 = MD5.getMD5(strPsw);
        }
        else {
            phoneDes = strPhone;
            passMD5 = strPsw;
        }
        if (null == mMySelf)
            mMySelf = new User();
        mMySelf.phone = phoneDes;
        mMySelf.password = passMD5;
        mIVNowAPI.login(phoneDes, passMD5);
    }

    /**
     * web 注销接口调用
     */
    public void doLogout() {
        if (null != mMySelf) {
            // mIVNowAPI.logout(mMySelf);
            _isCheckVersion = false;
            mMySelf.reset();
        }
    }

    public void exitCore() {
        if (null != mMySelf) {
            mConfItemList.clear();
            mListFriends.clear();
            mListFileUpdate.clear();
            mListRctContacts.clear();
            mIVNowAPI.coreExit(mContext);
            mMySelf.reset();
        }
        if (null != mCallBackListener)
            mCallBackListener.onCoreCallBack(CoreEvent.EVENT_LOGOUT_TASK);
    }

    public void logoutCore() {
        mIVNowAPI.coreExit(mContext);
    }

    /**
     * 启动并绑定VNowService服务
     */
    public void bindVNowService() {
        mIVNowAPI.bindVNowService(mContext);
    }

    public void loadWebData() {
        mTaskDao.deleteAllColleage(mMySelf.uuid);
        mTaskDao.deleteAllGroup(mMySelf.uuid);
        mTaskDao.deleteAllFriend(mMySelf.uuid);
        mIVNowAPI.queryColleagueList(mMySelf.uuid, 0, mMySelf.company_code);
        mIVNowAPI.queryFriendList(mMySelf.uuid, 0);
        mIVNowAPI.queryGroupList(mMySelf.uuid, 0);
    }

    /**
     * web 同事列表接口调用
     * 
     * @param version
     */
    public synchronized void doQueryColleagueList() {
        int currentVersion = Session.newInstance(mContext).getColleageVersion(mMySelf.uuid);
        mIVNowAPI.queryColleagueList(mMySelf.uuid, currentVersion, mMySelf.company_code);
    }

    /**
     * web 好友列表接口调用
     * 
     * @param version
     */
    public synchronized void doQueryFriendList() {
        int currentVersion = Session.newInstance(mContext).getFriendVersion(mMySelf.uuid);
        mIVNowAPI.queryFriendList(mMySelf.uuid, currentVersion);
    }

    /**
     * web 群组列表接口调用
     * 
     * @param version
     */
    public synchronized void doQueryGroupList() {
        int currentVersion = Session.newInstance(mContext).getGroupVersion(mMySelf.uuid);
        mIVNowAPI.queryGroupList(mMySelf.uuid, currentVersion);
    }

    /**
     * web 群组创建接口调用
     * 
     * @param version
     */
    public synchronized void doCreateGroup(String strGroupName) {
        mIVNowAPI.createGroup(mMySelf.uuid, strGroupName);
    }

    /**
     * web 添加好友接口调用
     * 
     * @param phone
     * @param name
     */
    public synchronized void doAddFriend(String phone, String name) {
        mIVNowAPI.addFriend(mDES.encrypt(phone), mMySelf.uuid, name);
    }

    /**
     * web 修改好友接口调用
     * 
     * @param phone
     * @param name
     * @param f_uuid
     */
    public synchronized void doModifyFrient(String phone, String name, String f_uuid) {
        mIVNowAPI.modifyFriend(mDES.encrypt(phone), mMySelf.uuid, name, f_uuid);
    }

    /**
     * web 删除好友接口调用
     * 
     * @param phone
     * @param f_uuid
     */
    public synchronized void doDelFriend(String phone, String f_uuid) {
        mIVNowAPI.delFriend(mMySelf.uuid, phone, f_uuid);
    }

    /**
     * web 修改群组接口调用
     * 
     * @param gName
     * @param g_uuid
     */
    public synchronized void doModifyGroup(String gName, String g_uuid) {
        mIVNowAPI.modifyGroup(mMySelf.uuid, gName, g_uuid);
    }

    /**
     * web 删除群组接口调用
     * 
     * @param g_uuid
     */
    public synchronized void doDelGroup(String g_uuid) {
        mIVNowAPI.delGroup(mMySelf.uuid, g_uuid);
    }

    /**
     * web 添加群成员接口调用
     * 
     * @param g_uuid
     * @param name
     * @param phone
     */
    public synchronized void doAddGroupUser(String g_uuid, String name, String phone) {
        mIVNowAPI.addGroupUser(mMySelf.uuid, g_uuid, name, mDES.encrypt(phone));
    }

    /**
     * web 删除群成员接口调用
     * 
     * @param phone
     * @param g_uuid
     */
    public synchronized void doDelGroupUser(String phone, String g_uuid) {
        mIVNowAPI.delGroupUser(phone, mMySelf.uuid, g_uuid);
    }

    public void doGetMyselfInfo() {
        mIVNowAPI.getMyselfInfo(mMySelf.uuid);
    }

    /**
     * 会议中通过媒体服务器向服务上传图片
     * 
     * @param filePath
     */
    public void doUpLoadFileConf(String filePath) {
        mIVNowAPI.uploadFile(filePath);
    }

    private double[] bd_encrypt(double gg_lat, double gg_lon)
    {
        double[] bd = new double[2];
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        bd[0] = bd_lat;
        bd[1] = bd_lon;
        return bd;
    }

    // -------------------------------web 接口回调--------------------------
    /**
     * web 接口调用回调接口
     * 
     * @author Administrator
     * 
     */
    private class MyEventListener extends EventListener {
        public void onResponseRegister(boolean bSuccess, String uuid) {// 注册回调
            if (bSuccess) {
                mMySelf.uuid = uuid;
            }
            else
                mMySelf = null;
        }

        public void onResponseLogin(boolean bSuccess, User user) {// 登录回调
            if (bSuccess) {
                mMySelf.uuid = user.uuid;
                mMySelf.company_code = user.company_code;
                mIVNowAPI.dispatchApi(mDES.decrypt(mMySelf.phone), mMySelf.password);
                Session.newInstance(mContext).setUserPhone(mMySelf.phone);
                Session.newInstance(mContext).setPassWord(mMySelf.password);
                loadWebData();
            }
            else
                mMySelf.reset();
        }

        public void onResponseLogout(boolean bSuccess) {// 注销回调
        }

        public void onResponseQueryColleageList(boolean bSuccess, String jsonResult) {// 同事列表回调
            if (jsonResult.contains("[")) {
                jsonResult = jsonResult.substring(jsonResult.indexOf("["),
                        jsonResult.indexOf("]") + 1);
                mJsonUtil.parseColleageFromJson(jsonResult);
                List<Colleage> cacheColleages = mJsonUtil.getColleageList();
                int currentVersion = Session.newInstance(mContext).getColleageVersion(mMySelf.uuid);
                int serverVersion = Integer.parseInt(cacheColleages.get(0).getG_updatenum());
                if (serverVersion != currentVersion) {
                    Session.newInstance(mContext).setColleageVersion(mMySelf.uuid,
                            serverVersion);
                }
                mTaskDao.reLoadColleage(mMySelf.uuid, cacheColleages);
            }
            mListColleages.clear();
            mListColleages = getDBColleageList();
        }

        public void onResponseQueryGroupList(boolean bSuccess, String jsonResult) {// 群组列表回调
            if (jsonResult.contains("[")) {
                jsonResult = jsonResult.substring(jsonResult.indexOf("["),
                        jsonResult.indexOf("]") + 1);
                mJsonUtil.parseGroupFromJson(jsonResult);
                List<Group> cacheGroups = mJsonUtil.getGroupList();
                int currentVersion = Session.newInstance(mContext).getGroupVersion(mMySelf.uuid);
                int serverVersion = Integer.parseInt(cacheGroups.get(0).getUpdatenum());
                if (serverVersion != currentVersion) {
                    Session.newInstance(mContext).setGroupVersion(mMySelf.uuid,
                            serverVersion);
                }
                mTaskDao.reLoadGroup(mMySelf.uuid, cacheGroups);
            }
            mListGroups.clear();
            mListGroups = getDBGroupList();
        }

        public void onResponseQueryFriendList(boolean bSuccess, String jsonResult) {// 好友列表回调
            if (jsonResult.contains("[")) {
                jsonResult = jsonResult.substring(jsonResult.indexOf("["),
                        jsonResult.indexOf("]") + 1);
                mJsonUtil.parseFriendFromJson(jsonResult);
                List<Friend> cacheFriends = mJsonUtil.getFriendList();
                int currentVersion = Session.newInstance(mContext).getFriendVersion(mMySelf.uuid);
                int serverVersion = Integer.parseInt(cacheFriends.get(0).getF_updatenum());
                if (serverVersion != currentVersion) {
                    Session.newInstance(mContext).setFriendVersion(mMySelf.uuid,
                            serverVersion);
                }
                mTaskDao.reLoadFriend(mMySelf.uuid, cacheFriends);
            }
            mListFriends.clear();
            mListFriends = getDBFriendList();
        }

        @Override
        public void onResponseModifyFriend(boolean bSuccess) {
            // TODO Auto-generated method stub
            super.onResponseModifyFriend(bSuccess);
            if (bSuccess) {}
            else {}
        }

        public void onResponseApiStatus(String status) {
            // TODO Auto-generated method stub
            mApiStatus = status;
        }

        public void onResponseCapture(String picPath, boolean isSuccess) {
            // TODO Auto-generated method stub
            if (isSuccess) {
                addUpLoadPhoto(picPath);
            }
        }

        @Override
        public void onResponseSynTransport(String contentUrl, String srcID, boolean isSuccess) {
            if (isSuccess)
                mListPicUrl.add(contentUrl);
        }
    }

    // --------------------多媒体接口-------------------------------------------------------
    public void doOpenLocalVideo(Surface surface, String params) {
        mIVNowAPI.openLocalVideo(surface, params);
    }

    public void doCloseLocalVideo() {
        mIVNowAPI.closeLocalVideo();
    }

    public void dosetRemoteVidSurface(Surface surface) {
        mIVNowAPI.setRemoteVidSurface(surface);
    }

    public void doCapture() {
        mIVNowAPI.uploadCapture();
    }

    public void doStartVdoRecode() {
        mIVNowAPI.vdoStartRecode();
    }

    public void doStopVdoRecode() {
        mIVNowAPI.vdoStopRecode();
    }

    public void doSendTransChannel(String DstID, String serUrl) {
        String serverName = "http://" + CommonUtil._svrIP + ":8080" + serUrl;
        mIVNowAPI.sendTransChannel(DstID, serverName);
    }

    public void doReceiveTransChannel(String SrcID, String resUrl) {
        mIVNowAPI.receiveTransChannel(SrcID, resUrl);
    }

    // -----------------p2p接口--------------------------------------
    public void doP2pCall(String callID) {
        mIVNowAPI.p2pCall(mDES.decrypt(callID));
    }

    public void doP2pAnswer() {
        mIVNowAPI.p2pAnswer();
    }

    public void doP2pHangup() {
        mIVNowAPI.p2pHangup();
    }

    /**
     * 当前登录用户
     * 
     * @return 当前用户对象
     */
    public User getMySelf() {
        return mMySelf;
    }

    public String getApiStatus() {
        return mApiStatus;
    }

    public List<Friend> getFriendList() {
        return mListFriends;
    }

    public List<Group> getGroupList() {
        return mListGroups;
    }

    public List<String> getSynPicUrl() {
        return mListPicUrl;
    }

    public List<Group> getGroupList(boolean isGroup, String mGrpUuid) {
        List<Group> groups = new ArrayList<Group>();
        if (isGroup) {
            for (Group group : mListGroups) {
                String type = group.getType();
                if (type.equals("1")) {
                    groups.add(group);
                }
            }
        }
        else {
            for (Group grp : mListGroups) {
                if (grp.getParentId().equals(mGrpUuid)
                        && grp.getType().equals("2")) {
                    groups.add(grp);
                }
            }
        }
        return groups;
    }

    public List<Colleage> getColleageList() {
        return mListColleages;
    }

    public VNConfItem getmCurrentConfInfo() {
        return mCurrentConfInfo;
    }

    public void setmCurrentConfInfo(VNConfItem mCurrentConfInfo) {
        this.mCurrentConfInfo = mCurrentConfInfo;
    }

    public List<VNowFriend> getmListPhoneContacts() {
        return mListPhoneContacts;
    }

    public void setmListPhoneContacts(List<VNowFriend> mListPhoneContacts) {
        this.mListPhoneContacts = mListPhoneContacts;
    }

    // **************************************************
    // LogicImpl
    // --------------db--------------------------------
    public void addConfItem(VNConfItem confItem) {
        mTaskDao.insertHistoryData(confItem);
    }

    public void deletConfItem(String confName) {
        mTaskDao.deleteHistInfo(confName);
    }

    public List<VNConfItem> getConfItems() {
        mConfItemList = mTaskDao.getMyHistoryData();
        if (null != mConfItemList)
            return mConfItemList;
        else
            return null;
    }

    public void insertCallHistory(VNowRctContact rctItem) {
        mTaskDao.insertRctContact(rctItem);
    }

    public List<VNowRctContact> getRctContact() {
        mListRctContacts = mTaskDao.getCallHistory(mMySelf.uuid);
        return mListRctContacts;
    }

    public int deleteCallHistory(String rctContactName) {
        return mTaskDao.deleteCallHistory(mMySelf.uuid, rctContactName);
    }

    // -------同事 db--------
    public void insertColleageList() {
        for (Colleage colleage : mListColleages) {
            mTaskDao.insertColleage(colleage, mMySelf.uuid);
        }
    }

    public List<Colleage> getDBColleageList() {
        return mTaskDao.getVNColleage(mMySelf.uuid);
    }

    // --------好友 db-------
    public void insertFriendList() {
        for (Friend friend : mListFriends) {
            mTaskDao.insertFriend(friend, mMySelf.uuid);
        }
    }

    public List<Friend> getDBFriendList() {
        return mTaskDao.getVNFriend(mMySelf.uuid);
    }

    public void deleteDBFriendItem(String f_phone) {
        mTaskDao.deleteFriend(mMySelf.uuid, f_phone);
    }

    // ------群组 db-------
    public void insertGroupList() {
        for (Group group : mListGroups) {
            mTaskDao.insertGroup(group, mMySelf.uuid);
        }
    }

    public List<Group> getDBGroupList() {
        return mTaskDao.getVNGroup(mMySelf.uuid);
    }

    public void deleteDBGroupItem(String uuId, int type) {
        if (type == 1) {
            mTaskDao.deleteGroup(mMySelf.uuid, uuId);
            mTaskDao.deleteGChild(mMySelf.uuid, uuId);
        }
        else {
            mTaskDao.deleteChild(mMySelf.uuid, uuId);
        }
    }

    // ----------UI mode logic--------------------------
    public void checkUpVersion(Context context, boolean auto) {
        if (_isCheckVersion && auto)
            return;
        if (null == mUpdateSoftManager) {
            mUpdateSoftManager = new UpdateSoftManager(context);
        }
        mUpdateSoftManager.downloadVersionConfig();
        mUpdateSoftManager.checkUpdate(auto);
        _isCheckVersion = true;
        mUpdateSoftManager = null;
    }

    public Bitmap getBitmapFromFile(File dst, int width, int height) {
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = null;
            if (width > 0 && height > 0) {
                opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dst.getPath(), opts);
                // 计算图片缩放比例
                final int minSideLength = Math.min(width, height);
                opts.inSampleSize = computeSampleSize(opts, minSideLength,
                        width * height);
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
            }
            try {
                return BitmapFactory.decodeFile(dst.getPath(), opts);
            }
            catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        }
        else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        }
        else if (minSideLength == -1) {
            return lowerBound;
        }
        else {
            return upperBound;
        }
    }

    /**
     * the method to get the Contacts from phone
     */
    public void getPhoneContacts() {
        ContentResolver resolver = mContext.getContentResolver();
        // ��ȡ�ֻ���ϵ��
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
                PHONES_PROJECTION, null, null, "sort_key asc");
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                String contactName = phoneCursor
                        .getString(PHONES_DISPLAY_NAME_INDEX);
                Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
                Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
                Bitmap contactPhoto = null;
                if (photoid > 0) {
                    Uri uri = ContentUris.withAppendedId(
                            ContactsContract.Contacts.CONTENT_URI, contactid);
                    InputStream input = ContactsContract.Contacts
                            .openContactPhotoInputStream(resolver, uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                }
                else {
                    // contactPhoto =
                    // BitmapFactory.decodeResource(getResources(),
                    // R.drawable.contact_photo);
                }
                VNowFriend avcContact = new VNowFriend(contactName, phoneNumber);
                avcContact.setmPhoto(contactPhoto);
                mListPhoneContacts.add(avcContact);
            }
            phoneCursor.close();
        }
    }

    /**
     * the method to get the Contacts from SIM Card
     */
    public void getSIMContacts() {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
                null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                String contactName = phoneCursor
                        .getString(PHONES_DISPLAY_NAME_INDEX);
                VNowFriend phoneContact = new VNowFriend(contactName, phoneNumber);
                Resources r = mContext.getResources();
                InputStream is = r.openRawResource(R.drawable.userhead);
                BitmapDrawable bmpDraw = new BitmapDrawable(is);
                Bitmap bmp = bmpDraw.getBitmap();
                phoneContact.setmPhoto(bmp);
                mListPhoneContacts.add(phoneContact);
            }
            phoneCursor.close();
        }
    }

    /**
     * 
     * 从sd卡中读取文件，并且以字节流返回
     * 
     * @param dir
     * @param fileName
     * @return
     */
    public byte[] readFileByte(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            return data;
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * the method about addgrp
     */
    public void addOtherContact(VNowFriend friend) {
        mListOtnereContacts.add(friend);
    }

    public List<VNowFriend> getOtherContacts() {
        return mListOtnereContacts;
    }

    public void openRingCalling(boolean open) {
        if (null == mMediaPlayer)
            mMediaPlayer = new MediaPlayer();
        if (open) {
            try {
                mMediaPlayer.setDataSource(mContext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else {
            mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void doechoDelaySet(int soundValue) {
        // TODO Auto-generated method stub
        mIVNowAPI.echoDelaySet(String.valueOf(soundValue));
    }
}
