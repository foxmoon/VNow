package com.nyist.vnow.struct;

import org.json.JSONException;
import org.json.JSONObject;

public class Room {
    public String id;

    public static Room parse(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return Room.parse(jsonObject);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Room parse(JSONObject jsonObject) {
        if (null == jsonObject) {
            return null;
        }
        Room room = new Room();
        room.id = jsonObject.optString("id", "");
        return room;
    }
}