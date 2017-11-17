package com.haoranwang.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by haoranwang on 2017/11/16.
 */

public class Weather {

    public String status;

    AQI aqil;

    Basic basic;

    Now now;

    Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
