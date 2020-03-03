package com.example.threat.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    private static SharedPreference mSharedPreference;
    protected Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;

    private SharedPreference(Context context) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();
    }

    /**
     * Creates single instance of SharedPreference
     *
     * @param context context of Activity or Service
     * @return Returns instance of SharedPreference
     */
    public static synchronized SharedPreference getInstance(Context context) {

        if (mSharedPreference == null) {
            mSharedPreference = new SharedPreference(context.getApplicationContext());
        }
        return mSharedPreference;
    }

    /**
     * Stores String value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    public void setValue(String key, String value) {
        mSharedPreferencesEditor.putString(key, value);
        mSharedPreferencesEditor.commit();
    }

    /**
     * Stores int value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    public void setValue(String key, int value) {
        mSharedPreferencesEditor.putInt(key, value);
        mSharedPreferencesEditor.commit();
    }

    /**
     * Stores Double value in String format in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    public void setValue(String key, double value) {
        setValue(key, Double.toString(value));
    }

    /**
     * Stores long value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    public void setValue(String key, long value) {
        mSharedPreferencesEditor.putLong(key, value);
        mSharedPreferencesEditor.commit();
    }

    /**
     * Stores boolean value in preference
     *
     * @param key   key of preference
     * @param value value for that key
     */
    public void setValue(String key, boolean value) {
        mSharedPreferencesEditor.putBoolean(key, value);
        mSharedPreferencesEditor.commit();
    }

    /**
     * Retrieves String value from preference
     *
     * @param key          key of preference
     * @param defaultValue default value if no key found
     */
    public String getStringValue(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    /**
     * Retrieves int value from preference
     *
     * @param key          key of preference
     * @param defaultValue default value if no key found
     */
    public int getIntValue(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Retrieves long value from preference
     *
     * @param key          key of preference
     * @param defaultValue default value if no key found
     */
    public long getLongValue(String key, long defaultValue) {
        return mSharedPreferences.getLong(key, defaultValue);
    }

    /**
     * Retrieves boolean value from preference
     *
     * @param keyFlag      key of preference
     * @param defaultValue default value if no key found
     */
    public boolean getBoolanValue(String keyFlag, boolean defaultValue) {
        return mSharedPreferences.getBoolean(keyFlag, defaultValue);
    }

    /**
     * Removes key from preference
     *
     * @param key key of preference that is to be deleted
     */
    public void removeKey(String key) {
        if (mSharedPreferencesEditor != null) {
            mSharedPreferencesEditor.remove(key);
            mSharedPreferencesEditor.commit();
        }
    }


    /**
     * Clears all the preferences stored
     */
    public void clear() {
        mSharedPreferencesEditor.clear().commit();
    }
}
