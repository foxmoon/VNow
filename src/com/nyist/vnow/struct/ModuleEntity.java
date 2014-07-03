package com.nyist.vnow.struct;

import java.io.Serializable;

public class ModuleEntity implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private int groupId;// 当前选中id
    private int type;// type 是 热推的类型
    private String keyword;// 搜索关键字

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
