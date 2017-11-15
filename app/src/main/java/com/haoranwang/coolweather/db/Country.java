package com.haoranwang.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by haoranwang on 2017/11/11.
 */

public class Country extends DataSupport {

    private int countryId;

    private String countryName;

    private String weatherId;

    private int cityId;

    public int getCountryId(){
        return countryId;
    }

    public void setCountryId(int id){
        this.countryId = countryId;
    }

    public String getCountryName(){
        return countryName;
    }

    public void setCountryName(String countryName){
        this.countryName = countryName;
    }

    public String getWeatherId(){
        return weatherId;
    }

    public void setWeatherId(String weatherId){
        this.weatherId = weatherId;
    }

    public int setCityId(){
        return cityId;
    }

    public void setCityId(int cityId){
        this.cityId = cityId;
    }


}
