package com.nyist.vnow.struct;

public class Group extends CommItem {
    private static final long serialVersionUID = 2L;
    private String updatenum;
    private String a_uuid;
    private String uuid;
    private String phone;
    private String createtime;
    private String parentId;
    private String head;
    private String name;
    private String type;

    public String getUpdatenum() {
        return updatenum;
    }

    public void setUpdatenum(String updatenum) {
        this.updatenum = updatenum;
    }

    public String getA_uuid() {
        return a_uuid;
    }

    public void setA_uuid(String a_uuid) {
        this.a_uuid = a_uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}