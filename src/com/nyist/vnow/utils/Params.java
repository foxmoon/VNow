package com.nyist.vnow.utils;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Parcelable;

/**
 * @author harichen
 * @version Creat on 2014-4-9下午4:47:46 NameValue键值对
 */
public class Params {
    public final ArrayList<NameValue> nameValueArray = new ArrayList<NameValue>();

    public static class NameValue {
        public final String name;
        public final Object value;

        public NameValue(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }

    public Params put(String name, boolean value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, boolean[] value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, byte value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, byte[] value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, int value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, int[] value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, long value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, long[] value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, float value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, float[] value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, double value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, double[] value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, String value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, String[] value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, CharSequence value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, CharSequence[] value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, Parcelable value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, Parcelable[] value) {
        appendToParamsArray(name, value);
        return this;
    }

    public Params put(String name, Serializable value) {
        appendToParamsArray(name, value);
        return this;
    }

    private Params appendToParamsArray(String name, Object value) {
        if (value != null && !"".equals(name) && !"".equals(value)) {
            nameValueArray.add(new NameValue(name, value));
        }
        return this;
    }

    public static Params build() {
        return new Params();
    }
}
