package tv.huan.bilibili.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @description sharePreference 管理类
 * @Date 2018-5-11
 */

public class CustomSpUtils {

    private static SharedPreferences sp;

    private static CustomSpUtils mSharedPreferenceUtil;

    private CustomSpUtils() {
    }

    public synchronized static CustomSpUtils getInstance() {
        if (mSharedPreferenceUtil == null) {
            mSharedPreferenceUtil = new CustomSpUtils();
        }
        return mSharedPreferenceUtil;
    }

    public CustomSpUtils setSharedPreferences(String name) {
        sp = Utils.getApp().getSharedPreferences(name, Context.MODE_PRIVATE);
        return mSharedPreferenceUtil;
    }

    public String getString(String key) {
        return sp.getString(key, "");
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public void putString(String key, String value) {
        sp.edit().putString(key, value).apply();
    }

    public boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        sp.edit().putBoolean(key, value).apply();
    }

    public int getInt(String key) {
        return sp.getInt(key, 0);
    }

    public int getInt(String key, int defValue) {
        return sp.getInt(key, defValue);
    }

    public void putInt(String key, int value) {
        sp.edit().putInt(key, value).apply();
    }

    public long getLong(String key) {
        return sp.getLong(key, 0);
    }

    public long getLong(String key, long defValue) {
        return sp.getLong(key, defValue);
    }

    public void putLong(String key, long value) {
        sp.edit().putLong(key, value).apply();
    }

    public void remove(String key) {
        sp.edit().remove(key).apply();
    }

    public void clare() {
        sp.edit().clear().apply();
    }

    public void putStringSet(String key, @Nullable Set<String> values) {
        sp.edit().putStringSet(key, values).apply();
    }

    public Set<String> getStringSet(String key) {
        return sp.getStringSet(key, new HashSet<String>());
    }

    /**
     * 对象和对象的成员对象也要序列化
     */
    public <T extends Serializable> void putObject(String key, T object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将对象放到OutputStream中
        // 将对象转换成byte数组，并将其进行base64编码
        String objectStr = new String(Base64.encode(baos.toByteArray(),
                Base64.DEFAULT));
        try {
            baos.close();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 将编码后的字符串写到base64.xml文件中
        putString(key, objectStr);
    }

    public <T> T getObject(String key) {
        try {
            String wordBase64 = getString(key);
// 将base64格式字符串还原成byte数组
            if (wordBase64 == null || wordBase64.equals("")) { // 不可少，否则在下面会报java.io.StreamCorruptedException
                return null;
            }
            byte[] objBytes = Base64.decode(wordBase64.getBytes(),
                    Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(objBytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
// 将byte数组转换成product对象
            Object obj = ois.readObject();
            bais.close();
            ois.close();
            return (T) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
