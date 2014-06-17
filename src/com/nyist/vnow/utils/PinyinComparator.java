package com.nyist.vnow.utils;

import java.util.Comparator;

import com.nyist.vnow.struct.CommItem;

/**
 * 
 * @author nyist_soft
 * 
 */
public class PinyinComparator implements Comparator<CommItem> {
    public int compare(CommItem o1, CommItem o2) {
        if (o1.getmSortLetters().equals("@")
                || o2.getmSortLetters().equals("#")) {
            return -1;
        }
        else if (o1.getmSortLetters().equals("#")
                || o2.getmSortLetters().equals("@")) {
            return 1;
        }
        else {
            return o1.getmPyName().compareTo(o2.getmPyName());// o1.getmSortLetters().compareTo(o2.getmSortLetters());
        }
    }
}
