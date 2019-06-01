package com.pricetolight.app.base;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.pricetolight.BuildConfig;
import com.pricetolight.api.gson.GsonFactory;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AppCache {



    private static final String TAG = AppCache.class.getSimpleName();
    private static final long MAX_CACHE_AGE = 2592000000L; // 30 days

    private Gson gson;
    private File cacheDirectory;


    public AppCache(File cacheDirectory) {
        this.gson = GsonFactory.create();
        this.cacheDirectory = cacheDirectory;
        if(!cacheDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cacheDirectory.mkdir();
        } else {
            invalidateCache(cacheDirectory);
        }
        if(BuildConfig.DEBUG) {
            okKeys();
        }
    }

    private static void invalidateCache(File dir) {
        // This method goes through the cached files and deletes everything older then MAX_CACHE_AGE
        long bytesDeleted = 0;
        long now = DateTime.now().getMillis();
        File[] files = dir.listFiles();

        for (File file : files) {
            if(now - file.lastModified() > MAX_CACHE_AGE) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
                bytesDeleted += file.length();
                Log.d(TAG, String.format("removed %s during cleanup", file.getName()));
            }
        }
        Log.d(TAG, String.format("deleted in total %s bytes during cleanup", String.valueOf(bytesDeleted)));
    }

    private <T> void writeToCache(String key, T data) {
        try {
            File file = cacheFile(key);
            //noinspection ResultOfMethodCallIgnored
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(gson.toJson(data));
            myOutWriter.close();
        } catch (IOException e) {
            Log.d(TAG, "writeToCache - Error while creating cache file", e);
            e.printStackTrace();
        }
    }

    public void clear() {
        String[] children = cacheDirectory.list();
        try{
            for (String child : children) {
                //noinspection ResultOfMethodCallIgnored
                new File(cacheDirectory, child).delete();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private <T> Observable<T> readFromCache(String key, Class<T> classOfT) {
        try {
            File file = cacheFile(key);
            debugLog("read from cache with path " + file.getAbsolutePath());
            return Observable.just(gson.fromJson(new JsonReader(new FileReader(file)), classOfT));
        } catch (IOException | RuntimeException e) {
            debugLog("readFromCache - Error while reading cache file, " + e.getMessage());
            e.printStackTrace();
        }
        return Observable.empty();
    }

    private static String cacheFileName(String key) {
        String cacheFileName = String.format(
                Locale.US, "%d-%d.rq", key.hashCode(), BuildConfig.VERSION_CODE);
        if(BuildConfig.DEBUG) {
            debugLog("using cache file name: " + cacheFileName);
        }
        return cacheFileName;
    }

    private File cacheFile(String key) {
        return new File(cacheDirectory.getPath(), cacheFileName(key));
    }

    private boolean useOnlyCache(String key, long minAllowedCacheAgeMillis) {
        File file = cacheFile(key);
        if(!file.exists()) {
            return false;
        }
        return (DateTime.now().getMillis() - file.lastModified()) < minAllowedCacheAgeMillis;
    }

    public <T> Observable.Transformer<T, T> applyCache(String key, Class<T> classOfT) {
        // This method simply reads from cache first, and returns an Observable with both
        // cached data, and data from a new request, this results in the subscriber first
        // receiving the cached data and when the API request completes, receives that data.

        return observable -> {
            Observable<T> fromCache = readFromCache(key, classOfT)
                    .subscribeOn(Schedulers.computation());

            Observable<T> fromApi = observable.map(data -> {
                Observable.create(subscriber -> {
                    writeToCache(key, data);
                    subscriber.onCompleted();
                }).subscribeOn(Schedulers.computation()).subscribe();
                return data;
            }).subscribeOn(Schedulers.io());

            return Observable
                    .concat(fromCache, fromApi)
                    .observeOn(AndroidSchedulers.mainThread());
        };
    }

    public <T> Observable.Transformer<T, T> applyCache(String key, Class<T> classOfT, long minAllowedCacheAgeMillis) {
        // Similar behavor as the other applyCache, but this one allows you to specify
        // minAllowedCacheAgeMillis, if the time passed since last request was made and cached is less
        // then minAllowedCacheAgeMillis there will be no API request made. Only the cached data will be used.

        return observable -> {
            Observable<T> fromCache = readFromCache(key, classOfT).subscribeOn(Schedulers.computation());
            if(useOnlyCache(key, minAllowedCacheAgeMillis)) {
                Log.d(TAG, "Only reading from cache, skipping request completely");
                return fromCache.observeOn(AndroidSchedulers.mainThread());
            }

            Observable<T> fromApi = observable.map(data -> {
                Observable.create(subscriber -> {
                    writeToCache(key, data);
                    subscriber.onCompleted();
                }).subscribeOn(Schedulers.computation()).subscribe();
                debugLog("Read from API and added to cache, " + data.toString());
                return data;
            }).subscribeOn(Schedulers.io());
            return Observable
                    .concat(fromCache, fromApi)
                    .observeOn(AndroidSchedulers.mainThread());
        };
    }

    public void clearKey(String key) {
        try {
            File file = cacheFile(key);
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        } catch (RuntimeException e) {
            debugLog("clearKey - Error while reading cache file, " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void okKeys() {
        Field[] fields = CacheKeys.class.getDeclaredFields();
        ArrayList<String> values = new ArrayList<>();

        for (Field field : fields) {
            String type = field.getType().getSimpleName();
            String name = field.getName();
            if("String".equals(type)) {
                try {
                    String value = (String) field.get(null);
                    values.add(value);
                    if(values.contains(value)) {
                        throw new IllegalStateException("There appears to be a duplicate cache key for " + name);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void debugLog(String log) {
        if(BuildConfig.DEBUG) {
            Log.d(TAG, log);
        }
    }

    public static class Keys {

        public static final String GET_CUSTOMER_HOME__hID = "gch-%1$s";
        public static final String GET_POWER_UP_hID = "gpu-%1$s";
        public static final String GET_HOME__hID = "h-%1$s";
        public static final String GET_REPORTS__hID = "r-%1$s";
        public static final String GET_ME = "me";
        public static final String GET_HOME_COMPARISONS_3S = "ghc_%1$s_%2$s_%3$s";
        public static final String GET_SENSORS = "sensor_%1$s";
        public static final String GET_WEATHER = "weather-%1$s";
        public static final String GET_INVOICES = "invoices-%1$s";
        public static final String GET_GREETINGS = "greetings-%1$s";
        public static final String GET_ENERGY_DEAL= "energy_deal-%1$s";

    }




    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {

        }
    }


    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
