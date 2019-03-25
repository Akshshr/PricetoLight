package com.pricetolight.app.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;

public class AppPreferences {

    private static final String TAG = AppPreferences.class.getSimpleName();

    public static SharedPreferences sharedPreferences;
    private RxSharedPreferences rxSharedPreferences;


    public AppPreferences(final Context context) {
        final SharedPreferences sharedPreferences
                = PreferenceManager.getDefaultSharedPreferences(context);
        rxSharedPreferences = RxSharedPreferences.create(sharedPreferences);
    }

    public Preference<String> getAuthenticationToken() {
        return rxSharedPreferences.getString(Keys.AUTHENTICATION_TOKEN);
    }

    public void setAuthenticationToken(final String token) {
        rxSharedPreferences.getString(Keys.AUTHENTICATION_TOKEN).set(token);
    }

    public Preference<String> getActiveHomeId() {
        return rxSharedPreferences.getString(Keys.ACTIVE_HOME_ID);
    }

    public void setActiveHomeId(String homeId) {
        rxSharedPreferences.getString(Keys.ACTIVE_HOME_ID).set(homeId);
    }

    public Preference<String> getCustomerId() {
        return rxSharedPreferences.getString(Keys.ACTIVE_CUSTOMER_ID);
    }

    public void setCustomerId(String customerId) {
        rxSharedPreferences.getString(Keys.ACTIVE_CUSTOMER_ID).set(customerId);
    }


    public void setPreferredTotalPrice(boolean preferred) {
        rxSharedPreferences.getBoolean(Keys.PREFERRED_TOTAL_PRICE).set(preferred);
    }

    public Preference<Boolean> getPreferredTotalPrice() {
        return rxSharedPreferences.getBoolean(Keys.PREFERRED_TOTAL_PRICE, false);
    }



    private static void initlizeSharePrefrences(Context context) {
        sharedPreferences= context.getSharedPreferences(Keys.kKeyForSharedPref,Context.MODE_PRIVATE);
    }

    public static float getLongitude(Context context) {
        if (sharedPreferences==null)
            initlizeSharePrefrences(context);
        return sharedPreferences.getFloat(Keys.LONGITUDE,0);
    }

    public static void setLongitude(Context context, float lon) {
        if (sharedPreferences==null)
            initlizeSharePrefrences((Context) context);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putFloat(Keys.LONGITUDE, lon);
        editor.apply();
    }
    //

    public static Boolean showWhatsNew(Context context) {
        if (sharedPreferences==null)
            initlizeSharePrefrences(context);
        return sharedPreferences.getBoolean(Keys.WHATS_NEW, false);
    }

    public static void setSeenWhatsNew(Context context, Boolean whatsNew) {
        if (sharedPreferences==null)
            initlizeSharePrefrences((Context) context);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(Keys.WHATS_NEW,whatsNew);
        editor.apply();
    }

    @Nullable
    public Preference<String> getLastConnectedIPAddressHue() {
        return rxSharedPreferences.getString(Keys.IP_ADDRESS,"");
    }

    public void setLastConnectedIPAddressHue(String ip) {
        rxSharedPreferences.getString(Keys.IP_ADDRESS).set(ip);
    }

    @Nullable
    public Preference<String> getUserNameHue() {
        return rxSharedPreferences.getString(Keys.USER_NAME);
    }

    public void setUserNameHue(String userNameHue) {
        rxSharedPreferences.getString(Keys.USER_NAME).set(userNameHue);
    }


    //   ---KEYS---  //
    private class Keys {
        static final String kKeyForSharedPref = "kKeyForSharedPref";
        static final String AUTHENTICATION_TOKEN = "authentication";

        static final String PREFERRED_TOTAL_PRICE = "preferred_total_price";
        static final String ACTIVE_CUSTOMER_ID = "customer_id";
        static final String ACTIVE_CUSTOMER_NAME = "customer_name";
        static final String ACTIVE_HOME_ID = "home_id";

        static final String WHATS_NEW = "whats_new";
        static final String LATITUDE = "latitude";
        static final String LONGITUDE = "longitude";
        static final String IP_ADDRESS = "ip_address";
        static final String USER_NAME = "user_name";


    }
}
