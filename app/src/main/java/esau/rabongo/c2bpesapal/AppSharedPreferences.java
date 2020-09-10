package esau.rabongo.c2bpesapal;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSharedPreferences implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences sharedpreferences;
    private static AppSharedPreferences appSharedPreferencesInstance;

    private final String PREF_CONSUMER_KEY = "pref_consumer_key";
    private final String PREF_CONSUMER_SECRET = "pref_consumer_secret";
    private final String PREF_CURRENCY = "pref_currency";

    public static AppSharedPreferences getClassInstance() {
        if (appSharedPreferencesInstance == null) {
            appSharedPreferencesInstance = new AppSharedPreferences();
        }
        return appSharedPreferencesInstance;
    }

    public void init(Context context) {
        this.sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.sharedpreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public AppSharedPreferences() {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    }

    public String getConsumerKey() {
        return this.sharedpreferences.getString(PREF_CONSUMER_KEY, "");
    }

    public void setConsumerKey(String consumer_key) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PREF_CONSUMER_KEY, consumer_key);
        editor.commit();
    }

    public String getConsumerSecret() {
        return this.sharedpreferences.getString(PREF_CONSUMER_SECRET, "");
    }

    public void setConsumerSecret(String consumer_secret) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PREF_CONSUMER_SECRET, consumer_secret);
        editor.commit();
    }

    public String getCurrency() {
        return this.sharedpreferences.getString(PREF_CURRENCY, "KES");
    }

    public void setCurrency(String currency) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(PREF_CURRENCY, currency);
        editor.commit();
    }
}
