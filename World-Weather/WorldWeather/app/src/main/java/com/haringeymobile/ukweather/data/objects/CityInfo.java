package com.haringeymobile.ukweather.data.objects;

import com.google.gson.annotations.SerializedName;

public class CityInfo {

	@SerializedName("coord")
	private Coordinates coordinates;

	@SerializedName("country")
	private String country;

	@SerializedName("id")
	private int cityId;

	@SerializedName("name")
	private String cityName;

	public String getCountry() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.data.objects.CityInfo.getCountry()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.data.objects.CityInfo.getCountry()",this);return country;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.data.objects.CityInfo.getCountry()",this,throwable);throw throwable;}
	}

	public String getCityName() {
		com.mijack.Xlog.logMethodEnter("java.lang.String com.haringeymobile.ukweather.data.objects.CityInfo.getCityName()",this);try{com.mijack.Xlog.logMethodExit("java.lang.String com.haringeymobile.ukweather.data.objects.CityInfo.getCityName()",this);return cityName;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("java.lang.String com.haringeymobile.ukweather.data.objects.CityInfo.getCityName()",this,throwable);throw throwable;}
	}

}
