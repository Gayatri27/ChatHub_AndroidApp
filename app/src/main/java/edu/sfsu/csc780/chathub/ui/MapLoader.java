package edu.sfsu.csc780.chathub.ui;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MapLoader extends AsyncTaskLoader {
    private static final String LOG_TAG = "MapLoader";
    private Context context;

    public MapLoader(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Nullable
    @Override
    public Bitmap loadInBackground() {

        double lat = LocationUtils.getLat();
        double lon = LocationUtils.getLon();
        StringBuilder urlBuilder = new StringBuilder("http://maps.google" +
                ".com/maps/api/staticmap?center=");
        urlBuilder.append(lat);
        urlBuilder.append(",");
        urlBuilder.append(lon);
        urlBuilder.append("&zoom=15&size=400x300");
        urlBuilder.append("&markers=color:blue%7Clabel:A%7C");
        urlBuilder.append(lat);
        urlBuilder.append(",");
        urlBuilder.append(lon);
        urlBuilder.append("&key=" + getMetadata(context, "com.google.android.geo.API_KEY"));
        Log.d(LOG_TAG, "map url:" + urlBuilder.toString());

        Bitmap bmp = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlBuilder.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            bmp = BitmapFactory.decodeStream(in);
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return bmp;

    }

    public String getMetadata(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
