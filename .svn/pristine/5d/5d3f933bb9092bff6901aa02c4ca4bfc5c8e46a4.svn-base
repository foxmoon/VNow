package com.nyist.vnow.utils;

import java.io.InputStream;

import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class PullParseXmlUtil {
    public static HashMap<String, String> getServerInfo(InputStream is)
            throws Exception {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "UTF-8");
        int type = parser.getEventType();
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if ("version".equals(parser.getName())) {
                        hashMap.put("version", parser.nextText());
                    }
                    else if ("mandatory".equals(parser.getName())) {
                        hashMap.put("mandatory", parser.nextText());
                    }
                    else if ("info".equals(parser.getName())) {
                        hashMap.put("info", parser.nextText());
                    }
                    else if ("url".equals(parser.getName())) {
                        hashMap.put("url", parser.nextText());
                    }
                    break;
            }
            type = parser.next();
        }
        is.close();
        return hashMap;
    }
}
