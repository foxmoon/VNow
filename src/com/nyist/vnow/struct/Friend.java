package com.nyist.vnow.struct;

import java.io.Serializable;
public class Friend extends CommItem implements Serializable{
	private static final long serialVersionUID = 1L;
	private String f_uuid;
	private String f_phone;
	private String f_updatenum;
	private String f_createtime;
	private String f_a_uuid;
	private String f_name;
	private String f_head;

	public Friend() {

	}

	public String getF_uuid() {
		return f_uuid;
	}

	public void setF_uuid(String f_uuid) {
		this.f_uuid = f_uuid;
	}

	public String getF_phone() {
		return f_phone;
	}

	public void setF_phone(String f_phone) {
		this.f_phone = f_phone;
	}

	public String getF_updatenum() {
		return f_updatenum;
	}

	public void setF_updatenum(String f_updatenum) {
		this.f_updatenum = f_updatenum;
	}

	public String getF_createtime() {
		return f_createtime;
	}

	public void setF_createtime(String f_createtime) {
		this.f_createtime = f_createtime;
	}

	public String getF_a_uuid() {
		return f_a_uuid;
	}

	public void setF_a_uuid(String f_a_uuid) {
		this.f_a_uuid = f_a_uuid;
	}

	public String getF_name() {
		return f_name;
	}

	public void setF_name(String f_name) {
		this.f_name = f_name;
	}

	public String getF_head() {
		return f_head;
	}

	public void setF_head(String f_head) {
		this.f_head = f_head;
	}
}
