package com.haoranwang.coolweather;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.haoranwang.coolweather.db.City;
import com.haoranwang.coolweather.db.Country;
import com.haoranwang.coolweather.db.Province;
import com.haoranwang.coolweather.utl.HttpUtil;
import com.haoranwang.coolweather.utl.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseAreaFragment extends Fragment {

    private static final String tag = "chooseAreaFragment";
    private static final String commonUrl = "http://guolin.tech/api";
    private TextView textView;
    private Button backButton;
    private Button deleteButton;
    private Button queryButton;
    private ListView listView;
    private ProgressDialog progressDialog;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countryList;
    private static final int PROVINCE_LEVEL = 1;
    private static final int CITY_LEVEL = 2;
    private static final int COUNTRY_LEVLE = 3;
    private int current_level;

    Province selectedProvince;
    City selectedCity;
    Country selectedCountry;

    public ChooseAreaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area, container, false);

        textView = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        deleteButton = view.findViewById(R.id.delete_button);
        queryButton = view.findViewById(R.id.query_button);
        listView = view.findViewById(R.id.list_view);

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout
                .simple_expandable_list_item_1, dataList);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                selectedProvince = provinceList.get(position);
                Log.d(tag, selectedProvince.getProvinceName());
                Log.d(tag, String.valueOf(selectedProvince.getId()));
                //queryCity();

                /*
                if (current_level == PROVINCE_LEVEL){
                    selectedProvince = provinceList.get(position);
                    Log.d(tag, selectedProvince.getProvinceName());
                    Log.d(tag, String.valueOf(selectedProvince.getId()));
                    //queryCity();
                }
                /*else if (current_level == CITY_LEVEL){
                    selectedCity = cityList.get(position);
                }*/
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataSupport.deleteAll(Province.class);
                DataSupport.deleteAll(City.class);
                dataList.clear();
                adapter.notifyDataSetChanged();
            }
        });

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryProvince();
            }
        });

        //初始化
        queryProvince();
        return view;
    }

    private void queryProvince(){

        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            queryProvinceFromDB();
        }
        else {
            showProgressDialog();
            queryProvinceFromServer(commonUrl + "/china");
        }
        current_level = PROVINCE_LEVEL;
    }

    private void queryProvinceFromDB(){

        dataList.clear();
        for (Province province: provinceList){
            dataList.add(province.getProvinceName());
        }
        adapter.notifyDataSetChanged();
    }

    private void queryProvinceFromServer(String address){

        HttpUtil.sendOkHttpRequest(address, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //could only use .string() once
                String responseString = response.body().string();
                boolean result = false;
                result = Utility.handleProvinceResponse(responseString);

                if (result){
                    closeProgressDialog();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryProvince();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {

                Log.d(tag,"queryProvinceFromServer Failure");
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Loaded Failure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void queryCity(){
        //cityList = DataSupport.where("cityId = ?", selectedCity.getId()).find(City.class);
        //List<Song> songs = DataSupport.where("name like ?", "song%").order("duration").find(Song.class)

        cityList = DataSupport.findAll(City.class);
        if (cityList.size() > 0){
            queryCityFromDB();
        }
        else {
            //showProgressDialog();
            Log.d(tag,"selectedProvinceId: " + selectedProvince.getId());
            //queryCityFromServer(commonUrl + "/china/" + selectedProvince.getId(),
                    //selectedProvince.getId());
        }
        current_level = CITY_LEVEL;
    }

    private void queryCityFromDB(){
        Log.d(tag, "queryCityFromDB Start");
        dataList.clear();
        for (City city: cityList){
            dataList.add(city.getCityName());
        }
        adapter.notifyDataSetChanged();
    }

    private void queryCityFromServer(String address, final int provinceId){

        HttpUtil.sendOkHttpRequest(address, new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //could only use .string() once
                String responseString = response.body().string();
                boolean result = false;
                result = Utility.handleCityResponse(responseString, provinceId);

                if (result){
                    closeProgressDialog();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            queryCity();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {

                Log.d(tag,"queryProvinceFromServer Failure");
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Loaded Failure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }





    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCancelable(true);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }
}

/*
 */

/*
    private void queryProvince(){

        //provinceList.clear(); provinceList操作要在 = DataSupport后，否则会报错
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0){
            queryProvinceFromDB();
        }
        else {
            showProgressDialog();
            queryFromServer(commonUrl + "/china");
        }
        current_level = PROVINCE_LEVEL;
    }
 */