package com.example.map;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.Date;

import static java.security.AccessController.getContext;

public class Weather {

    private final double lat;
    private final double lon;
    private final String description;
    double temp;
    private final String tempStr;
    private final String city;
    private final Date time;

    public Weather(JSONObject jsonObject) throws JSONException {
        lat = jsonObject.getJSONObject("coord").getDouble("lat");
        lon = jsonObject.getJSONObject("coord").getDouble("lon");
        description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
        temp = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
        tempStr = String.format("%.2f", temp);
        time = new Date((long) (jsonObject.getInt("dt") + jsonObject.getInt("timezone")) * 1000);
        city = jsonObject.getString("name");
    }


    public static Bitmap createImage(double temp, Resources res) {
        String text = "";
        if (temp > 0)
            text = "+" + String.format("%.0f", temp) + "°";
        else
            text = "" + String.format("%.0f", temp) + "°";
        if (text.equals("-0°") || (text.equals("+0°")))
            text = " 0°";
        if (temp < -0.5 && temp > -10 || temp > -0.5 && temp < 10)
            text = " " + text;

        Bitmap tmp = BitmapFactory.decodeResource(res, R.drawable.cloud);

        Bitmap bmp = tmp.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bmp);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);

//        canvas.drawLine(0, 0, width, 0, paint);
//        canvas.drawLine(0, 99, width, 99, paint);
//        canvas.drawLine(0, 0, 0, 100, paint);
//        canvas.drawLine(width - 1, 0, width - 1, 100, paint);

        canvas.drawText(text, 35, 140, paint);
        return bmp;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getDescription() {
        return description;
    }

    public double getTemp() {
        return temp;
    }

    public String getTempStr() {
        return tempStr;
    }

    public String getCity() {
        return city;
    }

    public Date getTime() {
        return time;
    }
}