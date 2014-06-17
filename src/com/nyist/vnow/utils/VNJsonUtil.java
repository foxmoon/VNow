package com.nyist.vnow.utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nyist.vnow.struct.Colleage;
import com.nyist.vnow.struct.Friend;
import com.nyist.vnow.struct.Group;

public class VNJsonUtil {
	private ArrayList<Friend> mListFriendItems;
	private ArrayList<Group> mListGroupItems;
	private ArrayList<Colleage> mListColleageItems;
//	
	public VNJsonUtil(){
		mListFriendItems = new ArrayList<Friend>();
		mListGroupItems = new ArrayList<Group>();
		mListColleageItems = new ArrayList<Colleage>();
	}
	
	public void parseFriendFromJson(String jsonData){
		Type listType=new TypeToken<LinkedList<Friend>>(){}.getType();
		Gson gson=new Gson();
		LinkedList<Friend> items =gson.fromJson(jsonData, listType);
		mListFriendItems.clear();
		for(Iterator iterator=items.iterator();iterator.hasNext();){
			Friend item=(Friend) iterator.next();
			mListFriendItems.add(item);
		}
	}
	
	public void parseGroupFromJson(String jsonData){
		Type listType=new TypeToken<LinkedList<Group>>(){}.getType();
		Gson gson=new Gson();
		LinkedList<Group> items =gson.fromJson(jsonData, listType);
		mListGroupItems.clear();
		for(Iterator iterator=items.iterator();iterator.hasNext();){
			Group item=(Group) iterator.next();
			mListGroupItems.add(item);
		}
	}
	
	public void parseColleageFromJson(String jsonData){
		Type listType=new TypeToken<LinkedList<Colleage>>(){}.getType();
		Gson gson=new Gson();
		LinkedList<Colleage> items =gson.fromJson(jsonData, listType);
		mListColleageItems.clear();
		for(Iterator iterator=items.iterator();iterator.hasNext();){
			Colleage item=(Colleage) iterator.next();
			mListColleageItems.add(item);
		}
	}
	
	public ArrayList<Friend> getFriendList(){
		return mListFriendItems;
	}
	
	public ArrayList<Group> getGroupList(){
		return mListGroupItems;
	}
	
	public ArrayList<Colleage> getColleageList(){
		return mListColleageItems;
	}
}
