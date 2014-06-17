package com.nyist.vnow.struct;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public String uuid;
    public String type; // 管理员：1，普通用户：0
    public String phone;
    public String password;
    public String name;
    public String company_code;

    public static User parse(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return User.parse(jsonObject);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User parse(JSONObject jsonObject) {
        if (null == jsonObject) {
            return null;
        }
        User user = new User();
        user.uuid = jsonObject.optString("uuid", "");
        user.type = jsonObject.optString("type", "0");
        user.phone = jsonObject.optString("ad_phone", "");
        user.password = jsonObject.optString("ad_pass", "");
        user.name = jsonObject.optString("ad_name", "");
        user.company_code = jsonObject.optString("code", "");
        return user;
    }

    public void reset() {
        uuid = "";
        type = "0";
        phone = "";
        password = "";
        name = "";
        company_code = "";
    }
}