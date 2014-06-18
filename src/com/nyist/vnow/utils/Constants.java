package com.nyist.vnow.utils;

/**
 * @author harry
 * @version Creat on 2014-6-17下午12:01:44
 */
public class Constants {
    public static final String PRF_KEY_FIRST_LAUNCH = "first_launch";
    public static final String PRF_KEY_LOGIN = "login";
    public static final String PRF_USER_PHONE = "user_phone";
    public static final String PRF_USER_PASSWORD = "user_password";
    public static final String MEDIA_SERVER_IP = "media_server_ip";
    public static final String SHOW_VIDEO_PARAMS = "show_video_params";
    public static final String SHOW_VIDEO_SOUND_RED = "show_video_sound_red";
    public static final String GROUP_UPDATE_VERSION = "group_update_version";
    public static final String FRIEND_UPDATE_VERSION = "friend_update_version";
    public static final String COLLEAGE_UPDATE_VERSION = "colleage_update_version";
    public static final String DEFULT_SERVER_IP = "112.5.192.156";
    public static final String DEFULT_VIDEO_PARAMS = "-30-320-240-300000-1";
    public static final String STANDARD_VIDEO_PARAMS = "-30-640-480-800000-1";
    public static final String HIGH_VIDEO_PARAMS = "-30-1280-720-1500000-1";
    public static final String P_APP_OLD_VERSION_CODE = "pref.app.old.version.code"; // 旧的version
    public static final String KEY_ACCOUNT = "key_account";
    public static final String KEY_ACCOUNT_TYPE = "key_account_type";
    public static final String KEY_USERNAME = "key_username";
    public static final String KEY_PICTURE = "key_picture";
    public static final String KEY_DEFALUT_PASSWORD = "key_defalut_password";
    public static final String RESTART_APP = "restart_app";
    public static final String RELOGIN_APP = "relogin_app";
    public static final String KEY_IS_DEFAULT_PASSWORD = "key_is_default_password";
    public static final String PREFS_KEY_USER_INFO = "prefs_key_user_info";
    public static final int DEFAULT_DAYS_NUM = 5;
    public static final int DEFAULT_CLASSES_NUM = 8;
    public static final int SCHEDULE_TOP_LEFT_BLOCK_PIXEL = 20;
    public static final int SCHEDULE_TOP_LEFT_OTHER_PIXEL = 30;
    public static final int SCHEDULE_TOP_LEFT_MORNING_READING_PIXEL = 30;
    public static final String KEY_NOTIFY_DATA = "key_notify_data";
    public static final String KEY_NOTIFY_TYPE = "key_notify_type";
    public static final int HTTP_PUSH_NOTIFICATION_ID = 1234;
    public static final String KEY_COMPOSE_TYPE = "key_compose_type";
    public static final String KEY_SUB_CLASS = "key_sub_class";
    public static final String KEY_SUB_CLASS_INDEX = "key_sub_class_index";
    public static final String PACKAGE_NAME = "com.iteacher.android";
    public static final String KEY_ID = "key_id";
    public static final String KEY_ACTION_TYPE = "key_action_type";
    public static final String KEY_ATTACHMENTS = "key_attachments";
    public static final String KEY_ATTACHMENT = "key_attachment";
    public static final String KEY_CURRENT_INDEX = "key_current_index";
    public static final String KEY_CLASS_ID = "key_class_id";
    public static final String KEY_SCHEDULE_TYPE = "key_schedule_type";
    public static final String KEY_CHART_TYPE = "key_chart_type";
    public static final String KEY_EXAM_ID = "key_exam_id";
    public static final String KEY_EXAM_NAME = "key_exam_name";
    public static final String KEY_STUDENT_ID = "key_student_id";
    public static final String KEY_COURSE_ID = "key_course_id";
    public static final String KEY_PERSON_ENTITY = "key_person_entity";
    public static final String KEY_TIMETABLE_ID = "key_timetable_id";
    public static final String SCHEDULE_DAY_L = "mScheduleClassDay";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 101;
    public static final int PICK_FROM_FILE_ACTIVITY_REQUEST_CODE = 102;
    public static final int REQUEST_CODE_CROP_IMAGE = 103;
    public static final int REQUEST_VIEW_IMAGE = 104;

    public enum ActionType {
        detail, creation
    }

    public enum ChoiceType {
        single, multiple
    }
}
