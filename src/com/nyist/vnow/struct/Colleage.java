package com.nyist.vnow.struct;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Colleage")
public class Colleage extends CommItem {
    public static final String USER_ID = "userId";
    public static final String CREATETIME = "g_createtime";
    @DatabaseField(id = true)
    private String g_uuid;
    @DatabaseField
    private String g_name;
    @DatabaseField
    private String g_pid;
    @DatabaseField
    private String g_type;
    @DatabaseField
    private String g_phone;
    @DatabaseField
    private String g_code;
    @DatabaseField
    private String g_updatenum;
    @DatabaseField
    private String g_createtime;
    @DatabaseField
    private String g_head;
    @DatabaseField
    private String userId;
    public boolean isChecked;
    

    public String getG_name() {
        return g_name;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    public String getG_pid() {
        return g_pid;
    }

    public void setG_pid(String g_pid) {
        this.g_pid = g_pid;
    }

    public String getG_type() {
        return g_type;
    }

    public void setG_type(String g_type) {
        this.g_type = g_type;
    }

    public String getG_phone() {
        return g_phone;
    }

    public void setG_phone(String g_phone) {
        this.g_phone = g_phone;
    }

    public String getG_code() {
        return g_code;
    }

    public void setG_code(String g_code) {
        this.g_code = g_code;
    }

    public String getG_updatenum() {
        return g_updatenum;
    }

    public void setG_updatenum(String g_updatenum) {
        this.g_updatenum = g_updatenum;
    }

    public String getG_createtime() {
        return g_createtime;
    }

    public void setG_createtime(String g_createtime) {
        this.g_createtime = g_createtime;
    }

    public String getG_head() {
        return g_head;
    }

    public void setG_head(String g_head) {
        this.g_head = g_head;
    }

    public String getG_uuid() {
        return g_uuid;
    }

    public void setG_uuid(String g_uuid) {
        this.g_uuid = g_uuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
}